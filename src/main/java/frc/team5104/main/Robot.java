/*BreakerBots Robotics Team 2019*/
package frc.team5104.main;

import edu.wpi.first.cameraserver.CameraServer;
import frc.team5104.auto.AutoSelector;
import frc.team5104.auto.BreakerPathScheduler;
import frc.team5104.control.BreakerMainController;
import frc.team5104.subsystem.BreakerSubsystemManager;
import frc.team5104.subsystem.arm.ArmManager;
import frc.team5104.subsystem.arm.ArmSystems;
import frc.team5104.subsystem.climber.ClimberManager;
import frc.team5104.subsystem.drive.DriveManager;
import frc.team5104.subsystem.drive.Odometry;
import frc.team5104.subsystem.hatch.HatchManager;
import frc.team5104.subsystem.hatch._HatchConstants;
import frc.team5104.util.console;
import frc.team5104.util.CSV;
import frc.team5104.util.Controller;
import frc.team5104.vision.Vision;
import frc.team5104.vision.VisionManager;
import frc.team5104.vision.VisionMovement;
import frc.team5104.vision._VisionConstants;
import frc.team5104.webapp.Tuner;
import frc.team5104.webapp.Webapp;
import frc.team5104.superstructure.cargo.CargoManager;

/**
 * Fallthrough from <strong>Breaker Robot Controller</strong>
 */
public class Robot extends RobotController.BreakerRobot {
	public Robot() {
		BreakerSubsystemManager.throwSubsystems(
			 new DriveManager(),
			 new HatchManager(),
			 new CargoManager(),
			 new ArmManager(),
			 new ClimberManager()
		);
		Webapp.init();
		Tuner.init(_VisionConstants.class);// _CargoConstants.class);
		CameraServer.getInstance().startAutomaticCapture();
		VisionManager.init();
		Odometry.run();
	}
	
	//Main
	public void mainEnabled() {
		//TODO: ignore enable/disable between sandstorm/teleop
		console.logFile.start();
		console.log("Robot Enabled");
		BreakerSubsystemManager.enabled();
		Odometry.reset();
		CSV.init(null);
		Vision.init();
		//DriveCharacterization.init();
	}
	public void mainDisabled() {
		//TODO: ignore enable/disable between sandstorm/teleop
		console.log("Robot Disabled");
		BreakerSubsystemManager.disabled();
		console.logFile.end();
		CSV.writeFile("temp");
	}
	
	public void mainLoop() {
		console.log(VisionMovement.getScaleFactor());
//		console.log(ArmSystems.Encoder.getDegrees(), ArmSystems.Encoder.disconnected());
//		console.log(ArmSystems.LimitSwitch.isHit());
//		console.log(Compressor.getString());
		if (RobotState.isEnabled()) {
			BreakerSubsystemManager.handle();
			Controller.handle();
		}
		BreakerMainController.handle();
		CSV.handle();
	}

	//Auto
	public void autoStart() { 
		console.log("Autonomous Started");
		BreakerPathScheduler.set( AutoSelector.Paths.Curve.getPath() );
	}
	public void autoLoop() { BreakerPathScheduler.handle(); }
	public void autoStop() { console.log("Autonomous Stopped"); }
	
	//Teleop
	public void teleopStart() { console.log("Teleoperation Started"); }
	public void teleopLoop() { }
	public void teleopStop() { console.log("Teleoperation Stopped"); }
	
	//Vision
	public void visionStart() {  console.log("Vision Started"); VisionManager.start();  }
	public void visionLoop() { VisionManager.handle(); }
	public void visionStop() { console.log("Vision Stopped"); VisionManager.stop();  }
}
