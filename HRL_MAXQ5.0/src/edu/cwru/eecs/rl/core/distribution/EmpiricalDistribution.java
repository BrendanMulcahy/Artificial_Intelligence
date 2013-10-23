package edu.cwru.eecs.rl.core.distribution;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class EmpiricalDistribution<T> implements Serializable {
	
	private static final long serialVersionUID = -4999610846931504575L;
	
	private Map<T, Integer> m_counter;
	private double m_sum;
	
	public EmpiricalDistribution() {
		m_counter = new HashMap<T, Integer>();
		m_sum = 0;
	}
	
	public void update(T t) {
		update(t, 1);
	}
	
	public void update(T t, int obsNum) {
		if(obsNum<=0)
			return ;
		if(!m_counter.containsKey(t)) 
			m_counter.put(t, 0);
		m_counter.put(t, m_counter.get(t)+obsNum);
		m_sum += obsNum;
	}
	
	public double getProb(T t) {
		if(!m_counter.containsKey(t) || m_sum<=0)
			return 0;
		return m_counter.get(t)/m_sum;
	}
	
	public int getCount(T t) {
		if(!m_counter.containsKey(t) || m_sum<=0)
			return 0;
		return m_counter.get(t);
	}
	
}
