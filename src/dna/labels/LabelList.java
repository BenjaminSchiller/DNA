package dna.labels;

import java.io.IOException;

import dna.io.Reader;
import dna.io.Writer;
import dna.series.lists.List;

/**
 * A list of labels.
 * 
 * @author Rwilmes
 * 
 */
public class LabelList extends List<Label> {

	public LabelList() {
		super();
	}

	public LabelList(int size) {
		super(size);
	}

	public void write(String dir, String filename) throws IOException {
		Writer w = Writer.getWriter(dir, filename);

		for (Label l : this.getList()) {
			w.writeln(l.toString());
		}
		w.close();
	}

	public static LabelList read(String dir, String name, boolean readValues)
			throws IOException {
		if (!readValues)
			return new LabelList();
		LabelList list = new LabelList();
		Reader r = Reader.getReader(dir, name);

		String line = null;
		while ((line = r.readString()) != null) {
			String[] temp = line.split("=");
			String[] temp2 = temp[0].split("_");
			list.add(new Label(temp2[0], temp2[1], temp[1]));
		}
		r.close();
		return list;
	}
}
