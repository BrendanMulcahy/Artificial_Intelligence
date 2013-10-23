package edu.cwru.eecs.rl.core.distribution;

import java.io.Serializable;

import jsc.distributions.StudentsT;

public class GaussianGamma implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private double m_mu;
	private double m_lambda;
	private double m_alpha;
	private double m_beta;

	public GaussianGamma() {
		m_mu = 0;
		m_lambda = 1;
		m_alpha = 1;
		m_beta = 1;
	}

	// refer to: http://en.wikipedia.org/wiki/Conjugate_prior

	public void updatePosterior(double observation) {
		double oldMu = m_mu;
		double oldLambda = m_lambda;
		double oldAlpha = m_alpha;
		double oldBeta = m_beta;
		m_mu = (oldLambda * oldMu + observation) / (oldLambda + 1);
		m_lambda = oldLambda + 1;
		m_alpha = oldAlpha + 0.5;
		m_beta = oldBeta + 0.5 * oldLambda * (observation - oldMu)
				* (observation - oldMu) / (oldLambda + 1);
	}
	
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

	public void complementary()
	{
		m_mu = m_mu-1;
	}
	
	public static void main(String[] args) {
		GaussianGamma gaussianGamma = new GaussianGamma();
		//gaussianGamma.updatePosterior(1000);
		//gaussianGamma.updatePosterior(-1000);
		for (int i = 0; i < 10; i++)
			gaussianGamma.updatePosterior(10);
		for(int i=0; i<10; i++)
			gaussianGamma.updatePosterior(1000);
		double sample;
		double average = 0;
		int number = 10;
		for (int i = 0; i < number; i++) {
			sample = gaussianGamma.sampleGaussianMean();
			average += sample;
			System.out.println("" + i + "'s sample: " + sample);
		}
		average /= number;
		System.out.println("Average: " + average);
	}

}
