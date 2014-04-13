package dna.graph.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import dna.graph.Element;
import dna.graph.IElement;
import dna.graph.datastructures.DArray;
import dna.graph.datastructures.DEmpty;
import dna.graph.datastructures.DataStructure;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.IEdgeListDatastructure;
import dna.graph.datastructures.INodeListDatastructure;
import dna.graph.datastructures.INodeListDatastructureReadable;
import dna.graph.datastructures.IReadable;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weightsNew.DoubleWeight;
import dna.graph.weightsNew.IntWeight;
import dna.graph.weightsNew.Weight.WeightSelection;
import dna.util.Rand;

@RunWith(Parameterized.class)
@SuppressWarnings("rawtypes")
public class DatastructureTester {

	private DataStructure dataStructure;
	private Class<? extends Element> elementClass;

	public DatastructureTester(Class<?> d, Class<? extends Element> e)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		this.dataStructure = (DataStructure) d.getConstructor(ListType.class,
				Class.class).newInstance(null, e);
		this.elementClass = e;
	}

	@Parameterized.Parameters(name = "{0} {1}")
	public static Collection testPairs() throws InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {

		ArrayList<Object> result = new ArrayList<>();
		for (Class sD : GlobalTestParameters.dataStructures) {
			for (Class sE : GlobalTestParameters.elementClasses) {
				// Check whether we can store an object of sE in sD
				if ((Node.class.isAssignableFrom(sE) && !INodeListDatastructure.class
						.isAssignableFrom(sD))
						|| (Edge.class.isAssignableFrom(sE) && !IEdgeListDatastructure.class
								.isAssignableFrom(sD)))
					continue;
				if (sD == DEmpty.class)
					continue;
				result.add(new Object[] { sD, sE });
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void checkOnlyOneDatatype() {
		IElement dummy;
		int exceptionCounter = 0;

		/*
		 * Checking whether add() throws the exception we want it to throw does
		 * not cover all cases here - we want to ensure that really only
		 * instances of one single element class can be stored, and *all* other
		 * instance types are rejected
		 * 
		 * The exception counter will be increased for each *allowed* class and
		 * each *rejected* class. There should be no gap in between...
		 */

		for (Class otherElementClass : GlobalTestParameters.elementClasses) {
			if (elementClass.isAssignableFrom(otherElementClass)) {
				assertTrue("Datastructure " + dataStructure.getClass() + "["
						+ this.elementClass + "] can't store "
						+ otherElementClass + " even if it should",
						dataStructure.canStore(otherElementClass));
				exceptionCounter++;
				continue;
			}
			dummy = (IElement) mock(otherElementClass);

			boolean exceptionThrown = false;
			assertFalse("Datastructure " + dataStructure.getClass() + "["
					+ this.elementClass + "/" + dataStructure.getDataType()
					+ "] can store " + otherElementClass
					+ " even if it shouldn't",
					dataStructure.canStore(otherElementClass));

			try {
				dataStructure.add(dummy);
			} catch (RuntimeException e) {
				exceptionCounter++;
				exceptionThrown = true;
			}

			assertTrue("Whoopsie - whats up with " + otherElementClass,
					exceptionThrown);
			/*
			 * So, this is thrown when the datastructure is not allowed to be
			 * added to that list, but no exception is thrown. Common error:
			 * add() has not called canAdd()
			 */
		}

		assertEquals(GlobalTestParameters.elementClasses.length,
				exceptionCounter);
	}

	@Test
	public void checkAddAndRemove() {
		IElement dummy = mock(elementClass);
		if (dummy instanceof Edge) {
			when(((Edge) dummy).getHashString()).thenReturn("1");
		}		
		assertFalse(dataStructure.contains(dummy));
		assertEquals(0, dataStructure.size());
		assertTrue(dataStructure.add(dummy));
		assertTrue(dataStructure.contains(dummy));
		assertEquals(1, dataStructure.size());
		assertTrue(dataStructure.remove(dummy));
		assertFalse(dataStructure.contains(dummy));
		assertEquals(0, dataStructure.size());
	}

	@Test
	public void checkNoOverwriting() {
		assumeTrue(dataStructure instanceof INodeListDatastructureReadable);
		IReadable tempDS = (IReadable) dataStructure;

		IElement dummy1 = mock(elementClass);
		IElement dummy2 = mock(elementClass);

		if (Node.class.isAssignableFrom(elementClass)) {
			when(((Node) dummy1).getIndex()).thenReturn(1);
			when(((Node) dummy2).getIndex()).thenReturn(2);
		}
		if (Edge.class.isAssignableFrom(elementClass)) {
			when(((Edge) dummy1).getHashString()).thenReturn("1");
			when(((Edge) dummy2).getHashString()).thenReturn("2");
		}

		assertFalse(tempDS.contains(dummy1));
		assertFalse(tempDS.contains(dummy2));
		assertEquals(0, tempDS.size());

		assertTrue(tempDS.add(dummy1));
		assertTrue(tempDS.contains(dummy1));
		assertEquals(1, tempDS.size());

		assertTrue(tempDS.add(dummy2));
		assertTrue(tempDS.contains(dummy2));
		assertEquals(2, tempDS.size());

		assertTrue(tempDS.remove(dummy1));
		assertFalse(tempDS.contains(dummy1));
		assertEquals(1, tempDS.size());

		assertTrue(tempDS.add(dummy1));
		assertTrue(tempDS.contains(dummy1));
		assertEquals(2, tempDS.size());

		int count = 0;
		for (@SuppressWarnings("unused")
		IElement e : tempDS.getElements()) {
			count++;
		}
		assertEquals(tempDS.size(), count);
	}

	@Test
	public void checkMaxNodeIndexOnAddAndRemove() {
		assumeTrue(dataStructure instanceof INodeListDatastructure);
		assumeTrue(Node.class.isAssignableFrom(elementClass));
		INodeListDatastructure tempDS = (INodeListDatastructure) dataStructure;

		Node[] dummies = new Node[10];
		for (int i = 0; i < dummies.length; i++) {
			dummies[i] = (Node) mock(this.elementClass);
			when(dummies[i].getIndex()).thenReturn(i);
			assertTrue(tempDS.add(dummies[i]));
			assertEquals(i, tempDS.getMaxNodeIndex());
		}

		/*
		 * Since we don't know if the data structure really used the
		 * maxNodeIndex property and did not return the count property, we will
		 * run some more sophisticated tests now
		 */

		Node[] secondDummies = new Node[10];
		int[] prevIndex = new int[10];
		int lastIndex = tempDS.getMaxNodeIndex();

		for (int i = 0; i < secondDummies.length; i++) {
			secondDummies[i] = (Node) mock(this.elementClass);
			prevIndex[i] = lastIndex;
			lastIndex = lastIndex + Rand.rand.nextInt(i + 1) + 1;
			when(secondDummies[i].getIndex()).thenReturn(lastIndex);
			assertTrue(tempDS.add(secondDummies[i]));
			assertEquals(lastIndex, tempDS.getMaxNodeIndex());
		}

		for (int i = secondDummies.length - 1; i >= 0; i--) {
			assertTrue(tempDS.remove(secondDummies[i]));
			assertEquals(prevIndex[i], tempDS.getMaxNodeIndex());
		}

		assertEquals(dummies[dummies.length - 1].getIndex(),
				tempDS.getMaxNodeIndex());
	}

	@Test
	public void checkCorrectSize() {
		IElement[] dummies = new IElement[10];
		for (int i = 0; i < dummies.length; i++) {
			dummies[i] = mock(this.elementClass);
			if (Node.class.isAssignableFrom(this.elementClass))
				when(((Node) dummies[i]).getIndex()).thenReturn(i);
			if (dummies[i] instanceof Edge)
				when(((Edge) dummies[i]).getHashString()).thenReturn("" + i);
			assertTrue(dataStructure.add(dummies[i]));
		}
		assertEquals(dummies.length, dataStructure.size());
	}

	@Test
	public void checkGetNode() {
		assumeTrue(dataStructure instanceof INodeListDatastructureReadable);
		assumeTrue(Node.class.isAssignableFrom(this.elementClass));
		INodeListDatastructureReadable tempDS = (INodeListDatastructureReadable) dataStructure;

		Node dummy = (Node) mock(this.elementClass);
		when(dummy.getIndex()).thenReturn(42);
		assertTrue(tempDS.add(dummy));

		assertEquals(null, tempDS.get(43));
		assertEquals(42, dummy.getIndex());
		assertEquals(dummy, tempDS.get(42));
	}

	@Test
	public void checkGetElements() {
		assumeTrue(dataStructure instanceof INodeListDatastructureReadable);
		IReadable tempDS = (IReadable) dataStructure;

		int size = 20;
		IElement singleDummy;

		ArrayList<IElement> dummies = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			singleDummy = mock(this.elementClass);

			/*
			 * Nodes are stored with an index, so set it please!
			 */
			if (singleDummy instanceof Node) {
				when(((Node) singleDummy).getIndex()).thenReturn(i);
			}
			if (singleDummy instanceof Edge) {
				when(((Edge) singleDummy).getHashString()).thenReturn("" + i);
			}			

			assertTrue(tempDS.add(singleDummy));
			dummies.add(singleDummy);
		}

		Collection<IElement> elements = tempDS.getElements();

		/*
		 * Check for the proper size and content
		 */
		assertEquals(dummies.size(), elements.size());
		assertTrue(dummies.containsAll(elements));
		assertTrue(elements.containsAll(dummies));
	}

	@Test
	public void checkResizement() {
		int initialSize = dataStructure.getDefaultSize();
		int goalSize = 10 * initialSize;

		assertEquals(0, dataStructure.size());

		IElement singleDummy;

		for (int i = 0; i < goalSize; i++) {
			singleDummy = mock(this.elementClass);

			/*
			 * Nodes are stored with an index, so set it please!
			 */
			if (singleDummy instanceof Node) {
				when(((Node) singleDummy).getIndex()).thenReturn(i);
			}
			if (singleDummy instanceof Edge) {
				when(((Edge) singleDummy).getHashString()).thenReturn("" + i);
			}

			dataStructure.add(singleDummy);
		}
		assertEquals(goalSize, dataStructure.size());
	}

	@Test
	public void checkIterator() {
		int size = 20;
		IElement singleDummy;

		ArrayList<IElement> dummies = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			singleDummy = mock(this.elementClass);

			/*
			 * Nodes are stored with an index, so set it please!
			 */
			if (singleDummy instanceof Node) {
				when(((Node) singleDummy).getIndex()).thenReturn(i);
			}
			if (singleDummy instanceof Edge) {
				when(((Edge) singleDummy).getHashString()).thenReturn("" + i);
			}

			dataStructure.add(singleDummy);
			dummies.add(singleDummy);
		}

		Iterator<IElement> elemIterator = dataStructure.iterator();
		while (elemIterator.hasNext()) {
			singleDummy = elemIterator.next();
			assertTrue(dummies.contains(singleDummy));
			dummies.remove(singleDummy);
		}
		assertTrue(dummies.isEmpty());
	}

	@Test
	public void checkNullsInGetElements() {
		assumeTrue(dataStructure instanceof INodeListDatastructureReadable);
		INodeListDatastructureReadable tempDS = (INodeListDatastructureReadable) dataStructure;

		dataStructure.reinitializeWithSize(10);
		for (IElement singleDummy : tempDS.getElements()) {
			assertNotNull(singleDummy);
		}
	}

	@Test
	public void checkNullsInIterator() {
		IElement singleDummy;

		dataStructure.reinitializeWithSize(10);
		Iterator<IElement> elemIterator = dataStructure.iterator();
		while (elemIterator.hasNext()) {
			singleDummy = elemIterator.next();
			assertNotNull(singleDummy);
		}
	}

	@Test
	public void checkGetNodeWithGaps() {
		/*
		 * Don't run this test for non-node datastructures and non-nodes
		 */
		assumeTrue(dataStructure instanceof INodeListDatastructureReadable);
		assumeTrue(Node.class.isAssignableFrom(this.elementClass));

		INodeListDatastructureReadable tempDS = (INodeListDatastructureReadable) dataStructure;

		Node dummy1 = (Node) mock(this.elementClass);
		when(dummy1.getIndex()).thenReturn(0);
		assertTrue(tempDS.add(dummy1));

		assertEquals(null, tempDS.get(1));
		assertEquals(0, dummy1.getIndex());
		assertEquals(dummy1, tempDS.get(0));

		Node dummy2 = (Node) mock(this.elementClass);
		when(dummy2.getIndex()).thenReturn(1);
		assertTrue(tempDS.add(dummy2));

		assertEquals(1, dummy2.getIndex());
		assertEquals(dummy2, tempDS.get(1));

		// Remove first node and get second one
		assertTrue(tempDS.remove(dummy1));
		assertEquals(1, tempDS.size());
		assertEquals(dummy2, tempDS.get(1));
	}

	@Test
	public void checkGetRandom() {
		assumeTrue(dataStructure instanceof INodeListDatastructureReadable);
		IReadable tempDS = (IReadable) dataStructure;

		IElement[] dummies = new IElement[10];
		for (int i = 0; i < dummies.length; i++) {
			dummies[i] = mock(elementClass);
			if (dummies[i] instanceof Edge)
				when(((Edge) dummies[i]).getHashString()).thenReturn("" + i);
			tempDS.add(dummies[i]);
		}

		IElement random;
		for (int i = 0; i < 2 * dummies.length; i++) {
			random = tempDS.getRandom();
			assertNotNull(random);
			assertTrue(tempDS.contains(random));
		}
	}

	@Test
	public void checkDuplicateCalls() {
		IElement dummy = getDummy(elementClass);

		assertTrue(dataStructure.add(dummy));
		assertFalse(dataStructure.add(dummy));

		assertTrue(dataStructure.remove(dummy));
		assertFalse(dataStructure.remove(dummy));
	}

	@SuppressWarnings("unchecked")
	private IElement getDummy(Class<? extends IElement> elementClass) {
		IElement dummy = null;
		GraphDataStructure tempGDS;
		EnumMap<ListType, Class<? extends IDataStructure>> listtypes = GraphDataStructure
				.getList(ListType.GlobalNodeList, DArray.class,
						ListType.GlobalEdgeList, DArray.class);

		if (Node.class.isAssignableFrom(elementClass)) {
			tempGDS = new GraphDataStructure(listtypes,
					(Class<? extends Node>) elementClass, null,
					DoubleWeight.class, WeightSelection.RandTrim1,
					IntWeight.class, WeightSelection.RandPos100);
			dummy = tempGDS.newNodeInstance(42);
		} else if (DirectedEdge.class.isAssignableFrom(elementClass)) {
			tempGDS = new GraphDataStructure(listtypes, null,
					(Class<? extends Edge>) elementClass, DoubleWeight.class,
					WeightSelection.RandTrim1, IntWeight.class,
					WeightSelection.RandPos100);

			DirectedNode n1 = new DirectedNode(1, tempGDS);
			DirectedNode n2 = new DirectedNode(2, tempGDS);

			dummy = tempGDS.newEdgeInstance(n1, n2);
		} else if (UndirectedEdge.class.isAssignableFrom(elementClass)) {
			tempGDS = new GraphDataStructure(listtypes, null,
					(Class<? extends Edge>) elementClass, DoubleWeight.class,
					WeightSelection.RandTrim1, IntWeight.class,
					WeightSelection.RandPos100);

			UndirectedNode n1 = new UndirectedNode(1, tempGDS);
			UndirectedNode n2 = new UndirectedNode(2, tempGDS);

			dummy = tempGDS.newEdgeInstance(n1, n2);
		} else {
			fail("Cannot identify " + elementClass);
		}
		return dummy;
	}

}
