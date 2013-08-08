package dna.series.aggdata;

import java.io.IOException;
<<<<<<< HEAD
import dna.io.filesystem.Dir;

/**
 * AggregatedSeries is a class for objects that contain the aggregation for a whole series.
=======

import dna.io.filesystem.Dir;

/**
 * AggregatedSeries is a class for objects that contain the aggregation for a
 * whole series.
>>>>>>> remotes/beniMaster/master
 * 
 * @author Rwilmes
 * @date 04.07.2013
 */
public class AggregatedSeries {

	// member variables
	private AggregatedBatch[] batches;
<<<<<<< HEAD
	
	// constructors
	public AggregatedSeries() {}
		
	public AggregatedSeries(AggregatedBatch[] batches) {
		this.batches = batches;
	}
	
=======

	// constructors
	public AggregatedSeries() {
	}

	public AggregatedSeries(AggregatedBatch[] batches) {
		this.batches = batches;
	}

>>>>>>> remotes/beniMaster/master
	// methods
	public AggregatedBatch[] getBatches() {
		return this.batches;
	}
<<<<<<< HEAD
	
	// IO Methods 
	// TODO: READ ??
	public void write(String dir) throws IOException {
		for(int i = 0; i < this.getBatches().length; i++) {
			this.getBatches()[i].write(Dir.getBatchDataDir(dir, i));
		}
	}
		
		
}
	
	
	

=======

	// IO Methods
	// TODO: READ ??
	public void write(String dir) throws IOException {
		for (int i = 0; i < this.getBatches().length; i++) {
			this.getBatches()[i].write(Dir.getBatchDataDir(dir, i));
		}
	}

}
>>>>>>> remotes/beniMaster/master
