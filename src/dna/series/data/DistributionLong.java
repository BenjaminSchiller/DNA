package dna.series.data;

import java.io.IOException;

public class DistributionLong extends Distribution {
	
	// class variables
	private long[] values;
	
	// constructor
	public DistributionLong(String name, long[] values) {
		super(name);
		this.values = values;
	}
	
	// class methods
	public long[] getLongValues() {
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

	public long getMin() {
		int y = 0;
		while(values[y] < 0) {
			y++;
			
		}
		return (long) y;
	}
	
	public long getMax() {
		return (long) values.length-1;
	}
}
