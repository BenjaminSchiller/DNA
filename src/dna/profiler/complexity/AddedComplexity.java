package dna.profiler.complexity;

import java.util.HashMap;
import java.util.Map.Entry;


/**
 * Complexity that is combined of two other complexities
 * @author Nico
 *
 */
public class AddedComplexity extends Complexity {

	private Complexity first;
	private Complexity second;

	public AddedComplexity(Complexity first,
			Complexity second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public int getComplexityCounter() {
		return this.first.getComplexityCounter() + this.second.getComplexityCounter();
	}
	
	@Override
	public String getComplexity() {
		return this.first.getComplexity() + " + " + this.second.getComplexity();
	}
	
	@Override
	public HashMap<ComplexityType, Integer> getComplexityMap() {
		HashMap<ComplexityType, Integer> res = first.getComplexityMap();
		HashMap<ComplexityType, Integer> resSecond = second.getComplexityMap();
		for ( Entry<ComplexityType, Integer> e : resSecond.entrySet() ) {
			Integer tempCounter = res.get(e.getKey());
			tempCounter += e.getValue();
			res.put(e.getKey(), tempCounter);
		}
		return res;
	}

	@Override
	public HashMap<ComplexityType, Integer> getWeightedComplexityMap() {
		HashMap<ComplexityType, Integer> res = first.getWeightedComplexityMap();
		HashMap<ComplexityType, Integer> resSecond = second.getWeightedComplexityMap();
		for ( Entry<ComplexityType, Integer> e : resSecond.entrySet() ) {
			Integer tempCounter = res.get(e.getKey());
			tempCounter += e.getValue();
			res.put(e.getKey(), tempCounter);
		}
		return res;
	}
}
