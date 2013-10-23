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

public class TaxiDomainSpec implements DomainSpec {

	private static final long serialVersionUID = 8359897791087731313L;

	
	private int predicate(State state, MaxNode maxNode) {
		int locX = (int)state.getValue(TaxiEnv.STATE_LOCX);
		int locY = (int)state.getValue(TaxiEnv.STATE_LOCY);
		int source = (int)state.getValue(TaxiEnv.STATE_SOURCE);
		int dest = (int)state.getValue(TaxiEnv.STATE_DEST);
		Point loc = new Point(locX, locY);
		boolean cond1, cond2;
		switch (maxNode.name()) {
		case TaxiMaxQGraph.TASK_ROOT:
			cond1 = (TaxiEnv.getSite(dest).equals(loc) && source==TaxiEnv.SITE_NAME_NONE);
			return cond1?STATE_GOAL:STATE_NON_TERMINAL;
		case TaxiMaxQGraph.TASK_GET:
			cond1 = (source==TaxiEnv.SITE_NAME_HELD);
			return cond1?STATE_GOAL:STATE_NON_TERMINAL;
		case TaxiMaxQGraph.TASK_PUT:
			cond1 = (source!=TaxiEnv.SITE_NAME_HELD);
			cond2 = (source==TaxiEnv.SITE_NAME_NONE && TaxiEnv.getSite(dest).equals(loc));
			if(cond2) 
				return STATE_GOAL;
			else if(cond1)
				return STATE_NON_GOAL_TERMINAL;
			return STATE_NON_TERMINAL;
		case TaxiMaxQGraph.TASK_NAVIGATE:
			int t = (int)maxNode.getParameter().getValue(0);
			cond1 = (TaxiEnv.getSite(t).equals(loc));
			return cond1?STATE_GOAL:STATE_NON_TERMINAL;
		case TaxiMaxQGraph.TASK_PICKUP:
		case TaxiMaxQGraph.TASK_PUTDOWN:
		case TaxiMaxQGraph.TASK_NORTH:
		case TaxiMaxQGraph.TASK_EAST:
		case TaxiMaxQGraph.TASK_SOUTH:
		case TaxiMaxQGraph.TASK_WEST:
			return STATE_GOAL;
		default:
			break;
		}
		return STATE_NON_TERMINAL;
	}

	@Override
	public boolean isStartingState(State state, MaxNode maxNode) {
		if(maxNode.isPrimitive())
			return true;
		return !isTerminal(state, maxNode);
	}

	@Override
	public ArrayList<Parameter> getQParameters(State state, MaxNode maxNode, IQNodeInterface iQNode) {
		switch (maxNode.name()) {
		case TaxiMaxQGraph.TASK_NAVIGATE:
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(maxNode.getParameter());
			return parameterList;
		default:
			return Parameter.getNilArrayList();
		}
	}

	@Override
	public ArrayList<Parameter> getMaxParameters(State state, QNode qNode) {
		switch (qNode.name()) {
		case TaxiMaxQGraph.Q_NAVIGATE_FOR_GET:
		case TaxiMaxQGraph.Q_NAVIGATE_FOR_PUT:
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			for(int i=0; i<4; i++) {
				Parameter parameter = new Parameter(1);
				parameter.setValue(0, i);
				parameterList.add(parameter);
			}
			return parameterList;
		default:
			return Parameter.getNilArrayList();
		}
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
		case TaxiMaxQGraph.TASK_ROOT:
			break;
		case TaxiMaxQGraph.TASK_GET:
			abstractState = new State(3);
			abstractState.setValue(0, locX);
			abstractState.setValue(1, locY);
			abstractState.setValue(2, source);
			break;
		case TaxiMaxQGraph.TASK_PUT:
			abstractState = new State(3);
			abstractState.setValue(0, locX);
			abstractState.setValue(1, locY);
			abstractState.setValue(2, dest);
			break;
		case TaxiMaxQGraph.TASK_NAVIGATE:
			abstractState = new State(2);
			abstractState.setValue(0, locX);
			abstractState.setValue(1, locY);
			break;
		case TaxiMaxQGraph.TASK_PICKUP:
			abstractState = new State(1);
			if(source!=TaxiEnv.SITE_NAME_HELD && source!=TaxiEnv.SITE_NAME_NONE && TaxiEnv.getSite(source).equals(loc))
				abstractState.setValue(0, 0);
			else
				abstractState.setValue(0, 1);
			break;
		case TaxiMaxQGraph.TASK_PUTDOWN:
			abstractState = new State(1);
			if(TaxiEnv.containSite(loc) && source==TaxiEnv.SITE_NAME_HELD && !TaxiEnv.getSite(dest).equals(loc))
				abstractState.setValue(0, 0);
			else if(TaxiEnv.getSite(dest).equals(loc) && source==TaxiEnv.SITE_NAME_HELD)
				abstractState.setValue(0, 1);
			else
				abstractState.setValue(0, 2);
			break;
		case TaxiMaxQGraph.TASK_NORTH:
		case TaxiMaxQGraph.TASK_EAST:
		case TaxiMaxQGraph.TASK_SOUTH:
		case TaxiMaxQGraph.TASK_WEST:
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
		int locX = (int)state.getValue(TaxiEnv.STATE_LOCX);
		int locY = (int)state.getValue(TaxiEnv.STATE_LOCY);
		int source = (int)state.getValue(TaxiEnv.STATE_SOURCE);
		int dest = (int)state.getValue(TaxiEnv.STATE_DEST);
		State abstractState = state;
		switch (qNode.name()) {
		case TaxiMaxQGraph.Q_GET:
		case TaxiMaxQGraph.Q_PUT:
			abstractState = new State(2);
			abstractState.setValue(0, source);
			abstractState.setValue(1, dest);
			break;
		case TaxiMaxQGraph.Q_NAVIGATE_FOR_GET:
			abstractState = new State(1);
			abstractState.setValue(0, source);
			break;
		case TaxiMaxQGraph.Q_NAVIGATE_FOR_PUT:
			abstractState = new State(1);
			abstractState.setValue(0, dest);
			break;
		case TaxiMaxQGraph.Q_PICKUP:
			abstractState = new State(3);
			abstractState.setValue(0, locX);
			abstractState.setValue(1, locY);
			abstractState.setValue(2, source);
			break;
		case TaxiMaxQGraph.Q_PUTDOWN:
			abstractState = new State(3);
			abstractState.setValue(0, locX);
			abstractState.setValue(1, locY);
			abstractState.setValue(2, dest);
			break;
		case TaxiMaxQGraph.Q_NORTH:
		case TaxiMaxQGraph.Q_EAST:
		case TaxiMaxQGraph.Q_SOUTH:
		case TaxiMaxQGraph.Q_WEST:
			abstractState = new State(2);
			abstractState.setValue(0, locX);
			abstractState.setValue(1, locY);
			break;
		default:
			break;
		}
		return abstractState;
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
		if(maxNode.name()==TaxiMaxQGraph.TASK_PUT)
			return false;
		return true;
	}

	
	

}
