package edu.cwru.eecs.rl.core.model;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.cwru.eecs.rl.core.maxnode.IMaxNodeInterface;
import edu.cwru.eecs.rl.core.maxnode.MaxNode;
import edu.cwru.eecs.rl.core.mdp.State;

/**
 * Non-bayesian primitive model: for R-max + MAXQ algorithm
 * Refer to: Hierarchical Model-Based Reinforcement Learning: R-max + MAXQ
 * by Nicholas K. Jong and Peter Stone (ICML 2008) 
 * @author Feng
 *
 */

/**
 * RMaxQModel defines the model associated with an internal max node.
 * (May correspond to multiple real subtasks.)
 */
public class RMaxQModel implements Model {

	private static final long serialVersionUID = 2222388556156191975L;
	
	private IMaxNodeInterface m_task;
	private boolean m_isPrimitive;
	
	private Map<MaxNode, RMaxQPrimitiveModel> m_primitiveModels;
	private Map<MaxNode, RMaxQCompositeModel> m_compositeModels;
	
	public RMaxQModel(IMaxNodeInterface task) {
		m_task = task;
		m_isPrimitive = m_task.isPrimitive();
		if(m_isPrimitive)
			m_primitiveModels = new HashMap<MaxNode, RMaxQPrimitiveModel>();
		else {
			m_compositeModels = new HashMap<MaxNode, RMaxQCompositeModel>();
		}
	}
	
	@Override
	public void updateModel(State state, MaxNode maxNode, double reward,
			State nextState) {
		if(!checkValidation(maxNode))
			return ;
		if(m_isPrimitive) {
			m_primitiveModels.get(maxNode).updateModel(state, reward, nextState);
		} else {
			System.out.println("Sholdn't call updateModel() for composite task: " + maxNode.nameString());
		}
	}

	@Override
	public double getReward(State state, MaxNode maxNode) {
		if(!checkValidation(maxNode))
			return Double.NaN;
		if(m_isPrimitive) {
			return m_primitiveModels.get(maxNode).getReward(state);
		} else {
			if(m_task.getMaxQGraph().isTerminal(state, maxNode))
				return 0;
			return m_compositeModels.get(maxNode).getReward(state);
		}
	}

	@Override
	public double getTransition(State state, MaxNode maxNode, State nextState) {
		if(!checkValidation(maxNode))
			return Double.NaN;
		if(m_isPrimitive) {
			return m_primitiveModels.get(maxNode).getTransition(state, nextState);
		} else {
			if(m_task.getMaxQGraph().isTerminal(state, maxNode) 
					|| !m_task.getMaxQGraph().isTerminal(nextState, maxNode))
				return 0;
			return m_compositeModels.get(maxNode).getTransition(state, nextState);
		}
	}

	@Override
	public State getNextState(State state, MaxNode maxNode) {
		System.out.println("No supported: getNextState() in RMaxQModel");
		return null;
	}

	@Override
	public void setReward(State state, MaxNode maxNode, double reward) {
		if(!checkValidation(maxNode))
			return ;
		if(!m_isPrimitive) {
			if(m_task.getMaxQGraph().isTerminal(state, maxNode))
				return ;
			m_compositeModels.get(maxNode).setReward(state, reward);
		} else {
			System.out.println("Shouldn't call setReward() for primitive model: RMaxQModel");;
		}
	}

	@Override
	public void setTransition(State state, MaxNode maxNode, State nextState,
			double prob) {
		if(!checkValidation(maxNode))
			return ;
		if(!m_isPrimitive) {
			if(m_task.getMaxQGraph().isTerminal(state, maxNode) 
					|| !m_task.getMaxQGraph().isTerminal(nextState, maxNode))
				return ;
			m_compositeModels.get(maxNode).setTransitio(state, nextState, prob);
		} else {
			System.out.println("Shouldn't call setTransition() for primitive model: RMaxQModel");;
		}
	}

	@Override
	public void clearRewards(MaxNode maxNode, Set<State> states) {
		if(!checkValidation(maxNode))
			return ;
		if(!m_isPrimitive) {
			m_compositeModels.get(maxNode).clearReward(states);
		} else {
			System.out.println("Shouldn't call clearReward() for primitive model: RMaxQModel");;
		}
	}

	@Override
	public void clearTransitions(MaxNode maxNode, Set<State> states) {
		if(!checkValidation(maxNode))
			return ;
		if(!m_isPrimitive) {
			m_compositeModels.get(maxNode).clearTransition(states);
		} else {
			System.out.println("Shouldn't call clearTransition() for primitive model: RMaxQModel");;
		}
	}
	
	@Override
	public void printModel(PrintStream ps, boolean real) {
		if(m_isPrimitive) {
			for(MaxNode maxNode : m_primitiveModels.keySet()) {
				ps.print(maxNode.nameString() + "\n");
				m_primitiveModels.get(maxNode).printModel(ps, real);
			}
		} else {
			for(MaxNode maxNode : m_compositeModels.keySet()) {
				ps.print(maxNode.nameString() + "\n");
				m_compositeModels.get(maxNode).printModel(ps, real);
			}
		}
	}

	
	
	private boolean checkValidation(MaxNode maxNode) {
		if(m_isPrimitive && maxNode.isPrimitive()) {
			if(!m_primitiveModels.containsKey(maxNode)) 
				m_primitiveModels.put(maxNode, new RMaxQPrimitiveModel());
			return true;
		} else if(!m_isPrimitive && !maxNode.isPrimitive()) {
			if(!m_compositeModels.containsKey(maxNode))
				m_compositeModels.put(maxNode, new RMaxQCompositeModel());
			return true;
		}
		return false;
	}

	@Override
	public void setPrior(Model model) {
		System.out.println("No supported setPrior(): " + "RMaxQModel");
	}
	
}


















