package dna.series.aggdata;

import java.io.IOException;

import dna.io.filesystem.Files;
import dna.series.lists.List;

/**
 * An AggregatedNodeValueListList lists AggregatedNodeValueList objects.
 * 
 * @author Rwilmes
 * @date 04.07.2013
 */
public class AggregatedNodeValueListList extends List<AggregatedNodeValueList> {
<<<<<<< HEAD

=======
	
>>>>>>> reworked aggregation
	// constructors
	public AggregatedNodeValueListList() {
		super();
	}

	public AggregatedNodeValueListList(int size) {
		super(size);
	}
<<<<<<< HEAD

	// IO methods
	public void write(String dir) throws IOException {
		for (AggregatedNodeValueList n : this.getList()) {
			AggregatedData.write(n, dir,
					Files.getNodeValueListFilename(n.getName()));
		}
	}

	public static AggregatedNodeValueListList read(String dir,
			boolean readValues) throws IOException {
		String[] nodevalues = Files.getNodeValueLists(dir);
		AggregatedNodeValueListList list = new AggregatedNodeValueListList(
				nodevalues.length);
		for (String nvl : nodevalues) {
			list.add(AggregatedNodeValueList.read(dir, nvl,
					Files.getDistributionName(nvl), readValues));
		}
		return list;
=======
	
	// IO methods
	public void write(String dir) throws IOException {
		for (AggregatedNodeValueList n : this.getList()) {
			AggregatedData.write(n, dir, Files.getNodeValueListFilename(n.getName()));
		}
>>>>>>> reworked aggregation
	}
}
