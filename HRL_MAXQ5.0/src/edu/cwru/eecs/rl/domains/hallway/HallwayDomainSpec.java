package edu.cwru.eecs.rl.domains.hallway;

import java.util.ArrayList;



import edu.cwru.eecs.rl.core.maxnode.MaxNode;
import edu.cwru.eecs.rl.core.mdp.Parameter;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.qnode.IQNodeInterface;
import edu.cwru.eecs.rl.core.qnode.QNode;
import edu.cwru.eecs.rl.domains.DomainSpec;
import edu.cwru.eecs.rl.env.HallwayEnv;

public class HallwayDomainSpec implements DomainSpec {

	private static final long serialVersionUID = 7136519955447656476L;
	
	/**
	 * return 	-1 if non-terminal,
	 * 			 0 if terminal but not goal,
	 * 			 1 if goal.
	 * @param state
	 * @param maxNode
	 * @return
	 */
	private int predicate(State state, MaxNode maxNode) {
		if(maxNode.isPrimitive()) 
			return STATE_GOAL;
		Parameter parameter = maxNode.getParameter();
		int roomNo = (int)state.getValue(HallwayEnv.STATE_ROOM_NUMBER);
		int locX = (int)state.getValue(HallwayEnv.STATE_ROOM_LOCX);
		int locY = (int)state.getValue(HallwayEnv.STATE_ROOM_LOCY);
		int r, d, p, x, y, _d;
		MaxNode parent = null;
		if(maxNode.name()!=HallwayMaxQGraph.TASK_ROOT)
			parent = maxNode.getParentQ().getParentMax();
		boolean cond = false;
		switch (maxNode.name()) {
		case HallwayMaxQGraph.TASK_ROOT:
			return (roomNo==HallwayEnv.ENV_GOAL_ROOM_NUMBER)?STATE_GOAL:STATE_NON_TERMINAL;
		case HallwayMaxQGraph.TASK_GO:
			d = (int)parameter.getValue(0);
			r = (int)parameter.getValue(1);
			int inter = HallwayEnv.findInter(r, d);
			if(inter>-1 && roomNo==inter)
				return STATE_GOAL;
			cond = (roomNo!=r && HallwayEnv.isInter(roomNo)) || (!HallwayEnv.hasRoom(r, d));
			break;
		case HallwayMaxQGraph.TASK_EXIT_INTER:
			d = (int)parameter.getValue(0);
			r = (int)parameter.getValue(1);
			if(HallwayEnv.neighbours(r, roomNo, d) && HallwayEnv.isHallway(roomNo))
				return STATE_GOAL;
			cond =  (roomNo!=r && HallwayEnv.isHallway(roomNo));
			break;
		case HallwayMaxQGraph.TASK_EXIT_HALL:
			d = (int)parameter.getValue(0);
			r = (int)parameter.getValue(1);
			if(HallwayEnv.neighbours(r, roomNo, d) && HallwayEnv.isInter(roomNo))
				return STATE_GOAL;
			cond =  HallwayEnv.isInter(roomNo);
			break;
		case HallwayMaxQGraph.TASK_SNIFF:
			d = (int)parameter.getValue(0);
			p = (int)parameter.getValue(1);
			cond = HallwayEnv.hasWall(roomNo, locX, locY, d) && HallwayEnv.hasWall(roomNo, locX, locY, p);
			if(cond)
				return STATE_GOAL;
			break;
		case HallwayMaxQGraph.TASK_FOLLOW_WALL:
			d = (int)parameter.getValue(0);
			p = (int)parameter.getValue(1);
			cond = !HallwayEnv.hasWall(roomNo, locX, locY, d) 
				|| (HallwayEnv.hasWall(roomNo, locX, locY, d) && HallwayEnv.hasWall(roomNo, locX, locY, p));
			if(cond)
				return STATE_GOAL;
			break;
		case HallwayMaxQGraph.TASK_TO_WALL:
			d = (int)parameter.getValue(0);
			cond = HallwayEnv.hasWall(roomNo, locX, locY, d);
			if(cond)
				return STATE_GOAL;
			break;
		case HallwayMaxQGraph.TASK_BACK:
			d = (int)parameter.getValue(0);
			p = (int)parameter.getValue(1);
			x = (int)parameter.getValue(2);
			y = (int)parameter.getValue(3);
			_d = (d+2)%4;
			cond = (HallwayEnv.dist(x, y, locX, locY, _d)>=1 || (HallwayEnv.hasWall(roomNo, locX, locY, _d))
				&& (HallwayEnv.dist(x, y, locX, locY, p)>=3) || HallwayEnv.hasWall(roomNo, locX, locY, p));
			if(cond)
				return STATE_GOAL;
			break;
		case HallwayMaxQGraph.TASK_BACK_ONE:
			d = (int)parameter.getValue(0);
			x = (int)parameter.getValue(1);
			y = (int)parameter.getValue(2);
			_d = (d+2)%4;
			cond = HallwayEnv.dist(x, y, locX, locY, _d)>=1
			|| HallwayEnv.hasWall(roomNo, locX, locY, _d);
			if(cond)
				return STATE_GOAL;
			break;
		case HallwayMaxQGraph.TASK_PERP_THREE:
			p = (int)parameter.getValue(0);
			x = (int)parameter.getValue(1);
			y = (int)parameter.getValue(2);
			cond = HallwayEnv.dist(x, y, locX, locY, p)>=3
			|| HallwayEnv.hasWall(roomNo, locX, locY, p);
			if(cond)
				return STATE_GOAL;
			break;
		case HallwayMaxQGraph.TASK_MOVE:
			return STATE_GOAL;
		default:
			break;
		}
		int parentPredicate = predicate(state, parent);
		if(parentPredicate==STATE_GOAL)
			return STATE_GOAL;
		if(parentPredicate==STATE_NON_GOAL_TERMINAL)
			return STATE_NON_GOAL_TERMINAL;
		return cond?STATE_NON_GOAL_TERMINAL:STATE_NON_TERMINAL;
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
	public ArrayList<Parameter> getQParameters(State state, MaxNode maxNode, IQNodeInterface iQNode) {
		Parameter maxParameter = maxNode.getParameter();
		ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
		Parameter parameter;
		int d, p, x, y;
		switch (maxNode.name()) {
		case HallwayMaxQGraph.TASK_ROOT:
			for(int i=0; i<4; i++) {
				Parameter tempParameter = new Parameter(1);
				tempParameter.setValue(0, i);
				parameterList.add(tempParameter);
			}
			return parameterList;
		case HallwayMaxQGraph.TASK_GO:
			parameterList.add(maxParameter);
			return parameterList;
		case HallwayMaxQGraph.TASK_EXIT_INTER:
		case HallwayMaxQGraph.TASK_EXIT_HALL:
			d = (int)maxNode.getParameter().getValue(0);
			for(int i=1; i<4; i+=2) {
				Parameter tempParameter = new Parameter(2);
				tempParameter.setValue(0, d);
				tempParameter.setValue(1, (d+i)%4);
				parameterList.add(tempParameter);
			}
			return parameterList;
		case HallwayMaxQGraph.TASK_SNIFF:
			if(iQNode.name()==HallwayMaxQGraph.Q_FOLLOW_WALL) {
				parameterList.add(maxParameter);
			} else if(iQNode.name()==HallwayMaxQGraph.Q_TO_WALL) {
				parameter = new Parameter(1);
				parameter.setValue(0, maxParameter.getValue(0));
				parameterList.add(parameter);
			} else {
				System.err.println("Maxnode and QNode are not parent and son: " + maxNode.getIMaxNode().nameString() + " " + iQNode.nameString());
				return Parameter.getNilArrayList();
			}
			return parameterList;
		case HallwayMaxQGraph.TASK_BACK:
			d = (int)maxParameter.getValue(0);
			p = (int)maxParameter.getValue(1);
			x = (int)maxParameter.getValue(2);
			y = (int)maxParameter.getValue(3);
			if(iQNode.name()==HallwayMaxQGraph.Q_BACK_ONE) {
				parameter = new Parameter(3);
				parameter.setValue(0, d);
				parameter.setValue(1, x);
				parameter.setValue(2, y);
				parameterList.add(parameter);
			} else if(iQNode.name()==HallwayMaxQGraph.Q_PERP_THREE) {
				parameter = new Parameter(3);
				parameter.setValue(0, p);
				parameter.setValue(1, x);
				parameter.setValue(2, y);
				parameterList.add(parameter);
			} else {
				System.err.println("Maxnode and QNode are not parent and son: " + maxNode.getIMaxNode().nameString() + " " + iQNode.nameString());
				return Parameter.getNilArrayList();
			}
			return parameterList;
		case HallwayMaxQGraph.TASK_FOLLOW_WALL:
			parameter = new Parameter(1);
			p = (int)maxParameter.getValue(1);
			parameter.setValue(0, p);
			parameterList.add(parameter);
			return parameterList;
		case HallwayMaxQGraph.TASK_TO_WALL:
			parameterList.add(maxParameter);
			return parameterList;
		case HallwayMaxQGraph.TASK_BACK_ONE:
			parameter = new Parameter(1);
			d = (int)maxParameter.getValue(0);
			parameter.setValue(0, (d+2)%4);
			parameterList.add(parameter);
			return parameterList;
		case HallwayMaxQGraph.TASK_PERP_THREE:
			parameter = new Parameter(1);
			p = (int)maxParameter.getValue(0);
			parameter.setValue(0, p);
			parameterList.add(parameter);
			return parameterList;
		default:
			return Parameter.getNilArrayList();
		}
	}

	@Override
	public ArrayList<Parameter> getMaxParameters(State state, QNode qNode) {
		Parameter qParameter = qNode.getParameter();
		ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
		Parameter parameter;
		switch (qNode.name()) {
		case HallwayMaxQGraph.Q_GO:
			parameter = new Parameter(2);
			parameter.setValue(0, qParameter.getValue(0));
			parameter.setValue(1, state.getValue(HallwayEnv.STATE_ROOM_NUMBER));
			parameterList.add(parameter);
			return parameterList;
		case HallwayMaxQGraph.Q_EXIT_INTER:
		case HallwayMaxQGraph.Q_EXIT_HALL:
		case HallwayMaxQGraph.Q_SNIFF_EI:
		case HallwayMaxQGraph.Q_SNIFF_EH:
			parameterList.add(qParameter);
			return parameterList;
		case HallwayMaxQGraph.Q_BACK_EI:
		case HallwayMaxQGraph.Q_BACK_EH:
			parameter = new Parameter(4);
			parameter.setValue(0, qParameter.getValue(0));
			parameter.setValue(1, qParameter.getValue(1));
			parameter.setValue(2, state.getValue(HallwayEnv.STATE_ROOM_LOCX));
			parameter.setValue(3, state.getValue(HallwayEnv.STATE_ROOM_LOCY));
			parameterList.add(parameter);
			return parameterList;
		case HallwayMaxQGraph.Q_FOLLOW_WALL:
		case HallwayMaxQGraph.Q_TO_WALL:
		case HallwayMaxQGraph.Q_BACK_ONE:
		case HallwayMaxQGraph.Q_PERP_THREE:
			parameterList.add(qParameter);
			return parameterList;
		case HallwayMaxQGraph.Q_MOVE_FW:
		case HallwayMaxQGraph.Q_MOVE_TW:
		case HallwayMaxQGraph.Q_MOVE_P3:
		case HallwayMaxQGraph.Q_MOVE_BO:
			parameterList.add(qParameter);
			return parameterList;
		default:
			return Parameter.getNilArrayList();
		}
	}

	@Override
	public State maxStateAbsMapping(State state, MaxNode maxNode) {
		int roomNo = (int)state.getValue(HallwayEnv.STATE_ROOM_NUMBER);
		switch (maxNode.name()) {
		case HallwayMaxQGraph.TASK_MOVE:
			if(roomNo!=24 && roomNo!=28 && roomNo!=29) {
				state = new State(1);
				state.setValue(0, 1);
			} else {
				int locX = (int)state.getValue(HallwayEnv.STATE_ROOM_LOCX);
				int locY = (int)state.getValue(HallwayEnv.STATE_ROOM_LOCY);
				if((roomNo==24 && locX<11) || (roomNo==28 && locY<11)) {
					state = new State(1);
					state.setValue(0, 1);
				}
			}
			break;
		default:
			break;
		}
		return state;
	}

	@Override
	public State qStateAbsMapping(State state, QNode qNode) {
		return state;
//		int roomNo = (int)state.getValue(HallwayEnv.STATE_ROOM_NUMBER);
//		switch (qNode.name()) {
//		case HallwayMaxQGraph.Q_GO:
//			int d = (int)qNode.getParameter().getValue(0);
//			int r = (int)qNode.getChild().getParameter().getValue(1);
//			int interNo = HallwayEnv.findInter(r, d);
//			if(interNo==-1)
//				System.err.println("Illegal Q_GO: " + roomNo + " " + d);
//			state = new State(1);
//			state.setValue(0, interNo);
//			break;
//		case HallwayMaxQGraph.Q_MOVE_BO:
//		case HallwayMaxQGraph.Q_EXIT_HALL:
//		case HallwayMaxQGraph.Q_MOVE_P3:
//			// termination condition for state abstract
//			state = new State(0);
//			break;
//		default:
//			break;
//		}
//		return state;
	}
	
	@Override
	public State eStateAbsMapping(State state, QNode qNode) {
		return state;
	}

	@Override
	public boolean singleExit(MaxNode maxNode) {
		return false;
	}



}
