package dna.series.data;

import java.io.IOException;
<<<<<<< HEAD
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
import dna.io.etc.Keywords;

/**
 * DistributionLong is an object which represents an distribution by whole numbers and its denominator.
 * Due to the use of long numbers it provides a way to represent distributions with large numbers.
 * 
 * @author Rwilmes
 * @date 17.06.2013
 */
=======

>>>>>>> Codeupdate 13-06-18
public class DistributionLong extends Distribution {
	
	// class variables
	private long[] values;
<<<<<<< HEAD
	private long denominator;
	
	// constructor
	public DistributionLong(String name, long[] values, long denominator) {
		super(name);
		this.values = values;
		this.denominator = denominator;
	}
	
	// get methods
=======
	
	// constructor
	public DistributionLong(String name, long[] values) {
		super(name);
		this.values = values;
	}
	
	// class methods
>>>>>>> Codeupdate 13-06-18
	public long[] getLongValues() {
		return this.values;
	}
	
<<<<<<< HEAD
	public long getDenominator() {
		return this.denominator;
	}
	
=======
	// IO methods
	public void write(String dir, String filename) throws IOException {
		super.write(dir, filename);
	}
	public static Distribution read(String dir, String filename, String name,
			boolean readValues) throws IOException {
		return Distribution.read(dir, filename, name, readValues);
	}

>>>>>>> Codeupdate 13-06-18
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
<<<<<<< HEAD
	
	// IO Methods
	/**
	 * @param dir String which contains the path / directory the Distribution will be written to.
	 * 
	 * @param filename String representing the desired filename for the Distribution.
	 */
	public void write(String dir, String filename) throws IOException {
		if (this.values == null) {
			throw new NullPointerException("no values for distribution \""
					+ this.getName() + "\" set to be written to " + dir);
		}
		Writer w = new Writer(dir, filename);

		w.writeln(this.denominator);	// write denominator in first line
		
		for (int i = 0; i < this.values.length; i++) {
			w.writeln(i + Keywords.distributionDelimiter + this.values[i]);
		}
		w.close();
	}
	
	/**
	 * @param dir String which contains the path to the directory the Distribution will be read from.
	 * 
	 * @param filename String representing the filename the Distribution will be read from.
	 * 
	 * @param readValues Boolean. True:  values from the file will be read.
	 * 							  False: empty Distribution will be created.	
	 */
	public static DistributionLong read(String dir, String filename, String name,
			boolean readValues) throws IOException {
		if (!readValues) {
			return new DistributionLong(name, null,0);
		}
		Reader r = new Reader(dir, filename);
		ArrayList<Long> list = new ArrayList<Long>();
		String line = null;
		int index = 0;
		
		line = r.readString();
		long denominator = Long.parseLong(line);
		
		while ((line = r.readString()) != null) {
			String[] temp = line.split(Keywords.distributionDelimiter);
			if (Integer.parseInt(temp[0]) != index) {
				throw new InvalidFormatException("expected index " + index
						+ " but found " + temp[0] + " @ \"" + line + "\"");
			}
			list.add(Long.parseLong(temp[1]));
			index++;
		}
		long[] values = new long[list.size()];
		for (int i = 0; i < list.size(); i++) {
			values[i] = list.get(i);
		}
		r.close();
		return new DistributionLong(name, values, denominator);
	}


=======
>>>>>>> Codeupdate 13-06-18
}
