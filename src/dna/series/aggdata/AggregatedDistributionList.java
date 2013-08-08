package dna.series.aggdata;

import java.io.IOException;

import dna.io.filesystem.Files;
import dna.series.lists.List;

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
			list.add(AggregatedDistribution.read(dir, distribution,
					Files.getDistributionName(distribution), readValues));
		}
		return list;
	}
}
