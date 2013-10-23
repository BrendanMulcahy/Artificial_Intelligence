package edu.cwru.eecs.rl.agent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import edu.cwru.eecs.futil.Pair;
import edu.cwru.eecs.futil.Samplers;
import edu.cwru.eecs.rl.core.maxnode.IMaxNodeInterface;
import edu.cwru.eecs.rl.core.maxnode.MaxNode;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.model.Model;
import edu.cwru.eecs.rl.core.qnode.IQNodeInterface;
import edu.cwru.eecs.rl.domains.DomainEnum;
import edu.cwru.eecs.rl.domains.MaxQGraph;
import edu.cwru.eecs.rl.env.Environment;

public class BayesianMaxQAgent extends MaxQAgent {
	
	private static final long serialVersionUID = 2341681729908890639L;

	protected int m_K; // model update interval
	protected int m_iter_Sim; // number of iteration each simulation.
	protected int m_maxStep_Sim; // max steps during simulation
	protected double m_epsilon_Sim;
	protected double m_alpha_Sim;
	
	protected BMaxQLogger m_logger;
	
	public BayesianMaxQAgent(Environment environment, String[] args) {
		this(environment, getAgentType(args[1]), args);
	}
	
	protected BayesianMaxQAgent(Environment environment, AgentType agentType, String[] args) {
		super(environment, agentType, args);
		
		// BMaxQPrNo, BMaxQPrMan, BMaxQPrBayes
		switch (agentType) {
		case BMaxQPrNo:
			m_qType = IMaxNodeInterface.ValueType.ProjectedValue;
			break;
		case BMaxQPrMan:
			m_prType = IMaxNodeInterface.ValueType.ManualPseudoReward;
			m_qType = IMaxNodeInterface.ValueType.PseudoProjectedValue;
			break;
		case BMaxQPrBayes:
			m_prType = IMaxNodeInterface.ValueType.BayesPseudoReward;
			m_qType = IMaxNodeInterface.ValueType.PseudoProjectedValue;
			break;
		default:
			break;
		}
		
		/** parameters for simulation */
		m_K = 50;
		if(environment.name().equals(DomainEnum.Hallway.toString())) {
			m_K = 100;
			m_iter_Sim = 500;
		} else if(environment.name().equals(DomainEnum.Taxi.toString())) {
			m_K = 50;
			m_iter_Sim = 500;
			if(m_hType.equals(MaxQGraph.HType.FLAT))
				m_iter_Sim = 1000;
		} else if(environment.name().equals(DomainEnum.Wargus.toString())) {
			m_K = 50;
			m_iter_Sim = 1000;
		} 
		m_maxStep_Sim = 1000;
		m_epsilon_Sim = 0.2;
		m_alpha_Sim = 0.5;
		
		m_logger = new BMaxQLogger();
	}
	
	@Override
	public void setPrior(Agent agent) {
		if(agent instanceof BayesianMaxQAgent) {
			BayesianMaxQAgent prior = (BayesianMaxQAgent) agent;
			MaxQGraph priorTaskGraph = prior.getMaxQGraph();
			int num = m_taskGraph.getMaxNodeNumber();
			for(int i=0; i<num; i++) {
				Model priorModel = priorTaskGraph.getMaxNode(i).getModel();
				Model currModel = m_taskGraph.getMaxNode(i).getModel();
				if(currModel!=null) {
					currModel.setPrior(priorModel);
					System.out.println("Prior set: " + m_taskGraph.getMaxNode(i).nameString());
				}
			}
		}
		else {
			System.out.println("Not an instance of BayesianMaxQAgent: " + agent.getClass().getName());
		}
	}
	
	private static AgentType getAgentType(String arg) {
		AgentType agentType = null;
		if(arg.equals("none"))
			agentType = AgentType.BMaxQPrNo;
		else if(arg.equals("manual"))
			agentType = AgentType.BMaxQPrMan;
		else if(arg.equals("bayes"))
			agentType = AgentType.BMaxQPrBayes;
		return agentType;
	}

	/**
	 * This is Bayesian max q algorithm.
	 * @param currentTask
	 * @param state
	 * @return
	 */
	protected Stack<State> MAXQ(MaxNode currentTask, State state) {
		Stack<State> seq = new Stack<State>();
		/** if primitive task */
		if(currentTask.isPrimitive() && m_maxStep>0) 
		{	
			/** take action */
			m_environment.takeAction(currentTask.getPrimitiveAction());
			double reward = m_environment.getReward();
			State nextState = m_environment.getState();
			m_step ++;
			m_cumulativeReward += reward;
			m_maxStep--;
			
			/** all the updating */
			
			/** update the value function here */
			updateProjectedValue(currentTask, state, reward);
			
			/** update count and visited states for subtask */
			m_logger.incCounter(currentTask);
			m_logger.addVisitedState(currentTask, state);
			
			/** update model distribution as well as MAP model */
			currentTask.updateModel(state, reward, nextState);
			
			/** recompute the projected value function if needed */
			recomputeValue(currentTask, state);
			
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
				
				/** all the updating goes below */
				
				/** update completion values*/
				Stack<State> reverseStack = updateCompletionValue(currentTask, state, subTask.getParentQ(), nextState, subSubTaskSeq);
				//Stack<State> reverseStack = dummyCompletionValueUpdating(currentTask, state, subTask.getParentQ(), nextState, subSubTaskSeq);
				
				/** update count and visited states for subtask */
				m_logger.incCounter(currentTask);
				m_logger.addVisitedStates(currentTask, reverseStack);
				
				/** recompute completion function if needed */
				recomputeValue(currentTask, state);
				
				while(!reverseStack.isEmpty())
					seq.push(reverseStack.pop());
				
				state = nextState;
			}
			
			updatePseudoReward(actionSeq, crSeq);
			
			//currentTask.epsilonDecay();
		}
		return seq;
	}
	
	private void recomputeValue(MaxNode currentTask, State state) {
		/** if need to update the value */
		if(m_logger.getCounter(currentTask)>=m_K) { 
			/** reset counter */
			m_logger.resetCounter(currentTask);
			//System.out.println("Recomputing: " + currentTask.nameString());
			/** if primitive subtask, then update projected value */
			if(currentTask.isPrimitive()) { 
				Set<State> visitedStates = m_logger.getVisitedStates(currentTask);
				for(State tempState : visitedStates) {
					currentTask.setValue(tempState, currentTask.getReward(tempState), IMaxNodeInterface.ValueType.ProjectedValue);
				}
			}
			/** if composite subtask */
			else {
				ArrayList<MaxNode> validChildMaxNodes = getValidMaxNodes(state, currentTask);
				for(MaxNode tempMaxNode : validChildMaxNodes) {
					recomputeValue(tempMaxNode, state); // recompute projected value function for subtask			
				}
				for(MaxNode tempMaxNode : validChildMaxNodes) {
					;
					//clearCompletionFunction(tempMaxNode); // reset completion function for subtask
					// TODO: the following clear the pseudo reward function --- 11.29, 2011
					//resamplePseudoRewardFunction(tempMaxNode); // resample pseudo reward function for subtask
				}
				
				for(int i=0; i<m_iter_Sim; i++) {
					State tempState = m_logger.sampleVisitedState(currentTask);
					//if(tempState!=null)
					MaxQ_Sim(currentTask, tempState, m_maxStep_Sim);
				}
			}
		}
	}
	
	private Stack<State> MaxQ_Sim(MaxNode currentTask, State state, int maxStep) {
		Stack<State> seq = new Stack<State>();
		/** if primitive task */
		if(currentTask.isPrimitive() && maxStep>0) // DIFF
		{
			/** generate next state from model */
			State nextState = currentTask.getNextState(state);
			double reward = currentTask.getReward(state);
			currentTask.setValue(state, reward, IMaxNodeInterface.ValueType.ProjectedValue);
			seq.push(state);
			seq.push(nextState);
		}
		/** if composite task */
		else {
			while(!m_taskGraph.isTerminal(state, currentTask) && maxStep>0) {
				/** choose action according to current exploration policy */
				double epsilon = m_epsilon_Sim;
				MaxNode subTask = eGreedyActionSelection(state, currentTask, m_qType, epsilon);

				/** take action */
				Stack<State> subSubTaskSeq = MaxQ_Sim(subTask, state, maxStep);  // DIFF
				State nextState = subSubTaskSeq.pop();
				//System.out.println("Task: " + currentTask.getNodeName() + "\tsubtask: " + subTask.getNodeName() + "\tsteps: " + subSubTaskSeq.size());
				maxStep-=subSubTaskSeq.size();

				/** update completion values */
				Stack<State> reverseStack = updateCompletionValue(currentTask, state, subTask.getParentQ(), nextState, subSubTaskSeq);

				while(!reverseStack.isEmpty())
					seq.push(reverseStack.pop());

				state = nextState;
			}
			seq.push(state);
		}
		return seq;
	}
	
//	protected void resamplePseudoRewardFunction(MaxNode maxNode) {
//		if(m_agentType.hasPseudoReward()) {
//			if(m_agentType.hasPseudoReward_Bayes())
//				maxNode.clearValues(IMaxNodeInterface.ValueType.BayesPseudoReward, null);
//		}
//	}
	
	protected void clearCompletionFunction(MaxNode maxNode) {
		maxNode.getParentQ().clearValues(IQNodeInterface.ValueType.CompletionValue, null);
		if(m_agentType.hasPseudoReward())
			maxNode.getParentQ().clearValues(IQNodeInterface.ValueType.PseudoCompletionValue, null);
	}
	
//	protected Stack<State> dummyCompletionValueUpdating(MaxNode currentTask, State state, QNode actionQNode, State nextState, Stack<State> subSubTaskSeq) {
//		Stack<State> reverseStack = new Stack<State>();
//		while(!subSubTaskSeq.isEmpty())
//		{	
//			State tempState = subSubTaskSeq.pop();
//			reverseStack.push(tempState);
//		}
//		return reverseStack;
//	}
}

class BMaxQLogger implements Serializable{ 
	
	private static final long serialVersionUID = 1374701067738778907L;
	
	/** counter for each max node (max node with different parameters are treated as different) */
	private HashMap<MaxNode, Integer> m_counter;
	/** count the visited states for each max node */
	private HashMap<MaxNode, HashMap<State, Integer>> m_visitedStates;
	private HashMap<MaxNode, Integer> m_totalVisitedCounter;
	
	public BMaxQLogger() {
		m_counter = new HashMap<MaxNode, Integer>();
		m_visitedStates = new HashMap<MaxNode, HashMap<State,Integer>>();
		m_totalVisitedCounter = new HashMap<MaxNode, Integer>();
	}

	
	public int getCounter(MaxNode maxNode) {
		if(!m_counter.containsKey(maxNode))
			m_counter.put(maxNode, 0);
		return m_counter.get(maxNode);
	}
	
	public void incCounter(MaxNode maxNode) {
		if(!m_counter.containsKey(maxNode))
			m_counter.put(maxNode, 0);
		m_counter.put(maxNode, m_counter.get(maxNode)+1);
	}
	
	public void resetCounter(MaxNode maxNode) {
		m_counter.put(maxNode, 0);
	}
	
	public State sampleVisitedState(MaxNode maxNode) {
		if(!m_visitedStates.containsKey(maxNode)) {
			System.err.println("no state  encountered in this max node: " + maxNode.getNodeDescription());
			return null;
		}
		int total = m_totalVisitedCounter.get(maxNode);
		if(total == 0) {
			System.err.println("no state  encountered in this max node: " + maxNode.getNodeDescription());
			return null;
		}
		int index = Samplers.sampleUniform(total);
		HashMap<State, Integer> tempVisitedStateCounter = m_visitedStates.get(maxNode);
		Set<State> tempVisitedStates = tempVisitedStateCounter.keySet();
		for(State tempState : tempVisitedStates) {
			index -= tempVisitedStateCounter.get(tempState);
			if(index<0)
				return tempState;
		}
		return null;
	}
	
	public Set<State> getVisitedStates(MaxNode maxNode) {
		if(!m_visitedStates.containsKey(maxNode)) {
			System.err.println("no state  encountered in this max node: " + maxNode.name());
			return null;
		}
		return m_visitedStates.get(maxNode).keySet();
	}
	
	public void addVisitedState(MaxNode maxNode, State state) {
		//System.out.println("add visited state: " + maxNode.getNodeDescription());
		if(!m_visitedStates.containsKey(maxNode)) {
			m_visitedStates.put(maxNode, new HashMap<State, Integer>());
			m_totalVisitedCounter.put(maxNode, 0);
		}
		HashMap<State, Integer> tempStateCounter = m_visitedStates.get(maxNode);
		if(!tempStateCounter.containsKey(state)) {
			tempStateCounter.put(state, 0);
		}
		tempStateCounter.put(state, tempStateCounter.get(state)+1);
		m_totalVisitedCounter.put(maxNode, m_totalVisitedCounter.get(maxNode)+1);
	
	}
	
	public void addVisitedStates(MaxNode maxNode, Vector<State> states) {
		//System.out.println("add visited states: " + states.size() + " " + maxNode.getNodeDescription());
		if(!m_visitedStates.containsKey(maxNode)) {
			m_visitedStates.put(maxNode, new HashMap<State, Integer>());
			m_totalVisitedCounter.put(maxNode, 0);
		}
		HashMap<State, Integer> tempStateCounter = m_visitedStates.get(maxNode);
		for(State tempState : states) {
			if(!tempStateCounter.containsKey(tempState)) {
				tempStateCounter.put(tempState, 0);
			}
			tempStateCounter.put(tempState, tempStateCounter.get(tempState)+1);
		}
		m_totalVisitedCounter.put(maxNode, m_totalVisitedCounter.get(maxNode)+states.size());
	}
}


