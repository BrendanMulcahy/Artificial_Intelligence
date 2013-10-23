package edu.cwru.eecs.rl.core.qnode;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Set;

import edu.cwru.eecs.rl.agent.AgentType;
import edu.cwru.eecs.rl.core.maxnode.IMaxNodeInterface;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.domains.MaxQGraph;


public interface IQNodeInterface extends Serializable{
	
	public enum ValueType {
		CompletionValue, PseudoCompletionValue, ExternalValue,
		QValue; 
	};
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public double getValue(State state, QNode qNode, ValueType vType);
	public void setValue(State state, QNode qNode, double value, ValueType vType);
	public void clearValues(QNode qNode, ValueType vType, Set<State> states);
	public void printVF(PrintStream ps, boolean real, ValueType vType);
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** regular q node */
	public IMaxNodeInterface getParentIMaxNode();
	public IMaxNodeInterface getChildIMaxNode();
	
	public void setParentIMaxNode(IMaxNodeInterface iMaxNode);
	public void setChildIMaxNode(IMaxNodeInterface iMaxNode);
	
	public int name();
	public String nameString();
	public MaxQGraph getMaxQGraph();
	public AgentType getAgentType();
}




