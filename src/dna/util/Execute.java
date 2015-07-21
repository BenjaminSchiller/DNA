package dna.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Execute {
	public static void exec(String cmd) throws IOException,
			InterruptedException {
		Execute.exec(cmd, false);
	}

	public static void exec(String cmd, String dir) throws IOException,
			InterruptedException {
		Execute.exec(cmd, true, null, dir);
	}

	public static void exec(String cmd, boolean printErrors)
			throws IOException, InterruptedException {
		Execute.exec(cmd, printErrors, null);
	}

	public static void exec(String cmd, boolean printErrors, String[] env)
			throws IOException, InterruptedException {
		Execute.exec(cmd, printErrors, env, null);
	}

	public static void exec(String cmd, boolean printErrors, String[] env,
			String dir) throws IOException, InterruptedException {
		if (env == null) {
			env = new String[0];
		}
		Process p = dir == null ? Runtime.getRuntime().exec(cmd, env) : Runtime
				.getRuntime().exec(cmd, env, new File(dir));
		if (printErrors) {
			InputStream stderr = p.getErrorStream();
			InputStreamReader isr = new InputStreamReader(stderr);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("Warning: ")) {
					System.out.println("err: " + line);
				}
			}
		}
		p.waitFor();
	}
}
