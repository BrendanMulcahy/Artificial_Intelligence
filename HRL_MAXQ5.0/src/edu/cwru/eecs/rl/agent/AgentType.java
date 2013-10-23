package edu.cwru.eecs.rl.agent;

import java.util.HashSet;
import java.util.Set;

public enum AgentType {
	FlatQ (new NodeType[]{}),
	MaxQPrNo (new NodeType[]{
			NodeType.V_MaxProjected,
			NodeType.V_QCompletion 
	}), 
	MaxQPrMan (new NodeType[] {
			NodeType.V_MaxProjected,
			NodeType.V_MaxPR_Manual,
			NodeType.V_QCompletion,
			NodeType.V_QPseudoCompletion
	}), 
	MaxQPrBayes (new NodeType[] {
			NodeType.V_MaxProjected,
			NodeType.V_MaxPR_Bayes,
			NodeType.V_QCompletion,
			NodeType.V_QPseudoCompletion
	}), 
	MaxQPrFunc (new NodeType[] {
			NodeType.V_MaxProjected,
			NodeType.V_MaxPR_Func,
			NodeType.V_QCompletion,
			NodeType.V_QPseudoCompletion
	}),
	BMaxQPrNo (new NodeType[]{
			NodeType.V_MaxProjected,
			NodeType.V_QCompletion,
			NodeType.M_BMaxQ
	}), 
	BMaxQPrMan (new NodeType[] {
			NodeType.V_MaxProjected,
			NodeType.V_MaxPR_Manual,
			NodeType.V_QCompletion,
			NodeType.V_QPseudoCompletion,
			NodeType.M_BMaxQ
	}), 
	BMaxQPrBayes (new NodeType[] {
			NodeType.V_MaxProjected,
			NodeType.V_MaxPR_Bayes,
			NodeType.V_QCompletion,
			NodeType.V_QPseudoCompletion,
			NodeType.M_BMaxQ
	}), 
	ALispQ (new NodeType[] {
			NodeType.V_MaxProjected,
			NodeType.V_QCompletion,
			NodeType.V_QExternal
	}),
	HOCQ (new NodeType[] {   // TODO
			NodeType.V_MaxProjected,
			NodeType.V_QCompletion,
	}), 
	RMaxMaxQ (new NodeType[] {
			NodeType.V_MaxStateValue,
			NodeType.V_QQValue,
			NodeType.M_RMaxQ
	});
	
	private final Set<NodeType> m_nodeTypes;
	
	private AgentType(NodeType[] types) {
		m_nodeTypes = new HashSet<AgentType.NodeType>();
		for(NodeType type : types) {
			m_nodeTypes.add(type);
		}
	}
	
	public Set<NodeType> nodeTypes() {
		return m_nodeTypes;
	}
	
	public enum NodeType {
		V_MaxProjected,
		V_MaxStateValue,
		V_MaxPR_Manual,
		V_MaxPR_Bayes,
		V_MaxPR_Func,
		V_QCompletion,
		V_QPseudoCompletion,
		V_QExternal,
		V_QQValue,
		M_BMaxQ,
		M_RMaxQ;
	}
	
	public boolean hasStateAbstraction() {
		return true;
	}
	
	public boolean hasPseudoReward() {
		return m_nodeTypes.contains(NodeType.V_MaxPR_Manual)
			|| m_nodeTypes.contains(NodeType.V_MaxPR_Bayes)
			|| m_nodeTypes.contains(NodeType.V_MaxPR_Func);
	}
	
	public boolean hasPseudoReward_Manual() {
		return m_nodeTypes.contains(NodeType.V_MaxPR_Manual);
	}
	
	public boolean hasPseudoReward_Func() {
		return m_nodeTypes.contains(NodeType.V_MaxPR_Func);
	}

	public boolean hasPseudoReward_Bayes() {
		return m_nodeTypes.contains(NodeType.V_MaxPR_Bayes);
	}
	
}





















