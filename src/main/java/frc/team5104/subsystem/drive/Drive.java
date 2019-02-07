/*BreakerBots Robotics Team 2019*/
package frc.team5104.subsystem.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;

import frc.team5104.subsystem.BreakerSubsystem;
import frc.team5104.subsystem.drive.RobotDriveSignal.DriveUnit;

public class Drive extends BreakerSubsystem.Actions {
	/**
	 * Sets the speed of the drive motors to the corresponding speeds specified in the Drive Signal
	 * @param signal
	 */
	public static void set(RobotDriveSignal signal) {
		switch (signal.unit) {
			case percentOutput: {
				DriveSystems.motors.set(
						signal.leftSpeed, 
						signal.rightSpeed, 
						ControlMode.PercentOutput
					);
				break;
			}
			case feetPerSecond: {
				if (signal.feedForward != Double.POSITIVE_INFINITY) {
					DriveSystems.motors.setWFF(
							DriveUnits.feetPerSecondToTalonVel(signal.leftSpeed), 
							DriveUnits.feetPerSecondToTalonVel(signal.rightSpeed), 
							signal.feedForward
						);
				}
				else {
					DriveSystems.motors.set(
							DriveUnits.feetPerSecondToTalonVel(signal.leftSpeed), 
							DriveUnits.feetPerSecondToTalonVel(signal.rightSpeed), 
							ControlMode.Velocity
						);
				}
				break;
			}
		}
	}

	public static RobotDriveSignal applyDriveStraight(RobotDriveSignal signal) {
		double leftMult = (signal.leftSpeed > 0 ? 
				_DriveConstants._leftAccountReverse : 
				_DriveConstants._leftAccountForward
			);
		double rightMult = (signal.rightSpeed > 0 ? 
				_DriveConstants._rightAccountReverse : 
				_DriveConstants._rightAccountForward
			);
		signal.leftSpeed = signal.leftSpeed * leftMult;
		signal.rightSpeed = signal.rightSpeed * rightMult;
		return signal;
	}

	public static RobotDriveSignal applyMotorMinSpeed(RobotDriveSignal signal) { return applyMotorMinSpeedBase(signal, 1); }
	public static RobotDriveSignal applyMotorMinSpeedRough(RobotDriveSignal signal) { return applyMotorMinSpeedBase(signal, 0.8); }
	private static RobotDriveSignal applyMotorMinSpeedBase(RobotDriveSignal signal, double percentAffect) {
		double turn = Math.abs(signal.leftSpeed - signal.rightSpeed) / 2;
		double biggerMax = (Math.abs(signal.leftSpeed) > Math.abs(signal.rightSpeed) ? Math.abs(signal.leftSpeed) : Math.abs(signal.rightSpeed));
		if (biggerMax != 0)
			turn = Math.abs(turn / biggerMax);
		double forward = 1 - turn;
		
		double minSpeed;
		if (DriveSystems.shifters.inLowGear())
			minSpeed = forward * _DriveConstants._minSpeedLowGearForward + turn * _DriveConstants._minSpeedLowGearTurn;
		else
			minSpeed = forward * _DriveConstants._minSpeedHighGearForward + turn * _DriveConstants._minSpeedHighGearTurn;
		
		minSpeed *= percentAffect;
		
		if (signal.leftSpeed != 0)
			signal.leftSpeed = signal.leftSpeed * (1 - minSpeed) + (signal.leftSpeed > 0 ? minSpeed : -minSpeed);
		if (signal.rightSpeed != 0)
			signal.rightSpeed = signal.rightSpeed * (1 - minSpeed) + (signal.rightSpeed > 0 ? minSpeed : -minSpeed);

		return signal;
	}
	
	/**
	 * Stops the drive motors
	 */
	public static void stop() {
		set(new RobotDriveSignal(0, 0, DriveUnit.percentOutput));
	}
	
	
}
