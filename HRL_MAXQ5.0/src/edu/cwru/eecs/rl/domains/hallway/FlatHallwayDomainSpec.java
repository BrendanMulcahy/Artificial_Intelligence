package edu.cwru.eecs.rl.domains.hallway;

import java.util.ArrayList;

import edu.cwru.eecs.rl.core.maxnode.MaxNode;
import edu.cwru.eecs.rl.core.mdp.Parameter;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.qnode.IQNodeInterface;
import edu.cwru.eecs.rl.core.qnode.QNode;
import edu.cwru.eecs.rl.domains.DomainSpec;
import edu.cwru.eecs.rl.env.HallwayEnv;

public class FlatHallwayDomainSpec implements DomainSpec {

	private static final long serialVersionUID = -1291578381341294246L;

	private int predicate(State state, MaxNode maxNode) {
		if(maxNode.isPrimitive()) 
			return STATE_GOAL;
		int roomNo = (int)state.getValue(HallwayEnv.STATE_ROOM_NUMBER);
		
		switch (maxNode.name()) {
		case FlatHallwayMaxQGraph.TASK_ROOT:
			return (roomNo==HallwayEnv.ENV_GOAL_ROOM_NUMBER)?STATE_GOAL:STATE_NON_TERMINAL;
		
		case FlatHallwayMaxQGraph.TASK_MOVE:
			return STATE_GOAL;
		default:
			break;
		}
		return STATE_NON_TERMINAL;
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
	public boolean isStartingState(State state, MaxNode maxNode) {
		if(maxNode.isPrimitive())
			return true;
		return !isTerminal(state, maxNode);
	}

	@Override
	public ArrayList<Parameter> getQParameters(State state, MaxNode maxNode,
			IQNodeInterface iQNode) {
		ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
		for(int i=0; i<4; i++) {
			Parameter tempParameter = new Parameter(1);
			tempParameter.setValue(0, i);
			parameterList.add(tempParameter);
		}
		return parameterList;
	}

	@Override
	public ArrayList<Parameter> getMaxParameters(State state, QNode qNode) {
		Parameter qParameter = qNode.getParameter();
		ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
		parameterList.add(qParameter);
		return parameterList;
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
	public boolean singleExit(MaxNode maxNode) {
		return true;
	}

}
