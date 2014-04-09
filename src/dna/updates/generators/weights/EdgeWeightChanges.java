package dna.updates.generators.weights;

import dna.graph.weightsNew.Weight.WeightSelection;
import dna.graph.weightsNew.Weight.WeightType;

public class EdgeWeightChanges extends WeightChanges {

	public EdgeWeightChanges(int edges, WeightType eType,
			WeightSelection eSelection) {
		super(0, null, null, edges, eType, eSelection);
	}

}
