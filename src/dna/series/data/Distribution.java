package dna.series.data;


/**
 * Distribution is the parent class for all distributions with all kind of
 * datastructures.
 */
public abstract class Distribution extends Data {

//	private String name;

	public Distribution(String name) {
		super(name);
	}

//	public String getName() {
//		return this.name;
//	}

	public abstract String toString();
}
