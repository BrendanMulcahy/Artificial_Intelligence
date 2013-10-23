package edu.cwru.eecs.rl.domains.wargus;

import java.util.ArrayList;

import edu.cwru.eecs.rl.core.maxnode.MaxNode;
import edu.cwru.eecs.rl.core.mdp.Parameter;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.qnode.IQNodeInterface;
import edu.cwru.eecs.rl.core.qnode.QNode;
import edu.cwru.eecs.rl.domains.DomainSpec;
import edu.cwru.eecs.rl.env.WargusEnv;

public class FlatWargusDomainSpec implements DomainSpec {

	private static final long serialVersionUID = 7125467958364111309L;

	private int predicate(State state, MaxNode maxNode) {
		boolean meetGoldReq = (state.getValue(WargusEnv.STATE_MEET_GOLD_REQ)>0);
		boolean meetWoodReq = (state.getValue(WargusEnv.STATE_MEET_WOOD_REQ)>0);
		switch (maxNode.name()) {
		case FlatWargusMaxQGraph.TASK_ROOT:
			return meetGoldReq && meetWoodReq ? STATE_GOAL : STATE_NON_TERMINAL;
		case FlatWargusMaxQGraph.TASK_CHOPWOOD:
		case FlatWargusMaxQGraph.TASK_MINEGOLD:
		case FlatWargusMaxQGraph.TASK_DEPOSIT:
		case FlatWargusMaxQGraph.TASK_GOTO:
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
		int qName = iQNode.name();
		if(qName==FlatWargusMaxQGraph.Q_GOTO) {
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.addAll(WargusEnv.getAllPosition());
			return parameterList;
		}
		else {
			return Parameter.getNilArrayList();
		}
	}

	@Override
	public ArrayList<Parameter> getMaxParameters(State state, QNode qNode) {
		switch (qNode.name()) {
		case FlatWargusMaxQGraph.Q_GOTO:
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(qNode.getParameter());
			return parameterList;
		default:
			return Parameter.getNilArrayList();	
		}
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
