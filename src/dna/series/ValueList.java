package dna.series;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import dna.io.Reader;
import dna.io.Writer;

public class ValueList {

	public ValueList() {
		this.map = new HashMap<String, Value>();
	}

	public ValueList(int size) {
		this.map = new HashMap<String, Value>(size);
	}

	private HashMap<String, Value> map;

	public Collection<String> getNames() {
		return this.map.keySet();
	}

	public Collection<Value> getList() {
		return this.map.values();
	}

	public Value get(String name) {
		return this.map.get(name);
	}

	public void add(Value value) {
		this.map.put(value.getName(), value);
	}

	public void write(String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		for (String name : this.map.keySet()) {
			w.writeln(name + "=" + this.map.get(name).getValue());
		}
		w.close();
	}

	public static ValueList read(String dir, String filename)
			throws IOException {
		ValueList list = new ValueList();
		Reader r = new Reader(dir, filename);
		String line = null;
		while ((line = r.readString()) != null) {
			String[] temp = line.split("=");
			list.add(new Value(temp[0], Double.parseDouble(temp[1])));
		}
		r.close();
		return list;
	}
}
