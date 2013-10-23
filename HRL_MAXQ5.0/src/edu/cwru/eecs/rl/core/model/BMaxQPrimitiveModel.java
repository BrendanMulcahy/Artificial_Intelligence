package edu.cwru.eecs.rl.core.model;

import java.io.Serializable;


import com.google.common.collect.BiMap;

import edu.cwru.eecs.rl.core.distribution.GaussianGamma;
import edu.cwru.eecs.rl.core.distribution.SparseDirichlet;
import edu.cwru.eecs.rl.core.mdp.State;



/**
 * BayesPrimitiveModel for a real primitive max node (model for an action).
 * 
 * Note: here use GaussianGamma for prior of reward model distribution
 * Since in current version of algorithm, we only use MAP of the bayesian prior on model,
 * so we don't actually need a model class. Instead, in this class, we provide methods
 * to sample from MAP of transition function and get mean of reward function.
 * 
 * @author Feng
 * 
 */
public class BMaxQPrimitiveModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private GaussianGamma[] m_rewardModelDistribution;
	private SparseDirichlet[] m_transitionModelDistribution;
	
	private BiMap<State, Integer> m_allStateSet;
	private int m_numState;
	
	/**
	 * Creator
	 * @param allStateSet
	 */
	public BMaxQPrimitiveModel(BiMap<State, Integer> allStateSet) {
		m_allStateSet = allStateSet;
		m_numState = m_allStateSet.size();
		
		m_rewardModelDistribution = new GaussianGamma[m_numState];
		m_transitionModelDistribution = new SparseDirichlet[m_numState];
		
		for(int i=0; i<m_numState; i++) {
			m_rewardModelDistribution[i] = new GaussianGamma();
			m_transitionModelDistribution[i] = new SparseDirichlet(m_numState);
		}
	}
	
	/**********************************************************************/
	/** The following four methods probably don't matter a lot here. */
	
	/** 
	 * get MAP of transition model distribution, which is the multinomial dist 
	 * with same parameters as the dirichlet dist itself.
	 * @param state
	 * @return MAP of transition model distribution
	 */
	public SparseDirichlet getMAPTransitionProb(State state) {
		return m_transitionModelDistribution[m_allStateSet.get(state)];
	}
	
	/**
	 * get MAP of transition model distribution, which is the multinomial dist 
	 * with same parameters as the dirichlet dist itself.
	 * @param s
	 * @return MAP of transition model distribution.
	 */
	public SparseDirichlet getMAPTransitionProb(int s) {
		return m_transitionModelDistribution[s];
	}

	/**
	 * get MAP of the reward mean
	 * @param state
	 * @return MAP of the mean reward for given state.
	 */
	public double getMAPRewardMean(State state) {
		return m_rewardModelDistribution[m_allStateSet.get(state)].getMAPMean();
	}
	
	public double getMAPRewardMean(int s) {
		return m_rewardModelDistribution[s].getMAPMean();
	}
	
	
	/**********************************************************************/
	/** the following four methods are used for sampling from MAP of the bayesian prior of model */
	
	/**
	 * sample 
	 */
	public int sampleNextState(int s) {
		// directly sample from the MAP of the prior
		return m_transitionModelDistribution[s].sample();  
	}
	
	public State sampleNextState(State state) {
		return m_allStateSet.inverse().get(m_transitionModelDistribution[m_allStateSet.get(state)].sample());
	}
	
	public double getMeanReward(int s) {
		return m_rewardModelDistribution[s].getMAPMean();
	}

	public double getMeanReward(State state) {
		return m_rewardModelDistribution[m_allStateSet.get(state)].getMAPMean();
	}


	/**********************************************************************/
	/** methods for updating */
	
	public void update(State state, double reward, State nextState) {
		int index = m_allStateSet.get(state);
		int nextIndex = m_allStateSet.get(nextState);
		m_transitionModelDistribution[index].updatePosterior(nextIndex, 1);
		m_rewardModelDistribution[index].updatePosterior(reward);
	}

	public void update(int s, double reward, int nextS) {
		m_transitionModelDistribution[s].updatePosterior(nextS, 1);
		m_rewardModelDistribution[s].updatePosterior(reward);
	}
}
