package dna.series.aggdata;

import java.io.IOException;
<<<<<<< HEAD
<<<<<<< HEAD

import dna.io.filesystem.Dir;

/**
 * AggregatedSeries is a class for objects that contain the aggregation for a
 * whole series.
 * 
 * @author Rwilmes
 * @date 04.07.2013
=======
import java.util.ArrayList;
import java.util.HashMap;

import dna.io.etc.Keywords;
=======
>>>>>>> reworked aggregation
import dna.io.filesystem.Dir;

/**
 * AggregatedSeries is a class for objects that contain the aggregation for a whole series.
 * 
 * @author Rwilmes
<<<<<<< HEAD
 * @date 25.06.2013
>>>>>>> Codeupdate 13-06-28
=======
 * @date 04.07.2013
>>>>>>> reworked aggregation
 */
public class AggregatedSeries {

	// member variables
<<<<<<< HEAD
<<<<<<< HEAD
	private AggregatedBatch[] batches;

	// constructors
	public AggregatedSeries() {
	}

	public AggregatedSeries(AggregatedBatch[] batches) {
		this.batches = batches;
	}

	// methods
	public AggregatedBatch[] getBatches() {
		return this.batches;
	}

	// IO Methods
	public void write(String dir) throws IOException {
		for (int i = 0; i < this.getBatches().length; i++) {
			long tempTimestamp = this.getBatches()[i].getTimestamp();
			this.getBatches()[i].write(Dir.getBatchDataDir(dir, tempTimestamp));
		}
	}

	public static AggregatedSeries read(String dir, String name,
			boolean readValues) throws IOException {
		String tempDir = Dir.getAggregationDataDir(dir);

		String[] batches = Dir.getBatches(tempDir);
		AggregatedBatch[] aggBatches = new AggregatedBatch[Dir
				.getBatches(tempDir).length];

		for (int i = 0; i < batches.length; i++) {
			long timestamp = Dir.getTimestamp(batches[i]);
			aggBatches[i] = AggregatedBatch.read(
					Dir.getAggregationBatchDir(dir, timestamp), timestamp,
					readValues);
		}

		return new AggregatedSeries(aggBatches);
	}
}
=======
	private HashMap<String, AggregatedDataList>[] aggregation;
=======
	private AggregatedBatch[] batches;
>>>>>>> reworked aggregation
	
	// constructors
	public AggregatedSeries() {}
		
	public AggregatedSeries(AggregatedBatch[] batches) {
		this.batches = batches;
	}
	
	// methods
	public AggregatedBatch[] getBatches() {
		return this.batches;
	}
	
	// IO Methods 
	// TODO: READ ??
	public void write(String dir) throws IOException {
		for(int i = 0; i < this.getBatches().length; i++) {
			this.getBatches()[i].write(Dir.getBatchDataDir(dir, i));
		}
	}
		
		
}
	
	
	

>>>>>>> Codeupdate 13-06-28
