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
	
	/**
	 * Tests if two MetricData objects are of the same type.
	 * Therefore it checks if they got the same distributions and metrics.
	 * 
	 * @returns true	- when they are of the same type 
	 * 			false	- else
	 * 
	 * @author Rwilmes
	 * @date 24.06.2013
	 */
	public static boolean sameType(MetricData m1, MetricData m2) {
		//if(m1.getName().equals(m2.getName()))
		//	return false;
		
		ValueList list1 = m1.getValues();
		ValueList list2 = m2.getValues();
		
		DistributionList dlist1 = m1.getDistributions();
		DistributionList dlist2 = m2.getDistributions();
		
		if(list1.size() != list2.size() || dlist1.size() != dlist2.size())
			return false;

		for(String k : list1.getNames()) {
			if(!list1.get(k).equals(list2.get(k))) {
				return false;
			}
		}
		for(String k : dlist1.getNames()) {
			if(!dlist1.get(k).equals(dlist2.get(k))) {
				return false;
			}
		}
		return true;
	}

}
