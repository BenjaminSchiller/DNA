package dna.profiler;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.Node;

public aspect GraphProfilerAspects {
	int addNodeCount;
	int addEdgeCount;
	
	pointcut init(Graph g, GraphDataStructure gds) : this(g) && execution(Graph+.new(String,long, GraphDataStructure,..)) && args(*,*,gds,..);
	pointcut generated() : execution(* GraphGenerator+.generate());
	
	pointcut addNode() : execution(* IDataStructure+.add(Node+));
	pointcut addEdge() : execution(* IDataStructure+.add(Edge+));
	    
	after(Graph g, GraphDataStructure gds) : init(g, gds) {
		System.out.println("Created new graph with gds" + gds.getDataStructures());
		
		addNodeCount = 0;
		addEdgeCount = 0;
	}
	
	after() : addNode() {
		addNodeCount++;
	}
	
	after() : addEdge() {
		addEdgeCount++;
	}
	
	after() : generated() {
		System.out.println("Okay, graph contained following calls: ");
		System.out.println("addNode: " + addNodeCount);
		System.out.println("addEdge: " + addEdgeCount);
	}
}
