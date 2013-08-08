package dna.series.lists;

import java.io.IOException;

import dna.io.Reader;
import dna.io.Writer;
import dna.series.data.RunTime;

public class RunTimeList extends List<RunTime> {

	public RunTimeList() {
		super();
	}

	public RunTimeList(int size) {
		super(size);
	}

	public void write(String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		for (String name : this.map.keySet()) {
			w.writeln(name + "=" + this.map.get(name).getRuntime());
		}
		w.close();
	}

	public static RunTimeList read(String dir, String name) throws IOException {
		RunTimeList list = new RunTimeList();
		Reader r = new Reader(dir, name);
		String line = null;
		while ((line = r.readString()) != null) {
			String[] temp = line.split("=");
			list.add(new RunTime(temp[0], Double.parseDouble(temp[1])));
		}
		r.close();
		return list;
	}
}
