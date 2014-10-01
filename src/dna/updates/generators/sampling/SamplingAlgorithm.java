package dna.updates.generators.sampling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.generators.sampling.startNode.StartNodeSelectionStrategy;
import dna.updates.update.EdgeAddition;
import dna.updates.update.NodeAddition;
import dna.updates.update.Update;
import dna.util.parameters.IntParameter;
import dna.util.parameters.Parameter;
import dna.util.parameters.StringParameter;

/**
 * Base class for all sampling algorithms
 * 
 * @author Benedict Jahn
 * 
 */
public abstract class SamplingAlgorithm extends BatchGenerator {

	private HashSet<Node> seenNodes;
	private HashSet<Node> visitedNodes;

	private HashMap<Integer, Node> addedNodes;

	protected Graph fullGraph;

	private StartNodeSelectionStrategy startNodeStartegy;

	private boolean firstIteration;
	private boolean takeResourceIntoAccount;
	private boolean noFurtherBatch;

	private int costPerBatch;
	private int graphSize;
	private int resource;
	private int initialResource;
	private int nodeDirection;

	private long timeStamp;

	public static enum SamplingStop {
		Seeing, Visiting
	}

	private SamplingStop samplingStop;

	/**
	 * Initializes the sampling algorithm
	 * 
	 * @param name
	 *            the name of the algorithm
	 * @param fullGraph
	 *            the graph the algorithm shall walk on
	 * @param startNodeStrat
	 *            the strategy how the algorithm will select the first node
	 * @param costPerBatch
	 *            how many steps the algorithm shall perform for one batch,
	 *            which indicates the amount of nodes added per batch
	 * @param ressouce
	 *            the maximum count of steps the algorithm shall perform, if
	 *            initialized with 0 or below the algorithm will walk until the
	 *            graph is fully visited
	 * @param parameters
	 *            the parameters which makes this algorithm unique and which
	 *            will be added to the name
	 */
	public SamplingAlgorithm(String name, Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy, int costPerBatch,
			int resource, SamplingStop samplingStop) {
		super(name, new Parameter[] {
				new StringParameter("start", startNodeStrategy.getClass()
						.getSimpleName()),
				new IntParameter("cost", costPerBatch),
				new IntParameter("resource", resource),
				new StringParameter("samplingStop", samplingStop.toString()) });

		this.fullGraph = fullGraph;
		this.costPerBatch = costPerBatch;
		this.startNodeStartegy = startNodeStrategy;
		this.resource = resource;
		this.initialResource = resource;

		if (resource > 0) {
			takeResourceIntoAccount = true;
		} else {
			takeResourceIntoAccount = false;
		}

		timeStamp = 0;

		firstIteration = true;
		noFurtherBatch = false;

		graphSize = fullGraph.getNodeCount();

		seenNodes = new HashSet<Node>(graphSize);
		visitedNodes = new HashSet<Node>(graphSize);

		addedNodes = new HashMap<Integer, Node>();

		this.samplingStop = samplingStop;

		if (DirectedNode.class.isAssignableFrom(this.fullGraph
				.getGraphDatastructures().getNodeType())) {
			nodeDirection = 1;
		} else {
			nodeDirection = 0;
		}

	}

	/**
	 * Let the sampling algorithm walk further and produce a batch based on the
	 * graph
	 * 
	 * @param g
	 *            the current sample
	 */
	public Batch generate(Graph g) {

		Batch newBatch = new Batch(g.getGraphDatastructures(),
				g.getTimestamp(), g.getTimestamp() + 1);
		addedNodes = new HashMap<Integer, Node>();

		if (firstIteration) {
			firstIteration = false;
			int firstCosts = startNodeStartegy.resourceCost(g);
			resource = -firstCosts;

			newBatch = addNodeAndNeighborsToBatch(newBatch,
					init(startNodeStartegy), g);
			timeStamp++;

			for (int i = firstCosts; i < costPerBatch; i++) {
				if (!isFurtherBatchPossible(g)) {
					break;
				}
				newBatch = addNodeAndNeighborsToBatch(newBatch, findNextNode(),
						g);
				resource--;
				timeStamp++;
			}

		} else {

			for (int i = 0; i < costPerBatch; i++) {
				if (!isFurtherBatchPossible(g)) {
					break;
				}
				newBatch = addNodeAndNeighborsToBatch(newBatch, findNextNode(),
						g);
				resource--;
				timeStamp++;
			}
		}
		return newBatch;
	}

	/**
	 * Produces the update based on the specific sampling algorithm
	 * 
	 * @return a node
	 */
	protected abstract Node findNextNode();

	/**
	 * Initializes the sampling algorithm with the start node selection strategy
	 * 
	 * @param startNode
	 *            the chosen starting node selection strategy
	 * 
	 * @return the first node
	 */
	protected abstract Node init(StartNodeSelectionStrategy startNode);

	/**
	 * Adds the node and if necessary the connected edges to the batch and
	 * updates the seen and visited structures
	 * 
	 * @param batch
	 *            the batch to which the updates shall be added
	 * @param node
	 *            the node that shall be added to the batch
	 * @param g
	 *            the sample
	 * @param preAddedNodes
	 *            a HashMap which maps IDs of keys to nodes which were / will be
	 *            added to the batch but are not yet added to the sample
	 * @return the current batch
	 */
	private Batch addNodeAndNeighborsToBatch(Batch batch, Node node, Graph g) {

		if (noFurtherBatch) {
			return batch;
		} else if (g.getNode(node.getIndex()) != null) {
			return batch;
		}

		Node newNode = g.getGraphDatastructures().newNodeInstance(
				node.getIndex());

		batch.add(new NodeAddition(newNode));

		addedNodes.put(newNode.getIndex(), newNode);

		seenNodes.add(node);
		visitedNodes.add(node);

		List<Update> upList = addNeighbors(node, newNode, g);

		for (Update u : upList) {
			batch.add(u);
		}

		return batch;
	}

	/**
	 * Adds the neighbors of a node into the seen structure and produces a list
	 * of edge editions if the neighbors were already visited
	 * 
	 * @param nodeFromFullGraph
	 *            the node from the full graph from which we want to receive the
	 *            neighbors
	 * @param newNode
	 *            the same node but from the sample
	 * @param g
	 *            the sample
	 * @param preAddedNodes
	 *            a HashMap which maps IDs of keys to nodes which were / will be
	 *            added to the batch but are not yet added to the sample
	 * @return a list of node and edge additions
	 */
	private List<Update> addNeighbors(Node nodeFromFullGraph, Node newNode,
			Graph g) {

		List<Update> updateList = new ArrayList<Update>();

		Iterable<IElement> iter = getEdgesFromNode(nodeFromFullGraph);

		// Iterate over all edges
		for (IElement e : iter) {
			Edge edge = (Edge) e;
			Node neighbor = edge.getDifferingNode(nodeFromFullGraph);

			// Is the neighbor in the visited HashSet?
			if (visitedNodes.contains(neighbor)) {

				// Yes -> Put Edge in list, End
				GraphDataStructure gds = g.getGraphDatastructures();

				Node dstNode;
				if (g.getNode(neighbor.getIndex()) == null) {
					dstNode = addedNodes.get(neighbor.getIndex());
				} else {
					dstNode = g.getNode(neighbor.getIndex());
				}
				Edge newEdge = gds.newEdgeInstance(newNode, dstNode);
				updateList.add(new EdgeAddition(newEdge));
			} else {

				// No -> continue
				// Is the neighbor in the seen HashSet?
				if (!seenNodes.contains(neighbor)) {

					// No -> Put neighbor in the seen HashSet, End
					seenNodes.add(neighbor);
				}
			}
		}
		return updateList;
	}

	/**
	 * Returns if the sampling algorithm can walk further through the graph
	 */
	public boolean isFurtherBatchPossible(Graph g) {

		if (takeResourceIntoAccount && resource <= 0) {
			return false;
		}
		if (this.samplingStop.equals(SamplingStop.Seeing)
				&& this.seenNodes.size() == this.fullGraph.getNodeCount()) {
			return false;
		}
		if (this.samplingStop.equals(SamplingStop.Visiting)
				&& this.visitedNodes.size() == this.fullGraph.getNodeCount()) {
			return false;
		}
		if (noFurtherBatch) {
			return false;
		}

		return true;
	}

	/**
	 * Resets the sampling algorithm, so it will start again from the beginning
	 */
	public void reset() {

		seenNodes = new HashSet<Node>(graphSize);
		visitedNodes = new HashSet<Node>(graphSize);

		addedNodes = new HashMap<Integer, Node>();

		resource = initialResource;

		firstIteration = true;
		noFurtherBatch = false;
		timeStamp = 0;

		localReset();
	}

	/**
	 * Resets the specific sampling algorithm instance, so it will start again
	 * from the beginning
	 */
	protected abstract void localReset();

	/**
	 * Returns true if the node was already visited by the sampling algorithm
	 * 
	 * @param n
	 *            the node
	 * @return true if it has been visited, false if it has not been visited yet
	 */
	protected boolean visited(Node n) {
		return visitedNodes.contains(n);
	}

	/**
	 * Returns a list of unvisited neighbors of node n
	 * 
	 * @param n
	 *            the node of whom we want to receive the unvisited neighbors
	 * @return a list of unvisited nodes
	 */
	protected ArrayList<Node> getUnvisitedNeighbors(Node n) {

		ArrayList<Node> neighbors = new ArrayList<Node>();
		Iterable<IElement> iter = getEdgesFromNode(n);

		for (IElement e : iter) {
			Edge edge = (Edge) e;
			Node neighbor = edge.getDifferingNode(n);
			if (!visitedNodes.contains(neighbor)) {
				neighbors.add(neighbor);
			}
		}
		return neighbors;
	}

	/**
	 * Returns a list of unseen neighbors of node n
	 * 
	 * @param n
	 *            the node of whom we want to receive the unseen neighbors
	 * @return a list of unseen nodes
	 */
	protected ArrayList<Node> getUnseenNeighbors(Node n) {

		ArrayList<Node> neighbors = new ArrayList<Node>();
		Iterable<IElement> iter = getEdgesFromNode(n);

		for (IElement e : iter) {
			Edge edge = (Edge) e;
			Node neighbor = edge.getDifferingNode(n);
			if (!seenNodes.contains(neighbor)) {
				neighbors.add(neighbor);
			}
		}
		return neighbors;
	}

	/**
	 * Returns a list of visited neighbors of node n
	 * 
	 * @param n
	 *            the node of whom we want to receive the visited neighbors
	 * @return a list of already visited nodes
	 */
	protected ArrayList<Node> getVisitedNeighbors(Node n) {

		ArrayList<Node> neighbors = new ArrayList<Node>();
		Iterable<IElement> iter = getEdgesFromNode(n);

		for (IElement e : iter) {
			Edge edge = (Edge) e;
			Node neighbor = edge.getDifferingNode(n);
			if (visitedNodes.contains(neighbor)) {
				neighbors.add(neighbor);
			}
		}
		return neighbors;
	}

	/**
	 * Returns a list of all neighbors of node n
	 * 
	 * @param n
	 *            the node of whom we want to receive the neighbors
	 * @return a list of nodes
	 */
	protected ArrayList<Node> getAllNeighbors(Node n) {
		ArrayList<Node> neighbors = new ArrayList<Node>();
		Iterable<IElement> iter = getEdgesFromNode(n);

		for (IElement e : iter) {
			Edge edge = (Edge) e;
			neighbors.add(edge.getDifferingNode(n));
		}
		return neighbors;
	}

	/**
	 * Returns the iterator over the outgoing edges of a node (undirected or
	 * directed)
	 * 
	 * @param n
	 *            the node
	 */
	protected Iterable<IElement> getEdgesFromNode(Node n) {
		if (nodeDirection == 1) {
			return ((DirectedNode) n).getOutgoingEdges();
		} else {
			return n.getEdges();
		}
	}

	/**
	 * Returns the outdegree of a given node (directed or undirected)
	 * 
	 * @param n
	 *            the node
	 */
	protected int getDegreeFromNode(Node n) {
		if (nodeDirection == 1) {
			return ((DirectedNode) n).getOutDegree();
		} else {
			return ((UndirectedNode) n).getDegree();
		}
	}

	/**
	 * Returns the HashSet with the already visited Nodes
	 */
	protected HashSet<Node> getVisitedNodes() {
		return visitedNodes;
	}

	/**
	 * Returns the number of nodes in the original graph, the sampling algorithm
	 * is operating on
	 * 
	 * @return number of nodes
	 */
	public int getNodeCountOfBaseGraph() {
		return fullGraph.getNodeCount();
	}

	/**
	 * Returns the number of nodes which has either been visited or seen by the
	 * sampling algorithm
	 * 
	 * @return number of nodes
	 */
	public int getSeenAndVisitedCount() {
		return seenNodes.size();
	}

	/**
	 * Returns the number of nodes which has been visited by the sampling
	 * algorithm
	 * 
	 * @return number of nodes
	 */
	public int getVisitedCount() {
		return visitedNodes.size();
	}

	/**
	 * This method is called, if the specific sampling algorithm can't find a
	 * further node
	 */
	protected void noNodeFound() {
		noFurtherBatch = true;
	}

	/**
	 * Returns the current time stamp of this sampling algorithm
	 */
	protected long getTimeStamp() {
		return timeStamp;
	}

}
