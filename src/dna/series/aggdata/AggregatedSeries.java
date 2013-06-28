package dna.series.aggdata;

import java.io.IOException;
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
import dna.io.filesystem.Dir;
import dna.io.filesystem.Files;
import dna.io.filesystem.Names;
import dna.io.filesystem.Suffix;

/**
 * AggregatedSeries is a class for objects that contain the aggregation for a whole series.
 * 
 * @author Rwilmes
 * @date 25.06.2013
>>>>>>> Codeupdate 13-06-28
 */
public class AggregatedSeries {

	// member variables
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
	
	// constructors
	public AggregatedSeries() {}
	
	public AggregatedSeries(HashMap<String, AggregatedDataList>[] aggregation) {
		this.aggregation = aggregation;
	}
	
	// methods
	public HashMap<String, AggregatedDataList>[] getAggregation() {
		return this.aggregation;
	}
	
	// IO Methods 
	// TODO: READ ??
	public void write(String dir) throws IOException {
		String tempdir = "";
		HashMap<String, AggregatedDataList>[] aggregation = this.getAggregation();
		
		for(int i = 0; i < aggregation.length; i++) {
			// aggMap contains aggregated data for batch i
			HashMap<String, AggregatedDataList> aggMap = aggregation[i];
			tempdir = Dir.getBatchDataDir(dir, i);
			
			// each batch got a batchData containing runtime data and statistical values 
			AggregatedDataList batchStats = aggMap.get(Keywords.batchData);
			aggMap.remove(Keywords.batchData);
			if(batchStats != null) {
				
				// gathering all statistical values in statsTemp to write at once in one file
				ArrayList<AggregatedValue> statsTemp = new ArrayList<AggregatedValue>();
				
				for(String batchDatas : batchStats.getNames()) {
					if(batchStats.get(batchDatas) instanceof AggregatedRunTimeList) {
						AggregatedValue[] aggValuesTemp = ((AggregatedRunTimeList) batchStats.get(batchDatas)).getValues();
						AggregatedData.write(aggValuesTemp, tempdir, Files.getRuntimesFilename(batchStats.get(batchDatas).getName()));
					}
					if(batchStats.get(batchDatas) instanceof AggregatedValue) {
						statsTemp.add((AggregatedValue) batchStats.get(batchDatas));
					}
				}
				AggregatedData.write( statsTemp , tempdir, Names.batchStats + Suffix.values);
			}

			// aggMap<metric, AggregatedDataList (of metric)>
			for(String metric : aggMap.keySet()) {
				tempdir = Dir.getMetricDataDir(tempdir, metric);
				AggregatedDataList aggDataList = aggMap.get(metric);
				
				ArrayList<AggregatedValue> aggValuesTemp = new ArrayList<AggregatedValue>();
				
				for(String aggData : aggDataList.getNames()) {
					if(aggDataList.get(aggData) instanceof AggregatedDistribution) {
						AggregatedData.write((AggregatedDistribution) aggDataList.get(aggData), tempdir, Files.getDistributionFilename(aggDataList.get(aggData).getName()));
					}
					if(aggDataList.get(aggData) instanceof AggregatedNodeValueList) {
						AggregatedData.write((AggregatedNodeValueList) aggDataList.get(aggData), tempdir, Files.getNodeValueListFilename(aggDataList.get(aggData).getName()));
					}

					if(aggDataList.get(aggData) instanceof AggregatedValue) {
						aggValuesTemp.add((AggregatedValue) aggDataList.get(aggData));
					}
				}
				AggregatedData.write(aggValuesTemp, tempdir, Names.metricDataValues + Suffix.values);
				tempdir = Dir.getBatchDataDir(dir, i);
			}
		}
	}
		
		
}
	
	
	

>>>>>>> Codeupdate 13-06-28
