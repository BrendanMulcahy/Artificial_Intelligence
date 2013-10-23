package edu.cwru.eecs.rl.domains.wargus;

import java.util.ArrayList;

import edu.cwru.eecs.rl.core.maxnode.MaxNode;
import edu.cwru.eecs.rl.core.mdp.Parameter;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.qnode.IQNodeInterface;
import edu.cwru.eecs.rl.core.qnode.QNode;
import edu.cwru.eecs.rl.domains.DomainSpec;
import edu.cwru.eecs.rl.env.WargusEnv;

public class WargusDomainSpec implements DomainSpec {

	private static final long serialVersionUID = 8091480381870459819L;
	
	/**
	 * STATE_GOAL | STATE_NON_GOAL_TERMINAL | STATE_NON_TERMINAL
	 */
	private int predicate(State state, MaxNode maxNode) {
//		int locX = (int)state.getValue(WargusEnv.STATE_LOCX);
//		int locY = (int)state.getValue(WargusEnv.STATE_LOCY);
		int resource = (int)state.getValue(WargusEnv.STATE_RESOURCE);
//		boolean goldInRegion = (state.getValue(WargusEnv.STATE_GOLD_IN_REGION)>0);
//		boolean woodInRegion = (state.getValue(WargusEnv.STATE_WOOD_IN_REGION)>0);
//		boolean townhallInRegion = (state.getValue(WargusEnv.STATE_TOWNHALL_IN_REGION)>0);
		boolean meetGoldReq = (state.getValue(WargusEnv.STATE_MEET_GOLD_REQ)>0);
		boolean meetWoodReq = (state.getValue(WargusEnv.STATE_MEET_WOOD_REQ)>0);
		
		switch (maxNode.name()) {
		case WargusMaxQGraph.TASK_ROOT:
			return meetGoldReq && meetWoodReq ? STATE_GOAL : STATE_NON_TERMINAL;
		case WargusMaxQGraph.TASK_GETGOLD:
			switch (resource) {
			case WargusEnv.RESOURCE_GOLD:
				return STATE_GOAL;
			case WargusEnv.RESOURCE_WOOD:
				return STATE_NON_GOAL_TERMINAL;
			case WargusEnv.RESOURCE_NONE:
				return STATE_NON_TERMINAL;
			default:
				return STATE_NON_TERMINAL;
			}
		case WargusMaxQGraph.TASK_GETWOOD:
			switch (resource) {
			case WargusEnv.RESOURCE_GOLD:
				return STATE_NON_GOAL_TERMINAL;
			case WargusEnv.RESOURCE_WOOD:
				return STATE_GOAL;
			case WargusEnv.RESOURCE_NONE:
				return STATE_NON_TERMINAL;
			default:
				return STATE_NON_TERMINAL;
			}
		case WargusMaxQGraph.TASK_GWDEPOSIT:
			return resource==WargusEnv.RESOURCE_NONE ? STATE_GOAL : STATE_NON_TERMINAL;
		case WargusMaxQGraph.TASK_CHOPWOOD:
		case WargusMaxQGraph.TASK_MINEGOLD:
		case WargusMaxQGraph.TASK_DEPOSIT:
		case WargusMaxQGraph.TASK_GOTO:
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
	public ArrayList<Parameter> getQParameters(State state, MaxNode maxNode,
			IQNodeInterface iQNode) {
		switch (maxNode.name()) {
		case WargusMaxQGraph.TASK_GETGOLD:
		case WargusMaxQGraph.TASK_GETWOOD:
		case WargusMaxQGraph.TASK_GWDEPOSIT:
			int qName = iQNode.name();
			if(qName==WargusMaxQGraph.Q_GOTO_FOR_GETGOLD ||
					qName==WargusMaxQGraph.Q_GOTO_FOR_GETWOOD ||
					qName==WargusMaxQGraph.Q_GOTO_FOR_GWDEPOSIT) {
				ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
				parameterList.addAll(WargusEnv.getAllPosition());
				return parameterList;
			}
			else {
				return Parameter.getNilArrayList();
			}
		default:
			return Parameter.getNilArrayList();
		}
	}

	@Override
	public ArrayList<Parameter> getMaxParameters(State state, QNode qNode) {
		switch (qNode.name()) {
		case WargusMaxQGraph.Q_GOTO_FOR_GETGOLD:
		case WargusMaxQGraph.Q_GOTO_FOR_GETWOOD:
		case WargusMaxQGraph.Q_GOTO_FOR_GWDEPOSIT:
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(qNode.getParameter());
			return parameterList;
		default:
			return Parameter.getNilArrayList();	
		}
	}

	@Override
	public State maxStateAbsMapping(State state, MaxNode maxNode) {
		int locX = (int)state.getValue(WargusEnv.STATE_LOCX);
		int locY = (int)state.getValue(WargusEnv.STATE_LOCY);
		int resource = (int)state.getValue(WargusEnv.STATE_RESOURCE);
		double goldInRegion = state.getValue(WargusEnv.STATE_GOLD_IN_REGION);
		double woodInRegion = state.getValue(WargusEnv.STATE_WOOD_IN_REGION);
		double townhallInRegion = state.getValue(WargusEnv.STATE_TOWNHALL_IN_REGION);
		double meetGoldReq = state.getValue(WargusEnv.STATE_MEET_GOLD_REQ);
		double meetWoodReq = state.getValue(WargusEnv.STATE_MEET_WOOD_REQ);

		State abstractState = state;
		switch (maxNode.name()) {
		case WargusMaxQGraph.TASK_ROOT:
			abstractState = state;
			break;
		case WargusMaxQGraph.TASK_GETGOLD:
			abstractState = new State(5);
			abstractState.setValue(0, locX);
			abstractState.setValue(1, locY);
			abstractState.setValue(2, resource);
			abstractState.setValue(3, goldInRegion);
			abstractState.setValue(4, meetGoldReq);
			break;
		case WargusMaxQGraph.TASK_GETWOOD:
			abstractState = new State(5);
			abstractState.setValue(0, locX);
			abstractState.setValue(1, locY);
			abstractState.setValue(2, resource);
			abstractState.setValue(3, woodInRegion);
			abstractState.setValue(4, meetWoodReq);
			break;
		case WargusMaxQGraph.TASK_GWDEPOSIT:
			abstractState = new State(4);
			abstractState.setValue(0, locX);
			abstractState.setValue(1, locY);
			abstractState.setValue(2, resource);
			abstractState.setValue(3, townhallInRegion);
			break;
		case WargusMaxQGraph.TASK_CHOPWOOD:
			abstractState = new State(1);
			abstractState.setValue(0, 0);
			if(woodInRegion==1 && resource==WargusEnv.RESOURCE_NONE)
				abstractState.setValue(0, 1);
			break;
		case WargusMaxQGraph.TASK_MINEGOLD:
			abstractState = new State(1);
			abstractState.setValue(0, 0);
			if(goldInRegion==1 && resource==WargusEnv.RESOURCE_NONE)
				abstractState.setValue(0, 1);
			break;
		case WargusMaxQGraph.TASK_DEPOSIT:
			abstractState = new State(4);
			abstractState.setValue(0, resource);
			abstractState.setValue(1, townhallInRegion);
			abstractState.setValue(2, meetGoldReq);
			abstractState.setValue(3, meetWoodReq);
			break;
		case WargusMaxQGraph.TASK_GOTO:
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
	public State qStateAbsMapping(State state, QNode qNode) {
		int locX = (int)state.getValue(WargusEnv.STATE_LOCX);
		int locY = (int)state.getValue(WargusEnv.STATE_LOCY);
		int resource = (int)state.getValue(WargusEnv.STATE_RESOURCE);
		double goldInRegion = state.getValue(WargusEnv.STATE_GOLD_IN_REGION);
		double woodInRegion = state.getValue(WargusEnv.STATE_WOOD_IN_REGION);
		double townhallInRegion = state.getValue(WargusEnv.STATE_TOWNHALL_IN_REGION);
		double meetGoldReq = state.getValue(WargusEnv.STATE_MEET_GOLD_REQ);
		double meetWoodReq = state.getValue(WargusEnv.STATE_MEET_WOOD_REQ);

		State abstractState = state;
		switch (qNode.name()) {
		case WargusMaxQGraph.Q_GETGOLD:
		case WargusMaxQGraph.Q_GETWOOD:
		case WargusMaxQGraph.Q_GWDEPOSIT:
			abstractState = new State(6);
			abstractState.setValue(0, resource);
			abstractState.setValue(1, goldInRegion);
			abstractState.setValue(2, woodInRegion);
			abstractState.setValue(3, townhallInRegion);
			abstractState.setValue(4, meetGoldReq);
			abstractState.setValue(5, meetWoodReq);
			//abstractState = state;
			break;
		case WargusMaxQGraph.Q_GOTO_FOR_GETGOLD:
		case WargusMaxQGraph.Q_MINEGOLD:
			abstractState = new State(4);
			abstractState.setValue(0, locX);
			abstractState.setValue(1, locY);
			abstractState.setValue(2, resource);
			abstractState.setValue(3, goldInRegion);
			break;
		case WargusMaxQGraph.Q_GOTO_FOR_GETWOOD:
		case WargusMaxQGraph.Q_CHOPWOOD:
			abstractState = new State(4);
			abstractState.setValue(0, locX);
			abstractState.setValue(1, locY);
			abstractState.setValue(2, resource);
			abstractState.setValue(3, woodInRegion);
			break;
		case WargusMaxQGraph.Q_GOTO_FOR_GWDEPOSIT:
		case WargusMaxQGraph.Q_DEPOSIT:
			abstractState = new State(4);
			abstractState.setValue(0, locX);
			abstractState.setValue(1, locY);
			abstractState.setValue(2, resource);
			abstractState.setValue(3, townhallInRegion);
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
		return false;
	}



}
