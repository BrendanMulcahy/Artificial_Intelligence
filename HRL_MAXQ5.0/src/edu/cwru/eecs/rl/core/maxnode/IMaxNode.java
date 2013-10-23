package edu.cwru.eecs.rl.core.maxnode;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import edu.cwru.eecs.rl.agent.AgentType;
import edu.cwru.eecs.rl.core.mdp.Action;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.core.model.Model;
import edu.cwru.eecs.rl.core.qnode.IQNodeInterface;
import edu.cwru.eecs.rl.core.vf.TabularVF;
import edu.cwru.eecs.rl.domains.MaxQGraph;




/**
 * This is inner max node, which supports only maxq-0 algorithm.
 * In other words, it contains information about projected value function for primitive node.
 * @author Feng
 *
 */
public class IMaxNode implements IMaxNodeInterface{

	private static final long serialVersionUID = 61799428411516897L;
	
	protected AgentType m_agentType;
	
	/** general information */
	protected MaxQGraph m_maxQGraph;
	protected int m_name; // index in the max q graph
	protected boolean m_isPrimitive;
	private ArrayList<IQNodeInterface> m_subIQNodes;
	
	/** value functions: only for primitive max node */
	private HashMap<MaxNode, TabularVF<State>> m_projectedValueFunction;
	
	public IMaxNode(int name, boolean isPrimitive, MaxQGraph maxQGraph) {
		initMaxNode(name, isPrimitive, maxQGraph);
	}
	
	protected void initMaxNode(int name, boolean isPrimitive, MaxQGraph maxQGraph) {
		m_maxQGraph = maxQGraph;
		m_agentType = m_maxQGraph.getAgentType();
		m_name = name;
		m_isPrimitive = isPrimitive;
		m_subIQNodes = new ArrayList<IQNodeInterface>();
		m_projectedValueFunction = new HashMap<MaxNode, TabularVF<State>>();
	}
	
	/**
	 * Adder of q node
	 */
	public void addQNode(IQNodeInterface iQNode) {
		m_subIQNodes.add(iQNode);
	}
	
	/**
	 * Getter of QNode
	 * @param index
	 * @return child q node (internal) according to index
	 */
	public IQNodeInterface getQNode(int index) {
		return m_subIQNodes.get(index);
	}
	
	public int name() {
		return m_name;
	}
	
	/**
	 * @return the name string of current node
	 */
	public String nameString() {
		return m_maxQGraph.getMaxNodeName(m_name);
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + m_name;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IMaxNode other = (IMaxNode) obj;
		if (m_name != other.m_name)
			return false;
		return true;
	}

	public MaxQGraph getMaxQGraph() {
		return m_maxQGraph;
	}
	
	/**
	 * @return whether this node is primitive or not
	 */
	public boolean isPrimitive() {
		return m_isPrimitive;
	}
	
	public boolean isRoot() {
		return this.m_name == m_maxQGraph.getRootIndex();
		//return this.equals(m_maxQGraph.getRoot());
	}
	
	/**
	 * @return node type
	 */
	public AgentType getAgentType() {
		return m_agentType;
	}
	
	@Override
	public Action getPrimitiveAction() {
		if(m_isPrimitive) {
			Action action = new Action(m_name);
			return action;
		}
		return null;
	}
	
	@Override
	public ArrayList<IQNodeInterface> getQNodes() {
		return m_subIQNodes;
	}
	
	
	/**********************************************************************
	 *  about value function 
	 */
	
	@Override
	public double getValue(State state, MaxNode maxNode, ValueType vType) {
		if(vType.equals(ValueType.ProjectedValue))
			return getProjectedValue(state, maxNode);
		else {
			System.err.println("ValueType no supported in getValue(): " + vType.toString());
			return Double.NaN;
		}
	}

	@Override
	public void setValue(State state, MaxNode maxNode, double value,
			ValueType vType) {
		if(vType.equals(ValueType.ProjectedValue))
			setProjectedValue(state, maxNode, value);
		else {
			System.err.println("ValueType no supported in setValue(): " + vType.toString());
		}
	}

	@Override
	public void printVF(PrintStream ps, boolean real, ValueType vType) {
		if(vType.equals(ValueType.ProjectedValue))
			printVF(ps, real);
		else {
			System.err.println("ValueType no supported in printValues(): " + vType.toString());
		}
	}
	
	@Override
	public void clearValues(MaxNode maxNode, ValueType vType, Set<State> states) {
		System.err.println("Method no implemented: clearValues()");
	}
	
	/**********************************************************************
	 *  about model
	 */
	
	@Override
	public Model getModel() {
		System.err.println("Method no implemented: getModel()");
		return null;
	}
	
	
	
	
	/******************************************************************
	 * private methods
	 */

	protected double getProjectedValue(State state, MaxNode maxNode) {
		if(m_isPrimitive) {
			if(m_agentType.hasStateAbstraction())
				state = m_maxQGraph.maxStateAbsMapping(state, maxNode);
			if(!m_projectedValueFunction.containsKey(maxNode))
				m_projectedValueFunction.put(maxNode, new TabularVF<State>(maxNode.getNodeDescription()));
			return m_projectedValueFunction.get(maxNode).getValue(state);
		}
		else {
			System.err.println("Shouldn't ask for projected value in non-primitive subtask: " + nameString());
			return Double.NaN;
		}
	}

	protected void setProjectedValue(State state, MaxNode maxNode, double value) {
		if(m_isPrimitive) {
			if(m_agentType.hasStateAbstraction())
				state = m_maxQGraph.maxStateAbsMapping(state, maxNode);
			if(!m_projectedValueFunction.containsKey(maxNode))
				m_projectedValueFunction.put(maxNode, new TabularVF<State>(maxNode.getNodeDescription()));
			m_projectedValueFunction.get(maxNode).updateValue(state, value);
		}
		else {
			System.err.println("Shouldn't set projected value in non-primitive subtask: " + nameString());
		}
	}


	protected void printVF(PrintStream ps, boolean real) {
		if(m_isPrimitive) {
			Set<MaxNode> maxNodeSet = m_projectedValueFunction.keySet();
			for(MaxNode tempMaxNode : maxNodeSet)
				m_projectedValueFunction.get(tempMaxNode).print(ps, real);
		} else {
			System.err.println("Sorry, can't output value functions for non-primitive action: " + nameString());
		}
	}

	
	

	
}



