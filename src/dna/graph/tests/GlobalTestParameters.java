package dna.graph.tests;

import com.google.common.collect.ObjectArrays;

import dna.graph.datastructures.DArray;
import dna.graph.datastructures.DArrayDeque;
import dna.graph.datastructures.DArrayList;
import dna.graph.datastructures.DEmpty;
import dna.graph.datastructures.DHashArrayList;
import dna.graph.datastructures.DHashMap;
import dna.graph.datastructures.DHashSet;
import dna.graph.datastructures.DHashTable;
import dna.graph.datastructures.DLinkedList;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.generators.canonical.CliqueGraph;
import dna.graph.generators.canonical.RingGraph;
import dna.graph.generators.evolvingNetworks.BarabasiAlbertGraph;
import dna.graph.generators.random.RandomGraph;
import dna.graph.generators.util.EmptyGraph;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.DirectedWeightedNode;
import dna.graph.nodes.UndirectedNode;
import dna.graph.nodes.UndirectedWeightedNode;

public class GlobalTestParameters {
	public static final Class[] nodeTypes = { UndirectedNode.class,
			UndirectedWeightedNode.class, DirectedNode.class,
			DirectedWeightedNode.class };

	public static final Class[] edgeTypes = { UndirectedEdge.class,
			UndirectedWeightedEdge.class, DirectedEdge.class,
			DirectedWeightedEdge.class };

	public static final Class[] elementClasses = ObjectArrays.concat(nodeTypes,
			edgeTypes, Class.class);

	public static final Class[] dataStructures = { DArray.class,
			DArrayDeque.class, DArrayList.class, DHashArrayList.class,
			DHashMap.class, DHashSet.class, DHashTable.class,
			DLinkedList.class, DEmpty.class };

	public static final Class[] graphGenerators = { CliqueGraph.class,
			RingGraph.class, BarabasiAlbertGraph.class, RandomGraph.class,
			EmptyGraph.class };
}
