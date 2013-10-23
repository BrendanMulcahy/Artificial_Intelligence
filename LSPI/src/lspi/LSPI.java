/**
 * @author Brendan Mulcahy
 * @date September 3, 2013
 * Case Western Reserve University
 * Department of Computer Science
 * 
 * Java implementation of the least-squares policy iteration
 * algorithm (LSPI).
 * 
 * See Lagoudakis & Parr "Least-Squares Policy Iteration".
 */
package lspi;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import Jama.Matrix;

public class LSPI {

	private ArrayList<Sample> samples;
	private Integer numBasisFunctions;
	private BasisFunctions basisFunctions;
	private Double discountFactor;
	private Double stopCriterion;
	private Double[] policy;
	private Action[] actions;
	
	/**
	 * Constructor for the LSPI class
	 * 
	 * @param samples samples to learn from (s, a, r, s')
	 * @param k number of basis functions
	 * @param phi basis functions class, methods of which are accessed through reflection
	 * @param gamma discount factor for Bellman equations
	 * @param epsilon stopping criterion
	 * @param policy initial policy, given as weights (default to w = 0)
	 */
	public LSPI(
			ArrayList<Sample> samples,
			Integer k,
			BasisFunctions phi,
			Double gamma,
			Double epsilon,
			Double[] policy, Action[] actions)
	{
		this.samples = samples;
		this.numBasisFunctions = k;
		this.basisFunctions = phi;
		this.discountFactor = gamma;
		this.stopCriterion = epsilon;
		this.policy = policy;
		this.actions = actions;
	}
	
	/**
	 * Executes the LSPI algorithm using the inputs given in the constructor
	 * @return the policy after LSPI has converged
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public Double[] run() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Double[] tempPolicy = policy; //bring in initial policy
		int count = 0;
		do {
			policy = tempPolicy; //store the policy
			tempPolicy = LSTDQ(samples, numBasisFunctions, basisFunctions, discountFactor, policy);
			System.out.println("LSPI Iteration: " + count++);
		}
		while(!policyConverged(policy, tempPolicy, stopCriterion) && count < 20);
		
		System.out.println("LSPI ran " + count + " iterations.");
		return policy;
	}
	
	/**
	 * Learns approximate Q-pi from samples
	 * Not optimized version of LSTDQ
	 * 
	 * @param samples source of samples (s, a, r, s')
	 * @param k number of basis functions
	 * @param phi basis functions
	 * @param gamma discount factor
	 * @param policy policy whose value function value is sought
	 * @return the policy
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	private Double[] LSTDQ(ArrayList<Sample> samples, Integer k, BasisFunctions basisFunctions, Double gamma, Double[] policy) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Matrix A = Matrix.identity(k, k).times(0.001); // k x k matrix of zeroes normally, in this case identity times a small value to prevent uninvertability
		Matrix b = new Matrix(k, 1); // k x 1 vector of zeroes
		int count = 0;
		
		for(Sample sample : samples) {
			count++;
			//System.out.println("LSTDQ: Running sample " + count++ + ".");
			Matrix phi = basisFunctions.evaluate(sample.state, sample.action);
			
			Matrix phiNext = basisFunctions.evaluate(sample.nextState, getAction(sample.nextState));
			
			//phi.print(5, 2);
			//phiNext.print(5, 2);
			
			//A <- A + phi(s,a)[phi(s,a) - gamma * phi(s', pi(s'))]^T
			//A.plusEquals(phi.arrayTimes(phi.minus(phiNext.times(gamma)).transpose()));
			Matrix temp = phiNext.times(gamma);
			//temp.print(5, 2);
			temp = phi.minus(temp);
			//temp.print(5, 2);
			temp = temp.transpose();
			//temp.print(5, 2);
			temp = phi.times(temp);
			//temp.print(5, 2);
			A.plusEquals(temp);
			//A.print(3, 3);
			
			//b <- b + phi(s,a) * r
			//System.out.println("Sample Reward: " + sample.reward);
			b.plusEquals(phi.times(sample.reward));
		}
		
		Matrix weights = A.inverse().times(b); //w <- (A^-1)b
		//weights.print(4,4);
		System.out.println("LSTDQ ran " + count + " iterations.");
		return convertColumnMatrixToArray(weights);
	}

	/**
	 * Converts a column matrix to a Double[]
	 * @param input the column matrix
	 * @return Double[] containing values from the matrix
	 */
	private Double[] convertColumnMatrixToArray(Matrix input) {
		double[][] weights = input.getArrayCopy();
		if (weights[0].length != 1) { throw new IllegalStateException("Weights should be 1-dimensional Found dimensions: " + weights.length); }
		if (weights.length != numBasisFunctions) { throw new IllegalStateException("There should be k weights! k: " + numBasisFunctions + " Found: " + weights.length); }
		
		Double[] output = new Double[numBasisFunctions];
		for(int i = 0; i < numBasisFunctions; i++) {
			output[i] = weights[i][0];
		}
		
		return output;
	}
	
	/**
	 * Determines if the policy iteration has converged.  Checks if each of the
	 * weights has converged between the policy and the tempPolicy to a value
	 * below epsilon.
	 * 
	 * @param policy the current policy
	 * @param tempPolicy the temporary policy gotten back from LSTDQ
	 * @param epsilon the stopping criterion
	 * @return true if the policy has converged, false otherwise
	 */
	private boolean policyConverged(Double[] policy, Double[] tempPolicy, Double epsilon) {
		if (policy.length != tempPolicy.length) {throw new IllegalArgumentException("These policies don't have the same number of parameters!");}
		
		//Check if each weight has converged to below epsilon
		for(int i = 0; i < policy.length; i++) {
			if (Math.abs(policy[i] - tempPolicy[i]) > epsilon) {
				return false;
			}
		}
		//if the execution makes it here then all weights have converged below epsilon
		System.out.println("Policy converged to:");
		for(int i = 0; i < policy.length; i++) {
			System.out.println(policy[i]);
		}
		return true;
	}
	
	public PendulumAction getAction(State s) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Double value = Double.NEGATIVE_INFINITY;
		Action temp = null;
		for(int i = 0; i < actions.length; i++) {
			double sum = 0;
			for(int j = 0; j < numBasisFunctions; j++) {
				Matrix A = basisFunctions.evaluate(s, actions[i]);
				//A.print(5, 2);
				sum += policy[j] * A.get(j,0);
			}
			 if (sum > value) { //fix this for general case
				 temp = actions[i];
				 value = sum;
			 }
		}
		//System.out.println(temp.toString());
		
		return (PendulumAction) temp;
	}
}
