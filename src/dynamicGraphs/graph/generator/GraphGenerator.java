package dynamicGraphs.graph.generator;

import dynamicGraphs.graph.Graph;

public abstract class GraphGenerator {

	public GraphGenerator(String name) {
		this.name = name;
	}

	private String name;

	public String getName() {
		return this.name;
	}

	public abstract Graph generate();
}
