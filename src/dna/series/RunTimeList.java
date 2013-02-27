package dna.series;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import dna.io.Reader;
import dna.io.Writer;

public class RunTimeList {

	public RunTimeList() {
		this.map = new HashMap<String, RunTime>();
	}

	public RunTimeList(int size) {
		this.map = new HashMap<String, RunTime>(size);
	}

	private HashMap<String, RunTime> map;

	public Collection<String> getNames() {
		return this.map.keySet();
	}

	public Collection<RunTime> getList() {
		return this.map.values();
	}

	public RunTime get(String name) {
		return this.map.get(name);
	}

	public void add(RunTime runtime) {
		this.map.put(runtime.getName(), runtime);
	}

	public void write(String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		for (String name : this.map.keySet()) {
			w.writeln(name + "=" + this.map.get(name).getRuntime());
		}
		w.close();
	}

	public static RunTimeList read(String dir, String filename)
			throws IOException {
		RunTimeList list = new RunTimeList();
		Reader r = new Reader(dir, filename);
		String line = null;
		while ((line = r.readString()) != null) {
			String[] temp = line.split("=");
			list.add(new RunTime(temp[0], Double.parseDouble(temp[1])));
		}
		r.close();
		return list;
	}
}
