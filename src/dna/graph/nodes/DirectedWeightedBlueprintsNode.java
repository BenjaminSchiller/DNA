package dna.graph.nodes;

import com.tinkerpop.blueprints.Vertex;

import dna.graph.IGraph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.Weight;

/**
 * The Class DirectedWeightedBlueprintsNode.
 * 
 * @author Matthias
 */
public class DirectedWeightedBlueprintsNode extends DirectedBlueprintsNode implements
		IWeightedNode {

	/** The weight. */
	private Weight weight;

	/**
	 * Instantiates a new directed weighted blueprints node.
	 *
	 * @param i the i
	 * @param gds the gds
	 */
	public DirectedWeightedBlueprintsNode(int i, GraphDataStructure gds) {
		super(i, gds);
	}

	/**
	 * Instantiates a new directed weighted blueprints node.
	 *
	 * @param i the i
	 * @param weight the weight
	 * @param gds the gds
	 */
	public DirectedWeightedBlueprintsNode(int i, Weight weight, GraphDataStructure gds) {
		this(i, gds);
		this.setWeight(weight);
	}

	/**
	 * Instantiates a new directed weighted blueprints node.
	 *
	 * @param str the str
	 * @param gds the gds
	 */
	public DirectedWeightedBlueprintsNode(String str, GraphDataStructure gds) {
		super(str.split(Weight.WeightDelimiter)[0], gds);
		this.setWeight(gds.newNodeWeight(str.split(Weight.WeightDelimiter)[1]));
	}

	/**
	 * Instantiates a new directed weighted blueprints node.
	 *
	 * @param i the i
	 * @param gds the gds
	 * @param vertex the vertex
	 */
	public DirectedWeightedBlueprintsNode(int i, GraphDataStructure gds, Vertex vertex) {
		this(i, gds);
		this.setGDBNode(vertex);
	}

	/**
	 * Instantiates a new directed weighted blueprints node.
	 *
	 * @param i the i
	 * @param gds the gds
	 * @param graph the graph
	 */
	public DirectedWeightedBlueprintsNode(int i, GraphDataStructure gds, IGraph graph) {
		super(i, gds);
		this.setGraph(graph);
	}

	/**
	 * Instantiates a new directed weighted blueprints node.
	 *
	 * @param i the i
	 * @param weight the weight
	 * @param gds the gds
	 * @param graph the graph
	 */
	public DirectedWeightedBlueprintsNode(int i, Weight weight,
			GraphDataStructure gds, IGraph graph) {
		this(i, gds);
		this.setWeight(weight);
		this.setGraph(graph);
	}

	/**
	 * Instantiates a new directed weighted blueprints node.
	 *
	 * @param str the str
	 * @param gds the gds
	 * @param graph the graph
	 */
	public DirectedWeightedBlueprintsNode(String str, GraphDataStructure gds,
			IGraph graph) {
		super(str.split(Weight.WeightDelimiter)[0], gds);
		this.setWeight(gds.newNodeWeight(str.split(Weight.WeightDelimiter)[1]));
		this.setGraph(graph);
	}

	/**
	 * Instantiates a new directed weighted blueprints node.
	 *
	 * @param i the i
	 * @param gds the gds
	 * @param vertex the vertex
	 * @param graph the graph
	 */
	public DirectedWeightedBlueprintsNode(int i, GraphDataStructure gds,
			Vertex vertex, IGraph graph) {
		this(i, gds);
		this.setGDBNode(vertex);
		this.setGraph(graph);
	}

	/**
	 * Instantiates a new directed weighted blueprints node.
	 *
	 * @param i the i
	 * @param weight the weight
	 * @param gds the gds
	 * @param vertex the vertex
	 * @param graph the graph
	 */
	public DirectedWeightedBlueprintsNode(int i, Weight weight,
			GraphDataStructure gds, Vertex vertex, IGraph graph) {
		this(i, gds);
		this.setGDBNode(vertex);
		this.setWeight(weight);
		this.setGraph(graph);
	}

	@Override
	public boolean hasEdge(Node n1, Node n2) {
		if (n1 == null || n2 == null 
				|| !(n1 instanceof DirectedWeightedBlueprintsNode)
				|| !(n2 instanceof DirectedWeightedBlueprintsNode))
			return false;
		
		return this.graph.getEdge(n1, n2) != null;
	}

	/* (non-Javadoc)
	 * @see dna.graph.weights.IWeighted#getWeight()
	 */
	@Override
	public Weight getWeight() {
		if (vertex == null) return this.weight;
		
		if (this.vertex.getProperty("weight") == null)
			this.setWeight(this.weight);
		return this.weight;
	}

	/* (non-Javadoc)
	 * @see dna.graph.weights.IWeighted#setWeight(dna.graph.weights.Weight)
	 */
	@Override
	public void setWeight(Weight weight) {
		this.weight = weight;
		if (vertex == null) return;
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
	 * @see dna.graph.nodes.DirectedBlueprintsNode#toString()
	 */
	public String toString() {
		return super.toString() + " [" + this.getWeight().asString() + "]";
	}

}
