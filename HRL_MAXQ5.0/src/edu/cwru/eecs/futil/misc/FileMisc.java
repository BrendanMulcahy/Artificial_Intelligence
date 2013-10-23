package edu.cwru.eecs.futil.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FileMisc {
	public static String ReadInFile(String fileName) {
		StringBuffer sb = new StringBuffer();
		 try {
			BufferedReader bReader = new BufferedReader(new FileReader(fileName));
			String line;
			while((line=bReader.readLine())!=null) {
				sb.append(line + "\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static String readInFile(File file) {
		StringBuffer sb = new StringBuffer();
		 try {
			BufferedReader bReader = new BufferedReader(new FileReader(file));
			String line;
			while((line=bReader.readLine())!=null) {
				sb.append(line + "\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static void saveDoubleArrayToFile(String filename, double[][] data, boolean headinfo) {
		try {
			DecimalFormat df = new DecimalFormat("#.########");
			FileWriter fw = new FileWriter(new File(filename));
			if(headinfo)
				fw.write("Size: " + data.length + " " + data[0].length + "\n");
			for(int i=0; i<data.length; i++) {
				StringBuffer lineSB = new StringBuffer();
				for(int j=0; j<data[0].length; j++) {
					if(data[i]==null)
						continue;
					lineSB.append(df.format(data[i][j]) + " ");
				}
				fw.write(lineSB.toString() + "\n");
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static double[][] loadDoubleArrayFromFile(String filename) {
		double[][] data = null;
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(filename));
			String info = bReader.readLine();
			int length = Integer.parseInt(info.split(" +")[1]);
			data = new double[length][];
			for(int i=0; i<length; i++) {
				data[i] = stringToDoubleArray(bReader.readLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public static double[][] loadDoubleArrayFromFile(String filename, boolean headinfo) {
		try {
			ArrayList<String> lines = new ArrayList<String>();
			BufferedReader bReader = new BufferedReader(new FileReader(filename));
			if(headinfo) {
				bReader.readLine();  // read in info line
			}
			String line = bReader.readLine();
			while(line!=null) {
				lines.add(line);
				line = bReader.readLine();
			}
			double[][] data = new double[lines.size()][];
			int i=0;
			for(String tempLine : lines)
				data[i++] = stringToDoubleArray(tempLine);
			return data;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static double[] stringToDoubleArray(String content) {
		String[] instance = content.split("\\s+");
		double[] data = new double[instance.length];
		for(int i=0; i<instance.length; i++) {
			data[i] = Double.parseDouble(instance[i]);
		}
		return data;
	}
	
	public static List<File> getFiles(String dir, String suffix) {
		List<File> files = new ArrayList<File>();
		File[] allfiles = new File(dir).listFiles();
		for(File file : allfiles) {
			String name = file.getName();
			if(name.endsWith(suffix) && !file.isDirectory())
				files.add(file);
		}
		return files;
	}
	
	public static void stringToFile(String filename, String content) {
		File f = new File(filename);
		if(!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		try {
			FileWriter fw = new FileWriter(f);
			fw.write(content);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> getFiles(File file, String suffix) {
		if(!file.isDirectory())
			return null;
		String[] children = file.list();
		ArrayList<String> results = new ArrayList<String>();
		for(String child : children) {
			if(child.endsWith(suffix))
				results.add(child);
		}
		return results;
	}
	
	public static Object readInObject(File file) {
		try {
			//File file = new File(filename);
			ObjectInputStream inputStream;
			inputStream = new ObjectInputStream(new FileInputStream(file));
			Object object = inputStream.readObject();
			inputStream.close();
			return object;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
