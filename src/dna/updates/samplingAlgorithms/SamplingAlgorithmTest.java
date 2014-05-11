package dna.updates.samplingAlgorithms;

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
import dna.updates.samplingAlgorithms.startNodeSelection.StartNodeSelectionStrategy;
import dna.updates.update.EdgeAddition;
import dna.updates.update.NodeAddition;
import dna.updates.update.Update;
import dna.util.parameters.Parameter;

/**
 * Base class for all sampling algorithms
 * 
 * @author Benedict Jahn
 * 
 */
public abstract class SamplingAlgorithmTest extends BatchGenerator {

	private HashSet<Node> seenNodes;
	private HashSet<Node> visitedNodes;

	private HashMap<Integer, Node> addedNodes;

	protected Graph fullGraph;

	private StartNodeSelectionStrategy startNodeStartegy;

	private boolean onlyVisited;
	private boolean firstIteration;
	private boolean takeResourceIntoAccount;
	private boolean noFurtherBatch;

	private int costPerBatch;
	private int graphSize;
	private int resource;
	private int initialResource;
	private int nodeDirection;

	private long timeStamp;

	/**
	 * Initializes the sampling algorithm
	 * 
	 * @param name
	 *            the name of the algorithm
	 * @param fullGraph
	 *            the graph the algorithm shall walk on
	 * @param startNodeStrat
	 *            the strategy how the algorithm will select the first node
	 * @param onlyVisitedNodesToGraph
	 *            if set to true the generator will only put visited nodes in
	 *            the batch
	 * @param costPerBatch
	 *            how many steps the algorithm shall perform for one batch
	 * @param ressouce
	 *            the maximum count of steps the algorithm shall perform, if
	 *            initialized with 0 or below the algorithm will walk until the
	 *            graph is fully visited
	 * @param parameters
	 *            the parameters which makes this algorithm unique and which
	 *            will be added to the name
	 */
	public SamplingAlgorithmTest(String name, Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy,
			boolean onlyVisitedNodesToGraph, int costPerBatch, int resource,
			Parameter[] parameters) {
		super(name, parameters);

		this.fullGraph = fullGraph;
		this.onlyVisited = onlyVisitedNodesToGraph;
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

		if (DirectedNode.class.isAssignableFrom(this.fullGraph
				.getGraphDatastructures().getNodeType())) {
			nodeDirection = 1;
		} else {
			nodeDirection = 0;
		}

	}

	/**
	 * Let the walking algorithm walk further and produce a batch based on the
	 * graph
	 * 
	 * @param g
	 *            the current progress of the walking algorithm
	 */
	public Batch generate(Graph g) {

		Batch newBatch = new Batch(g.getGraphDatastructures(),
				g.getTimestamp(), g.getTimestamp() + 1);
		addedNodes = new HashMap<Integer, Node>();

		if (firstIteration) {
			firstIteration = false;
			int firstCosts = startNodeStartegy.resourceCost();
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
	 * Produces the update based on the specific walking algorithm
	 * 
	 * @return a node
	 */
	protected abstract Node findNextNode();

	/**
	 * Initializes the walking algorithm with the start node selection strategy
	 * 
	 * @param startNode
	 *            the chosen starting node selection strategy
	 * 
	 * @return the first node
	 */
	protected abstract Node init(StartNodeSelectionStrategy startNode);

	/**
	 * Adds the node (and in case of all seen nodes in graph, also the
	 * neighbors) to the batch and updates the hashMaps
	 * 
	 * @param batch
	 *            the batch to which the updates shall be added
	 * @param node
	 *            the node that shall be added to the batch
	 * @param g
	 *            the current graph
	 * @param preAddedNodes
	 *            a HashMap which maps IDs of keys to nodes which were / will be
	 *            added with the batch but are not yet added to the graph
	 * @return
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
			// System.out.println(u);
			batch.add(u);
		}

		return batch;
	}

	/**
	 * Returns the neighbors of a node and the edges that connects them,
	 * additionally it updates the seenNodes HashSet
	 * 
	 * @param nodeFromFullGraph
	 *            the node from the full graph from which we want to receive the
	 *            neighbors
	 * @param newNode
	 *            the same node but from the sample
	 * @param g
	 *            the current graph
	 * @param preAddedNodes
	 *            a HashMap which maps IDs of keys to nodes which were / will be
	 *            added with the batch but are not yet added to the graph
	 * @return a list of node and edge additions
	 */
	private List<Update> addNeighbors(Node nodeFromFullGraph, Node newNode,
			Graph g) {

		List<Update> updateList = new ArrayList<Update>();

		Iterable<IElement> iter = getEdgesFromNode(nodeFromFullGraph);

		if (onlyVisited) {

			// Nur besuchte Nodes in Graph
			// Iteriere über alle Edges
			for (IElement e : iter) {
				Edge edge = (Edge) e;
				Node neighbor = edge.getDifferingNode(nodeFromFullGraph);

				// Ist der Nachbar in der besucht Liste?
				if (visitedNodes.contains(neighbor)) {

					// Ja -> Edge in Liste, Ende
					GraphDataStructure gds = g.getGraphDatastructures();

					Node dstNode;
					if (g.getNode(neighbor.getIndex()) == null) {
						dstNode = addedNodes.get(neighbor.getIndex());
					} else {
						dstNode = g.getNode(neighbor.getIndex());
					}
					Edge newEdge = gds.newEdgeInstance(newNode, dstNode);
					// newEdge.connectToNodes();
					updateList.add(new EdgeAddition(newEdge));
				} else {

					// Nein -> Weiter
					// Ist der Nachbar in der gesehen Liste?
					if (!seenNodes.contains(neighbor)) {

						// Nein -> Nachbar in gesehen Liste, Ende
						seenNodes.add(neighbor);
					}
				}
			}
		} else {

			// Besuchte und gesehene Nodes in Graph
			// Iteriere über alle Edges
			for (IElement e : iter) {
				Edge edge = (Edge) e;
				Node neighbor = edge.getDifferingNode(nodeFromFullGraph);

				// Ist der Nachbar im Graph?
				if (!g.containsNode(neighbor)) {

					// Nein -> Nachbar in Liste, Weiter
					Node newNeighbor = g.getGraphDatastructures()
							.newNodeInstance(neighbor.getIndex());

					updateList.add(new NodeAddition(newNeighbor));

					addedNodes.put(newNeighbor.getIndex(), newNeighbor);
				}

				// Edge bereits im Graph?
				if (!g.containsEdge(edge)) {

					// Nein -> Edge in Liste, Weiter
					GraphDataStructure gds = g.getGraphDatastructures();

					Node dstNode;
					if (g.getNode(neighbor.getIndex()) == null) {
						dstNode = addedNodes.get(neighbor.getIndex());
					} else {
						dstNode = g.getNode(neighbor.getIndex());
					}

					Edge newEdge = gds.newEdgeInstance(newNode, dstNode);
					// newEdge.connectToNodes();
					updateList.add(new EdgeAddition(newEdge));
				}

				// Ist der Nachbar in der gesehen Liste?
				if (!seenNodes.contains(neighbor)) {

					// Nein -> Nachbar in gesehen Liste, Ende
					seenNodes.add(neighbor);
				}
			}
		}
		return updateList;
	}

	/**
	 * Returns if the walking algorithm can walk further through the graph
	 */
	public boolean isFurtherBatchPossible(Graph g) {

		if (takeResourceIntoAccount && resource <= 0) {

			return false;

		} else if (fullGraph.getNodeCount() == seenNodes.size()) {

			return false;

		} else if (noFurtherBatch) {

			return false;

		} else {

			return true;

		}
	}

	/**
	 * Resets the walking algorithm, so it will start again from the beginning
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
	 * Resets the walking algorithm instance, so it will start again from the
	 * beginning
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
	 * @return a list of nodes
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
	 * @return a list of nodes
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
	 * @return a list of nodes
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
	 * Returns the outdegree of a given node (directed o r undirected)
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
	 * Returns the number of nodes in the graph, the walking algorithm is
	 * operating on
	 * 
	 * @return number of nodes
	 */
	public int getNodeCountOfBaseGraph() {
		return fullGraph.getNodeCount();
	}

	/**
	 * Returns the number of nodes which has either been visited or seen by the
	 * walking algorithm
	 * 
	 * @return number of nodes
	 */
	public int getSeenAndVisitedCount() {
		return seenNodes.size();
	}

	/**
	 * Returns the number of nodes which has been visited by the walking
	 * algorithm
	 * 
	 * @return number of nodes
	 */
	public int getVisitedCount() {
		return visitedNodes.size();
	}

	/**
	 * This method is called, if the walking algorithm can't find a further node
	 */
	protected void noNodeFound() {
		noFurtherBatch = true;
	}

	/**
	 * Returns the current time stamp of this walking algorithm
	 */
	protected long getTimeStamp() {
		return timeStamp;
	}

}
