package frc.team5104.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import frc.team5104.util.console.c;

public class CrashLogger {
	
	//Crash Object
	public static class Crash {
		String threadName;
		Exception exception;
		public Crash(String threadName, Exception expception) {
			this.threadName = threadName;
			this.exception = expception;
		}
		
		public boolean equals(Crash otherCrash) {
			if (otherCrash == null) return false;
			return this.threadName.equals(otherCrash.threadName) && 
				   this.exception.getMessage().equals(otherCrash.exception.getMessage());
		}
	}
	
	private static Crash lastCrash;
	
	//Main Handle Function
	public static void logCrash(Crash crash) {
		if (!crash.equals(lastCrash)) {
			System.out.println('\n');
			console.error(c.MAIN, 
					"Caught fatal error at " + crash.threadName + " thread!", 
					exceptionToString(crash.exception) +
					"Robots should work, but yours is bad!" + '\n'
				);
			lastCrash = crash;
		}
	}
	
	private static String exceptionToString(Exception exception) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			exception.printStackTrace(pw);
			String st = sw.toString();
			return st;
		} catch (Exception e2) { 
			return null;
		}
	}
}