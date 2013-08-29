package dna.graph.datastructures;

import dna.graph.generators.GraphGenerator;
import dna.graph.Graph;

public aspect GraphGeneratorProfiler {
	int addNodeCount;
	int addEdgeCount;
	
	pointcut init(Graph g, GraphDataStructure gds) : this(g) && execution(Graph+.new(String,long, GraphDataStructure,..)) && args(*,*,gds,..);
	pointcut generated() : execution(* GraphGenerator+.generate());
	
	pointcut addNode() : execution(* Graph+.addNode(..));
	pointcut addEdge() : execution(* Graph+.addEdge(..));
	    
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
