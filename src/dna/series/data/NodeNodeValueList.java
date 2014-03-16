package dna.series.data;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
import dna.util.ArrayUtils;
import dna.util.Config;
import dna.util.Log;

/**
 * A NodeNodeValueList is an object that represents a n*n matrix for n nodes.
 * Each node n is identified by its index and possesses n double values, one for
 * each node. If a node is removed from the graph, his former values are
 * replaced by Double.NaN's. When inserting new nodevalues with out-of-bound
 * indices, the array's are expanded accordingly. Note: The field of doubles
 * used to store the data will always have the same amount of rows and columns.
 * The use of truncate allows to free unused index-space.
 * 
 * @author Rwilmes
 * @date 02.02.2014
 */
public class NodeNodeValueList extends Data {

	// variables
	private double[][] values;
	public static final double emptyValue = Double.NaN;

	// constructors
	public NodeNodeValueList(String name, int size) {
		this(name, new double[size][size]);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				this.values[i][j] = NodeNodeValueList.emptyValue;
			}
		}
	}

	public NodeNodeValueList(String name, double[][] values) {
		super(name);
		this.values = values;
	}

	/*
	 * GET METHODS
	 */

	/** Returns the value field of this object. **/
	public double[][] getValues() {
		return this.values;
	}

	/** Returns the node values of the node with the given index. **/
	public double[] getValuesByIndex(int index) {
		try {
			return this.values[index];
		} catch (IndexOutOfBoundsException e) {
			Log.error("NodeNodeValueList IndexOutOfBoundsException");
		}
		return new double[0];
	}

	/** Gets the nodes (index1) double value with index2. **/
	public double getValue(int index1, int index2) {
		try {
			return this.values[index1][index2];
		} catch (IndexOutOfBoundsException e) {
			Log.error("NodeNodeValueList IndexOutOfBoundsException");
		}
		return 0;
	}

	/*
	 * SET METHODS
	 */

	/**
	 * Sets the value field of this object.
	 * 
	 * Note: The input array is not checked and should therefore be of quadratic
	 * size.
	 */
	public void setValues(double[][] values) {
		this.values = values;
	}

	/**
	 * Sets the node values of the node with the given index.
	 * 
	 * Example: setValuesByIndex(3, {0.1, 0.2, 0.3}) -> Sets values(3,0) = 0.1,
	 * values(3,1) = 0.2, values(3,2) = 0.3.
	 * 
	 * Note: If the array is to short it will be expanded and filled up with the
	 * default value. If the index is out of bounds or the array to long, the
	 * values field will be expanded accordingly.
	 * 
	 */
	public void setValuesByIndex(int index, double[] values) {
		this.values = ArrayUtils.set(this.values, index, values,
				NodeNodeValueList.emptyValue);
	}

	/**
	 * Sets for each node the given input double as node value for the node with
	 * the input index.
	 * 
	 * Example: setValueByColumnIndex(5, 10.0) -> Sets values(n,5) = 10.0, for
	 * all nodes n.
	 * 
	 * Note: If the index is out of bounds, the values field will be expanded
	 * accordingly.
	 */
	public void setValueByColumnIndex(int index, double value) {
		if (this.values.length <= index) {
			double[][] valuesNew = new double[index + 1][];
			for (int i = 0; i < this.values.length; i++) {
				valuesNew[i] = this.values[i];
			}
			for (int i = values.length; i < valuesNew.length; i++) {
				double[] valuesTemp = new double[index + 1];
				for (int j = 0; j < valuesTemp.length; j++) {
					valuesTemp[j] = NodeNodeValueList.emptyValue;
				}
				valuesNew[i] = valuesTemp;
			}
			this.values = valuesNew;
		}
		for (int i = 0; i < this.values.length; i++) {
			this.values[i] = ArrayUtils.set(this.values[i], index, value,
					NodeNodeValueList.emptyValue);
		}
	}

	/**
	 * Sets for the node with the given index the input double as a value for
	 * all other nodes.
	 * 
	 * Example: setValueByColumnIndex(5, 10.0) -> Sets values(5,n) = 10.0, for
	 * all nodes n.
	 * 
	 * Note: If the index is out of bounds, the values field will be expanded
	 * accordingly.
	 */
	public void setValueByRowIndex(int index, double value) {
		if (this.values.length <= index) {
			double[][] valuesNew = new double[index + 1][];
			for (int i = 0; i < this.values.length; i++) {
				valuesNew[i] = this.values[i];
			}
			for (int i = values.length; i < valuesNew.length; i++) {
				double[] valuesTemp = new double[index + 1];
				for (int j = 0; j < valuesTemp.length; j++) {
					valuesTemp[j] = NodeNodeValueList.emptyValue;
				}
				valuesNew[i] = valuesTemp;
			}
			this.values = valuesNew;
		}
		for (int i = 0; i < this.values.length; i++) {
			this.values[i] = ArrayUtils.set(this.values[i], index,
					NodeNodeValueList.emptyValue, NodeNodeValueList.emptyValue);
			this.values[index][i] = value;
		}
	}

	/**
	 * Sets the nodes (index1) double value with index2.
	 * 
	 * Example: setValue(2, 3, 0.9) -> Sets values(2,3) = 0.9.
	 * 
	 * Note: If one or both indices is out of bounds, the values field will be
	 * expanded accordingly.
	 */
	public void setValue(int index1, int index2, double value) {
		int max = index1;
		if (index1 < index2)
			max = index2;

		if (this.values.length <= max) {
			// index1 or index2 out of bounds -> expand field
			double[][] valuesNew = new double[max + 1][];

			for (int i = 0; i < this.values.length; i++) {
				valuesNew[i] = ArrayUtils.set(this.values[i], max,
						NodeNodeValueList.emptyValue,
						NodeNodeValueList.emptyValue);
			}
			for (int i = this.values.length; i < max + 1; i++) {
				double[] tempDoubleArray = new double[max + 1];
				for (int j = 0; j < tempDoubleArray.length; j++) {
					tempDoubleArray[j] = NodeNodeValueList.emptyValue;
				}
				valuesNew[i] = tempDoubleArray;
			}

			valuesNew[index1] = ArrayUtils.set(valuesNew[index1], index2,
					value, NodeNodeValueList.emptyValue);
			this.values = valuesNew;
		} else {
			try {
				this.values[index1] = ArrayUtils.set(this.values[index1],
						index2, value, NodeNodeValueList.emptyValue);
			} catch (IndexOutOfBoundsException e) {
				Log.error("NodeNodeValueList IndexOutOfBoundsException e");
			}
		}

	}

	/*
	 * CLASS METHODS
	 */

	public String toString() {
		return "nodenodevaluelist(" + super.getName() + ")";
	}

	/**
	 * Truncates the value field regarding missing nodes. The last i rows and
	 * columns, which are filled with Double.NaN's only, will be disbanded.
	 * 
	 * Note: Resulting values field will remain quadratic.
	 */
	public void truncate() {
		// get new length of field (amount of nodes)
		int sizeNew = 0;
		for (int i = this.values.length - 1; i >= 0; i--) {
			boolean allNan = true;
			for (int j = 0; j < this.values[i].length; j++) {
				if (!Double.isNaN(this.values[i][j]))
					allNan = false;
			}

			if (!allNan) {
				sizeNew = i + 1;
				break;
			}
		}

		for (int i = this.values.length - 1; i >= 0; i--) {
			boolean allNan = true;
			for (int j = 0; j < this.values.length; j++) {
				if (!Double.isNaN(this.values[j][i]))
					allNan = false;
			}

			if (!allNan) {
				if ((i + 1) > sizeNew)
					sizeNew = i + 1;
				break;
			}
		}

		// only truncate if necessary
		if (sizeNew < this.values.length) {
			// create values field with new size
			double[][] tempValues = new double[sizeNew][];

			// fill new field
			for (int i = 0; i < sizeNew; i++) {
				double[] tempNodeValues = new double[sizeNew];
				for (int j = 0; j < tempNodeValues.length; j++) {
					try {
						tempNodeValues[j] = this.values[i][j];
					} catch (ArrayIndexOutOfBoundsException e) {
						tempNodeValues[j] = NodeNodeValueList.emptyValue;
					}
				}

				tempValues[i] = tempNodeValues;
			}

			// set new field
			this.values = tempValues;
		}
	}

	/** Prints all values **/
	public void printValues() {
		System.out.println("Printing values of '" + this.getName()
				+ "' with Dimension: " + this.values.length + "x"
				+ this.values.length);
		String columns = "\t";
		for (int i = 0; i < this.values.length; i++) {
			columns += i + ":\t";
		}
		System.out.println(columns);
		for (int i = 0; i < this.values.length; i++) {
			double[] t = this.values[i];
			String output = "" + i + ":\t";
			for (int j = 0; j < t.length; j++) {
				output += t[j] + "\t";
			}
			System.out.println(output);
		}
	}

	/*
	 * IO METHODS
	 */

	/**
	 * 
	 * @param dir
	 *            String which contains the path / directory the
	 *            NodeNodeValueList will be written to.
	 * 
	 * @param filename
	 *            String representing the desired filename for the
	 *            NodeNodeValueList.
	 */
	public void write(String dir, String filename) throws IOException {
		Log.debug("WRITING NodeNodeValueList '" + filename + "' to " + dir);
		if (this.values == null) {
			throw new NullPointerException("no values for nodenodevaluelist \""
					+ super.getName() + "\" set to be written to " + dir);
		}

		Writer w = Writer.getWriter(dir, filename);

		for (int i = 0; i < this.values.length; i++) {
			String line = i + Config.get("DATA_DELIMITER");
			for (int j = 0; j < this.values.length; j++) {
				line += this.values[i][j] + Config.get("DATA_DELIMITER");
			}
			w.writeln(line);
		}
		w.close();
	}

	/**
	 * 
	 * @param dir
	 *            String which contains the path to the directory the
	 *            NodeNodeValueList will be read from.
	 * 
	 * @param filename
	 *            String representing the filename the NodeNodeValueList will be
	 *            read from.
	 * 
	 * @param readValues
	 *            Boolean. True: values from the file will be read. False: empty
	 *            NodeValueList will be created.
	 */
	public static NodeNodeValueList read(String dir, String filename,
			String name, boolean readValues) throws IOException {
		if (!readValues) {
			return new NodeNodeValueList(name, null);
		}

		Reader r = new Reader(dir, filename);
		ArrayList<double[]> list = new ArrayList<double[]>();
		String line = null;
		int index = 0;
		while ((line = r.readString()) != null) {
			String[] temp = line.split(Config.get("DATA_DELIMITER"));
			if (Integer.parseInt(temp[0]) != index) {
				throw new InvalidFormatException("expected index " + index
						+ " but found " + temp[0] + " @ \"" + line + "\"");
			}
			double[] tempValues = new double[temp.length - 1];
			for (int i = 1; i < temp.length; i++) {
				tempValues[i - 1] = Double.parseDouble(temp[i]);
			}
			list.add(tempValues);
			index++;
		}
		double[][] values = new double[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			values[i] = list.get(i);
		}
		r.close();
		return new NodeNodeValueList(name, values);
	}
}
