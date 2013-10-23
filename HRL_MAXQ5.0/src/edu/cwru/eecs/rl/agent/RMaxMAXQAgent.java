package edu.cwru.eecs.rl.agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.google.common.collect.HashBiMap;

import edu.cwru.eecs.rl.core.maxnode.IMaxNodeInterface;
import edu.cwru.eecs.rl.core.maxnode.MaxNode;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.qnode.IQNodeInterface;
import edu.cwru.eecs.rl.env.Environment;
import edu.cwru.eecs.rl.experiment.FileProcessor;

public class RMaxMAXQAgent extends MaxQAgent {

	private static final long serialVersionUID = 2418138902537819685L;
	
	private HashBiMap<State, Integer> m_allStateSet;
	
	private RMaxQLogger m_logger;

	protected RMaxMAXQAgent(Environment environment, AgentType agentType) {
		super(environment, agentType);
		int K = 10;
		m_logger = new RMaxQLogger(K);
		m_allStateSet = environment.getAllStateSet();
		m_debug = false;
	}
	
	public RMaxMAXQAgent(Environment environment, String[] args) {
		this(environment, AgentType.RMaxMaxQ);
	}
	
	public String message(String message) {
		String[] messages = message.split(",");
		if(messages[0].trim().equals("vf")) {
			if(messages[1].trim().equals("print")) {
				if(messages.length>2) {
					String pathString = messages[2].trim();
					try {
						if(m_agentType.nodeTypes().contains(AgentType.NodeType.V_QQValue)) {
							PrintStream ps_qv = new PrintStream(new File(pathString+FileProcessor.RESULT_QV_EXTENSION));
							printQV(ps_qv, true);
							ps_qv.close();
						}
						if(m_agentType.nodeTypes().contains(AgentType.NodeType.V_MaxStateValue)) {
							PrintStream ps_sv = new PrintStream(new File(pathString+FileProcessor.RESULT_SV_EXTENSION));
							printSV(ps_sv, true);
							ps_sv.close();
						}
						if(m_agentType.nodeTypes().contains(AgentType.NodeType.M_RMaxQ)) {
							PrintStream ps_rm = new PrintStream(new File(pathString+FileProcessor.RESULT_RM_EXTENSION));
							printRM(ps_rm, true);
							ps_rm.close();
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
			else {
				if(m_agentType.nodeTypes().contains(AgentType.NodeType.V_QQValue)) {
					printQV(System.out, false);
				}
				if(m_agentType.nodeTypes().contains(AgentType.NodeType.V_MaxStateValue)) {
					printSV(System.out, false);
				}
			}
		}
		return super.message(message);
	}
	
	/******************************************************************
	 * All the printing methods
	 ******************************************************************/
	protected void printQV(PrintStream ps, boolean real) {
		ps.println("Q Value Function for Subtasks: " + m_taskGraph.getTaskName());
		int qNodeNum = m_taskGraph.getQNodeNumber();
		ps.println("Summary: -------------");
		for(int i=0; i<qNodeNum; i++) {
			IQNodeInterface iQNode = m_taskGraph.getQNode(i);
			iQNode.printVF(ps, false, IQNodeInterface.ValueType.QValue);
		}
		ps.println("Summary end: ---------------");
		for(int i=0; i<qNodeNum; i++) {
			IQNodeInterface iQNode = m_taskGraph.getQNode(i);
			iQNode.printVF(ps, real, IQNodeInterface.ValueType.QValue);
		}
	}
	
	protected void printSV(PrintStream ps, boolean real) {
		ps.println("State Value Function for Subtasks: " + m_taskGraph.getTaskName());
		int maxNodeNum = m_taskGraph.getMaxNodeNumber();
		for(int i=0; i<maxNodeNum; i++) {
			IMaxNodeInterface iMaxNode = m_taskGraph.getMaxNode(i);
			iMaxNode.printVF(ps, real, IMaxNodeInterface.ValueType.StateValue);
		}
	}
	
	protected void printRM(PrintStream ps, boolean real) {
		ps.println("Model for Subtasks: " + m_taskGraph.getTaskName());
		int maxNodeNum = m_taskGraph.getMaxNodeNumber();
		for(int i=0; i<maxNodeNum; i++) {
			IMaxNodeInterface iMaxNode = m_taskGraph.getMaxNode(i);
			iMaxNode.printVF(ps, real, IMaxNodeInterface.ValueType.RMaxQModel);
		}
	}

	
	/**
	 * R-MAXQ algorithm
	 */
	protected Stack<State> MAXQ(MaxNode currentTask, State state) {
		
		/** if primitive task */
		if(currentTask.isPrimitive() && m_maxStep>0) {
			/** take action */
			if(m_debug)
				System.out.println("Step: " + m_step + " \t" + state + " Action: " + m_environment.actionName((int)currentTask.getPrimitiveAction().getAction()));
			m_environment.takeAction(currentTask.getPrimitiveAction());
			double reward = m_environment.getReward();
			m_step ++;
			m_cumulativeReward += reward;
			m_maxStep--;
			
			/** UPDATE the primitive model */
			currentTask.updateModel(state, reward, m_environment.getState());
			m_logger.incT();
		}
		/** if composite subtask */
		else {
			while(!m_taskGraph.isTerminal(state, currentTask) && m_maxStep>0)
			{
				computePolicy(currentTask, state);
				
				/** choose action according to current exploration policy */
				double epsilon = m_epsilon;
				MaxNode subTask = eGreedyActionSelection(state, currentTask, m_qType, epsilon);
				
				/** take action (subtask) */
				MAXQ(subTask, state);
				state = m_environment.getState();
			}
		}
		return null;
	}
	
	
	protected void computePolicy(MaxNode a, State s) {
		/** update time stamp */
		if(!m_logger.outdated(a))
			return ;
		else {
			m_logger.updateTimeStamp(a);
			m_logger.clearEnvelope(a);
		}
		
		if(m_debug)
			System.out.println("Compute Policy: " + a.getNodeDescription());
		
		/** prepare envelope */
		prepareEnvelope(a, s);
		
		/** value iteration */
		double delta = Double.MAX_VALUE;
		int step = 0;
		
		// clean up old values
		// TODO: instead of clear everything, only clear states in the envelope.
//		a.clearValues(IMaxNodeInterface.ValueType.StateValue, m_envelope.get(a));
		ArrayList<MaxNode> subActions = getValidMaxNodes(s, a);
//		for(MaxNode subA : subActions) {
//			subA.getParentQ().clearValues(IQNodeInterface.ValueType.QValue, m_envelope.get(a));
//		}
		
		while(delta>0.001 && step<300) {  // MAGIC NUMBERS
		//while(step<30) {
			double maxDelta = 0;
			double maxNewQ = Double.NEGATIVE_INFINITY;
			Set<State> envelope = m_logger.getEnvelope(a);
			for(State tempState : envelope) {
				if(m_taskGraph.isTerminal(tempState, a))
					continue ;
				subActions = getValidMaxNodes(tempState, a);
				for(MaxNode subA : subActions) {
					// update Q(s', a') using Equation 1
					double oldQ = subA.getParentQ().getValue(tempState, IQNodeInterface.ValueType.QValue);
					double q = subA.getReward(tempState);
					for(State ns : m_allStateSet.keySet()) {
						double prob = subA.getTransition(tempState, ns);
						if(prob>0 && !m_taskGraph.isTerminal(ns, a)) {
							//q += prob*a.getValue(ns, IMaxNodeInterface.ValueType.StateValue);
							MaxNode bestA = bestActionSelection(ns, a, m_qType);
							double sv = bestA.getParentQ().getValue(ns, IQNodeInterface.ValueType.QValue);
							q += prob*sv;
						}
					}
					subA.getParentQ().setValue(tempState, q, IQNodeInterface.ValueType.QValue);
					// update maxDelta
					if(Math.abs(oldQ-q)>maxDelta)
						maxDelta = Math.abs(oldQ-q); 
					// update maxQ
					if(q>maxNewQ)
						maxNewQ = q;
				}
			}
			delta = maxDelta;
		//	System.out.println("delta: " + delta);
			step++;
		}
		if(m_debug) {
			System.out.println("~Compute Policy: " + a.getNodeDescription() + " step: " + step + " delta: " + delta + "\n");
		}
	}
	
	protected void prepareEnvelope(MaxNode a, State s) {
		if(m_taskGraph.isTerminal(s, a))
			return ;
		Set<State> envelope = m_logger.getEnvelope(a);
		if(!envelope.contains(s)) {
			//if(m_debug)
			//	System.out.println("Prepare Envelope: " + a.getNodeDescription());
			envelope.add(s);
			ArrayList<MaxNode> subActions = getValidMaxNodes(s, a);
			for(MaxNode subA : subActions) {
				computeModel(subA, s);
				// prepare envelope for each possible next state
				for(State ns : m_allStateSet.keySet()) {
					if(subA.getTransition(s, ns)>0) 
						prepareEnvelope(a, ns);
				}
			}
		}
	}
	
	protected void computeModel(MaxNode a, State s) {
		if(!a.isPrimitive()) {
			if(!m_logger.outdated(a))
				return ;
			
			if(m_debug)
				System.out.println("Compute Model: " + a.getNodeDescription());
			computePolicy(a, s);
			/** dynamic programming */
			double delta = Double.MAX_VALUE;
			int step = 0;
			
			// clean up old values
			// TODO: instead of clear everything, only clear states in the envelope.
			//a.clearRewards(m_envelope.get(a));
			//a.clearTransitions(m_envelope.get(a));
			
			while(delta>0.001 && step<300) {  // MAGIC NUMBERS
			//while(step<30) {
				double maxDelta = 0;
				Set<State> envelope = m_logger.getEnvelope(a);
				Set<State> terminals = new HashSet<State>();
				Set<State> nonterminals = new HashSet<State>();
				for(State x : m_allStateSet.keySet()) {
					if(m_taskGraph.isTerminal(x, a))
						terminals.add(x);
					else {
						nonterminals.add(x);
					}
				}
				
				for(State tempState : envelope) {
					MaxNode subA = bestActionSelection(tempState, a, m_qType);
					Set<State> subATerminal = new HashSet<State>();
					for(State ns : nonterminals) {
						if(m_taskGraph.isTerminal(ns, subA))
							subATerminal.add(ns);
					}
					// update R^a(s') using Equation 4
					double oldR = a.getReward(tempState);
					double r = subA.getReward(tempState);
					for(State ns : m_allStateSet.keySet()) {
						if(!m_taskGraph.isTerminal(ns, a)) {
							r += subA.getTransition(tempState, ns)*a.getReward(ns);
						}
					}
					a.setReward(tempState, r);
					if(Math.abs(oldR-r)>maxDelta)
						maxDelta=Math.abs(oldR-r);
					// update P^a(s', x) using Equation 5, 
					// 		for all terminal state x in a
					for(State x : terminals) {
						double oldP = a.getTransition(tempState, x);
						double p = subA.getTransition(tempState, x);
						for(State ns : subATerminal) {
							p += subA.getTransition(tempState, ns)*a.getTransition(ns, x);
						}
						a.setTransition(tempState, x, p);
						if(Math.abs(oldP-p)>maxDelta)
							maxDelta=Math.abs(oldP-p);
					}	
				}
				delta = maxDelta;
			//	System.out.println("delta: " + delta);
				step++;
			} // end while
			if(m_debug) {
				System.out.println("~Compute Model: " + a.getNodeDescription() + " step: " + step + " delta: " + delta + "\n");
			}
		} // end if
	}
	
	
	
	
	/*********************************************************************/
	/** action selection related methods */
	protected double[] getAllQValues(State state, ArrayList<MaxNode> allValidMaxNodes, IMaxNodeInterface.ValueType vType) {
		int actionSize = allValidMaxNodes.size();
		double[] allQValues = new double[actionSize];
		for(int i=0; i<actionSize; i++) {
			MaxNode action = allValidMaxNodes.get(i);
			allQValues[i] = action.getParentQ().getValue(state, IQNodeInterface.ValueType.QValue);
		}
		return allQValues;
	}

}

class RMaxQLogger implements Serializable {
	
	private static final long serialVersionUID = -6975374357686231892L;
	private int m_K = 0;
	private long m_t;
	private Map<MaxNode, Long> m_timeStamp;
	private Map<MaxNode, Set<State>> m_envelope;
	
	public RMaxQLogger(int k) {
		m_K = (k>=0 ? k : 0);
		m_t = 1;
		m_timeStamp = new HashMap<MaxNode, Long>();
		m_envelope = new HashMap<MaxNode, Set<State>>();
	}
	
	public void incT() {
		m_t++;
	}
	
	public long getT() {
		return m_t;
	}
	
	public long getTimeStamp(MaxNode a) {
		if(!m_timeStamp.containsKey(a))
			m_timeStamp.put(a, -1l);
		return m_timeStamp.get(a);
	}
	
	public boolean outdated(MaxNode a) {
		if(getTimeStamp(a)<m_t-m_K)
			return true;
		return false;
	}
	
	public void updateTimeStamp(MaxNode a) {
		m_timeStamp.put(a, m_t);
	}
	
	public Set<State> getEnvelope(MaxNode maxNode) {
		return m_envelope.get(maxNode);
	}
	
	public void clearEnvelope(MaxNode a) {
		if(!m_envelope.containsKey(a))
			m_envelope.put(a, new HashSet<State>());
		m_envelope.get(a).clear();
	}
}










