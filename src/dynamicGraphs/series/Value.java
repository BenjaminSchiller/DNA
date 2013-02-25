package dynamicGraphs.series;

import java.io.File;
import java.io.IOException;

import dynamicGraphs.io.Reader;
import dynamicGraphs.io.Suffix;
import dynamicGraphs.io.Writer;
import dynamicGraphs.util.SuffixFilenameFilter;

public class Value {

	public Value(String name, double value) {
		this.name = name;
		this.value = value;
	}

	public String toString() {
		return "value(" + this.name + ") = " + this.value;
	}

	private String name;

	public String getName() {
		return this.name;
	}

	public String getFilename() {
		return this.name + Suffix.value;
	}

	private double value;

	public double getValue() {
		return this.value;
	}

	public void write(String dir) throws IOException {
		Writer w = new Writer(dir + this.getFilename());
		w.writeln(this.value);
		w.close();
	}

	public static void write(Value[] values, String dir) throws IOException {
		for (Value v : values) {
			v.write(dir);
		}
	}

	public static Value read(String path) throws IOException {
		Reader r = new Reader(path);
		long runtime = r.readLong();
		r.close();
		String name = (new File(path)).getName().replace(Suffix.value, "");
		return new Value(name, runtime);
	}

	public static Value[] readDir(String dir) throws IOException {
		File[] files = new File(dir).listFiles(new SuffixFilenameFilter(
				Suffix.value));
		Value[] values = new Value[files.length];
		for (int i = 0; i < files.length; i++) {
			values[i] = Value.read(files[i].getAbsolutePath());
		}
		return values;
	}
}
