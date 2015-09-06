package dna.graph.nodes;

import com.tinkerpop.blueprints.Vertex;

import dna.graph.IGraph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.Weight;

// TODO: Auto-generated Javadoc
/**
 * The Class UndirectedWeightedBlueprintsNode.
 */
public class UndirectedWeightedBlueprintsNode extends UndirectedBlueprintsNode implements
		IWeightedNode {

	/** The weight. */
	protected Weight weight;

	/**
	 * Instantiates a new undirected weighted blueprints node.
	 *
	 * @param i the index
	 * @param gds the gds
	 */
	public UndirectedWeightedBlueprintsNode(int i, GraphDataStructure gds) {
		super(i, gds);
	}

	/**
	 * Instantiates a new undirected weighted blueprints node.
	 *
	 * @param i the index
	 * @param weight the weight
	 * @param gds the gds
	 */
	public UndirectedWeightedBlueprintsNode(int i, Weight weight,
			GraphDataStructure gds) {
		this(i, gds);
		this.setWeight(weight);
	}

	/**
	 * Instantiates a new undirected weighted blueprints node.
	 *
	 * @param str the str
	 * @param gds the gds
	 */
	public UndirectedWeightedBlueprintsNode(String str, GraphDataStructure gds) {
		super(str.split(Weight.WeightDelimiter)[0], gds);
		this.setWeight(gds.newNodeWeight(str.split(Weight.WeightDelimiter)[1]));
	}

	/**
	 * Instantiates a new undirected weighted blueprints node.
	 *
	 * @param i the index
	 * @param gds the gds
	 * @param graph the graph
	 */
	public UndirectedWeightedBlueprintsNode(int i, GraphDataStructure gds, IGraph graph) {
		super(i, gds, graph);		
	}

	/**
	 * Instantiates a new undirected weighted blueprints node.
	 *
	 * @param i the index
	 * @param weight the weight
	 * @param gds the gds
	 * @param graph the graph
	 */
	public UndirectedWeightedBlueprintsNode(int i, Weight weight,
			GraphDataStructure gds, IGraph graph) {
		this(i, gds, graph);
		this.setWeight(weight);		
	}

	/**
	 * Instantiates a new undirected weighted blueprints node.
	 *
	 * @param str the str
	 * @param gds the gds
	 * @param graph the graph
	 */
	public UndirectedWeightedBlueprintsNode(String str, GraphDataStructure gds,
			IGraph graph) {
		super(str.split(Weight.WeightDelimiter)[0], gds);
		this.setWeight(gds.newNodeWeight(str.split(Weight.WeightDelimiter)[1]));
		this.setGraph(graph);
	}

	/**
	 * Instantiates a new undirected weighted blueprints node.
	 *
	 * @param i the index
	 * @param gds the gds
	 * @param vertex the vertex
	 * @param graph the graph
	 */
	public UndirectedWeightedBlueprintsNode(int i, GraphDataStructure gds,
			Vertex vertex, IGraph graph) {
		super(i, gds, vertex, graph);		
	}

	/**
	 * Instantiates a new undirected weighted blueprints node.
	 *
	 * @param i the index
	 * @param weight the weight
	 * @param gds the gds
	 * @param vertex the vertex
	 * @param graph the graph
	 */
	public UndirectedWeightedBlueprintsNode(int i, Weight weight,
			GraphDataStructure gds, Vertex vertex, IGraph graph) {
		this(i, gds, vertex, graph);
		this.setWeight(weight);		
	}

	/* (non-Javadoc)
	 * @see dna.graph.weights.IWeighted#getWeight()
	 */
	@Override
	public Weight getWeight() {
		if (vertex == null)
			return this.weight;

		String weight = this.vertex.getProperty("weight");
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
		if (vertex == null)
			return;
		this.vertex.setProperty("weight", weight.asString());
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.Node#asString()
	 */
	@Override
	public String asString() {
		return super.asString() + Weight.WeightDelimiter
				+ this.getWeight().asString();
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.UndirectedNode#toString()
	 */
	public String toString() {
		return super.toString() + " [" + this.getWeight().asString() + "]";
	}

}
