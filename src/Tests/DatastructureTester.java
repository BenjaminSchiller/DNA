package Tests;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import Utils.Rand;
import DataStructures.*;
import Graph.Element;
import Graph.IElement;
import Graph.Edges.*;
import Graph.Nodes.*;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
@SuppressWarnings("rawtypes")
public class DatastructureTester {

	private DataStructure dataStructure;
	private Class<? extends Element> elementClass;

	public DatastructureTester(Class<?> d, Class<? extends Element> e) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this.dataStructure = (DataStructure) d.getConstructor(Class.class).newInstance(e);
		this.elementClass = e;
	}

    @Parameterized.Parameters(name="{0} {1}")
	public static Collection testPairs() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    	Class[] dataStructures = {DArrayList.class, DArray.class, DHashSet.class};
    	Class[] elements = {DirectedNode.class, UndirectedNode.class, UndirectedEdge.class, DirectedEdge.class};
    	
    	ArrayList<Object> result = new ArrayList<>();
    	for ( Class sD: dataStructures ) {
    		for ( Class sE: elements) {
    			// Check whether we can store an object of sE in sD
    			DataStructure ds = (DataStructure) sD.getConstructor(Class.class).newInstance(sE);
    			if ( !ds.canStore(sE)) continue;
    			result.add(new Object[]{sD, sE});
    		}
    	}
    	
		return result;
	}

	@Test
	public void checkAddAndRemove() {
		IElement dummy = mock(elementClass);
		assertFalse(dataStructure.contains(dummy));
		assertEquals(0, dataStructure.size());
		dataStructure.add(dummy);
		assertTrue(dataStructure.contains(dummy));
		assertEquals(1, dataStructure.size());
		dataStructure.remove(dummy);
		assertFalse(dataStructure.contains(dummy));
		assertEquals(0, dataStructure.size());
	}
	
	@Test
	public void checkMaxNodeIndexOnAddAndRemove() {
		assumeTrue(dataStructure instanceof INodeListDatastructure);
		INodeListDatastructure tempDS = (INodeListDatastructure) dataStructure;
		
		IElement[] dummies = new IElement[10];
		for (int i = 0; i < dummies.length; i++) {
			dummies[i] = mock(this.elementClass);
			when(dummies[i].getIndex()).thenReturn(i);
			tempDS.add(dummies[i]);
			assertEquals(i, tempDS.getMaxNodeIndex());
		}
		
		/*
		 * Since we don't know if the data structure really
		 * used the maxNodeIndex property and did not return
		 * the count property, we will run some more
		 * sophisticated tests now
		 */
		
		IElement[] secondDummies = new IElement[10];
		int[] prevIndex = new int[10];
		int lastIndex = tempDS.getMaxNodeIndex();
		
		for (int i = 0; i < secondDummies.length; i++) {
			secondDummies[i] = mock(this.elementClass);
			prevIndex[i] = lastIndex;
			lastIndex = lastIndex + Rand.rand.nextInt(5000) + 3;
			when(secondDummies[i].getIndex()).thenReturn(lastIndex);
			tempDS.add(secondDummies[i]);
			assertEquals(lastIndex, tempDS.getMaxNodeIndex());
		}
		
		for (int i = secondDummies.length - 1; i >= 0; i--) {
			tempDS.remove(secondDummies[i]);
			assertEquals(prevIndex[i], tempDS.getMaxNodeIndex());
		}
		
		assertEquals(dummies[dummies.length-1].getIndex(), tempDS.getMaxNodeIndex());
	}
	
	@Test
	public void checkCorrectSize() {
		IElement[] dummies = new IElement[10];
		for (int i = 0; i < dummies.length; i++) {
			dummies[i] = mock(this.elementClass);
			when(dummies[i].getIndex()).thenReturn(i);
			dataStructure.add(dummies[i]);
		}
		assertEquals(dummies.length, dataStructure.size());		
	}
	
	@Test
	public void checkGetNode() {
		assumeTrue(dataStructure instanceof INodeListDatastructure);
		INodeListDatastructure tempDS = (INodeListDatastructure) dataStructure;
		
		IElement dummy = mock(this.elementClass);
		when(dummy.getIndex()).thenReturn(42);
		tempDS.add(dummy);
		
		assertEquals(null, tempDS.get(43));
		assertEquals(42, dummy.getIndex());
		assertEquals(dummy, tempDS.get(42));
	}
	
	@Test
	public void checkGetElements() {
		int size = 20;
		IElement singleDummy;
		
		ArrayList<IElement> dummies = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			singleDummy = mock(this.elementClass);
			
			/*
			 * Nodes are stored with an index, so set it please!
			 */
			if ( singleDummy instanceof Node ) {
				when(singleDummy.getIndex()).thenReturn(i);
			}
			
			dataStructure.add(singleDummy);
			dummies.add(singleDummy);
		}
		
		Collection<IElement> elements = dataStructure.getElements();
		
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
			if ( singleDummy instanceof Node ) {
				when(singleDummy.getIndex()).thenReturn(i);
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
			if ( singleDummy instanceof Node ) {
				when(singleDummy.getIndex()).thenReturn(i);
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

	@Ignore @Test
	public void checkGetNodeWithGaps() {
		assumeTrue(dataStructure instanceof INodeListDatastructure);
		INodeListDatastructure tempDS = (INodeListDatastructure) dataStructure;
		
		IElement dummy = mock(this.elementClass);
		when(dummy.getIndex()).thenReturn(42);
		tempDS.add(dummy);
		
		assertEquals(null, tempDS.get(43));
		assertEquals(42, dummy.getIndex());
		assertEquals(dummy, tempDS.get(42));
		
		/*
		 * Magic done here: through giving the
		 * mock another index, we can search for an
		 * element which has the proper index, but is not
		 * at the indexed position -- the former
		 * arraylist implementation in DNA did this 
		 */
		
		when(dummy.getIndex()).thenReturn(23);
		assertEquals(23, dummy.getIndex());
		assertEquals(null, tempDS.get(42));
	}
	
	@Test
	public void checkGetRandom() {
		IElement[] dummies = new IElement[10];
		for (int i = 0; i < dummies.length; i++) {
			dummies[i] = mock(elementClass);
			dataStructure.add(dummies[i]);
		}
		
		IElement random;
		for ( int i = 0; i < 2 * dummies.length; i++) {
			random = dataStructure.getRandom();
			assertTrue(dataStructure.contains(random));
		}
	}
	
}
