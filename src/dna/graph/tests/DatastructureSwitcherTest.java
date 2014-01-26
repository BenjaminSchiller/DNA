package dna.graph.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import dna.graph.datastructures.DEmpty;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.INodeListDatastructureReadable;
import dna.graph.datastructures.IReadable;
import dna.graph.nodes.DirectedNode;

@RunWith(Parameterized.class)
public class DatastructureSwitcherTest {
	private Class<? extends IDataStructure> oldDS;
	private Class<? extends IDataStructure> newDS;

	public DatastructureSwitcherTest(Class<? extends IDataStructure> oldDS,
			Class<? extends IDataStructure> newDS)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		this.oldDS = oldDS;
		this.newDS = newDS;
	}

	@SuppressWarnings("rawtypes")
	@Parameterized.Parameters(name = "{0} {1}")
	public static Collection<Object> testPairs() {
		ArrayList<Object> result = new ArrayList<>();
		for (Class oldDS : GlobalTestParameters.dataStructures) {
			for (Class newDS : GlobalTestParameters.dataStructures) {
				if (oldDS.equals(DEmpty.class))
					continue;
				result.add(new Object[] { oldDS, newDS });
			}
		}
		return result;
	}

	@Test
	public void checkSimpleConversion() {
		IReadable oldDSInstance = (IReadable) this.instantiate(oldDS);
		DirectedNode n = mock(DirectedNode.class);
		oldDSInstance.add(n);
		assertTrue(oldDSInstance.contains(n));
		assertEquals(1, oldDSInstance.size());
		assertTrue(oldDSInstance.getClass().equals(oldDS));

		IDataStructure newDSInstance = oldDSInstance
				.switchTo(instantiate(newDS));
		assertTrue(newDSInstance.getClass().equals(newDS));
		if (!newDS.equals(DEmpty.class)) {
			assertTrue(newDSInstance.contains(n));
			assertEquals(1, newDSInstance.size());
		}
	}

	private IDataStructure instantiate(Class<? extends IDataStructure> ds) {
		IDataStructure res = null;
		try {
			res = ds.getConstructor(ListType.class, Class.class).newInstance(
					ListType.GlobalNodeList, DirectedNode.class);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			try {
				res = ds.getConstructor(ListType.class).newInstance(
						ListType.GlobalNodeList);
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return res;
	}
}
