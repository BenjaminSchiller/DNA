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

>>>>>>> remotes/beniMaster/master
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
			AggregatedData.write(n, dir, Files.getNodeValueListFilename(n.getName()));
=======

	// IO methods
	public void write(String dir) throws IOException {
		for (AggregatedNodeValueList n : this.getList()) {
			AggregatedData.write(n, dir,
					Files.getNodeValueListFilename(n.getName()));
>>>>>>> remotes/beniMaster/master
		}
	}
}
