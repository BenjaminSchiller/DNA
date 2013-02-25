package dynamicGraphs.diff.generator;

import dynamicGraphs.diff.Diff;
import dynamicGraphs.graph.Graph;

public abstract class DiffGenerator {

	public DiffGenerator(String name) {
		this.name = name;
	}

	private String name;

	public String getName() {
		return this.name;
	}

	public abstract Diff generate(Graph g);

}
