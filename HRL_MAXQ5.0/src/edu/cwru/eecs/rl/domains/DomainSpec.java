package edu.cwru.eecs.rl.domains;

import java.io.Serializable;
import java.util.ArrayList;

import edu.cwru.eecs.rl.core.maxnode.MaxNode;
import edu.cwru.eecs.rl.core.mdp.Parameter;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.qnode.IQNodeInterface;
import edu.cwru.eecs.rl.core.qnode.QNode;



public interface DomainSpec extends Serializable{
	
	public static final int STATE_NON_TERMINAL = -1;
	public static final int STATE_NON_GOAL_TERMINAL = 0;
	public static final int STATE_GOAL = 1;

	public boolean isGoal(State state, MaxNode maxNode);
	
	public boolean isNonGoalTerminal(State state, MaxNode maxNode);
	
	public boolean isTerminal(State state, MaxNode maxNode);

	public boolean isStartingState(State state, MaxNode maxNode);

	public ArrayList<Parameter> getQParameters(State state, MaxNode maxNode, IQNodeInterface iQNode);

	public ArrayList<Parameter> getMaxParameters(State state, QNode qNode);

	// only for primitive action
	public State maxStateAbsMapping(State state, MaxNode maxNode);

	public State qStateAbsMapping(State state, QNode qNode);
	
	public State eStateAbsMapping(State state, QNode qNode); // state abstraction for external value function in alispq algorithm
	
	public boolean singleExit(MaxNode maxNode);
}
