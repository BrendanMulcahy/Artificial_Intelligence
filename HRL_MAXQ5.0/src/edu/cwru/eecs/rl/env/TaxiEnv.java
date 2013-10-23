package edu.cwru.eecs.rl.env;

import java.util.ArrayList;


import com.google.common.collect.HashBiMap;

import edu.cwru.eecs.futil.Point;
import edu.cwru.eecs.rl.core.mdp.Action;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.domains.DomainEnum;


public class TaxiEnv extends Environment {

	private static final long serialVersionUID = -1576623957791441826L;

	private static final int WORLDSIZE = 5;

	public static final int STATE_LOCX = 0;
	public static final int STATE_LOCY = 1;
	public static final int STATE_SOURCE = 2;
	public static final int STATE_DEST = 3;
	
	private static final int ACTION_NORTH = 0;
	private static final int ACTION_EAST = 1;
	private static final int ACTION_WEST = 2;
	private static final int ACTION_SOUTH = 3;
	private static final int ACTION_PICKUP = 4;
	private static final int ACTION_PUTDOWN = 5;
	
	
	private static final double REWARD_ACTION = -1;
	private static double REWARD_DELIVERY = 10;
	private static final double REWARD_ERROR = -10;

	public static final int SITE_NAME_RED = 0;
	public static final int SITE_NAME_GREEN = 1;
	public static final int SITE_NAME_BLUE = 2;
	public static final int SITE_NAME_YELLOW = 3;
	public static final int SITE_NAME_HELD = 4;
	public static final int SITE_NAME_NONE = 5;
	

	public static final int GAME_STATUS_ACTIVE = 0;
	public static final int GAME_STATUS_INACTIVE = 1;

	// environment configuration
	private static ArrayList<Point> SITES;
	private static ArrayList<Point> NORTH_WALLS;
	private static ArrayList<Point> SOUTH_WALLS;
	private static ArrayList<Point> EAST_WALLS;
	private static ArrayList<Point> WEST_WALLS;
	
	// environment spec
	private double m_noise = 0;
	private boolean m_randomStart = true;

	// Game status
	private Point m_location;
	private int m_source;
	private int m_destination;
	
	@SuppressWarnings("unused")
	private boolean m_fickleFlag;  // change destination when picked up the passenger.

	
	protected TaxiEnv() {
		super(DomainEnum.Taxi.toString(), 1.0d);
		m_location = new Point(0, 0);
	}
	
	public TaxiEnv(String[] args) {
		this();
		m_noise = (args.length<2) ? 0 : Double.parseDouble(args[1]);
		REWARD_DELIVERY = (args.length<3) ? 10 : Double.parseDouble(args[2]);
		//System.out.println("Start: " + args[3]);
		m_randomStart = (args.length<4) ? true : args[3].equals("true");
		//System.out.println("Real: " + m_randomStart);
	}
	
	public static String getUsage() {
		return "arg2: noise finalreward randomstart";
	}
	
	@Override
	public String getDescription() {
		String desp = super.getDescription() + "-";
		if(m_noise!=0)
			desp += "n" + m_noise;
		desp += "r" + (int)REWARD_DELIVERY + (m_randomStart?"":"fixstart");
		return desp;
	}

	@Override
	public void start() {
		// randomly start
		// state
		if (m_randomStart) {
			m_location.randomize(WORLDSIZE, WORLDSIZE);
			m_source = (int) (Math.random() * 4);
			m_destination = (int) (Math.random() * 4);
		} else {
			m_location = new Point(0, 3);
			m_source = 1;
			m_destination = 2;
		}
		// reward
		m_reward = 0;
		// is terminal
		m_isTerminal = false;

		m_fickleFlag = false;
		
		if (m_debug)
			System.out.println("Start Taxi World:\n" + "Location: "
					+ m_location.toString() + "\tSource: " + m_source
					+ "\tDestination: " + m_destination);

	}
	
//	private void checkFickle(int a) {
//		if(m_fickleFlag==true && a<4) {
//			// change destination
//			double random = Math.random();
//			if(random<0.3) {
//				random = Math.random();
//				int newDest = (int)(random*4);
//				m_destination = newDest;
//			}
//			m_fickleFlag = false;
//		}
//	}

	@Override
	public boolean takeAction(Action action) {
		int a = (int) action.getAction();
		//checkFickle(a);
		
		double random;
		// add noise to the action
		if (a < 4) {
			random = Math.random();
			if (random < m_noise) {
				if (a == 0)
					a = 1;
				else if (a == 1)
					a = 3;
				else if (a == 2)
					a = 0;
				else if (a == 3)
					a = 2;
			} else if (random < 2 * m_noise) {
				if (a == 0)
					a = 2;
				else if (a == 1)
					a = 0;
				else if (a == 2)
					a = 3;
				else if (a == 3)
					a = 1;
			}
		}
		// take action a
		double r = REWARD_ACTION;
		switch (a) {
		case ACTION_NORTH:
			if (!NORTH_WALLS.contains(m_location))
				m_location.m_y++;
			break;
		case ACTION_SOUTH:
			if (!SOUTH_WALLS.contains(m_location))
				m_location.m_y--;
			break;
		case ACTION_EAST:
			if (!EAST_WALLS.contains(m_location))
				m_location.m_x++;
			break;
		case ACTION_WEST:
			if (!WEST_WALLS.contains(m_location))
				m_location.m_x--;
			break;
		case ACTION_PICKUP:
			if (m_source == SITE_NAME_HELD) // already holding passenger
				r = REWARD_ERROR;
			else if (m_source == SITE_NAME_NONE) // no passenger to pick up
				r = REWARD_ERROR;
			else if (m_location.equals(SITES.get(m_source))) {
				m_source = SITE_NAME_HELD;
				m_fickleFlag = true;
			} else { // not at source
				r = REWARD_ERROR;
			}
			break;
		case ACTION_PUTDOWN:
			if (m_source != SITE_NAME_HELD) // not holding a passenger
				r = REWARD_ERROR;
			else if (m_location.equals(SITES.get(m_destination))) // reach
				// goal
			{
				r = REWARD_DELIVERY;
				m_source = SITE_NAME_NONE;
				m_isTerminal = true;
			} else { // not at destination
				if(SITES.contains(m_location)) { // put down in a wrong place
					m_source = SITES.indexOf(m_location);
				}
				else
					r = REWARD_ERROR;
			}
			break;
		default:
			System.out.println("Invalid action: " + a);
			break;
		}
		m_reward = r;
		
		if(m_debug)
			System.out.println(genState(m_location, m_source, m_destination)+ "\tAction: " + ACTION_NAME[a] + "\tReward: " + m_reward);
		
		return true;
	}

	@Override
	public State getState() {
		return genState(m_location, m_source, m_destination);
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
		//int size = WORLDSIZE * WORLDSIZE * 4 * 4;
		HashBiMap<State, Integer> startingStateSet = HashBiMap.create();
		int count = 0;
		for (int x = 0; x < WORLDSIZE; x++) {
			for (int y = 0; y < WORLDSIZE; y++) {
				for (int src = 0; src < 4; src++) {
					for (int dest = 0; dest < 4; dest++) {
						State state = new State(4);
						state.setValue(0, x);
						state.setValue(1, y);
						state.setValue(2, src);
						state.setValue(3, dest);
						startingStateSet.put(state, count);
						count++;
					}
				}
			}
		}

		return startingStateSet;
	}

	@Override
	public HashBiMap<State, Integer> getAllStateSet() {
		//int size = WORLDSIZE * WORLDSIZE * 6 * 4;
		HashBiMap<State, Integer> allStateSet = HashBiMap.create();
		int count = 0;
		for (int x = 0; x < WORLDSIZE; x++) {
			for (int y = 0; y < WORLDSIZE; y++) {
				for (int src = 0; src < 6; src++) {
					for (int dest = 0; dest < 4; dest++) {
						State state = new State(4);
						state.setValue(0, x);
						state.setValue(1, y);
						state.setValue(2, src);
						state.setValue(3, dest);
						allStateSet.put(state, count);
						count++;
					}
				}
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
	
	
	private State genState(Point location, int source, int destination) {
		State state = new State(4);
		state.setValue(0, location.getX());
		state.setValue(1, location.getY());
		state.setValue(2, source);
		state.setValue(3, destination);
		return state;
	}
	
	public static Point getSite(int site) {
		return SITES.get(site);
	}
	
	public static boolean containSite(Point loc) {
		return SITES.contains(loc);
	}
	
	// set the environment configuration
	static {
		SITES = new ArrayList<Point>(4);
		SITES.add(new Point(0, 4)); // Red
		SITES.add(new Point(4, 4)); // Green
		SITES.add(new Point(3, 0)); // Blue
		SITES.add(new Point(0, 0)); // Yellow

		NORTH_WALLS = new ArrayList<Point>(5);
		NORTH_WALLS.add(new Point(0, 4));
		NORTH_WALLS.add(new Point(1, 4));
		NORTH_WALLS.add(new Point(2, 4));
		NORTH_WALLS.add(new Point(3, 4));
		NORTH_WALLS.add(new Point(4, 4));

		SOUTH_WALLS = new ArrayList<Point>(5);
		SOUTH_WALLS.add(new Point(0, 0));
		SOUTH_WALLS.add(new Point(1, 0));
		SOUTH_WALLS.add(new Point(2, 0));
		SOUTH_WALLS.add(new Point(3, 0));
		SOUTH_WALLS.add(new Point(4, 0));

		EAST_WALLS = new ArrayList<Point>(11);
		EAST_WALLS.add(new Point(4, 4));
		EAST_WALLS.add(new Point(4, 3));
		EAST_WALLS.add(new Point(4, 2));
		EAST_WALLS.add(new Point(4, 1));
		EAST_WALLS.add(new Point(4, 0));
		EAST_WALLS.add(new Point(2, 1));
		EAST_WALLS.add(new Point(2, 0));
		EAST_WALLS.add(new Point(1, 4));
		EAST_WALLS.add(new Point(1, 3));
		EAST_WALLS.add(new Point(0, 1));
		EAST_WALLS.add(new Point(0, 0));

		WEST_WALLS = new ArrayList<Point>(11);
		WEST_WALLS.add(new Point(3, 1));
		WEST_WALLS.add(new Point(3, 0));
		WEST_WALLS.add(new Point(2, 4));
		WEST_WALLS.add(new Point(2, 3));
		WEST_WALLS.add(new Point(1, 1));
		WEST_WALLS.add(new Point(1, 0));
		WEST_WALLS.add(new Point(0, 4));
		WEST_WALLS.add(new Point(0, 3));
		WEST_WALLS.add(new Point(0, 2));
		WEST_WALLS.add(new Point(0, 1));
		WEST_WALLS.add(new Point(0, 0));
	}
	
	static {
		STATE_DIMENSION = 4;
		ACTION_NUMBER = 6;
		ACTION_NAME = new String[ACTION_NUMBER];
		ACTION_NAME[ACTION_NORTH] = "NORTH";
		ACTION_NAME[ACTION_EAST] = "EAST";
		ACTION_NAME[ACTION_WEST] = "WEST";
		ACTION_NAME[ACTION_SOUTH] = "SOUTH";
		ACTION_NAME[ACTION_PICKUP] = "PICKUP";
		ACTION_NAME[ACTION_PUTDOWN] = "PUTDOWN";
	}

}
