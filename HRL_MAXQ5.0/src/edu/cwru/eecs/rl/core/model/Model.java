package edu.cwru.eecs.rl.core.model;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Set;

import edu.cwru.eecs.rl.core.maxnode.MaxNode;
import edu.cwru.eecs.rl.core.mdp.State;

public interface Model extends Serializable {
	public void updateModel(State state, MaxNode maxNode, double reward, State nextState);
	
	public double getReward(State state, MaxNode maxNode);
	public double getTransition(State state, MaxNode maxNode, State nextState);
	public State getNextState(State state, MaxNode maxNode);
	
	public void setReward(State state, MaxNode maxNode, double reward);
	public void setTransition(State state, MaxNode maxNode, State nextState, double prob);
	
	public void clearRewards(MaxNode maxNode, Set<State> states);
	public void clearTransitions(MaxNode maxNode, Set<State> states);
	
	
	public void printModel(PrintStream ps, boolean real);
	
	public void setPrior(Model model);
}













