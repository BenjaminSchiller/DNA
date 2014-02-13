package dna.graph.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.datastructures.DArray;
import dna.graph.datastructures.DEmpty;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.IEdgeListDatastructure;
import dna.graph.datastructures.IEdgeListDatastructureReadable;
import dna.graph.datastructures.INodeListDatastructure;
import dna.graph.datastructures.INodeListDatastructureReadable;
import dna.graph.datastructures.IReadable;
import dna.graph.edges.DirectedEdge;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.random.RandomGraph;
import dna.graph.nodes.DirectedNode;

@RunWith(Parameterized.class)
public class DatastructureSwitcherTest {
	private Class<? extends IDataStructure> oldDS;
	private Class<? extends IDataStructure> newDS;
	private EnumMap<ListType, Class<? extends IDataStructure>> listTypes;

	public DatastructureSwitcherTest(Class<? extends IDataStructure> oldDS,
			Class<? extends IDataStructure> newDS)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		this.oldDS = oldDS;
		this.newDS = newDS;
	}

	@Before
	public void instantiateListType() {
		listTypes = new EnumMap<ListType, Class<? extends IDataStructure>>(
				ListType.class);
		for (ListType lt : ListType.values()) {
			listTypes.put(lt, DArray.class);
		}
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
		IReadable oldDSInstance = (IReadable) this.instantiate(
				ListType.GlobalNodeList, oldDS);
		DirectedNode n = mock(DirectedNode.class);
		oldDSInstance.add(n);
		assertTrue(oldDSInstance.contains(n));
		assertEquals(1, oldDSInstance.size());
		assertTrue(oldDSInstance.getClass().equals(oldDS));

		IDataStructure newDSInstance = oldDSInstance.switchTo(instantiate(
				ListType.GlobalNodeList, newDS));
		assertTrue(newDSInstance.getClass().equals(newDS));
		if (!newDS.equals(DEmpty.class)) {
			assertTrue(newDSInstance.contains(n));
			assertEquals(1, newDSInstance.size());
		}
	}

	@Test
	public void checkChangeOfGlobalEdgeList() throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		assumeTrue(IEdgeListDatastructureReadable.class.isAssignableFrom(oldDS));
		assumeTrue(IEdgeListDatastructure.class.isAssignableFrom(newDS));

		listTypes.put(ListType.GlobalEdgeList, oldDS);
		GraphDataStructure gdsOld = new GraphDataStructure(listTypes,
				DirectedNode.class, DirectedEdge.class);
		GraphGenerator gg = new RandomGraph(gdsOld, 200, 100);
		Graph g = gg.generate();

		Field innerGlobalEdgeList = Graph.class.getDeclaredField("edges");
		innerGlobalEdgeList.setAccessible(true);
		IEdgeListDatastructure listOld = (IEdgeListDatastructure) innerGlobalEdgeList
				.get(g);
		assertTrue(listOld.getClass().equals(oldDS));

		int edgesInGraph = g.getEdgeCount();

		g.switchDataStructure(ListType.GlobalEdgeList,
				instantiate(ListType.GlobalEdgeList, newDS));
		listOld = (IEdgeListDatastructure) innerGlobalEdgeList.get(g);
		assertTrue(listOld.getClass().equals(newDS));
		if (!newDS.equals(DEmpty.class)) {
			assertEquals(edgesInGraph, listOld.size());
		}
	}

	@Test
	public void checkChangeOfGlobalNodeList() throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		assumeTrue(INodeListDatastructureReadable.class.isAssignableFrom(oldDS));
		assumeTrue(INodeListDatastructure.class.isAssignableFrom(newDS));

		listTypes.put(ListType.GlobalNodeList, oldDS);
		GraphDataStructure gdsOld = new GraphDataStructure(listTypes,
				DirectedNode.class, DirectedEdge.class);
		GraphGenerator gg = new RandomGraph(gdsOld, 200, 100);
		Graph g = gg.generate();

		Field innerGlobalNodeList = Graph.class.getDeclaredField("nodes");
		innerGlobalNodeList.setAccessible(true);
		INodeListDatastructure listOld = (INodeListDatastructure) innerGlobalNodeList
				.get(g);
		assertTrue(listOld.getClass().equals(oldDS));

		int nodesInGraph = g.getNodeCount();

		g.switchDataStructure(ListType.GlobalNodeList,
				instantiate(ListType.GlobalNodeList, newDS));
		listOld = (INodeListDatastructure) innerGlobalNodeList.get(g);
		assertTrue(listOld.getClass().equals(newDS));
		if (!newDS.equals(DEmpty.class)) {
			assertEquals(nodesInGraph, listOld.size());
		}
	}

	private IDataStructure instantiate(ListType lt,
			Class<? extends IDataStructure> ds) {

		Class<? extends IElement> storedElementClass = null;
		switch (lt) {
		case GlobalEdgeList:
		case LocalEdgeList:
			storedElementClass = DirectedEdge.class;
			break;
		case GlobalNodeList:
		case LocalNodeList:
			storedElementClass = DirectedNode.class;
			break;
		}

		IDataStructure res = null;
		try {
			res = ds.getConstructor(ListType.class, Class.class).newInstance(
					lt, storedElementClass);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			try {
				res = ds.getConstructor(ListType.class).newInstance(lt);
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e1) {
				e1.printStackTrace();
			}
		}
		return res;
	}
}
