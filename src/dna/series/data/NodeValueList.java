package dna.series.data;

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> Codeupdate 13-06-24
=======
>>>>>>> datatype NodeValueList added
import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
import dna.util.ArrayUtils;
import dna.util.Config;
import dna.util.Log;

/**
 * A NodeValueList is an object containing an array with 1 value for each node.
 * The node index is used as the index for the array. If a node is removed from
 * the graph, his former value is replaced by a Double.NaN. When inserting new
 * nodevalues with out-of-bound indeces, the array is expanded accordingly.
=======
import dna.io.etc.Keywords;
=======
>>>>>>> Codeupdate 13-06-18
=======
import dna.io.etc.Keywords;
>>>>>>> Codeupdate 13-06-24
import dna.util.Log;

/**
 * A NodeValueList is an object containing an array with 1 value for each node. The node index is used as 
 * the index for the array. If a node is removed from the graph, his former value is replaced by a Double.NaN.
 * When inserting new nodevalues with out-of-bound indeces, the array is expanded accordingly.
>>>>>>> datatype NodeValueList added
=======
import dna.io.etc.Keywords;
import dna.util.Log;

/**
 * NodeValueList is a class containing an array with 1 value for each node. The node index is used as 
 * the index for the array. If a node is removed from the graph, his former value is replaced by a Double.NaN.
 * When inserting new nodevalues with out-of-bound indeces, the array is expanded accordingly.
>>>>>>> datatype NodeValueList added
 * 
 * @author Rwilmes
 * @date 03.06.2013
 */
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
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

	// get methods
=======
public class NodeValueList implements ListItem {
=======
public class NodeValueList extends Data {
>>>>>>> Codeupdate 13-06-10.

	// member variables
	private double[] values;
	
	// constructors
	public NodeValueList(String name, int size) {
		super(name);
		values = new double[size];
	}
	
	public NodeValueList(String name, double[] values){
		super(name);
		this.values = values;
	}
	
<<<<<<< HEAD
<<<<<<< HEAD
	public NodeValueList(String name, double value) {
		Log.warn("NodeValueList initialized with a single value");
		double[] temp = { value };
		this.values = temp;
	}
	
	// get methods
	public String getType() {
		return "NodeValueList";
	}
	
=======
public class NodeValueList implements ListItem {
=======
public class NodeValueList extends Data {
>>>>>>> Codeupdate 13-06-10.

	// class variables
	private double[] values;
	private String name;
	
	// constructors
	public NodeValueList(String name, int size) {
		this.name = name;
		this.values = new double[size];
	}
	
	public NodeValueList(String name, double[] values){
		this.name = name;
		this.values = values;
	}
	
<<<<<<< HEAD
	// class methods
>>>>>>> datatype NodeValueList added
=======
	public NodeValueList(String name, double value) {
		Log.warn("NodeValueList initialized with a single value");
		double[] temp = { value };
		this.values = temp;
	}
	
	// get methods
	public String getType() {
		return "NodeValueList";
	}
	
>>>>>>> Codeupdate 13-06-10.
	public String getName() {
		return this.name;
	}
	
<<<<<<< HEAD
>>>>>>> datatype NodeValueList added
=======
>>>>>>> datatype NodeValueList added
	public double[] getValues() {
		return this.values;
	}

<<<<<<< HEAD
<<<<<<< HEAD
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
			Log.error("AggregatedNodeValueList IndexOutOfBoundsException");
		}
		return 0;
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
		Writer w = new Writer(dir, filename);
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
=======
=======
>>>>>>> datatype NodeValueList added
	public double getValue(int index) {
		return this.values[index];
	}
	
<<<<<<< HEAD
<<<<<<< HEAD
	// other methods
=======
>>>>>>> datatype NodeValueList added
=======
	// other methods
>>>>>>> Codeupdate 13-06-10.
	public void set(int index, double value) {
		try {
			values[index] = value;
		} catch (ArrayIndexOutOfBoundsException e) {
			double[] valuesNew = new double[index + 1];
			System.arraycopy(values, 0, valuesNew, 0, values.length);
			valuesNew[index] = value;
			this.values = valuesNew;
		}
	}
	
	public void remove(int index){
		this.values[index] = Double.NaN;
	}
	
	public void setValues(double[] values){
		this.values = values;
	}
<<<<<<< HEAD
=======
	// class methods
=======
	// get methods
>>>>>>> Codeupdate 13-06-24
	public double[] getValues() {
		return this.values;
	}
>>>>>>> Codeupdate 13-06-18
	
	public double getValue(int index) {
		try{
			return this.values[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			Log.error("AggregatedNodeValueList IndexOutOfBoundsException");
		}
		return 0;
	}
	
	// class methods
	public String toString() {
		return "nodevaluelist(" + super.getName() + ")";
	}
=======
	
	public boolean exists(int index) {
		try{
			if(this.values[index] != Double.NaN)
				return true;
			else
				return false;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}
	
	
>>>>>>> datatype NodeValueList added
	// IO methods
	/**
	 * 
	 * @param dir String which contains the path / directory the NodeValueList will be written to.
	 * 
	 * @param filename String representing the desired filename for the NodeValueList.
	 */
	public void write(String dir, String filename) throws IOException {
		if (this.values == null) {
			throw new NullPointerException("no values for nodevaluelist \""
<<<<<<< HEAD
					+ super.getName() + "\" set to be written to " + dir);
		}
		Writer w = new Writer(dir, filename);
		for (int i = 0; i < this.values.length; i++) {
			w.writeln(i + Keywords.dataDelimiter + this.values[i]);
=======
					+ this.name + "\" set to be written to " + dir);
		}
		Writer w = new Writer(dir, filename);
		for (int i = 0; i < this.values.length; i++) {
<<<<<<< HEAD
			w.writeln(i + Keywords.distributionDelimiter + this.values[i]);
>>>>>>> datatype NodeValueList added
=======
			w.writeln(i + Keywords.dataDelimiter + this.values[i]);
>>>>>>> Codeupdate 13-06-10.
		}
		w.close();
	}
	
	/**
	 * 
	 * @param dir String which contains the path to the directory the NodeValueList will be read from.
	 * 
	 * @param filename String representing the filename the NodeValueList will be read from.
	 * 
	 * @param readValues Boolean. True:  values from the file will be read.
	 * 							  False: empty NodeValueList will be created.	
<<<<<<< HEAD
>>>>>>> datatype NodeValueList added
=======
>>>>>>> datatype NodeValueList added
	 */
	public static NodeValueList read(String dir, String filename, String name,
			boolean readValues) throws IOException {
		if (!readValues) {
			return new NodeValueList(name, null);
		}
<<<<<<< HEAD
<<<<<<< HEAD

=======
>>>>>>> datatype NodeValueList added
=======
>>>>>>> datatype NodeValueList added
		Reader r = new Reader(dir, filename);
		ArrayList<Double> list = new ArrayList<Double>();
		String line = null;
		int index = 0;
		while ((line = r.readString()) != null) {
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
			String[] temp = line.split(Config.get("DATA_DELIMITER"));
=======
			String[] temp = line.split(Keywords.distributionDelimiter);
>>>>>>> datatype NodeValueList added
=======
			String[] temp = line.split(Keywords.dataDelimiter);
>>>>>>> Codeupdate 13-06-10.
=======
			String[] temp = line.split(Keywords.distributionDelimiter);
>>>>>>> datatype NodeValueList added
=======
			String[] temp = line.split(Keywords.dataDelimiter);
>>>>>>> Codeupdate 13-06-10.
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
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD

}
=======
=======
	*/
>>>>>>> Codeupdate 13-06-18
=======

>>>>>>> Codeupdate 13-06-24
}
	
	
>>>>>>> datatype NodeValueList added
=======
}
	
	
>>>>>>> datatype NodeValueList added
