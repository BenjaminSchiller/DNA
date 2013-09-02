package dna.profiler;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.Node;
import dna.profiler.GraphProfiler.ProfilerType;

public aspect GraphProfilerAspects {
	pointcut init(Graph g, GraphDataStructure gds) : this(g) && execution(Graph+.new(String,long, GraphDataStructure,..)) && args(*,*,gds,..);
	pointcut generated() : execution(* GraphGenerator+.generate());
	
	pointcut addNode() : execution(* IDataStructure+.add(Node+));
	pointcut addEdge() : execution(* IDataStructure+.add(Edge+));
	pointcut removeNode() : execution(* IDataStructure+.remove(Node+));
	pointcut removeEdge() : execution(* IDataStructure+.remove(Edge+));
	    
	after(Graph g, GraphDataStructure gds) : init(g, gds) {
		GraphProfiler.init(gds);
	}
	
	after() : addNode() {
		GraphProfiler.count(ProfilerType.AddNode);
	}
	
	after() : addEdge() {
		GraphProfiler.count(ProfilerType.AddEdge);
	}
	
	after() : removeNode() {
		GraphProfiler.count(ProfilerType.RemoveNode);
	}
	
	after() : removeEdge() {
		GraphProfiler.count(ProfilerType.RemoveEdge);
	}
	
	after() : generated() {
		GraphProfiler.finish();
	}
}
