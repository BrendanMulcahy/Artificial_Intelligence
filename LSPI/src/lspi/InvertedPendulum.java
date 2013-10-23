package lspi;
/**
Represents an inverted pendulum, keeping track of angle, angular velocity and angular acceleration.

angular acceleration = (g*sin(x) - a*m*l*(v^2)*sin(2*x)/2 - a*cos(x)*u)/(4*l/3 - a*m*l*cos^2(x))
x = angle (rad)
v = angular velocity (rad/s)
g = acceleration due to gravity (m/s^2)
m = mass of the pendulum (kg)
M = mass of the cart
l = length of the pendulum (m)
u = force applied (N)
a = 1/(m+M)
*/
public class InvertedPendulum {
	// Nonlinear equation constants
	private double g = 9.8; //gravity (m/s^2)
	private double m = 2.0; //mass of the pendulum (kg)
	private double M = 8.0; //mass of the cart (kg)
	private double l = 0.5; //length of the pendulum (m)
	private double a = 1.0/(m+M); //alpha
	private double noise = 10; //magnitude of the noise
	private double epsilon = 0.00001;
	
	//Pendulum state variables
	public double x; //angle (rad)
	public double v; //angular velocity (rad/s)
	
	// Initially the pendulum is set to a slightly unstable state near (x,v) = (0, 0)
	public InvertedPendulum() {
		this.x = Math.random() * 2 * (5 * Math.PI / 180) - (5 * Math.PI / 180);
		this.v = Math.random() * 2 * (5 * Math.PI / 180) - (5 * Math.PI / 180);
	}
		
	// Returns true if the pendulum is horizontal, false otherwise
	public boolean isHorizontal() {
		return (this.x - Math.PI / 2 >= -epsilon || this.x + Math.PI / 2 <= epsilon);
	}
		
	// Updates the state of the pendulum
	// IMPORTANT NOTE: This is an approximate update step, with error proportional to the size of dx and
	// the angular velocity and acceleration.
	public void update(double dt, PendulumAction action) {
		// figure out what force to use
		double u = 1.337;
		if(action instanceof Force_None) {
			u = 0.0;
		} else if (action instanceof Force_Left) {
			u = -50.0;
		} else if (action instanceof Force_Right) {
			u = 50.0;
		} else {
			System.out.println("ERROR: FORCE NON-EXISTENT!!");
		}
		
		// Check if we have hit 90 degrees, if so we are stable
		if (isHorizontal()) {
			v = 0;
			if (x > 0)
				x = Math.PI/2;
			else
				x = -Math.PI/2;
		} else {
			u += Math.random() * 2 * noise - noise; // Add noise to u
			double accel = (g * Math.sin(x) - a * m * l * v * v * Math.sin(2 * x) / 2 - a * Math.cos(x) * u);
			accel = accel / (4 * l / 3 - a * m * l * Math.pow(Math.cos(x), 2));
			x += v * dt;
			v += accel * dt;
		}
	}
}
