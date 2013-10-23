package edu.cwru.eecs.plot;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import edu.cwru.eecs.futil.misc.FileMisc;


public class Averager {
	
	public static String[] exps = new String[]{
		"BayesTaxi0506",
		"BayesWargus0506",
		"PRHallway0506",
		"PRTaxi0506"
	};
	
	/**
	 * Take average of 5 run experimental results.
	 * @param dirname
	 */
	public static void averager(String dirname) {
		File dir = new File(dirname);
		String[] children = dir.list();
		HashMap<String, ArrayList<File>> results = new HashMap<String, ArrayList<File>>();
		for(String child : children) {
			//System.out.println(child);
			File subDir = new File(dirname + "/" + child);
			if(subDir.isDirectory()) {
				String key = dirname + "/" + child.substring(0, child.indexOf("(2012")-3);
				//System.out.println(key);
				if(!results.containsKey(key))
					results.put(key, new ArrayList<File>());
				results.get(key).add(subDir);
			}
		}
		for(String key : results.keySet()) {  // for each algorithm
			System.out.println("Processing: " + key + "...");
			ArrayList<File> resDirs = results.get(key);
			if(resDirs.size()<5) {
				System.out.println("Imcomplete: " + key);
				continue;
			}
			
			double[][][] rlDataRun = new double[resDirs.size()][][];
			int i=0;
			for(File res : resDirs) {
				String parDir = res.getAbsolutePath();
				String[] outputs = res.list();
				for(String op : outputs) { 
					if(op.endsWith(".rl")) {
						rlDataRun[i++] = FileMisc.loadDoubleArrayFromFile(parDir + "/" + op, false);
					}
				}
			}
			int length = rlDataRun[0].length;
			int dim = rlDataRun[0][0].length;
			double[][] average = new double[length][];
			for(int l = 0; l<length; l++) {
				average[l] = new double[dim];
				Arrays.fill(average[l], 0);
				for(int d = 0; d<dim; d++) {
					for(i=0; i<resDirs.size(); i++)
						average[l][d] += rlDataRun[i][l][d];
					average[l][d] /= resDirs.size();  // take average
				}
			}
			FileMisc.saveDoubleArrayToFile(key+".rl", average, false);
		}
	}
	
	public static void main(String[] args) {
//		String dir = "result0506/";
		//averager(dir + exps[2]);
//		for(String algrithm : exps) {
//			averager(dir+algrithm);
//		}
	}
}
