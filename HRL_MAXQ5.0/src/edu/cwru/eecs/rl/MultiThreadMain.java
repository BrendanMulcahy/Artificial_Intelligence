package edu.cwru.eecs.rl;

import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.cli.CommandLine;


import edu.cwru.eecs.futil.misc.StringMisc;

public class MultiThreadMain {
	public static void main(String[] args) throws InterruptedException {
		CommandLine cmd = Main.parse(args);
		if(cmd==null || !cmd.hasOption("run")) {
			Main.printUsage("Please specify run number.");
			return ;
		}
		int RUN = Integer.parseInt(cmd.getOptionValue("run"));
		if(RUN<=0)
			return ;
		CountDownLatch latch = new CountDownLatch(RUN);
		SingleRunThread[] threads = new SingleRunThread[RUN];
		
 		for(int r=0; r<RUN; r++) {
			// get new args
			Vector<String> newArgsVec = new Vector<String>();
			for(int i=0; i<args.length; i++) {
				if(args[i].equals("-run")) { 
					i++;
					continue;
				}
				newArgsVec.add(args[i]);
				if(args[i].equals("-info")) {
					newArgsVec.add(args[++i]+"-r"+r);
				}
			}
			if(!newArgsVec.contains("-info")) {
				newArgsVec.add("-info");
				newArgsVec.add("r" + r);
			}
			String[] newArgs = StringMisc.vectorToStrArray(newArgsVec);
			// run!
			threads[r] = new SingleRunThread(newArgs, r, latch);
			threads[r].start();
		}
		
		// After done, calculate average of them -- use Matlab!
//		latch.await();
//		
//		System.out.println("I'm done!");
//		for(int i=0; i<RUN; i++) {
//			System.out.println(threads[i].getOutputFilename());
//		}
//		String avgOutputFilename = threads[0].getOutputFilename().replaceAll("-r[0-9]+", "-avg"+RUN);
//		String avgOutputDir = avgOutputFilename.replaceAll("/[^/]+$", "");
//		System.out.println(avgOutputFilename);
//		System.out.println(avgOutputDir);
//		File avgOutputDirFile = new File(avgOutputDir);
//		avgOutputDirFile.mkdirs();
//		PrintStream ps = null;
//		try {
//			ps = new PrintStream(new File(avgOutputFilename+".rl"));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		
		
		// Do the smooth
		
	}
}

class SingleRunThread extends Thread {
	
	private String[] args;
	private int t;
	private CountDownLatch latch;
	protected String fullOutputFilename;
	
	public SingleRunThread(String[] args, int t, CountDownLatch latch) {
		this.args = args;
		this.t = t;
		this.latch = latch;
	}
	public void run() {
		System.out.println("I'm thread " + t);
		fullOutputFilename = Main.singleMain(args);
		latch.countDown();
	}
	
	public String getOutputFilename() {
		return fullOutputFilename;
	}
}

