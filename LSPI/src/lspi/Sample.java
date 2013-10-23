package lspi;

public class Sample {
	public State state;
	public Action action;
	public Double reward;
	public State nextState;
	
	public Sample(State state, Action action, double reward, State nextState) {
		this.state = state;
		this.action = action;
		this.reward = reward;
		this.nextState = nextState;
	}
}
