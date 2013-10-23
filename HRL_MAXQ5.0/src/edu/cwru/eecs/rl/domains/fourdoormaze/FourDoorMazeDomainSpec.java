package edu.cwru.eecs.rl.domains.fourdoormaze;

import java.util.ArrayList;

import edu.cwru.eecs.rl.core.maxnode.MaxNode;
import edu.cwru.eecs.rl.core.mdp.Parameter;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.qnode.IQNodeInterface;
import edu.cwru.eecs.rl.core.qnode.QNode;
import edu.cwru.eecs.rl.domains.DomainSpec;

public class FourDoorMazeDomainSpec implements DomainSpec {

	private static final long serialVersionUID = -5967934593298136691L;

	public int predicate(State state, MaxNode maxNode) {
		if(maxNode.isPrimitive())
			return STATE_GOAL;
		// TODO;
		return 0;
	}

	@Override
	public boolean isStartingState(State state, MaxNode maxNode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ArrayList<Parameter> getQParameters(State state, MaxNode maxNode,
			IQNodeInterface iQNode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Parameter> getMaxParameters(State state, QNode qNode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public State maxStateAbsMapping(State state, MaxNode maxNode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public State qStateAbsMapping(State state, QNode qNode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean singleExit(MaxNode maxNode) {
		if(maxNode.name()==FourDoorMazeMaxQGraph.TASK_EXIT_COMMON
				|| maxNode.name()==FourDoorMazeMaxQGraph.TASK_ENTER_COMMON)
			return false;
		return true;
	}
	
	@Override
	public boolean isGoal(State state, MaxNode maxNode) {
		return predicate(state, maxNode)==STATE_GOAL;
	}
	
	@Override
	public boolean isNonGoalTerminal(State state, MaxNode maxNode) {
		return predicate(state, maxNode)==STATE_NON_GOAL_TERMINAL;
	}

	@Override
	public boolean isTerminal(State state, MaxNode maxNode) {
		return isGoal(state, maxNode) || isNonGoalTerminal(state, maxNode);
	}

	@Override
	public State eStateAbsMapping(State state, QNode qNode) {
		// TODO Auto-generated method stub
		return null;
	}

}
