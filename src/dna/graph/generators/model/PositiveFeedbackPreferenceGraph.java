package dna.graph.generators.model;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.generators.GraphGenerator;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.generators.evolving.PositiveFeedbackPreferenceBatch;
import dna.util.parameters.Parameter;

/**
 * Implements the Positive-feedback Preference network model for rich-club networks.
 * Follows the algorithm in: <br>
 *  The Positive-Feedback Preference Model of the AS-level Internet Topology (Zhou, Mondragon @ ICC'05)    
 * @author Tim
 *
 */
public class PositiveFeedbackPreferenceGraph extends GraphGenerator {

	private int startNodes;

	private int startEdges;

	private int nodesToAdd;


	public PositiveFeedbackPreferenceGraph(GraphDataStructure gds, int startNodes,
			int startEdges, int nodesToAdd) {
		super("PositiveFeedbackPreferenceGraph", new Parameter[0], gds, 0, startNodes
				+ nodesToAdd, startEdges + nodesToAdd * 2);
		this.startNodes = startNodes;
		this.startEdges = startEdges;
		this.nodesToAdd = nodesToAdd;
	}

	@Override
	public Graph generate() {
		GraphGenerator gg = new RandomGraph(this.gds, this.startNodes,
				this.startEdges);
		
		Graph g = gg.generate();
		
		
		if(this.nodesToAdd>0){
			BatchGenerator bg = new PositiveFeedbackPreferenceBatch(this.nodesToAdd);
			Batch b = bg.generate(g);
			b.apply(g);
		}
		return g;
	}

}
