package edu.cwru.eecs.rl.domains;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import edu.cwru.eecs.rl.agent.AgentType;
import edu.cwru.eecs.rl.core.maxnode.IMaxNode;
import edu.cwru.eecs.rl.core.maxnode.IMaxNodeInterface;
import edu.cwru.eecs.rl.core.maxnode.MaxNode;
import edu.cwru.eecs.rl.core.mdp.Parameter;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.qnode.IQNode;
import edu.cwru.eecs.rl.core.qnode.IQNodeInterface;
import edu.cwru.eecs.rl.core.qnode.QNode;
import edu.cwru.eecs.rl.env.EnvSpec;

public abstract class MaxQGraph implements Serializable{
	
	private static final long serialVersionUID = 3067397204588949311L;
	
	public HType HTYPE; 
	
	public enum HType {
		FLAT, HIERARCHY;
	}
	
	/** to be initialized in the subclass */
	protected int m_maxNodeNumber;
	protected int m_qNodeNumber;
	protected int m_primitiveMaxNodeNumber;
	protected String[] m_taskNames;
	protected String[] m_qNodeNames;
	
	protected int m_rootMaxNodeIndex;
	protected IMaxNodeInterface[] m_MaxNodes;
	protected IQNodeInterface[] m_QNodes;
	protected HashMap<Integer, Integer> m_Q_MaxParent_Connection;
	protected HashMap<Integer, Integer> m_Q_MaxChild_Connection;
	
	/** initialize here */
	protected AgentType m_agentType;
	protected EnvSpec m_envSpec;
	protected DomainSpec m_domainSpec;
	
	public MaxQGraph(EnvSpec envSpec, AgentType agentType, DomainSpec domainSpec) {
		m_envSpec = envSpec;
		m_agentType = agentType;
		m_domainSpec = domainSpec;
		
		initializeTaskNames();
		initializeQNodeNames();
		initializeConnections();
		buildMaxQGraph();
	}
	
	public int getMaxNodeNumber() {
		return m_maxNodeNumber;
	}
	
	public int getQNodeNumber() {
		return m_qNodeNumber;
	}
	
	public int getPrimitiveMaxNodeNumber() {
		return m_primitiveMaxNodeNumber;
	}
	
	public String getMaxNodeName(int index) {
		return m_taskNames[index];
	}
	
	public String getQNodeName(int index) {
		return m_qNodeNames[index];
	}
	
	public IMaxNodeInterface getMaxNode(int index) {
		return m_MaxNodes[index];
	}

	public IQNodeInterface getQNode(int index) {
		return m_QNodes[index];
	}

	public EnvSpec getEnvSpec() {
		return m_envSpec;
	}
	
	public String getTaskName() {
		return m_envSpec.getName();
	}
	
	public IMaxNodeInterface getRoot() {
		return m_MaxNodes[m_rootMaxNodeIndex];
	}
	
	public int getRootIndex() {
		return m_rootMaxNodeIndex;
	}
	
	public AgentType getAgentType() {
		return m_agentType;
	}
	
	protected void buildMaxQGraph() {
		/** build in a bottom-up way. */
		
		// create max nodes
		m_MaxNodes = new IMaxNodeInterface[m_maxNodeNumber];
		for(int i=0; i<m_maxNodeNumber; i++) {
			boolean isPrimitive = false;
			if(i<m_primitiveMaxNodeNumber) {
				isPrimitive = true;
			}
			IMaxNodeInterface regularMaxNode = new IMaxNode(i, isPrimitive, this);
			m_MaxNodes[i] = MaxQGraphBuilder.buildMaxNode(regularMaxNode, m_agentType);
		}
		
		// create q nodes
		m_QNodes = new IQNodeInterface[m_qNodeNumber];
		for(int i=0; i<m_qNodeNumber; i++) {
			IQNodeInterface regularQNode = new IQNode(i, this);
			m_QNodes[i] = MaxQGraphBuilder.buildQNode(regularQNode, m_agentType);
		}
		
		// create connections between max nodes and q nodes.
		Set<Integer> qToParentIndices = m_Q_MaxParent_Connection.keySet();
		for (Iterator<Integer> iterator = qToParentIndices.iterator(); iterator.hasNext();) {
			Integer qToParentIndex = (Integer) iterator.next();
			m_MaxNodes[m_Q_MaxParent_Connection.get(qToParentIndex)].addQNode(m_QNodes[qToParentIndex]);
			m_QNodes[qToParentIndex].setParentIMaxNode(m_MaxNodes[m_Q_MaxParent_Connection.get(qToParentIndex)]);
		}
		
		Set<Integer> qIndices = m_Q_MaxChild_Connection.keySet();
		for (Iterator<Integer> iterator = qIndices.iterator(); iterator.hasNext();) {
			Integer qIndex = (Integer) iterator.next();
			m_QNodes[qIndex].setChildIMaxNode(m_MaxNodes[m_Q_MaxChild_Connection.get(qIndex)]);
		}
	}
	
	
	protected abstract void initializeTaskNames();
	protected abstract void initializeQNodeNames();
	protected abstract void initializeConnections();
	
	public boolean isGoal(State state, MaxNode maxNode){
		return m_domainSpec.isGoal(state, maxNode);
	}
	
	public boolean isNonGoalTerminal(State state, MaxNode maxNode) {
		return m_domainSpec.isNonGoalTerminal(state, maxNode);
	}
	
	public boolean isTerminal(State state, MaxNode maxNode){
		return m_domainSpec.isTerminal(state, maxNode);
	}
	public boolean isStartingState(State state, MaxNode maxNode) {
		return m_domainSpec.isStartingState(state, maxNode);
	}
	public ArrayList<Parameter> getQParameters(State state, MaxNode maxNode, IQNodeInterface iQNode){
		return m_domainSpec.getQParameters(state, maxNode, iQNode);
	}
	public ArrayList<Parameter> getMaxParameters(State state, QNode qNode){
		return m_domainSpec.getMaxParameters(state, qNode);
	}
	public State maxStateAbsMapping(State state, MaxNode maxNode){
		return m_domainSpec.maxStateAbsMapping(state, maxNode);
	}
	public State qStateAbsMapping(State state, QNode qNode){
		return m_domainSpec.qStateAbsMapping(state, qNode);
	}
	public State eStateAbsMapping(State state, QNode qNode) {
		return m_domainSpec.eStateAbsMapping(state, qNode);
	}
	
	public boolean singleExit(MaxNode maxNode) {
		return m_domainSpec.singleExit(maxNode);
	}
}
