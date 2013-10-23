package edu.cwru.eecs.rl.env;

import com.google.common.collect.HashBiMap;

import edu.cwru.eecs.futil.Point;
import edu.cwru.eecs.rl.core.mdp.Action;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.domains.DomainEnum;

public class FourDoorMazeEnv extends Environment {

	/**
	 * left-bottom is (0, 0)
	 */
	
	private static final long serialVersionUID = 7653868401795220877L;

	private static final int X_LENGTH = 10;
	private static final int Y_LENGTH = 10;
	
	private static final int ACTION_NORTH = 0;
	private static final int ACTION_EAST = 1;
	private static final int ACTION_SOUTH = 2;
	private static final int ACTION_WEST = 3;
	
	public static final int ROOM_1 = 1;
	public static final int ROOM_2 = 2;
	public static final int ROOM_3 = 3;
	public static final int ROOM_4 = 4;
	public static final int ROOM_COMMON = 0;
	
	static {
		STATE_DIMENSION = 2;
		ACTION_NUMBER = 4;
		ACTION_NAME = new String[ACTION_NUMBER];
		ACTION_NAME[ACTION_NORTH] = "NORTH";
		ACTION_NAME[ACTION_EAST] = "EAST";
		ACTION_NAME[ACTION_SOUTH] = "SOUTH";
		ACTION_NAME[ACTION_WEST] = "WEST";
	}
	
	private static final double REWARD_MOVE = -1;
	private static final double REWARD_GOAL = 0;
	
	// environment configuration
	private static Point[] m_criticalPoints;
	static {
		m_criticalPoints = new Point[5];
		m_criticalPoints[0] = new Point(1, 1);
		m_criticalPoints[1] = new Point(9, 1);
		m_criticalPoints[2] = new Point(9, 9);
		m_criticalPoints[3] = new Point(1, 9);
	}
	
	// environment spec
	private double m_noise = 0.00;
	private boolean m_randomStart = true;
	
	// game status
	private int m_x;
	private int m_y;
	private Point m_startPoint;
	private Point m_goalPoint;
	
	public FourDoorMazeEnv() {
		super(DomainEnum.FourDoorMaze.toString(), 1.0d);
	}

	public FourDoorMazeEnv(String[] args) {
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
		int ran = 0;
		if(m_randomStart)
			ran = (int)(Math.random()*4);
		m_startPoint = m_criticalPoints[ran];
		m_goalPoint = m_criticalPoints[(ran+2)%4];
		m_x = m_startPoint.m_x;
		m_y = m_startPoint.m_y;
		m_isTerminal = false;
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
			if(m_y==9 || (m_y==4 && (m_x<=2 || m_x>=7)) 
					|| (m_y==6 && (m_x>=4 && m_x<=6))
					|| (m_y==2 && (m_x>=3 && m_x<=5)))
				break;
			else {
				m_y++;
			}
			break;
		case ACTION_EAST:
			if(m_x==9 || (m_x==4 && (m_y<=2 || m_y>=7))
					|| (m_x==6 && (m_y>=3 && m_y<=5))
					|| (m_x==2 && (m_y>=4 && m_y<=6)))
				break;
			else {
				m_x++;
			}
			break;
		case ACTION_SOUTH:
			if(m_y==0 || (m_y==5 && (m_x<=2 || m_x>=7))
					|| (m_y==3 && (m_x>=3 && m_x<=5))
					|| (m_y==7 && (m_x>=4 && m_x<=6)))
				break;
			else {
				m_y--;
			}
			break;
		case ACTION_WEST:
			if(m_x==0 || (m_x==5 && (m_y<=2 || m_y>=7))
					|| (m_x==3 && (m_y>=4 && m_y<=6))
					|| (m_x==7 && (m_y>=3 && m_x<=5)))
				break;
			else {
				m_x--;
			}
			break;
		default:
			System.out.println("Invalid action: " + a);
			break;
		}
		if(m_goalPoint.m_x==m_x && m_goalPoint.m_y==m_y) {
			r += REWARD_GOAL;
			m_isTerminal = true;
		}
		m_reward = r;
		return true;
	}

	@Override
	public State getState() {
		return genState(m_x, m_y);
	}
	
	private State genState(int x, int y) {
		State state = new State(2);
		state.setValue(0, x);
		state.setValue(1, y);
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
		HashBiMap<State, Integer> startStateSet = HashBiMap.create();
		for(int i=0; i<m_criticalPoints.length; i++) {
			State state = new State(2);
			state.setValue(0, m_criticalPoints[i].m_x);
			state.setValue(1, m_criticalPoints[i].m_y);
			startStateSet.put(state, i);
		}
		return startStateSet;
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

	public static int roomNo(State state) {
		return roomNo((int)state.getValue(0), (int)state.getValue(1));
	}
	
	public static int roomNo(int x, int y) {
		if(x>=3 && x<=6 && y>=3 && y<=6)
			return ROOM_COMMON;
		else if(x>=0 && x<=4 && y>=0 && y<=4)
			return ROOM_1;
		else if(x>=5 && x<=9 && y>=0 && y<=4)
			return ROOM_2;
		else if(x>=5 && x<=9 && y>=5 && y<=9)
			return ROOM_3;
		else if(x>=0 && x<=4 && y>=5 && y<=9)
			return ROOM_4;
		return -1;
	}
}
