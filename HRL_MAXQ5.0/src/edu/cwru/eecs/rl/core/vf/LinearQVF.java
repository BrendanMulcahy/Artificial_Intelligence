package edu.cwru.eecs.rl.core.vf;

import java.util.Set;

import edu.cwru.eecs.futil.Pair;
import edu.cwru.eecs.rl.core.mdp.Parameter;
import edu.cwru.eecs.rl.core.mdp.State;

public class LinearQVF extends LinearVF<Pair<State, Parameter>> {

	private static final long serialVersionUID = -8106813852303750890L;

	public LinearQVF(int featureSize) {
		super(featureSize);
	}

	@Override
	protected double[] getFeatures(Pair<State, Parameter> t) {
		int stateDim = t.left().getNumVariable();
		int actionDim = t.right().getNumVariable();
		double[] features = new double[stateDim+actionDim];
		for(int i=0; i<stateDim; i++)
			features[i] = t.left().getValue(i);
		for(int i=0; i<actionDim; i++)
			features[i+stateDim] = t.right().getValue(i);
		return features;
	}

	@Override
	public void clear(Set<Pair<State, Parameter>> keys) {
		
	}

}
