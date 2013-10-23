package edu.cwru.eecs.rl.env;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.StringTokenizer;


import com.google.common.collect.HashBiMap;

import edu.cwru.eecs.futil.misc.StringMisc;
import edu.cwru.eecs.rl.core.mdp.Action;
import edu.cwru.eecs.rl.core.mdp.Parameter;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.domains.DomainEnum;
import edu.cwru.eecs.rl.experiment.FileProcessor;


public class HallwayEnv extends Environment {
	
	private static final long serialVersionUID = 3985811533592554790L;
	
	/** state variables' indices */
	public static final int STATE_ROOM_NUMBER = 0;
	public static final int STATE_ROOM_LOCX = 1;
	public static final int STATE_ROOM_LOCY = 2;
	
	/** action parameters*/
	public static final int MOVE_DIRECTION_NUMBER = 4;
	public static final int ACTION_MOVE_NORTH = 0;
	public static final int ACTION_MOVE_EAST = 1;
	public static final int ACTION_MOVE_SOUTH = 2;
	public static final int ACTION_MOVE_WEST = 3;
	
	/** directions */
	public static final int DIR_NORTH = ACTION_MOVE_NORTH;
	public static final int DIR_EAST = ACTION_MOVE_EAST;
	public static final int DIR_SOUTH = ACTION_MOVE_SOUTH;
	public static final int DIR_WEST = ACTION_MOVE_WEST;
	
	static {
		STATE_DIMENSION = 3;
		ACTION_NUMBER = MOVE_DIRECTION_NUMBER;
		ACTION_NAME = new String[ACTION_NUMBER];
		ACTION_NAME[ACTION_MOVE_NORTH] = "MOVE_NORTH";
		ACTION_NAME[ACTION_MOVE_EAST] = "MOVE_EAST";
		ACTION_NAME[ACTION_MOVE_SOUTH] = "MOVE_SOUTH";
		ACTION_NAME[ACTION_MOVE_WEST] = "MOVE_WEST";
	}
	
	/** rewards */
	public static final double REWARD_MOVE = -1;
	public static final double REWARD_STAY = -1;
	public static final double REWARD_GOAL = 5;
	
	/** room number and room size */
	public static final int ENV_ROOM_NUMBER = 30;
	public static final int ENV_ROOM_SIZE_X = 12;
	public static final int ENV_ROOM_SIZE_Y = 12;
	public static final int ENV_ROOM_WALL_CONFIG_NUM = 5;
	
	public static final int ENV_START_ROOM_NUMBER = 0;
	public static final int ENV_GOAL_ROOM_NUMBER = 29;
	
	/** domain specific information */
	private static HallwayConfig m_hallwayConfig;
	private int m_roomNumber;
	private int m_locX;
	private int m_locY;
	
	/**
	 * with prob 1-noise*2, the action succeed. 
	 * with prob noise, the action will move to a perpendicular direction.
	 */
	private double m_noise = 0; 
	
	public HallwayEnv() {
		super(DomainEnum.Hallway.toString(), 1.0d);
		m_hallwayConfig = new HallwayConfig(DomainEnum.Hallway.toString());
	}
	
	public HallwayEnv(String[] args) {
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
	
	public String message(String message) {
		return super.message(message);
	}
	
	@Override
	public void start() {
		m_roomNumber = ENV_START_ROOM_NUMBER;
		boolean invalid = true;
		while(invalid) {
			m_locX = (int)(Math.random()*ENV_ROOM_SIZE_X);
			m_locY = (int)(Math.random()*ENV_ROOM_SIZE_Y);
			if(m_hallwayConfig.available(m_roomNumber, m_locX, m_locY))
				invalid = false;
		}
		m_isTerminal = false;
	}

	@Override
	public boolean takeAction(Action action) {
		int direction = (int)action.getParameter().getValue(0);
		double random = Math.random();
		if(random<m_noise) {
			direction = (direction+1)%MOVE_DIRECTION_NUMBER;
		} else if(random<m_noise*2) {
			direction = (direction-1+MOVE_DIRECTION_NUMBER)%MOVE_DIRECTION_NUMBER;
		}
		
		State newState = m_hallwayConfig.move(m_roomNumber, m_locX, m_locY, direction);
		if(newState==null) { // no move
			m_reward = REWARD_STAY;
			return false;
		} else { // move
			m_roomNumber = (int)newState.getValue(STATE_ROOM_NUMBER);
			m_locX = (int)newState.getValue(STATE_ROOM_LOCX);
			m_locY = (int)newState.getValue(STATE_ROOM_LOCY);
			m_reward = REWARD_MOVE;
		}
		if(m_roomNumber==ENV_GOAL_ROOM_NUMBER) { // reach goal
			m_reward += REWARD_GOAL;
			m_isTerminal = true;
		}
		return true;
	}
	

	@Override
	public HashBiMap<State, Integer> getStartingStateSet() {
		return getAllStateSet();
	}

	@Override
	public HashBiMap<State, Integer> getAllStateSet() {
		HashBiMap<State, Integer> allStateSet = HashBiMap.create();
		int count = 0;
		for(int r=0; r<ENV_ROOM_NUMBER; r++) {
			for(int x=0; x<ENV_ROOM_SIZE_X; x++) {
				for(int y=0; y<ENV_ROOM_SIZE_Y; y++) {
					if(!m_hallwayConfig.available(r, x, y))
						continue;
					State state = new State(3);
					state.setValue(0, r);
					state.setValue(1, x);
					state.setValue(2, y);
					allStateSet.put(state, count);
					count++;
				}
			}
		}
		return allStateSet;
	}
	
	@Override
	public HashBiMap<Action, Integer> getAllActionSet() {
		HashBiMap<Action, Integer> allActionSet = HashBiMap.create();
		for(int i=0; i<4; i++) {
			Parameter parameter = new Parameter(1);
			parameter.setValue(0, i);
			Action action = new Action(0, parameter);
			allActionSet.put(action, i);
		}
		return allActionSet;
	}


	protected HallwayConfig getHallwayConfig() {
		return m_hallwayConfig;
	}


	@Override
	public State getState() {
		State state = new State(STATE_DIMENSION);
		state.setValue(STATE_ROOM_NUMBER, m_roomNumber);
		state.setValue(STATE_ROOM_LOCX, m_locX);
		state.setValue(STATE_ROOM_LOCY, m_locY);
		return state;
	}

	/** following is some domain specific methods */
	public static boolean isInter(int roomNo) {
		return m_hallwayConfig.isInter(roomNo);
	}
	public static boolean isHallway(int roomNo) {
		return m_hallwayConfig.isHallway(roomNo);
	}
	
	/**
	 * r2 is to the d of r1, no necessary adjacent
	 * @param r1
	 * @param r2
	 * @param d
	 * @return true if r2 is to the d of r1, no necessary adjacent
	 */
	public static boolean neighbours(int r1, int r2, int d) {
		return m_hallwayConfig.neighbours(r1, r2, d);
	}
	public static boolean hasWall(int roomNo, int locX, int locY, int d) {
		return m_hallwayConfig.hasWall(roomNo, locX, locY, d);
	}
	public static int dist(int x1, int y1, int x2, int y2, int d) {
		return m_hallwayConfig.dist(x1, y1, x2, y2, d);
	}
	public static boolean hasRoom(int r, int d) {
		return m_hallwayConfig.hasRoom(r, d);
	}
	public static int findInter(int r, int d) {
		return m_hallwayConfig.findInter(r, d);
	}
}

class HallwayConfig implements Serializable {

	private static final long serialVersionUID = 8295357526140773378L;

	private static final int WALL = 1;
	
	/** room wall configuration */
	protected int[][][] m_wallConfig;
	protected char[] m_wallConfigName = new char[]{'L', 'R', 'U', 'D', 'N'};
	
	/** rooms type and configuration:
	 * 1st dim: room id
	 * 2nd dim: room attributes */
	protected int[] m_roomWallConfigType; // wall config type
	protected char[] m_roomType; // {I | V | H}
	protected char[] m_roomNeighbourName = new char[]{'U', 'R', 'D', 'L'};
	protected int[][] m_roomNeighbour;	// U, R, D, L {0 ~ ENV_ROOM_NUMBER-1}
	
	
	public HallwayConfig(String configFileName) {
		readInHallwayConfig(configFileName);
	}
	
	public boolean isInter(int roomNo) {
		if(roomNo<0 || roomNo>m_roomType.length-1)
			return false;
		return m_roomType[roomNo]=='I';
	}
	public boolean isHallway(int roomNo) {
		if(roomNo<0 || roomNo>m_roomType.length-1)
			return false;
		return m_roomType[roomNo]!='I';
	}
	public boolean neighbours(int r1, int r2, int d) {
		if(m_roomNeighbour[r1][d]==-1)
			return false;
		return m_roomNeighbour[r1][d]==r2 || neighbours(m_roomNeighbour[r1][d], r2, d);
	}
	public boolean hasWall(int roomNo, int locX, int locY, int d) {
		return move(roomNo, locX, locY, d)==null;
	}
	public int dist(int x1, int y1, int x2, int y2, int d) {
		switch (d) {
		case HallwayEnv.ACTION_MOVE_NORTH:
			return (x1+12-x2)%12;
		case HallwayEnv.ACTION_MOVE_EAST:
			return (y2+12-y1)%12;
		case HallwayEnv.ACTION_MOVE_SOUTH:
			return (x2+12-x1)%12;
		case HallwayEnv.ACTION_MOVE_WEST:
			return (y1+12-y2)%12;
		default:
			System.err.println("Invalid direction: " + HallwayEnv.ACTION_NAME[d]);
			return -1;
		}
	}
	public boolean hasRoom(int r, int d) {
		return m_roomNeighbour[r][d]!=-1;
	}
	
	public int findInter(int r, int d) {
		while(m_roomNeighbour[r][d]!=-1) {
			r = m_roomNeighbour[r][d];
		}
		if(m_roomType[r]=='I')
			return r;
		return -1;
	}

	/**
	 * 
	 * @param roomNumber
	 * @param locX
	 * @param locY
	 * @param direction
	 * @return the new state if movable, otherwise, return null, means stay at the same place.
	 */
	public State move(int roomNumber, int locX, int locY, int direction) {
		if(roomNumber<0 || roomNumber>=HallwayEnv.ENV_ROOM_NUMBER) {
			System.err.println("room number out of bound: " + roomNumber);
			return null;
		} else if(locX<0 || locX>=HallwayEnv.ENV_ROOM_SIZE_X || locY<0 || locY>=HallwayEnv.ENV_ROOM_SIZE_Y) {
			System.err.println("location out of bound: " + locX + " " + locY);
			return null;
		} 
		int newRoomNumber = roomNumber;
		int newLocX = locX;
		int newLocY = locY;
		switch (direction) {
		case HallwayEnv.ACTION_MOVE_EAST:
			newLocY++;	break;
		case HallwayEnv.ACTION_MOVE_NORTH:
			newLocX--; 	break;
		case HallwayEnv.ACTION_MOVE_SOUTH:
			newLocX++;	break;
		case HallwayEnv.ACTION_MOVE_WEST:
			newLocY--;	break;
		default:
			System.err.println("Invalid direction: " + direction);
			return null;
		}
		
		if(newLocX>=0 && newLocX<HallwayEnv.ENV_ROOM_SIZE_X && newLocY>=0 && newLocY<HallwayEnv.ENV_ROOM_SIZE_Y) {
			// still in the same room
			if(m_wallConfig[m_roomWallConfigType[roomNumber]][newLocX][newLocY]==WALL)
				return null;
		} else {
			// in another room?
			if(m_roomNeighbour[roomNumber][direction]<0)  // not neighbor room
				return null;
			newRoomNumber = m_roomNeighbour[roomNumber][direction];
			if(newLocX<0 || newLocX>=HallwayEnv.ENV_ROOM_SIZE_X) { //the action should be UP or DOWN
				newLocX = (newLocX + HallwayEnv.ENV_ROOM_SIZE_X)%HallwayEnv.ENV_ROOM_SIZE_X;
			} else if(newLocY<0 || newLocY>=HallwayEnv.ENV_ROOM_SIZE_Y) {
				newLocY = (newLocY + HallwayEnv.ENV_ROOM_SIZE_Y)%HallwayEnv.ENV_ROOM_SIZE_Y;
			} else {
				System.err.println("You won't be possible to reach here. Go debug!");
				return null;
			}
			if(m_wallConfig[m_roomWallConfigType[newRoomNumber]][newLocX][newLocY]==WALL)  // there is wall
				return null;
		}
		State newState = new State(HallwayEnv.STATE_DIMENSION);
		newState.setValue(HallwayEnv.STATE_ROOM_NUMBER, newRoomNumber);
		newState.setValue(HallwayEnv.STATE_ROOM_LOCX, newLocX);
		newState.setValue(HallwayEnv.STATE_ROOM_LOCY, newLocY);
		return newState;
	}
	
	
	public boolean available(int roomNumber, int locX, int locY) {
		if(roomNumber<0 || roomNumber>=HallwayEnv.ENV_ROOM_NUMBER) {
			System.err.println("room number out of bound: " + roomNumber);
			return false;
		} else if(locX<0 || locX>=HallwayEnv.ENV_ROOM_SIZE_X || locY<0 || locY>=HallwayEnv.ENV_ROOM_SIZE_Y) {
			System.err.println("location out of bound: " + locX + " " + locY);
			return false;
		}
		if(m_wallConfig[m_roomWallConfigType[roomNumber]][locX][locY]==WALL)
			return false;
		return true;
	}
	
	private static final String WALL_CONFIG = "@WALL_CONFIG";
	private static final String ROOM_CONFIG = "@ROOM_CONFIG";
	
	private void readInHallwayConfig(String configFileName) {
		// read in Halway.config
		String configFilePath = "data/" + configFileName + FileProcessor.CONFIG_FILE_EXTENSION;
		m_wallConfig = new int[HallwayEnv.ENV_ROOM_WALL_CONFIG_NUM][HallwayEnv.ENV_ROOM_SIZE_X][HallwayEnv.ENV_ROOM_SIZE_Y];  // by default, initial values are all 0
		m_roomWallConfigType = new int[HallwayEnv.ENV_ROOM_NUMBER];
		m_roomType = new char[HallwayEnv.ENV_ROOM_NUMBER];
		m_roomNeighbour = new int[HallwayEnv.ENV_ROOM_NUMBER][4];
		int configIndex = -1;
		BufferedReader inputBufferedReader = null;
		try {
			inputBufferedReader = new BufferedReader(new FileReader(configFilePath));
			String lineString;
			StringTokenizer stringTokenizer;
			while((lineString = FileProcessor.nextLine(inputBufferedReader))!=null) {
				stringTokenizer = new StringTokenizer(lineString);
				String head = stringTokenizer.nextToken();
				if(head.equals(WALL_CONFIG)) {
					configIndex = StringMisc.strToInt(stringTokenizer.nextToken());
				}
				else if(head.equals(ROOM_CONFIG)) {
					configIndex = -1;
				}
				else if(configIndex>-1){  // in wall config
					int x = StringMisc.strToInt(head);
					while(stringTokenizer.hasMoreTokens()) {
						int y = StringMisc.strToInt(stringTokenizer.nextToken());
						m_wallConfig[configIndex][x][y] = WALL;  // there is a wall block here
					}
				}
				else { // configIndex==-1, in room config
					int roomID = StringMisc.strToInt(head);
					char wallConfigType = stringTokenizer.nextToken().charAt(0);
					int wallConfigTypeIndex = -1;
					switch (wallConfigType) {
					case 'L':
						wallConfigTypeIndex = 0; break;
					case 'R':
						wallConfigTypeIndex = 1; break;
					case 'U':
						wallConfigTypeIndex = 2; break;
					case 'D':
						wallConfigTypeIndex = 3; break;
					case 'N':
						wallConfigTypeIndex = 4; break;
					default:
						break;
					}
					m_roomWallConfigType[roomID] = wallConfigTypeIndex;
					m_roomNeighbour[roomID][0] = StringMisc.strToInt(stringTokenizer.nextToken());
					m_roomNeighbour[roomID][1] = StringMisc.strToInt(stringTokenizer.nextToken());
					m_roomNeighbour[roomID][2] = StringMisc.strToInt(stringTokenizer.nextToken());
					m_roomNeighbour[roomID][3] = StringMisc.strToInt(stringTokenizer.nextToken());
					m_roomType[roomID] = stringTokenizer.nextToken().charAt(0);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputBufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void drawRoomLayout() {
		int[][] roomLayout = new int[7][7];
		int[][] roomWallLayout = new int[7][7];
		for(int i=0; i<7; i++){
			for(int j=0; j<7; j++) {
				roomLayout[i][j] = -1;
				roomWallLayout[i][j] = -1;
			}
		}
		int[] pos = new int[30];
		Arrays.fill(pos, -1);
		roomLayout[0][0] = 0;
		pos[0] = 0;
		roomLayout[2][2] = 8;
		pos[8] = 22;
		for(int i=0; i<HallwayEnv.ENV_ROOM_NUMBER; i++) {
			int x = pos[i]/10;  //pos[i] = 'xy'
			int y = pos[i]%10;
			roomWallLayout[x][y] = m_roomWallConfigType[i];
			//System.out.println("X: " + x + "Y: " + y);
			if(pos[i]==-1) 
				System.err.println("room layout error: " + i);
			for(int j=0; j<4; j++) {
				int index = m_roomNeighbour[i][j];
				if(index==-1) 
					continue;
				int newPos = -1;
				switch (j) {
				case 0:  // U
					roomLayout[x-1][y] = index;	
					newPos = (x-1)*10+y;
					break;
				case 1: // R
					roomLayout[x][y+1] = index; 
					newPos = x*10+(y+1);
					break;
				case 2: // D
					roomLayout[x+1][y] = index; 
					newPos = (x+1)*10+y;
					break;
				case 3: // L
					roomLayout[x][y-1] = index; 
					newPos = x*10+y-1;
					break;
				default:
					break;
				}
				if(pos[index]!=-1 && pos[index]!=newPos) {
					System.err.println("configuration conflict: " + i);
				}
				pos[index] = newPos;
			}
		}
		// draw roomLayout
		System.out.println("Rooms Layout: ");
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<7; i++) {
			for(int j=0; j<7; j++) {
				int temp = roomLayout[i][j];
				sb.append("|" + (temp==-1?" ":temp) + "\t");
			}
			sb.append("|\n");
		}
		System.out.println(sb.toString());
		
		// draw room wall layout
		System.out.println("Room walls layout: ");
		sb = new StringBuffer();
		for(int i=0; i<7; i++) {
			for(int j=0; j<7; j++) {
				int temp = roomWallLayout[i][j];
				sb.append("|" + (temp==-1?" ":m_wallConfigName[temp]) + "\t");
			}
			sb.append("|\n");
		}
		System.out.println(sb.toString());
	}
	
	public void drawWallLayout() {
		for(int i=0; i<HallwayEnv.ENV_ROOM_WALL_CONFIG_NUM; i++) {
			System.out.println("Wall config " + i + ":");
			StringBuffer sb = new StringBuffer();
			for(int x=0; x<HallwayEnv.ENV_ROOM_SIZE_X; x++) {
				for(int y=0; y<HallwayEnv.ENV_ROOM_SIZE_Y; y++) {
					if(m_wallConfig[i][x][y]==WALL)
						sb.append("0");
					else {
						sb.append(" ");
					}
				}
				sb.append("\n");
			}
			System.out.println(sb.toString());
		}
	}
}



