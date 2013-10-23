package edu.cwru.eecs.rl.core.mdp;

public class Action extends AbstractType {

	
	private static final long serialVersionUID = 1L;
	
	public Action(double action, Parameter parameter) {
		super(1+parameter.size());
		setAction(action);
		setParameter(parameter);
		m_name = "Action";
	}

	public Action(double action) {
		this(action, Parameter.NIL);
	}

	public void setAction(double a) {
		setValue(0, a);
	}

	public void setParameter(Parameter parameter) {
		int numPara = parameter.getNumVariable();
		for (int i = 0; i < numPara; i++)
			setValue(i + 1, parameter.getValue(i));
	}

	public double getAction() {
		return getValue(0);
	}

	public Parameter getParameter() {
		int numPara = getNumVariable() - 1;
		Parameter parameter = new Parameter(numPara);
		for (int i = 0; i < numPara; i++)
			parameter.setValue(i, getValue(i + 1));
		return parameter;
	}

}
