package edu.cwru.eecs.rl.core.maxnode;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Set;

import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.vf.TabularVF;

public class IMaxNode_VF extends IMaxNodeDecorator {

	private static final long serialVersionUID = -5047887191300789701L;

	protected HashMap<MaxNode, TabularVF<State>> m_valueFunction;
	
	protected ValueType m_vType;
	
	public IMaxNode_VF(IMaxNodeInterface iMaxNode, ValueType vType) {
		super(iMaxNode);
		m_vType = vType;
		m_valueFunction = new HashMap<MaxNode, TabularVF<State>>();
	}

	@Override
	public double getValue(State state, MaxNode maxNode, ValueType vType) {
		if(m_vType.equals(vType)) {
			switch (m_vType) {
			case StateValue:
				return getSValue(state, maxNode);
			default:
				break;
			}
			return Double.NaN;
		}
		else
			return m_iMaxNode.getValue(state, maxNode, vType);
	}

	@Override
	public void setValue(State state, MaxNode maxNode, double value,
			ValueType vType) {
		if(m_vType.equals(vType))
			switch (vType) {
			case StateValue:
				setSValue(state, maxNode, value);
				break;
			default:
				break;
			}
		else
			m_iMaxNode.setValue(state, maxNode, value, vType);
	}


	@Override
	public void clearValues(MaxNode maxNode, ValueType vType, Set<State> states) {
		if(m_vType.equals(vType)) {
			if(m_valueFunction.containsKey(maxNode)) {
				if(states==null)
					m_valueFunction.get(maxNode).reset();
				else 
					m_valueFunction.get(maxNode).clear(states);
			}
		}
		else
			m_iMaxNode.clearValues(maxNode, vType, states);
	}

	@Override
	public void printVF(PrintStream ps, boolean real, ValueType vType) {
		if(m_vType.equals(vType)) {
			ps.println("\n;;" + m_vType.toString());
			Set<MaxNode> maxNodeSet = m_valueFunction.keySet();
			for(MaxNode tempMaxNode : maxNodeSet) {
				ps.println("==> " + tempMaxNode + ": ");
				m_valueFunction.get(tempMaxNode).print(ps, real);
			}
		}
		else
			m_iMaxNode.printVF(ps, real, vType);
	}
	
	
	/********************************************
	 * Real stuff
	 */
	
	private double getSValue(State state, MaxNode maxNode) {
		if(m_iMaxNode.getMaxQGraph().isTerminal(state, maxNode))
			return 0;
		if(!m_valueFunction.containsKey(maxNode))
			m_valueFunction.put(maxNode, new TabularVF<State>(maxNode.getNodeDescription()));
		return m_valueFunction.get(maxNode).getValue(state);
	}
	
	private void setSValue(State state, MaxNode maxNode, double value) {
		if(m_iMaxNode.getMaxQGraph().isTerminal(state, maxNode))
			return ;
		if(!m_valueFunction.containsKey(maxNode))
			m_valueFunction.put(maxNode, new TabularVF<State>(maxNode.getNodeDescription()));
		m_valueFunction.get(maxNode).updateValue(state, value);
	}
	
}
