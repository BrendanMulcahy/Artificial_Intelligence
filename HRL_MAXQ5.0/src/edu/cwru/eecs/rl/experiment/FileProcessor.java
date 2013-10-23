package edu.cwru.eecs.rl.experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.Vector;

import edu.cwru.eecs.rl.agent.Agent;




public class FileProcessor {
	
	public static final String CONFIG_FILE_EXTENSION = ".config";
	
	public static final String RESULT_DATA_FILE_EXTENSION = ".rl";
	public static final String RESULT_AGENT_FILE_EXTENSION = ".agent";
	
	public static final String RESULT_VF_EXTENSION = ".vf";
	public static final String RESULT_CF_EXTENSION = ".cf";
	public static final String RESULT_PCF_EXTENSION = ".pcf"; // pseudo completion function
	public static final String RESULT_PR_EXTENSION = ".pr";
	public static final String RESULT_EF_EXTENSION = ".ef";
	public static final String RESULT_QV_EXTENSION = ".qv"; // q value
	public static final String RESULT_SV_EXTENSION = ".sv"; // state value
	public static final String RESULT_RM_EXTENSION = ".rmodel"; // rmaxq model
	
	
	/**
	 * return the next trimmed line without comment.
	 * Note that comment is starting with '%'.
	 * @param inputBufferedReader
	 * @return the next trimmed line without comment.
	 * @throws IOException
	 */
	public static String nextLine(BufferedReader inputBufferedReader) throws IOException {
		String lineString = null;
		while((lineString = inputBufferedReader.readLine())!=null) {
			lineString = lineString.trim();
			if(lineString.startsWith("%") || lineString.equals("")) {
				// it's a comment line
				continue; 
			}
			if(lineString.indexOf("%")>-1) {
				// means that there is comment at the end of this line
				lineString = lineString.substring(0, lineString.indexOf("%")-1);
			}
			// following is basically for read in .data file
			lineString = lineString.replace(',', ' ');
			lineString = lineString.replace('"', ' ');
			lineString = lineString.replace(':', ' ');
			if(lineString.endsWith("."))
				lineString = lineString.substring(0, lineString.length()-1);
			lineString = lineString.trim();
			if(lineString.equals(""))
				continue;
			return lineString;
		}
		return lineString;
	}


	public static void storeCumulativeReward(Vector<Double> cumulativeRewards,Vector<Double> steps, Vector<Double> cpuTime, 
			String fullOutputFilename) {
		DecimalFormat df = new DecimalFormat("#.##");
		String fileFullPath = fullOutputFilename + RESULT_DATA_FILE_EXTENSION;
		try {
			FileWriter FW = new FileWriter(new File(fileFullPath));
			for (int i = 0; i < cumulativeRewards.size(); i++) {
				FW.write("" + i 
						+ "\t" + df.format(cumulativeRewards.get(i)) 
						+ "\t\t" + df.format(steps.get(i)) 
						+ "\t\t" + df.format(cpuTime.get(i)) + "\n");
			}
			FW.close();
		} catch (IOException e) {
			System.out.println("Problem writing results out to file: "
					+ fullOutputFilename + " :: " + e);
		}
	}
	
	public static void storeAgent(Agent agent, String fullOutputFilename) {
		String fileFullPath = fullOutputFilename + RESULT_AGENT_FILE_EXTENSION;
		ObjectOutputStream outputStream = null;
		try {
			outputStream = new ObjectOutputStream(new FileOutputStream(fileFullPath));
			outputStream.writeObject(agent);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Agent loadAgent(String fullOutputFilename) {
		String fileFullPath = fullOutputFilename + RESULT_AGENT_FILE_EXTENSION;
		Agent agent = null;
		ObjectInputStream inputStream;
		try {
			inputStream = new ObjectInputStream(new FileInputStream(fileFullPath));
			agent = (Agent)inputStream.readObject();
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return agent;
	}
	
	
}
