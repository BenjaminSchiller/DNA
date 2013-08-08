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
<<<<<<< HEAD
	
	// IO methods
	public void write(String dir) throws IOException {
		for (AggregatedDistribution n : this.getList()) {
			AggregatedData.write(n, dir, Files.getDistributionFilename(n.getName()));
		}
	}
	
=======

	// IO methods
	public void write(String dir) throws IOException {
		for (AggregatedDistribution n : this.getList()) {
			AggregatedData.write(n, dir,
					Files.getDistributionFilename(n.getName()));
		}
	}

>>>>>>> remotes/beniMaster/master
}
