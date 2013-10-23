package edu.cwru.eecs.rl.domains;

import java.util.Set;

import edu.cwru.eecs.rl.agent.AgentType;
import edu.cwru.eecs.rl.core.maxnode.IMaxNodeInterface;
import edu.cwru.eecs.rl.core.maxnode.IMaxNode_BMaxQModel;
import edu.cwru.eecs.rl.core.maxnode.IMaxNode_PR_Bayes;
import edu.cwru.eecs.rl.core.maxnode.IMaxNode_PR_Func;
import edu.cwru.eecs.rl.core.maxnode.IMaxNode_PR_Manual;
import edu.cwru.eecs.rl.core.maxnode.IMaxNode_RMaxQModel;
import edu.cwru.eecs.rl.core.maxnode.IMaxNode_VF;
import edu.cwru.eecs.rl.core.qnode.IQNodeInterface;
import edu.cwru.eecs.rl.core.qnode.IQNode_VF;
import edu.cwru.eecs.rl.domains.hallway.FlatHallwayMaxQGraph;
import edu.cwru.eecs.rl.domains.hallway.HallwayMaxQGraph;
import edu.cwru.eecs.rl.domains.simplemaze.SimpleMazeMaxQGraph;
import edu.cwru.eecs.rl.domains.taxi.FlatTaxiMaxQGraph;
import edu.cwru.eecs.rl.domains.taxi.TaxiMaxQGraph;
import edu.cwru.eecs.rl.domains.wargus.FlatWargusMaxQGraph;
import edu.cwru.eecs.rl.domains.wargus.WargusMaxQGraph;
import edu.cwru.eecs.rl.env.Environment;

public class MaxQGraphBuilder {
	public static MaxQGraph buildMaxQGraph(Environment environment, AgentType agentType, MaxQGraph.HType hType) {
		String name = environment.name();
		if(DomainEnum.Taxi.toString().equals(name)) {
			if(hType.equals(MaxQGraph.HType.FLAT))
				return new FlatTaxiMaxQGraph(environment.getEnvSpec(), agentType);
			else
				return new TaxiMaxQGraph(environment.getEnvSpec(), agentType);
		}
		if(DomainEnum.Hallway.toString().equals(name))
			if(hType.equals(MaxQGraph.HType.FLAT))
				return new FlatHallwayMaxQGraph(environment.getEnvSpec(), agentType);
			else
				return new HallwayMaxQGraph(environment.getEnvSpec(), agentType);
		if(DomainEnum.SimpleMaze.toString().equals(name))
			return new SimpleMazeMaxQGraph(environment.getEnvSpec(), agentType);
		if(DomainEnum.Wargus.toString().equals(name))
			if(hType.equals(MaxQGraph.HType.FLAT))
				return new FlatWargusMaxQGraph(environment.getEnvSpec(), agentType);
			else
				return new WargusMaxQGraph(environment.getEnvSpec(), agentType);
		System.err.println("Undefined domain: " + name);
		return null;
	}
	
	public static IMaxNodeInterface buildMaxNode(IMaxNodeInterface regularMaxNode, AgentType agentType) {
		IMaxNodeInterface result = regularMaxNode;
		Set<AgentType.NodeType> nodeTypes = agentType.nodeTypes();
		if(nodeTypes.contains(AgentType.NodeType.V_MaxPR_Manual))
			result = new IMaxNode_PR_Manual(result);
		if(nodeTypes.contains(AgentType.NodeType.V_MaxPR_Func))
			result = new IMaxNode_PR_Func(result);
		if(nodeTypes.contains(AgentType.NodeType.V_MaxPR_Bayes))
			result = new IMaxNode_PR_Bayes(result);
		if(nodeTypes.contains(AgentType.NodeType.M_BMaxQ))
			result = new IMaxNode_BMaxQModel(result);
		if(nodeTypes.contains(AgentType.NodeType.M_RMaxQ))
			result = new IMaxNode_RMaxQModel(result);
		if(nodeTypes.contains(AgentType.NodeType.V_MaxStateValue))
			result = new IMaxNode_VF(result, IMaxNodeInterface.ValueType.StateValue);
		return result;
	}
	
	public static IQNodeInterface buildQNode(IQNodeInterface regularQNode, AgentType agentType) {
		IQNodeInterface result = regularQNode;
		Set<AgentType.NodeType> nodeTypes = agentType.nodeTypes();
		if(nodeTypes.contains(AgentType.NodeType.V_QCompletion))
			result = new IQNode_VF(result, IQNodeInterface.ValueType.CompletionValue);
		if(nodeTypes.contains(AgentType.NodeType.V_QPseudoCompletion))
			result = new IQNode_VF(result, IQNodeInterface.ValueType.PseudoCompletionValue);
		if(nodeTypes.contains(AgentType.NodeType.V_QExternal))
			result = new IQNode_VF(result, IQNodeInterface.ValueType.ExternalValue);
		if(nodeTypes.contains(AgentType.NodeType.V_QQValue))
			result = new IQNode_VF(result, IQNodeInterface.ValueType.QValue);
		return result;
	}
	
}













