package edu.cwru.eecs.rl.core.vf;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Set;

public interface ValueFunction<T> extends Serializable{
	
	public abstract void updateValue(T t, double value);
	public abstract double getValue(T t);
	
	public abstract void reset();
	public abstract void clear(Set<T> keys);
	public void print(PrintStream ps, boolean real);
	
}
