package lspi;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class PendulumSimulation {
	private static int trials = 100;
	private static int episodes = 10000;
	private static double dt = 0.1;
	
	public static void main(String args[]) {
		System.out.println("Start:");
		InvertedPendulum pendulum = new InvertedPendulum();
		ArrayList<Sample> samples = new ArrayList<Sample>();
		
		while(episodes != 0) {
			PendulumState state = new PendulumState(pendulum.x, pendulum.v);
			PendulumAction action = getRandomAction();
			pendulum.update(dt, action);
			PendulumState nextState = new PendulumState(pendulum.x, pendulum.v);
			double reward;
			if (pendulum.isHorizontal()) {
				pendulum = new InvertedPendulum();
				episodes--;
				reward = -1;
			} else {
				reward = 0;
			}
			Sample sample = new Sample(state, action, reward, nextState);
			samples.add(sample);
		}
		
		Action[] actions = new Action[3];
		actions[0] = new Force_Left();
		actions[1] = new Force_Right();
		actions[2] = new Force_None();
		Double[] startingWeights = new Double[]{
				1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 
				1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 
				1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };
		LSPI lspi = new LSPI(samples, 30, new PendulumBasisFunctions(), 0.95, 0.1, startingWeights, actions);
		try {
			lspi.run();
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pendulum = new InvertedPendulum();
		double[] time = new double[trials];
		int count = 0;
		while(count != trials) {
			while(!pendulum.isHorizontal() && time[count] < 300) {
				try {
					pendulum.update(dt, lspi.getAction(new PendulumState(pendulum.x, pendulum.v)));
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				time[count] += dt;
			}
			pendulum = new InvertedPendulum();
			count++;
		}
		
		System.out.println("Results:");
		double sum = 0;
		for(int i = 0; i < time.length; i++) {
			sum += time[i];
		}
		System.out.println("Average lifetime (seconds): " + sum / time.length);
		
//		System.out.println("An example trial: ");
//		pendulum = new InvertedPendulum();
//		while(!pendulum.isHorizontal()) {
//			try {
//				PendulumAction a = lspi.getAction(new PendulumState(pendulum.x, pendulum.v));
//				System.out.println("State: x: " + pendulum.x + "  v: " + pendulum.v);
//				System.out.println("Action chosen: " + a.toString());
//				System.out.println("");
//				pendulum.update(dt, a);
//			} catch (IllegalAccessException | IllegalArgumentException
//					| InvocationTargetException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}
	
	private static PendulumAction getRandomAction() {
		int result = (int) (Math.random() * 3);
		if (result == 0) {
			return new Force_Left();
		} else if (result == 1) {
			return new Force_Right();
		} else {
			return new Force_None();
		}
	}
}
