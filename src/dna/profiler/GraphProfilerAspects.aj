package dna.profiler;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.Node;
import dna.profiler.GraphProfiler.ProfilerType;

public aspect GraphProfilerAspects {
	private static boolean isActive = false;
	
	pointcut activate() : execution(* GraphProfiler.activate());
	
	pointcut init(Graph g, GraphDataStructure gds) : this(g) && execution(Graph+.new(String,long, GraphDataStructure,..)) && args(*,*,gds,..);
	pointcut generated() : execution(* GraphGenerator+.generate());
	
	pointcut addNode() : call(* IDataStructure+.add(Node+)) && if(isActive);
	pointcut addEdge() : call(* IDataStructure+.add(Edge+)) && if(isActive);
	pointcut removeNode() : call(* IDataStructure+.remove(Node+)) && if(isActive);
	pointcut removeEdge() : call(* IDataStructure+.remove(Edge+)) && if(isActive);
	
	pointcut graphAction() : this(Graph);
	pointcut nodeAction() : this(Node);
	 
	after() : activate() {
//		isActive = true;
	}
	
	after(Graph g, GraphDataStructure gds) : init(g, gds) {
		GraphProfiler.init(gds);
	}
	
	after() : addNode() && graphAction() {
		GraphProfiler.count(null, ProfilerType.AddNodeGlobal);
	}
	
	after() : addEdge() && graphAction()  {
		GraphProfiler.count(null, ProfilerType.AddEdgeGlobal);
	}

	after() : addEdge() && nodeAction()  {
		GraphProfiler.count(null, ProfilerType.AddEdgeLocal);
	}	
	
	after() : removeNode() && graphAction()  {
		GraphProfiler.count(null, ProfilerType.RemoveNodeGlobal);
	}
	
	after() : removeEdge() && graphAction()  {
		GraphProfiler.count(null, ProfilerType.RemoveEdgeGlobal);
	}

	after() : removeEdge() && nodeAction()  {
		GraphProfiler.count(null, ProfilerType.RemoveEdgeLocal);
	}
	
	after() : generated() {
		//GraphProfiler.finish();
	}
}
