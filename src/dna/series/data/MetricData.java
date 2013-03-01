package dna.series.data;

import java.io.IOException;

import dna.io.filesystem.Files;
import dna.io.filesystem.Names;
import dna.series.lists.DistributionList;
import dna.series.lists.ListItem;
import dna.series.lists.ValueList;

public class MetricData implements ListItem {

	public MetricData(String name) {
		this.name = name;
		this.values = new ValueList();
		this.distributions = new DistributionList();
	}

	public MetricData(String name, int sizeValues, int sizeDistributions) {
		this.name = name;
		this.values = new ValueList(sizeValues);
		this.distributions = new DistributionList(sizeDistributions);
	}

	public MetricData(String name, Value[] values, Distribution[] distributions) {
		this(name, values.length, distributions.length);
		for (Value v : values) {
			this.values.add(v);
		}
		for (Distribution d : distributions) {
			this.distributions.add(d);
		}
	}

	public MetricData(String name, ValueList values,
			DistributionList distributions) {
		this.name = name;
		this.values = values;
		this.distributions = distributions;
	}

	String name;

	public String getName() {
		return this.name;
	}

	private ValueList values;

	public ValueList getValues() {
		return this.values;
	}

	private DistributionList distributions;

	public DistributionList getDistributions() {
		return this.distributions;
	}

	public void write(String dir) throws IOException {
		this.values.write(dir, Files.getValuesFilename(Names.metricDataValues));
		this.distributions.write(dir);
	}

	public static MetricData read(String dir, String name,
			boolean readDistributionValues) throws IOException {
		ValueList values = ValueList.read(dir,
				Files.getValuesFilename(Names.metricDataValues));
		DistributionList distributions = DistributionList.read(dir,
				readDistributionValues);
		return new MetricData(name, values, distributions);
	}

}
