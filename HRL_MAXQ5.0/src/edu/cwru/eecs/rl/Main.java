 
package edu.cwru.eecs.rl;

import java.io.File;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


import edu.cwru.eecs.futil.misc.FileMisc;
import edu.cwru.eecs.futil.misc.StringMisc;
import edu.cwru.eecs.rl.agent.Agent;
import edu.cwru.eecs.rl.env.Environment;
import edu.cwru.eecs.rl.experiment.ExperimentRun;

public class Main {
	public static void main(String[] args) {
		System.out.println("Hello, this is HRL_MAXQ5.0");
		singleMain(args);
	}
	
	public static String singleMain(String[] args) {
		CommandLine cmd = parse(args);
		if(cmd==null) {
			return null;
		}
		
		if(!cmd.hasOption("e") || !cmd.hasOption("a") || !cmd.hasOption("exp")) {
			printUsage("Please specify agent, environment and experiment parameters.");
			return null;
		}
		
		/*************************************************************************
		 *	specify the environment. 
		 *************************************************************************/
		String[] envArgs = cmd.getOptionValues("e");
		Environment environment = null;
		
		Class<?> envClass = null;
		
		// get environment class
		try {
			String path = "edu.cwru.eecs.rl.env.";
			if(envArgs[0].equals("taxi")) {
				envClass = Class.forName(path+"TaxiEnv");
			} 
			else if(envArgs[0].equals("simplemaze")) {
				envClass = Class.forName(path+"SimpleMazeEnv");
			} 
			else if(envArgs[0].equals("hallway")) {
				envClass = Class.forName(path+"HallwayEnv");
			} 
			else if(envArgs[0].equals("wargus")) {
				envClass = Class.forName(path+"WargusEnv");
			}
			else if(envArgs[0].equals("fourdoormaze")) {
				envClass = Class.forName(path+"FourDoorMazeEnv");
			}
			else {
				printUsage("Bad environment specified: " + envArgs[0]);
				return null;
			}
		} catch (ClassNotFoundException e) {
			printUsage("Environment class was not found.");
			return null;
		}
		
		// instantiate the environment
		try {
			environment = (Environment) envClass.getConstructor(String[].class).newInstance((Object)envArgs);
		} catch (Exception e) {
			try {
				//e.printStackTrace();
				printUsage("Unable to instantiate a new instance of " + envClass.getSimpleName() + 
					"\n"+"It requires the following additional parameters: " + 
					(String)envClass.getMethod("getUsage").invoke(null));
			} catch (Exception e2) {
				//e.printStackTrace();
				printUsage("Unable to instantiate a new instance of "+envClass.getSimpleName());
				return null;
			}
			return null;
		}
		
		if(environment==null) {
			printUsage("Bad environment specified: " + envArgs[0]);
			return null;
		}
		
		/*************************************************************************
		 *	specify the agent. 
		 *************************************************************************/
		String[] agentArgs = cmd.getOptionValues("a");
		Agent agent = null;
		
		Class<?> agentClass = null;
		
		// get agent class
		try {
			String path = "edu.cwru.eecs.rl.agent.";
			if(agentArgs[0].equals("flatq")) {
				agentClass = Class.forName(path+"FlatQAgent");
			} 
			else if(agentArgs[0].equals("maxq")) {
				agentClass = Class.forName(path+"MaxQAgent");
			} 
			else if(agentArgs[0].equals("bmaxq")) {
				agentClass = Class.forName(path+"BayesianMaxQAgent");
			} 
			else if(agentArgs[0].equals("alispq")) {
				agentClass = Class.forName(path+"ALispQAgent");
			}
			else if(agentArgs[0].equals("rmaxq")) {
				agentClass = Class.forName(path+"RMaxMAXQAgent");
			}
			else {
				printUsage("Bad agent specified: " + agentArgs[0]);
				return null;
			}
		} catch (ClassNotFoundException e) {
			printUsage("Agent class was not found.");
			return null;
		}
		
		// instantiate the agent
		try {
			agent = (Agent) agentClass.getConstructor(Environment.class, String[].class).newInstance(environment, (Object)agentArgs);
		} catch (Exception e) {
			try {
				//e.printStackTrace();
				printUsage("Unable to instantiate a new instance of " + agentClass.getSimpleName() + 
					"\n"+"It requires the following additional parameters: " + 
					(String)agentClass.getMethod("getUsage").invoke(null));
			} catch (Exception e2) {
				//e.printStackTrace();
				printUsage("Unable to instantiate a new instance of "+agentClass.getSimpleName());
				return null;
			}
			return null;
		}
 		
		if(agent==null) {
			printUsage("Bad agent specified: " + agentArgs[0]);
			return null;
		}
		
		// if use good prior 
		String priorArg = cmd.getOptionValue("p");
		if(priorArg!=null) {
			List<File> files = FileMisc.getFiles(priorArg, ".agent");
			if(files.size()==0) {
				System.out.println("No prior found: " + priorArg);
				return null;
			}
			Agent priorAgent = (Agent) FileMisc.readInObject(files.get(0));
			agent.setPrior(priorAgent);
		}
		
		
		/*************************************************************************
		 *	specify the environment, and run it.
		 *************************************************************************/
		String[] experimentArgs = cmd.getOptionValues("exp");
		if(!StringMisc.isInteger(experimentArgs[0]) || !StringMisc.isInteger(experimentArgs[1])) {
			printUsage("Bad experiment setting: " + experimentArgs[0] + " " + experimentArgs[1]);
			return null;
		}
		int maxEpisode = StringMisc.strToInt(experimentArgs[0]);
		int maxStepPerEpi = StringMisc.strToInt(experimentArgs[1]);
		
		String saveToDir = cmd.getOptionValue("to");
		String extraInfo = cmd.getOptionValue("info");
		String fullOutputFilename = ExperimentRun.singleRun(environment, agent, maxEpisode, maxStepPerEpi, saveToDir!=null, saveToDir, extraInfo);
		
		return fullOutputFilename;
	}
	
	
	@SuppressWarnings("static-access")
	public static CommandLine parse(String[] args) {
		// agent
		Option agentOpt = OptionBuilder.withArgName("algorithm, pr, [htype]")
									   .hasArgs()
									   .withDescription("Specify the rl algorithm to be used by agent: \n" +
									   		"flatq | maxq | bmaxq | alispq | rmaxq\n" +
									   		"follow by pseudo reward settings: (no need for alispq and rmaxq)\n" +
									   		"none | manual | bayes | func\n" +
									   		"follow by hierarchy settings: (optional)\n" +
									   		"f | h")
									   .create("a");
		
		// prior (load saved agent)
		Option priorOpt = OptionBuilder.withArgName("prior, dir")
		   								.hasArg()
		   								.withDescription("Specify the prior by agent: \n" +
		   										"dir of the saved agent (suffix .agent)")
		   								.create("p");
		
		// environment
		Option envOpt = OptionBuilder.withArgName("domain, param")
									 .hasArgs()
									 .withDescription("Specify the domain name: \n" +
									 		"taxi | hallway | simplemaze | wargus | fourdoormaze \n" +
									 		"followed with parameters")
									 .create("e");
		
		// experiment
		Option expOpt = OptionBuilder.withArgName("#episode, max step")
									 .hasArg()
									 .withDescription("Specify the number of episodes to run and max step each episode")
									 .create("exp");
		expOpt.setArgs(2);
		
		// save to dir
		Option saveToOpt = OptionBuilder.withArgName("save_to_dir")
										.hasArg()
										.withDescription("save the result to the specified directory")
										.create("to");
		// extra info
		Option extraInfoOpt = OptionBuilder.withArgName("extra info")
										   .hasArg()
										   .withDescription("extra information of the save to file")
										   .create("info");
		
		// help
		Option helpOpt = OptionBuilder.withDescription("display help info")
									  .create("h");
		
		// multi run
		Option multiRunOpt = OptionBuilder.withArgName("#run")
										  .hasArg()
										  .withDescription("multi-run")
										  .create("run");
		
		options = new Options();
		options.addOption(agentOpt);
		options.addOption(priorOpt);
		options.addOption(envOpt);
		options.addOption(expOpt);
		options.addOption(helpOpt);
		options.addOption(saveToOpt);
		options.addOption(extraInfoOpt);
		options.addOption(multiRunOpt);
		
		CommandLineParser parser = new GnuParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			printUsage( "Parsing failed.  Reason: " + e.getMessage() );
			return null;
		}
		
		if(cmd.hasOption("h")) {
			printUsage("");
			return null;
		}
		
		return cmd;
	}
	
	public static void printUsage(String error) {
		System.out.println(error);
		if(options!=null)
			new HelpFormatter().printHelp("java -cp bin;lib/* edu.cwru.eecs.rl.Main", options);
		System.out.println("Example: ");
		System.out.println("\tjava -cp bin:lib/* edu.cwru.eecs.rl.Main -a flatq -e taxi 0 10 true -exp 1000 1000 -to res_taxi");
		System.out.println("\tjava -cp bin:lib/* edu.cwru.eecs.rl.Main -a maxq manual -e taxi 0.15 -exp 1000 1000 -to res_taxi");
		System.out.println("\tjava -cp bin:lib/* edu.cwru.eecs.rl.Main -a bmaxq none f -e simplemaze 0 -exp 500 500 -to res_simplemaze -p dir");
		System.out.println("\tjava -cp bin:lib/* edu.cwru.eecs.rl.Main -a bmaxq bayes -e simplemaze 0 -exp 500 500 -to res_simplemaze");
	}
	
	private static Options options;
}




















