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
>>>>>>> Codeupdate 13-06-10.
	
	// constructors
	public Data() {}
	
	public Data(String name) {
		this.name = name;
	}
	
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
	// get methods
	public String getName() {
		return this.name;
	}
	
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
}
