package edu.cwru.eecs.futil;

import java.io.Serializable;

public class Pair<L, R> implements Serializable {
 
	private static final long serialVersionUID = 724851935409534353L;
	
	private L m_l;
	private R m_r;
	
	public Pair(L l, R r) {
		m_l = l;
		m_r = r;
	}
	
	public L left(){
		return m_l;
	}
	
	public R right() {
		return m_r;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_l == null) ? 0 : m_l.hashCode());
		result = prime * result + ((m_r == null) ? 0 : m_r.hashCode());
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
		Pair<?, ?> other = (Pair<?, ?>) obj;
		if (m_l == null) {
			if (other.m_l != null)
				return false;
		} else if (!m_l.equals(other.m_l))
			return false;
		if (m_r == null) {
			if (other.m_r != null)
				return false;
		} else if (!m_r.equals(other.m_r))
			return false;
		return true;
	}

	public String toString() {
		return m_l.toString() + " " + m_r.toString();
	}
}
