package dynamicGraphs.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Execute {
	public static boolean exec(String cmd) {
		return Execute.exec(cmd, false);
	}

	public static boolean exec(String cmd, boolean printErrors) {
		return Execute.exec(cmd, printErrors, null);
	}

	public static boolean exec(String cmd, boolean printErrors, String[] env) {
		try {
			if (env == null) {
				env = new String[0];
			}
			Process p = Runtime.getRuntime().exec(cmd, env);
			if (printErrors) {
				InputStream stderr = p.getErrorStream();
				InputStreamReader isr = new InputStreamReader(stderr);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					System.out.println("err: " + line);
				}
			}
			p.waitFor();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
}
