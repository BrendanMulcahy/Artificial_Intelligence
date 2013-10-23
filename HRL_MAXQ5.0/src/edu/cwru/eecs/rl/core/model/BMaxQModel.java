package edu.cwru.eecs.rl.core.model;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.BiMap;

import edu.cwru.eecs.rl.core.maxnode.IMaxNodeInterface;
import edu.cwru.eecs.rl.core.maxnode.MaxNode;
import edu.cwru.eecs.rl.core.mdp.State;

public class BMaxQModel implements Model {

	private static final long serialVersionUID = 1601107393758944675L;
	
	private IMaxNodeInterface m_task;
	private BiMap<State, Integer> m_allStateSet;
	private boolean m_isPrimitive;
	
	private Map<MaxNode, BMaxQPrimitiveModel> m_primitiveModels;

	public BMaxQModel(IMaxNodeInterface task) {
		m_task = task;
		m_isPrimitive = m_task.isPrimitive();
		m_allStateSet = m_task.getMaxQGraph().getEnvSpec().getAllStateSet();
		m_primitiveModels = new HashMap<MaxNode, BMaxQPrimitiveModel>();
	}
	
	
	@Override
	public void updateModel(State state, MaxNode maxNode, double reward,
			State nextState) {
		if(!checkValidation(maxNode))
			return ;
		m_primitiveModels.get(maxNode).update(state, reward, nextState);
	}

	@Override
	public double getReward(State state, MaxNode maxNode) {
		if(!checkValidation(maxNode))
			return Double.NaN;
		return m_primitiveModels.get(maxNode).getMeanReward(state);
	}

	@Override
	public double getTransition(State state, MaxNode maxNode, State nextState) {
		System.out.println("No supported: getTransition() in BMaxQModel");
		return Double.NaN;
	}

	@Override
	public State getNextState(State state, MaxNode maxNode) {
		if(!checkValidation(maxNode))
			return null;
		return m_primitiveModels.get(maxNode).sampleNextState(state);
	}

	@Override
	public void setReward(State state, MaxNode maxNode, double reward) {
		System.out.println("No supported: setReward() in BMaxQModel");
	}

	@Override
	public void setTransition(State state, MaxNode maxNode, State nextState,
			double prob) {
		System.out.println("No supported: setTransition() in BMaxQModel");
	}

	@Override
	public void clearRewards(MaxNode maxNode, Set<State> states) {
		System.out.println("No supported: clearRewards() in BMaxQModel");
	}

	@Override
	public void clearTransitions(MaxNode maxNode, Set<State> states) {
		System.out.println("No supported: clearTransitions() in BMaxQModel");
	}
	
	@Override
	public void printModel(PrintStream ps, boolean real) {
		// TODO
	}
	
	
	private boolean checkValidation(MaxNode maxNode) {
		if(m_isPrimitive && maxNode.isPrimitive()) {
			if(!m_primitiveModels.containsKey(maxNode)) 
				m_primitiveModels.put(maxNode, new BMaxQPrimitiveModel(m_allStateSet));
			return true;
		} 
		return false;
	}


	@Override
	public void setPrior(Model model) {
		if(model instanceof BMaxQModel) {
			m_primitiveModels = ((BMaxQModel)model).m_primitiveModels;
		}
		else {
			System.out.println("Wrong model in setPrior(): " + model.getClass().getName());
		}
	}

}
