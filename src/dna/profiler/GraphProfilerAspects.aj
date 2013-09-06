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
	
	pointcut addNode() : call(* IDataStructure+.add(Node+));
	pointcut addEdge() : call(* IDataStructure+.add(Edge+));
	pointcut removeNode() : call(* IDataStructure+.remove(Node+));
	pointcut removeEdge() : call(* IDataStructure+.remove(Edge+));
	
	pointcut graphAction() : this(Graph);
	pointcut nodeAction() : this(Node);
	    
	after(Graph g, GraphDataStructure gds) : init(g, gds) {
		GraphProfiler.init(gds);
	}
	
	after() : addNode() && graphAction() {
		GraphProfiler.count(ProfilerType.AddNodeGlobal);
	}
	
	after() : addEdge() && graphAction()  {
		GraphProfiler.count(ProfilerType.AddEdgeGlobal);
	}

	after() : addEdge() && nodeAction()  {
		GraphProfiler.count(ProfilerType.AddEdgeLocal);
	}	
	
	after() : removeNode() && graphAction()  {
		GraphProfiler.count(ProfilerType.RemoveNodeGlobal);
	}
	
	after() : removeEdge() && graphAction()  {
		GraphProfiler.count(ProfilerType.RemoveEdgeGlobal);
	}

	after() : removeEdge() && nodeAction()  {
		GraphProfiler.count(ProfilerType.RemoveEdgeLocal);
	}
	
	after() : generated() {
		GraphProfiler.finish();
	}
}
