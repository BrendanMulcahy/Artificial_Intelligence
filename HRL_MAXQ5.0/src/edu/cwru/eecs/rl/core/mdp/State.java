package edu.cwru.eecs.rl.core.mdp;

public class State extends AbstractType {

	private static final long serialVersionUID = 1L;

	public State(int numOfVariable) {
		super(numOfVariable);
		m_name = "State";
	}
	
	public State clone() {
		State newState = new State(m_size);
		for(int i=0; i<m_size; i++)
			newState.setValue(i, this.getValue(i));
		return newState;
	}
}
