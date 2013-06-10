package dna.series.data;

import dna.util.Log;

public class Value extends Data {

	// class variables
	private double value;
	
	// constructors
	public Value(String name, double value) {
		super(name);
		this.value = value;
	}
	
	public Value(String name, double[] values) {
		Log.warn("Value object initialized with to much arguments");
<<<<<<< HEAD
=======
	}

	public String toString() {
		return "value(" + this.name + ") = " + this.value;
>>>>>>> Codeupdate 13-06-10.
	}

	// class methods
	public String toString() {
		return "value(" + super.getName() + ") = " + this.value;
	}

	public double getValue() {
		return this.value;
	}
	
<<<<<<< HEAD
	/*public static Value read(String dir, String filename) {
		Reader r = new Reader(dir, filename);

		String line = null;
		
		line = r.readString();
		String[] temp = line.split(Keywords.aggregatedDataDelimiter);

		double[] tempDouble = new double[temp.length];
		for(int i = 0; i < tempDouble.length; i++) {
			tempDouble[i] = Double.parseDouble(temp[i]);
		}

		r.close();
		return new AggregatedValue(name, tempDouble);
	}*/
=======
	public String getType() {
		return "Value";
	}
>>>>>>> Codeupdate 13-06-10.

}
