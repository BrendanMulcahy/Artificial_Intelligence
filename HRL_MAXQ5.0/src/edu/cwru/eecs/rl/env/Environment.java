package edu.cwru.eecs.rl.env;

import java.io.Serializable;


import com.google.common.collect.HashBiMap;

import edu.cwru.eecs.rl.core.mdp.Action;
import edu.cwru.eecs.rl.core.mdp.State;


public abstract class Environment implements Serializable{
	
	private static final long serialVersionUID = 614001809452276886L;
	
	/** to be specified in subclass */
	protected static int STATE_DIMENSION;
	protected static int ACTION_NUMBER;
	protected static String[] ACTION_NAME; 
	
	/** environment specification */
	protected EnvSpec m_envSpec;
	
	/** game status */
	protected double m_reward;
	protected boolean m_isTerminal;
	
	protected boolean m_debug = false;
	
	protected Environment(String name, double gamma) {
		m_envSpec = new EnvSpec(this);
		m_envSpec.setName(name);
		m_envSpec.setGamma(gamma);
		m_envSpec.setNumStateVariable(STATE_DIMENSION);
		m_envSpec.setNumAction(ACTION_NUMBER);
		
		m_isTerminal = true;
	}
	
	public String name() {
		return m_envSpec.getName();
	}
	
	public String getDescription() {
		return name();
	}
	
	public double getReward() {
		return m_reward;
	}
	public boolean isTerminal() {
		return m_isTerminal;
	}
	public EnvSpec getEnvSpec() {
		return m_envSpec;
	}
	public boolean debug() {
		return m_debug;
	}
	public String actionName(int actionIndex) {
		if(actionIndex<0 || actionIndex>=ACTION_NUMBER)
			throw new ArrayIndexOutOfBoundsException("Action Index out of bound: " + actionIndex);
		return ACTION_NAME[actionIndex];
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
	
	public static String getUsage() {
		return "Unknown, it was not defined";
	}
	
	public abstract void start();
	public abstract boolean takeAction(Action action);
	
	public abstract State getState();
	
	public abstract HashBiMap<State, Integer> getStartingStateSet();
	public abstract HashBiMap<State, Integer> getAllStateSet();
	public abstract HashBiMap<Action, Integer> getAllActionSet();
	//public abstract HashBiMap<Parameter, Integer> getActionParameterList(int actionName);
	
}






