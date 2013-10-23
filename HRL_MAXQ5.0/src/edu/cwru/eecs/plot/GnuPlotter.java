package edu.cwru.eecs.plot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.cwru.eecs.futil.misc.FileMisc;

public class GnuPlotter {
	
	public static final String LINE_XLABEL = "set xlabel 'Episode'";
	public static final String LINE_YLABEL = "set ylabel 'Average Cumulative Reward Per Episode'";
	public static final String LINE_XRANGE = "set xrange [0:500]";
	public static final String LINE_YRANGE = "set yrange [-1000:200]";
	public static final String LINE_KEY = "set key right bottom";
	
	public static final String LINE_TERM = "set term postscript default";
	public static final String LINE_OUTPUT = "set output "; // 'Taxi.ps'
	
	public static final String LINE_END = "set terminal pop\nreplot";
	
	public static HashMap<String, String> m_linestyle;
	static {
		m_linestyle = new HashMap<String, String>();
		m_linestyle.put("B-MaxQ Uninformed", "lt 5 pt 5");
		m_linestyle.put("B-MaxQ Good", "lt 7 pt 7");
		m_linestyle.put("MaxQ", "lt 2 pt 2");
		m_linestyle.put("B-MB-Q Uninformed", "lt 4 pt 4");
		m_linestyle.put("B-MB-Q Good", "lt 6 pt 6");
		m_linestyle.put("B-MB-Q Good Comparable Simulations", "lt 8 pt 8");
		m_linestyle.put("R-MaxQ", "lt 9 pt 9");
		m_linestyle.put("FlatQ", "lt 1 pt 1");
		
		m_linestyle.put("B-MaxQ Bayes PR", "lt 5 pt 5");
		m_linestyle.put("B-MaxQ Manual PR", "lt 7 pt 7");
		m_linestyle.put("B-MaxQ No PR", "lt 9 pt 9");
		m_linestyle.put("MaxQ Non-Bayes PR", "lt 4 pt 4");
		m_linestyle.put("MaxQ Manual PR", "lt 6 pt 6");
		m_linestyle.put("MaxQ No PR", "lt 8 pt 8");
		m_linestyle.put("FlatQ", "lt 1 pt 1");
		m_linestyle.put("ALispQ", "lt 2 pt 2");
	}
	
	protected static String gpScript(boolean xlabel, boolean ylabel, int[] ranges, int pi, String output,  Map<String, String> filenames) {
		StringBuffer sb = new StringBuffer();
		if(xlabel)
			sb.append(LINE_XLABEL + "\n");
		if(ylabel)
			sb.append(LINE_YLABEL + "\n");
		sb.append("set xrange [" + ranges[0] + ":" + ranges[1] + "]\n");
		sb.append("set yrange [" + ranges[2] + ":" + ranges[3] + "]\n");
		sb.append(LINE_KEY + "\n");
		sb.append(LINE_TERM + "\n");
		sb.append(LINE_OUTPUT + "'" + output + ".ps" + "'" + "\n");
		
		sb.append("\n" + genPlot(filenames, pi) + "\n");
		
		sb.append(LINE_END + "\n");
		return sb.toString();
	}
	
	protected static String genPlot(Map<String, String> filenames, int pi) {
		StringBuffer sb = new StringBuffer();
		sb.append("plot ");
		for(String filename : filenames.keySet()) {
			sb.append("'" + filename + "'");
			sb.append(" using 1:2 title ");
			String legend = filenames.get(filename);
			sb.append("'" + legend + "'");
			sb.append(" with linespoints pi " + pi + " " );
			sb.append(m_linestyle.get(legend));
			sb.append(" lw 2");
			sb.append(",\\\n ");
		}
		sb.delete(sb.length()-4, sb.length()-1);
		//sb.deleteCharAt(sb.length()-1);
		sb.append("\n");
		return sb.toString();
	}
	
	public static void bayes(String dirname, String gpScriptName, int[] ranges, int pi) {
		//String dirname = "result0506/BayesTaxi0506";
		
		//String gpScriptName = "bayesTaxi";
		
		ArrayList<String> datafiles = FileMisc.getFiles(new File(dirname), ".rls");
		Map<String, String> filenames = new HashMap<String, String>();
		for(String datafile : datafiles) {
			if(datafile.indexOf("-BMaxQPrNo-")>0)
				filenames.put(datafile, "B-MaxQ Uninformed");
			else if(datafile.indexOf("-BMaxQPrNoiF-")>0)
				filenames.put(datafile, "B-MB-Q Uninformed");
			else if(datafile.indexOf("-FlatQ-")>0)
				filenames.put(datafile, "FlatQ");
			else if(datafile.indexOf("-MaxQPrNo-")>0)
				filenames.put(datafile, "MaxQ");
			else if(datafile.indexOf("-RMaxMaxQ-")>0)
				filenames.put(datafile, "R-MaxQ");
			else if(datafile.indexOf("-BMaxQPrNoGoodPrior-")>0)
				filenames.put(datafile, "B-MaxQ Good");
			else if(datafile.indexOf("-BMaxQPrNoiFGoodPrior-")>0)
				filenames.put(datafile, "B-MB-Q Good");
			else if(datafile.indexOf("-BMaxQPrNoiFGoodPriorComp-")>0)
				filenames.put(datafile, "B-MB-Q Good Comparable Simulations");
		}
		//int[] ranges = new int[]{0, 500, -1000, 200};
		String gpfile = gpScript(true, true, ranges, pi, gpScriptName, filenames);
		FileMisc.stringToFile(dirname+gpScriptName+".gp", gpfile);
		
		StringBuffer bash = new StringBuffer();
		bash.append("gnuplot " + gpScriptName + ".gp\n");
		bash.append("ps2pdf " + gpScriptName + ".ps\n");
		FileMisc.stringToFile(dirname+gpScriptName+".sh", bash.toString());
	}
	
	public static void pr(String dirname, String gpScriptName, int[] ranges, int pi) {
		//String gpScriptName = "prTaxi";
		
		ArrayList<String> datafiles = FileMisc.getFiles(new File(dirname), ".rls");
		Map<String, String> filenames = new HashMap<String, String>();
		for(String datafile : datafiles) {
			if(datafile.indexOf("-BMaxQPrBayes-")>0)
				filenames.put(datafile, "B-MaxQ Bayes PR");
			else if(datafile.indexOf("-BMaxQPrMan-")>0)
				filenames.put(datafile, "B-MaxQ Manual PR");
			else if(datafile.indexOf("-BMaxQPrNo-")>0)
				filenames.put(datafile, "B-MaxQ No PR");
			
			if(datafile.indexOf("-MaxQPrFunc-")>0)
				filenames.put(datafile, "MaxQ Non-Bayes PR");
			else if(datafile.indexOf("-MaxQPrMan-")>0)
				filenames.put(datafile, "MaxQ Manual PR");
			else if(datafile.indexOf("-MaxQPrNo-")>0)
				filenames.put(datafile, "MaxQ No PR");
			
			else if(datafile.indexOf("-FlatQ-")>0)
				filenames.put(datafile, "FlatQ");
			else if(datafile.indexOf("-ALispQ-")>0)
				filenames.put(datafile, "ALispQ");
		}
		//int[] ranges = new int[]{0, 500, -1000, 200};
		String gpfile = gpScript(true, true, ranges, pi, gpScriptName, filenames);
		FileMisc.stringToFile(dirname+gpScriptName+".gp", gpfile);
		
		StringBuffer bash = new StringBuffer();
		bash.append("gnuplot " + gpScriptName + ".gp\n");
		bash.append("ps2pdf " + gpScriptName + ".ps\n");
		FileMisc.stringToFile(dirname+gpScriptName+".sh", bash.toString());
	}
	
	public static void main(String[] args) {
		//bayesTaxi("result0506/BayesTaxi0506/");
		//pr("result0506/PRHallway0506/", "prHallway", new int[]{0, 5000, -2000, 0});
		pr("hallway0528/Hallway-MaxQPrFunc-5000(2012-05-28)/", "prHallway", new int[]{0, 5000, -2000, 0}, 100);
	}
}


















