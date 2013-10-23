package edu.cwru.eecs.futil.misc;

public class MathMisc {
	/**
	 * compute the cross product of two vectors
	 * @param x
	 * @param y
	 * @return cross product of x and y
	 */
	public static double crossProduct(double[] x, double[] y) {
		if(x.length != y.length) {
			System.err.println("the dimension of two vectors doesn't match.");
			return 0;
		}
		double product = 0;
		for(int i=0; i<x.length; i++) {
			product += x[i]*y[i];
		}
		return product;
	}
}
