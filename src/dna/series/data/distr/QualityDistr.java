package dna.series.data.distr;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
import dna.util.Config;
import dna.util.Log;

public abstract class QualityDistr<T> extends Distr<T, double[]> {

	public QualityDistr(String name, T binSize) {
		super(name, binSize, new double[0]);
	}

	public QualityDistr(String name, T binSize, double[] values) {
		super(name, binSize, values);
	}

	@Override
	protected void print() {
		System.out.println("name: " + this.getName());
		System.out.println("@ binSize: " + this.binSize);
		for (int i = 0; i < this.values.length; i++) {
			System.out.println("    " + i + ": " + this.values[i]);
		}
	}

	@Override
	public void write(String dir, String filename) throws IOException {
		Writer w = Writer.getWriter(dir, filename);
		w.writeln(this.binSize.toString());
		for (int i = 0; i < this.values.length; i++) {
			w.writeln(i + Config.get("DISTRIBUTION_DELIMITER") + this.values[i]);
		}
		w.close();
	}

	/**
	 * reads a quality distribution from the specified file. in case the
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
	public static QualityDistr read(String dir, String filename, String name,
			boolean readValues, Class<? extends QualityDistr> type)
			throws IOException {
		if (!readValues) {
			try {
				return (QualityDistr) type.getConstructor(String.class)
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

		// binSize
		String binSize = r.readString();

		// values
		ArrayList<Double> list = new ArrayList<Double>();
		String line = null;
		int index = 0;
		while ((line = r.readString()) != null) {
			String[] temp = line.split(Config.get("DISTRIBUTION_DELIMITER"));
			if (Integer.parseInt(temp[0]) != index) {
				throw new InvalidFormatException("expected index " + index
						+ " but found " + temp[0] + " @ \"" + line + "\"");
			}
			list.add(Double.parseDouble(temp[1]));
			index++;
		}
		double[] values = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			values[i] = list.get(i);
		}
		r.close();
		try {
			return (QualityDistr) type.getConstructor(String.class,
					String.class, double[].class).newInstance(name, binSize,
					values);
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
		if (obj == null || !(obj instanceof BinnedDistr)) {
			return false;
		}
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}
		BinnedDistr d = (BinnedDistr) obj;
		if (!d.getName().equals(this.getName())) {
			return false;
		}
		if (d.binSize.equals(this.binSize)) {
			return false;
		}
		if (((double[]) d.values).length != this.values.length) {
			return false;
		}
		for (int i = 0; i < this.values.length; i++) {
			if (((double[]) d.values)[i] != this.values[i]) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	public boolean equalsVerbose(QualityDistr d) {
		if (!d.getClass().equals(this.getClass())) {
			Log.warn("distribution type differs: (" + this.getName() + ") "
					+ this.getClass().getSimpleName() + " != "
					+ d.getClass().getSimpleName() + " (" + d.getName() + ")");
			return false;
		}
		if (!d.getName().equals(this.getName())) {
			Log.warn("name differs: " + this.getName() + " != " + d.getName());
			return false;
		}
		if (d.binSize.equals(this.binSize)) {
			Log.warn(this.getName() + " - binSize differs: "
					+ this.getBinSize() + " != " + d.getBinSize());
			return false;
		}
		if (((double[]) d.values).length != this.values.length) {
			Log.warn(this.getName() + " - length of values differs: "
					+ this.values.length + " != "
					+ ((double[]) d.values).length);
			return false;
		}
		for (int i = 0; i < this.values.length; i++) {
			if (((double[]) d.values)[i] != this.values[i]) {
				Log.warn(this.getName() + " - value differs at index " + i
						+ ": " + this.values[i] + " != "
						+ ((double[]) d.values)[i]);
				return false;
			}
		}
		return true;
	}

}
