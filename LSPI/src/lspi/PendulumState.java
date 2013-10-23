package lspi;

public class PendulumState implements State {
	public double x; //angle (rad)
	public double v; //angular velocity (rad/s)
	
	public PendulumState(double x, double v) {
		this.x = x;
		this.v = v;
	}
	
	public String toString() {
		return "x: " + x + " v: " + v;
	}
}
