package edu.cwru.eecs.rl.domains.taxi;

import java.util.HashMap;

import edu.cwru.eecs.rl.agent.AgentType;
import edu.cwru.eecs.rl.domains.MaxQGraph;
import edu.cwru.eecs.rl.env.EnvSpec;

public class TaxiMaxQGraph extends MaxQGraph {

	private static final long serialVersionUID = -8487582505000505545L;

	{
		HTYPE = HType.HIERARCHY; 
	}

	public TaxiMaxQGraph(EnvSpec envSpec, AgentType agentType) {
		super(envSpec, agentType, new TaxiDomainSpec());
	}
	
	public static final int TASK_NORTH = 0;
	public static final int TASK_EAST = 1;
	public static final int TASK_SOUTH = 2;
	public static final int TASK_WEST = 3;
	public static final int TASK_PICKUP = 4;
	public static final int TASK_PUTDOWN = 5;
	public static final int TASK_NAVIGATE = 6;
	public static final int TASK_GET = 7;
	public static final int TASK_PUT = 8;
	public static final int TASK_ROOT = 9;
	
	public static final int Q_NORTH = 0;
	public static final int Q_EAST = 1;
	public static final int Q_SOUTH = 2;
	public static final int Q_WEST = 3;
	public static final int Q_PICKUP = 4;
	public static final int Q_PUTDOWN = 5;
	public static final int Q_NAVIGATE_FOR_GET = 6;
	public static final int Q_NAVIGATE_FOR_PUT = 7;
	public static final int Q_GET = 8;
	public static final int Q_PUT = 9;
	

	@Override
	protected void initializeTaskNames() {
		m_maxNodeNumber = 10;
		m_primitiveMaxNodeNumber = 6;
		m_taskNames = new String[m_maxNodeNumber];
		m_taskNames[TASK_NORTH] = "TASK_NORTH";
		m_taskNames[TASK_EAST] = "TASK_EAST";
		m_taskNames[TASK_SOUTH] = "TASK_SOUTH";
		m_taskNames[TASK_WEST] = "TASK_WEST";
		m_taskNames[TASK_PICKUP] = "TASK_PICKUP";
		m_taskNames[TASK_PUTDOWN] = "TASK_PUTDOWN";
		m_taskNames[TASK_NAVIGATE] = "TASK_NAVIGATE";
		m_taskNames[TASK_GET] = "TASK_GET";
		m_taskNames[TASK_PUT] = "TASK_PUT";
		m_taskNames[TASK_ROOT] = "TASK_ROOT";
		m_rootMaxNodeIndex = 9;
	}

	@Override
	protected void initializeQNodeNames() {
		m_qNodeNumber = 10;
		m_qNodeNames = new String[m_qNodeNumber];
		m_qNodeNames[Q_NORTH] = "Q_NORTH";
		m_qNodeNames[Q_EAST] = "Q_EAST";
		m_qNodeNames[Q_SOUTH] = "Q_SOUTH";
		m_qNodeNames[Q_WEST] = "Q_WEST";
		m_qNodeNames[Q_PICKUP] = "Q_PICKUP";
		m_qNodeNames[Q_PUTDOWN] = "Q_PUTDOWN";
		m_qNodeNames[Q_NAVIGATE_FOR_GET] = "Q_NAVIGATE_FOR_GET";
		m_qNodeNames[Q_NAVIGATE_FOR_PUT] = "Q_NAVIGATE_FOR_PUT";
		m_qNodeNames[Q_GET] = "Q_GET";
		m_qNodeNames[Q_PUT] = "Q_PUT";
	}

	@Override
	protected void initializeConnections() {
		/** initialize connections between MaxNode and QNode */
		m_Q_MaxParent_Connection = new HashMap<Integer, Integer>();
		m_Q_MaxParent_Connection.put(Q_GET, TASK_ROOT);
		m_Q_MaxParent_Connection.put(Q_PUT, TASK_ROOT);
		m_Q_MaxParent_Connection.put(Q_PICKUP, TASK_GET);
		m_Q_MaxParent_Connection.put(Q_NAVIGATE_FOR_GET, TASK_GET);
		m_Q_MaxParent_Connection.put(Q_PUTDOWN, TASK_PUT);
		m_Q_MaxParent_Connection.put(Q_NAVIGATE_FOR_PUT, TASK_PUT);
		m_Q_MaxParent_Connection.put(Q_NORTH, TASK_NAVIGATE);
		m_Q_MaxParent_Connection.put(Q_EAST, TASK_NAVIGATE);
		m_Q_MaxParent_Connection.put(Q_SOUTH, TASK_NAVIGATE);
		m_Q_MaxParent_Connection.put(Q_WEST, TASK_NAVIGATE);
		
		/** initialize connections between QNode and MaxNode */
		m_Q_MaxChild_Connection = new HashMap<Integer, Integer>();
		m_Q_MaxChild_Connection.put(Q_GET, TASK_GET);
		m_Q_MaxChild_Connection.put(Q_PUT, TASK_PUT);
		m_Q_MaxChild_Connection.put(Q_PICKUP, TASK_PICKUP);
		m_Q_MaxChild_Connection.put(Q_PUTDOWN, TASK_PUTDOWN);
		m_Q_MaxChild_Connection.put(Q_NAVIGATE_FOR_GET, TASK_NAVIGATE);
		m_Q_MaxChild_Connection.put(Q_NAVIGATE_FOR_PUT, TASK_NAVIGATE);
		m_Q_MaxChild_Connection.put(Q_NORTH, TASK_NORTH);
		m_Q_MaxChild_Connection.put(Q_EAST, TASK_EAST);
		m_Q_MaxChild_Connection.put(Q_SOUTH, TASK_SOUTH);
		m_Q_MaxChild_Connection.put(Q_WEST, TASK_WEST);
	}


	

}
