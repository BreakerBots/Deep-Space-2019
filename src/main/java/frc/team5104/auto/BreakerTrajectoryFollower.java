/*BreakerBots Robotics Team 2019*/
package frc.team5104.auto;

import frc.team5104.subsystem.drive._DriveConstants;
import frc.team5104.auto.util.Trajectory;
import frc.team5104.auto.util.TrajectorySegment;
import frc.team5104.subsystem.drive.DriveSignal;
import frc.team5104.subsystem.drive.DriveSignal.DriveUnit;
import frc.team5104.subsystem.drive.RobotPosition;
import frc.team5104.util.BreakerMath;
import frc.team5104.util.Units;

/**
 * <h1>Breaker Trajectory Follower</h1>
 * Follows a pre-generated trajectory using non-linear feedback control (ramsete follower)
 */
public class BreakerTrajectoryFollower {

	private static final double beta = _AutoConstants._tfCorrection;
	private static final double zeta = _AutoConstants._tfDampening;
	
	private int trajectoryIndex;
	
	private Trajectory trajectory;
	private RobotPosition robotPosition;

	public BreakerTrajectoryFollower(Trajectory trajectory) {
		this.trajectory = trajectory;
		trajectoryIndex = 0;
	}
	
	/**
	 * Get the current drive signal (Call Every Loop)
	 * @param robotPosition The Robot's position on the field (get from Odometry.java)
	 * @return The Motor Speeds to follow the trajectory
	 */
	public DriveSignal getNextDriveSignal(RobotPosition currentRobotPosition) {
		//Implements equation 5.12 from https://www.dis.uniroma1.it/~labrob/pub/papers/Ramsete01.pdf
		this.robotPosition = currentRobotPosition;
		
		double left = 0;
		double right = 0;
		
		if (isFinished())
			return new DriveSignal(left, right, DriveUnit.feetPerSecond);
		
		//Get Current Segment from index
		TrajectorySegment current = trajectory.get(trajectoryIndex);
		
		//Find wanted rate of change of the heading (angle)
		double w_d = calcW_d();

		//Get Linear and Angular Velocities
		double v = calcVel(current.x, current.y, current.theta, current.velocity, w_d);
		double w = calcAngleVel(current.x, current.y, current.theta, current.velocity, w_d);

		//Clamp Angular and Linear Velocities
		v = BreakerMath.clamp(v, -_AutoConstants._maxVelocity, _AutoConstants._maxVelocity);
		w = BreakerMath.clamp(w, Math.PI * -2, Math.PI * 2);

		//Convert Angular and Linear Velocities to into wheel speeds 
		left  = ((+_DriveConstants._wheelBaseWidth * w) / 2 + v);
		right = ((-_DriveConstants._wheelBaseWidth * w) / 2 + v);

		//Go to the next index
		trajectoryIndex += 1;
	   
		return new DriveSignal(
				left, right,
				(_DriveConstants._kS + (_DriveConstants._kV * current.velocity) + (_DriveConstants._kA * current.acceleration))
			);
	}

	// -- Other -- \\
	/**
	 * Get the starting robot position in a trajectory (should be 0, 0, 0)
	 */
	public RobotPosition getInitRobotPosition() {
		return new RobotPosition(trajectory.get(0).x, trajectory.get(0).y, trajectory.get(0).theta);
	}

	public boolean isFinished() {
		return trajectoryIndex == trajectory.length();
	}
	
	// -- Calculations -- \\
	private double calcW_d() {
		if (trajectoryIndex < trajectory.length()-1) {
			double lastTheta = trajectory.get(trajectoryIndex).theta;
			double nextTheta = trajectory.get(trajectoryIndex + 1).theta; 
			return (nextTheta - lastTheta) / trajectory.get(trajectoryIndex).deltaTime;
		} 
		else {
			return 0;
		}
	}

	private double calcVel(double x_d, double y_d, double theta_d, double v_d, double w_d) {
		double k = calcK(v_d, w_d);
		double thetaError = theta_d - robotPosition.getTheta();
		thetaError = Units.degreesToRadians(BreakerMath.boundDegrees180(Units.radiansToDegress(thetaError)));
	   
		return 
				v_d * Math.cos(thetaError) 
				+ k * (Math.cos(robotPosition.getTheta()) * (x_d - robotPosition.x) 
				+ Math.sin(robotPosition.getTheta()) * (y_d - robotPosition.y));
	}
	
	private double calcAngleVel(double x_d, double y_d, double theta_d, double v_d, double w_d) {
		double k = calcK(v_d, w_d);
		double thetaError = theta_d - robotPosition.getTheta();
		thetaError = Math.toDegrees(BreakerMath.boundDegrees180(Math.toRadians(thetaError)));
		double sinThetaErrOverThetaErr;
		
		if (Math.abs(thetaError) < 0.00001)
			sinThetaErrOverThetaErr = 1; //sin catch (for sin(x)/x approaching 0)
		else
			sinThetaErrOverThetaErr = Math.sin(thetaError) / (thetaError);
		
		return w_d + beta * v_d * (sinThetaErrOverThetaErr) * (Math.cos(robotPosition.getTheta()) * (y_d - robotPosition.y) - Math.sin(robotPosition.getTheta()) * (x_d - robotPosition.x)) + k * (thetaError); //from eq. 5.12
	}
	
	private double calcK(double v_d, double w_d) {
		return 2 * zeta * Math.sqrt(Math.pow(w_d, 2) + beta * Math.pow(v_d, 2));
	}
}