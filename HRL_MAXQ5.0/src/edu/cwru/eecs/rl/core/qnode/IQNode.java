package edu.cwru.eecs.rl.core.qnode;


import java.io.PrintStream;
import java.util.Set;

import edu.cwru.eecs.rl.agent.AgentType;
import edu.cwru.eecs.rl.core.maxnode.IMaxNodeInterface;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.domains.MaxQGraph;



/**
 * This is the regular internal Q Node, which supports only maxq-0 algorithm.
 * i.e. It stores regular completion function.
 * @author feng
 *
 */
public class IQNode implements IQNodeInterface{
	
	private static final long serialVersionUID = 8163915721061635190L;

	protected AgentType m_agentType;
	
	/** general information */
	protected MaxQGraph m_maxQGraph;
	protected int m_name; // index in the max q graph
	protected IMaxNodeInterface m_parentIMaxNode;
	protected IMaxNodeInterface m_childIMaxNode;
	
	//protected HashMap<QNode, TabularVF<State>> m_completionFunction;
	
	public IQNode(int name, MaxQGraph maxQGraph) {
		m_maxQGraph = maxQGraph;
		m_agentType = m_maxQGraph.getAgentType();
		m_name = name;
	}
	
	@Override
	public IMaxNodeInterface getParentIMaxNode() {
		return m_parentIMaxNode;
	}

	@Override
	public IMaxNodeInterface getChildIMaxNode() {
		return m_childIMaxNode;
	}
	
	@Override
	public void setParentIMaxNode(IMaxNodeInterface iMaxNode) {
		m_parentIMaxNode = iMaxNode;
	}

	@Override
	public void setChildIMaxNode(IMaxNodeInterface iMaxNode) {
		m_childIMaxNode = iMaxNode;
	}

	public int name() {
		return m_name;
	}
	
	@Override
	public String nameString() {
		return m_maxQGraph.getQNodeName(m_name);
	}
	
	@Override
	public MaxQGraph getMaxQGraph() {
		return m_maxQGraph;
	}
	
	/**
	 * @return node type
	 */
	@Override
	public AgentType getAgentType() {
		return m_agentType;
	}

	
	
	@Override
	public double getValue(State state, QNode qNode, ValueType vType) {
		System.err.println("ValueType no supported in getValue(): " + vType.toString());
		return Double.NaN;
	}
	
	@Override
	public void setValue(State state, QNode qNode, double value, ValueType vType) {
		System.err.println("ValueType no supported in setValue(): " + vType.toString());
	}
	
	@Override
	public void clearValues(QNode qNode, ValueType vType, Set<State> states) {
		System.err.println("ValueType no supported in clearValues(): " + vType.toString());
	}
	
	@Override
	public void printVF(PrintStream ps, boolean real, ValueType vType) {
		System.err.println("ValueType no supported in printValues(): " + vType.toString());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((m_childIMaxNode == null) ? 0 : m_childIMaxNode.hashCode());
		result = prime * result + m_name;
		result = prime
				* result
				+ ((m_parentIMaxNode == null) ? 0 : m_parentIMaxNode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IQNode other = (IQNode) obj;
		if (m_childIMaxNode == null) {
			if (other.m_childIMaxNode != null)
				return false;
		} else if (!m_childIMaxNode.equals(other.m_childIMaxNode))
			return false;
		if (m_name != other.m_name)
			return false;
		if (m_parentIMaxNode == null) {
			if (other.m_parentIMaxNode != null)
				return false;
		} else if (!m_parentIMaxNode.equals(other.m_parentIMaxNode))
			return false;
		return true;
	}
	
}
