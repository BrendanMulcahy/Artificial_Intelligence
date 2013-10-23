package edu.cwru.eecs.rl.core.vf;

import java.io.PrintStream;
import java.util.Arrays;

import edu.cwru.eecs.futil.misc.MathMisc;



public abstract class LinearVF<T> implements ValueFunction<T> {
	
	private static final long serialVersionUID = 1501594862684381191L;

	private double m_alpha = 0.2;
	
	private int m_featureSize;
	private double[] m_coefficients;
	private double m_intercept;
	
	public LinearVF(int featureSize) {
		m_featureSize = featureSize;
		m_coefficients = new double[m_featureSize];
		m_intercept = 0;
	}

	@Override
	public void reset() {
		Arrays.fill(m_coefficients, 0);
		m_intercept = 0;
	}

	@Override
	public void updateValue(T t, double value) {
		double[] features = getFeatures(t);
		double currentQValue = getValue(features);
		double difference = value - currentQValue;
		for(int i=0; i<m_featureSize; i++) {
			m_coefficients[i] += m_alpha*difference*features[i];
		}
		m_intercept += m_alpha*difference;
	}

	@Override
	public double getValue(T t) {
		double[] features = getFeatures(t);
		return getValue(features);
	}
	
	private double getValue(double[] features) {
		return MathMisc.crossProduct(m_coefficients, features)+m_intercept;
	}
	
	protected abstract double[] getFeatures(T t);

	@Override
	public void print(PrintStream ps, boolean real) {
		ps.println("This is linear FA VF.");
		ps.println("coefficients are: ");
		for(int i=0; i<m_featureSize; i++) {
			ps.print(m_coefficients[i] + "\t");
		}
		ps.println("\nintercept: \n" + m_intercept);
	}

}
