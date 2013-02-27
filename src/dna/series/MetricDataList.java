package dna.series;

import java.util.Collection;
import java.util.HashMap;

public class MetricDataList {

	public MetricDataList() {
		this.map = new HashMap<String, MetricData>();
	}

	public MetricDataList(int size) {
		this.map = new HashMap<String, MetricData>(size);
	}

	private HashMap<String, MetricData> map;

	public Collection<String> getNames() {
		return this.map.keySet();
	}

	public Collection<MetricData> getList() {
		return this.map.values();
	}

	public MetricData get(String name) {
		return this.map.get(name);
	}

	public void add(MetricData metricData) {
		this.map.put(metricData.getName(), metricData);
	}

	public void write(String dir) {
		// TODO implement write
	}

	public static MetricDataList read(String dir) {
		// TODO implement read
		return null;
	}
}
