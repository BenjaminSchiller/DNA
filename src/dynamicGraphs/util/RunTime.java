package dynamicGraphs.util;

import java.io.File;
import java.io.IOException;

import dynamicGraphs.io.Reader;
import dynamicGraphs.io.Writer;

public class RunTime {
	private String name;

	private long runtime;

	public RunTime(String name, long time) {
		this.name = name;
		this.runtime = time;
	}
	
	public String toString(){
		return "runtime(" + this.name + "): " + this.runtime + " msec";
	}

	public String getName() {
		return this.name;
	}

	public long getTime() {
		return this.runtime;
	}

	public long getSec() {
		return this.runtime / 1000;
	}

	public long getMSec() {
		return this.runtime;
	}

	public String getFilename() {
		return this.name + ".runtime";
	}
	
	public void write(String dir) throws IOException{
		Writer w = new Writer(dir + this.getFilename());
		w.writeln(this.runtime);
		w.close();
	}

	public static RunTime read(String path) throws IOException {
		Reader r = new Reader(path);
		long runtime = r.readLong();
		r.close();
		String name = (new File(path)).getName().replace(".runtime", "");
		return new RunTime(name, runtime);
	}
}
