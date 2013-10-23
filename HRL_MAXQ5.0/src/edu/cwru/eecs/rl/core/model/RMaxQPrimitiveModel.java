package edu.cwru.eecs.rl.core.model;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashMap;

import edu.cwru.eecs.futil.Pair;
import edu.cwru.eecs.rl.core.mdp.State;

/**
 * PrimitiveModel: model for a primitive action 
 * 
 * Non-bayesian primitive model: for R-max + MAXQ algorithm
 * Refer to: Hierarchical Model-Based Reinforcement Learning: R-max + MAXQ
 * by Nicholas K. Jong and Peter Stone (ICML 2008) 
 * @author Feng
 *
 */
public class RMaxQPrimitiveModel implements Serializable{

	private static final long serialVersionUID = 6656788397066526436L;
	
	private static double DEFAULT_REWARD = 10;
	private static int M = 10;  // heuristic value, set to 5 in RMaxQ paper
	//private static double DEFAULT_VALUE = 0;
	
	// transition and reward function.
	private HashMap<State, Double> m_r;
	private HashMap<State, Integer> m_nsa;
	private HashMap<Pair<State, State>, Integer> m_nsas;
	
	public RMaxQPrimitiveModel() {
		m_r = new HashMap<State, Double>();
		m_nsa  = new HashMap<State, Integer>();
		m_nsas = new HashMap<Pair<State,State>, Integer>();
	}
	
	public void updateModel(State state, double reward, State nextState) {
		updateReward(state, reward);
		updateTransition(state, nextState);
	}
	
	public double getReward(State state) {
		if(!m_r.containsKey(state) || m_nsa.get(state)<M) 
			return DEFAULT_REWARD;
		//if(m_r.get(state)*1.0/m_nsa.get(state)!=DEFAULT_REWARD)
		//	System.err.println("fuck reward!!");
		return m_r.get(state)*1.0/m_nsa.get(state);
	}
	
	public double getTransition(State state, State nextState) {
		Pair<State, State> sns = new Pair<State, State>(state, nextState);
		if(!m_nsa.containsKey(state) || m_nsa.get(state)<M)
			return 0;
		if(!m_nsas.containsKey(sns))
			return 0;
		
		//if(m_nsas.get(sns)*1.0/m_nsa.get(state)>0)
		//	System.err.println("fuck transition!!");
		return m_nsas.get(sns)*1.0/m_nsa.get(state);
	}
	
	private void updateReward(State state, double r) {
		if(!m_r.containsKey(state)) 
			m_r.put(state, 0.0);
		m_r.put(state, m_r.get(state)+r);
	}
	
	private void updateTransition(State state, State nextState) {
		Pair<State, State> sns = new Pair<State, State>(state, nextState);
		if(!m_nsa.containsKey(state))
			m_nsa.put(state, 0);
		if(!m_nsas.containsKey(sns))
			m_nsas.put(sns, 0);
		m_nsa.put(state, m_nsa.get(state)+1);
		m_nsas.put(sns, m_nsas.get(sns)+1);
	}
	
	public void printModel(PrintStream ps, boolean real) {
		// print transition
		ps.print("==> Transition: number of entries: " + m_nsas.size() + "\n");
		for(Pair<State, State> sns : m_nsas.keySet()) {
			double prob = m_nsas.get(sns)*1.0/m_nsa.get(sns.left());
			if(prob>0)
				ps.print(sns + " Prob: " + prob + "\n");
		}
		// print reward
		ps.print("==> Reward: number of entries: " + m_r.size() + "\n");
		for(State s : m_r.keySet()) {
			double reward = m_r.get(s)*1.0/m_nsa.get(s);
			if(reward!=DEFAULT_REWARD)
				ps.print(s + " Reward: " + reward + "\n");
		}
	}
}
























