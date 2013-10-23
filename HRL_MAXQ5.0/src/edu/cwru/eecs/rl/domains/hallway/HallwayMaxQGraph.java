package edu.cwru.eecs.rl.domains.hallway;

import java.util.HashMap;

import edu.cwru.eecs.rl.agent.AgentType;
import edu.cwru.eecs.rl.domains.MaxQGraph;
import edu.cwru.eecs.rl.env.EnvSpec;

public class HallwayMaxQGraph extends MaxQGraph {
	
	private static final long serialVersionUID = -5935804945871115135L;

	{
		HTYPE = HType.HIERARCHY; 
	}

	public HallwayMaxQGraph(EnvSpec envSpec, AgentType agentType) {
		super(envSpec, agentType, new HallwayDomainSpec());
	}
	
	public static final int TASK_MOVE = 0;
	public static final int TASK_FOLLOW_WALL = 1;
	public static final int TASK_TO_WALL = 2;
	public static final int TASK_BACK_ONE = 3;
	public static final int TASK_PERP_THREE = 4;
	public static final int TASK_SNIFF = 5;
	public static final int TASK_BACK = 6;
	public static final int TASK_EXIT_INTER = 7;
	public static final int TASK_EXIT_HALL = 8;
	public static final int TASK_GO = 9;
	public static final int TASK_ROOT = 10;
	
	public static final int Q_MOVE_FW = 0;
	public static final int Q_MOVE_TW = 1;
	public static final int Q_MOVE_BO = 2;
	public static final int Q_MOVE_P3 = 3;
	public static final int Q_FOLLOW_WALL = 4;
	public static final int Q_TO_WALL = 5;
	public static final int Q_BACK_ONE = 6;
	public static final int Q_PERP_THREE = 7;
	public static final int Q_SNIFF_EI = 8;
	public static final int Q_BACK_EI = 9;
	public static final int Q_SNIFF_EH = 10;
	public static final int Q_BACK_EH = 11;
	public static final int Q_EXIT_INTER = 12;
	public static final int Q_EXIT_HALL = 13;
	public static final int Q_GO = 14;
	
	
	
	@Override
	protected void initializeTaskNames() {
		m_maxNodeNumber = 11;
		m_primitiveMaxNodeNumber = 1;
		m_taskNames = new String[m_maxNodeNumber];
		m_taskNames[TASK_MOVE] = "TASK_MOVE";
		m_taskNames[TASK_FOLLOW_WALL] = "TASK_FOLLOW_WALL";
		m_taskNames[TASK_TO_WALL] = "TASK_TO_WALL";
		m_taskNames[TASK_BACK_ONE] = "TASK_BACK_ONE";
		m_taskNames[TASK_PERP_THREE] = "TASK_PERP_THREE";
		m_taskNames[TASK_SNIFF] = "TASK_SNIFF";
		m_taskNames[TASK_BACK] = "TASK_BACK";
		m_taskNames[TASK_EXIT_INTER] = "TASK_EXIT_INTER";
		m_taskNames[TASK_EXIT_HALL] = "TASK_EXIT_HALL";
		m_taskNames[TASK_GO] = "TASK_GO";
		m_taskNames[TASK_ROOT] = "TASK_ROOT";
		m_rootMaxNodeIndex = 10;
	}

	@Override
	protected void initializeQNodeNames() {
		m_qNodeNumber = 15;
		m_qNodeNames = new String[m_qNodeNumber];
		m_qNodeNames[Q_MOVE_FW] = "Q_MOVE_FW";
		m_qNodeNames[Q_MOVE_TW] = "Q_MOVE_TW";
		m_qNodeNames[Q_MOVE_BO] = "Q_MOVE_BO";
		m_qNodeNames[Q_MOVE_P3] = "Q_MOVE_P3";
		m_qNodeNames[Q_FOLLOW_WALL] = "Q_FOLLOW_WALL";
		m_qNodeNames[Q_TO_WALL] = "Q_TO_WALL";
		m_qNodeNames[Q_BACK_ONE] = "Q_BACK_ONE";
		m_qNodeNames[Q_PERP_THREE] = "Q_PERP_THREE";
		m_qNodeNames[Q_SNIFF_EI] = "Q_SNIFF_EI";
		m_qNodeNames[Q_BACK_EI] = "Q_BACK_EI";
		m_qNodeNames[Q_SNIFF_EH] = "Q_SNIFF_EH";
		m_qNodeNames[Q_BACK_EH] = "Q_BACK_EH";
		m_qNodeNames[Q_EXIT_INTER] = "Q_EXIT_INTER";
		m_qNodeNames[Q_EXIT_HALL] = "Q_EXIT_HALL";
		m_qNodeNames[Q_GO] = "Q_GO";
	}



	@Override
	protected void initializeConnections() {
		/** initialize connections between MaxNode and QNode */
		m_Q_MaxParent_Connection = new HashMap<Integer, Integer>();
		m_Q_MaxParent_Connection.put(Q_GO, TASK_ROOT);
		m_Q_MaxParent_Connection.put(Q_EXIT_INTER, TASK_GO);
		m_Q_MaxParent_Connection.put(Q_EXIT_HALL, TASK_GO);
		m_Q_MaxParent_Connection.put(Q_SNIFF_EI, TASK_EXIT_INTER);
		m_Q_MaxParent_Connection.put(Q_BACK_EI, TASK_EXIT_INTER);
		m_Q_MaxParent_Connection.put(Q_SNIFF_EH, TASK_EXIT_HALL);
		m_Q_MaxParent_Connection.put(Q_BACK_EH, TASK_EXIT_HALL);
		m_Q_MaxParent_Connection.put(Q_FOLLOW_WALL, TASK_SNIFF);
		m_Q_MaxParent_Connection.put(Q_TO_WALL, TASK_SNIFF);
		m_Q_MaxParent_Connection.put(Q_BACK_ONE, TASK_BACK);
		m_Q_MaxParent_Connection.put(Q_PERP_THREE, TASK_BACK);
		m_Q_MaxParent_Connection.put(Q_MOVE_FW, TASK_FOLLOW_WALL);
		m_Q_MaxParent_Connection.put(Q_MOVE_TW, TASK_TO_WALL);
		m_Q_MaxParent_Connection.put(Q_MOVE_BO, TASK_BACK_ONE);
		m_Q_MaxParent_Connection.put(Q_MOVE_P3, TASK_PERP_THREE);
		
		/** initialize connections between QNode and MaxNode */
		m_Q_MaxChild_Connection = new HashMap<Integer, Integer>();
		m_Q_MaxChild_Connection.put(Q_GO, TASK_GO);
		m_Q_MaxChild_Connection.put(Q_EXIT_INTER, TASK_EXIT_INTER);
		m_Q_MaxChild_Connection.put(Q_EXIT_HALL, TASK_EXIT_HALL);
		m_Q_MaxChild_Connection.put(Q_SNIFF_EI, TASK_SNIFF);
		m_Q_MaxChild_Connection.put(Q_BACK_EI, TASK_BACK);
		m_Q_MaxChild_Connection.put(Q_SNIFF_EH, TASK_SNIFF);
		m_Q_MaxChild_Connection.put(Q_BACK_EH, TASK_BACK);
		m_Q_MaxChild_Connection.put(Q_FOLLOW_WALL, TASK_FOLLOW_WALL);
		m_Q_MaxChild_Connection.put(Q_TO_WALL, TASK_TO_WALL);
		m_Q_MaxChild_Connection.put(Q_BACK_ONE, TASK_BACK_ONE);
		m_Q_MaxChild_Connection.put(Q_PERP_THREE, TASK_PERP_THREE);
		m_Q_MaxChild_Connection.put(Q_MOVE_FW, TASK_MOVE);
		m_Q_MaxChild_Connection.put(Q_MOVE_TW, TASK_MOVE);
		m_Q_MaxChild_Connection.put(Q_MOVE_BO, TASK_MOVE);
		m_Q_MaxChild_Connection.put(Q_MOVE_P3, TASK_MOVE);
	}

	
}






