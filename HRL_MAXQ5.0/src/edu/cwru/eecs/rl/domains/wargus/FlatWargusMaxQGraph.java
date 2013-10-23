package edu.cwru.eecs.rl.domains.wargus;

import java.util.HashMap;

import edu.cwru.eecs.rl.agent.AgentType;
import edu.cwru.eecs.rl.domains.MaxQGraph;
import edu.cwru.eecs.rl.env.EnvSpec;

public class FlatWargusMaxQGraph extends MaxQGraph {

	private static final long serialVersionUID = 1507568278412593269L;

	{
		HTYPE = HType.FLAT; 
	}
	
	public FlatWargusMaxQGraph(EnvSpec envSpec, AgentType agentType) {
		super(envSpec, agentType, new FlatWargusDomainSpec());
	}
	
	public static final int TASK_MINEGOLD = 0;
	public static final int TASK_CHOPWOOD = 1;
	public static final int TASK_DEPOSIT = 2;
	public static final int TASK_GOTO = 3;
	public static final int TASK_ROOT = 4;
	
	public static final int Q_MINEGOLD = 0;
	public static final int Q_CHOPWOOD = 1;
	public static final int Q_DEPOSIT = 2;
	public static final int Q_GOTO = 3;

	@Override
	protected void initializeTaskNames() {
		m_maxNodeNumber = 5;
		m_primitiveMaxNodeNumber = 4;
		m_taskNames = new String[m_maxNodeNumber];
		m_taskNames[TASK_MINEGOLD] = "TASK_MINEGOLD";
		m_taskNames[TASK_CHOPWOOD] = "TASK_CHOPWOOD";
		m_taskNames[TASK_DEPOSIT] = "TASK_DEPOSIT";
		m_taskNames[TASK_GOTO] = "TASK_GOTO";
		m_taskNames[TASK_ROOT] = "TASK_ROOT";
		m_rootMaxNodeIndex = 4;
	}

	@Override
	protected void initializeQNodeNames() {
		m_qNodeNumber = 4;
		m_qNodeNames = new String[m_qNodeNumber];
		m_qNodeNames[Q_MINEGOLD] = "Q_MINEGOLD";
		m_qNodeNames[Q_CHOPWOOD] = "Q_CHOPWOOD";
		m_qNodeNames[Q_DEPOSIT] = "Q_DEPOSIT";
		m_qNodeNames[Q_GOTO] = "Q_GOTO";
	}

	@Override
	protected void initializeConnections() {
		/** initialize connections between MaxNode and QNode */
		m_Q_MaxParent_Connection = new HashMap<Integer, Integer>();
		m_Q_MaxParent_Connection.put(Q_MINEGOLD, TASK_ROOT);
		m_Q_MaxParent_Connection.put(Q_CHOPWOOD, TASK_ROOT);
		m_Q_MaxParent_Connection.put(Q_DEPOSIT, TASK_ROOT);
		m_Q_MaxParent_Connection.put(Q_GOTO, TASK_ROOT);
		
		/** initialize connections between QNode and MaxNode */
		m_Q_MaxChild_Connection = new HashMap<Integer, Integer>();
		m_Q_MaxChild_Connection.put(Q_MINEGOLD, TASK_MINEGOLD);
		m_Q_MaxChild_Connection.put(Q_CHOPWOOD, TASK_CHOPWOOD);
		m_Q_MaxChild_Connection.put(Q_DEPOSIT, TASK_DEPOSIT);
		m_Q_MaxChild_Connection.put(Q_GOTO, TASK_GOTO);
	}

}
