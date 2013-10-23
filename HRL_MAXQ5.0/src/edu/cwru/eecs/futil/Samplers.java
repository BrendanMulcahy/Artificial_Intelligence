package edu.cwru.eecs.futil;

import java.util.Calendar;
import java.util.Random;

public class Samplers {
	private static Random rng = new Random(Calendar.getInstance()
			.getTimeInMillis() + Thread.currentThread().getId());

	public static int sampleUniform(int size) {
		return rng.nextInt(size);
	}
	
	public static int sampleMultinomial(double[] probCount, double totalCount) {
		double random = rng.nextInt((int)totalCount);
		double sum = 0;
		for (int i = 0; i < probCount.length; i++) {
			sum += probCount[i];
			if (random < sum)
				return i;
		}
		return probCount.length - 1;
	}
	
	public static int sampleMultinomial(double[] probs) {
		double random = Math.random();
		double sum = 0;
		for (int i = 0; i < probs.length; i++) {
			sum += probs[i];
			if (random < sum)
				return i;
		}
		return probs.length - 1;
	}
	
	public static double sampleGamma(double k, double theta) {
		boolean accept = false;
		if (k < 1) {
			// Weibull algorithm
			double c = (1 / k);
			double d = ((1 - k) * Math.pow(k, (k / (1 - k))));
			double u, v, z, e, x;
			do {
				u = rng.nextDouble();
				v = rng.nextDouble();
				z = -Math.log(u);
				e = -Math.log(v);
				x = Math.pow(z, c);
				if ((z + e) >= (d + x)) {
					accept = true;
				}
			} while (!accept);
			return (x * theta);
		} else {
			// Cheng's algorithm
			double b = (k - Math.log(4));
			double c = (k + Math.sqrt(2 * k - 1));
			double lam = Math.sqrt(2 * k - 1);
			double cheng = (1 + Math.log(4.5));
			double u, v, x, y, z, r;
			do {
				u = rng.nextDouble();
				v = rng.nextDouble();
				y = ((1 / lam) * Math.log(v / (1 - v)));
				x = (k * Math.exp(y));
				z = (u * v * v);
				r = (b + (c * y) - x);
				if ((r >= ((4.5 * z) - cheng)) || (r >= Math.log(z))) {
					accept = true;
				}
			} while (!accept);
			return (x * theta);
		}
	}

	public static void main(String[] args) {
		for (int i = 1; i < 10; i++) {
			double temp = 0;
			for (int j = 0; j < 10; j++)
				temp += sampleGamma(1, i);
			temp /= 10;
			System.out.println("" + i + ": " + temp);
		}
	}
}
