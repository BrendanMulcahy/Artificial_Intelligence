package edu.cwru.eecs.rl.core.vf;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Set;


public class TabularVF<T> implements ValueFunction<T> {

	private static final long serialVersionUID = 6333900119701180251L;

	protected double m_defaultValue;
	
	protected HashMap<T, Double> m_values;
	
	protected String m_name = "";

	
	public TabularVF(String name) {
		this(0.123, name);
	}

	public TabularVF(double defaultValue, String name) {
		m_values = new HashMap<T, Double>();
		m_defaultValue = defaultValue;
		m_name = name;
	}
	
	@Override
	public void reset() {
		m_values.clear();
	}
	
	

	@Override
	public void updateValue(T t, double value) {
		m_values.put(t, value);
	}

	@Override
	public double getValue(T t) {
		if(!m_values.containsKey(t))
			m_values.put(t, m_defaultValue);
		return m_values.get(t);
	}

	@Override
	public void print(PrintStream ps, boolean real) {
		//ps.println("This is tabular value function for " + m_name);
		ps.println("~~There are " + m_values.size() + " entries!");
		if(real) {
			Set<T> tSet = m_values.keySet();
			for(T t : tSet){
				ps.println(t.toString() + ": " + m_values.get(t));
			}
		}
		ps.println("~~End");
	}

	@Override
	public void clear(Set<T> keys) {
		for(T key : keys)
			m_values.remove(key);
	}
}
