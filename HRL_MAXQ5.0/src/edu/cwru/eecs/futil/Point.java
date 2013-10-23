package edu.cwru.eecs.futil;

import java.io.Serializable;
import java.util.Random;

public class Point implements Serializable{

	private static final long serialVersionUID = -8608626440095445837L;
	
	public int m_x;
	public int m_y;


	public Point(int x, int y) {
		m_x = x;
		m_y = y;
	}

	public void randomize(int maxX, int maxY, int rng_seed) {
		Random rnd = new Random(rng_seed);
		m_x = rnd.nextInt(maxX);
		m_y = rnd.nextInt(maxY);
	}

	public void randomize(int maxX, int maxY) {
		m_x = (int) (Math.random() * maxX);
		m_y = (int) (Math.random() * maxY);
	}

	public boolean isEqual(int x, int y) {
		return (x == m_x && y == m_y);
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + m_x;
		result = prime * result + m_y;
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
		Point other = (Point) obj;
		if (m_x != other.m_x)
			return false;
		if (m_y != other.m_y)
			return false;
		return true;
	}

	public String toString() {
		return "X: " + m_x + "\tY: " + m_y;
	}

	public static double manhattanDistance(Point a, Point b) {
		return Math.abs(a.m_x - b.m_x) + Math.abs(a.m_y - b.m_y);
	}

	public int getX() {
		return m_x;
	}

	public int getY() {
		return m_y;
	}
}
