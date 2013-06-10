package dna.series.data;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
import dna.io.etc.Keywords;
import dna.util.Log;

/**
 * A NodeValueList is an object containing an array with 1 value for each node. The node index is used as 
import dna.series.lists.ListItem;

/**
 * NodeValueList is a class containing an array with 1 value for each node. The node index is used as 
>>>>>>> datatype NodeValueList added
 * the index for the array. If a node is removed from the graph, his former value is replaced by a Double.NaN.
 * When inserting new nodevalues with out-of-bound indeces, the array is expanded accordingly.
 * 
 * @author Rwilmes
 * @date 03.06.2013
 */
public class NodeValueList extends Data {

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
	
	// get methods
	public double[] getValues() {
		return this.values;
	}
	
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
public class NodeValueList implements ListItem {

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
	
	public NodeValueList(String name, double value) {
		Log.warn("NodeValueList initialized with a single value");
		double[] temp = { value };
		this.values = temp;
	}
	
	// get methods
	public String getType() {
		return "NodeValueList";
	}
	
	public String getName() {
		return this.name;
	}
	
	public double[] getValues() {
		return this.values;
	}

	public double getValue(int index) {
		return this.values[index];
	}
	
	// other methods
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
			w.writeln(i + Keywords.dataDelimiter + this.values[i]);
>>>>>>> datatype NodeValueList added
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
>>>>>>> datatype NodeValueList added
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

=======
>>>>>>> datatype NodeValueList added
}
	
	
