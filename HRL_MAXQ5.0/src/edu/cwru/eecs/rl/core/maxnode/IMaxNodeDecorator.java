package edu.cwru.eecs.rl.core.maxnode;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Set;

import edu.cwru.eecs.rl.agent.AgentType;
import edu.cwru.eecs.rl.core.mdp.Action;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.model.Model;
import edu.cwru.eecs.rl.core.qnode.IQNodeInterface;
import edu.cwru.eecs.rl.domains.MaxQGraph;



public class IMaxNodeDecorator implements IMaxNodeInterface {
	
	private static final long serialVersionUID = 2485023919334056577L;
	
	protected IMaxNodeInterface m_iMaxNode;
	
	public IMaxNodeDecorator(IMaxNodeInterface iMaxNode) {
		m_iMaxNode = iMaxNode;
	}

	@Override
	public boolean isPrimitive() {
		return m_iMaxNode.isPrimitive();
	}

	
	@Override
	public Action getPrimitiveAction() {
		return m_iMaxNode.getPrimitiveAction();
	}


	@Override
	public void addQNode(IQNodeInterface qNode) {
		m_iMaxNode.addQNode(qNode);
	}

	@Override
	public IQNodeInterface getQNode(int index) {
		return m_iMaxNode.getQNode(index);
	}

	@Override
	public ArrayList<IQNodeInterface> getQNodes() {
		return m_iMaxNode.getQNodes();
	}

	@Override
	public String nameString() {
		return m_iMaxNode.nameString();
	}

	@Override
	public AgentType getAgentType() {
		return m_iMaxNode.getAgentType();
	}
	
	@Override
	public int name() {
		return m_iMaxNode.name();
	}

	@Override
	public MaxQGraph getMaxQGraph() {
		return m_iMaxNode.getMaxQGraph();
	}

	@Override
	public boolean isRoot() {
		return m_iMaxNode.isRoot();
	}

	/**********************************************************************
	 *  about value function 
	 */

	@Override
	public double getValue(State state, MaxNode maxNode, ValueType vType) {
		return m_iMaxNode.getValue(state, maxNode, vType);
	}

	@Override
	public void setValue(State state, MaxNode maxNode, double value,
			ValueType vType) {
		m_iMaxNode.setValue(state, maxNode, value, vType);
	}

	@Override
	public void clearValues(MaxNode maxNode, ValueType vType, Set<State> states) {
		m_iMaxNode.clearValues(maxNode, vType, states);
	}
	
	@Override
	public void printVF(PrintStream ps, boolean real, ValueType vType) {
		m_iMaxNode.printVF(ps, real, vType);
	}
	
	/**********************************************************************
	 *  about model
	 */
	
	@Override
	public Model getModel() {
		return m_iMaxNode.getModel();
	}
	
	
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((m_iMaxNode == null) ? 0 : m_iMaxNode.hashCode());
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
		IMaxNodeDecorator other = (IMaxNodeDecorator) obj;
		if (m_iMaxNode == null) {
			if (other.m_iMaxNode != null)
				return false;
		} else if (!m_iMaxNode.equals(other.m_iMaxNode))
			return false;
		return true;
	}

	

}
