package edu.cwru.eecs.rl.env;

import java.util.ArrayList;


import com.google.common.collect.HashBiMap;

import edu.cwru.eecs.futil.Point;
import edu.cwru.eecs.rl.core.mdp.Action;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.domains.DomainEnum;


/**
 * This is a simple environment to test the difference between 
 * hierarchically optimal policy and recursively optimal policy. 
 * 
 * This is a 6*7 grid world. With a wall between fourth and fifth column. 
 * There are two doors on the wall.
 * 
 *  Y
 *  ^
 *  | _ _ _ _ _ _
 *  6|       |  G|
 *  5| 	   		 |
 *  4|       |   |
 *  3|       |   |
 *  2|       |   |
 *  1|           |
 *  0|_ _ _ _|_ _|
 *    0 1 2 3 4 5  -> X
 *    
 * @author feng
 *
 */
public class SimpleMazeEnv extends Environment {

	private static final long serialVersionUID = 7244793342937524419L;
	
	private static final int X_LENGTH = 6;
	private static final int Y_LENGTH = 7;
	
	private static final int ACTION_NORTH = 0;
	private static final int ACTION_EAST = 2;
	private static final int ACTION_SOUTH = 1;
	//private static final int ACTION_WEST = 3;
	
	static {
		STATE_DIMENSION = 2;
		ACTION_NUMBER = 3;
		ACTION_NAME = new String[ACTION_NUMBER];
		ACTION_NAME[ACTION_NORTH] = "NORTH";
		ACTION_NAME[ACTION_EAST] = "EAST";
		ACTION_NAME[ACTION_SOUTH] = "SOUTH";
		//ACTION_NAME[ACTION_WEST] = "WEST";
	}
	
	private static final double REWARD_MOVE = -1;
	private static final double REWARD_GOAL = 0;  // don't really need this
	
	// environment configuration
	private static Point m_goal;
	private static ArrayList<Point> m_northWalls;
	private static ArrayList<Point> m_southWalls;
	private static ArrayList<Point> m_eastWalls;
	private static ArrayList<Point> m_westWalls;
	
	
	// environment spec
	private double m_noise = 0.00;
	private boolean m_randomStart = true;
	
	// Game status
	private Point m_location;
	

	protected SimpleMazeEnv() {
		super(DomainEnum.SimpleMaze.toString(), 1.0d);
		m_location = new Point(0, 0);
	}
	
	public SimpleMazeEnv(String[] args) {
		this();
		m_noise = (args.length<2) ? 0 : Double.parseDouble(args[1]);
	}
	
	public static String getUsage() {
		return "arg1: noise";
	}
	
	@Override
	public String getDescription() {
		String desp = super.getDescription();
		if(m_noise!=0)
			desp += "-n" + m_noise;
		return desp;
	}

	@Override
	public void start() {
		// randomly start
		// state
		if(m_randomStart) {
			m_location.randomize(X_LENGTH, Y_LENGTH);
			while(m_location.equals(m_goal))
				m_location.randomize(X_LENGTH, Y_LENGTH);
		}
		else {
			m_location = new Point(2, 2);
		}
		// reward
		m_reward = 0;
		// is terminal
		m_isTerminal = false;
		
		if(m_debug)
			System.out.println("Start Two Door Maze From -- " + m_location);
	}

	@Override
	public boolean takeAction(Action action) {
		int a = (int)action.getAction();
		
		if(m_debug)
			System.out.println("Action: " + ACTION_NAME[a]);
		
		// add noise to the action
		double random = Math.random();
		if(random < m_noise) { // right perpendicular direction
			a = (a+1)%ACTION_NUMBER;
		}
		else if(random < 2*m_noise) {
			a = (a+2)%ACTION_NUMBER;
		}
		
		// take action a
		double r = REWARD_MOVE;
		switch (a) {
		case ACTION_NORTH:
			if(!m_northWalls.contains(m_location))
				m_location.m_y++;
			break;
		case ACTION_SOUTH:
			if (!m_southWalls.contains(m_location))
				m_location.m_y--;
			break;
		case ACTION_EAST:
			if (!m_eastWalls.contains(m_location))
				m_location.m_x++;
			break;
		default:
			System.out.println("Invalid action: " + a);
			break;
		}
		if(m_location.equals(m_goal)) {
			r += REWARD_GOAL;
			m_isTerminal = true;
		}
		m_reward = r;
		return true;
	}

	@Override
	public State getState() {
		return genState(m_location);
	}
	
	private State genState(Point location) {
		State state = new State(2);
		state.setValue(0, location.getX());
		state.setValue(1, location.getY());
		return state;
	}
	
	public String message(String message) {
		String[] messages = message.split(",");
		
		if (messages[0].trim().equals("noise")){
			m_noise = Double.parseDouble(messages[1].trim());
			return "set noise " + messages[1].trim();
		}
		if (messages[0].trim().equals("random start")) {
			m_randomStart = true;
			if(messages[1].trim().equals("on")) {
				m_randomStart = true;
				return "set random start on";
			} else if(messages[1].trim().equals("off")) {
				m_randomStart = false;
				return "set random start off";
			}
		}
		return super.message(message);
	}

	@Override
	public HashBiMap<State, Integer> getStartingStateSet() {
		return getAllStateSet();
	}

	@Override
	public HashBiMap<State, Integer> getAllStateSet() {
		HashBiMap<State, Integer> allStateSet = HashBiMap.create();
		int count = 0;
		for(int x=0; x<X_LENGTH; x++) {
			for(int y=0; y<Y_LENGTH; y++) {
				State state = new State(2);
				state.setValue(0, x);
				state.setValue(1, y);
				allStateSet.put(state, count);
				count++;
			}
		}
		return allStateSet;
	}

	@Override
	public HashBiMap<Action, Integer> getAllActionSet() {
		int size = m_envSpec.getNumAction();
		HashBiMap<Action, Integer> allActionSet = HashBiMap.create();
		for (int a = 0; a < size; a++) {
			Action action = new Action(1);
			action.setAction(a);
			allActionSet.put(action, a);
		}
		return allActionSet;
	}


	
	public static State goalState() {
		State goalState = new State(2);
		goalState.setValue(0, m_goal.getX());
		goalState.setValue(1, m_goal.getY());
		return goalState;
	}
	
	public static int exitX() {
		return 4;
	}
	
	public static int exitYGoal() {
		return 5;
	}
	
	// set the environment configuration
	static {
		m_goal = new Point(5, 6);
		
		m_northWalls = new ArrayList<Point>(6);
		m_northWalls.add(new Point(0, 6));
		m_northWalls.add(new Point(1, 6));
		m_northWalls.add(new Point(2, 6));
		m_northWalls.add(new Point(3, 6));
		m_northWalls.add(new Point(4, 6));
		m_northWalls.add(new Point(5, 6));
		
		m_southWalls = new ArrayList<Point>(6);
		m_southWalls.add(new Point(0, 0));
		m_southWalls.add(new Point(1, 0));
		m_southWalls.add(new Point(2, 0));
		m_southWalls.add(new Point(3, 0));
		m_southWalls.add(new Point(4, 0));
		m_southWalls.add(new Point(5, 0));
		
		m_eastWalls = new ArrayList<Point>(12);
		m_eastWalls.add(new Point(5, 6));
		m_eastWalls.add(new Point(5, 5));
		m_eastWalls.add(new Point(5, 4));
		m_eastWalls.add(new Point(5, 3));
		m_eastWalls.add(new Point(5, 2));
		m_eastWalls.add(new Point(5, 1));
		m_eastWalls.add(new Point(5, 0));
		m_eastWalls.add(new Point(3, 6));
		m_eastWalls.add(new Point(3, 4));
		m_eastWalls.add(new Point(3, 3));
		m_eastWalls.add(new Point(3, 2));
		m_eastWalls.add(new Point(3, 0));
		
		m_westWalls = new ArrayList<Point>(12);
		m_westWalls.add(new Point(0, 6));
		m_westWalls.add(new Point(0, 5));
		m_westWalls.add(new Point(0, 4));
		m_westWalls.add(new Point(0, 3));
		m_westWalls.add(new Point(0, 2));
		m_westWalls.add(new Point(0, 1));
		m_westWalls.add(new Point(0, 0));
		m_westWalls.add(new Point(4, 6));
		m_westWalls.add(new Point(4, 4));
		m_westWalls.add(new Point(4, 3));
		m_westWalls.add(new Point(4, 2));
		m_westWalls.add(new Point(4, 0));
	}
}
