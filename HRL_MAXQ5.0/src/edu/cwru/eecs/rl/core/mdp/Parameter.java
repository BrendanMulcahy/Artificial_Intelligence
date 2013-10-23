package edu.cwru.eecs.rl.core.mdp;

import java.util.ArrayList;

public class Parameter extends AbstractType {

	private static final long serialVersionUID = 1L;
	
	public static final Parameter NIL = new Parameter(0);

	public Parameter(int numOfVariable) {
		super(numOfVariable);
		m_name = "Parameter";
	}
	
	public static ArrayList<Parameter> getNilArrayList() {
		ArrayList<Parameter> parameters = new ArrayList<Parameter>(1);
		parameters.add(NIL);
		return parameters;
	}

}
