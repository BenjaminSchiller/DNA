package genericsWithTest.Tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import genericsWithTest.Edge;
import genericsWithTest.Element;
import genericsWithTest.Node;
import genericsWithTest.DataStructures.DArrayList;
import genericsWithTest.DataStructures.DHashSet;
import genericsWithTest.DataStructures.DataStructure;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
@SuppressWarnings("rawtypes")
public class DatastructureTester {

	private Element element;
	private DataStructure dataStructure;

	public DatastructureTester(Class<?> d, Class<?> e) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this.dataStructure = (DataStructure) d.getConstructor(Class.class).newInstance(e);
		this.element = (Element) e.getConstructor().newInstance();
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
		Element dummy = element.getDummy();
		dataStructure.add(dummy);
		assertTrue(dataStructure.contains(dummy));
		assertEquals(1, dataStructure.size());
	}

}
