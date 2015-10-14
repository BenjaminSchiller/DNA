package dna.series.data.distr;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
import dna.util.Config;
import dna.util.Log;

public abstract class BinnedDistr<T> extends Distr<T> {

	protected T binSize;

	public BinnedDistr(String name, T binSize) {
		super(name);
		this.binSize = binSize;
	}

	public BinnedDistr(String name, T binSize, long denominator, long[] values) {
		super(name, denominator, values);
		this.binSize = binSize;
	}

	public abstract T[] getBin(int index);

	/**
	 * outputs the contents stored in this distribution
	 */
	@Override
	public void print() {
		System.out.println("name: " + this.getName());
		System.out.println("denominator: " + this.denominator);
		System.out.println("@ binSize: " + this.binSize);
		for (int i = 0; i < this.values.length; i++) {
			T[] bin = this.getBin(i);
			System.out.println("    " + i + ": " + this.values[i] + " ("
					+ bin[0] + "," + bin[1] + "]");
		}
	}

	/**
	 * writes the contents of the distribution to the specified file in the
	 * specified directory.
	 * 
	 * @param dir
	 *            directory where to store the output
	 * @param filename
	 *            file where to write the data to
	 * @throws IOException
	 */
	@Override
	public void write(String dir, String filename) throws IOException {
		Writer w = Writer.getWriter(dir, filename);
		w.writeln(this.denominator);
		w.writeln(this.binSize.toString());
		for (int i = 0; i < this.values.length; i++) {
			w.writeln(i + Config.get("DISTRIBUTION_DELIMITER") + this.values[i]);
		}
		w.close();
	}

	/**
	 * reads a binned distribution from the specified file. in case the
	 * readValues flag is false, an empty distribution of the specified type
	 * with the given name is returned. otherwise, denominator and values are
	 * read from the file.
	 * 
	 * @param dir
	 * @param filename
	 * @param name
	 * @param readValues
	 * @param type
	 * @return the read distribution and null in case an error occurrs.
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static BinnedDistr readBinned(String dir, String filename,
			String name, boolean readValues, Class<? extends BinnedDistr> type)
			throws IOException {
		if (!readValues) {
			try {
				return (BinnedDistr) type.getConstructor(String.class)
						.newInstance(name);
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				System.err.println("could not read distribution of type '"
						+ type.getSimpleName() + "' from " + dir + filename);
				e.printStackTrace();
				return null;
			}
		}
		Reader r = Reader.getReader(dir, filename);

		// denominator
		long denominator = r.readLong();
		String binSize = r.readString();

		// values
		ArrayList<Integer> list = new ArrayList<Integer>();
		String line = null;
		int index = 0;
		while ((line = r.readString()) != null) {
			String[] temp = line.split(Config.get("DISTRIBUTION_DELIMITER"));
			if (Integer.parseInt(temp[0]) != index) {
				throw new InvalidFormatException("expected index " + index
						+ " but found " + temp[0] + " @ \"" + line + "\"");
			}
			list.add(Integer.parseInt(temp[1]));
			index++;
		}
		long[] values = new long[list.size()];
		for (int i = 0; i < list.size(); i++) {
			values[i] = list.get(i);
		}
		r.close();
		try {
			return (BinnedDistr) type.getConstructor(String.class,
					String.class, long.class, long[].class).newInstance(name,
					binSize, denominator, values);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			System.err.println("could not read distribution of type '"
					+ type.getSimpleName() + "' from " + dir + filename);
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof BinnedDistr
				&& ((BinnedDistr) obj).binSize.equals(this.binSize)
				&& super.equals(obj);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equalsVerbose(Distr d) {
		if (!super.equalsVerbose(d)) {
			return false;
		}
		if (!this.binSize.equals(((BinnedDistr) d).binSize)) {
			Log.warn(this.getName() + " - binSize differs: " + this.binSize
					+ " != " + ((BinnedDistr) d).binSize);
			return false;
		}
		return true;
	}

}
