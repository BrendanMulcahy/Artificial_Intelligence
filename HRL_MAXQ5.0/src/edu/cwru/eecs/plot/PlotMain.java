package edu.cwru.eecs.plot;

/**
 * Plot:
 * 1. take average
 * 2. smooth
 * 3. plot -- generate gnuplot script and run it
 * 
 * @author feng
 *
 */
public class PlotMain {
	
	private static int pi;
	private static int halfSize;
	private static int[] ranges;
	
	public static void main(String[] args) {
		//bayesTaxi();
		//prTaxi();
		//prHallway();
		//bayesWargus();
		
		//BayesTaxiComp/BayesTaxi0529
		String dir = "BayesTaxi0530_epsilon/";
		taxi();
		String name = "bayesTaxi";
		
		
//		String dir = "wargus0529/Wargus3322-n0.15-BMaxQPrNo-1000(2012-05-29)/";
//		wargus();
//		String name = "bayesWargus";
		Averager.averager(dir);
		Smoother.smoother(dir, halfSize);
		GnuPlotter.bayes(dir, name, ranges, pi);
	}
	
	public static void bayesTaxi() {
		String dir = "result0527/BayesTaxi0527/";
		taxi();
		String name = "bayesTaxi";
		Averager.averager(dir);
		Smoother.smoother(dir, halfSize);
		GnuPlotter.bayes(dir, name, ranges, pi);
	}
	
	public static void bayesWargus() {
		String dir = "result0506/BayesWargus0506/";
		wargus();
		String name = "bayesWargus";
		Averager.averager(dir);
		Smoother.smoother(dir, halfSize);
		GnuPlotter.bayes(dir, name, ranges, pi);
	}
	
	public static void prTaxi() {
		String dir = "result0527/PRTaxi0527/";
		taxi();
		String name = "prTaxi";
		Averager.averager(dir);
		Smoother.smoother(dir, halfSize);
		GnuPlotter.pr(dir, name, ranges, pi);
	}
	
	public static void prHallway() {
		String dir = "result0527/PRHallway0527/";
		hallway();
		String name = "prHallway";
		Averager.averager(dir);
		Smoother.smoother(dir, halfSize);
		GnuPlotter.pr(dir, name, ranges, pi);
	}
	
	public static void taxi() {
		pi = 10;
		halfSize = 10;
		ranges = new int[]{0, 500, -1000, 0};
	}
	
	public static void hallway() {
		pi = 100;
		halfSize = 100;
		ranges = new int[]{0, 5000, -2000, 0};
	}
	
	public static void wargus() {
		pi = 20;
		halfSize = 20;
		ranges = new int[]{0, 1000, -1000, 0};
	}
}
