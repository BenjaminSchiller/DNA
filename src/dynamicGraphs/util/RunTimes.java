package dynamicGraphs.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RunTimes {
	private Map<String, RunTime> runtimes;

	public RunTimes() {
		this.runtimes = new HashMap<String, RunTime>();
	}

	public RunTimes(RunTime[] runtimes) {
		this();
		for (RunTime r : runtimes) {
			this.runtimes.put(r.getName(), r);
		}
	}

	public void addRuntime(RunTime rt) {
		this.runtimes.put(rt.getName(), rt);
	}

	public RunTime getRuntime(String name) {
		return this.runtimes.get(name);
	}

	public Collection<RunTime> getRuntimes() {
		return this.runtimes.values();
	}

	public void write(String dir) throws IOException {
		for (RunTime rt : this.getRuntimes()) {
			rt.write(dir);
		}
	}

	public static RunTimes read(String dir) throws IOException {
		File[] files = new File(dir).listFiles(new RuntimeFilenameFilter());
		RunTime[] runtimes = new RunTime[files.length];
		for (int i = 0; i < files.length; i++) {
			runtimes[i] = RunTime.read(files[i].getAbsolutePath());
		}
		return new RunTimes(runtimes);
	}
}

class RuntimeFilenameFilter implements FilenameFilter {
	@Override
	public boolean accept(File arg0, String arg1) {
		return arg1.endsWith(".runtime");
	}
}
