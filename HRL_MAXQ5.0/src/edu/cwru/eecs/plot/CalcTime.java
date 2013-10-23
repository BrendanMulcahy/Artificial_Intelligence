package edu.cwru.eecs.plot;

import java.io.File;
import java.util.List;

import edu.cwru.eecs.futil.misc.FileMisc;

public class CalcTime {
	
	public static void main(String[] args) {
		calcTime("result_noise/BayesTaxi0530", 500);
		//calcTime("final_results/BayesTaxi", 500);
		//calcTime("final_results/PRTaxi", 500);
		//calcTime("final_results/PRHallway", 5000);
	}
	
	public static void calcTime(String dir, int episode) {
		List<File> files = FileMisc.getFiles(dir, ".txt");
		StringBuffer output = new StringBuffer(); 
		output.append("Average time for " + episode + " episodes:\n");
		for(File file : files) {
			String content = FileMisc.readInFile(file);
			if(content.indexOf("user")<=0)
				continue;
			int user = content.indexOf("user");
			int sys = content.indexOf("sys", user+1);
			String userTimeString = content.substring(user+4, sys-1).trim();
			String sysTimeString = content.substring(sys+3).trim();
			double totalTime = seconds(userTimeString) + seconds(sysTimeString);
			totalTime /= 5;
			System.out.println(file.getName() + ": " + totalTime + "s");
			output.append(file.getName() + ":\t " + totalTime + "s\n");
		}
		FileMisc.stringToFile(dir+"/timetable.txt", output.toString());
	}
	
	public static double seconds(String s) {
		s = s.trim();
		double time = 0;
		time += 60*Integer.parseInt(s.substring(0, s.indexOf("m")));
		time += Double.parseDouble(s.substring(s.indexOf("m")+1,s.indexOf("s")));
		return time;
	}
}
