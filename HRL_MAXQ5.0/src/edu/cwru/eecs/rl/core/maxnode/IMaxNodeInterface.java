package edu.cwru.eecs.rl.core.maxnode;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;


import edu.cwru.eecs.rl.agent.AgentType;
import edu.cwru.eecs.rl.core.mdp.Action;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.model.Model;
import edu.cwru.eecs.rl.core.qnode.IQNodeInterface;
import edu.cwru.eecs.rl.domains.MaxQGraph;

public interface IMaxNodeInterface extends Serializable{
	
	public enum ValueType {
		ProjectedValue, PseudoProjectedValue, RCEProjectedValue, HOCQProjectedValue, 
		ManualPseudoReward, BayesPseudoReward, TabularPseudoReward,
		StateValue,
		RMaxQModel; 
	};
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** about value function */
	public double getValue(State state, MaxNode maxNode, ValueType vType);
	public void setValue(State state, MaxNode maxNode, double value, ValueType vType);
	public void clearValues(MaxNode maxNode, ValueType vType, Set<State> states);
	public void printVF(PrintStream ps, boolean real, ValueType vType);
	
	/** about model */
	public Model getModel();
//	public void updateModel(State state, MaxNode maxNode, double reward, State nextState);
//	public double getReward(State state, MaxNode maxNode);
//	public double getTransition(State state, MaxNode maxNode, State nextState);
//	public State getNextState(State state, MaxNode maxNode);
//	public void setReward(State state, MaxNode maxNode, double reward);
//	public void setTransition(State state, MaxNode maxNode, State nextState, double prob);
//	public void clearRewards(MaxNode maxNode);
//	public void clearTransitions(MaxNode maxNode);
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** regular max node */
	public MaxQGraph getMaxQGraph();
	public boolean isRoot();
	public boolean isPrimitive();
	
	public Action getPrimitiveAction();
	
	public void addQNode(IQNodeInterface iQNode);
	public IQNodeInterface getQNode(int index);
	
	public ArrayList<IQNodeInterface> getQNodes();
	
	public int name();
	public String nameString();
	public AgentType getAgentType();
	
}












