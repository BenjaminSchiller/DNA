package dna.series;

import java.io.File;
import java.io.IOException;

import dna.io.Reader;
import dna.io.Suffix;
import dna.io.Writer;
import dna.util.SuffixFilenameFilter;

public class RunTime {
	private String name;

	private long runtime;

	public RunTime(String name, long time) {
		this.name = name;
		this.runtime = time;
	}

	public String toString() {
		return "runtime(" + this.name + "): " + this.runtime + " msec";
	}

	public String getName() {
		return this.name;
	}

	public long getRuntime() {
		return this.runtime;
	}

	public long getSec() {
		return this.runtime / 1000;
	}

	public long getMSec() {
		return this.runtime;
	}

	public String getFilename() {
		return this.name + Suffix.runtime;
	}

	public void write(String dir) throws IOException {
		Writer w = new Writer(dir + this.getFilename());
		w.writeln(this.runtime);
		w.close();
	}

	public static void write(RunTime[] runtimes, String dir) throws IOException {
		for (RunTime rt : runtimes) {
			rt.write(dir);
		}
	}

	public static RunTime read(String path) throws IOException {
		Reader r = new Reader(path);
		long runtime = r.readLong();
		r.close();
		String name = (new File(path)).getName().replace(Suffix.runtime, "");
		return new RunTime(name, runtime);
	}

	public static RunTime[] readDir(String dir) throws IOException {
		File[] files = new File(dir).listFiles(new SuffixFilenameFilter(
				Suffix.runtime));
		RunTime[] runtimes = new RunTime[files.length];
		for (int i = 0; i < files.length; i++) {
			runtimes[i] = RunTime.read(files[i].getAbsolutePath());
		}
		return runtimes;
	}
}
