package dna.series.lists;

import java.io.IOException;

import dna.io.filesystem.Files;
import dna.series.data.BinnedDistributionDouble;
import dna.series.data.BinnedDistributionInt;
import dna.series.data.BinnedDistributionLong;
import dna.series.data.Distribution;
import dna.series.data.DistributionDouble;
import dna.series.data.DistributionInt;
import dna.series.data.DistributionLong;
import dna.util.Config;
import dna.util.Log;

public class DistributionList extends List<Distribution> {
	public DistributionList() {
		super();
	}

	public DistributionList(int size) {
		super(size);
	}

	public void write(String dir) throws IOException {
		for (Distribution d : this.getList()) {
			if (d instanceof DistributionInt) {
				if (d instanceof BinnedDistributionInt)
					((BinnedDistributionInt) d)
							.write(dir, Files
									.getDistributionBinnedIntFilename(d
											.getName()));
				else
					((DistributionInt) d).write(dir,
							Files.getDistributionIntFilename(d.getName()));
			}
			if (d instanceof DistributionLong) {
				if (d instanceof BinnedDistributionLong)
					((BinnedDistributionLong) d).write(dir, Files
							.getDistributionBinnedLongFilename(d.getName()));
				else
					((DistributionLong) d).write(dir,
							Files.getDistributionLongFilename(d.getName()));
			}
			if (d instanceof DistributionDouble) {
				if (d instanceof BinnedDistributionDouble)
					((BinnedDistributionDouble) d).write(dir, Files
							.getDistributionBinnedDoubleFilename(d.getName()));
				else
					((DistributionDouble) d).write(dir,
							Files.getDistributionDoubleFilename(d.getName()));
			}
			if (!(d instanceof DistributionInt)
					&& !(d instanceof DistributionLong)
					&& !(d instanceof DistributionDouble))
				d.write(dir, Files.getDistributionFilename(d.getName()));

		}
	}

	public static DistributionList readDiff(String dir, boolean readValues)
			throws IOException {
		String[] distributions = Files.getDistributions(dir);
		DistributionList list = new DistributionList(distributions.length);
		for (String distribution : distributions) {
			list.add(Distribution.read(dir, distribution,
					Files.getDistributionName(distribution), readValues));
		}
		return list;
	}

	public static DistributionList read(String dir, boolean readValues)
			throws IOException {
		String[] distributions = Files.getDistributions(dir);
		DistributionList list = new DistributionList(distributions.length);

		for (String distribution : distributions) {
			String[] temp = distribution.split("\\"
					+ Config.get("FILE_NAME_DELIMITER"));
			try {
				if ((Config.get("FILE_NAME_DELIMITER") + temp[temp.length - 1])
						.equals(Config.get("SUFFIX_DIST_INT")))
					list.add(DistributionInt.read(dir,
							Files.getDistributionIntFilename(temp[0]), temp[0],
							readValues));
				if ((Config.get("FILE_NAME_DELIMITER") + temp[temp.length - 1])
						.equals(Config.get("SUFFIX_DIST_LONG")))
					list.add(DistributionLong.read(dir,
							Files.getDistributionLongFilename(temp[0]),
							temp[0], readValues));
				if ((Config.get("FILE_NAME_DELIMITER") + temp[temp.length - 1])
						.equals(Config.get("SUFFIX_DIST_DOUBLE"))) {
					list.add(DistributionDouble.read(dir,
							Files.getDistributionDoubleFilename(temp[0]),
							temp[0], readValues));
				}
				if ((Config.get("FILE_NAME_DELIMITER") + temp[temp.length - 1])
						.equals(Config.get("SUFFIX_DIST"))) {
					list.add(Distribution.read(dir,
							Files.getDistributionFilename(temp[0]), temp[0],
							readValues));
				}
				if ((Config.get("FILE_NAME_DELIMITER") + temp[temp.length - 1])
						.equals(Config.get("SUFFIX_DIST_BINNED_INT"))) {
					list.add(BinnedDistributionInt.read(dir,
							Files.getDistributionBinnedIntFilename(temp[0]),
							temp[0], readValues));
				}
				if ((Config.get("FILE_NAME_DELIMITER") + temp[temp.length - 1])
						.equals(Config.get("SUFFIX_DIST_BINNED_LONG"))) {
					list.add(BinnedDistributionLong.read(dir,
							Files.getDistributionBinnedLongFilename(temp[0]),
							temp[0], readValues));
				}
				if ((Config.get("FILE_NAME_DELIMITER") + temp[temp.length - 1])
						.equals(Config.get("SUFFIX_DIST_BINNED_DOUBLE"))) {
					list.add(BinnedDistributionDouble.read(dir,
							Files.getDistributionBinnedDoubleFilename(temp[0]),
							temp[0], readValues));
				}
			} catch (IndexOutOfBoundsException e) {
				Log.warn("Attempting to read distribution " + distribution
						+ " at " + dir + " ! No datastructure detected!");
			}
		}
		return list;
	}
}
