package edu.cwru.eecs.rl.env;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashBiMap;

import edu.cwru.eecs.futil.Point;
import edu.cwru.eecs.rl.core.mdp.Action;
import edu.cwru.eecs.rl.core.mdp.Parameter;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.domains.DomainEnum;

/**
 * This is a very simple simulation of Wargus Environment for Resource Collection Task
 * There are a couple of GoldMines, Forests and a TownHall.
 * The peasant (controlled by agent) is supposed to collect a number of gold and wood from
 * gold mines and forests, respectively, and put them in the town hall. 
 * 
 * @author Feng
 *
 */
public class WargusEnv extends Environment {

	private static final long serialVersionUID = 6019514889786696279L;
	
	protected static final int WORLDSIZE = 25;
	
	public static final int STATE_LOCX = 0;
	public static final int STATE_LOCY = 1;
	public static final int STATE_RESOURCE = 2;
	public static final int STATE_GOLD_IN_REGION = 3;
	public static final int STATE_WOOD_IN_REGION = 4;
	public static final int STATE_TOWNHALL_IN_REGION = 5;
	public static final int STATE_MEET_GOLD_REQ = 6;
	public static final int STATE_MEET_WOOD_REQ = 7;
	

	public static final int ACTION_MINEGOLD = 0;
	public static final int ACTION_CHOPWOOD = 1;
	public static final int ACTION_DEPOSIT = 2;
	public static final int ACTION_GOTO = 3; // with parameter
	
	public static final String[] ACTION_NAME;

	private static final double REWARD_ENERGY_PER_ACTION = -1;
	private static final double REWARD_PER_MOVE = -0.5;
	private static final double REWARD_MINEGOLD = -1;
	private static final double REWARD_CHOPWOOD = -1;
	private static final double REWARD_DEPOSIT = -1;
	private static final double REWARD_ERROR = -1;
	private static final double REWARD_FINISH_ONE = 50;

	public static final int RESOURCE_NONE = 0;
	public static final int RESOURCE_GOLD = 1;
	public static final int RESOURCE_WOOD = 2;

	// Environment configuration
	protected static int MAX_REQUIRED_GOLD = 1;
	protected static int MAX_REQUIRED_WOOD = 1;
	protected static int MAX_GOLD_PER_MINE = 2;
	protected static int MAX_WOOD_PER_TREE = 2;

	private static final ArrayList<Point> m_forestLocations;
	private static final ArrayList<Point> m_goldMinesLocations;
	private static final Point m_townHallLocation;
	private static final ArrayList<Point> m_allLocationSet;
	private static final int m_numLocation;
	private static final ArrayList<Parameter> m_allPositionsList;

	static {
		m_forestLocations = new ArrayList<Point>(2);
		m_forestLocations.add(new Point(18, 10));
		m_forestLocations.add(new Point(10, 20));

		m_goldMinesLocations = new ArrayList<Point>(2);
		m_goldMinesLocations.add(new Point(10, 5));
		m_goldMinesLocations.add(new Point(5, 15));

		m_townHallLocation = new Point(23, 20);

		Forest forest = new Forest(m_forestLocations);
		GoldMines goldMines = new GoldMines(m_goldMinesLocations);
		TownHall townHall = new TownHall(m_townHallLocation);
		Set<Point> allLocationSet = new HashSet<Point>();
		allLocationSet.addAll(forest.getChopableLocations());
		allLocationSet.addAll(goldMines.getMinableLocations());
		allLocationSet.addAll(townHall.getDepositableLocations());
		m_allLocationSet = new ArrayList<Point>(allLocationSet);
		m_numLocation = m_allLocationSet.size();
		
		m_allPositionsList = new ArrayList<Parameter>();
		for (int i = 0; i < m_numLocation; i++) {
			Point tempPoint = m_allLocationSet.get(i);
			Parameter tempParameter = new Parameter(2);
			tempParameter.setValue(0, tempPoint.getX());
			tempParameter.setValue(1, tempPoint.getY());
			m_allPositionsList.add(tempParameter);
		}
		
		STATE_DIMENSION = 8;
		ACTION_NUMBER = 3+m_numLocation;
		ACTION_NAME = new String[ACTION_NUMBER];
		ACTION_NAME[ACTION_MINEGOLD] = "MINEGOLD";
		ACTION_NAME[ACTION_CHOPWOOD] = "CHOPWOOD";
		ACTION_NAME[ACTION_DEPOSIT] = "DEPOSIT";
		ACTION_NAME[ACTION_GOTO] = "GOTO";
	}

	// environment spec
	private double m_noise = 0;

	// Game status
	private Point m_location;
	private int m_resource;
	private boolean m_goldInRegion;
	private boolean m_woodInRegion;
	private boolean m_townhallInRegion;
	private boolean m_meetGoldReq; // true if meet gold requirement
	private boolean m_meetWoodReq; // true if meet wood requirement

	// Inner state
	private Forest m_forest;
	private GoldMines m_goldMines;
	private TownHall m_townHall;

	protected WargusEnv() {
		super(DomainEnum.Wargus.toString(), 1.0d);
		m_noise = 0;
		MAX_REQUIRED_GOLD = 1;
		MAX_REQUIRED_WOOD = 1;
		MAX_GOLD_PER_MINE = 2;
		MAX_WOOD_PER_TREE = 2;
	}
	
	public WargusEnv(String[] args) {
		this();
		Integer.parseInt(args[1]); // make sure that each character in args[2] is digit
		MAX_REQUIRED_GOLD = args[1].charAt(0)-'0';
		MAX_REQUIRED_WOOD = args[1].charAt(1)-'0';
		MAX_GOLD_PER_MINE = args[1].charAt(2)-'0';
		MAX_WOOD_PER_TREE = args[1].charAt(3)-'0';
		m_noise = (args.length<3) ? 0 : Double.parseDouble(args[2]);
	}
	
	public static String getUsage() {
		return "arg1: config (e.g. 3322), arg2: noise";
	}
	
	@Override
	public String getDescription() {
		String desp = super.getDescription();
		desp += MAX_REQUIRED_GOLD+""+MAX_REQUIRED_WOOD+""+MAX_GOLD_PER_MINE+""+MAX_WOOD_PER_TREE;
		if(m_noise!=0)
			desp += "-n" + m_noise;
		return desp;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void start() {
		// randomly start
		// state
		m_location = m_allLocationSet.get((int) (Math.random() * m_numLocation));
		m_resource = RESOURCE_NONE;

		m_forest = new Forest((ArrayList<Point>) m_forestLocations.clone());
		m_goldMines = new GoldMines(
				(ArrayList<Point>) m_goldMinesLocations.clone());
		m_townHall = new TownHall(m_townHallLocation);

		m_goldInRegion = m_goldMines.isGoldMineInRange(m_location);
		m_woodInRegion = m_forest.isForestInRegion(m_location);
		m_townhallInRegion = m_townHall.isTownHallInRange(m_location);
		m_meetGoldReq = m_townHall.meetGoldReq();
		m_meetWoodReq = m_townHall.meetWoodReq();

		// reward
		m_reward = 0;
		// is terminal
		m_isTerminal = m_townHall.meetRequirement();

		if (m_debug) {
			System.out.println("Start Wargus Game:\n" + "Location: "
					+ m_location.toString());
		}
	}

	@Override
	public boolean takeAction(Action action) {
		int actionIndex = (int)action.getValue(0);
		if (m_debug)
			System.out.println("Action: " + actionIndex);

		// add randomness to the goto action
		if(actionIndex==ACTION_GOTO) {
			double random = Math.random();
			if(random<m_noise){
				int newDest = (int)(Math.random()*m_numLocation);
				Point newDestPoint = m_allLocationSet.get(newDest);
				Parameter parameter = new Parameter(2);
				parameter.setValue(0, newDestPoint.getX());
				parameter.setValue(1, newDestPoint.getY());
				action = new Action(ACTION_GOTO, parameter);
			}
		}
		
		// take action
		double r = REWARD_ERROR + REWARD_ENERGY_PER_ACTION;
		switch (actionIndex) {
		case ACTION_MINEGOLD:
			if (m_goldMines.isGoldMineInRange(m_location)
					&& m_resource == RESOURCE_NONE) {
				if (m_goldMines.mineGold(m_location)) {
					m_resource = RESOURCE_GOLD;
					m_goldInRegion = m_goldMines.isGoldMineInRange(m_location);
					r = REWARD_MINEGOLD + REWARD_ENERGY_PER_ACTION;
				}
			}
			break;
		case ACTION_CHOPWOOD:
			if (m_forest.isForestInRegion(m_location)
					&& m_resource == RESOURCE_NONE) {
				if (m_forest.chopWood(m_location)) {
					m_resource = RESOURCE_WOOD;
					m_woodInRegion = m_forest.isForestInRegion(m_location);
					r = REWARD_CHOPWOOD + REWARD_ENERGY_PER_ACTION;
				}
			}
			break;
		case ACTION_DEPOSIT:
			if (m_townHall.isTownHallInRange(m_location)
					&& m_resource != RESOURCE_NONE) {
				if (m_townHall.deposit(m_location, m_resource)) {
					r = REWARD_DEPOSIT + REWARD_ENERGY_PER_ACTION;
					m_resource = RESOURCE_NONE;
					if(m_meetGoldReq!=m_townHall.meetGoldReq() || m_meetWoodReq!=m_townHall.meetWoodReq())
						r += REWARD_FINISH_ONE;
					m_meetGoldReq = m_townHall.meetGoldReq();
					m_meetWoodReq = m_townHall.meetWoodReq();
					m_isTerminal = m_townHall.meetRequirement();
				}
			}
			break;
		case ACTION_GOTO:
			int x = (int)action.getValue(1);
			int y = (int)action.getValue(2);
			Point loc = new Point(x, y);
			if (!m_allLocationSet.contains(loc)) {
				System.out.println("Invalid loc for GOTO: " + loc);
				break;
			}
			r = REWARD_PER_MOVE * Point.manhattanDistance(m_location, loc) + REWARD_ENERGY_PER_ACTION;
			m_location = loc;
			m_goldInRegion = m_goldMines.isGoldMineInRange(m_location);
			m_woodInRegion = m_forest.isForestInRegion(m_location);
			m_townhallInRegion = m_townHall.isTownHallInRange(m_location);
			break;
		default:
			System.out.println("Invalid action: " + actionIndex);
			break;
		}
		m_reward = r;
		return true;
	}

	@Override
	public State getState() {
		return genState(m_location, m_resource, m_goldInRegion, m_woodInRegion,
				m_townhallInRegion, m_meetGoldReq, m_meetWoodReq);
	}
	
	@Override
	public String message(String message) {
		String[] messages = message.split(",");
		
		if (messages[0].trim().equals("noise")){
			m_noise = Double.parseDouble(messages[1].trim());
			return "set noise " + messages[1].trim();
		}
		
		return super.message(message);
	}

	@Override
	public HashBiMap<State, Integer> getStartingStateSet() {
		HashBiMap<State, Integer> startingStateSet = HashBiMap.create();
		int count = 0;
		for (int i = 0; i < m_numLocation; i++) {
			for (int resource = 0; resource < 3; resource++) {
				for (int goldInRegion = 0; goldInRegion < 2; goldInRegion++) {
					for (int woodInRegion = 0; woodInRegion < 2; woodInRegion++) {
						for (int townHallInRegion = 0; townHallInRegion < 2; townHallInRegion++) {
							for (int meetGoldReq = 0; meetGoldReq < 2; meetGoldReq++) {
								for (int meetWoodReq = 0; meetWoodReq < 2; meetWoodReq++) {
									State tempState = genState(
											m_allLocationSet.get(i), resource,
											goldInRegion, woodInRegion,
											townHallInRegion, meetGoldReq,
											meetWoodReq);
									startingStateSet.put(tempState, count);
									count++;
								}
							}
						}
					}
				}
			}
		}
		return startingStateSet;
	}

	@Override
	public HashBiMap<State, Integer> getAllStateSet() {
		HashBiMap<State, Integer> allStateSet = HashBiMap.create();
		int count = 0;
		for (int i = 0; i < m_numLocation; i++) {
			for (int resource = 0; resource < 3; resource++) {
				for (int goldInRegion = 0; goldInRegion < 2; goldInRegion++) {
					for (int woodInRegion = 0; woodInRegion < 2; woodInRegion++) {
						for (int townHallInRegion = 0; townHallInRegion < 2; townHallInRegion++) {
							for (int meetGoldReq = 0; meetGoldReq < 2; meetGoldReq++) {
								for (int meetWoodReq = 0; meetWoodReq < 2; meetWoodReq++) {
									if(goldInRegion+woodInRegion+townHallInRegion<=1 && (!(meetGoldReq+meetWoodReq==2 && townHallInRegion==0))) {
										State tempState = genState(
												m_allLocationSet.get(i), resource,
												goldInRegion, woodInRegion,
												townHallInRegion, meetGoldReq,
												meetWoodReq);
										allStateSet.put(tempState, count);
										count++;
									}
								}
							}
						}
					}
				}
			}
		}
		return allStateSet;
	}

	@Override
	public HashBiMap<Action, Integer> getAllActionSet() {
		HashBiMap<Action, Integer> allActionSet = HashBiMap.create();
		int count = 0;
		
		Action MG = new Action(ACTION_MINEGOLD);
		allActionSet.put(MG, count++);
		
		Action CW = new Action(ACTION_CHOPWOOD);
		allActionSet.put(CW, count++);
		
		Action Deposit = new Action(ACTION_DEPOSIT);
		allActionSet.put(Deposit, count++);
		
		for (int i = 0; i < m_numLocation; i++) {
			Point tempPoint = m_allLocationSet.get(i);
			Parameter tempParameter = new Parameter(2);
			tempParameter.setValue(0, tempPoint.getX());
			tempParameter.setValue(1, tempPoint.getY());
			Action tempGOTO = new Action(ACTION_GOTO, tempParameter);
			allActionSet.put(tempGOTO, count++);
		}
		
		return allActionSet;
	}
	
	public static ArrayList<Parameter> getAllPosition() {
		return m_allPositionsList;
	}
	
	private State genState(Point location, int resource, int goldInRegion,
			int woodInRegion, int townhallInRegion, int meetGoldReq,
			int meetWoodReq) {
		State state = new State(8);
		state.setValue(0, location.getX());
		state.setValue(1, location.getY());
		state.setValue(2, resource);
		state.setValue(3, goldInRegion);
		state.setValue(4, woodInRegion);
		state.setValue(5, townhallInRegion);
		state.setValue(6, meetGoldReq);
		state.setValue(7, meetWoodReq);
		return state;
	}
	
	private State genState(Point location, int resource, boolean goldInRegion,
			boolean woodInRegion, boolean townhallInRegion,
			boolean meetGoldReq, boolean meetWoodReq) {
		State state = new State(8);
		state.setValue(0, location.getX());
		state.setValue(1, location.getY());
		state.setValue(2, resource);
		state.setValue(3, goldInRegion ? 1 : 0);
		state.setValue(4, woodInRegion ? 1 : 0);
		state.setValue(5, townhallInRegion ? 1 : 0);
		state.setValue(6, meetGoldReq ? 1 : 0);
		state.setValue(7, meetWoodReq ? 1 : 0);
		return state;
	}
}


class Forest implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Point> m_trees;
	private ArrayList<Integer> m_remainingWoods;
	private int m_numTrees;
	private HashSet<Point> m_chopableLocations;

	private boolean m_isEmpty;

	/**
	 * Creator
	 * 
	 * @param trees
	 */
	public Forest(ArrayList<Point> trees) {
		m_trees = trees;
		m_numTrees = m_trees.size();
		m_remainingWoods = new ArrayList<Integer>(m_numTrees);
		for (int i = 0; i < m_numTrees; i++) {
			m_remainingWoods.add(WargusEnv.MAX_WOOD_PER_TREE);
		}
		findChopableLocations();
	}

	private void findChopableLocations() {
		if (m_numTrees == 0) {
			m_isEmpty = true;
			return;
		}
		m_chopableLocations = new HashSet<Point>(m_numTrees * 9);
		int worldSize = WargusEnv.WORLDSIZE;
		for (int i = 0; i < m_numTrees; i++) {
			Point tempTree = m_trees.get(i);
			int x = tempTree.getX();
			int y = tempTree.getY();
			for (int m = -1; m <= 1; m++) {
				for (int n = -1; n <= 1; n++) {
					if (x + m >= 0 && x + m < worldSize && y + n >= 0
							&& y + n < worldSize) {
						Point newPoint = new Point(x + m, y + n);
						m_chopableLocations.add(newPoint);
					}
				}
			}
			m_chopableLocations.remove(tempTree);
		}
	}

	public Collection<Point> getChopableLocations() {
		return m_chopableLocations;
	}

	public boolean isForestInRegion(Point location) {
		if (m_isEmpty == true)
			return false;
		return m_chopableLocations.contains(location);
	}

	public boolean chopWood(Point location) {
		if (m_isEmpty)
			return false;
		if (m_chopableLocations.contains(location)) {
			int x = location.getX();
			int y = location.getY();
			for (int i = 0; i < m_numTrees; i++) {
				if (Math.abs(x - m_trees.get(i).getX()) <= 1
						&& Math.abs(y - m_trees.get(i).getY()) <= 1) {
					int remainingWood = m_remainingWoods.get(i) - 1;
					if (remainingWood == 0) { // one tree disappears
						m_trees.remove(i);
						m_remainingWoods.remove(i);
						m_numTrees--;
						findChopableLocations();
					} else {
						m_remainingWoods.set(i, remainingWood);
					}
					return true;
				}
			}
		}
		return false;
	}
}

class GoldMines implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private ArrayList<Point> m_goldMines;
	private int m_numGoldMines;
	private ArrayList<Integer> m_remainingGold;

	private boolean m_isEmpty;

	public GoldMines(ArrayList<Point> goldMines) {
		m_goldMines = goldMines;
		m_numGoldMines = m_goldMines.size();
		if (m_numGoldMines == 0)
			m_isEmpty = true;
		m_remainingGold = new ArrayList<Integer>(m_numGoldMines);
		for (int i = 0; i < m_numGoldMines; i++) {
			m_remainingGold.add(WargusEnv.MAX_GOLD_PER_MINE);
		}
	}

	public Collection<Point> getMinableLocations() {
		return m_goldMines;
	}

	public boolean isGoldMineInRange(Point location) {
		if (m_isEmpty == true)
			return false;
		return m_goldMines.contains(location);
	}

	public boolean mineGold(Point location) {
		if (m_isEmpty == true)
			return false;
		int index = m_goldMines.indexOf(location);
		if (index >= 0) {
			int remainingGold = m_remainingGold.get(index) - 1;
			if (remainingGold == 0) { // one gold mine disappears
				m_goldMines.remove(index);
				m_remainingGold.remove(index);
				m_numGoldMines--;
				if (m_numGoldMines == 0)
					m_isEmpty = true;
			} else
				m_remainingGold.set(index, remainingGold);
			return true;
		}
		return false;
	}
}

class TownHall implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Point m_townHall;
	private int m_collectedWood;
	private int m_collectedGold;
	private int m_woodRequiredQuota;
	private int m_goldRequiredQuota;

	private boolean m_meetWoodReq;
	private boolean m_meetGoldReq;
	
	private HashSet<Point> m_depositableLocations;

	public TownHall(Point townHall) {
		m_townHall = townHall;
		m_collectedWood = 0;
		m_collectedGold = 0;
		m_woodRequiredQuota = WargusEnv.MAX_REQUIRED_WOOD;
		m_goldRequiredQuota = WargusEnv.MAX_REQUIRED_GOLD;

		m_meetWoodReq = (m_collectedWood >= m_woodRequiredQuota);
		m_meetGoldReq = (m_collectedGold >= m_goldRequiredQuota);
		
		findDepositableLocations();
	}

	private void findDepositableLocations()
	{
		if(m_townHall==null)
			return ;
		m_depositableLocations = new HashSet<Point>(9);
		int worldSize = WargusEnv.WORLDSIZE;
		int x = m_townHall.getX();
		int y = m_townHall.getY();
		for (int m = -1; m <= 1; m++) {
			for (int n = -1; n <= 1; n++) {
				if (x + m >= 0 && x + m < worldSize && y + n >= 0
						&& y + n < worldSize) {
					Point newPoint = new Point(x + m, y + n);
					m_depositableLocations.add(newPoint);
				}
			}
		}
		m_depositableLocations.remove(m_townHall);
	}
	
	public boolean meetRequirement() {
		return m_meetGoldReq && m_meetWoodReq;
	}

	public boolean meetWoodReq() {
		return m_meetWoodReq;
	}

	public boolean meetGoldReq() {
		return m_meetGoldReq;
	}

	public Collection<Point> getDepositableLocations() {
		return m_depositableLocations;
	}

	public boolean isTownHallInRange(Point location) {
		return m_depositableLocations.contains(location);
	}

	/**
	 * 
	 * @param location
	 * @param resource
	 * @return TRUE if.f. town hall is in range and resource is not NONE.
	 */
	public boolean deposit(Point location, int resource) {
		if (m_meetGoldReq && m_meetWoodReq)
			return true;
		if (m_depositableLocations.contains(location)) {
			if (resource == WargusEnv.RESOURCE_GOLD) {
				m_collectedGold++;
				m_meetGoldReq = (m_collectedGold >= m_goldRequiredQuota);
				return true;
			} else if (resource == WargusEnv.RESOURCE_WOOD) {
				m_collectedWood++;
				m_meetWoodReq = (m_collectedWood >= m_woodRequiredQuota);
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

}
