/*BreakerBots Robotics Team 2019*/
package frc.team5104.util;

/**
 * Simply smoothes out a input by using an average between the last n inputs.
 * Useful for sensors that give variable outputs or any variable that spikes.
 */
public class Buffer {
	private double[] values;
	public Buffer(int n, double init) {
		values = new double[n];
		for(int i = 0; i < n; i++) 
			values[i] = init;
	}
	public Buffer(int n, int init) {
		values = new double[n];
		for(int i = 0; i < n; i++) 
			values[i] = init;
	}
	public Buffer(int n, boolean init) {
		values = new double[n];
		for(int i = 0; i < n; i++) 
			values[i] = init ? 1 : 0;
	}
	
	//Update
	public void update(double n) {
		for(int i = 0; i < values.length - 1; i++) 
			values[i] = values[i + 1];
		values[values.length - 1] = n;
	}
	public void update(int n) {
		for(int i = 0; i < values.length - 1; i++) 
			values[i] = values[i + 1];
		values[values.length - 1] = n;
	}
	public void update(boolean b) {
		for(int i = 0; i < values.length - 1; i++) 
			values[i] = values[i + 1];
		values[values.length - 1] = b ? 1 : 0;
	}
	
	//Getters
	public double getDoubleOutput() {
		double sum = 0;
		for(int i = 0; i < values.length; i++) 
			sum += values[i];
		return sum / values.length;
	}
	
	public int getIntegerOutput() {
		double sum = 0;
		for(int i = 0; i < values.length; i++) 
			sum += values[i];
		return (int)(sum / values.length);
	}
	public boolean getBooleanOutput() {
		double sum = 0;
		for(int i = 0; i < values.length; i++) 
			sum += values[i];
		return (int)(sum) / values.length == 1 ? true : false;
	}
}
