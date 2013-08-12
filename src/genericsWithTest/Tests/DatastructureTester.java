package genericsWithTest.Tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import genericsWithTest.DataStructures.DArrayList;
import genericsWithTest.DataStructures.DHashSet;
import genericsWithTest.DataStructures.DataStructure;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import Graph.Edge;
import Graph.Element;
import Graph.IElement;
import Graph.Node;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
@SuppressWarnings("rawtypes")
public class DatastructureTester {

	private IElement element;
	private DataStructure dataStructure;
	private Class<? extends Element> elementClass;

	public DatastructureTester(Class<?> d, Class<? extends Element> e) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this.dataStructure = (DataStructure) d.getConstructor(Class.class).newInstance(e);
		this.elementClass = e;
		this.element = (IElement) e.getConstructor().newInstance();
	}

    @Parameterized.Parameters(name="{0} {1}")
	public static Collection testPairs() {
    	Class[] dataStructures = {DArrayList.class, DHashSet.class};
    	Class[] elements = {Node.class, Edge.class};
    	
    	ArrayList result = new ArrayList<>();
    	for ( Class sD: dataStructures ) {
    		for ( Class sE: elements) {
    			result.add(new Object[]{sD, sE});
    		}
    	}
    	
		return result;
	}

	@Test
	public void checkAdd() {
		IElement dummy = mock(elementClass);
		dataStructure.add(dummy);
		assertTrue(dataStructure.contains(dummy));
		assertEquals(1, dataStructure.size());
	}

}
