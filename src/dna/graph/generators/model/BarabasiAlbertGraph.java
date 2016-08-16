package dna.graph.generators.model;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.generators.GraphGenerator;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.generators.evolving.BarabasiAlbertBatch;
import dna.util.parameters.IntParameter;
import dna.util.parameters.Parameter;

public class BarabasiAlbertGraph extends GraphGenerator {

	private int startNodes;

	private int startEdges;

	private int nodesToAdd;

	private int edgesPerNode;

	public BarabasiAlbertGraph(GraphDataStructure gds, int startNodes,
			int startEdges, int nodesToAdd, int edgesPerNode) {
		super("BarabasiAlbertGraph", new Parameter[] {
				new IntParameter("StartNodes", startNodes),
				new IntParameter("StartEdges", startEdges),
				new IntParameter("NodesToAdd", nodesToAdd),
				new IntParameter("edgesPerNode", edgesPerNode) }, gds, 0,
				startNodes + nodesToAdd, startEdges + nodesToAdd * edgesPerNode);
		this.startNodes = startNodes;
		this.startEdges = startEdges;
		this.nodesToAdd = nodesToAdd;
		this.edgesPerNode = edgesPerNode;
	}

	@Override
	public Graph generate() {
		GraphGenerator gg = new RandomGraph(this.gds, this.startNodes,
				this.startEdges);
		BatchGenerator bg = new BarabasiAlbertBatch(this.nodesToAdd,
				this.edgesPerNode);
		Graph g = gg.generate();
		Batch b = bg.generate(g);
		b.apply(g);
		g.setTimestamp(0);
		g.setName(this.getName());
		return g;
	}

}
