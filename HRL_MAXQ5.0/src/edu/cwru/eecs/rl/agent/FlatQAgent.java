package edu.cwru.eecs.rl.agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import edu.cwru.eecs.futil.Pair;
import edu.cwru.eecs.rl.core.mdp.Action;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.vf.LinearQVF;
import edu.cwru.eecs.rl.core.vf.TabularVF;
import edu.cwru.eecs.rl.core.vf.ValueFunction;
import edu.cwru.eecs.rl.env.Environment;
import edu.cwru.eecs.rl.experiment.FileProcessor;

public class FlatQAgent extends Agent {
	
	
	private static final long serialVersionUID = -3419890141634346867L;
	
	@SuppressWarnings("rawtypes")
	private ValueFunction m_QValues;
	private int m_numAction;
	
	public FlatQAgent(Environment environment, String[] args) {
		super(environment, AgentType.FlatQ);
		
		m_numAction = m_envSpec.getNumAction();
		m_QValues = new TabularVF<Pair<State,Action>>(m_agentType.toString());
		
//		m_epsilon = 1.0;
//		m_epsilonMin = 0.05;
//		m_epsilonDecayRate = 0.95;
//		m_alpha = 0.5;
		
		m_epsilon = 0.01;  // 0.1 for compariable experiment
		m_epsilonMin = 0.01;
		m_epsilonDecayRate = 0.95;
		m_alpha = 0.02;  // 0.05 for comparable experiment
	}
	
	public static String getUsage() {
		return "arg1: none";
	}
	
	public String message(String message) {
		String[] messages = message.split(",");
		if(messages[0].trim().equals("vf")) {
			if(messages[1].trim().equals("table")) {
				m_QValues = new TabularVF<Pair<State,Action>>(m_agentType.toString());
				return "set vf table";
			}
			if(messages[1].trim().equals("linear")) {
				m_QValues = new LinearQVF(m_envSpec.getNumStateVariable()+1);
				return "set vf linear";
			}
			if(messages[1].trim().equals("print")) {
				if(messages.length>2) {
					String pathString = messages[2].trim();
					try {
						PrintStream ps = new PrintStream(new File(pathString+FileProcessor.RESULT_VF_EXTENSION));
						m_QValues.print(ps, true);
						ps.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				else {
					m_QValues.print(System.out, false);
				}
			}
		}
		return super.message(message);
	}

	@Override
	protected void run(int mode) {
		State currentState = m_environment.getState();
		Action action;
		int actionIndex;
		double reward;
		State nextState;
		if(m_debug) {
			System.out.println("Starting state: " + currentState.toString()); 
		}
		
		while(!m_environment.isTerminal() && m_step<m_maxStep)
		{
			// choose action
			double epsilon = m_epsilon;
			if(mode == MODE_TESTING) {
				epsilon = 0;
			}
			actionIndex = actionSelection(currentState, epsilon);
			action = getAction(actionIndex);
			if(m_debug) {
				System.out.println("Action: " + getActionName(actionIndex));
			}
			
			m_environment.takeAction(action);
			
			// get reward and next state
			reward = m_environment.getReward();
			nextState = m_environment.getState();
			
			if(m_debug) {
				System.out.println("Reward: " + reward);
				System.out.println("State: " + nextState.toString()); 
			}
			
			// update q value function
			if(mode == MODE_LEARNING) {
				update(currentState, action, reward, nextState);
				epsilonDecay();
			}
			
			// move to next iteration
			currentState = nextState;
			m_cumulativeReward += reward;
			m_step ++;
		}
		if(mode==Agent.MODE_LEARNING && m_step<m_maxStep)
			epsilonDecay();
		if(mode==Agent.MODE_LEARNING)
			m_episode++;
	}

	@SuppressWarnings("unchecked")
	protected void update(State state, Action action, double reward,
			State nextState) {
		double oldValue = m_QValues.getValue(getStateAction(state, action));
		double[] values = getQValues(nextState);
		int nextActionIndex = eGreedySelection(values, 0);
		Action nextAction = getAction(nextActionIndex);
		double nextQValue = m_QValues.getValue(getStateAction(nextState, nextAction));
		double newValue = oldValue + m_alpha*(reward + m_gamma*nextQValue - oldValue);
		m_QValues.updateValue(getStateAction(state, action), newValue);
	}
	
	private int actionSelection(State state, double epsilon) {
		double[] values = getQValues(state);
		return eGreedySelection(values, epsilon);
	}
	
	@SuppressWarnings("unchecked")
	private double[] getQValues(State state) {
		double[] values = new double[m_numAction];
		for(int a=0; a<m_numAction; a++)
		{
			values[a] = m_QValues.getValue(getStateAction(state, getAction(a)));
		}
		return values;
	}
}
