package dna.series.data;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
import dna.io.etc.Keywords;

/**
<<<<<<< HEAD
 * DistributionInt is an object which represents an distribution by whole numbers and its denominator.
 * Integer data-structures are used. For larger numbers see DistributionLong.
=======
 * DistributionInt is an object which represents an distribution by whole
 * numbers and its denominator. Integer data-structures are used. For larger
 * numbers see DistributionLong.
>>>>>>> remotes/beniMaster/master
 * 
 * @author Rwilmes
 * @date 17.06.2013
 */
public class DistributionInt extends Distribution {

	// class variables
	private int[] values;
	private int denominator;
<<<<<<< HEAD
	
=======

>>>>>>> remotes/beniMaster/master
	// constructors
	public DistributionInt(String name, int[] values, int denominator) {
		super(name);
		this.values = values;
		this.denominator = denominator;
	}
<<<<<<< HEAD
	
=======

>>>>>>> remotes/beniMaster/master
	// get methods
	public int[] getIntValues() {
		return this.values;
	}
<<<<<<< HEAD
	
=======

>>>>>>> remotes/beniMaster/master
	public int getDenominator() {
		return this.denominator;
	}

	public int getMin() {
		int y = 0;
<<<<<<< HEAD
		while(values[y] < 0) {
			y++;
			
		}
		return y;
	}
	
	public int getMax() {
		return values.length-1;
	}
	
	// IO Methods
	/**
	 * @param dir String which contains the path / directory the Distribution will be written to.
	 * 
	 * @param filename String representing the desired filename for the Distribution.
=======
		while (values[y] < 0) {
			y++;

		}
		return y;
	}

	public int getMax() {
		return values.length - 1;
	}

	// IO Methods
	/**
	 * @param dir
	 *            String which contains the path / directory the Distribution
	 *            will be written to.
	 * 
	 * @param filename
	 *            String representing the desired filename for the Distribution.
>>>>>>> remotes/beniMaster/master
	 */
	public void write(String dir, String filename) throws IOException {
		if (this.values == null) {
			throw new NullPointerException("no values for distribution \""
					+ this.getName() + "\" set to be written to " + dir);
		}
		Writer w = new Writer(dir, filename);

<<<<<<< HEAD
		w.writeln(this.denominator);	// write denominator in first line
		
=======
		w.writeln(this.denominator); // write denominator in first line

>>>>>>> remotes/beniMaster/master
		for (int i = 0; i < this.values.length; i++) {
			w.writeln(i + Keywords.distributionDelimiter + this.values[i]);
		}
		w.close();
	}

	/**
<<<<<<< HEAD
	 * @param dir String which contains the path to the directory the Distribution will be read from.
	 * 
	 * @param filename String representing the filename the Distribution will be read from.
	 * 
	 * @param readValues Boolean. True:  values from the file will be read.
	 * 							  False: empty Distribution will be created.	
	 */
	public static DistributionInt read(String dir, String filename, String name,
			boolean readValues) throws IOException {
		if (!readValues) {
			return new DistributionInt(name, null,0);
=======
	 * @param dir
	 *            String which contains the path to the directory the
	 *            Distribution will be read from.
	 * 
	 * @param filename
	 *            String representing the filename the Distribution will be read
	 *            from.
	 * 
	 * @param readValues
	 *            Boolean. True: values from the file will be read. False: empty
	 *            Distribution will be created.
	 */
	public static DistributionInt read(String dir, String filename,
			String name, boolean readValues) throws IOException {
		if (!readValues) {
			return new DistributionInt(name, null, 0);
>>>>>>> remotes/beniMaster/master
		}
		Reader r = new Reader(dir, filename);
		ArrayList<Integer> list = new ArrayList<Integer>();
		String line = null;
		int index = 0;
<<<<<<< HEAD
		
		line = r.readString();
		int denominator = Integer.parseInt(line);
		
=======

		line = r.readString();
		int denominator = Integer.parseInt(line);

>>>>>>> remotes/beniMaster/master
		while ((line = r.readString()) != null) {
			String[] temp = line.split(Keywords.distributionDelimiter);
			if (Integer.parseInt(temp[0]) != index) {
				throw new InvalidFormatException("expected index " + index
						+ " but found " + temp[0] + " @ \"" + line + "\"");
			}
			list.add(Integer.parseInt(temp[1]));
			index++;
		}
		int[] values = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			values[i] = list.get(i);
		}
		r.close();
		return new DistributionInt(name, values, denominator);
	}
<<<<<<< HEAD
	

	
=======

>>>>>>> remotes/beniMaster/master
}
