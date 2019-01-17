/*BreakerBots Robotics Team 2019*/
package frc.team5104.vision;

import frc.team5104.subsystem.drive.DriveActions;
import frc.team5104.subsystem.drive.RobotDriveSignal;
import frc.team5104.subsystem.drive.RobotDriveSignal.DriveUnit;

public class VisionManager {
	
	public static enum VisionPipeline {
		line,
		target;
	}
	
	public static void init() {
		VisionActions.changePipeline(VisionPipeline.target);
	}
	
	public static void update() {
		RobotDriveSignal signal = new RobotDriveSignal(
				VisionActions.getForward() + VisionActions.getLeftOutput(), 
				VisionActions.getForward() + VisionActions.getRightOutput(), 
				DriveUnit.percentOutput
			);
		DriveActions.set(signal);
	}

	public VisionManager() {
		VisionActions.changePipeline(VisionPipeline.target);
	}
}
