package edu.cwru.eecs.rl.core.mdp;

import java.io.Serializable;
import java.util.Arrays;

/**
 * This class defines a fix length double array. 
 * Provide commonly used methods.
 * @author feng
 *
 */
public abstract class AbstractType implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	protected String m_name = "Fix Length Vector";
	protected int m_size;
	private double[] m_values;
	

	public AbstractType(int size) {
		m_size = size;
		m_values = new double[m_size];
		Arrays.fill(m_values, 0.0);
	}

	public void setValue(int index, double value) {
		if(index<0 || index>=m_size)
			throw new ArrayIndexOutOfBoundsException(index);
		m_values[index] = value;
	}

	public double getValue(int index) {
		if(index<0 || index>=m_size)
			throw new ArrayIndexOutOfBoundsException(index);
		return m_values[index];
	}

	public int size() {
		return m_size;
	}
	
	public int getNumVariable() {
		return m_size;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + m_size;
		result = prime * result + Arrays.hashCode(m_values);
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
		AbstractType other = (AbstractType) obj;
		if (m_size != other.m_size)
			return false;
		if (!Arrays.equals(m_values, other.m_values))
			return false;
		return true;
	}

	public String toString() {
		if(m_size==0)
			return "";
		String string = m_name + ": ";
		for (int i = 0; i < m_size; i++)
			string += m_values[i] + "\t";
		return string;
	}
	
}
