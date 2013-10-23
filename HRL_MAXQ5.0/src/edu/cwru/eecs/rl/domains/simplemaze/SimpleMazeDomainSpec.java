package edu.cwru.eecs.rl.domains.simplemaze;

import java.util.ArrayList;



import edu.cwru.eecs.rl.core.maxnode.MaxNode;
import edu.cwru.eecs.rl.core.mdp.Parameter;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.qnode.IQNodeInterface;
import edu.cwru.eecs.rl.core.qnode.QNode;
import edu.cwru.eecs.rl.domains.DomainSpec;
import edu.cwru.eecs.rl.env.SimpleMazeEnv;

public class SimpleMazeDomainSpec implements DomainSpec {

	private static final long serialVersionUID = -8006080537810345202L;
	
	public int predicate(State state, MaxNode maxNode) {
		if(maxNode.isPrimitive())
			return STATE_GOAL;
		boolean cond1, cond2;
		switch (maxNode.name()) {
		case SimpleMazeMaxQGraph.TASK_ROOT:
			return (state.equals(SimpleMazeEnv.goalState()))?STATE_GOAL:STATE_NON_TERMINAL;
		case SimpleMazeMaxQGraph.TASK_EXIT:
			cond1 = (state.getValue(0)>=SimpleMazeEnv.exitX());
			cond2 = (state.getValue(1)==SimpleMazeEnv.exitYGoal());
			if(cond1 && cond2)
				return STATE_GOAL;
			else if(cond1 && !cond2)
				return STATE_NON_GOAL_TERMINAL;
			return STATE_NON_TERMINAL;
		case SimpleMazeMaxQGraph.TASK_GOTOGOAL:
			cond1 = (state.getValue(0)<SimpleMazeEnv.exitX());
			cond2 = (state.equals(SimpleMazeEnv.goalState()));
			if(cond2)
				return STATE_GOAL;
			else if(cond1)
				return STATE_NON_GOAL_TERMINAL;
			return STATE_NON_TERMINAL;
		case SimpleMazeMaxQGraph.TASK_NORTH:
		case SimpleMazeMaxQGraph.TASK_SOUTH:
		case SimpleMazeMaxQGraph.TASK_EAST:
			return STATE_GOAL;
		default:
			return STATE_NON_TERMINAL;
		}
	}

	@Override
	public boolean isStartingState(State state, MaxNode maxNode) {
		if(maxNode.isPrimitive())
			return true;
		return !isTerminal(state, maxNode);
	}

	@Override
	public ArrayList<Parameter> getQParameters(State state, MaxNode maxNode,
			IQNodeInterface iQNode) {
		return Parameter.getNilArrayList();
	}

	@Override
	public ArrayList<Parameter> getMaxParameters(State state, QNode qNode) {
		return Parameter.getNilArrayList();
	}

	@Override
	public State maxStateAbsMapping(State state, MaxNode maxNode) {
		return state;
	}

	@Override
	public State qStateAbsMapping(State state, QNode qNode) {
		return state;
	}
	
	@Override
	public State eStateAbsMapping(State state, QNode qNode) {
		return state;
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
	public boolean singleExit(MaxNode maxNode) {
		if(maxNode.name()==SimpleMazeMaxQGraph.TASK_EXIT)
			return false;
		return true;
	}

	

}
