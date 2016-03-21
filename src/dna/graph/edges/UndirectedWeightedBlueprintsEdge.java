package dna.graph.edges;

import java.util.HashMap;

import dna.graph.IGraph;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedBlueprintsNode;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.Weight;

/**
 * The Class UndirectedWeightedBlueprintsEdge.
 * 
 * @author Matthias
 */
public class UndirectedWeightedBlueprintsEdge extends UndirectedBlueprintsEdge implements
		IWeightedEdge {

	/** The weight. */
	protected Weight weight;

	/**
	 * Instantiates a new undirected weighted blueprints edge.
	 *
	 * @param src the src
	 * @param dst the dst
	 * @param weight the weight
	 */
	public UndirectedWeightedBlueprintsEdge(UndirectedBlueprintsNode src, UndirectedBlueprintsNode dst,
			Weight weight) {
		super(src, dst);
		this.weight = weight;
	}

	/**
	 * Instantiates a new undirected weighted blueprints edge.
	 *
	 * @param s the s
	 * @param g the g
	 */
	public UndirectedWeightedBlueprintsEdge(String s, IGraph g) {
		super(s.split(Weight.WeightDelimiter)[0], g);
		this.weight = g.getGraphDatastructures().newEdgeWeight(
				s.split(Weight.WeightDelimiter)[1]);
	}

	/**
	 * Instantiates a new undirected weighted blueprints edge.
	 *
	 * @param s the s
	 * @param g the g
	 * @param addedNodes the added nodes
	 */
	public UndirectedWeightedBlueprintsEdge(String s, IGraph g,
			HashMap<Integer, Node> addedNodes) {
		super(s.split(Weight.WeightDelimiter)[0], g, addedNodes);
		this.weight = g.getGraphDatastructures().newEdgeWeight(
				s.split(Weight.WeightDelimiter)[1]);
	}
	
	/**
	 * Instantiates a new undirected weighted blueprints edge.
	 *
	 * @param src the src
	 * @param dst the dst
	 * @param weight the weight
	 * @param e the e
	 */
	public UndirectedWeightedBlueprintsEdge(UndirectedBlueprintsNode src, UndirectedBlueprintsNode dst,
			Weight weight, Object gdbEdgeId) {
		super(src, dst, gdbEdgeId);		
		this.setWeight(weight);
	}
	

	/* (non-Javadoc)
	 * @see dna.graph.weights.IWeighted#getWeight()
	 */
	@Override
	public Weight getWeight() {
		if (this.getGDBEdge() == null)
			return this.weight;

		String weight = this.getGDBEdge().getProperty("weight");
		if (weight == null)
			this.setWeight(this.weight);
		return this.weight;
	}

	/* (non-Javadoc)
	 * @see dna.graph.weights.IWeighted#setWeight(dna.graph.weights.Weight)
	 */
	@Override
	public void setWeight(Weight weight) {
		this.weight = weight;
		if (this.getGDBEdge() == null)
			return;
		this.getGDBEdge().setProperty("weight", weight.asString());
	}

	/* (non-Javadoc)
	 * @see dna.graph.edges.UndirectedEdge#asString()
	 */
	@Override
	public String asString() {
		return super.asString() + Weight.WeightDelimiter
				+ this.weight.asString();
	}

	/* (non-Javadoc)
	 * @see dna.graph.edges.UndirectedEdge#toString()
	 */
	public String toString() {
		return super.toString() + " [" + this.getWeight().asString() + "]";
	}

}
