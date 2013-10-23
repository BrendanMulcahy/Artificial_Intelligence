package edu.cwru.eecs.rl.core.distribution;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;


public class SparseDirichlet implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static Random rng = new Random(Calendar.getInstance()
			.getTimeInMillis() + Thread.currentThread().getId());
	
	private double m_prior;
	private HashMap<Integer, Integer> m_visitedValues;
	private int m_L;
	private double m_sum;

	public SparseDirichlet(int L) {
		m_L = L;
		m_prior = 0.00001;
		m_visitedValues = new HashMap<Integer, Integer>();
		m_sum = m_prior*m_L;
	}

	public int sample()
	{
		double random = rng.nextDouble()*m_sum;
		double current_sum = 0;
		for(int i=0; i<m_L; i++)
		{
			current_sum+=m_prior;
			if(current_sum>random)
				return i;
		}
		Set<Integer> keySet = m_visitedValues.keySet();
		for (Iterator<Integer> iterator = keySet.iterator(); iterator.hasNext();) {
			Integer integer = (Integer) iterator.next();
			current_sum += m_visitedValues.get(integer);
			if(current_sum>random)
				return integer;
		}
		return -1;
	}

	public void updatePosterior(int index, int N) {
		if(m_visitedValues.containsKey((Integer)index)) {
			Integer oldValue = m_visitedValues.get(index);
			m_visitedValues.put(index, oldValue+N);
		}
		else {
			m_visitedValues.put(index, (Integer)N);
		}
		m_sum += N;
	}

}
