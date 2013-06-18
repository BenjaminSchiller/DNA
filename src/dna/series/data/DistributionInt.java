package dna.series.data;

import java.io.IOException;

public class DistributionInt extends Distribution {

	// class variables
	private int[] values;
	
	// constructors
	public DistributionInt(String name, int[] values) {
		super(name);
		this.values = values;
	}
	
	// class methods
	public int[] getIntValues() {
		return this.values;
	}

	// IO methods
	public void write(String dir, String filename) throws IOException {
		super.write(dir, filename);
	}

	public static Distribution read(String dir, String filename, String name,
			boolean readValues) throws IOException {
		return Distribution.read(dir, filename, name, readValues);
	}
	
	public int getMin() {
		int y = 0;
		while(values[y] < 0) {
			y++;
			
		}
		return y;
	}
	
	public int getMax() {
		return values.length-1;
	}
	
}
