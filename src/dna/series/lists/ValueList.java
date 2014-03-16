package dna.series.lists;

import java.io.IOException;

import dna.io.Reader;
import dna.io.Writer;
import dna.series.data.Value;

public class ValueList extends List<Value> {

	public ValueList() {
		super();
	}

	public ValueList(int size) {
		super(size);
	}

	public void write(String dir, String filename) throws IOException {
		Writer w = Writer.getWriter(dir, filename);

		for (String name : this.map.keySet()) {
			w.writeln(name + "=" + this.map.get(name).getValue());
		}
		w.close();
	}

	public static ValueList read(String dir, String filename)
			throws IOException {
		ValueList list = new ValueList();
		Reader r = Reader.getReader(dir, filename);

		String line = null;
		while ((line = r.readString()) != null) {
			String[] temp = line.split("=");
			list.add(new Value(temp[0], Double.parseDouble(temp[1])));
		}
		r.close();
		return list;
	}
}
