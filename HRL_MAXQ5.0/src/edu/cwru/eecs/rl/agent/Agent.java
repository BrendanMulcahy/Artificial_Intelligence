package edu.cwru.eecs.rl.agent;

import java.io.Serializable;

import edu.cwru.eecs.futil.Pair;
import edu.cwru.eecs.rl.core.mdp.Action;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.env.EnvSpec;
import edu.cwru.eecs.rl.env.Environment;

public abstract class Agent implements Serializable {
	
	private static final long serialVersionUID = 8629914342508673524L;
	
	public static final int MODE_LEARNING = 0;
	public static final int MODE_TESTING = 1;
	
	
	protected AgentType m_agentType;
	
	/** environment */
	protected Environment m_environment;
	protected EnvSpec m_envSpec;
	
	/** statistics for running episode */
	protected double m_cumulativeReward;
	protected int m_step;
	
	protected int m_maxStep;
	
	protected int m_episode;
	
	/** learning parameters */
	protected double m_epsilonMin;
	protected double m_maxEpsilonDecayCount;
	protected double m_epsilon;
	protected double m_epsilonDecayRate;
	
	protected double m_alpha;
	protected double m_gamma;

	
	/** debug */
	protected boolean m_debug = false;
	
	protected Agent(Environment environment, AgentType agentType) {
		m_agentType = agentType;
		
		m_environment = environment;
		m_envSpec = m_environment.getEnvSpec();
		m_gamma = environment.getEnvSpec().getGamma();
		
		m_alpha = 0.25;
		m_episode = 0;
	}
	
	public void setPrior(Agent agent) {
		System.out.println("No implemented: " + "setPrior");
	}
	
	public static String getUsage() {
		return "Unknown, it was not defined";
	}
	
	protected void setEpsilon(double epsilon) {
		m_epsilon = epsilon;
	}
	
	protected void epsilonDecay() {
		if(m_epsilon==m_epsilonMin)
			return ;
		if(m_epsilon<m_epsilonMin)
			m_epsilon = m_epsilonMin;
		else {
			m_epsilon *= m_epsilonDecayRate;
		}
	}
	
	public AgentType agentType() {
		return m_agentType;
	}
	
//	public AgentType.Algorithm algorithm() {
//		return m_agentType.algorithm();
//	}
	
	public String name() {
		return m_agentType.toString();
	}
	
	public String info() {
		return "";
	}
	
	public double getCumulativeReward() {
		return m_cumulativeReward;
	}
	
	public int getStep() {
		return m_step;
	}
	
	/**
	 * @param message
	 * @return true if can handle the message, else return false
	 */
	public String message(String message) {
		String[] messages = message.split(",");
		
		if(messages[0].trim().equals("debug")) {
			if(messages[1].trim().equals("on")) {
				m_debug = true;
				return "set debug on";
			} else if(messages[1].trim().equals("off")) {
				m_debug = false;
				return "set debug off";
			}
		}
		return null;
	}
	
	
	public int episode(int mode, int maxStep) {
		m_environment.start();
		m_cumulativeReward = 0;
		m_step = 0;
		
		m_maxStep=maxStep;
		if(maxStep==0)
			m_maxStep = Integer.MAX_VALUE;
		
		run(mode);
		
		return m_step;	
	}
	
	/**
	 * Run an episode according to mode (LEARNING or TESTING)
	 * Set the value of m_step and m_cumulativeReward
	 * @param mode
	 */
	protected abstract void run(int mode);
	
	
	public static int eGreedySelection(double[] values, double epsilon) {
		int length = values.length;
		// calculate max and numMax
		double max = values[0];
		int numMax = 1;
		for(int i=1; i<length; i++)
		{
			if(values[i]==max)
				numMax++;
			else if(values[i]>max)
			{
				max = values[i];
				numMax = 1;
			}
		}	
		// calculate probs
		double[] probs = new double[length];
		for(int i=0; i<length; i++)
		{
			probs[i] = epsilon/length;
			if(values[i]==max)
				probs[i] += (1-epsilon)/numMax;
		}
		// choose action based on probs
		double random = Math.random();
		for(int i=0; i<length; i++)
		{
			random -= probs[i];
			if(random<0)
				return i;
		}
		return length-1;
	}
	
	protected int getActionIndex(Action action) {
		return m_envSpec.getActionIndex(action);
	}
	
	protected Action getAction(int actionIndex) {
		return m_envSpec.getAction(actionIndex);
	}
	
	protected String getActionName(int actionIndex) {
		return m_environment.actionName(actionIndex);
	}
	
	protected Pair<State, Action> getStateAction(State state, Action action) {
		return new Pair<State, Action> (state, action);
	}
}







