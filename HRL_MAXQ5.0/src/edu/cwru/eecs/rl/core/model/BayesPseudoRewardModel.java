package edu.cwru.eecs.rl.core.model;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import edu.cwru.eecs.rl.core.distribution.GaussianGammaWindow;
import edu.cwru.eecs.rl.core.mdp.State;



public class BayesPseudoRewardModel implements Serializable {

	private static final long serialVersionUID = 5884143755767380991L;
	
	private HashMap<State, GaussianGammaWindow> m_pseudoRewardDistribution;
	private HashMap<State, Double> m_currentSample;
	
	public BayesPseudoRewardModel() {
		m_pseudoRewardDistribution = new HashMap<State, GaussianGammaWindow>();
		m_currentSample = new HashMap<State, Double>();
	}
	
	public void update(State state, double value) {
		if(!m_pseudoRewardDistribution.containsKey(state))
			m_pseudoRewardDistribution.put(state, new GaussianGammaWindow());
		m_pseudoRewardDistribution.get(state).updatePosterior(value);
		// TODO: this should not be used when resampling is called
		m_currentSample.put(state, value);
	}
	
	public void resampling() {
		m_currentSample.clear();
	}
	
	/**
	 * Get sample mean pseudo reward of current state.
	 * They are sampled when prior is updated and then store in the object.
	 * @param state
	 * @return sample mean pseudo reward of current state
	 */
	public double getSampleMean(State state) {
		if(!m_currentSample.containsKey(state)) {
			if(!m_pseudoRewardDistribution.containsKey(state))
				m_pseudoRewardDistribution.put(state, new GaussianGammaWindow());
			m_currentSample.put(state, m_pseudoRewardDistribution.get(state).sampleGaussianMean());
		} 
		return m_currentSample.get(state);
		//return m_pseudoRewardDistribution.get(state).sampleGaussianMean();
	}
	
	public void print(PrintStream ps, boolean real) {
		ps.println("~~There are " + m_pseudoRewardDistribution.size() + " entries!");
		if(real) {
			Set<State> keySet = m_pseudoRewardDistribution.keySet();
			for(State state : keySet) {
				double temp = m_pseudoRewardDistribution.get(state).getMAPMean();
				//if(temp!=0.0)
					ps.println(state.toString() + ": " + temp);
			}
		}
		ps.println("~~End");
	}
	
}
