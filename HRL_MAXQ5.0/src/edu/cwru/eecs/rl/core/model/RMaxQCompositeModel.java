package edu.cwru.eecs.rl.core.model;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.cwru.eecs.futil.Pair;
import edu.cwru.eecs.rl.core.mdp.State;

/**
 * CompositeModel: model for a composite action 
 * 
 * Non-bayesian primitive model: for R-max + MAXQ algorithm
 * Refer to: Hierarchical Model-Based Reinforcement Learning: R-max + MAXQ
 * by Nicholas K. Jong and Peter Stone (ICML 2008) 
 * @author Feng
 *
 */
public class RMaxQCompositeModel implements Serializable {

	private static final long serialVersionUID = 8061791497528585432L;
	
	private static double DEFAULT_REWARD = 1;
	
	// transition and reward function
	private Map<State, Double> m_reward;
	private Map<Pair<State, State>, Double> m_transition;
	
	public RMaxQCompositeModel() {
		m_reward = new HashMap<State, Double>();
		m_transition = new HashMap<Pair<State,State>, Double>();
	}
	
	public void setReward(State state, double reward) {
		m_reward.put(state, reward);
	}
	
	public void setTransitio(State state, State nextState, double prob) {
		m_transition.put(new Pair<State, State>(state, nextState), prob);
	}
	
	public double getReward(State state) {
		if(!m_reward.containsKey(state)) 
			m_reward.put(state, DEFAULT_REWARD);
		return m_reward.get(state);
	}
	
	public double getTransition(State state, State nextState) {
		Pair<State, State> sns = new Pair<State, State>(state, nextState);	
		if(!m_transition.containsKey(sns))
			m_transition.put(sns, 0.0);
		return m_transition.get(sns);
	}
	
	public void clearReward(Set<State> states) {
		if(states==null)
			m_reward.clear();
		else {
			for(State state : states) {
				m_reward.remove(state);
			}
		}
	}
	
	public void clearTransition(Set<State> states) {
		if(states==null)
			m_transition.clear();
		else {
			Set<Pair<State, State>> toBeRemoved = new HashSet<Pair<State,State>>();
			for(Pair<State, State> sns : m_transition.keySet()) {
				if(states.contains(sns.left()))
					toBeRemoved.add(sns);
			}
			for(Pair<State, State> sns : toBeRemoved) 
			m_transition.remove(sns);
		}
	}
	
	public void printModel(PrintStream ps, boolean real) {
		// print transition
		ps.print("==> Transition: number of entries: " + m_transition.size() + "\n");
		for(Pair<State, State> sns : m_transition.keySet()) {
			if(m_transition.get(sns)>0)
				ps.print(sns + " Prob: " + m_transition.get(sns) + "\n");
		}
		// print reward
		ps.print("==> Reward: number of entries: " + m_reward.size() + "\n");
		for(State s : m_reward.keySet()) {
			ps.print(s + " Reward: " + m_reward.get(s) + "\n");
		}
	}
}












