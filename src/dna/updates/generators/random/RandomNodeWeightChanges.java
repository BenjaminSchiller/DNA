package dna.updates.generators.random;

import dna.graph.weights.Weight.WeightSelection;

public class RandomNodeWeightChanges extends RandomWeightChanges {

	public RandomNodeWeightChanges(int nodes, WeightSelection nSelection) {
		super(nodes, nSelection, 0, null);
	}

}
