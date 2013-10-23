package edu.cwru.eecs.rl.core.distribution;

import java.util.Random;

public class Gaussian {

	private double m_mean;
	private double m_tau;

	private int N = 0;
	private double m_average = 0;

	// fix precision
	private double m_knownTau = 100;

	public Gaussian(double mean, double tau) {
		m_mean = mean;
		m_tau = 1;
	}

	// refer to: http://en.wikipedia.org/wiki/Conjugate_prior

	public void updatePosterior(double observation) {
		m_mean = (m_tau * m_mean + m_knownTau * observation)
				/ (m_tau + m_knownTau);
		m_tau = m_tau + m_knownTau;

		// test
		N++;
		m_average = m_average + 1.0 / N * (observation - m_average);
	}

	public double sampleGaussianMean() {
		Random rd = new Random();
		double x = rd.nextGaussian();
		return (x * Math.sqrt(1.0 / m_tau)) + m_mean;
		// test
		// return m_average;
	}
}
