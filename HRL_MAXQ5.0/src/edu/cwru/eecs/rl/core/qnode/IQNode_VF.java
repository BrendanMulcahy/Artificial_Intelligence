package edu.cwru.eecs.rl.core.qnode;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Set;

import edu.cwru.eecs.rl.agent.AgentType;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.vf.TabularVF;
import edu.cwru.eecs.rl.domains.MaxQGraph;

public class IQNode_VF extends IQNodeDecorator {

	private static final long serialVersionUID = -8424576311456425985L;
	
	protected HashMap<QNode, TabularVF<State>> m_valueFunction;
	
	protected ValueType m_vType; // ValueType.CompletionValue || PseudoCompletionValue || ExternalValue
	
	protected MaxQGraph m_maxQGraph;
	protected AgentType m_agentType;
	
	public IQNode_VF(IQNodeInterface qNode, ValueType vType) {
		super(qNode);
		m_vType = vType;
		m_maxQGraph = getMaxQGraph();
		m_agentType = getAgentType();
		m_valueFunction = new HashMap<QNode, TabularVF<State>>();
	}
	
	@Override
	public void setValue(State state, QNode qNode, double value, ValueType vType) {
		if(m_vType.equals(vType)) {
			switch (m_vType) {
			case CompletionValue:
			case PseudoCompletionValue:
				setCValue(state, qNode, value);
				break;
			case ExternalValue:
				setEValue(state, qNode, value);
				break;
			case QValue:
				setQValue(state, qNode, value);
				break;
			default:
				break;
			}
		}
		else {
			m_iQNode.setValue(state, qNode, value, vType);
		}
	}
	
	@Override
	public double getValue(State state, QNode qNode, ValueType vType) {
		if(m_vType.equals(vType)) {
			switch (m_vType) {
			case CompletionValue:
			case PseudoCompletionValue:
				return getCValue(state, qNode);
			case ExternalValue:
				return getEValue(state, qNode);
			case QValue:
				return getQValue(state, qNode);
			default:
				break;
			}
			return Double.NaN;
		}
		else 
			return m_iQNode.getValue(state, qNode, vType);
	}
	
	@Override
	public void clearValues(QNode qNode, ValueType vType, Set<State> states) {
		if(m_vType.equals(vType)) {
			if(m_valueFunction.containsKey(qNode)) {
				if(states==null)
					m_valueFunction.get(qNode).reset();
				else
					m_valueFunction.get(qNode).clear(states);
			}
		}
		else {
			m_iQNode.clearValues(qNode, vType, states);
		}
	}
	
	@Override
	public void printVF(PrintStream ps, boolean real, ValueType vType) {
		if(m_vType.equals(vType)) {
			ps.println("\n;;" + m_vType.toString());
			Set<QNode> qNodeSet = m_valueFunction.keySet();
			for(QNode tempQNode : qNodeSet) {
				ps.println("==> " + tempQNode + ": ");
				m_valueFunction.get(tempQNode).print(ps, real);
			}
		}
		else {
			m_iQNode.printVF(ps, real, vType);
		}
	}
	
	/********************************************
	 * Real stuff
	 */
	
	protected double getCValue(State state, QNode qNode) {
		if(m_maxQGraph.isTerminal(state, qNode.getParentMax())) { 
			// Partial Shielding for state abstraction: completion value for terminal state is 0
			return 0;
		} 
		if(m_agentType.hasStateAbstraction())
			state = m_maxQGraph.qStateAbsMapping(state, qNode);
		if(state.size()==0) { 
			// termination condition for state abstraction
			return 0;
		} 
		if(!m_valueFunction.containsKey(qNode))
			m_valueFunction.put(qNode, new TabularVF<State>(qNode.getNodeDescription()));
		return m_valueFunction.get(qNode).getValue(state);
	}
	
	protected void setCValue(State state, QNode qNode, double value) {
		if(m_maxQGraph.isTerminal(state, qNode.getParentMax())) 
			// Partial Shielding for state abstraction: completion value for terminal state is 0
			return ;
		if(m_agentType.hasStateAbstraction())
			state = m_maxQGraph.qStateAbsMapping(state, qNode);
		if(state.size()==0) { 
			// termination condition for state abstraction
			return ;
		}
		if(!m_valueFunction.containsKey(qNode))
			m_valueFunction.put(qNode, new TabularVF<State>(qNode.getNodeDescription()));
		m_valueFunction.get(qNode).updateValue(state, value);
	}
	
	protected double getEValue(State state, QNode qNode) {
//		if(getParentIMaxNode().isPrimitive())
//			return 0;
//		if(getParentIMaxNode().isRoot())
//			return 0;
//		if(getMaxQGraph().singleExit(qNode.getParentMax()))
//			return 0;
		if(!m_valueFunction.containsKey(qNode))
			m_valueFunction.put(qNode, new TabularVF<State>(qNode.getNodeDescription()));
		return m_valueFunction.get(qNode).getValue(state);
	}
	
	protected void setEValue(State state, QNode qNode, double value) {
//		if(getParentIMaxNode().isPrimitive())
//			return;
//		if(getParentIMaxNode().isRoot())
//			return;
//		if(getMaxQGraph().singleExit(qNode.getParentMax()))
//			return;
		if(!m_valueFunction.containsKey(qNode))
			m_valueFunction.put(qNode, new TabularVF<State>(qNode.getNodeDescription()));
		m_valueFunction.get(qNode).updateValue(state, value);
	}
	
	protected double getQValue(State state, QNode qNode) {
		if(m_maxQGraph.isTerminal(state, qNode.getParentMax()))
			return 0;
		if(!m_valueFunction.containsKey(qNode))
			m_valueFunction.put(qNode, new TabularVF<State>(qNode.getNodeDescription()));
		return m_valueFunction.get(qNode).getValue(state);
	}
	
	protected void setQValue(State state, QNode qNode, double value) {
		if(m_maxQGraph.isTerminal(state, qNode.getParentMax()))
			return ;
		if(!m_valueFunction.containsKey(qNode))
			m_valueFunction.put(qNode, new TabularVF<State>(qNode.getNodeDescription()));
		m_valueFunction.get(qNode).updateValue(state, value);
	}

}
