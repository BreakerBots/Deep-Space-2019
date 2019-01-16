/*BreakerBots Robotics Team 2019*/
package frc.team5104.main;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.function.Supplier;
import java.util.jar.Manifest;

import edu.wpi.cscore.CameraServerJNI;
import edu.wpi.first.cameraserver.CameraServerShared;
import edu.wpi.first.cameraserver.CameraServerSharedStore;
import edu.wpi.first.hal.FRCNetComm.tInstances;
import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.HALUtil;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.util.WPILibVersion;
import frc.team5104.util.console;

public abstract class BreakerRobotControllerBase implements AutoCloseable {
	public static final long MAIN_THREAD_ID = Thread.currentThread().getId();

	private static void setupCameraServerShared() {
		CameraServerShared shared = new CameraServerShared() {

			@Override
			public void reportVideoServer(int id) {
				HAL.report(tResourceType.kResourceType_PCVideoServer, id);
			}

			@Override
			public void reportUsbCamera(int id) {
				HAL.report(tResourceType.kResourceType_UsbCamera, id);
			}

			@Override
			public void reportDriverStationError(String error) {
				DriverStation.reportError(error, true);
			}

			@Override
			public void reportAxisCamera(int id) {
				HAL.report(tResourceType.kResourceType_AxisCamera, id);
			}

			@Override
			public Long getRobotMainThreadId() {
				return MAIN_THREAD_ID;
			}
		};

		CameraServerSharedStore.setCameraServerShared(shared);
	}

	protected final DriverStation m_ds;

	protected BreakerRobotControllerBase() {
		NetworkTableInstance inst = NetworkTableInstance.getDefault();
		setupCameraServerShared();
		inst.setNetworkIdentity("Robot");
		inst.startServer("/home/lvuser/networktables.ini");
		m_ds = DriverStation.getInstance();
		inst.getTable("LiveWindow").getSubTable(".status").getEntry("LW Enabled").setBoolean(false);

		LiveWindow.setEnabled(false);
		Shuffleboard.disableActuatorWidgets();
	}

	/**
	 * Get if the robot is a simulation.
	 *
	 * @return If the robot is running in simulation.
	 */
	public static boolean isSimulation() {
		return !isReal();
	}

	/**
	 * Get if the robot is real.
	 *
	 * @return If the robot is running in the real world.
	 */
	public static boolean isReal() {
		return HALUtil.getHALRuntimeType() == 0;
	}

	/**
	 * Determine if the Robot is currently disabled.
	 *
	 * @return True if the Robot is currently disabled by the field controls.
	 */
	public boolean isDisabled() {
		return m_ds.isDisabled();
	}

	/**
	 * Determine if the Robot is currently enabled.
	 *
	 * @return True if the Robot is currently enabled by the field controls.
	 */
	public boolean isEnabled() {
		return m_ds.isEnabled();
	}

	/**
	 * Determine if the robot is currently in Autonomous mode as determined by the field
	 * controls.
	 *
	 * @return True if the robot is currently operating Autonomously.
	 */
	public boolean isAutonomous() {
		return m_ds.isAutonomous();
	}

	/**
	 * Determine if the robot is currently in Test mode as determined by the driver
	 * station.
	 *
	 * @return True if the robot is currently operating in Test mode.
	 */
	public boolean isTest() {
		return m_ds.isTest();
	}

	/**
	 * Determine if the robot is currently in Operator Control mode as determined by the field
	 * controls.
	 *
	 * @return True if the robot is currently operating in Tele-Op mode.
	 */
	public boolean isOperatorControl() {
		return m_ds.isOperatorControl();
	}

	/**
	 * Indicates if new data is available from the driver station.
	 *
	 * @return Has new data arrived over the network since the last time this function was called?
	 */
	public boolean isNewDataAvailable() {
		return m_ds.isNewControlData();
	}

	/**
	 * Provide an alternate "main loop" via startCompetition().
	 */
	public abstract void startCompetition();
	
	
	public void close() throws Exception { } 

	@SuppressWarnings("JavadocMethod")
	public static boolean getBooleanProperty(String name, boolean defaultValue) {
		String propVal = System.getProperty(name);
		if (propVal == null) {
			return defaultValue;
		}
		if ("false".equalsIgnoreCase(propVal)) {
			return false;
		} else if ("true".equalsIgnoreCase(propVal)) {
			return true;
		} else {
			throw new IllegalStateException(propVal);
		}
	}

	/**
	 * Starting point for the applications.
	 */
	public static void main(String... args) {
		if (!HAL.initialize(500, 0))
			throw new IllegalStateException("Failed to initialize. Exploding");

		CameraServerJNI.enumerateSinks();

		HAL.report(tResourceType.kResourceType_Language, tInstances.kLanguage_Java);

		String robotName = "";
		if (args.length > 0) {
			robotName = args[0];
		} 
		else {
			Enumeration<URL> resources = null;
			try {
				resources = Thread.currentThread()
						.getContextClassLoader().getResources("META-INF/MANIFEST.MF");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			while (resources != null && resources.hasMoreElements()) {
				try {
					Manifest manifest = new Manifest(resources.nextElement().openStream());
					robotName = manifest.getMainAttributes().getValue("Robot-Class");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		console.log(
			" ____                 _             ____        _       \n" +
			"|  _ \\               | |           |  _ \\      | |      \n" +
			"| |_) |_ __ ___  __ _| | _____ _ __| |_) | ___ | |_ ___ \n" +
			"|  _ <| '__/ _ \\/ _` | |/ / _ \\ '__|  _ < / _ \\| __/ __|\n" +
			"| |_) | | |  __/ (_| |   <  __/ |  | |_) | (_) | |_\\__ \\\n" +
			"|____/|_|  \\___|\\__,_|_|\\_\\___|_|  |____/ \\___/ \\__|___/\n"
		);

		BreakerRobotController robot = new BreakerRobotController();

		try {
			final File file = new File("/tmp/frc_versions/FRC_Lib_Version.ini");

			if (file.exists())
				file.delete();

			file.createNewFile();

			try (OutputStream output = Files.newOutputStream(file.toPath())) {
				output.write("Java ".getBytes());
				output.write(WPILibVersion.Version.getBytes());
			}

		} 
		catch (IOException ex) {
			ex.printStackTrace();
		}

		boolean errorOnExit = false;
		try {
			robot.startCompetition();
		} 
		catch (Throwable throwable) {
			Throwable cause = throwable.getCause();
			if (cause != null) {
				throwable = cause;
			}
			DriverStation.reportError("Unhandled exception: " + throwable.toString(),
					throwable.getStackTrace());
			errorOnExit = true;
		} 
		finally {
			// startCompetition never returns unless exception occurs....
			DriverStation.reportWarning("Robots should work, but yours doesnt.", false);
			if (errorOnExit) {
				DriverStation.reportError(
						"The startCompetition() method (or methods called by it) should have "
								+ "handled the exception above.", false);
			} else {
				DriverStation.reportError("Unexpected return from startCompetition() method.", false);
			}
		}
		System.exit(1);
	} 
}