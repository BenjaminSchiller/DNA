package dna.updates.generators.random;

import dna.graph.weights.Weight.WeightSelection;

public class RandomEdgeWeightChanges extends RandomWeightChanges {

	public RandomEdgeWeightChanges(int edges, WeightSelection eSelection) {
		super(0, null, edges, eSelection);
	}

}
