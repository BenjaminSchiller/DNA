package dna.graph.tests;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

import dna.profiler.complexity.AddedComplexity;
import dna.profiler.complexity.Complexity;
import dna.profiler.complexity.ComplexityType;
import dna.profiler.complexity.ComplexityType.Type;

public class ComplexityTests {
	private ComplexityType c = new ComplexityType(Type.Static, null);
	
	@Test
	public void simpleAdditionOfSameKind() {
		Complexity c1 = new Complexity(1, c);
		Complexity c2 = new Complexity(1, c);
		
		c1.increaseBy(1);
		c2.increaseBy(1);
		
		Complexity c3 = new AddedComplexity(c1, c2);
		assertEquals(2,c3.getComplexityCounter());
		
		HashMap<ComplexityType, Integer> complexityMap = c3.getComplexityMap();
		assertEquals(2, (int)complexityMap.get(c));

		HashMap<ComplexityType, Integer> weightedComplexityMap = c3.getWeightedComplexityMap();
		assertEquals(2, (int)weightedComplexityMap.get(c));
	}
	
	@Test
	public void simpleAdditionOfSameKindWithDifferentFactor() {
		Complexity c1 = new Complexity(1, c);
		Complexity c2 = new Complexity(3, c);
		
		c1.increaseBy(2);
		c2.increaseBy(4);

		HashMap<ComplexityType, Integer> weightedComplexityMap = c1.getWeightedComplexityMap();
		assertEquals(2, (int)weightedComplexityMap.get(c));
		weightedComplexityMap = c2.getWeightedComplexityMap();
		assertEquals(12, (int)weightedComplexityMap.get(c));
		
		Complexity c3 = new AddedComplexity(c1, c2);
		assertEquals(6, c3.getComplexityCounter());
		
		HashMap<ComplexityType, Integer> complexityMap = c3.getComplexityMap();
		assertEquals(6, (int)complexityMap.get(c));	
		
		weightedComplexityMap = c3.getWeightedComplexityMap();
		assertEquals(14, (int)weightedComplexityMap.get(c));		
	}
	
	@Test
	public void nestedAdditionWithDifferentFactor() {
		Complexity c1 = new Complexity(1, c);
		Complexity c2 = new Complexity(3, c);
		
		c1.increaseBy(2);
		c2.increaseBy(4);
		Complexity c3 = new AddedComplexity(c1, c2);
		Complexity c4 = new AddedComplexity(c3, c2);
		assertEquals(10, c4.getComplexityCounter());
		
		HashMap<ComplexityType, Integer> weightedComplexityMap = c1.getWeightedComplexityMap();
		assertEquals(2, (int)weightedComplexityMap.get(c));
		
		weightedComplexityMap = c2.getWeightedComplexityMap();
		assertEquals(12, (int)weightedComplexityMap.get(c));
		
		weightedComplexityMap = c3.getWeightedComplexityMap();
		assertEquals(14, (int)weightedComplexityMap.get(c));
		
		weightedComplexityMap = c4.getWeightedComplexityMap();
		assertEquals(26, (int)weightedComplexityMap.get(c));
	}

}
