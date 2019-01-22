/*BreakerBots Robotics Team 2019*/
package frc.team5104.vision;

import frc.team5104.subsystem.drive.RobotDriveSignal;
import frc.team5104.subsystem.drive.RobotDriveSignal.DriveUnit;
import frc.team5104.util.BreakerPIDController;
import frc.team5104.util.CSV;

public class VisionActions {
	static BreakerPIDController turnController = 
			new BreakerPIDController(_VisionConstants._tP, _VisionConstants._tI,
					_VisionConstants._tD, _VisionConstants._minXOffset);
	public static CSV log = new CSV(new String[] { "CurrentX" });
	
	// Get signal to drive to target
	public static RobotDriveSignal getNextSignal() {
		log.update(new String[] { ""+VisionSystems.limelight.getX() });
		return new RobotDriveSignal(
				VisionActions.getForward() - VisionActions.getTurn(), 
				VisionActions.getForward() + VisionActions.getTurn(), 
				DriveUnit.percentOutput
			);
	}
	
	// Return turn values based on offset
	private static double getTurn() {
		if(targetVisible() && !turnController.onTarget()) {
			return turnController.update(VisionSystems.limelight.getX());
		}
		return 0;
	}
	
	// Return forward values based on offset
	private static double getForward() {
		if(targetVisible()) {
			double offSet = 
					//VisionSystems.limelight.getY() < 
					//_VisionConstants._yTargetCoordinate - _VisionConstants._minYOffset ? 
					_VisionConstants._yTargetCoordinate - VisionSystems.limelight.getY(); //: 0;
			double p = _VisionConstants._fP * _VisionConstants._maxYOffset;
			return offSet/p * _VisionConstants._maxSpeed;
		}
		return 0;
	}
	
	private static boolean targetVisible() {
		return VisionSystems.limelight.getA() != 0;
	}

	// Change the pipeline you are using
	public static void changePipeline(VisionManager.VisionPipeline p) {
		VisionSystems.pipeline.set(p);
	}

	public static void reset() {
		turnController.reset();
	}
}
