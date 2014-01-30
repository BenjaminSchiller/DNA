package dna.updates.walkingAlgorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.startNodeSelection.StartNodeSelectionStrategy;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.EdgeAddition;
import dna.updates.update.NodeAddition;
import dna.updates.update.Update;
import dna.util.parameters.Parameter;

/**
 * @author Benedict
 * 
 */
public abstract class WalkingAlgorithm extends BatchGenerator {

	private HashSet<Node> seenNodes;
	private HashSet<Node> visitedNodes;

	private Graph fullGraph;

	private StartNodeSelectionStrategy startNodeStartegy;

	private boolean onlyVisited;
	private boolean firstIteration;
	private boolean takeResourceIntoAccount;

	private int costPerBatch;
	private int graphSize;
	private int resource;

	/**
	 * Initializes the walking algorithm
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
	public WalkingAlgorithm(String name, Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy,
			boolean onlyVisitedNodesToGraph, int costPerBatch, int resource,
			Parameter[] parameters) {
		super(name, parameters);

		this.fullGraph = fullGraph;
		this.onlyVisited = onlyVisitedNodesToGraph;
		this.costPerBatch = costPerBatch;
		this.startNodeStartegy = startNodeStrategy;
		this.resource = resource;

		if (resource > 0) {
			takeResourceIntoAccount = true;
		} else {
			takeResourceIntoAccount = false;
		}

		firstIteration = true;

		graphSize = fullGraph.getNodeCount();

		seenNodes = new HashSet<Node>(graphSize);
		visitedNodes = new HashSet<Node>(graphSize);

	}

	/**
	 * Let the walking algorithm walk further and produce a batch based on the
	 * graph
	 * 
	 * @param g
	 *            the current progress of the walking algorithm
	 */
	public Batch generate(Graph g) {

		// TODO Überprüfen ist das so korrekt mit Timestamp?
		Batch newBatch = new Batch(g.getGraphDatastructures(),
				g.getTimestamp(), g.getTimestamp() + 1);

		if (firstIteration) {
			firstIteration = false;
			int firstCosts = startNodeStartegy.resourceCost();
			resource = -firstCosts;

			newBatch = addNodeAndNeighborsToBatch(newBatch,
					init(fullGraph, startNodeStartegy), g);

			for (int i = firstCosts; i < costPerBatch; i++) {
				if (!isFurtherBatchPossible(g)) {
					break;
				}
				newBatch = addNodeAndNeighborsToBatch(newBatch,
						findNextNode(fullGraph, g), g);
				resource--;
			}

		} else {

			for (int i = 0; i < costPerBatch; i++) {
				if (!isFurtherBatchPossible(g)) {
					break;
				}
				newBatch = addNodeAndNeighborsToBatch(newBatch,
						findNextNode(fullGraph, g), g);
				resource--;
			}
		}
		return newBatch;
	}

	/**
	 * Produces the update based on the specific walking algorithm
	 * 
	 * @param fullyGraph
	 *            the full graph on which the algorithm operates
	 * @param currentGraph
	 *            the current progress on the graph
	 * @return an update
	 */
	protected abstract Node findNextNode(Graph fullyGraph, Graph currentGraph);

	/**
	 * Initializes the walking algorithm with the start node selection strategy
	 * 
	 * @return the first update
	 */
	protected abstract Node init(Graph fullyGraph,
			StartNodeSelectionStrategy startNode);

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
	 * @return
	 */
	private Batch addNodeAndNeighborsToBatch(Batch batch, Node node, Graph g) {

		batch.add(new NodeAddition(node));

		seenNodes.add(node);
		visitedNodes.add(node);

		List<Update> upList = getNeighbors(node, g);

		for (Update u : upList) {
			batch.add(u);
		}

		return batch;
	}

	/**
	 * Returns the neighbors of a node and the edges that connects them,
	 * additionally it updates the seenNodes HashSet
	 * 
	 * @param node
	 *            the node from which we want to receive the neighbors
	 * @param g
	 *            the current graph
	 * @return a list of node and edge additions
	 */
	private List<Update> getNeighbors(Node node, Graph g) {

		// Für bessere Memory Usage könnte man das auch in
		// addNodeAndNeighborsToBatch integrieren

		List<Update> list = new ArrayList<Update>();

		Iterable<IElement> iter = node.getEdges();

		if (onlyVisited) {

			// Nur besuchte Nodes in Graph
			// Iteriere über alle Edges
			for (IElement e : iter) {
				Edge edge = (Edge) e;
				Node neighbor = edge.getDifferingNode(node);

				// Ist der Nachbar in der besucht Liste?
				if (visitedNodes.contains(neighbor)) {

					// Ja -> Edge in Liste, Ende
					list.add(new EdgeAddition(edge));
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
				Node neighbor = edge.getDifferingNode(node);

				// Edge bereits im Graph?
				if (!g.containsEdge(edge)) {

					// Nein -> Edge in Liste, Weiter
					list.add(new EdgeAddition(edge));
				}

				// Ist der Nachbar im Graph?
				if (!g.containsNode(neighbor)) {

					// Nein -> Nachbar in Liste, Weiter
					list.add(new NodeAddition(neighbor));
				}

				// Ist der Nachbar in der gesehen Liste?
				if (!seenNodes.contains(neighbor)) {

					// Nein -> Nachbar in gesehen Liste, Ende
					seenNodes.add(neighbor);
				}
			}
		}
		return list;
	}

	/**
	 * Returns if the walking algorithm can walk further through the graph
	 */
	public boolean isFurtherBatchPossible(Graph g) {

		if (takeResourceIntoAccount && resource <= 0) {

			return false;

		} else if (g.getNodeCount() == seenNodes.size()) {

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

		firstIteration = true;
	}

	/**
	 * Returns a list of unvisited neighbors of node n
	 * 
	 * @param n
	 *            the node of whom we want to receive the unvisited neighbors
	 * @return a list of nodes
	 */
	protected static List<UndirectedNode> getUnvisitedNeighbors(UndirectedNode n) {
		// TODO implementieren
		return null;
	}
	
	

}
