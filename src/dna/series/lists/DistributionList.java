package dna.series.lists;

import java.io.IOException;

import dna.io.filesystem.Files;
import dna.series.data.Distribution;

public class DistributionList extends List<Distribution> {
	public DistributionList() {
		super();
	}

	public DistributionList(int size) {
		super(size);
	}

	public void write(String dir) throws IOException {
		for (Distribution d : this.getList()) {
			d.write(dir, Files.getDistributionFilename(d.getName()));
		}
	}

	public static DistributionList read(String dir,
			boolean readDistributionValues) throws IOException {
		String[] distributions = Files.getDistributions(dir);
		DistributionList list = new DistributionList(distributions.length);
		for (String distribution : distributions) {
			list.add(Distribution.read(dir, distribution,
					Files.getDistributionName(distribution),
					readDistributionValues));
		}
		return list;
	}
}
