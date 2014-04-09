package dna.updates.generators.weights;

import dna.graph.weightsNew.Weight.WeightSelection;
import dna.graph.weightsNew.Weight.WeightType;

public class NodeWeightChanges extends WeightChanges {

	public NodeWeightChanges(int nodes, WeightType nType,
			WeightSelection nSelection) {
		super(nodes, nType, nSelection, 0, null, null);
	}

}
