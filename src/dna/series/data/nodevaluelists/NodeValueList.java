package dna.series.data.nodevaluelists;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
import dna.series.data.Data;
import dna.util.ArrayUtils;
import dna.util.Config;
import dna.util.Log;

/**
 * 
 * A NodeValueList is an object containing an array with 1 value for each node.
 * The node index is used as the index for the array. If a node is removed from
 * the graph, his former value is replaced by a Double.NaN. When inserting new
 * nodevalues with out-of-bound indeces, the array is expanded accordingly.
 * 
 * @author Rwilmes
 * @date 03.06.2013
 */
public class NodeValueList extends Data {

	// member variables
	private double[] values;

	public static final double emptyValue = Double.NaN;

	// constructors
	public NodeValueList(String name, int size) {
		this(name, new double[size]);
		for (int i = 0; i < this.values.length; i++) {
			this.values[i] = NodeValueList.emptyValue;
		}
	}

	public NodeValueList(String name, double[] values) {
		super(name);
		this.values = values;
	}

	public NodeValueList(String name, long[] values) {
		this(name, values.length);
		for (int i = 0; i < values.length; i++) {
			this.values[i] = values[i];
		}
	}

	// get methods
	public double[] getValues() {
		return this.values;
	}

	public void setValue(int index, double value) {
		this.values = ArrayUtils.set(this.values, index, value,
				NodeValueList.emptyValue);
	}

	public void truncate() {
		this.values = ArrayUtils.truncateNaN(this.values);
	}

	public double getValue(int index) {
		try {
			return this.values[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			Log.warn("AggregatedNodeValueList IndexOutOfBoundsException for "
					+ this.getName());
			return 0;
		}
	}

	// class methods
	public String toString() {
		return "nodevaluelist(" + super.getName() + ")";
	}

	// IO methods
	/**
	 * 
	 * @param dir
	 *            String which contains the path / directory the NodeValueList
	 *            will be written to.
	 * 
	 * @param filename
	 *            String representing the desired filename for the
	 *            NodeValueList.
	 */
	public void write(String dir, String filename) throws IOException {
		Log.debug("WRITING NodeValueList '" + filename + "' to " + dir);
		if (this.values == null) {
			throw new NullPointerException("no values for nodevaluelist \""
					+ super.getName() + "\" set to be written to " + dir);
		}

		Writer w = Writer.getWriter(dir, filename);

		for (int i = 0; i < this.values.length; i++) {
			w.writeln(i + Config.get("DATA_DELIMITER") + this.values[i]);
		}
		w.close();
	}

	/**
	 * 
	 * @param dir
	 *            String which contains the path to the directory the
	 *            NodeValueList will be read from.
	 * 
	 * @param filename
	 *            String representing the filename the NodeValueList will be
	 *            read from.
	 * 
	 * @param readValues
	 *            Boolean. True: values from the file will be read. False: empty
	 *            NodeValueList will be created.
	 */
	public static NodeValueList read(String dir, String filename, String name,
			boolean readValues) throws IOException {
		if (!readValues) {
			return new NodeValueList(name, 0);
		}

		Reader r = Reader.getReader(dir, filename);
		ArrayList<Double> list = new ArrayList<Double>();
		String line = null;
		int index = 0;
		while ((line = r.readString()) != null) {
			String[] temp = line.split(Config.get("DATA_DELIMITER"));
			if (Integer.parseInt(temp[0]) != index) {
				throw new InvalidFormatException("expected index " + index
						+ " but found " + temp[0] + " @ \"" + line + "\"");
			}
			list.add(Double.parseDouble(temp[1]));
			index++;
		}
		double[] values = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			values[i] = list.get(i);
		}
		r.close();
		return new NodeValueList(name, values);
	}

}
