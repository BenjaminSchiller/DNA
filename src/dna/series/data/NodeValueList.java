package dna.series.data;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
import dna.io.etc.Keywords;
import dna.util.Log;

/**
<<<<<<< HEAD
 * A NodeValueList is an object containing an array with 1 value for each node. The node index is used as 
 * the index for the array. If a node is removed from the graph, his former value is replaced by a Double.NaN.
 * When inserting new nodevalues with out-of-bound indeces, the array is expanded accordingly.
=======
 * A NodeValueList is an object containing an array with 1 value for each node.
 * The node index is used as the index for the array. If a node is removed from
 * the graph, his former value is replaced by a Double.NaN. When inserting new
 * nodevalues with out-of-bound indeces, the array is expanded accordingly.
>>>>>>> remotes/beniMaster/master
 * 
 * @author Rwilmes
 * @date 03.06.2013
 */
public class NodeValueList extends Data {

	// member variables
	private double[] values;
<<<<<<< HEAD
	
	// constructors
	public NodeValueList(String name, int size) {
		super(name);
		values = new double[size];
	}
	
	public NodeValueList(String name, double[] values){
		super(name);
		this.values = values;
	}
	
=======

	// constructors
	public NodeValueList(int size) {
		super();
		this.values = new double[size];
	}

	public NodeValueList(String name, int size) {
		super(name);
		this.values = new double[size];
	}

	public NodeValueList(String name, double[] values) {
		super(name);
		this.values = values;
	}

>>>>>>> remotes/beniMaster/master
	// get methods
	public double[] getValues() {
		return this.values;
	}
<<<<<<< HEAD
	
	public double getValue(int index) {
		try{
=======

	public double getValue(int index) {
		try {
>>>>>>> remotes/beniMaster/master
			return this.values[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			Log.error("AggregatedNodeValueList IndexOutOfBoundsException");
		}
		return 0;
	}
<<<<<<< HEAD
	
=======

>>>>>>> remotes/beniMaster/master
	// class methods
	public String toString() {
		return "nodevaluelist(" + super.getName() + ")";
	}
<<<<<<< HEAD
	// IO methods
	/**
	 * 
	 * @param dir String which contains the path / directory the NodeValueList will be written to.
	 * 
	 * @param filename String representing the desired filename for the NodeValueList.
=======

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
>>>>>>> remotes/beniMaster/master
	 */
	public void write(String dir, String filename) throws IOException {
		if (this.values == null) {
			throw new NullPointerException("no values for nodevaluelist \""
					+ super.getName() + "\" set to be written to " + dir);
		}
		Writer w = new Writer(dir, filename);
		for (int i = 0; i < this.values.length; i++) {
			w.writeln(i + Keywords.dataDelimiter + this.values[i]);
		}
		w.close();
	}
<<<<<<< HEAD
	
	/**
	 * 
	 * @param dir String which contains the path to the directory the NodeValueList will be read from.
	 * 
	 * @param filename String representing the filename the NodeValueList will be read from.
	 * 
	 * @param readValues Boolean. True:  values from the file will be read.
	 * 							  False: empty NodeValueList will be created.	
=======

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
>>>>>>> remotes/beniMaster/master
	 */
	public static NodeValueList read(String dir, String filename, String name,
			boolean readValues) throws IOException {
		if (!readValues) {
			return new NodeValueList(name, null);
		}
		Reader r = new Reader(dir, filename);
		ArrayList<Double> list = new ArrayList<Double>();
		String line = null;
		int index = 0;
		while ((line = r.readString()) != null) {
			String[] temp = line.split(Keywords.dataDelimiter);
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
<<<<<<< HEAD
	
	
=======
>>>>>>> remotes/beniMaster/master
