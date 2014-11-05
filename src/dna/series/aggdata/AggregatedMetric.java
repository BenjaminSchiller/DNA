package dna.series.aggdata;

import java.io.IOException;

import dna.io.filesystem.Files;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
import dna.series.lists.ListItem;
import dna.util.Config;

/**
 * An AggregatedMetric contains aggregated values of a metric.
 * 
 * @author Rwilmes
 * @date 04.07.2013
 */
public class AggregatedMetric implements ListItem {

	// member variables
	private String name;
	private AggregatedValueList values;
	private AggregatedDistributionList distributions;
	private AggregatedNodeValueListList nodevalues;

	// constructors
	public AggregatedMetric(String name) {
		this.name = name;
		this.values = new AggregatedValueList();
		this.distributions = new AggregatedDistributionList();
		this.nodevalues = new AggregatedNodeValueListList();
	}

	public AggregatedMetric(String name, int sizeValues, int sizeDistributions,
			int sizeNodeValueList) {
		this.name = name;
		this.values = new AggregatedValueList(sizeValues);
		this.distributions = new AggregatedDistributionList(sizeDistributions);
		this.nodevalues = new AggregatedNodeValueListList(sizeNodeValueList);
	}

	public AggregatedMetric(String name, AggregatedValueList values,
			AggregatedDistributionList distributions,
			AggregatedNodeValueListList nodevalues) {
		this.name = name;
		this.values = values;
		this.distributions = distributions;
		this.nodevalues = nodevalues;
	}

	// methods
	public String getName() {
		return this.name;
	}

	public AggregatedValueList getValues() {
		return this.values;
	}

	public AggregatedDistributionList getDistributions() {
		return this.distributions;
	}

	public AggregatedNodeValueListList getNodeValues() {
		return this.nodevalues;
	}

	// IO methods
	public void write(String dir) throws IOException {
		if (this.values.size() > 0)
			this.values.write(dir,
					Files.getValuesFilename(Config.get("METRIC_DATA_VALUES")));
		this.distributions.write(dir);
		this.nodevalues.write(dir);
	}

	public static AggregatedMetric read(String dir, String name,
			BatchReadMode batchReadMode) throws IOException {
		boolean readSingleValues;
		boolean readDistributions;
		boolean readNodeValues;
		switch (batchReadMode) {
		case readAllValues:
			readSingleValues = true;
			readDistributions = true;
			readNodeValues = true;
			break;
		case readOnlySingleValues:
			readSingleValues = true;
			readDistributions = false;
			readNodeValues = false;
			break;
		case readOnlyDistAndNvl:
			readSingleValues = false;
			readDistributions = true;
			readNodeValues = true;
			break;
		case readNoValues:
			readSingleValues = false;
			readDistributions = false;
			readNodeValues = false;
			break;
		default:
			readSingleValues = true;
			readDistributions = true;
			readNodeValues = true;
			break;
		}
		AggregatedValueList values = AggregatedValueList.read(dir,
				Files.getValuesFilename(Config.get("METRIC_DATA_VALUES")),
				readSingleValues);
		AggregatedDistributionList distributions = AggregatedDistributionList
				.read(dir, readDistributions);
		AggregatedNodeValueListList nodevalues = AggregatedNodeValueListList
				.read(dir, readNodeValues);
		return new AggregatedMetric(name, values, distributions, nodevalues);
	}
}
