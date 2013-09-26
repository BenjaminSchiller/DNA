package dna.series.data;

import dna.series.lists.ListItem;

<<<<<<< HEAD

=======
>>>>>>> remotes/beniMaster/master
/**
 * Data is the super-class for all provided data-structures.
 * 
 * @author Rwilmes
 * @date 06.06.2013
 */
public class Data implements ListItem {

	// member variables
	private String name;
<<<<<<< HEAD
	
	// constructors
	public Data() {}
	
	public Data(String name) {
		this.name = name;
	}
	
=======

	// constructors
	public Data() {
	}

	public Data(String name) {
		this.name = name;
	}

>>>>>>> remotes/beniMaster/master
	// get methods
	public String getName() {
		return this.name;
	}
<<<<<<< HEAD
	
	public static boolean equals(Object o1, Object o2) {
	    return o1.getClass() == o2.getClass();
=======

	public static boolean equals(Object o1, Object o2) {
		return o1.getClass() == o2.getClass();
>>>>>>> remotes/beniMaster/master
	}

}
