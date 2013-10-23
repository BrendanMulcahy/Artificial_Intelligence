package edu.cwru.eecs.rl.domains.taxi;

import java.util.HashMap;

import edu.cwru.eecs.rl.agent.AgentType;
import edu.cwru.eecs.rl.domains.MaxQGraph;
import edu.cwru.eecs.rl.env.EnvSpec;

public class FlatTaxiMaxQGraph extends MaxQGraph {

	private static final long serialVersionUID = 5335625844925375531L;
	
	{
		HTYPE = HType.FLAT; 
	}
	
	public FlatTaxiMaxQGraph(EnvSpec envSpec, AgentType agentType) {
		super(envSpec, agentType, new FlatTaxiDomainSpec());
	}
	
	public static final int TASK_NORTH = 0;
	public static final int TASK_EAST = 1;
	public static final int TASK_SOUTH = 2;
	public static final int TASK_WEST = 3;
	public static final int TASK_PICKUP = 4;
	public static final int TASK_PUTDOWN = 5;
	public static final int TASK_ROOT = 6;
	
	public static final int Q_NORTH = 0;
	public static final int Q_EAST = 1;
	public static final int Q_SOUTH = 2;
	public static final int Q_WEST = 3;
	public static final int Q_PICKUP = 4;
	public static final int Q_PUTDOWN = 5;

	@Override
	protected void initializeTaskNames() {
		m_maxNodeNumber = 7;
		m_primitiveMaxNodeNumber = 6;
		m_taskNames = new String[m_maxNodeNumber];
		m_taskNames[TASK_NORTH] = "TASK_NORTH";
		m_taskNames[TASK_EAST] = "TASK_EAST";
		m_taskNames[TASK_SOUTH] = "TASK_SOUTH";
		m_taskNames[TASK_WEST] = "TASK_WEST";
		m_taskNames[TASK_PICKUP] = "TASK_PICKUP";
		m_taskNames[TASK_PUTDOWN] = "TASK_PUTDOWN";
		m_taskNames[TASK_ROOT] = "TASK_ROOT";
		m_rootMaxNodeIndex = 6;
	}

	@Override
	protected void initializeQNodeNames() {
		m_qNodeNumber = 6;
		m_qNodeNames = new String[m_qNodeNumber];
		m_qNodeNames[Q_NORTH] = "Q_NORTH";
		m_qNodeNames[Q_EAST] = "Q_EAST";
		m_qNodeNames[Q_SOUTH] = "Q_SOUTH";
		m_qNodeNames[Q_WEST] = "Q_WEST";
		m_qNodeNames[Q_PICKUP] = "Q_PICKUP";
		m_qNodeNames[Q_PUTDOWN] = "Q_PUTDOWN";
	}

	@Override
	protected void initializeConnections() {
		/** initialize connections between MaxNode and QNode */
		m_Q_MaxParent_Connection = new HashMap<Integer, Integer>();
		m_Q_MaxParent_Connection.put(Q_PICKUP, TASK_ROOT);
		m_Q_MaxParent_Connection.put(Q_PUTDOWN, TASK_ROOT);
		m_Q_MaxParent_Connection.put(Q_NORTH, TASK_ROOT);
		m_Q_MaxParent_Connection.put(Q_EAST, TASK_ROOT);
		m_Q_MaxParent_Connection.put(Q_SOUTH, TASK_ROOT);
		m_Q_MaxParent_Connection.put(Q_WEST, TASK_ROOT);
		
		/** initialize connections between QNode and MaxNode */
		m_Q_MaxChild_Connection = new HashMap<Integer, Integer>();
		m_Q_MaxChild_Connection.put(Q_PICKUP, TASK_PICKUP);
		m_Q_MaxChild_Connection.put(Q_PUTDOWN, TASK_PUTDOWN);
		m_Q_MaxChild_Connection.put(Q_NORTH, TASK_NORTH);
		m_Q_MaxChild_Connection.put(Q_EAST, TASK_EAST);
		m_Q_MaxChild_Connection.put(Q_SOUTH, TASK_SOUTH);
		m_Q_MaxChild_Connection.put(Q_WEST, TASK_WEST);
	}

}
