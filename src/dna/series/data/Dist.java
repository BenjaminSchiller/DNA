package dna.series.data;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
import dna.util.Config;

/**
 * Distribution is a class for representing a distribution. Values are stored in
 * an array of generic type T. Note: Some methods may not be available for
 * different types than Double, Int and Long.
 * 
 * @date 24.06.2013
 */
public class Dist<T> {

	private String name;
	private T[] values;

	public Dist(String name, T[] values) {
		this.name = name;
		this.values = values;
	}

	public T[] getValues() {
		return this.values;
	}

	public String getName() {
		return this.name;
	}

	public void write(String dir, String filename) throws IOException {
		if (this.values == null) {
			throw new NullPointerException("no values for distribution \""
					+ this.getName() + "\" set to be written to " + dir);
		}
		Writer w = Writer.getWriter(dir, filename);

		for (int i = 0; i < this.values.length; i++) {
			w.writeln(i + Config.get("DISTRIBUTION_DELIMITER") + this.values[i]);
		}
		w.close();
	}

	private Dist<T> read(String dir, String filename, String name,
			boolean readValues) throws NumberFormatException,
			InvalidFormatException, IOException {
		if (!readValues) {
			this.values = null;
			return this;
		}
		Reader r = Reader.getReader(dir, filename);
		if (this.values instanceof Integer[]) {
			System.out.println("debug");
			ArrayList<Integer> list = new ArrayList<Integer>();
			String line = null;
			int index = 0;
			while ((line = r.readString()) != null) {
				String[] temp = line
						.split(Config.get("DISTRIBUTION_DELIMITER"));
				if (Integer.parseInt(temp[0]) != index) {
					throw new InvalidFormatException("expected index " + index
							+ " but found " + temp[0] + " @ \"" + line + "\"");
				}

				list.add(Integer.parseInt(temp[1]));
				index++;
			}
			Integer[] values = new Integer[list.size()];
			for (int i = 0; i < list.size(); i++) {
				values[i] = list.get(i);
			}
			this.values = (T[]) values;
		}
		if (this.values instanceof Long[]) {
			ArrayList<Long> list = new ArrayList<Long>();
			String line = null;
			int index = 0;
			while ((line = r.readString()) != null) {
				String[] temp = line
						.split(Config.get("DISTRIBUTION_DELIMITER"));
				if (Integer.parseInt(temp[0]) != index) {
					throw new InvalidFormatException("expected index " + index
							+ " but found " + temp[0] + " @ \"" + line + "\"");
				}

				list.add(Long.parseLong(temp[1]));
				index++;
			}
			Long[] values = new Long[list.size()];
			for (int i = 0; i < list.size(); i++) {
				values[i] = list.get(i);
			}
			this.values = (T[]) values;
		}

		if (this.values instanceof Double[]) {
			ArrayList<Double> list = new ArrayList<Double>();
			String line = null;
			int index = 0;
			while ((line = r.readString()) != null) {
				String[] temp = line
						.split(Config.get("DISTRIBUTION_DELIMITER"));
				if (Integer.parseInt(temp[0]) != index) {
					throw new InvalidFormatException("expected index " + index
							+ " but found " + temp[0] + " @ \"" + line + "\"");
				}

				list.add(Double.parseDouble(temp[1]));
				index++;
			}
			Double[] values = new Double[list.size()];
			for (int i = 0; i < list.size(); i++) {
				values[i] = list.get(i);
			}
			this.values = (T[]) values;
		}
		r.close();

		return this;
	}

	public static Dist<Double> readAsDouble(String dir, String filename,
			String name, boolean readValues) throws IOException {
		Dist<Double> x = new Dist<Double>(name, new Double[0]);
		return x.read(dir, filename, name, readValues);
	}

	public static Dist<Integer> readAsInt(String dir, String filename,
			String name, boolean readValues) throws IOException {
		Dist<Integer> x = new Dist<Integer>(name, new Integer[0]);
		return x.read(dir, filename, name, readValues);
	}

	public static Dist<Long> readAsLong(String dir, String filename,
			String name, boolean readValues) throws IOException {
		Dist<Long> x = new Dist<Long>(name, new Long[0]);
		return x.read(dir, filename, name, readValues);
	}
}
