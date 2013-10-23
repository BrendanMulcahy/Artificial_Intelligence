package edu.cwru.eecs.plot;

import java.io.File;

import edu.cwru.eecs.futil.misc.FileMisc;

public class Smoother {
	
	
	public static void main(String[] args) {
		smoother("result0506/PRHallway0506/", 100);
	}
	
	
	public static void smoother(String dirname, int halfSize) {
		int col = 1;
		//int halfSize = 10;
		File dir = new File(dirname);
		String[] children = dir.list();
		for(String child : children) {
			if(child.endsWith(".rl")) {
				smooth(dirname + "/" + child, col, halfSize);
			}
		}
	}
	
	// smooth the second column, with windows size 10
	// column = 1, halfSize = 10
	protected static void smooth(String filename, int col, int halfSize) {
		double[][] data = FileMisc.loadDoubleArrayFromFile(filename, false);
		double[] smoothed = new double[data.length];
		int pre = 0;
		int post = halfSize;
		double sum = 0;
		for(int i=pre; i<=post; i++)
			sum+=data[i][col];
		for(int i=0; i<data.length; i++) {
			//System.out.println(post-pre+1);
			smoothed[i] = sum/(post-pre+1);  // smooth
			while(pre<i-halfSize) {
				sum-=data[pre][col];
				pre++;
			}
			while(post<i+halfSize && post<data.length-1) {
				post++;
				sum+=data[post][col];
			}
		}
		for(int i=0; i<data.length; i++)
			data[i][col] = smoothed[i];
		FileMisc.saveDoubleArrayToFile(filename+"s", data, false);
	}
}
