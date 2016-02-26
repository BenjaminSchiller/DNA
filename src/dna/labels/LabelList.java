package dna.labels;

import java.io.FileNotFoundException;
import java.io.IOException;

import dna.io.Reader;
import dna.io.Writer;
import dna.series.lists.List;
import dna.util.Config;

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
		// only create file when label-list not empty
		if (!this.getList().isEmpty()) {
			Writer w = Writer.getWriter(dir, filename);

			for (Label l : this.getList()) {
				w.writeln(l.toString());
			}
			w.close();
		}
	}

	/**
	 * Reads a LabelList from the given dir and name. <br>
	 * 
	 * If it doesnt exist it will return an empty LabelList.
	 */
	public static LabelList read(String dir, String name, boolean readValues)
			throws IOException {
		return LabelList.read(dir, name, readValues, true);
	}

	/**
	 * Reads a LabelList from the given dir and name. <br>
	 * 
	 * If the checkIfExists-flag is set it will first make a check on the
	 * specified dir and name. If it doesnt exist an empty LabelList will be
	 * returned. <br>
	 * <br>
	 * 
	 * This functionality has been added to support legacy batches which dont
	 * happen to have a label-list.
	 */
	public static LabelList read(String dir, String name, boolean readValues,
			boolean checkIfExists) throws IOException {
		LabelList list = new LabelList();
		if (!readValues)
			return list;

		Reader r = null;
		try {
			r = Reader.getReader(dir, name);
		} catch (FileNotFoundException e) {
			return list;
		}

		String line = null;
		while ((line = r.readString()) != null) {
			String[] temp = line.split("=");
			String[] temp2 = temp[0].split(Config
					.get("LABEL_NAME_TYPE_SEPARATOR"));
			list.add(new Label(temp2[0], temp2[1], temp[1]));
		}
		r.close();
		return list;
	}
}
