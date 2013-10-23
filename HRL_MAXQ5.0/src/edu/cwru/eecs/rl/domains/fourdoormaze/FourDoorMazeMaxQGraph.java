package edu.cwru.eecs.rl.domains.fourdoormaze;

import java.util.HashMap;

import edu.cwru.eecs.rl.agent.AgentType;
import edu.cwru.eecs.rl.domains.MaxQGraph;
import edu.cwru.eecs.rl.env.EnvSpec;

public class FourDoorMazeMaxQGraph extends MaxQGraph {

	private static final long serialVersionUID = 669298533856138243L;

	public FourDoorMazeMaxQGraph(EnvSpec envSpec, AgentType agentType) {
		super(envSpec, agentType, new FourDoorMazeDomainSpec());
	}

	public static final int TASK_NORTH = 0;
	public static final int TASK_EAST = 1;
	public static final int TASK_SOUTH = 2;
	public static final int TASK_WEST = 3;
	public static final int TASK_ENTER_COMMON = 4;
	public static final int TASK_EXIT_COMMON = 5;
	public static final int TASK_GOTO_R1 = 6;
	public static final int TASK_GOTO_R2 = 7;
	public static final int TASK_GOTO_R3 = 8;
	public static final int TASK_GOTO_R4 = 9;
	public static final int TASK_ROOT = 10;
	
	public static final int Q_NORTH_FOR_ENTER = 0;
	public static final int Q_EAST_FOR_ENTER = 1;
	public static final int Q_SOUTH_FOR_ENTER = 2;
	public static final int Q_WEST_FOR_ENTER = 3;
	public static final int Q_NORTH_FOR_EXIT = 4;
	public static final int Q_EAST_FOR_EXIT = 5;
	public static final int Q_SOUTH_FOR_EXIT = 6;
	public static final int Q_WEST_FOR_EXIT = 7;
	
	public static final int Q_ENTER_COMMON1 = 8;
	public static final int Q_ENTER_COMMON2 = 9;
	public static final int Q_ENTER_COMMON3 = 10;
	public static final int Q_ENTER_COMMON4 = 11;
	public static final int Q_EXIT_COMMON1 = 12;
	public static final int Q_EXIT_COMMON2 = 13;
	public static final int Q_EXIT_COMMON3 = 14;
	public static final int Q_EXIT_COMMON4 = 15;
	
	public static final int Q_GOTO_R1 = 16;
	public static final int Q_GOTO_R2 = 17;
	public static final int Q_GOTO_R3 = 18;
	public static final int Q_GOTO_R4 = 19;
	
	
	@Override
	protected void initializeTaskNames() {
		m_maxNodeNumber = 11;
		m_primitiveMaxNodeNumber = 4;
		m_taskNames = new String[m_maxNodeNumber];
		m_taskNames[TASK_NORTH] = "TASK_NORTH";
		m_taskNames[TASK_EAST] = "TASK_EAST";
		m_taskNames[TASK_SOUTH] = "TASK_SOUTH";
		m_taskNames[TASK_WEST] = "TASK_WEST";
		m_taskNames[TASK_ENTER_COMMON] = "TASK_ENTER_COMMON";
		m_taskNames[TASK_EXIT_COMMON] = "TASK_EXIT_COMMON";
		m_taskNames[TASK_GOTO_R1] = "TASK_GOTO_R1";
		m_taskNames[TASK_GOTO_R2] = "TASK_GOTO_R2";
		m_taskNames[TASK_GOTO_R3] = "TASK_GOTO_R3";
		m_taskNames[TASK_GOTO_R4] = "TASK_GOTO_R4";
		m_taskNames[TASK_ROOT] = "TASK_ROOT";
		m_rootMaxNodeIndex = TASK_ROOT;
	}

	@Override
	protected void initializeQNodeNames() {
		m_qNodeNumber = 20;
		m_qNodeNames = new String[m_qNodeNumber];
		
		m_qNodeNames[Q_NORTH_FOR_ENTER] = "Q_NORTH_FOR_ENTER";
		m_qNodeNames[Q_EAST_FOR_ENTER] = "Q_EAST_FOR_ENTER";
		m_qNodeNames[Q_SOUTH_FOR_ENTER] = "Q_SOUTH_FOR_ENTER";
		m_qNodeNames[Q_WEST_FOR_ENTER] = "Q_WEST_FOR_ENTER";
		m_qNodeNames[Q_NORTH_FOR_EXIT] = "Q_NORTH_FOR_EXIT";
		m_qNodeNames[Q_EAST_FOR_EXIT] = "Q_EAST_FOR_EXIT";
		m_qNodeNames[Q_SOUTH_FOR_EXIT] = "Q_SOUTH_FOR_EXIT";
		m_qNodeNames[Q_WEST_FOR_EXIT] = "Q_WEST_FOR_EXIT";
		
		m_qNodeNames[Q_ENTER_COMMON1] = "Q_ENTER_COMMON1";
		m_qNodeNames[Q_ENTER_COMMON2] = "Q_ENTER_COMMON2";
		m_qNodeNames[Q_ENTER_COMMON3] = "Q_ENTER_COMMON3";
		m_qNodeNames[Q_ENTER_COMMON4] = "Q_ENTER_COMMON4";
		m_qNodeNames[Q_EXIT_COMMON1] = "Q_EXIT_COMMON1";
		m_qNodeNames[Q_EXIT_COMMON2] = "Q_EXIT_COMMON2";
		m_qNodeNames[Q_EXIT_COMMON3] = "Q_EXIT_COMMON3";
		m_qNodeNames[Q_EXIT_COMMON4] = "Q_EXIT_COMMON4";
		
		m_qNodeNames[Q_GOTO_R1] = "Q_GOTO_R1";
		m_qNodeNames[Q_GOTO_R2] = "Q_GOTO_R2";
		m_qNodeNames[Q_GOTO_R3] = "Q_GOTO_R3";
		m_qNodeNames[Q_GOTO_R4] = "Q_GOTO_R4";
	}

	@Override
	protected void initializeConnections() {
		/** initialize connections between MaxNode and QNode */
		m_Q_MaxParent_Connection = new HashMap<Integer, Integer>();
		m_Q_MaxParent_Connection.put(Q_NORTH_FOR_ENTER, TASK_ENTER_COMMON);
		m_Q_MaxParent_Connection.put(Q_EAST_FOR_ENTER, TASK_ENTER_COMMON);
		m_Q_MaxParent_Connection.put(Q_SOUTH_FOR_ENTER, TASK_ENTER_COMMON);
		m_Q_MaxParent_Connection.put(Q_WEST_FOR_ENTER, TASK_ENTER_COMMON);
		m_Q_MaxParent_Connection.put(Q_NORTH_FOR_EXIT, TASK_EXIT_COMMON);
		m_Q_MaxParent_Connection.put(Q_EAST_FOR_EXIT, TASK_EXIT_COMMON);
		m_Q_MaxParent_Connection.put(Q_SOUTH_FOR_EXIT, TASK_EXIT_COMMON);
		m_Q_MaxParent_Connection.put(Q_WEST_FOR_EXIT, TASK_EXIT_COMMON);
		
		m_Q_MaxParent_Connection.put(Q_ENTER_COMMON1, TASK_GOTO_R1);
		m_Q_MaxParent_Connection.put(Q_ENTER_COMMON2, TASK_GOTO_R2);
		m_Q_MaxParent_Connection.put(Q_ENTER_COMMON3, TASK_GOTO_R3);
		m_Q_MaxParent_Connection.put(Q_ENTER_COMMON4, TASK_GOTO_R4);
		m_Q_MaxParent_Connection.put(Q_EXIT_COMMON1, TASK_GOTO_R1);
		m_Q_MaxParent_Connection.put(Q_EXIT_COMMON2, TASK_GOTO_R2);
		m_Q_MaxParent_Connection.put(Q_EXIT_COMMON3, TASK_GOTO_R3);
		m_Q_MaxParent_Connection.put(Q_EXIT_COMMON4, TASK_GOTO_R4);
		
		m_Q_MaxParent_Connection.put(Q_GOTO_R1, TASK_ROOT);
		m_Q_MaxParent_Connection.put(Q_GOTO_R2, TASK_ROOT);
		m_Q_MaxParent_Connection.put(Q_GOTO_R3, TASK_ROOT);
		m_Q_MaxParent_Connection.put(Q_GOTO_R4, TASK_ROOT);
		
		/** initialize connections between QNode and MaxNode */
		m_Q_MaxChild_Connection = new HashMap<Integer, Integer>();
		m_Q_MaxChild_Connection.put(Q_NORTH_FOR_ENTER, TASK_NORTH);
		m_Q_MaxChild_Connection.put(Q_EAST_FOR_ENTER, TASK_EAST);
		m_Q_MaxChild_Connection.put(Q_SOUTH_FOR_ENTER, TASK_SOUTH);
		m_Q_MaxChild_Connection.put(Q_WEST_FOR_ENTER, TASK_WEST);
		m_Q_MaxChild_Connection.put(Q_NORTH_FOR_EXIT, TASK_NORTH);
		m_Q_MaxChild_Connection.put(Q_EAST_FOR_EXIT, TASK_EAST);
		m_Q_MaxChild_Connection.put(Q_SOUTH_FOR_EXIT, TASK_SOUTH);
		m_Q_MaxChild_Connection.put(Q_WEST_FOR_EXIT, TASK_WEST);
		
		m_Q_MaxChild_Connection.put(Q_ENTER_COMMON1, TASK_ENTER_COMMON);
		m_Q_MaxChild_Connection.put(Q_ENTER_COMMON2, TASK_ENTER_COMMON);
		m_Q_MaxChild_Connection.put(Q_ENTER_COMMON3, TASK_ENTER_COMMON);
		m_Q_MaxChild_Connection.put(Q_ENTER_COMMON4, TASK_ENTER_COMMON);
		m_Q_MaxChild_Connection.put(Q_EXIT_COMMON1, TASK_EXIT_COMMON);
		m_Q_MaxChild_Connection.put(Q_EXIT_COMMON2, TASK_EXIT_COMMON);
		m_Q_MaxChild_Connection.put(Q_EXIT_COMMON3, TASK_EXIT_COMMON);
		m_Q_MaxChild_Connection.put(Q_EXIT_COMMON4, TASK_EXIT_COMMON);
		
		m_Q_MaxChild_Connection.put(Q_GOTO_R1, TASK_GOTO_R1);
		m_Q_MaxChild_Connection.put(Q_GOTO_R2, TASK_GOTO_R2);
		m_Q_MaxChild_Connection.put(Q_GOTO_R3, TASK_GOTO_R3);
		m_Q_MaxChild_Connection.put(Q_GOTO_R4, TASK_GOTO_R4);
	}

}







