package dna.profiler.complexity;

import java.util.EnumMap;
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
	public EnumMap<ComplexityType, Integer> getComplexityMap() {
		EnumMap<ComplexityType, Integer> res = first.getComplexityMap();
		EnumMap<ComplexityType, Integer> resSecond = second.getComplexityMap();
		for ( Entry<ComplexityType, Integer> e : resSecond.entrySet() ) {
			Integer tempCounter = res.get(e.getKey());
			tempCounter += e.getValue();
			res.put(e.getKey(), tempCounter);
		}
		return res;
	}

	@Override
	public EnumMap<ComplexityType, Integer> getWeightedComplexityMap() {
		EnumMap<ComplexityType, Integer> res = first.getWeightedComplexityMap();
		EnumMap<ComplexityType, Integer> resSecond = second.getWeightedComplexityMap();
		for ( Entry<ComplexityType, Integer> e : resSecond.entrySet() ) {
			Integer tempCounter = res.get(e.getKey());
			tempCounter += e.getValue();
			res.put(e.getKey(), tempCounter);
		}
		return res;
	}
}
