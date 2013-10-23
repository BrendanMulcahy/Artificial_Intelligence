package edu.cwru.eecs.rl.domains.simplemaze;

import java.util.HashMap;

import edu.cwru.eecs.rl.agent.AgentType;
import edu.cwru.eecs.rl.domains.MaxQGraph;
import edu.cwru.eecs.rl.env.EnvSpec;

public class SimpleMazeMaxQGraph extends MaxQGraph {

	private static final long serialVersionUID = 3970654729606766239L;

	{
		HTYPE = HType.HIERARCHY; 
	}
	
	public SimpleMazeMaxQGraph(EnvSpec envSpec, AgentType agentType) {
		super(envSpec, agentType, new SimpleMazeDomainSpec());
	}

	@Override
	protected void initializeTaskNames() {
		m_maxNodeNumber = 6;
		m_primitiveMaxNodeNumber = 3;
		m_taskNames = new String[m_maxNodeNumber];
		m_taskNames[TASK_NORTH] = "TASK_NORTH";
		m_taskNames[TASK_SOUTH] = "TASK_SOUTH";
		m_taskNames[TASK_EAST] = "TASK_EAST";
		m_taskNames[TASK_EXIT] = "TASK_EXIT";
		m_taskNames[TASK_GOTOGOAL] = "TASK_GOTOGOAL";
		m_taskNames[TASK_ROOT] = "TASK_ROOT";
		m_rootMaxNodeIndex = TASK_ROOT;
	}

	@Override
	protected void initializeQNodeNames() {
		m_qNodeNumber = 8;
		m_qNodeNames = new String[m_qNodeNumber];
		m_qNodeNames[Q_EXIT_NORTH] = "Q_EXIT_NORTH";
		m_qNodeNames[Q_EXIT_SOUTH] = "Q_EXIT_SOUTH";
		m_qNodeNames[Q_EXIT_EAST] = "Q_EXIT_EAST";
		m_qNodeNames[Q_G_NORTH] = "Q_G_NORTH";
		m_qNodeNames[Q_G_SOUTH] = "Q_G_SOUTH";
		m_qNodeNames[Q_G_EAST] = "Q_G_EAST";
		m_qNodeNames[Q_EXIT] = "Q_EXIT";
		m_qNodeNames[Q_GOTOGOAL] = "Q_GOTOGOAL";
	}

	@Override
	protected void initializeConnections() {
		/** initialize connections between MaxNode and QNode */
		m_Q_MaxParent_Connection = new HashMap<Integer, Integer>();
		m_Q_MaxParent_Connection.put(Q_EXIT, TASK_ROOT);
		m_Q_MaxParent_Connection.put(Q_GOTOGOAL, TASK_ROOT);
		m_Q_MaxParent_Connection.put(Q_EXIT_NORTH, TASK_EXIT);
		m_Q_MaxParent_Connection.put(Q_EXIT_SOUTH, TASK_EXIT);
		m_Q_MaxParent_Connection.put(Q_EXIT_EAST, TASK_EXIT);
		m_Q_MaxParent_Connection.put(Q_G_NORTH, TASK_GOTOGOAL);
		m_Q_MaxParent_Connection.put(Q_G_SOUTH, TASK_GOTOGOAL);
		m_Q_MaxParent_Connection.put(Q_G_EAST, TASK_GOTOGOAL);
		
		/** initialize connections between QNode and MaxNode */
		m_Q_MaxChild_Connection = new HashMap<Integer, Integer>();
		m_Q_MaxChild_Connection.put(Q_EXIT, TASK_EXIT);
		m_Q_MaxChild_Connection.put(Q_GOTOGOAL, TASK_GOTOGOAL);
		m_Q_MaxChild_Connection.put(Q_EXIT_NORTH, TASK_NORTH);
		m_Q_MaxChild_Connection.put(Q_EXIT_SOUTH, TASK_SOUTH);
		m_Q_MaxChild_Connection.put(Q_EXIT_EAST, TASK_EAST);
		m_Q_MaxChild_Connection.put(Q_G_NORTH, TASK_NORTH);
		m_Q_MaxChild_Connection.put(Q_G_SOUTH, TASK_SOUTH);
		m_Q_MaxChild_Connection.put(Q_G_EAST, TASK_EAST);
	}
	
	
	public static final int TASK_NORTH = 0;
	public static final int TASK_SOUTH = 1;
	public static final int TASK_EAST = 2;
	public static final int TASK_EXIT = 3;
	public static final int TASK_GOTOGOAL = 4;
	public static final int TASK_ROOT = 5;
	
	public static final int Q_EXIT_NORTH = 0;
	public static final int Q_EXIT_SOUTH = 1;
	public static final int Q_EXIT_EAST = 2;
	public static final int Q_G_NORTH = 3;
	public static final int Q_G_SOUTH = 4;
	public static final int Q_G_EAST = 5;
	public static final int Q_EXIT = 6;
	public static final int Q_GOTOGOAL = 7;
}
