package dynamicGraphs.series;

import java.util.Collection;
import java.util.HashMap;

public class MetricData {

	public MetricData(String name) {
		this.name = name;
		this.values = new HashMap<String, Value>();
		this.distributions = new HashMap<String, Distribution>();
	}

	public MetricData(String name, int sizeValues, int sizeDistributions) {
		this.name = name;
		this.values = new HashMap<String, Value>(sizeValues);
		this.distributions = new HashMap<String, Distribution>(
				sizeDistributions);
	}

	public MetricData(String name, Value[] values, Distribution[] distributions) {
		this.name = name;
		this.values = new HashMap<String, Value>(values.length);
		for (Value value : values) {
			this.addValue(value);
		}
		this.distributions = new HashMap<String, Distribution>(
				distributions.length);
		for (Distribution distribution : distributions) {
			this.addDistribution(distribution);
		}
	}

	String name;

	public String getName() {
		return this.name;
	}

	private HashMap<String, Value> values;

	public Collection<Value> getValues() {
		return this.values.values();
	}

	public Value getValue(String name) {
		return this.values.get(name);
	}

	public void addValue(Value value) {
		this.values.put(value.getName(), value);
	}

	private HashMap<String, Distribution> distributions;

	public Collection<Distribution> getDistributions() {
		return this.distributions.values();
	}

	public Distribution getDistribution(String name) {
		return this.distributions.get(name);
	}

	public void addDistribution(Distribution distribution) {
		this.distributions.put(distribution.getName(), distribution);
	}

}
