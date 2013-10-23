package edu.cwru.eecs.rl.core.qnode;


import java.io.PrintStream;
import java.util.Set;

import edu.cwru.eecs.rl.agent.AgentType;
import edu.cwru.eecs.rl.core.maxnode.IMaxNodeInterface;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.domains.MaxQGraph;



public class IQNodeDecorator implements IQNodeInterface {
	
	private static final long serialVersionUID = -109749876718799690L;
	
	protected IQNodeInterface m_iQNode;
	
	public IQNodeDecorator(IQNodeInterface qNode) {
		m_iQNode = qNode;
	}

	@Override
	public IMaxNodeInterface getParentIMaxNode() {
		return m_iQNode.getParentIMaxNode();
	}

	@Override
	public IMaxNodeInterface getChildIMaxNode() {
		return m_iQNode.getChildIMaxNode();
	}
	
	@Override
	public void setParentIMaxNode(IMaxNodeInterface maxNode) {
		m_iQNode.setParentIMaxNode(maxNode);
	}

	@Override
	public void setChildIMaxNode(IMaxNodeInterface maxNode) {
		m_iQNode.setChildIMaxNode(maxNode);
	}

	@Override
	public String nameString() {
		return m_iQNode.nameString();
	}

	@Override
	public AgentType getAgentType() {
		return m_iQNode.getAgentType();
	}


	@Override
	public int name() {
		return m_iQNode.name();
	}


	@Override
	public MaxQGraph getMaxQGraph() {
		return m_iQNode.getMaxQGraph();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((m_iQNode == null) ? 0 : m_iQNode.hashCode());
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
		IQNodeDecorator other = (IQNodeDecorator) obj;
		if (m_iQNode == null) {
			if (other.m_iQNode != null)
				return false;
		} else if (!m_iQNode.equals(other.m_iQNode))
			return false;
		return true;
	}

	@Override
	public double getValue(State state, QNode qNode, ValueType vType) {
		return m_iQNode.getValue(state, qNode, vType);
	}


	@Override
	public void setValue(State state, QNode qNode, double value, ValueType vType) {
		m_iQNode.setValue(state, qNode, value, vType);
	}


	@Override
	public void clearValues(QNode qNode, ValueType vType, Set<State> states) {
		m_iQNode.clearValues(qNode, vType, states);
	}
	
	@Override
	public void printVF(PrintStream ps, boolean real, ValueType vType) {
		m_iQNode.printVF(ps, real, vType);
	}
	
	
}
