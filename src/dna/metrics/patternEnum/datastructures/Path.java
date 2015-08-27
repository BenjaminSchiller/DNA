package dna.metrics.patternEnum.datastructures;


public class Path {
	private boolean changed;
	private boolean endsWithHub;
	private SmallGraph graph;
	private SmallGraph prevGraph;
	
	public SmallGraph getPrevGraph() {
		return prevGraph;
	}

	public void setPrevGraph(SmallGraph prevGraph) {
		this.prevGraph = prevGraph;
	}

	public boolean endsWithHub() {
		return endsWithHub;
	}

	public void setEndsWithHub(boolean endsWithHub) {
		this.endsWithHub = endsWithHub;
	}

	public boolean hasChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	public SmallGraph getGraph() {
		return graph;
	}
	
	public void setGraph(SmallGraph graph) {
		this.graph = graph;
	}
	
	public Path(SmallGraph graph) {
		this.graph = graph;
	}
	
	public Path shallowClone() {
		Path clonedPath = new Path(graph.shallowClone());
		clonedPath.setChanged(changed);
		clonedPath.setEndsWithHub(endsWithHub);
		
		if(prevGraph != null) {
			clonedPath.setPrevGraph(prevGraph.shallowClone());
		}
		
		return clonedPath;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
			.append("graph: ").append(graph).append("\n")
			.append("prevgraph: ").append(prevGraph).append("\n")
			.append("changed: ").append(changed).append("\n")
			.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Path))
			return false;
		
		Path otherPath = (Path)o;
		
		if(changed != otherPath.hasChanged()) {
			return false;
		}
		if(endsWithHub != otherPath.endsWithHub()) {
			return false;
		}
		if(graph != null && !graph.equals(otherPath.getGraph())) {
			return false;
		}
		if(graph == null && otherPath.getGraph() != null) {
			return false;
		}
		if(prevGraph != null && !prevGraph.equals(otherPath.getPrevGraph())) {
			return false;
		}
		if(prevGraph == null && otherPath.getPrevGraph() != null) {
			return false;
		}
		
		return true;
	}
}
