package edu.cwru.eecs.rl.domains.wargus;

import java.util.HashMap;

import edu.cwru.eecs.rl.agent.AgentType;
import edu.cwru.eecs.rl.domains.MaxQGraph;
import edu.cwru.eecs.rl.env.EnvSpec;

public class WargusMaxQGraph extends MaxQGraph {

	private static final long serialVersionUID = -6694168022418038336L;

	{
		HTYPE = HType.HIERARCHY; 
	}
	
	public WargusMaxQGraph(EnvSpec envSpec, AgentType agentType) {
		super(envSpec, agentType, new WargusDomainSpec());
	}

	public static final int TASK_MINEGOLD = 0;
	public static final int TASK_CHOPWOOD = 1;
	public static final int TASK_DEPOSIT = 2;
	public static final int TASK_GOTO = 3;
	public static final int TASK_GETGOLD = 4;
	public static final int TASK_GETWOOD = 5;
	public static final int TASK_GWDEPOSIT = 6;
	public static final int TASK_ROOT = 7;
	
	public static final int Q_MINEGOLD = 0;
	public static final int Q_CHOPWOOD = 1;
	public static final int Q_DEPOSIT = 2;
	public static final int Q_GOTO_FOR_GETGOLD = 3;
	public static final int Q_GOTO_FOR_GETWOOD = 4;
	public static final int Q_GOTO_FOR_GWDEPOSIT = 5;
	public static final int Q_GETGOLD = 6;
	public static final int Q_GETWOOD = 7;
	public static final int Q_GWDEPOSIT = 8;
	
	@Override
	protected void initializeTaskNames() {
		m_maxNodeNumber = 8;
		m_primitiveMaxNodeNumber = 4;
		m_taskNames = new String[m_maxNodeNumber];
		m_taskNames[TASK_MINEGOLD] = "TASK_MINEGOLD";
		m_taskNames[TASK_CHOPWOOD] = "TASK_CHOPWOOD";
		m_taskNames[TASK_DEPOSIT] = "TASK_DEPOSIT";
		m_taskNames[TASK_GOTO] = "TASK_GOTO";
		m_taskNames[TASK_GETGOLD] = "TASK_GETGOLD";
		m_taskNames[TASK_GETWOOD] = "TASK_GETWOOD";
		m_taskNames[TASK_GWDEPOSIT] = "TASK_GWDEPOSIT";
		m_taskNames[TASK_ROOT] = "TASK_ROOT";
		m_rootMaxNodeIndex = 7;
	}

	@Override
	protected void initializeQNodeNames() {
		m_qNodeNumber = 9;
		m_qNodeNames = new String[m_qNodeNumber];
		m_qNodeNames[Q_MINEGOLD] = "Q_MINEGOLD";
		m_qNodeNames[Q_CHOPWOOD] = "Q_CHOPWOOD";
		m_qNodeNames[Q_DEPOSIT] = "Q_DEPOSIT";
		m_qNodeNames[Q_GOTO_FOR_GETGOLD] = "Q_GOTO_FOR_GETGOLD";
		m_qNodeNames[Q_GOTO_FOR_GETWOOD] = "Q_GOTO_FOR_GETWOOD";
		m_qNodeNames[Q_GOTO_FOR_GWDEPOSIT] = "Q_GOTO_FOR_GWDEPOSIT";
		m_qNodeNames[Q_GETGOLD] = "Q_GETGOLD";
		m_qNodeNames[Q_GETWOOD] = "Q_GETWOOD";
		m_qNodeNames[Q_GWDEPOSIT] = "Q_GWDEPOSIT";
	}

	@Override
	protected void initializeConnections() {
		/** initialize connections between MaxNode and QNode */
		m_Q_MaxParent_Connection = new HashMap<Integer, Integer>();
		m_Q_MaxParent_Connection.put(Q_GETGOLD, TASK_ROOT);
		m_Q_MaxParent_Connection.put(Q_GETWOOD, TASK_ROOT);
		m_Q_MaxParent_Connection.put(Q_GWDEPOSIT, TASK_ROOT);
		m_Q_MaxParent_Connection.put(Q_MINEGOLD, TASK_GETGOLD);
		m_Q_MaxParent_Connection.put(Q_GOTO_FOR_GETGOLD, TASK_GETGOLD);
		m_Q_MaxParent_Connection.put(Q_CHOPWOOD, TASK_GETWOOD);
		m_Q_MaxParent_Connection.put(Q_GOTO_FOR_GETWOOD, TASK_GETWOOD);
		m_Q_MaxParent_Connection.put(Q_DEPOSIT, TASK_GWDEPOSIT);
		m_Q_MaxParent_Connection.put(Q_GOTO_FOR_GWDEPOSIT, TASK_GWDEPOSIT);
		
		/** initialize connections between QNode and MaxNode */
		m_Q_MaxChild_Connection = new HashMap<Integer, Integer>();
		m_Q_MaxChild_Connection.put(Q_GETGOLD, TASK_GETGOLD);
		m_Q_MaxChild_Connection.put(Q_GETWOOD, TASK_GETWOOD);
		m_Q_MaxChild_Connection.put(Q_GWDEPOSIT, TASK_GWDEPOSIT);
		m_Q_MaxChild_Connection.put(Q_MINEGOLD, TASK_MINEGOLD);
		m_Q_MaxChild_Connection.put(Q_GOTO_FOR_GETGOLD, TASK_GOTO);
		m_Q_MaxChild_Connection.put(Q_CHOPWOOD, TASK_CHOPWOOD);
		m_Q_MaxChild_Connection.put(Q_GOTO_FOR_GETWOOD, TASK_GOTO);
		m_Q_MaxChild_Connection.put(Q_DEPOSIT, TASK_DEPOSIT);
		m_Q_MaxChild_Connection.put(Q_GOTO_FOR_GWDEPOSIT, TASK_GOTO);
		
	}

}
