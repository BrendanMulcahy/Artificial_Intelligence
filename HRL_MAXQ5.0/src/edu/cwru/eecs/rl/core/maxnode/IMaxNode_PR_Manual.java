package edu.cwru.eecs.rl.core.maxnode;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Set;

import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.vf.TabularVF;


public class IMaxNode_PR_Manual extends IMaxNodeDecorator {

	private static final long serialVersionUID = -6705635110222190410L;
	
	private static final double PSEUDO_REWARD_GOAL = 0;
	private static final double PSEUDO_REWARD_NON_GOAL = -1000;
	
	private ValueType m_vType = ValueType.ManualPseudoReward;
	
	// only for print purpose
	protected HashMap<MaxNode, TabularVF<State>> m_pseudoRewardFunction;

	public IMaxNode_PR_Manual(IMaxNodeInterface iMaxNode) {
		super(iMaxNode);
		m_pseudoRewardFunction = new HashMap<MaxNode, TabularVF<State>>();
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
	
	protected double getPseudoReward(State state, MaxNode maxNode) {
		if(maxNode.getIMaxNode().getMaxQGraph().isGoal(state, maxNode)) {
			if(!m_pseudoRewardFunction.containsKey(maxNode)) {
				m_pseudoRewardFunction.put(maxNode, new TabularVF<State>(maxNode.nameString()));
			}
			m_pseudoRewardFunction.get(maxNode).updateValue(state, PSEUDO_REWARD_GOAL);
			return PSEUDO_REWARD_GOAL;
		}
		else if(maxNode.getIMaxNode().getMaxQGraph().isNonGoalTerminal(state, maxNode)) {
			if(!m_pseudoRewardFunction.containsKey(maxNode)) {
				m_pseudoRewardFunction.put(maxNode, new TabularVF<State>(maxNode.nameString()));
			}
			m_pseudoRewardFunction.get(maxNode).updateValue(state, PSEUDO_REWARD_NON_GOAL);
			return PSEUDO_REWARD_NON_GOAL;
		}
		return 0;
	}

	protected void setPseudoReward(State state, MaxNode maxNode, double value) {
		System.err.println("This method doesn't do anything here.");
	}
	
	protected void printPR(PrintStream ps, boolean real) {
		ps.println("\n;;Manually Set Pseudo Reward!!");
		Set<MaxNode> keySet = m_pseudoRewardFunction.keySet();
		for(MaxNode key : keySet) {
			ps.println("==> " + key.getNodeDescription() + ": ");
			m_pseudoRewardFunction.get(key).print(ps, real);
		}
	}

}
