package dna.graph.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import dna.graph.IWeighted;
import dna.graph.datastructures.DArray;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedDoubleWeightedEdge;
import dna.graph.nodes.DirectedDoubleWeightedNode;
import dna.util.MathHelper;

public class SingleTests {

	@Test
	public void testMathHelper() {
		assertEquals(0, MathHelper.parseInt("0"));
		assertEquals(1, MathHelper.parseInt("1"));
		assertEquals(10, MathHelper.parseInt("10"));
		assertEquals(100, MathHelper.parseInt("100"));

		assertEquals(1, MathHelper.parseInt("1abc###"));
		assertEquals(1, MathHelper.parseInt("1abc###0012"));
		assertEquals(10, MathHelper.parseInt("10abc###0012"));
		assertEquals(10, MathHelper.parseInt("10.abc###0012"));
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void checkWeightedNode() {
		GraphDataStructure gds = new GraphDataStructure(DArray.class,
				DArray.class, DArray.class, DirectedDoubleWeightedNode.class, DirectedDoubleWeightedEdge.class);
		IWeighted n = gds.newWeightedNode(1, 1d);
		assertTrue(n instanceof DirectedDoubleWeightedNode);
		assertEquals(1d, n.getWeight());

		boolean caughtException = false;
		try {
			IWeighted n2 = gds.newWeightedNode(2, "Test");
		} catch (RuntimeException e) {
			caughtException = true;
		}
		assertTrue("Node n2 instantiated with incompatible signature", caughtException);
	}
}
