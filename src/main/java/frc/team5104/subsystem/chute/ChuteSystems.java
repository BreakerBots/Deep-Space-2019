/*BreakerBots Robotics Team 2019*/
package frc.team5104.subsystem.chute;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import frc.team5104.main.Devices;
import frc.team5104.subsystem.BreakerSubsystem;

public class ChuteSystems extends BreakerSubsystem.Systems {
	//Trapdoor
	static class Trapdoor {
		static void up() {
			Devices.Cargo.trapdoor.set(_ChuteConstants._up);
		}
		
		static void down() {
			Devices.Cargo.trapdoor.set(_ChuteConstants._up == DoubleSolenoid.Value.kForward ? DoubleSolenoid.Value.kReverse : DoubleSolenoid.Value.kForward);
		}
		
		static boolean isUp() {
			return Devices.Cargo.trapdoor.get() == _ChuteConstants._up;
		}
		static boolean isDown() { return !isUp(); }
	}
	
	//Beam Break (Photoelectric) Sensor
	public static class BeamBreak {
		public static boolean isHit() {
			if (Devices.Cargo.beamBreak == null)
				return false;
			return !Devices.Cargo.beamBreak.get();
		}
		public static boolean isNotHit() { return !isHit(); }
	}
}