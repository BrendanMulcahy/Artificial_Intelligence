package edu.cwru.eecs.rl.core.qnode;

import java.io.Serializable;
import java.util.Set;

import edu.cwru.eecs.rl.core.maxnode.MaxNode;
import edu.cwru.eecs.rl.core.mdp.Parameter;
import edu.cwru.eecs.rl.core.mdp.State;


public class QNode implements Serializable{
	
	private static final long serialVersionUID = -1368945229922423620L;
	
	protected IQNodeInterface m_iQNode;
	protected Parameter m_parameter;
	
	
	protected MaxNode m_parentMax;
	protected MaxNode m_childMax;
	
	
	public QNode(IQNodeInterface iQNode, Parameter parameter) {
		m_iQNode = iQNode;
		m_parameter = parameter;
	}

	public QNode clone() {
		QNode newQNode = new QNode(m_iQNode, m_parameter);
		newQNode.setParentMax(m_parentMax);
		return newQNode;
	}
	
	/** basic getter and setter methods */
	
	public void setParentMax(MaxNode parentMax) {
		m_parentMax = parentMax;
	}
	public MaxNode getParentMax() {
		return m_parentMax;
	}
	
	public void setChildMax(MaxNode childMax) {
		m_childMax = childMax;
	}
	public MaxNode getChildMax() {
		return m_childMax;
	}
	
	public int name() {
		return m_iQNode.name();
	}
	
	public String nameString() {
		return m_iQNode.nameString();
	}
	
	public IQNodeInterface getIQNode(){
		return m_iQNode;
	}
	public Parameter getParameter() {
		return m_parameter;
	}
	
	public String getNodeDescription() {
		return m_iQNode.nameString() + m_parameter;
	}
	
	
	public double getValue(State state, IQNodeInterface.ValueType vType) {
		return m_iQNode.getValue(state, this, vType);
	}
	
	public void setValue(State state, double value, IQNodeInterface.ValueType vType) {
		m_iQNode.setValue(state, this, value, vType);
	}
	
	public void clearValues(IQNodeInterface.ValueType vType, Set<State> states) {
		m_iQNode.clearValues(this, vType, states);
	}
	
	/*********************************************************************/
	/** getter and setter for completion function and pseudo completion function */
	
//	public double getCompletionValue(State state, boolean pseudo) {
//		if(!pseudo)
//			return m_iQNode.getCompletionValue(state, this);
//		else {
//			return m_iQNode.getPseudoCompletionValue(state, this);
//		}
//	}
//	
//	public void setCompletionValue(State state, double value, boolean pseudo) {
//		if(!pseudo)
//			m_iQNode.setCompletionValue(state, this, value);
//		else {
//			m_iQNode.setPseudoCompletionValue(state, this, value);
//		}
//	}
	
	
	/*********************************************************************/
	/** model based related methods */
	
//	public void clearCompletionFunction(boolean pseudo) {
//		m_iQNode.clearCompletionFunction(this, pseudo);
//	}
	
//	public boolean equals(Object obj) {
//		QNode temp = (QNode)obj;
//		if(!(m_iQNode.equals(temp.m_iQNode) && m_parameter.equals(temp.m_parameter)))
//			return false;
//		else if(!m_parentMax.getParameter().equals(temp.m_parentMax.getParameter()))
//			return false;
//		else if(m_childMax!=null && !m_childMax.getParameter().equals(temp.m_childMax.getParameter()))
//			return false;
//		return true;
//	}
	
	
	
	public String toString() {
		String res = m_iQNode.nameString() + " " + m_parameter.toString();
		if(m_childMax!=null)
			res += " == " + m_childMax.getNodeDescription();
		return res;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((m_childMax == null) ? 0 : m_childMax.hashCode());
		result = prime * result
				+ ((m_iQNode == null) ? 0 : m_iQNode.hashCode());
		result = prime * result
				+ ((m_parameter == null) ? 0 : m_parameter.hashCode());
		result = prime * result
				+ ((m_parentMax == null) ? 0 : m_parentMax.hashCode());
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
		QNode other = (QNode) obj;
		if (m_childMax == null) {
			if (other.m_childMax != null)
				return false;
		} else if (!m_childMax.equals(other.m_childMax))
			return false;
		if (m_iQNode == null) {
			if (other.m_iQNode != null)
				return false;
		} else if (!m_iQNode.equals(other.m_iQNode))
			return false;
		if (m_parameter == null) {
			if (other.m_parameter != null)
				return false;
		} else if (!m_parameter.equals(other.m_parameter))
			return false;
		if (m_parentMax == null) {
			if (other.m_parentMax != null)
				return false;
		} else if (!m_parentMax.equals(other.m_parentMax))
			return false;
		return true;
	}
}
