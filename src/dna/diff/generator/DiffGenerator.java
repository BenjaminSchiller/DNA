package dna.diff.generator;

import dna.diff.Diff;
import dna.graph.old.OldGraph;

public abstract class DiffGenerator {

	public DiffGenerator(String name) {
		this.name = name;
	}

	private String name;

	public String getName() {
		return this.name;
	}

	public abstract Diff generate(OldGraph g);

}
