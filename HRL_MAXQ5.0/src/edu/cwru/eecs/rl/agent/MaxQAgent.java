package edu.cwru.eecs.rl.agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Stack;

import edu.cwru.eecs.futil.Pair;
import edu.cwru.eecs.rl.core.maxnode.IMaxNodeInterface;
import edu.cwru.eecs.rl.core.maxnode.MaxNode;
import edu.cwru.eecs.rl.core.mdp.Parameter;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.qnode.IQNodeInterface;
import edu.cwru.eecs.rl.core.qnode.QNode;
import edu.cwru.eecs.rl.core.qnode.IQNodeInterface.ValueType;
import edu.cwru.eecs.rl.domains.MaxQGraph;
import edu.cwru.eecs.rl.domains.MaxQGraphBuilder;
import edu.cwru.eecs.rl.env.Environment;
import edu.cwru.eecs.rl.experiment.FileProcessor;

public class MaxQAgent extends Agent {
	
	private static final long serialVersionUID = -2059775481180406518L;

	protected MaxQGraph m_taskGraph;
	
	protected MaxQGraph.HType m_hType;
	
	protected IMaxNodeInterface.ValueType m_qType; // ProjectedValue || PseudoProjectedValue
	protected IMaxNodeInterface.ValueType m_prType; // ManualPseudoReward || BayesPseudoReward || TabularPseudoReward
	
	protected MaxQAgent(Environment environment, AgentType agentType) {
		this(environment, agentType, new String[]{""});
	}
	
	protected MaxQAgent(Environment environment, AgentType agentType, String[] args) {
		super(environment, agentType);
		m_hType = MaxQGraph.HType.HIERARCHY;
		if(args.length>2 && args[2].equals("f"))
			m_hType = MaxQGraph.HType.FLAT;
		/** parameters for learning */
		m_taskGraph = MaxQGraphBuilder.buildMaxQGraph(environment, m_agentType, m_hType);
		// MaxQPrNo, MaxQPrMan, MaxQPrBayes, MaxQPrFunc
		switch (agentType) {
		case MaxQPrNo:
			m_qType = IMaxNodeInterface.ValueType.ProjectedValue;
			break;
		case MaxQPrMan:
			m_prType = IMaxNodeInterface.ValueType.ManualPseudoReward;
			m_qType = IMaxNodeInterface.ValueType.PseudoProjectedValue;
			break;
		case MaxQPrBayes:
			m_prType = IMaxNodeInterface.ValueType.BayesPseudoReward;
			m_qType = IMaxNodeInterface.ValueType.PseudoProjectedValue;
			break;
		case MaxQPrFunc:
			m_prType = IMaxNodeInterface.ValueType.TabularPseudoReward;
			m_qType = IMaxNodeInterface.ValueType.PseudoProjectedValue;
			break;
		default:
			break;
		}

		m_epsilon = 0.01;  // 0.1 for compariable experiment
		m_epsilonMin = 0.01;
		m_epsilonDecayRate = 0.95;
		m_alpha = 0.02;  // 0.05 for comparable experiment
	}
	
	public MaxQAgent(Environment environment, String[] args) {
		this(environment, getAgentType(args[1]), args);
	}
	
	private static AgentType getAgentType(String arg) {
		AgentType agentType = null;
		if(arg.equals("none"))
			agentType = AgentType.MaxQPrNo;
		else if(arg.equals("manual"))
			agentType = AgentType.MaxQPrMan;
		else if(arg.equals("bayes"))
			agentType = AgentType.MaxQPrBayes;
		else if(arg.equals("func"))
			agentType = AgentType.MaxQPrFunc;
		return agentType;
	}
	
	public static String getUsage() {
		return "arg1: pr";
	}

	public MaxQGraph getMaxQGraph() {
		return m_taskGraph;
	}
	
	@Override
	public String info() {
		if(m_taskGraph.HTYPE.equals(MaxQGraph.HType.FLAT))
			return "iF";
		return "";
	}
	
	@Override
	protected void run(int mode) {
		State state = m_environment.getState();
		MaxNode rootTask = new MaxNode(m_taskGraph.getRoot(), Parameter.NIL);
		if(mode==Agent.MODE_LEARNING) {
			MAXQ(rootTask, state);
			
			m_episode++;
		}
		else {
			executeHPolicy(rootTask, state);
		}
	}

	/**
	 * MaxQ algorithm: {for both maxq-0 and maxq-q aglorithms}
	 * @param currentTask
	 * @param state
	 * @return
	 */
	protected Stack<State> MAXQ(MaxNode currentTask, State state)
	{
		
		Stack<State> seq = new Stack<State>();
		/** if primitive task */
		if(currentTask.isPrimitive() && m_maxStep>0) 
		{	
			/** take action */
			m_environment.takeAction(currentTask.getPrimitiveAction());
			double reward = m_environment.getReward();
			m_step ++;
			m_cumulativeReward += reward;
			m_maxStep--;
			
			/** UPDATE the value function here */
			updateValue(currentTask, state, reward);
			
			seq.push(state);
		}
		/** if composite subtask */
		else {
			/** initialize the action sequence */
			Stack<Pair<MaxNode, State>> actionSeq = new Stack<Pair<MaxNode, State>>();
			Stack<Double> crSeq = new Stack<Double>();
			
			while(!m_taskGraph.isTerminal(state, currentTask) && m_maxStep>0)
			{	
				/** choose action according to current exploration policy */
				double epsilon = m_epsilon;
				MaxNode subTask = eGreedyActionSelection(state, currentTask, m_qType, epsilon);
				
				if (m_debug && !subTask.isPrimitive()) {
					System.err.println("Begin: " + state + " " + subTask.getNodeDescription());
				}
				
				/** take action (subtask) */
				double cumulativeRewards = m_cumulativeReward;
				
				Stack<State> subSubTaskSeq = MAXQ(subTask, state);
				State nextState = m_environment.getState();
				
				cumulativeRewards = m_cumulativeReward-cumulativeRewards;
				actionSeq.push(new Pair<MaxNode, State>(subTask, nextState));
				crSeq.push(cumulativeRewards);
				
				if (m_debug && !subTask.isPrimitive()) {
					System.err.println("Done: " + nextState + " " + subTask.toString());
				}
				
				/** UPDATE completion values */
				Stack<State> reverseStack = updateValue(currentTask, state, subTask.getParentQ(), nextState, subSubTaskSeq);
				
				while(!reverseStack.isEmpty())
					seq.push(reverseStack.pop());
				
				state = nextState;
			}
			
			/** UPDATE pseudo rewards */
			updatePseudoReward(actionSeq, crSeq);
			
			//currentTask.epsilonDecay();
		}
		return seq;
	}
	
	/**
	 * execute current hierarchical policy
	 * @param currentTask
	 * @param state
	 */
	protected void executeHPolicy(MaxNode currentTask, State state)
	{
		if(currentTask.isPrimitive() && m_maxStep>0)
		{
			// take primitive action
			m_environment.takeAction(currentTask.getPrimitiveAction());
			// get reward
			double reward = m_environment.getReward();
			// update episode statistics
			m_step ++;
			m_cumulativeReward += reward;
			m_maxStep--;
		}
		else {
			while(!m_taskGraph.isTerminal(state, currentTask) && m_maxStep>0)
			{
				MaxNode subTask = bestActionSelection(state, currentTask, m_qType);
			
				if(m_debug && !subTask.isPrimitive()) {
					System.out.println(subTask.toString());
					System.out.println("State: " + state);
				}
				
				executeHPolicy(subTask, state);
				State nextState = m_environment.getState();
				state = nextState;
			}
		}
	}

	/**
	 * epsilon decay
	 */
	protected void epsilonDecay() {
		m_epsilon-=0.001;
		m_epsilon = Math.max(m_epsilon, m_epsilonMin);
	}
	

	
	public String message(String message) {
		String[] messages = message.split(",");
		if(messages[0].trim().equals("vf")) {
			if(messages[1].trim().equals("print")) {
				if(messages.length>2) {
					String pathString = messages[2].trim();
					try {
						if(m_agentType.nodeTypes().contains(AgentType.NodeType.V_MaxProjected)) {
							PrintStream ps_vf = new PrintStream(new File(pathString+FileProcessor.RESULT_VF_EXTENSION));
							printVF(ps_vf, true);
							ps_vf.close();
						}
						if(m_agentType.nodeTypes().contains(AgentType.NodeType.V_QCompletion)) {
							PrintStream ps_cf = new PrintStream(new File(pathString + FileProcessor.RESULT_CF_EXTENSION));
							printCF(ps_cf, true);
							ps_cf.close();
						}
						if(m_agentType.hasPseudoReward()) {
							PrintStream ps_pcf = new PrintStream(new File(pathString + FileProcessor.RESULT_PCF_EXTENSION));
							printPR(ps_pcf, true);
							ps_pcf.close();
							
							PrintStream ps_pr = new PrintStream(new File(pathString + FileProcessor.RESULT_PR_EXTENSION));
							printPR(ps_pr, true);
							ps_pr.close();
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				else {
					if(m_agentType.nodeTypes().contains(AgentType.NodeType.V_MaxProjected))
						printVF(System.out, false);
					if(m_agentType.nodeTypes().contains(AgentType.NodeType.V_QCompletion))
						printCF(System.out, false);
					if(m_agentType.hasPseudoReward()) {
						printPseudoCF(System.out, false);
						printPR(System.out, false);
					}
				}
			}
		}
		return super.message(message);
	}
	
	/******************************************************************
	 * All the printing methods
	 ******************************************************************/
	
	protected void printVF(PrintStream ps, boolean real) {
		ps.println("Project Value Function for Primitive Actions: " + m_taskGraph.getTaskName());
		int primitiveNum = m_taskGraph.getPrimitiveMaxNodeNumber();
		for(int i=0; i<primitiveNum; i++) {
			IMaxNodeInterface iMaxNode = m_taskGraph.getMaxNode(i);
			iMaxNode.printVF(ps, real, IMaxNodeInterface.ValueType.ProjectedValue);
		}
	}
	protected void printCF(PrintStream ps, boolean real) {
		ps.println("Completion Function for Subtasks: " + m_taskGraph.getTaskName());
		int qNodeNum = m_taskGraph.getQNodeNumber();
		ps.println("Summary: -------------");
		for(int i=0; i<qNodeNum; i++) {
			IQNodeInterface iQNode = m_taskGraph.getQNode(i);
			iQNode.printVF(ps, false, IQNodeInterface.ValueType.CompletionValue);
		}
		ps.println("Summary end: ---------------");
		for(int i=0; i<qNodeNum; i++) {
			IQNodeInterface iQNode = m_taskGraph.getQNode(i);
			iQNode.printVF(ps, real, IQNodeInterface.ValueType.CompletionValue);
		}
	}
	protected void printPseudoCF(PrintStream ps, boolean real) {
		ps.println("Completion Function for Subtasks: " + m_taskGraph.getTaskName());
		int qNodeNum = m_taskGraph.getQNodeNumber();
		ps.println("Summary: -------------");
		for(int i=0; i<qNodeNum; i++) {
			IQNodeInterface iQNode = m_taskGraph.getQNode(i);
			iQNode.printVF(ps, false, IQNodeInterface.ValueType.PseudoCompletionValue);
		}
		ps.println("Summary end: ---------------");
		for(int i=0; i<qNodeNum; i++) {
			IQNodeInterface iQNode = m_taskGraph.getQNode(i);
			iQNode.printVF(ps, real, IQNodeInterface.ValueType.PseudoCompletionValue);
		}
	}
	protected void printPR(PrintStream ps, boolean real) {
		ps.println("Pseudo Reward for Subtasks: " + m_taskGraph.getTaskName());
		int maxNodeNum = m_taskGraph.getMaxNodeNumber();
		for(int i=0; i<maxNodeNum; i++) {
			IMaxNodeInterface iMaxNode = m_taskGraph.getMaxNode(i);
			iMaxNode.printVF(ps, real, m_prType);
		}
	}
	
	/******************************************************************
	 * All the updating methods
	 ******************************************************************/
	
	protected void updateValue(MaxNode currentTask, State state, double reward) {
		updateProjectedValue(currentTask, state, reward);
	}
	
	protected Stack<State> updateValue(MaxNode currentTask, State state, QNode actionQNode, State nextState, Stack<State> subSubTaskSeq) {
		return updateCompletionValue(currentTask, state, actionQNode, nextState, subSubTaskSeq);
	}
	
	/**
	 * this only works for primitive tasks
	 * @param currentTask
	 * @param state
	 * @param reward
	 */
	protected void updateProjectedValue(MaxNode currentTask, State state, double reward) { // for primitive subtask
		double oldValue = currentTask.getValue(state, IMaxNodeInterface.ValueType.ProjectedValue);
		double alpha = m_alpha;
		double newValue = (1-alpha)*oldValue + alpha*reward;
		currentTask.setValue(state, newValue, IMaxNodeInterface.ValueType.ProjectedValue);
	}
	
	/**
	 * wrap for updating completion values
	 * @param currentTask
	 * @param state
	 * @param actionQNode
	 * @param nextState
	 * @param subSubTaskSeq
	 * @return
	 */
	protected Stack<State> updateCompletionValue(MaxNode currentTask, State state, QNode actionQNode, State nextState, Stack<State> subSubTaskSeq) {
		if(m_agentType.hasPseudoReward()) // has PR
			return updateCVQ(currentTask, state, actionQNode, nextState, subSubTaskSeq);
		else // no PR
			return updateCV0(currentTask, state, actionQNode, nextState, subSubTaskSeq);
	}
	
	/**
	 * wrap for update pseudo rewards
	 * @param actionSeq
	 * @param crSeq
	 */
	protected void updatePseudoReward(Stack<Pair<MaxNode, State>> actionSeq, Stack<Double> crSeq) {
		if(m_agentType.hasPseudoReward()) {
			if(m_agentType.hasPseudoReward_Bayes())
				updatePRBayes(actionSeq, crSeq);
			else if(m_agentType.hasPseudoReward_Func())
				updatePRFunc(actionSeq, crSeq);
		}
	}
	
	/******************************************************************
	 * Real updating stuff
	 ******************************************************************/
	
	/**
	 * Update completion function in maxq-0
	 * @param currentTask
	 * @param state
	 * @param actionQNode
	 * @param nextState
	 * @param subSubTaskSeq
	 * @return
	 */
	protected Stack<State> updateCV0(MaxNode currentTask, State state, QNode actionQNode, State nextState, Stack<State> subSubTaskSeq){ // for composite subtask
			
		double projectedValue2 = 0;  // projected value for subTask2
		double completionValue2 = 0; // completion value for subTask2
		if(!m_taskGraph.isTerminal(nextState, currentTask)) {
			MaxNode subTask2 = bestActionSelection(nextState, currentTask, m_qType);

			projectedValue2 = getProjectedValue(nextState, subTask2, IMaxNodeInterface.ValueType.ProjectedValue);
			completionValue2 = subTask2.getParentQ().getValue(nextState, IQNodeInterface.ValueType.CompletionValue);
		}
		double alpha = m_alpha;
		int N = 1;
		Stack<State> reverseStack = new Stack<State>();
		/** update completion values for each state in the subSubTaskSeq */
		while(!subSubTaskSeq.isEmpty())
		{
			State tempState = subSubTaskSeq.pop();
			reverseStack.push(tempState);
			// update completion function for a1, i.e. subTask
			double oldValue = actionQNode.getValue(tempState, IQNodeInterface.ValueType.CompletionValue);
			double newValue = (1-alpha)*oldValue + alpha*Math.pow(m_gamma, N)*(completionValue2+projectedValue2);
			actionQNode.setValue(tempState, newValue, IQNodeInterface.ValueType.CompletionValue);
			N += 1;
		}
		
		return reverseStack;
	}
	
	/**
	 * update completion function in maxq-q
	 * @param currentTask
	 * @param state
	 * @param actionQNode
	 * @param nextState
	 * @param subSubTaskSeq
	 * @return
	 */
	protected Stack<State> updateCVQ(MaxNode currentTask, State state, QNode actionQNode, State nextState, Stack<State> subSubTaskSeq){ // for composite subtask
		double projectedValue2;  // projected value for subTask2
		double completionValue2; // completion value for subTask2
		double pseudoCompletionValue2;
		double pseudoReward2;
		if(!m_taskGraph.isTerminal(nextState, currentTask)) {
			MaxNode subTask2 = bestActionSelection(nextState, currentTask, m_qType);

			projectedValue2 = getProjectedValue(nextState, subTask2, IMaxNodeInterface.ValueType.ProjectedValue);
			completionValue2 = subTask2.getParentQ().getValue(nextState, IQNodeInterface.ValueType.CompletionValue);
			pseudoCompletionValue2 = subTask2.getParentQ().getValue(nextState, IQNodeInterface.ValueType.PseudoCompletionValue);
			pseudoReward2 = 0;
		} else {
			projectedValue2 = 0;
			completionValue2 = 0;
			pseudoCompletionValue2 = 0;
			pseudoReward2 = currentTask.getValue(nextState, m_prType);
			//System.out.println(currentTask.getNodeName() + " -- " + state + " -- " + actionQNode.getNodeName() + " -- Pseudo reward: " + pseudoReward2);
			//pseudoReward2 = 0;
		}
		double alpha = m_alpha;
		int N = 1;
		Stack<State> reverseStack = new Stack<State>();
		/** update completion values for each state s in the subSubTaskSeq */
		while(!subSubTaskSeq.isEmpty())
		{
			State tempState = subSubTaskSeq.pop();
			reverseStack.push(tempState);
			
			// update pseudo completion function for a1, i.e. subTask
			double oldPseudoCompValue1 = actionQNode.getValue(tempState, IQNodeInterface.ValueType.PseudoCompletionValue);
			double newPseudoCompValue1 = (1-alpha)*oldPseudoCompValue1 + alpha*Math.pow(m_gamma, N)*(pseudoReward2+pseudoCompletionValue2+projectedValue2);
			actionQNode.setValue(tempState, newPseudoCompValue1, IQNodeInterface.ValueType.PseudoCompletionValue);
			
			// update completion function for a1, i.e. subTask
			double oldCompValue1 = actionQNode.getValue(tempState, IQNodeInterface.ValueType.CompletionValue);
			double newCompValue1 = (1-alpha)*oldCompValue1 + alpha*Math.pow(m_gamma, N)*(completionValue2+projectedValue2);
			actionQNode.setValue(tempState, newCompValue1, IQNodeInterface.ValueType.CompletionValue);
			N += 1;
		}
		
		return reverseStack;
	}
	
	
	/**
	 * update the pseudo reward for bayesian PR
	 * for manual PR or PR with vf, 
	 * @param actionSeq
	 * @param crSeq: cumurative rewards sequence
	 */
	protected void updatePRBayes(Stack<Pair<MaxNode, State>> actionSeq, Stack<Double> crSeq) {
		/** compute completion cumulative rewards for each action in actionSeq */
		if(!actionSeq.isEmpty()) {
			actionSeq.pop();
			double tempCR = 0;
			
			while(!actionSeq.isEmpty()) {
				Pair<MaxNode, State> tempPair = actionSeq.pop();
				MaxNode tempMaxNode = tempPair.left();
				State tempState = tempPair.right();
				tempCR += crSeq.pop();
				tempMaxNode.setValue(tempState, tempCR, IMaxNodeInterface.ValueType.BayesPseudoReward);
			}
		}
	}
	
	protected void updatePRFunc(Stack<Pair<MaxNode, State>> actionSeq, Stack<Double> crSeq) {
		/** compute completion cumulative rewards for each action in actionSeq */
		double alpha = 0.1;
		if(!actionSeq.isEmpty()) {
			double tempCR = 0;
			actionSeq.pop();
			while(!actionSeq.isEmpty()) {
				Pair<MaxNode, State> tempPair = actionSeq.pop();
				MaxNode tempMaxNode = tempPair.left();
				State tempState = tempPair.right();
				tempCR += crSeq.pop();
				double newV = (1-alpha)*tempMaxNode.getValue(tempState, IMaxNodeInterface.ValueType.TabularPseudoReward) + alpha*tempCR;
				tempMaxNode.setValue(tempState, newV, IMaxNodeInterface.ValueType.TabularPseudoReward);
			}
		}
	}
	
	
	/*********************************************************************/
	/** action selection related methods */
	
	/**
	 * Choose action
	 */
	protected MaxNode eGreedyActionSelection(State state, MaxNode maxNode, IMaxNodeInterface.ValueType vType, double epsilon) {
		ArrayList<MaxNode> allValidMaxNodes = getValidMaxNodes(state, maxNode);
		if(allValidMaxNodes.size()==0)
			System.err.println("No action to choose from in eGreedyActionSelection: " + maxNode.getNodeDescription() + " " + state.toString());
		double[] qValues = getAllQValues(state, allValidMaxNodes, vType);
		int actionIndex = Agent.eGreedySelection(qValues, epsilon);
		return allValidMaxNodes.get(actionIndex);
	}
	
	/**
	 * choose action that maximize Q value (value of finishing current task).
	 * @param state
	 * @param pseudo
	 * @return the best action (max node) w.r.t. current q value estimation.
	 */
	protected MaxNode bestActionSelection(State state, MaxNode maxNode, IMaxNodeInterface.ValueType vType) {
		return eGreedyActionSelection(state, maxNode, vType, 0);
	}
	
	/**
	 * Requires that state is not the terminal state of task
	 * @param state
	 * @return all the valid child max nodes for current state.
	 */
	protected ArrayList<MaxNode> getValidMaxNodes(State state, MaxNode task) {
		IMaxNodeInterface iTask = task.getIMaxNode();
		MaxQGraph maxQGraph = iTask.getMaxQGraph();
		ArrayList<MaxNode> allValidMaxNodes = new ArrayList<MaxNode>();  // init results
		if(m_taskGraph.isTerminal(state, task))
			return allValidMaxNodes;
		ArrayList<IQNodeInterface> allIQNodes = task.getIMaxNode().getQNodes();  // get all sub-qNodes
		for(IQNodeInterface iQNode : allIQNodes) {
			ArrayList<Parameter> tempQParameters =  maxQGraph.getQParameters(state, task, iQNode); // get all parameters for a sub-qNode
			for(Parameter qParameter : tempQParameters) {
				QNode tempQNode = new QNode(iQNode, qParameter);
				tempQNode.setParentMax(task);
				IMaxNodeInterface tempIMaxNode = tempQNode.getIQNode().getChildIMaxNode(); // get child max node for the sub-qNode
				ArrayList<Parameter> tempMaxParameters = maxQGraph.getMaxParameters(state, tempQNode);  // get all parameters for child max node
				for(Parameter maxParameter : tempMaxParameters) {
					MaxNode tempMaxNode = new MaxNode(tempIMaxNode, maxParameter);
					tempMaxNode.setParentQ(tempQNode);
					if(maxQGraph.isStartingState(state, tempMaxNode)) {  // if this state is valid for child max node, then keep it in results.
						QNode newQNode = tempQNode.clone();
						tempMaxNode.setParentQ(newQNode);
						newQNode.setChildMax(tempMaxNode);
						allValidMaxNodes.add(tempMaxNode);
					}
				}
			}
		}
		return allValidMaxNodes;
	}
	
	
	protected double[] getAllQValues(State state, ArrayList<MaxNode> allValidMaxNodes, IMaxNodeInterface.ValueType vType) {
		int actionSize = allValidMaxNodes.size();
		double[] allQValues = new double[actionSize];
		for(int i=0; i<actionSize; i++) {
			MaxNode action = allValidMaxNodes.get(i);
			allQValues[i] = getProjectedValue(state, action.getParentQ().getParentMax(), action, m_qType);
		}
		return allQValues;
	}
	
	protected double getProjectedValue(State state, MaxNode maxNode, IMaxNodeInterface.ValueType vType) {
		
		if(maxNode.isPrimitive()) 
			return maxNode.getValue(state, IMaxNodeInterface.ValueType.ProjectedValue);
		else {
			if(m_taskGraph.isTerminal(state, maxNode))
				return 0;
			MaxNode bestMaxNode = bestActionSelection(state, maxNode, vType);
			return getProjectedValue(state, maxNode, bestMaxNode, vType);
		}
	}
	
	protected double getProjectedValue(State state, MaxNode maxNode, MaxNode action, IMaxNodeInterface.ValueType vType) {
		double remains = 0;
		switch (vType) {
		case ProjectedValue:
			remains = action.getParentQ().getValue(state, ValueType.CompletionValue);
			break;
		case PseudoProjectedValue:
			remains = action.getParentQ().getValue(state, ValueType.PseudoCompletionValue);
		case RCEProjectedValue: // implemented in ALispQAgent
		default:
			break;
		}
		return remains + getProjectedValue(state, action, IMaxNodeInterface.ValueType.ProjectedValue);
	}
	
}






