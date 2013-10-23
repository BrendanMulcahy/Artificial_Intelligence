package edu.cwru.eecs.rl.agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Stack;

import edu.cwru.eecs.rl.core.maxnode.IMaxNodeInterface;
import edu.cwru.eecs.rl.core.maxnode.MaxNode;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.qnode.IQNodeInterface;
import edu.cwru.eecs.rl.core.qnode.QNode;
import edu.cwru.eecs.rl.env.Environment;
import edu.cwru.eecs.rl.experiment.FileProcessor;

/**
 * ALispQ learning agent (also called HORDQ)
 * Refer to: State Abstraction for Programmable Reinforcement Learning Agents 
 * by David Andre and Stuart J. Russell, AAAI 2002
 * 
 * Q(w, a) = Q_r(w, a) + Q_c(w, a) + Q_e(w, a)
 * 
 * @author Feng
 *
 */
public class ALispQAgent extends MaxQAgent {

	private static final long serialVersionUID = -4235984840278808824L;

	public ALispQAgent(Environment environment) {
		super(environment, AgentType.ALispQ);
		m_qType = IMaxNodeInterface.ValueType.RCEProjectedValue;
	}
	
	public ALispQAgent(Environment environment, String[] args) {
		this(environment);
	}
	
	public static String getUsage() {
		return "arg0";
	}
	
	@Override
	public String message(String message) {
		String[] messages = message.split(",");
		if(messages[0].trim().equals("vf")){
			if(messages[1].trim().equals("print")) {
				if(messages.length>2) {
					String pathString = messages[2].trim();
					try {
						PrintStream ps_ef = new PrintStream(new File(pathString+FileProcessor.RESULT_EF_EXTENSION));
						printEF(ps_ef, true);
						ps_ef.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
			else {
				printEF(System.out, false);
			}
		}
		
		return super.message(message);
	}
	
	/******************************************************************
	 * The printing methods
	 ******************************************************************/
	
	protected void printEF(PrintStream ps, boolean real) {
		ps.println("External Value Function for Subtasks: " + m_taskGraph.getTaskName());
		int qNodeNum = m_taskGraph.getQNodeNumber();
		ps.println("Summary: -------------");
		for(int i=0; i<qNodeNum; i++) {
			IQNodeInterface iQNode = m_taskGraph.getQNode(i);
			iQNode.printVF(ps, false, IQNodeInterface.ValueType.ExternalValue);
		}
		ps.println("Summary end: ---------------");
		for(int i=0; i<qNodeNum; i++) {
			IQNodeInterface iQNode = m_taskGraph.getQNode(i);
			iQNode.printVF(ps, real, IQNodeInterface.ValueType.ExternalValue);
		}
	}
	
	/******************************************************************
	 * All the updating methods
	 ******************************************************************/
	
	@Override
	protected void updateValue(MaxNode currentTask, State state, double reward) {
		updateProjectedValue(currentTask, state, reward);
	}
	
	protected Stack<State> updateValue(MaxNode currentTask, State state, QNode actionQNode, State nextState, Stack<State> subSubTaskSeq) {
		return updateCnEValue(currentTask, state, actionQNode, nextState, subSubTaskSeq);
	}
	
	protected Stack<State> updateCnEValue(MaxNode currentTask, State state, QNode actionQNode, State nextState, Stack<State> subSubTaskSeq) {
		double projectedValue = 0;
		double completionValue = 0;
		double externalValue = 0;
		if(!m_taskGraph.isTerminal(nextState, currentTask)) {
			MaxNode subTask2 = bestActionSelection(nextState, currentTask, m_qType);
			projectedValue = getProjectedValue(nextState, subTask2, IMaxNodeInterface.ValueType.ProjectedValue);
			completionValue = subTask2.getParentQ().getValue(nextState, IQNodeInterface.ValueType.CompletionValue);
			externalValue = subTask2.getParentQ().getValue(nextState, IQNodeInterface.ValueType.ExternalValue);
		} else {
			//if(currentTask.)
			if(currentTask.getParentQ()!=null && !m_taskGraph.isTerminal(nextState, currentTask.getParentQ().getParentMax())) {
			//if(currentTask.getParentQ()!=null) {
				MaxNode nextTask = bestActionSelection(nextState, currentTask.getParentQ().getParentMax(), m_qType);
				externalValue = getProjectedValue(nextState, nextTask, m_qType);
			}
		}
		
		double alpha = m_alpha;
		int N = 1;
		Stack<State> reverseStack = new Stack<State>();
		/** update completion values and external values for 
		 * each intermediate state in the interStates */
		while(!subSubTaskSeq.isEmpty()) {
			State tempState = subSubTaskSeq.pop();
			reverseStack.push(tempState);
			// update completion value for taking action at tempState in task
			double oldValue = actionQNode.getValue(tempState, IQNodeInterface.ValueType.CompletionValue);
			double newValue = (1-alpha)*oldValue + alpha*Math.pow(m_gamma, N)*(projectedValue+completionValue);
			actionQNode.setValue(tempState, newValue, IQNodeInterface.ValueType.CompletionValue);
			// update external value for taking action at tempState in task
			oldValue = actionQNode.getValue(tempState, IQNodeInterface.ValueType.ExternalValue);
			newValue = (1-alpha)*oldValue + alpha*Math.pow(m_gamma, N)*externalValue;
			actionQNode.setValue(tempState, newValue, IQNodeInterface.ValueType.ExternalValue);
			N++;
		}
		return reverseStack;
	}
	
	protected double getProjectedValue(State state, MaxNode maxNode, MaxNode action, IMaxNodeInterface.ValueType vType) {
		switch (vType) {
		case RCEProjectedValue: // implemented in ALispQAgent
			return getProjectedValue(state, action, IMaxNodeInterface.ValueType.ProjectedValue)
				+ action.getParentQ().getValue(state, IQNodeInterface.ValueType.CompletionValue)
				+ action.getParentQ().getValue(state, IQNodeInterface.ValueType.ExternalValue);
		default:
			break;
		}
		return super.getProjectedValue(state, maxNode, action, vType);
	}
}















