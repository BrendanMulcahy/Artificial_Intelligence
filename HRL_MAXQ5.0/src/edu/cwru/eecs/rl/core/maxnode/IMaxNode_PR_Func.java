package edu.cwru.eecs.rl.core.maxnode;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.vf.TabularVF;

public class IMaxNode_PR_Func extends IMaxNodeDecorator {
private static final long serialVersionUID = -238602379816301887L;
	
	protected HashMap<CallingStack, TabularVF<State>> m_pseudoRewardFunction;
	
	private double DEFAULT_PR = -1000;
	
	private ValueType m_vType = ValueType.TabularPseudoReward;
	
	public IMaxNode_PR_Func(IMaxNodeInterface iMaxNode) {
		super(iMaxNode);
		m_pseudoRewardFunction = new HashMap<CallingStack, TabularVF<State>>();
	}
	
	@Override
	public double getValue(State state, MaxNode maxNode, ValueType vType) {
		if(vType.equals(m_vType))
			return getPseudoReward(state, maxNode);
		else {
			return m_iMaxNode.getValue(state, maxNode, vType);
		}
	}

	@Override
	public void setValue(State state, MaxNode maxNode, double value,
			ValueType vType) {
		if(vType.equals(m_vType))
			setPseudoReward(state, maxNode, value);
		else {
			m_iMaxNode.setValue(state, maxNode, value, vType);
		}
	}

	@Override
	public void printVF(PrintStream ps, boolean real, ValueType vType) {
		if(vType.equals(m_vType))
			printPR(ps, real);
		else {
			m_iMaxNode.printVF(ps, real, vType);
		}
	}
	
	public double getPseudoReward(State state, MaxNode maxNode) {
		if(m_iMaxNode.isPrimitive())
			return 0;
		if(maxNode.getIMaxNode().isRoot())
			return 0;
		if(!getMaxQGraph().isTerminal(state, maxNode))
			return 0;
		if(getMaxQGraph().singleExit(maxNode))
			return 0;
		CallingStack key = new CallingStack(maxNode);
		if(!m_pseudoRewardFunction.containsKey(key)) {
			m_pseudoRewardFunction.put(key, new TabularVF<State>(DEFAULT_PR, maxNode.nameString()));
		}
		state = getMaxQGraph().maxStateAbsMapping(state, maxNode);
		return m_pseudoRewardFunction.get(key).getValue(state);
	}

	public void setPseudoReward(State state, MaxNode maxNode, double value) {
		if(m_iMaxNode.isPrimitive())
			return ;
		if(maxNode.getIMaxNode().isRoot())
			return ;
		if(!getMaxQGraph().isTerminal(state, maxNode))
			return ;
		if(getMaxQGraph().singleExit(maxNode))
			return ;
		CallingStack key = new CallingStack(maxNode);
		if(!m_pseudoRewardFunction.containsKey(key)) {
			m_pseudoRewardFunction.put(key, new TabularVF<State>(DEFAULT_PR, maxNode.nameString()));
		}
		m_pseudoRewardFunction.get(key).updateValue(state, value);
	}
	
	public void printPR(PrintStream ps, boolean real) {
		ps.println(";;Pseudo Reward Function!!");
		Set<CallingStack> keySet = m_pseudoRewardFunction.keySet();
		for(CallingStack key : keySet) {
			ps.println("==> " + key.getCallingStackDescription() + ": ");
			m_pseudoRewardFunction.get(key).print(ps, real);
		}
	}
	
	class CallingStack implements Serializable {
		
		private static final long serialVersionUID = -2742490456406291031L;
		
		private MaxNode m_maxNode;
		private int m_d; // m_d=1 correspond to current node
		private MaxNode[] m_maxNodeStack;
		
		public CallingStack(MaxNode maxNode) {
			this(maxNode, 0);
		}
		public CallingStack(MaxNode maxNode, int d) {
			m_maxNode = maxNode;
			if(d<=0) {
				d=1;
				MaxNode temp = maxNode;
				while(!temp.getIMaxNode().isRoot()) {
					temp = temp.getParentQ().getParentMax();
					d++;
				}
			}
			m_d = d;
			m_maxNodeStack = new MaxNode[d];
			int i=0;
			while(!maxNode.getIMaxNode().isRoot()) {
				m_maxNodeStack[i] = maxNode;
				maxNode = maxNode.getParentQ().getParentMax();
				i++;
			}
			m_maxNodeStack[i] = maxNode;
		}
		
		public String getCallingStackDescription() {
			StringBuffer sb = new StringBuffer();
			sb.append("d: " + m_d);
			for(int i=0; i<m_d; i++) {
				sb.append(" " + i + ": " + m_maxNodeStack[i].getNodeDescription());
			}
			return sb.toString();
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + m_d;
			result = prime * result
					+ ((m_maxNode == null) ? 0 : m_maxNode.hashCode());
			result = prime * result + Arrays.hashCode(m_maxNodeStack);
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
			CallingStack other = (CallingStack) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (m_d != other.m_d)
				return false;
			if (m_maxNode == null) {
				if (other.m_maxNode != null)
					return false;
			} else if (!m_maxNode.equals(other.m_maxNode))
				return false;
			if (!Arrays.equals(m_maxNodeStack, other.m_maxNodeStack))
				return false;
			return true;
		}
		private IMaxNode_PR_Func getOuterType() {
			return IMaxNode_PR_Func.this;
		}
	}
}
