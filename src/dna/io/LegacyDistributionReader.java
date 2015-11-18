package dna.io;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.filesystem.Files;
import dna.series.data.distr.BinnedDoubleDistr;
import dna.series.data.distr.BinnedIntDistr;
import dna.series.data.distr.Distr;
import dna.series.data.distr.QualityIntDistr;
import dna.util.Config;
import dna.util.Log;

/**
 * Reads an old (legacy) distribution, converts it into a new distribution-type
 * and returns it.
 * 
 * @author Rwilmes
 * @date 18.11.2015
 */
public class LegacyDistributionReader {

	public static enum LegacyDistributionType {
		DIST, DOUBLE, INT, LONG, BINNED_DOUBLE, BINNED_INT, BINNED_LONG
	};

	/** Reads a legacy distribution from file. **/
	public static Distr<?, ?> read(String dir, String filename,
			boolean readValues) throws IOException {
		LegacyDistributionType type = LegacyDistributionReader
				.getLegacyDistributionTypeFromFilename(filename);
		return read(dir, filename,
				Files.getDistributionNameFromFilename(filename, type),
				readValues);
	}

	/** Reads a legacy distribution from file. **/
	public static Distr<?, ?> read(String dir, String filename, String name,
			boolean readValues) throws IOException {
		LegacyDistributionType type = getLegacyDistributionTypeFromFilename(filename);
		switch (type) {
		case DIST:
			return readDistributionDouble(dir, filename, name, readValues);
		case DOUBLE:
			return readDistributionDouble(dir, filename, name, readValues);
		case INT:
			return readDistributionInt(dir, filename, name, readValues);
		case LONG:
			return readDistributionLong(dir, filename, name, readValues);
		case BINNED_DOUBLE:
			return readBinnedDistributionDouble(dir, filename, name, readValues);
		case BINNED_INT:
			return readBinnedDistributionInt(dir, filename, name, readValues);
		case BINNED_LONG:
			return readBinnedDistributionLong(dir, filename, name, readValues);
		}
		return null;
	}

	/** Returns the LegacyDistributionType represented by the filenames suffix. **/
	public static LegacyDistributionType getLegacyDistributionTypeFromFilename(
			String filename) {
		if (filename.endsWith(Config.get("LEGACY_SUFFIX_DIST")))
			return LegacyDistributionType.DIST;
		if (filename.endsWith(Config.get("LEGACY_SUFFIX_DIST_INT")))
			return LegacyDistributionType.INT;
		if (filename.endsWith(Config.get("LEGACY_SUFFIX_DIST_LONG")))
			return LegacyDistributionType.LONG;
		if (filename.endsWith(Config.get("LEGACY_SUFFIX_DIST_DOUBLE")))
			return LegacyDistributionType.DOUBLE;
		if (filename.endsWith(Config.get("LEGACY_SUFFIX_DIST_BINNED_INT")))
			return LegacyDistributionType.BINNED_INT;
		if (filename.endsWith(Config.get("LEGACY_SUFFIX_DIST_BINNED_LONG")))
			return LegacyDistributionType.BINNED_LONG;
		if (filename.endsWith(Config.get("LEGACY_SUFFIX_DIST_BINNED_DOUBLE")))
			return LegacyDistributionType.BINNED_DOUBLE;
		return null;
	}

	/** Reads a legacy DistributionDouble from file. **/
	public static QualityIntDistr readDistributionDouble(String dir,
			String filename, String name, boolean readValues)
			throws IOException {
		Log.warn("reading legacy DoubleDistribution, converting into QualityIntDitr!");
		if (!readValues)
			return new QualityIntDistr(name);

		Reader r = Reader.getReader(dir, filename);
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
		return new QualityIntDistr(name, 1, values);
	}

	/** Reads a legacy DistributionInt from file. **/
	public static BinnedIntDistr readDistributionInt(String dir,
			String filename, String name, boolean readValues)
			throws IOException {
		return readDistributionLong(dir, filename, name, readValues);
	}

	/** Reads a legacy DistributionLong from file. **/
	public static BinnedIntDistr readDistributionLong(String dir,
			String filename, String name, boolean readValues)
			throws IOException {
		if (!readValues)
			return new BinnedIntDistr(name);

		Reader r = Reader.getReader(dir, filename);
		ArrayList<Long> list = new ArrayList<Long>();
		String line = null;
		int index = 0;

		line = r.readString();
		long denominator = Long.parseLong(line);

		while ((line = r.readString()) != null) {
			String[] temp = line.split(Config.get("DISTRIBUTION_DELIMITER"));
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

		return new BinnedIntDistr(name, 1, values, denominator);
	}

	/** Reads a legacy BinnedDistributionDouble from file. **/
	public static BinnedDoubleDistr readBinnedDistributionDouble(String dir,
			String filename, String name, boolean readValues)
			throws IOException {
		return readBinnedDistributionLong(dir, filename, name, readValues);
	}

	/** Reads a legacy BinnedDistributionInt from file. **/
	public static BinnedIntDistr readBinnedDistributionInt(String dir,
			String filename, String name, boolean readValues)
			throws IOException {
		if (!readValues) {
			return new BinnedIntDistr(name);
		}
		Reader r = Reader.getReader(dir, filename);
		ArrayList<Long> list = new ArrayList<Long>();
		String line = null;
		int index = 0;

		line = r.readString();
		long denominator = Integer.parseInt(line);
		line = r.readString();
		int binsize = Integer.parseInt(line);

		while ((line = r.readString()) != null) {
			String[] temp = line.split(Config.get("DISTRIBUTION_DELIMITER"));
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
		return new BinnedIntDistr(name, binsize, values, denominator);
	}

	/** Reads a legacy BinnedDistributionLong from file. **/
	public static BinnedDoubleDistr readBinnedDistributionLong(String dir,
			String filename, String name, boolean readValues)
			throws IOException {
		if (!readValues)
			return new BinnedDoubleDistr(name);

		Reader r = Reader.getReader(dir, filename);
		ArrayList<Long> list = new ArrayList<Long>();
		String line = null;
		int index = 0;

		line = r.readString();
		long denominator = Long.parseLong(line);
		line = r.readString();
		double binsize = Double.parseDouble(line);

		while ((line = r.readString()) != null) {
			String[] temp = line.split(Config.get("DISTRIBUTION_DELIMITER"));
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
		return new BinnedDoubleDistr(name, binsize, values, denominator);
	}

}
