package dna.series.aggdata;

import java.io.IOException;

import dna.io.filesystem.Files;
import dna.series.lists.List;
import dna.util.Config;
import dna.util.Log;

/**
 * An AggregatedDistributionList lists AggregatedDistribution objects.
 * 
 * @author Rwilmes
 * @date 04.07.2013
 */
public class AggregatedDistributionList extends List<AggregatedDistribution> {

	// constructors
	public AggregatedDistributionList() {
		super();
	}

	public AggregatedDistributionList(int size) {
		super(size);
	}

	// IO methods
	public void write(String dir) throws IOException {
		for (AggregatedDistribution n : this.getList()) {
			if (n instanceof AggregatedBinnedDistribution)
				AggregatedData.write(n, dir,
						Files.getDistributionBinnedFilename(n.getName()));
			else
				AggregatedData.write(n, dir,
						Files.getDistributionFilename(n.getName()));
		}
	}

	public static AggregatedDistributionList read(String dir, boolean readValues)
			throws IOException {
		String[] distributions = Files.getDistributions(dir);
		AggregatedDistributionList list = new AggregatedDistributionList(
				distributions.length);
		for (String distribution : distributions) {
			String[] temp = distribution.split("\\"
					+ Config.get("FILE_NAME_DELIMITER"));
			try {
				if ((Config.get("FILE_NAME_DELIMITER") + temp[temp.length - 1])
						.equals(Config.get("SUFFIX_DIST"))) {
					list.add(AggregatedDistribution
							.read(dir, distribution,
									Files.getDistributionName(distribution),
									readValues));
				}
				if ((Config.get("FILE_NAME_DELIMITER") + temp[temp.length - 1])
						.equals(Config.get("SUFFIX_DIST_BINNED"))) {
					list.add(AggregatedBinnedDistribution.read(dir,
							distribution,
							Files.getDistributionName(distribution), readValues));
				}
			} catch (IndexOutOfBoundsException e) {
				Log.warn("Attempting to read distribution " + distribution
						+ " at " + dir + " ! No datastructure detected!");
			}
		}
		return list;
	}
}
