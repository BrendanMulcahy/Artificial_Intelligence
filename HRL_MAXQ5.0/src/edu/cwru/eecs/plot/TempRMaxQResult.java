package edu.cwru.eecs.plot;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import edu.cwru.eecs.futil.misc.FileMisc;

public class TempRMaxQResult {
	public static void main(String[] args) throws IOException {
		String dir = "result0527/BayesWargus_RMaxQ/";
		String filename = "wargus_rmaxq.txt";
		int max = 1000;
		double[] episodeCount = new double[max];
		double[] step = new double[max];
		double[] reward = new double[max]; 
		Arrays.fill(episodeCount, 0);
		Arrays.fill(step, 0);
		Arrays.fill(reward, 0);
		List<String> lines = Files.readLines(new File(dir+filename), Charsets.UTF_8);
		for(String line : lines) {
			if(line.trim().startsWith("Episode")) {
				double[] temp = parseLine(line);
				int ep = (int)temp[0];
				episodeCount[ep]++;
				step[ep] += temp[1];
				reward[ep] += temp[2];
			}
		}
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<max; i++) {
			if(episodeCount[i]>2) {
				step[i]/=episodeCount[i];
				reward[i]/=episodeCount[i];
				System.out.println(episodeCount[i] + " E: " + i + " S: " + step[i] + " R: " + reward[i]);
				sb.append(i + "\t" + reward[i] + "\t" + step[i] + "\t" + 0 + "\n");
			}
		}
		FileMisc.stringToFile(dir+"Wargus3322-n0.15-RMaxMaxQ-1000.rl", sb.toString());
	}
	
	//Episode 342:    Step: 34        Reward: -89.5
	public static double[] parseLine(String line) {
		line = line.trim();
		double[] result = new double[3];
		int eIndex = line.indexOf("Episode");
		result[0] = Double.parseDouble(line.substring(eIndex+7, line.indexOf(":", eIndex)).trim());
		int sIndex = line.indexOf("Step");
		int rIndex = line.indexOf("Reward");
		int endIndex = line.length();
		result[1] = Double.parseDouble(line.substring(sIndex+5, rIndex).trim());
		result[2] = Double.parseDouble(line.substring(rIndex+7, endIndex).trim());
		return result;
	}
}
