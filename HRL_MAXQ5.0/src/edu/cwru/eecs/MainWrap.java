package edu.cwru.eecs;

import edu.cwru.eecs.rl.Main;


public class MainWrap {
	public static void main(String[] args) {
		//String parameter = "-a rmaxq -e simplemaze 0 -exp 1000 500 -to rmaxq_simplemaze";
		
		//String parameter = "-a rmaxq -e taxi 0 10 true -exp 1000 500 -to rmaxq_taxi";
		//String parameter = "-a alispq none -e taxi 0 10 true -exp 1000 500 -to alispq_taxi";
		//String parameter = "-a bmaxq none -e taxi 0 10 true -exp 1000 500 -to bmaxq_taxi";
		
		String parameter = "-a maxq none -e taxi 0 10 true -exp 10000 1000 -to taxi0528";
		
		//String parameter = "-a maxq func -e hallway 0 -exp 5000 2000 -to hallway0528";
		
		//String parameter = "-a bmaxq none f -e taxi 0 -exp 1000 1000 -to taxi";
		
		//String parameter = "-a bmaxq none -e wargus 3322 0.15 -exp 1000 1000 " +
		//		"-to wargus0529/Wargus3322-n0.15-BMaxQPrNo-1000(2012-05-29)/goodprior " +
		//		"-p wargus0529/Wargus3322-n0.15-BMaxQPrNo-1000(2012-05-29)";
		//Taxi-n0.15r10-BMaxQPrNo-500-r0(2012-05-27)
		
		//String parameter = "-a bmaxq none f -e taxi 0.15 10 true -exp 500 1000 " +
		//		"-to result0527/BayesTaxi0527/Taxi-n0.15r10-BMaxQPrNoiF-500-r0(2012-05-27)/goodprior " +
		//		"-p result0527/BayesTaxi0527/Taxi-n0.15r10-BMaxQPrNoiF-500-r0(2012-05-27)";
		
		args = parameter.split(" +");
		//for(int i=0; i<args.length; i++)
		//	System.out.println(args[i]);
		Main.main(args);
	}
}









