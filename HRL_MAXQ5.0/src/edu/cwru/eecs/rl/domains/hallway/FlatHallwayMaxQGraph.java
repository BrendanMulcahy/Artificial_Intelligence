package edu.cwru.eecs.rl.domains.hallway;

import java.util.HashMap;

import edu.cwru.eecs.rl.agent.AgentType;
import edu.cwru.eecs.rl.domains.MaxQGraph;
import edu.cwru.eecs.rl.env.EnvSpec;

public class FlatHallwayMaxQGraph extends MaxQGraph {

	private static final long serialVersionUID = 8423629734537871949L;

	{
		HTYPE = HType.FLAT; 
	}
	
	public FlatHallwayMaxQGraph(EnvSpec envSpec, AgentType agentType) {
		super(envSpec, agentType, new FlatHallwayDomainSpec());
	}

	public static final int TASK_MOVE = 0;
	public static final int TASK_ROOT = 1;
	
	public static final int Q_MOVE = 0;
	
	@Override
	protected void initializeTaskNames() {
		m_maxNodeNumber = 2;
		m_primitiveMaxNodeNumber = 1;
		m_taskNames = new String[m_maxNodeNumber];
		m_taskNames[TASK_MOVE] = "TASK_MOVE";
		m_taskNames[TASK_ROOT] = "TASK_ROOT";
		m_rootMaxNodeIndex = 1;
	}

	@Override
	protected void initializeQNodeNames() {
		m_qNodeNumber = 1;
		m_qNodeNames = new String[m_qNodeNumber];
		m_qNodeNames[Q_MOVE] = "Q_MOVE";
	}

	@Override
	protected void initializeConnections() {
		/** initialize connections between MaxNode and QNode */
		m_Q_MaxParent_Connection = new HashMap<Integer, Integer>();
		m_Q_MaxParent_Connection.put(Q_MOVE, TASK_ROOT);
		
		/** initialize connections between QNode and MaxNode */
		m_Q_MaxChild_Connection = new HashMap<Integer, Integer>();
		m_Q_MaxChild_Connection.put(Q_MOVE, TASK_MOVE);
	}

}
