package dna.series.data;

import dna.series.lists.ListItem;


/**
 * Data is the super-class for all provided data-structures.
 * 
 * @author Rwilmes
 * @date 06.06.2013
 */
public class Data implements ListItem {

	// class variables
	private String name;
	
	// constructors
	public Data() {}
	
	public Data(String name) {
		this.name = name;
	}
	
	// get methods
	public String getName() {
		return this.name;
	}
	
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
}
