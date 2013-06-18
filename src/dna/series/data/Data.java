package dna.series.data;

import dna.series.lists.ListItem;


/**
 * Data is the super-class for all provided data-structures.
 * 
 * @author Rwilmes
 * @date 06.06.2013
 */
public class Data implements ListItem {

<<<<<<< HEAD
	// member variables
	private String name;
=======
	// class variables
	private String name;
<<<<<<< HEAD
	private double value;
	private double[] values;
>>>>>>> Codeupdate 13-06-10.
=======
>>>>>>> Codeupdate 13-06-18
	
	// constructors
	public Data() {}
	
	public Data(String name) {
		this.name = name;
	}
	
<<<<<<< HEAD
<<<<<<< HEAD
=======
	public Data(String name, double value) {
		this.name = name;
		this.value = value;
	}
	
	public Data(String name, double[] values) {
		this.name = name;
		this.values = values;
	}
	
>>>>>>> Codeupdate 13-06-10.
=======
>>>>>>> Codeupdate 13-06-18
	// get methods
	public String getName() {
		return this.name;
	}
	
<<<<<<< HEAD
<<<<<<< HEAD
	public static boolean equals(Object o1, Object o2) {
	    return o1.getClass() == o2.getClass();
	}

=======
	public String getType() {
		return "Data";
	}
	
	public double getValue() {
		return this.value;
	}
	
	public double[] getValues() {
		return this.values;
	}
>>>>>>> Codeupdate 13-06-10.
=======
	// IO methods
	/**
	 * 
	 * @param dir String which contains the path / directory the Data will be written to.
	 * 
	 * @param filename String representing the desired filename for the Data.
	 */
	/*
	public void write(String dir, String filename) throws IOException {
		if (this.values == null) {
			throw new NullPointerException("no values for data \""
					+ this.name + "\" set to be written to " + dir);
		}
		Writer w = new Writer(dir, filename);
		for (int i = 0; i < this.values.length; i++) {
			w.writeln(i + Keywords.dataDelimiter + this.values[i]);
		}
		w.close();
	}*/
>>>>>>> Codeupdate 13-06-18
}
