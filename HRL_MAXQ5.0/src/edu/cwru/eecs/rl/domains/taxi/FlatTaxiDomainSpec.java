package edu.cwru.eecs.rl.domains.taxi;

import java.util.ArrayList;

import edu.cwru.eecs.futil.Point;
import edu.cwru.eecs.rl.core.maxnode.MaxNode;
import edu.cwru.eecs.rl.core.mdp.Parameter;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.qnode.IQNodeInterface;
import edu.cwru.eecs.rl.core.qnode.QNode;
import edu.cwru.eecs.rl.domains.DomainSpec;
import edu.cwru.eecs.rl.env.TaxiEnv;

public class FlatTaxiDomainSpec implements DomainSpec {

	private static final long serialVersionUID = 8630419519193255374L;

	private int predicate(State state, MaxNode maxNode) {
		int locX = (int)state.getValue(TaxiEnv.STATE_LOCX);
		int locY = (int)state.getValue(TaxiEnv.STATE_LOCY);
		int source = (int)state.getValue(TaxiEnv.STATE_SOURCE);
		int dest = (int)state.getValue(TaxiEnv.STATE_DEST);
		Point loc = new Point(locX, locY);
		boolean cond1;
		switch (maxNode.name()) {
		case FlatTaxiMaxQGraph.TASK_PICKUP:
		case FlatTaxiMaxQGraph.TASK_PUTDOWN:
		case FlatTaxiMaxQGraph.TASK_NORTH:
		case FlatTaxiMaxQGraph.TASK_EAST:
		case FlatTaxiMaxQGraph.TASK_SOUTH:
		case FlatTaxiMaxQGraph.TASK_WEST:
			return STATE_GOAL;
		case FlatTaxiMaxQGraph.TASK_ROOT:
			cond1 = (TaxiEnv.getSite(dest).equals(loc) && source==TaxiEnv.SITE_NAME_NONE);
			return cond1?STATE_GOAL:STATE_NON_TERMINAL;
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
		return Parameter.getNilArrayList();
	}

	@Override
	public ArrayList<Parameter> getMaxParameters(State state, QNode qNode) {
		return Parameter.getNilArrayList();
	}

	@Override
	public State maxStateAbsMapping(State state, MaxNode maxNode) {
		int locX = (int)state.getValue(TaxiEnv.STATE_LOCX);
		int locY = (int)state.getValue(TaxiEnv.STATE_LOCY);
		int source = (int)state.getValue(TaxiEnv.STATE_SOURCE);
		int dest = (int)state.getValue(TaxiEnv.STATE_DEST);
		Point loc = new Point(locX, locY);
		State abstractState = state;
		switch (maxNode.name()) {
		case FlatTaxiMaxQGraph.TASK_ROOT:
			break;
		case FlatTaxiMaxQGraph.TASK_PICKUP:
			abstractState = new State(1);
			if(source!=TaxiEnv.SITE_NAME_HELD && source!=TaxiEnv.SITE_NAME_NONE && TaxiEnv.getSite(source).equals(loc))
				abstractState.setValue(0, 0);
			else
				abstractState.setValue(0, 1);
			break;
		case FlatTaxiMaxQGraph.TASK_PUTDOWN:
			abstractState = new State(1);
			if(TaxiEnv.containSite(loc) && source==TaxiEnv.SITE_NAME_HELD && !TaxiEnv.getSite(dest).equals(loc))
				abstractState.setValue(0, 0);
			else if(TaxiEnv.getSite(dest).equals(loc) && source==TaxiEnv.SITE_NAME_HELD)
				abstractState.setValue(0, 1);
			else
				abstractState.setValue(0, 2);
			break;
		case FlatTaxiMaxQGraph.TASK_NORTH:
		case FlatTaxiMaxQGraph.TASK_EAST:
		case FlatTaxiMaxQGraph.TASK_SOUTH:
		case FlatTaxiMaxQGraph.TASK_WEST:
			abstractState = new State(1);
			abstractState.setValue(0, 0);
			break;
		default:
			break;
		}
		return abstractState;
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
