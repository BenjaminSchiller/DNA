package dna.graph.tests;

import static org.junit.Assert.assertEquals;

import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.Test;

import dna.graph.datastructures.GraphDataStructure;
import dna.profiler.datatypes.AddedComparableEntry;
import dna.profiler.datatypes.ComparableEntry;
import dna.profiler.datatypes.complexity.Complexity;
import dna.profiler.datatypes.complexity.ComplexityMap;
import dna.profiler.datatypes.complexity.ComplexityType;
import dna.profiler.datatypes.complexity.ComplexityType.Base;
import dna.profiler.datatypes.complexity.ComplexityType.Type;

public class ComplexityTests {
	private ComplexityType c = new ComplexityType(Type.Static, null);

	@Test
	public void simpleAdditionOfSameKind() {
		ComparableEntry c1 = new Complexity(1, c);
		ComparableEntry c2 = new Complexity(1, c);

		c1.setCounter(c1.getCounter() + 1);
		c2.setCounter(c2.getCounter() + 1);

		ComparableEntry c3 = new AddedComparableEntry(c1, c2);
		assertEquals(2, c3.getCounter());

		ComplexityMap weightedComplexityMap = (ComplexityMap) c3.getMap();
		assertEquals(2, (int) weightedComplexityMap.get(c));
	}

	@Test
	public void simpleAdditionOfSameKindWithDifferentFactor() {
		ComparableEntry c1 = new Complexity(1, c);
		ComparableEntry c2 = new Complexity(3, c);

		c1.setCounter(c1.getCounter() + 2);
		c2.setCounter(c2.getCounter() + 4);

		ComplexityMap weightedComplexityMap = (ComplexityMap) c1.getMap();
		assertEquals(2, (int) weightedComplexityMap.get(c));
		weightedComplexityMap = (ComplexityMap) c2.getMap();
		assertEquals(12, (int) weightedComplexityMap.get(c));

		ComparableEntry c3 = new AddedComparableEntry(c1, c2);
		assertEquals(6, c3.getCounter());

		weightedComplexityMap = (ComplexityMap) c3.getMap();
		assertEquals(14, (int) weightedComplexityMap.get(c));
	}

	@Test
	public void nestedAdditionWithDifferentFactor() {
		ComparableEntry c1 = new Complexity(1, c);
		ComparableEntry c2 = new Complexity(3, c);

		c1.setCounter(c1.getCounter() + 2);
		c2.setCounter(c2.getCounter() + 4);
		
		ComparableEntry c3 = new AddedComparableEntry(c1, c2);
		ComparableEntry c4 = new AddedComparableEntry(c3, c2);
		assertEquals(10, c4.getCounter());

		ComplexityMap weightedComplexityMap = (ComplexityMap) c1.getMap();
		assertEquals(2, (int) weightedComplexityMap.get(c));

		weightedComplexityMap = (ComplexityMap) c2.getMap();
		assertEquals(12, (int) weightedComplexityMap.get(c));

		weightedComplexityMap = (ComplexityMap) c3.getMap();
		assertEquals(14, (int) weightedComplexityMap.get(c));

		weightedComplexityMap = (ComplexityMap) c4.getMap();
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
		assertFirstIsSmallerThanSecond(c1, c2);

		c1 = new ComplexityMap();
		c2 = new ComplexityMap();

		c1.put(new ComplexityType(Type.Linear, Base.Degree), 5);
		c2.put(new ComplexityType(Type.Linear, Base.Degree), 6);
		assertFirstIsSmallerThanSecond(c1, c2);
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
			assertFirstIsSmallerThanSecond(c1, c2);
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
		assertFirstIsSmallerThanSecond(c1, c2);
		
		c1 = new ComplexityMap();
		c2 = new ComplexityMap();

		c1.put(new ComplexityType(Type.Linear, Base.NodeSize), 10);
		c2.put(new ComplexityType(Type.Static, Base.NodeSize), 15);
		assertFirstIsSmallerThanSecond(c2, c1);
	}
	
	@Test
	public void checkSomeSortingCases() {
		ComplexityMap c1, c2;
		
		c1 = new ComplexityMap();
		c1.put(new ComplexityType(Type.Static, Base.Degree), 2067);
		
		c2 = new ComplexityMap();
		c2.put(new ComplexityType(Type.Static, Base.Degree), 2067);
		c2.put(new ComplexityType(Type.Linear, Base.EdgeSize), 2067);
		
		assertFirstIsSmallerThanSecond(c1, c2);
	}
	
	@Test
	public void checkSortingCasesInTheTree_A() {
		ComplexityMap c1, c2;
		
		c1 = new ComplexityMap();
		c1.put(new ComplexityType(Type.Static, Base.Degree), 2067);
		
		c2 = new ComplexityMap();
		c2.put(new ComplexityType(Type.Static, Base.Degree), 2067);
		c2.put(new ComplexityType(Type.Linear, Base.EdgeSize), 2067);
		
		TreeMap<ComplexityMap, GraphDataStructure> tree = new TreeMap<>();
		tree.put(c2, null);
		tree.put(c1, null);
		
		assertEquals(2, tree.size());
		assertEquals(c1, tree.pollFirstEntry().getKey());
		assertEquals(c2, tree.pollFirstEntry().getKey());
	}
	
	@Test
	public void checkSortingCasesInTheTree_B() {
		ComplexityMap c1, c2;
			
		c1 = new ComplexityMap();
		c1.put(new ComplexityType(Type.Static, Base.Degree), 8158);
		c1.put(new ComplexityType(Type.Linear, Base.EdgeSize), 4071);
		
		c2 = new ComplexityMap();
		c2.put(new ComplexityType(Type.Linear, Base.NodeSize), 8158);
		c2.put(new ComplexityType(Type.Linear, Base.EdgeSize), 4071);
		
		TreeMap<ComplexityMap, GraphDataStructure> tree = new TreeMap<>();
		tree.put(new ComplexityMap(), null);
		tree.put(c1, null);
		tree.put(c2, null);
		
		assertEquals(3, tree.size());
		tree.pollFirstEntry();
		assertEquals(c1, tree.pollFirstEntry().getKey());
		assertEquals(c2, tree.pollFirstEntry().getKey());
	}
	
	@Test
	public void checkSortingCasesInTheTree_C() {
		ComplexityMap c1, c2, c3;
			
		c1 = new ComplexityMap();
		c1.put(new ComplexityType(Type.Static, Base.Degree), 246016);
		
		c2 = new ComplexityMap();
		c2.put(new ComplexityType(Type.Static, Base.Degree), 246016);		
		c2.put(new ComplexityType(Type.Linear, Base.EdgeSize), 20196);
		
		c3 = new ComplexityMap();
		c3.put(new ComplexityType(Type.Static, Base.Degree), 225820);
		c3.put(new ComplexityType(Type.Linear, Base.EdgeSize), 40392);
		
		TreeMap<ComplexityMap, GraphDataStructure> tree = new TreeMap<>();
		tree.put(c3, null);
		tree.put(c1, null);
		tree.put(c2, null);
		
		assertEquals(3, tree.size());
		assertEquals(c1, tree.pollFirstEntry().getKey());
		assertEquals(c2, tree.pollFirstEntry().getKey());
	}	
	
	@Test
	public void checkSortingCasesInTheTree_D() {
		ComplexityMap c1, c2, c3, c4;
			
		c1 = new ComplexityMap();
		c1.put(new ComplexityType(Type.Static, Base.Degree), 1);
		
		c2 = new ComplexityMap();
		c2.put(new ComplexityType(Type.Static, Base.Degree), 1);		
		c2.put(new ComplexityType(Type.Linear, Base.EdgeSize), 1);

		c3 = new ComplexityMap();
		c3.put(new ComplexityType(Type.Static, Base.Degree), 1);		
		c3.put(new ComplexityType(Type.Linear, Base.EdgeSize), 2);		
		
		c4 = new ComplexityMap();
		c4.put(new ComplexityType(Type.Static, Base.Degree), 1);		
		c4.put(new ComplexityType(Type.Linear, Base.EdgeSize), 1);	
				
		TreeMap<ComplexityMap, GraphDataStructure> tree = new TreeMap<>();
		tree.put(c1, null);
		tree.put(c2, null);
		tree.put(c3, null);
		tree.put(c4, null);
		
		assertEquals(3, tree.size());
		assertEquals(c1, tree.pollFirstEntry().getKey());
		assertEquals(c2, tree.pollFirstEntry().getKey());
	}
	
	@Test
	public void checkSortingCasesInTheTree_E() {
		ComplexityMap c1, c2;
			
		c1 = new ComplexityMap();
		c1.put(new ComplexityType(Type.Static, Base.Degree), 1);
		c1.put(new ComplexityType(Type.Linear, Base.Degree), 5);
		
		c2 = new ComplexityMap();
		c2.put(new ComplexityType(Type.Static, Base.Degree), 20);		
		c2.put(new ComplexityType(Type.Linear, Base.Degree), 5);
			
		TreeMap<ComplexityMap, GraphDataStructure> tree = new TreeMap<>();
		tree.put(c1, null);
		tree.put(c2, null);
		System.out.println(tree.keySet());
		
		assertEquals(2, tree.size());
		assertEquals(c1, tree.pollFirstEntry().getKey());
		assertEquals(c2, tree.pollFirstEntry().getKey());
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

	private <T extends java.lang.Comparable<T>> void assertFirstIsSmallerThanSecond(T one,
			T two) {
		/**
		 * As a remark: a.compareTo(b) returns the following results:
		 * 		-1 iff a < b
		 * 		0  iff a == b
		 * 		1  iff a > b
		 * 
		 * This assertion should check whether `one` is smaller than `two`,
		 * so the assertion should check for the result -1.
		 */
		assertEquals(one + " != " + one, 0, one.compareTo(one));
		assertEquals(two + " != " + two, 0, two.compareTo(two));
		assertEquals(one + " !< " + two, -1, one.compareTo(two));
		assertEquals(one + " !> " + two, +1, two.compareTo(one));
	}

}
