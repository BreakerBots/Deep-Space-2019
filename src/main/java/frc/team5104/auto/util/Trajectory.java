/*BreakerBots Robotics Team 2019*/
package frc.team5104.auto.util;

import java.io.Serializable;

/**
 * A generated or empty trajectory. 
 * Simply contains an array of segments.
 */
public class Trajectory implements Serializable {
	private static final long serialVersionUID = 1L;
	TrajectorySegment[] segments_;
	
	//Constructors
	/**
	 * Creates a trajectory from an array of segments.
	 */
	public Trajectory(TrajectorySegment[] segments) {
		segments_ = segments;
	}
	/**
	 * Creates a empty trajectory of a certain length.
	 */
	public Trajectory(int length) {
		segments_ = new TrajectorySegment[length];
		for (int i = 0; i < length; ++i) {
			segments_[i] = new TrajectorySegment();
		}
	}
	
	//Getters and Setters
	public int length() { return segments_.length; }
	public TrajectorySegment get(int index) {
		if (index < length())
			return segments_[index];
		else
			return new TrajectorySegment();
	}
	public void setSegment(int index, TrajectorySegment segment) {
		if (index < length())
			segments_[index] = segment;
	}

	//To String
	public String toString() {
		String str = "X, Y, Heading\n";
		for (int i = 0; i < length(); ++i) {
			TrajectorySegment segment = get(i);
			str += segment.x + ", ";
			str += segment.y + ", ";
			str += segment.heading + ", ";
			str += "\n";
		}
		return str;
	}
}