package edu.cwru.eecs.rl.core.distribution;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Vector;

import jsc.distributions.StudentsT;

public class GaussianGammaWindow implements Serializable{
	
	// design for bayes pseudo reward, because at the beginning, the "observation" of pseudo reward are inaccurate, 
	// and normally too large, so need to use a window to control the affect of early observations. 
	
	private static final long serialVersionUID = 1L;

	public static final int INIT_WINDOW_SIZE = 500;
	public static final int FINAL_WINDOW_SIZE = 500;
	public static final double DECAY_RATE = 0.995;
	public static final double[] FACTORS;  // from big to small
	public static double SUM;
	static {
		FACTORS = new double[INIT_WINDOW_SIZE];
		double temp = 1;
		SUM = 0;
		for(int i=0; i<INIT_WINDOW_SIZE; i++) {
			FACTORS[i] = temp;
			SUM += temp;
			temp *= DECAY_RATE;
		}
	}
	
	
	/**
	 * 	Sample τ from a gamma distribution with parameters α and β
		Sample x from a normal distribution with mean μ and variance 1 / (λτ)
	 */
	
	private double m_oldmu;
	private double m_oldlambda;
	private double m_oldalpha;
	private double m_oldbeta;

	private double m_mu; 		// 
	private double m_lambda;
	private double m_alpha;
	private double m_beta;
	
	private int m_maxSize;
	private int m_currentSize;
	private LinkedList<Double> m_obs;
	private Vector<Double> m_factors;
	protected double m_factorSUM;
	protected double m_M1n;
	protected double m_M2n;
	

	public GaussianGammaWindow() {
		this(INIT_WINDOW_SIZE);
	}
	
	private GaussianGammaWindow(int maxSize) {
		m_oldmu = 0;
		m_oldlambda = 100;
		m_oldalpha = 1;
		m_oldbeta = 1;
		
		m_mu = 0;
		m_lambda = 100;
		m_alpha = 1;
		m_beta = 1;
		
		m_maxSize = maxSize;
		m_currentSize = -1;
		m_M1n = 0;
		m_M2n = 0;
		m_obs = new LinkedList<Double>();
		m_factors = new Vector<Double>();
	}
	
	public void updatePosterior(double observation) {
		m_currentSize++;
		if(m_currentSize>=m_maxSize) { // exceed, should delete the first one
			
			double first = m_obs.removeFirst();
			double firstFactor = m_factors.get(m_currentSize-1);
			m_M1n -= first*firstFactor;
			m_M2n -= first*first*firstFactor;
			
			m_currentSize = m_maxSize-1;
			
		} else {  // increase the size of m_obs
			double newFactor;
			if(m_factors.size()==0)
				newFactor = 1;
			else
				newFactor = m_factors.get(m_currentSize-1)*DECAY_RATE;
			m_factors.add(newFactor);
			m_factorSUM += newFactor;
		}
		
		m_obs.add(observation);
		m_M1n *= DECAY_RATE;
		m_M2n *= DECAY_RATE;
		m_M1n += observation;
		m_M2n += observation*observation;
		double n = m_factorSUM;
		double M1 = m_M1n/n;
		double M2 = m_M2n/n;
		
		m_mu = (m_oldlambda * m_oldmu + m_M1n) / (m_oldlambda + n);
		m_lambda = m_oldlambda + n;
		m_alpha = m_oldalpha + 0.5*n;
		m_beta = m_oldbeta + 0.5*n*(M2-M1*M1)+0.5*n*m_oldlambda*(M1-m_oldmu)*(M1-m_oldmu)/(m_oldlambda+n);
		
		//System.out.println("mu: " + m_mu + " lambda: " + m_lambda + " alpha: " + m_alpha + " beta: " + m_beta + " M1: " + M1 + " M2: " + M2);
	}
	
	public void incMaxSize(int inc) {
		m_maxSize += inc;
		if(m_maxSize>FINAL_WINDOW_SIZE)
			m_maxSize = FINAL_WINDOW_SIZE;
	}
	
	/**
	 * get mu
	 * @return m_mu of gaussian gamma distribution
	 */
	public double getMAPMean() {
		return m_mu;
	}

	public double sampleGaussianMean() {
		double d = 2 * m_alpha;
		StudentsT studentsT = new StudentsT(d);
		double random = Math.random();
		double x = studentsT.inverseCdf(random);
		x = x / Math.sqrt(m_lambda * m_alpha / m_beta) + m_mu;
		return x;
	}
	
	public static void main(String[] args) {
		GaussianGammaWindow gaussianGammaWindow = new GaussianGammaWindow();
		//
		for(int i=0; i<500; i++)
			gaussianGammaWindow.updatePosterior(10);
		System.out.println("sampling after 50 times of update with 10");
		for(int i=0; i<10; i++)
			System.out.println(sample(gaussianGammaWindow));
		//
		for(int i=0; i<100; i++)
			gaussianGammaWindow.updatePosterior(100);
		System.out.println("sampling after 100 times of update with 100");
		for(int i=0; i<10; i++)
			System.out.println(sample(gaussianGammaWindow));
		//
		for(int i=0; i<50; i++)
			gaussianGammaWindow.updatePosterior(10);
		System.out.println("sampling after 50 times of update with 10");
		for(int i=0; i<10; i++)
			System.out.println(sample(gaussianGammaWindow));
		
		
		
//		gaussianGammaWindow.incMaxSize(450);
//		//gaussianGamma.updatePosterior(1000);
//		//gaussianGamma.updatePosterior(-1000);
//		for (int i = 0; i < 10; i++)
//			gaussianGammaWindow.updatePosterior(10);
//		for(int i=0; i<1000; i++) {
//			System.out.println(i + ": " + sample(gaussianGammaWindow) + "\t factor sum: " + 
//					gaussianGammaWindow.m_factorSUM + "\t M1n: " + gaussianGammaWindow.m_M1n + "\t M2n: " + gaussianGammaWindow.m_M2n);
//			gaussianGammaWindow.updatePosterior(1000);
//		}
	}
	
	public static double sample(GaussianGammaWindow gaussianGammaWindow) {
		double sample;
		double average = 0;
		int number = 10;
		for (int i = 0; i < number; i++) {
			sample = gaussianGammaWindow.sampleGaussianMean();
			average += sample;
		}
		average /= number;
		return average;
	}
	
}
