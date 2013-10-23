package edu.cwru.eecs.rl.core.maxnode;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Set;

import edu.cwru.eecs.rl.core.maxnode.IMaxNodeInterface.ValueType;
import edu.cwru.eecs.rl.core.mdp.Action;
import edu.cwru.eecs.rl.core.mdp.Parameter;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.qnode.QNode;



public class MaxNode implements Serializable{
	
	private static final long serialVersionUID = 3985682265093987050L;
	
	private IMaxNodeInterface m_iMaxNode;
	private Parameter m_parameter;
	
	private QNode m_parentQ;
	private QNode m_childQ;
	
	
	public MaxNode(IMaxNodeInterface iMaxNode, Parameter parameter){
		m_iMaxNode = iMaxNode;
		m_parameter = parameter;
	}
	
//	public MaxNode clone() {
//		MaxNode newMaxNode = new MaxNode(m_iMaxNode, m_parameter);
//		newMaxNode.setParent(m_parentQ);
//		return newMaxNode;
//	}
	
	/** basic getter and setter methods */
	
	public QNode getParentQ() {
		return m_parentQ;
	}
	public void setParentQ(QNode parentQ) {
		m_parentQ = parentQ;
	}
	public void setChildQ(QNode childQ) {
		m_childQ = childQ;
	}
	public QNode getChildQ() {
		return m_childQ;
	}
	
	public int name() {
		return m_iMaxNode.name();
	}
	
	public String nameString() {
		return m_iMaxNode.nameString();
	}
	
	public IMaxNodeInterface getIMaxNode() {
		return m_iMaxNode;
	}
	
	public Parameter getParameter() {
		return m_parameter;
	}
	
	public String getNodeDescription() {
		return m_iMaxNode.nameString() + " " + m_parameter;
	}
	

	public boolean isPrimitive() {
		return m_iMaxNode.isPrimitive();
	}
	
	public Action getPrimitiveAction() {
		Action action = m_iMaxNode.getPrimitiveAction();
		return new Action(action.getAction(), m_parameter);
	}
	

	
	
	/**********************************************************************
	 *  about value function 
	 */

	public double getValue(State state, IMaxNodeInterface.ValueType vType) {
		return m_iMaxNode.getValue(state, this, vType);
	}
	
	public void setValue(State state, double value, IMaxNodeInterface.ValueType vType) {
		m_iMaxNode.setValue(state, this, value, vType);
	}

	public void clearValues(IMaxNodeInterface.ValueType vType, Set<State> states) {
		m_iMaxNode.clearValues(this, vType, states);
	}
	
	public void printVF(PrintStream ps, boolean real, ValueType vType) {
		m_iMaxNode.printVF(ps, real, vType);
	}
	
	/**********************************************************************
	 *  about model
	 */
	
	public void updateModel(State state, double reward,
			State nextState) {
		m_iMaxNode.getModel().updateModel(state, this, reward, nextState);
	}

	public double getReward(State state) {
		return m_iMaxNode.getModel().getReward(state, this);
	}

	public double getTransition(State state, State nextState) {
		return m_iMaxNode.getModel().getTransition(state, this, nextState);
	}

	public State getNextState(State state) {
		return m_iMaxNode.getModel().getNextState(state, this);
	}
	
	public void setReward(State state, double reward) {
		m_iMaxNode.getModel().setReward(state, this, reward);
	}
	public void setTransition(State state, State nextState, double prob){
		m_iMaxNode.getModel().setTransition(state, this, nextState, prob);
	}
	public void clearRewards(Set<State> states){
		m_iMaxNode.getModel().clearRewards(this, states);
	}
	public void clearTransitions(Set<State> states){
		m_iMaxNode.getModel().clearTransitions(this, states);
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((m_iMaxNode == null) ? 0 : m_iMaxNode.hashCode());
		result = prime * result
				+ ((m_parameter == null) ? 0 : m_parameter.hashCode());
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
		MaxNode other = (MaxNode) obj;
		if (m_iMaxNode == null) {
			if (other.m_iMaxNode != null)
				return false;
		} else if (!m_iMaxNode.equals(other.m_iMaxNode))
			return false;
		if (m_parameter == null) {
			if (other.m_parameter != null)
				return false;
		} else if (!m_parameter.equals(other.m_parameter))
			return false;
		return true;
	}

	public String toString() {
		return getNodeDescription();
	}
}










