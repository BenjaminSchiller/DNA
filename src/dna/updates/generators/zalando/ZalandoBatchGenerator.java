package dna.updates.generators.zalando;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.generators.zalando.EdgeValuesForNodes;
import dna.graph.generators.zalando.Event;
import dna.graph.generators.zalando.EventColumn;
import dna.graph.generators.zalando.EventFilter;
import dna.graph.generators.zalando.EventMappings;
import dna.graph.generators.zalando.EventReader;
import dna.graph.nodes.Node;
import dna.graph.weights.DoubleWeight;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.IntWeight;
import dna.graph.weights.Weight;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeAddition;

/**
 * The most general version of a {@link BatchGenerator} for Zalando log files.
 * It is the base for the more specific {@link BatchGenerator}s.
 * 
 * @see ZalandoEqualityBatchGenerator
 * @see ZalandoChronologyBatchGenerator
 */
public abstract class ZalandoBatchGenerator extends BatchGenerator {

	/**
	 * In {@link #generate()} mapped {@link Event}s. The indices for the
	 * {@link Node}s of {@link #graph}.
	 */
	EventMappings mappings;

	/**
	 * If this {@link EventFilter} is not null, only events that passes this
	 * filter are recognized.
	 * 
	 * @see #generate()
	 */
	private EventFilter eventFilter;

	// fields for graph metadata

	/**
	 * The number of lines after which the timestamp of the graph should be
	 * increased by {@link #timestep}. The timestamp is not increased if this
	 * value is < 1.
	 */
	private int numberOfLinesOfOneTimestep;
	/**
	 * The number of timeunits to increase the current timestamp.
	 * 
	 * @see #numberOfLinesOfOneTimestep
	 */
	private int timestep;
	/** The time flies in one batch of this generator. */
	private int timeOfBatch;

	// fields for EventReader

	/** The parser of the log file. */
	private EventReader reader;
	/**
	 * The maximum number of events (i.e. lines of file) to read in. The actual
	 * number of lines may be smaller than this value.
	 * 
	 * @see #eventsFilepath
	 */
	int numberOfLinesPerBatch;
	/**
	 * The path of the file containing all events to read in.
	 * 
	 * @see #numberOfLinesPerBatch
	 * */
	String eventsFilepath;

	// fields for nodes

	/**
	 * This column groups will be added as nodes. Each value of the specified
	 * column group will be added exactly once.
	 */
	EventColumn[][] columnGroupsToAddAsNodes;

	// fields for edges

	/**
	 * An edge will be added between two nodes if the values for both nodes are
	 * equal in any column group.
	 */
	EventColumn[][] columnGroupsToCheckForEquality;
	/**
	 * Contains all indices for {@link Node}s added so far by
	 * {@link #addNodesForColumns(Event)} to get all {@link Node}s to which an
	 * edge should be added in {@link #addEdgesForColumns(Event)}.
	 */
	EdgeValuesForNodes nodesSortedByColumnGroupsToCheckForEquality;
	/**
	 * If this is true, the weight of an edge is the number of events caused
	 * this edge (positive {@code int}). If this is false, this number is
	 * normalized by 1 (positive {@code double}), i.e. 1/<i>[number of
	 * events]</i>.
	 */
	boolean absoluteEdgeWeights;
	/** All edges added so for to batches with their current weights. */
	private Map<Edge, Object> edgesAdded;
	/** All nodes added so for to batches with their current weights. */
	private Set<Node> nodesAdded;

	// fields for batches

	/**
	 * The total number of {@link #generate(Graph)}-calls, e.g. used to set the
	 * right timestamp.
	 */
	private int numberOfRuns;
	/**
	 * {@link NodeAddition}s generated for the current batch. Sorted by Node to
	 * check equality of two {@link NodeAddition}s: two {@link NodeAddition}s
	 * are equal, if they add the same node (i.e. the key of this map).
	 */
	private Map<Node, NodeAddition> nodeAdditions;
	/**
	 * {@link EdgeAddition}s generated for the current batch. Sorted by
	 * {@link Edge} to check equality of two {@link EdgeAddition}s: two
	 * {@link EdgeAddition}s are equal, if they add the same {@link Edge} (i.e.
	 * the key of this map).
	 */
	private Map<Edge, EdgeAddition> edgeAdditions;
	/**
	 * {@link EdgeWeight}s generated for the current batch. Sorted by
	 * {@link Edge} to check equality of two {@link EdgeWeight}s: two
	 * {@link EdgeWeight}s are equal, if they add the same {@link Edge} (i.e.
	 * the key of this map).
	 */
	private Map<Edge, EdgeWeight> edgeWeights;

	/**
	 * Initializes the {@link ZalandoGraphGenerator}.
	 * 
	 * @param name
	 *            The name of the graph to generate. The final name will be
	 *            <i>Zalando</i>{@code name}<i>Directed</i> or <i>Zalando</i>
	 *            {@code name}<i>Undirected</i>, depending on {@code gds}.
	 * @param gds
	 *            The {@link GraphDataStructure} of the graph to generate.
	 * @param timestampInit
	 *            The time right before start creating the graph.
	 * @param eventFilter
	 *            If this is set to an {@link EventFilter} != {@code null}, all
	 *            {@link Event}s must pass it in order to be used for graph
	 *            generation. All {@link Event}s are used if this is
	 *            {@code null}.
	 * @param numberOfLinesPerBatch
	 *            The maximum number of {@code Event}s used for each batch. It
	 *            is the <u>maximum</u> number because the log file may have
	 *            fewer lines.
	 * @param eventsFilepath
	 *            The full path of the Zalando log file. Will be passed to
	 *            {@link EventReader}.
	 * @param columnsToAddAsNodes
	 *            The {@link EventColumn}s of an event which values will be
	 *            represented as nodes in the graph.
	 * @param oneNodeForEachColumn
	 *            If this is true, each value of each
	 *            {@code columnsToAddAsNodes} will be a single node (e.g.
	 *            <i>Product1</i>, <i>SALE</i>). If this is false each value of
	 *            all {@code columnsToAddAsNodes} together will be a single node
	 *            (e.g. <i>Product1|SALE</i>).
	 * @param columnsToCheckForEquality
	 *            The {@link EventColumn}s of an event which values will be
	 *            represented as edges in the graph.
	 * @param allColumnsMustBeEqual
	 *            If this is true, all the values of all
	 *            {@code columnsToCheckForEquality} must be equal for the nodes
	 *            of two events to add an edge between those two events. If this
	 *            is false, at least one value of all
	 *            {@code columnsToCheckForEquality} must be equal.
	 * @param absoluteWeights
	 *            If this is true, the weight of an edge is the number of
	 *            relations represented by the edge between the two nodes
	 *            connected by this edge. So it is greater if the two nodes have
	 *            much in common. If this is false, the weight is the inverse of
	 *            this number. So two nodes are "close together" if they have
	 *            much in common.
	 */
	public ZalandoBatchGenerator(String name, GraphDataStructure gds,
			long timestampInit, EventFilter eventFilter,
			int numberOfLinesPerBatch, String eventsFilepath,
			EventColumn[] columnsToAddAsNodes, boolean oneNodeForEachColumn,
			EventColumn[] columnsToCheckForEquality,
			boolean allColumnsMustBeEqual, boolean absoluteWeights) {
		super(name);

		this.mappings = new EventMappings(this.buildEventColumnGroups(
				columnsToAddAsNodes, oneNodeForEachColumn,
				columnsToCheckForEquality, allColumnsMustBeEqual));

		this.eventFilter = eventFilter;

		this.numberOfLinesOfOneTimestep = 1;
		this.timestep = 1;
		this.timeOfBatch = ((numberOfLinesPerBatch / numberOfLinesOfOneTimestep) * timestep);

		this.numberOfLinesPerBatch = numberOfLinesPerBatch;
		this.eventsFilepath = eventsFilepath;

		this.nodesSortedByColumnGroupsToCheckForEquality = new EdgeValuesForNodes();
		this.absoluteEdgeWeights = absoluteWeights;
		this.edgesAdded = new HashMap<Edge, Object>();

		this.nodesAdded = new HashSet<Node>();

		this.numberOfRuns = 0;
		this.reader = new EventReader(this.eventsFilepath);

		this.nodeAdditions = new HashMap<Node, NodeAddition>();
		this.edgeAdditions = new HashMap<Edge, EdgeAddition>();
		this.edgeWeights = new HashMap<Edge, EdgeWeight>();
	}

	/**
	 * Adds an {@link DirectedDoubleWeightedEdge} with given weight from given
	 * source to target {@link Node} to given graph. If the given graph contains
	 * this edge already, the weight of this edge is increased by given weight.
	 * <p>
	 * Instead of adding the edge directly to the graph, it is added to
	 * {@link #edgeAdditions} or {@link #edgeWeights}. {@link #generate(Graph)}
	 * adds these to the batch to update the graph.
	 * </p>
	 * 
	 * @param source
	 *            The source node of the edge to add.
	 * @param target
	 *            The target node of the edge to add.
	 * @param weight
	 *            The weight of the edge to add or -if the edge already exists-
	 *            the weight added to the current weight of the existing edge.
	 */
	private void addDirectedEdge(Graph g, Node source, Node target,
			double weight) {
		final Edge edge = g.getGraphDatastructures().newEdgeInstance(source,
				target);

		final double newWeight = this.calculcateEdgeWeight(edge, weight);

		final DirectedWeightedEdge weightedEdge = (DirectedWeightedEdge) edge;
		if (edgesAdded.containsKey(edge)
				&& !this.edgeAdditions.containsKey(edge)) {
			weightedEdge.setWeight(new DoubleWeight(this
					.calculateFinalEdgeWeight((double) this.edgesAdded
							.get(edge))));

			if (g.containsEdge(edge))
				this.addToEdgeWeights(
						edge,
						new EdgeWeight((IWeightedEdge) g
								.getEdge(source, target), new DoubleWeight(this
								.calculateFinalEdgeWeight(newWeight))));
			else
				this.addToEdgeWeights(
						edge,
						new EdgeWeight(weightedEdge, new DoubleWeight(this
								.calculateFinalEdgeWeight(newWeight))));
		} else {
			weightedEdge.setWeight(new DoubleWeight(this
					.calculateFinalEdgeWeight(newWeight)));
			this.addToEdgeAdditionsForNewEdges(edge, new EdgeAddition(
					weightedEdge));
		}
		this.edgesAdded.put(edge, newWeight);
	}

	/**
	 * Adds an {@link DirectedIntWeightedEdge} with given weight from given
	 * source to target {@link Node} to given graph. If the given graph contains
	 * this edge already, the weight of this edge is increased by given weight.
	 * <p>
	 * Instead of adding the edge directly to the graph, it is added to
	 * {@link #edgeAdditions} or {@link #edgeWeights}. {@link #generate(Graph)}
	 * adds these to the batch to update the graph.
	 * </p>
	 * 
	 * @param source
	 *            The source node of the edge to add.
	 * @param target
	 *            The target node of the edge to add.
	 * @param weight
	 *            The weight of the edge to add or -if the edge already exists-
	 *            the weight added to the current weight of the existing edge.
	 */
	private void addDirectedEdge(Graph g, Node source, Node target, int weight) {
		final Edge edge = g.getGraphDatastructures().newEdgeInstance(source,
				target);

		final int newWeight = this.calculcateEdgeWeight(edge, weight);

		final DirectedWeightedEdge weightedEdge = (DirectedWeightedEdge) edge;
		if (edgesAdded.containsKey(edge)
				&& !this.edgeAdditions.containsKey(edge)) {
			weightedEdge
					.setWeight(new IntWeight(this
							.calculateFinalEdgeWeight((int) this.edgesAdded
									.get(edge))));

			if (g.containsEdge(edge))
				this.addToEdgeWeights(
						edge,
						new EdgeWeight((IWeightedEdge) g
								.getEdge(source, target), new IntWeight(this
								.calculateFinalEdgeWeight(newWeight))));
			else
				this.addToEdgeWeights(
						edge,
						new EdgeWeight(weightedEdge, new IntWeight(this
								.calculateFinalEdgeWeight(newWeight))));
		} else {
			weightedEdge.setWeight(new IntWeight(this
					.calculateFinalEdgeWeight(newWeight)));
			this.addToEdgeAdditionsForNewEdges(edge, new EdgeAddition(
					weightedEdge));
		}
		this.edgesAdded.put(edge, newWeight);
	}

	/**
	 * Adds an {@link Edge} with given weight from given source to target
	 * {@link Node} to given graph. Depending on the {@link GraphDataStructure}
	 * the edge is directed or undirected. If the graph contains this edge
	 * already, the weight of this edge is increased by given weight.
	 * <p>
	 * Instead of adding the edge directly to the graph, it is added to
	 * {@link #edgeAdditions} or {@link #edgeWeights}. {@link #generate(Graph)}
	 * adds these to the batch to update the graph.
	 * </p>
	 * 
	 * @param source
	 *            The source node of the edge to add.
	 * @param target
	 *            The target node of the edge to add.
	 * @param weight
	 *            The weight of the edge to add or -if the edge already exists-
	 *            the weight added to the current weight of the existing edge.
	 *            Depending on the {@link GraphDataStructure} the weight will be
	 *            casted to an {@code int} or {@code double} value.
	 */
	void addEdge(Graph g, Node source, Node target, Object weight) {
		final Class<? extends Edge> edgeType = g.getGraphDatastructures()
				.getEdgeType();
		final Class<? extends Weight> edgeWeightType = g
				.getGraphDatastructures().getEdgeWeightType();

		if (edgeType.equals(DirectedWeightedEdge.class)
				&& edgeWeightType.equals(DoubleWeight.class))
			this.addDirectedEdge(g, source, target,
					Double.valueOf(weight.toString()));
		else if (edgeType.equals(DirectedWeightedEdge.class)
				&& edgeWeightType.equals(IntWeight.class))
			this.addDirectedEdge(g, source, target,
					Integer.valueOf(weight.toString()));
		else if (edgeType.equals(UndirectedWeightedEdge.class)
				&& edgeWeightType.equals(DoubleWeight.class))
			this.addUndirectedEdge(g, source, target,
					Double.valueOf(weight.toString()));
		else if (edgeType.equals(UndirectedWeightedEdge.class)
				&& edgeWeightType.equals(IntWeight.class))
			this.addUndirectedEdge(g, source, target,
					Integer.valueOf(weight.toString()));
	}

	abstract void addEdgesForColumns(Graph g, Event currentEvent);

	/**
	 * Adds the {@link Node}s for given {@link Event} to given graph.
	 * <p>
	 * Additionally groups added {@link Node}s into
	 * {@link #nodesSortedByColumnGroupsToCheckForEquality} for
	 * {@link #addEdgesForColumns(Event)}.
	 * </p>
	 * <p>
	 * Instead of adding the nodes directly to the graph, they are added to
	 * {@link #nodeAdditions}. {@link #generate(Graph)} adds these to the batch
	 * to update the graph.
	 * </p>
	 * 
	 * @param event
	 *            The {@link Event} which nodes should be added.
	 */
	private void addNodesForColumns(Graph g, Event event) {
		int globalMappingForEvent;
		final GraphDataStructure gds = g.getGraphDatastructures();
		Node potentialNewNode;
		for (EventColumn[] eventColumnGroup : this.columnGroupsToAddAsNodes) {
			globalMappingForEvent = this.mappings.getGlobalMapping(
					eventColumnGroup, event);

			potentialNewNode = gds.newNodeInstance(globalMappingForEvent);
			if (!nodesAdded.contains(potentialNewNode)) {
				this.nodesAdded.add(potentialNewNode);
				this.addToNodeAdditions(potentialNewNode, new NodeAddition(
						potentialNewNode));
			}

			// Group recently added nodes by the value for
			// columnGroupsToCheckForEquality of the given event. This
			// simplifies adding edges in addEdgesForColumns(Event) between
			// events (nodes) with equal values.
			for (EventColumn[] eventColumnGroup2 : this.columnGroupsToCheckForEquality)
				this.nodesSortedByColumnGroupsToCheckForEquality.addNode(
						this.mappings
								.getGlobalMapping(eventColumnGroup2, event),
						globalMappingForEvent);
		}
	}

	/**
	 * Adds the given {@link EdgeAddition} for given {@link Edge} to the updates
	 * to do in this batch. The {@link Edge} is then added by
	 * {@link #generate(Graph)}.
	 * 
	 * @param edge
	 *            The {@link Edge} to add.
	 * @param update
	 *            A proper {@link EdgeAddition} to add the {@link Edge}.
	 */
	private void addToEdgeAdditionsForNewEdges(Edge edge, EdgeAddition update) {
		this.edgeAdditions.put(edge, update);
	}

	/**
	 * Adds the given {@link EdgeWeight} for given {@link Edge} to the updates
	 * to do in this batch. The {@link Edge} is then updated by
	 * {@link #generate(Graph)}.
	 * 
	 * @param edge
	 *            The {@link Edge} to update.
	 * @param update
	 *            A proper {@link EdgeWeight} to update the {@link Edge}.
	 */
	private void addToEdgeWeights(Edge edge, EdgeWeight update) {
		if (this.edgeWeights.containsKey(edge)) {
			final EdgeWeight ew = new EdgeWeight(
					(IWeightedEdge) this.edgeWeights.get(edge).getEdge(),
					update.getWeight());

			this.edgeWeights.put(edge, ew);
		} else {
			this.edgeWeights.put(edge, update);
		}

	}

	/**
	 * Adds the given {@link NodeAddition} for given {@link Node} to the updates
	 * to do in this batch. The {@link Node} is then added by
	 * {@link #generate(Graph)}.
	 * 
	 * @param node
	 *            The {@link Node} to add.
	 * @param update
	 *            A proper {@link NodeAddition} to add the {@link Node}.
	 */
	private void addToNodeAdditions(Node node, NodeAddition update) {
		// because nodes once added do not change or get deleted, the behaviour
		// of this method could be like #addToEdgeRemovals(Edge, EdgeRemoval) or
		// addToEdgeAdditionsForChangedEdges(Edge, EdgeAddition)
		if (!this.nodeAdditions.containsKey(node))
			this.nodeAdditions.put(node, update);
	}

	/**
	 * Adds an {@link UndirectedDoubleWeightedEdge} with given weight from given
	 * source to target {@link Node} to given graph. If the given graph contains
	 * this edge already, the weight of this edge is increased by given weight.
	 * <p>
	 * Instead of adding the edge directly to the graph, it is added to
	 * {@link #edgeAdditions} or {@link #edgeWeights}. {@link #generate(Graph)}
	 * adds these to the batch to update the graph.
	 * </p>
	 * 
	 * @param source
	 *            The source node of the edge to add.
	 * @param target
	 *            The target node of the edge to add.
	 * @param weight
	 *            The weight of the edge to add or -if the edge already exists-
	 *            the weight added to the current weight of the existing edge.
	 */
	private void addUndirectedEdge(Graph g, Node source, Node target,
			double weight) {
		final Edge edge = g.getGraphDatastructures().newEdgeInstance(source,
				target);

		final double newWeight = this.calculcateEdgeWeight(edge, weight);

		final UndirectedWeightedEdge weightedEdge = (UndirectedWeightedEdge) edge;
		if (edgesAdded.containsKey(edge)
				&& !this.edgeAdditions.containsKey(edge)) {
			weightedEdge.setWeight(new DoubleWeight(this
					.calculateFinalEdgeWeight((double) this.edgesAdded
							.get(edge))));

			if (g.containsEdge(edge))
				this.addToEdgeWeights(
						edge,
						new EdgeWeight((IWeightedEdge) g
								.getEdge(source, target), new DoubleWeight(this
								.calculateFinalEdgeWeight(newWeight))));
			else
				this.addToEdgeWeights(
						edge,
						new EdgeWeight(weightedEdge, new DoubleWeight(this
								.calculateFinalEdgeWeight(newWeight))));
		} else {
			weightedEdge.setWeight(new DoubleWeight(this
					.calculateFinalEdgeWeight(newWeight)));
			this.addToEdgeAdditionsForNewEdges(edge, new EdgeAddition(
					weightedEdge));
		}
		this.edgesAdded.put(edge, newWeight);
	}

	/**
	 * Adds an {@link UndirectedIntWeightedEdge} with given weight from given
	 * source to target {@link Node} to given graph. If the given graph contains
	 * this edge already, the weight of this edge is increased by given weight.
	 * <p>
	 * Instead of adding the edge directly to the graph, it is added to
	 * {@link #edgeAdditions} or {@link #edgeWeights}. {@link #generate(Graph)}
	 * adds these to the batch to update the graph.
	 * </p>
	 * 
	 * @param source
	 *            The source node of the edge to add.
	 * @param target
	 *            The target node of the edge to add.
	 * @param weight
	 *            The weight of the edge to add or -if the edge already exists-
	 *            the weight added to the current weight of the existing edge.
	 */
	private void addUndirectedEdge(Graph g, Node source, Node target, int weight) {
		final Edge edge = g.getGraphDatastructures().newEdgeInstance(source,
				target);

		final int newWeight = this.calculcateEdgeWeight(edge, weight);

		final UndirectedWeightedEdge weightedEdge = (UndirectedWeightedEdge) edge;
		if (edgesAdded.containsKey(edge)
				&& !this.edgeAdditions.containsKey(edge)) {
			weightedEdge
					.setWeight(new IntWeight(this
							.calculateFinalEdgeWeight((int) this.edgesAdded
									.get(edge))));

			if (g.containsEdge(edge))
				this.addToEdgeWeights(
						edge,
						new EdgeWeight((IWeightedEdge) g
								.getEdge(source, target), new IntWeight(this
								.calculateFinalEdgeWeight(newWeight))));
			else
				this.addToEdgeWeights(
						edge,
						new EdgeWeight(weightedEdge, new IntWeight(this
								.calculateFinalEdgeWeight(newWeight))));
		} else {
			weightedEdge.setWeight(new IntWeight(this
					.calculateFinalEdgeWeight(newWeight)));
			this.addToEdgeAdditionsForNewEdges(edge, new EdgeAddition(
					weightedEdge));
		}
		this.edgesAdded.put(edge, newWeight);
	}

	/**
	 * Sets
	 * <ul>
	 * <li>{@link #columnGroupsToAddAsNodes} based on
	 * {@code columnsToAddAsNodes} and {@code oneNodeForEachColumn}</li>
	 * <li>{@link #columnGroupsToCheckForEquality} based on
	 * {@code columnsToCheckForEquality} and {@code allColumnsMustBeEqual}</li>
	 * </ul>
	 * 
	 * @return Joined {@link #columnGroupsToAddAsNodes} and
	 *         {@link #columnGroupsToCheckForEquality}. Used as parameter for
	 *         new Mapping (so each value is globally unique mapped).
	 */
	private EventColumn[][] buildEventColumnGroups(
			EventColumn[] columnsToAddAsNodes, boolean oneNodeForEachColumn,
			EventColumn[] columnsToCheckForEquality,
			boolean allColumnsMustBeEqual) {
		// build groups for nodes

		if (oneNodeForEachColumn) {
			this.columnGroupsToAddAsNodes = new EventColumn[columnsToAddAsNodes.length][1];

			for (int i = 0; i < this.columnGroupsToAddAsNodes.length; i++)
				this.columnGroupsToAddAsNodes[i][0] = columnsToAddAsNodes[i];
		} else {
			this.columnGroupsToAddAsNodes = new EventColumn[1][columnsToAddAsNodes.length];

			this.columnGroupsToAddAsNodes[0] = columnsToAddAsNodes;
		}

		// build groups for edges

		if (allColumnsMustBeEqual) {
			this.columnGroupsToCheckForEquality = new EventColumn[1][columnsToCheckForEquality.length];

			this.columnGroupsToCheckForEquality[0] = columnsToCheckForEquality;
		} else {
			this.columnGroupsToCheckForEquality = new EventColumn[columnsToCheckForEquality.length][1];

			for (int i = 0; i < this.columnGroupsToCheckForEquality.length; i++)
				this.columnGroupsToCheckForEquality[i][0] = columnsToCheckForEquality[i];
		}

		// join all groups in one list for EventMappings

		final Collection<EventColumn[]> groups = new ArrayList<EventColumn[]>();
		groups.addAll(Arrays.asList(this.columnGroupsToAddAsNodes));
		groups.addAll(Arrays.asList(this.columnGroupsToCheckForEquality));

		return groups.toArray(new EventColumn[groups.size()][]);
	}

	/**
	 * Calculates the weight of an edge based on given weight and
	 * {@link #absoluteEdgeWeights} and returns the result.
	 */
	private double calculateFinalEdgeWeight(double weight) {
		if (this.absoluteEdgeWeights)
			return weight;
		else
			return 1 / weight;
	}

	/**
	 * {@code int}-equivalent for {@link #calculateFinalEdgeWeight(double)}. But
	 * because {@link #absoluteEdgeWeights} is irrelevant for {@code int}
	 * weighted edges, it's basically the given value which is returned.
	 */
	private int calculateFinalEdgeWeight(int weight) {
		// ignore this.absoluteEdgeWeights!
		return weight;
	}

	/**
	 * @return The new weight for the given edge as sum of its current weight
	 *         and given weight.
	 */
	private double calculcateEdgeWeight(Edge edge, double weight) {
		if (!this.edgesAdded.containsKey(edge))
			return weight;

		// new weight = current weight + given weight
		return (double) this.edgesAdded.get(edge) + weight;
	}

	/**
	 * @return The new weight for the given edge as sum of its current weight
	 *         and given weight.
	 */
	private int calculcateEdgeWeight(Edge edge, int weight) {
		if (!this.edgesAdded.containsKey(edge))
			return weight;

		// new weight = current weight + given weight
		return (int) this.edgesAdded.get(edge) + weight;
	}

	@Override
	public Batch generate(Graph g) {
		if (this.numberOfRuns == 0 && g.getNodeCount() > 0)
			throw new IllegalArgumentException(
					"To guarantee the correct meaning of the graph, it must be empty before the first generated batch and must only be modifidied by the same batch generator!");

		this.nodeAdditions.clear();
		this.edgeAdditions.clear();
		this.edgeWeights.clear();

		Batch b = new Batch(g.getGraphDatastructures(), this.timeOfBatch
				* this.numberOfRuns, this.timeOfBatch * (this.numberOfRuns + 1));

		Event currentEvent;
		for (int currentNumberOfEvents = 0; currentNumberOfEvents < this.numberOfLinesPerBatch; currentNumberOfEvents++) {
			if ((currentEvent = reader.readNext()) == null) {
				// no more events(although maxNumberOfEvents not reached
				// so far), close reader ...
				reader.close();
				// ... and stop
				break;
			}

			if (this.eventFilter != null
					&& !this.eventFilter.passes(currentEvent))
				// EventFilter exists and current event is blocked by it, skip
				// the current event
				continue;

			this.mappings.map(currentEvent);

			this.addNodesForColumns(g, currentEvent);

			this.addEdgesForColumns(g, currentEvent);
		}

		if (!this.nodeAdditions.isEmpty())
			b.addAll(this.nodeAdditions.values());

		// add all EdgeUpdates to the batch
		if (!this.edgeAdditions.isEmpty())
			b.addAll(this.edgeAdditions.values());
		if (!this.edgeWeights.isEmpty())
			b.addAll(this.edgeWeights.values());

		this.numberOfRuns++;

		return b;
	}

	/**
	 * <b>Note: returns always true!</b> If no further batch is possible,
	 * because there is no other event to read, {@link #generate(Graph)} returns
	 * an empty batch.
	 */
	@Override
	public boolean isFurtherBatchPossible(Graph g) {
		return true;
	}

	@Override
	public void reset() {
		this.nodesSortedByColumnGroupsToCheckForEquality = new EdgeValuesForNodes();
		this.edgesAdded.clear();

		this.nodesAdded.clear();

		this.numberOfRuns = 0;
		this.reader = new EventReader(this.eventsFilepath);

		this.nodeAdditions.clear();
		this.edgeAdditions.clear();
		this.edgeWeights.clear();
	}

}
