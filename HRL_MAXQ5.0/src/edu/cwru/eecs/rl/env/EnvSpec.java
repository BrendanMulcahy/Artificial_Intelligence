package edu.cwru.eecs.rl.env;

import java.io.Serializable;


import com.google.common.collect.HashBiMap;

import edu.cwru.eecs.rl.core.mdp.Action;
import edu.cwru.eecs.rl.core.mdp.State;



public class EnvSpec implements Serializable{

	
	private static final long serialVersionUID = -8336087737102041270L;

	private Environment m_env;

	private String m_name;
	private double m_gamma;
	private int m_numStateVariable;
	private int m_numAction;

	private HashBiMap<State, Integer> m_startingStateSet;
	private HashBiMap<State, Integer> m_allStateSet;
	private HashBiMap<Action, Integer> m_allActionSet;

	public EnvSpec(Environment env) {
		m_env = env;
		m_startingStateSet = null;
		m_allStateSet = null;
		m_allActionSet = null;
	}

	public void setName(String name) {
		m_name = name;
	}

	public String getName() {
		return m_name;
	}

	public void setGamma(double gamma) {
		m_gamma = gamma;
	}

	public double getGamma() {
		return m_gamma;
	}

	public void setNumStateVariable(int numStateVariable) {
		m_numStateVariable = numStateVariable;
	}
	

	public int getNumStateVariable() {
		return m_numStateVariable;
	}

	public void setNumAction(int numAction) {
		m_numAction = numAction;
	}

	public int getNumAction() {
		return m_numAction;
	}
	
	public Action getAction(int actionIndex) {
		if(m_allActionSet==null)
			m_allActionSet = getAllActionSet();
		return m_allActionSet.inverse().get(actionIndex);
	}
	
	public int getActionIndex(Action action) {
		if(m_allActionSet==null)
			m_allActionSet = getAllActionSet();
		return m_allActionSet.get(action);
	}
	
	public HashBiMap<State, Integer> getStartingStateSet() {
		if(m_startingStateSet==null)
			m_startingStateSet = m_env.getStartingStateSet();
		return m_startingStateSet;
	}

	public HashBiMap<State, Integer> getAllStateSet() {
		if(m_allActionSet==null)
			m_allStateSet = m_env.getAllStateSet();
		return m_allStateSet;
	}

	public HashBiMap<Action, Integer> getAllActionSet() {
		if(m_allActionSet==null)
			m_allActionSet = m_env.getAllActionSet();
		return m_allActionSet;
	}

//	public HashBiMap<Parameter, Integer> getActionParameterList(int actionName) {
//		// TODO
//		return null;
//	}
}
