package dna.series.aggdata;

import java.io.IOException;

import dna.io.filesystem.Files;
import dna.io.filesystem.Names;
import dna.series.lists.ListItem;

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
<<<<<<< HEAD

=======
	
>>>>>>> reworked aggregation
	// constructors
	public AggregatedMetric(String name) {
		this.name = name;
		this.values = new AggregatedValueList();
		this.distributions = new AggregatedDistributionList();
		this.nodevalues = new AggregatedNodeValueListList();
	}
<<<<<<< HEAD

	public AggregatedMetric(String name, int sizeValues, int sizeDistributions,
			int sizeNodeValueList) {
=======
	
	public AggregatedMetric(String name, int sizeValues, int sizeDistributions, int sizeNodeValueList) {
>>>>>>> reworked aggregation
		this.name = name;
		this.values = new AggregatedValueList(sizeValues);
		this.distributions = new AggregatedDistributionList(sizeDistributions);
		this.nodevalues = new AggregatedNodeValueListList(sizeNodeValueList);
	}
<<<<<<< HEAD

	public AggregatedMetric(String name, AggregatedValueList values,
			AggregatedDistributionList distributions,
			AggregatedNodeValueListList nodevalues) {
=======
	
	public AggregatedMetric(String name, AggregatedValueList values, AggregatedDistributionList distributions, AggregatedNodeValueListList nodevalues) {
>>>>>>> reworked aggregation
		this.name = name;
		this.values = values;
		this.distributions = distributions;
		this.nodevalues = nodevalues;
	}
<<<<<<< HEAD

=======
	
>>>>>>> reworked aggregation
	// methods
	public String getName() {
		return this.name;
	}
<<<<<<< HEAD

	public AggregatedValueList getValues() {
		return this.values;
	}

	public AggregatedDistributionList getDistributions() {
		return this.distributions;
	}

	public AggregatedNodeValueListList getNodeValues() {
		return this.nodevalues;
	}

=======
	
	public AggregatedValueList getValues() {
		return this.values;
	}
	
	public AggregatedDistributionList getDistributions() {
		return this.distributions;
	}
	
	public AggregatedNodeValueListList getNodeValues() {
		return this.nodevalues;
	}
	
>>>>>>> reworked aggregation
	// IO methods
	public void write(String dir) throws IOException {
		this.values.write(dir, Files.getValuesFilename(Names.metricDataValues));
		this.distributions.write(dir);
		this.nodevalues.write(dir);
	}
<<<<<<< HEAD
=======
	
>>>>>>> reworked aggregation

}
