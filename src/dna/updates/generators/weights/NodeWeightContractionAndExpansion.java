package dna.updates.generators.weights;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.Weight;
import dna.graph.weights.doubleW.Double2dWeight;
import dna.graph.weights.doubleW.Double3dWeight;
import dna.graph.weights.doubleW.DoubleWeight;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.NodeWeight;
import dna.util.parameters.DoubleParameter;
import dna.util.parameters.IntParameter;

public class NodeWeightContractionAndExpansion extends BatchGenerator {

	private double contraction;
	private int contractionSteps;

	private double expansion;
	private int expansionSteps;

	private int step;

	public NodeWeightContractionAndExpansion(double contraction,
			int contractionSteps, double expansion, int expansionSteps) {
		super("NodeWeightContractionAndExpansion", new DoubleParameter(
				"CONTRACTION", contraction), new IntParameter(
				"CONTRACTION_STEPS", contractionSteps), new DoubleParameter(
				"EXPANSION", expansion), new IntParameter("EXPANSION_STEPS",
				expansionSteps));
		this.contraction = contraction;
		this.contractionSteps = contractionSteps;
		this.expansion = expansion;
		this.expansionSteps = expansionSteps;
		this.step = 0;
	}

	@Override
	public Batch generate(Graph g) {
		double factor;
		if (this.step < this.contractionSteps) {
			factor = this.contraction;
		} else {
			factor = this.expansion;
		}
		
		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1, 0, 0, g.getNodeCount(), 0, 0, 0);

		for (IElement n_ : g.getNodes()) {
			IWeightedNode n = (IWeightedNode) n_;
			Weight w = null;
			if (n.getWeight() instanceof DoubleWeight) {
				DoubleWeight old = (DoubleWeight) n.getWeight();
				w = new DoubleWeight(old.getWeight() * factor);
			} else if (n.getWeight() instanceof Double2dWeight) {
				Double2dWeight old = (Double2dWeight) n.getWeight();
				w = new Double2dWeight(old.getX() * factor, old.getY() * factor);
			} else if (n.getWeight() instanceof Double3dWeight) {
				Double3dWeight old = (Double3dWeight) n.getWeight();
				w = new Double3dWeight(old.getX() * factor,
						old.getY() * factor, old.getZ() * factor);
			}
			b.add(new NodeWeight(n, w));
		}

		this.step = (this.step + 1)
				% (this.contractionSteps + this.expansionSteps);
		return b;
	}

	@Override
	public void reset() {
		this.step = 0;

	}

	@Override
	public boolean isFurtherBatchPossible(Graph g) {
		return g.getGraphDatastructures().isNodeType(IWeightedNode.class)
				&& g.getGraphDatastructures().isNodeWeightType(
						DoubleWeight.class, Double2dWeight.class,
						Double3dWeight.class);
	}

}
