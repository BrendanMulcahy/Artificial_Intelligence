package edu.cwru.eecs.rl.core.qnode;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Set;

import edu.cwru.eecs.futil.Pair;
import edu.cwru.eecs.rl.core.distribution.EmpiricalDistribution;
import edu.cwru.eecs.rl.core.mdp.State;
import edu.cwru.eecs.rl.domains.MaxQGraph;


/**
 * TODO: for HOCQAgent
 * @author feng
 *
 */
public class IQNode_ExitDist extends IQNodeDecorator {

	private static final long serialVersionUID = 2192148239618699413L;
	protected static final double ITA = 0.25;  // learning rate

	protected HashMap<Pair<State, QNode>, EmpiricalDistribution<State>> m_exitDist;
	
	protected MaxQGraph m_maxQGraph;
	
	
	public IQNode_ExitDist(IQNodeInterface qNode) {
		super(qNode);
		m_maxQGraph = getMaxQGraph();
		m_exitDist = new HashMap<Pair<State,QNode>, EmpiricalDistribution<State>>();
	}
	
	/**
	 * 
	 */
	@Override
	public void setValue(State state, QNode qNode, double value, ValueType vType) {
		if(m_maxQGraph.isTerminal(state, qNode.getParentMax())) {
			
		}
	}
	
	
	@Override
	public double getValue(State state, QNode qNode, ValueType vType) {
		
		return 0;
	}
	
	@Override
	public void clearValues(QNode qNode, ValueType vType, Set<State> states) {
		
	}
	
	@Override
	public void printVF(PrintStream ps, boolean real, ValueType vType) {
		
	}

}
