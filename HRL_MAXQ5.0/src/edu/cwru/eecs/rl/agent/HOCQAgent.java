package edu.cwru.eecs.rl.agent;

import edu.cwru.eecs.rl.core.maxnode.IMaxNodeInterface;
import edu.cwru.eecs.rl.env.Environment;


/**
 * HOCQ learning agent (Hierarchically Optimal Cascaded Q-Learning algorithm)
 * Refer to: A Compact, Hierarchically Optimal Q-function Decomposition
 * by  Bhaskara marthi,  Stuart J. Russell and David Andre, UAI 2006
 * 
 * Q(w, a) = Q_r(w, a) + Q_c(w, a) + Q_e(w, a)
 * Q_e(w, a) = E_{Pe(w^e|w,a)} [V(w^e)]
 * 
 * @author Feng
 *
 */
public class HOCQAgent extends MaxQAgent {

	private static final long serialVersionUID = 8026547390249644619L;

	public HOCQAgent(Environment environment) {
		super(environment, AgentType.HOCQ);
		m_qType = IMaxNodeInterface.ValueType.HOCQProjectedValue;
	}
	
	public HOCQAgent(Environment environment, String[] args) {
		this(environment);
	}
	
	public static String getUsage() {
		return "arg0";
	}
	
	/******************************************************************
	 * All the updating methods
	 ******************************************************************/
	
	

}
