package dna.profiler.datatypes.benchmarkresults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.common.base.Joiner;

import dna.profiler.datatypes.ComparableEntry;
import dna.profiler.datatypes.ComparableEntryMap;
import dna.profiler.datatypes.complexity.ComplexityType.Base;

public class BenchmarkingResult extends ComparableEntry {

	private int counter = 0;
	private double meanListSize;	
	
	private String name;
	private TreeMap<Integer, ArrayList<Double>> datamap;

	public BenchmarkingResult(String name) {
		this(name, new TreeMap<Integer, ArrayList<Double>>());
	}

	public BenchmarkingResult(String name, TreeMap<Integer, ArrayList<Double>> entryMap) {
		this.name = name;
		this.datamap = entryMap;
	}
	
	public static ComparableEntry parseString(String key, String val) {
		TreeMap<Integer, ArrayList<Double>> entryMap = new TreeMap<Integer, ArrayList<Double>>();
		
		String[] inputParts = val.split(";");
		for (String singlePart: inputParts) {
			String[] keyValuePair = singlePart.split("=");
			Integer partKey = Integer.parseInt(keyValuePair[0]);
			String partValue = keyValuePair[1];
			
			ArrayList<Double> innerList = new ArrayList<Double>(); 
			String[] innerParts = partValue.split(",");
			for ( String singleInnerPart: innerParts) {
				innerList.add(Double.parseDouble(singleInnerPart));
			}
			entryMap.put(partKey, innerList);
		}
		
		return new BenchmarkingResult(key, entryMap);
	}	
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name.toUpperCase() + " = ");
		
		String[] innerSets = new String[datamap.size()];
		int count = 0;
		for ( Entry<Integer, ArrayList<Double>> e: datamap.entrySet()) {
			innerSets[count] = e.getKey() + "=" + Joiner.on(",").join(e.getValue());
			count++;
		}
		sb.append(Joiner.on(";").join(innerSets));		
		return sb.toString();
	}

	public void addToMap(Integer key, Collection<Double> value) {
		// Does the key already exist? Then append new values
		if (datamap.containsKey(key)) {
			ArrayList<Double> existingValues = datamap.get(key);
			existingValues.addAll(value);
			datamap.put(key, existingValues);
		} else {
			ArrayList<Double> values = new ArrayList<Double>();
			values.addAll(value);
			datamap.put(key, values);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ComparableEntry clone() {
		TreeMap<Integer, ArrayList<Double>> map = (TreeMap<Integer, ArrayList<Double>>) datamap
				.clone();
		BenchmarkingResult cloned = new BenchmarkingResult(this.name, map);
		cloned.setValues(counter, meanListSize, null);
		return cloned;
	}

	@Override
	public void setValues(int numberOfCalls, double meanListSize, Base base) {
		this.counter = numberOfCalls;
		this.meanListSize = meanListSize;
	}

	@Override
	public String getData() {
		return counter + " calls with a mean list size of " + meanListSize;
	}

	@Override
	public int getCounter() {
		return counter;
	}

	@Override
	public void setCounter(int counter) {
		this.counter = counter;
	}

	@Override
	public ComparableEntryMap getMap() {
		// TODO set a proper value here!
		BenchmarkingResultsMap map = new BenchmarkingResultsMap(0);
		return map;
	}

}
