package dna.graph.tests;

import static org.junit.Assert.assertEquals;

import java.util.Map.Entry;

import org.junit.Test;

import dna.profiler.complexity.AddedComplexity;
import dna.profiler.complexity.Complexity;
import dna.profiler.complexity.ComplexityMap;
import dna.profiler.complexity.ComplexityType;
import dna.profiler.complexity.ComplexityType.Base;
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
		assertEquals(2, c3.getComplexityCounter());

		ComplexityMap complexityMap = c3.getComplexityMap();
		assertEquals(2, (int) complexityMap.get(c));

		ComplexityMap weightedComplexityMap = c3.getWeightedComplexityMap();
		assertEquals(2, (int) weightedComplexityMap.get(c));
	}

	@Test
	public void simpleAdditionOfSameKindWithDifferentFactor() {
		Complexity c1 = new Complexity(1, c);
		Complexity c2 = new Complexity(3, c);

		c1.increaseBy(2);
		c2.increaseBy(4);

		ComplexityMap weightedComplexityMap = c1.getWeightedComplexityMap();
		assertEquals(2, (int) weightedComplexityMap.get(c));
		weightedComplexityMap = c2.getWeightedComplexityMap();
		assertEquals(12, (int) weightedComplexityMap.get(c));

		Complexity c3 = new AddedComplexity(c1, c2);
		assertEquals(6, c3.getComplexityCounter());

		ComplexityMap complexityMap = c3.getComplexityMap();
		assertEquals(6, (int) complexityMap.get(c));

		weightedComplexityMap = c3.getWeightedComplexityMap();
		assertEquals(14, (int) weightedComplexityMap.get(c));
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

		ComplexityMap weightedComplexityMap = c1.getWeightedComplexityMap();
		assertEquals(2, (int) weightedComplexityMap.get(c));

		weightedComplexityMap = c2.getWeightedComplexityMap();
		assertEquals(12, (int) weightedComplexityMap.get(c));

		weightedComplexityMap = c3.getWeightedComplexityMap();
		assertEquals(14, (int) weightedComplexityMap.get(c));

		weightedComplexityMap = c4.getWeightedComplexityMap();
		assertEquals(26, (int) weightedComplexityMap.get(c));
	}

	@Test
	public void checkThatSortWorksWithSameKey() {
		ComplexityMap c1;
		ComplexityMap c2;

		c1 = new ComplexityMap();
		c2 = new ComplexityMap();

		c1.put(new ComplexityType(Type.Linear, Base.NodeSize), 5);
		c2.put(new ComplexityType(Type.Linear, Base.NodeSize), 6);
		assertSmallerThan(c1, c2);

		c1 = new ComplexityMap();
		c2 = new ComplexityMap();

		c1.put(new ComplexityType(Type.Linear, Base.Degree), 5);
		c2.put(new ComplexityType(Type.Linear, Base.Degree), 6);
		assertSmallerThan(c1, c2);
	}

	@Test
	public void checkThatSortWorksWithDifferentKey() {
		ComplexityMap c1;
		ComplexityMap c2;

		for (Base n : Base.values()) {
			c1 = new ComplexityMap();
			c2 = new ComplexityMap();

			c1.put(new ComplexityType(Type.Static, n), 1);
			c2.put(new ComplexityType(Type.Linear, n), 1);
			assertSmallerThan(c1, c2);
		}
	}

	@Test
	public void checkThatSortWorksWithDifferentKeyAndCounts() {
		ComplexityMap c1;
		ComplexityMap c2;

		c1 = new ComplexityMap();
		c2 = new ComplexityMap();

		c1.put(new ComplexityType(Type.Static, Base.NodeSize), 5);
		c2.put(new ComplexityType(Type.Linear, Base.NodeSize), 1);
		assertSmallerThan(c1, c2);

		c1 = new ComplexityMap();
		c2 = new ComplexityMap();

		c2.put(new ComplexityType(Type.Static, Base.NodeSize), 15);
		c1.put(new ComplexityType(Type.Linear, Base.NodeSize), 10);
		assertSmallerThan(c2, c1);
	}
	
	@Test
	public void checkProperOrderOfComplexityMap() {
		ComplexityType staticCompl = new ComplexityType(Type.Static, null);
		ComplexityType linearDegreeCompl = new ComplexityType(Type.Linear, Base.Degree);
		ComplexityType linearNodeSizeCompl = new ComplexityType(Type.Linear, Base.NodeSize);
		ComplexityType linearEdgeSizeCompl = new ComplexityType(Type.Linear, Base.EdgeSize);
		ComplexityType unknownCompl = new ComplexityType(Type.Unknown, null);
		
		ComplexityMap c = new ComplexityMap();
		c.put(staticCompl, 1);
		c.put(linearDegreeCompl, 1);
		c.put(linearNodeSizeCompl, 1);
		c.put(linearEdgeSizeCompl, 1);
		c.put(unknownCompl, 1);
		
		Entry<ComplexityType, Integer>[] entrySet = c.entrySet().toArray(new Entry[0]);
		assertEquals(staticCompl, entrySet[0].getKey());
		assertEquals(linearDegreeCompl, entrySet[1].getKey());
		assertEquals(linearNodeSizeCompl, entrySet[2].getKey());
		assertEquals(linearEdgeSizeCompl, entrySet[3].getKey());
		assertEquals(unknownCompl, entrySet[4].getKey());
	}

	private <T extends java.lang.Comparable<T>> void assertSmallerThan(T one,
			T two) {
		assertEquals(-1, one.compareTo(two));
	}

}
