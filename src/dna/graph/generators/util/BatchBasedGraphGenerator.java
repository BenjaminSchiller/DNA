package dna.graph.generators.util;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.generators.GraphGenerator;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.util.parameters.Parameter;

public class BatchBasedGraphGenerator extends GraphGenerator {

	private GraphGenerator gg;

	private BatchGenerator bg;

	private int times;

	public BatchBasedGraphGenerator(GraphDataStructure gds, GraphGenerator gg,
			BatchGenerator bg, int times) {
		super("BatchBasedGraphGenerator", new Parameter[] {}, gds, 0, 0, 0);
		this.gg = gg;
		this.bg = bg;
		this.times = times;
	}

	@Override
	public Graph generate() {
		Graph g = this.gg.generate();
		for (int i = 0; i < this.times; i++) {
			if (!this.bg.isFurtherBatchPossible(g)) {
				break;
			}
			Batch b = this.bg.generate(g);
			b.apply(g);
		}
		return g;
	}

}
