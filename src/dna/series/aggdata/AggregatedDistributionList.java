package dna.series.aggdata;

import java.io.IOException;

import dna.io.filesystem.Files;
import dna.series.lists.List;
import dna.util.Config;

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
				((AggregatedBinnedDistribution) n).write(dir, Files
						.getAggregatedBinnedDistributionFilename(n.getName()));
			else
				AggregatedData.write(n, dir,
						Files.getAggregatedDistributionFilename(n.getName()));
		}
	}

	public static AggregatedDistributionList read(String dir, boolean readValues)
			throws IOException {
		String[] distributions = Files.getDistributions(dir);
		if (distributions == null)
			return new AggregatedDistributionList(0);

		AggregatedDistributionList list = new AggregatedDistributionList(
				distributions.length);
		for (String distribution : distributions) {
			if (distribution.endsWith(Config.get("SUFFIX_DIST_AGGR"))) {
				list.add(AggregatedDistribution.read(dir, distribution,
						Files.getAggregatedDistributionName(distribution),
						readValues));
			} else if (distribution.endsWith(Config
					.get("SUFFIX_DIST_AGGR_BINNED"))) {
				list.add(AggregatedBinnedDistribution.read(
						dir,
						distribution,
						Files.getAggregatedBinnedDistributionName(distribution),
						readValues));
			}
		}
		return list;
	}
}
