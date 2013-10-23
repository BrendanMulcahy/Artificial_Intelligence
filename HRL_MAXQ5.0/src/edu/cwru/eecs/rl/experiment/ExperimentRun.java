package edu.cwru.eecs.rl.experiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Vector;

import edu.cwru.eecs.futil.misc.Misc;
import edu.cwru.eecs.rl.agent.Agent;
import edu.cwru.eecs.rl.env.Environment;


public class ExperimentRun {
	/**
	 * This static method actually runs the specific task for once, and collect data from output.
	 * @param environment
	 * @param agent
	 * @param maxEpisode
	 * @param maxStepPerEpi
	 * @param save
	 * @param dir
	 * @param extraInfo
	 */
	public static String singleRun(Environment environment, Agent agent, int maxEpisode, int maxStepPerEpi, boolean save, String dir, String extraInfo) {

		/**
		 * Experiment set up
		 */
		int MAX_EPISODE = maxEpisode;  // xx episodes per run
		int MAX_STEP_PER_EPI = maxStepPerEpi;  // at most xx steps per episode
		int testingEpi = 5;  // run xx episodes of test after learning
		
		// each element in the vector corresponds to an episode
		Vector<Double> cumulativeRewards = new Vector<Double>(MAX_EPISODE);
		Vector<Double> steps = new Vector<Double>(MAX_EPISODE);
		Vector<Double> cpuTime = new Vector<Double>(MAX_EPISODE);
		
		// initialize the save related stuff
		String fullOutputFilename="output/"+Misc.getDateTime();
		if(save) {
			String resultFileName = environment.getDescription() + "-" + agent.name() + agent.info() + "-" + MAX_EPISODE + (extraInfo==null || extraInfo.equals("")?"":("-" + extraInfo));
			File dirFile = new File(dir);
			if(!dirFile.exists())
				dirFile.mkdir();
			File targetDirFile = new File(dir+"/"+resultFileName + "(" + Misc.getDate() + ")");
			if(!targetDirFile.exists())
				targetDirFile.mkdir();
			fullOutputFilename = targetDirFile + "/" + resultFileName;
		}
		
		PrintStream ps = null;
		try {  // output the console output into file
			ps = new PrintStream(new File(fullOutputFilename.replace(":", "=") + ".out"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String info;
		
		/**
		 * Start running experiment
		 */
		
		/*********************************************************/
		//agent.message("debug, off");
		//environment.message("debug, off");
		/*********************************************************/
		
		
		int testFreq = 20;
		int saveFreq = 1000;  /// 1000
		for(int i=0; i<MAX_EPISODE; i++)
		{
			// running episode
			long startTimeMs = System.currentTimeMillis();
			agent.episode(Agent.MODE_LEARNING, MAX_STEP_PER_EPI);
			int taskTime = (int)(System.currentTimeMillis() - startTimeMs);
			int step = agent.getStep();
			double cumulativeReward = agent.getCumulativeReward();
				
			// output episode information
			info = "Episode " + i + ":\tStep: " + agent.getStep() + "\tReward: " + agent.getCumulativeReward();
			System.out.println(info);
			ps.println(info);
				
			// collecting information
			cumulativeRewards.add(cumulativeReward);
			steps.add((double)step);
			cpuTime.add((double)taskTime);
			if(i%saveFreq==0) {  // save every 1000 episodes.
				if(save) {
					FileProcessor.storeCumulativeReward(cumulativeRewards, steps, cpuTime, fullOutputFilename);
					FileProcessor.storeAgent(agent, fullOutputFilename);
					agent.message("vf, print, " + fullOutputFilename);
				}
			}
			
			if(i%testFreq==0) {
				// test every "testFreq" episodes
				step = 0;
				cumulativeReward = 0;
				int testNum = 1;
				for(int t=0; t<testNum; t++)
				{
					agent.episode(Agent.MODE_TESTING, MAX_STEP_PER_EPI);
					step += agent.getStep();
					cumulativeReward += agent.getCumulativeReward();
				}
				info = "Test after Episode " + i + ":\tAverage Step: " + (1.0*step/testNum) + "\tAverage Reward: " + (1.0*cumulativeReward/testNum);
				System.out.println(info);
				ps.println(info);
				ps.flush();
			}
		} // learning done!

		
		ps.flush();
		
		/**
		 *  store learning information
		 */
		if(save) {
			FileProcessor.storeCumulativeReward(cumulativeRewards, steps, cpuTime, fullOutputFilename);
			FileProcessor.storeAgent(agent, fullOutputFilename);
			agent.message("vf, print, " + fullOutputFilename);
		}
		
		
		/**
		 *  print value function
		 */
		//agent.message("Print Value Function");
		
		/**
		 *  Running test after learning
		 */
		info = "*********************** Test ******************************";
		System.out.println(info);
		ps.println(info);
		
		/*********************************************************/
		//environment.message("debug, off");
		//agent.message("debug, off");
		/*********************************************************/
		
		for(int j=0; j<testingEpi; j++)
		{
			agent.episode(Agent.MODE_TESTING, MAX_STEP_PER_EPI);
			info = "Test Episode " + j + ": \tStep: " + agent.getStep() + " \tReward: " + agent.getCumulativeReward();
			System.out.println(info);
			ps.println(info);
		}
		ps.close();
		
		return fullOutputFilename;
	}
}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	