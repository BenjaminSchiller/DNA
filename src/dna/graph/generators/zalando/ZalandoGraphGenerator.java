package dna.graph.generators.zalando;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.Node;
import dna.graph.weights.DoubleWeight;
import dna.graph.weights.IntWeight;
import dna.graph.weights.Weight;

/**
 * The most general version of a {@link GraphGenerator} for Zalando log files.
 * It is the base for the more specific {@link GraphGenerator}s.
 * 
 * @see ZalandoEqualityGraphGenerator
 * @see ZalandoChronologyGraphGenerator
 */
abstract class ZalandoGraphGenerator extends GraphGenerator {

	/**
	 * In {@link #generate()} mapped {@link Event}s. The indices for the
	 * {@link Node}s of {@link #graph}.
	 */
	EventMappings mappings;

	/**
	 * The result of any {@link #generate()} call in any subclass of
	 * {@linkZalandoGraphGenerator}.
	 */
	Graph graph;

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
	int numberOfLinesOfOneTimestep;
	/**
	 * The number of timeunits to increase the current timestamp.
	 * 
	 * @see #numberOfLinesOfOneTimestep
	 */
	int timestep;

	// fields for EventReader

	/**
	 * The maximum number of events (i.e. lines of file) to read in. The actual
	 * number of lines may be smaller than this value.
	 * 
	 * @see #eventsFilepath
	 */
	int maxNumberOfEvents;
	/**
	 * The path of the file containing all events to read in.
	 * 
	 * @see #maxNumberOfEvents
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
	/** All edges added so for to {@link #graph} with their current weights. */
	private Map<Edge, Object> edgeWeights;

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
	 * @param maxNumberOfEvents
	 *            The maximum number of {@code Event}s used for graph
	 *            generation. It is the <u>maximum</u> number because the log
	 *            file may have fewer lines.
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
	public ZalandoGraphGenerator(String name, GraphDataStructure gds,
			long timestampInit, EventFilter eventFilter, int maxNumberOfEvents,
			String eventsFilepath, EventColumn[] columnsToAddAsNodes,
			boolean oneNodeForEachColumn,
			EventColumn[] columnsToCheckForEquality,
			boolean allColumnsMustBeEqual, boolean absoluteWeights) {
		super(buildName("Zalando" + name, gds), null, gds, timestampInit, 0, 0);

		this.mappings = new EventMappings(this.buildEventColumnGroups(
				columnsToAddAsNodes, oneNodeForEachColumn,
				columnsToCheckForEquality, allColumnsMustBeEqual));

		this.graph = this.gds.newGraphInstance(this.getName(),
				this.timestampInit, this.nodesInit, this.edgesInit);

		this.eventFilter = eventFilter;

		this.numberOfLinesOfOneTimestep = 1;
		this.timestep = 1;

		this.maxNumberOfEvents = maxNumberOfEvents;
		this.eventsFilepath = eventsFilepath;

		this.nodesSortedByColumnGroupsToCheckForEquality = new EdgeValuesForNodes();
		this.absoluteEdgeWeights = absoluteWeights;
		this.edgeWeights = new HashMap<Edge, Object>();
	}

	/**
	 * Adds an {@link DirectedDoubleWeightedEdge} with given weight from given
	 * source to target {@link Node} to {@link #graph}. If the graph contains
	 * this edge already, the weight of this edge is increased by given weight.
	 * 
	 * @param source
	 *            The source node of the edge to add.
	 * @param target
	 *            The target node of the edge to add.
	 * @param weight
	 *            The weight of the edge to add or -if the edge already exists-
	 *            the weight added to the current weight of the existing edge.
	 */
	private void addDirectedEdge(Node source, Node target, double weight) {
		final DirectedWeightedEdge edge = (DirectedWeightedEdge) this.gds
				.newWeightedEdge(source, target, new DoubleWeight(weight));
		final DoubleWeight newWeight = new DoubleWeight(
				this.calculcateEdgeWeight(edge, weight));

		if (this.graph.containsEdge(edge))
			((DirectedWeightedEdge) this.graph.getEdge(source, target))
					.setWeight(newWeight);
		else {
			edge.setWeight(newWeight);
			this.graph.addEdge(edge);
		}
	}

	/**
	 * Adds an {@link DirectedIntWeightedEdge} with given weight from given
	 * source to target {@link Node} to {@link #graph}. If the graph contains
	 * this edge already, the weight of this edge is increased by given weight.
	 * 
	 * @param source
	 *            The source node of the edge to add.
	 * @param target
	 *            The target node of the edge to add.
	 * @param weight
	 *            The weight of the edge to add or -if the edge already exists-
	 *            the weight added to the current weight of the existing edge.
	 */
	private void addDirectedEdge(Node source, Node target, int weight) {
		final DirectedWeightedEdge edge = (DirectedWeightedEdge) this.gds
				.newWeightedEdge(source, target, new IntWeight(weight));
		final IntWeight newWeight = new IntWeight(this.calculcateEdgeWeight(
				edge, weight));

		if (this.graph.containsEdge(edge))
			((DirectedWeightedEdge) this.graph.getEdge(source, target))
					.setWeight(newWeight);
		else {
			edge.setWeight(newWeight);
			this.graph.addEdge(edge);
		}
	}

	/**
	 * Adds an {@link Edge} with given weight from given source to target
	 * {@link Node} to {@link #graph}. Depending on the
	 * {@link GraphDataStructure} the edge is directed or undirected. If the
	 * graph contains this edge already, the weight of this edge is increased by
	 * given weight.
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
	void addEdge(Node source, Node target, Object weight) {
		final Class<? extends Edge> edgeType = this.gds.getEdgeType();
		final Class<? extends Weight> edgeWeightType = this.gds
				.getEdgeWeightType();

		if (edgeType.equals(DirectedWeightedEdge.class)
				&& edgeWeightType.equals(DoubleWeight.class))
			this.addDirectedEdge(source, target,
					Double.valueOf(weight.toString()));
		else if (edgeType.equals(DirectedWeightedEdge.class)
				&& edgeWeightType.equals(IntWeight.class))
			this.addDirectedEdge(source, target,
					Integer.valueOf(weight.toString()));
		else if (edgeType.equals(UndirectedWeightedEdge.class)
				&& edgeWeightType.equals(DoubleWeight.class))
			this.addUndirectedEdge(source, target,
					Double.valueOf(weight.toString()));
		else if (edgeType.equals(UndirectedWeightedEdge.class)
				&& edgeWeightType.equals(IntWeight.class))
			this.addUndirectedEdge(source, target,
					Integer.valueOf(weight.toString()));
	}

	abstract void addEdgesForColumns(Event currentEvent);

	/**
	 * Adds the {@link Node}s for given {@link Event} to {@link #graph}.
	 * <p>
	 * Additionally groups added {@link Node}s into
	 * {@link #nodesSortedByColumnGroupsToCheckForEquality} for
	 * {@link #addEdgesForColumns(Event)}.
	 * </p>
	 * 
	 * @param event
	 *            The {@link Event} which nodes should be added.
	 */
	private void addNodesForColumns(Event event) {
		int globalMappingForEvent;
		for (EventColumn[] eventColumnGroup : this.columnGroupsToAddAsNodes) {
			globalMappingForEvent = this.mappings.getGlobalMapping(
					eventColumnGroup, event);

			this.graph.addNode(this.gds.newNodeInstance(globalMappingForEvent));

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
	 * Adds an {@link UndirectedDoubleWeightedEdge} with given weight from given
	 * source to target {@link Node} to {@link #graph}. If the graph contains
	 * this edge already, the weight of this edge is increased by given weight.
	 * 
	 * @param source
	 *            The source node of the edge to add.
	 * @param target
	 *            The target node of the edge to add.
	 * @param weight
	 *            The weight of the edge to add or -if the edge already exists-
	 *            the weight added to the current weight of the existing edge.
	 */
	private void addUndirectedEdge(Node source, Node target, double weight) {
		final UndirectedWeightedEdge edge = (UndirectedWeightedEdge) this.gds
				.newWeightedEdge(source, target, new DoubleWeight(weight));
		final DoubleWeight newWeight = new DoubleWeight(
				this.calculcateEdgeWeight(edge, weight));

		if (this.graph.containsEdge(edge))
			((UndirectedWeightedEdge) this.graph.getEdge(source, target))
					.setWeight(newWeight);
		else {
			edge.setWeight(newWeight);
			this.graph.addEdge(edge);
		}
	}

	/**
	 * Adds an {@link UndirectedIntWeightedEdge} with given weight from given
	 * source to target {@link Node} to {@link #graph}. If the graph contains
	 * this edge already, the weight of this edge is increased by given weight.
	 * 
	 * @param source
	 *            The source node of the edge to add.
	 * @param target
	 *            The target node of the edge to add.
	 * @param weight
	 *            The weight of the edge to add or -if the edge already exists-
	 *            the weight added to the current weight of the existing edge.
	 */
	private void addUndirectedEdge(Node source, Node target, int weight) {
		final UndirectedWeightedEdge edge = (UndirectedWeightedEdge) this.gds
				.newEdgeInstance(source, target);
		final IntWeight newWeight = new IntWeight(this.calculcateEdgeWeight(
				edge, weight));

		if (this.graph.containsEdge(edge))
			((UndirectedWeightedEdge) this.graph.getEdge(source, target))
					.setWeight(newWeight);
		else {
			edge.setWeight(newWeight);
			this.graph.addEdge(edge);
		}
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
	 * @return The new weight for the given edge as sum of its current weight
	 *         and given weight.
	 */
	private double calculcateEdgeWeight(Edge edge, double weight) {
		if (!this.edgeWeights.containsKey(edge))
			this.edgeWeights.put(edge, .0);

		// new weight = current weight + given weight
		final double newWeight = (double) this.edgeWeights.get(edge) + weight;

		this.edgeWeights.put(edge, newWeight);

		if (this.absoluteEdgeWeights)
			return newWeight;
		else
			return 1 / newWeight;
	}

	/**
	 * @return The new weight for the given edge as sum of its current weight
	 *         and given weight.
	 */
	private int calculcateEdgeWeight(Edge edge, int weight) {
		if (!this.edgeWeights.containsKey(edge))
			this.edgeWeights.put(edge, 0);

		// new weight = current weight + given weight
		final int newWeight = (int) this.edgeWeights.get(edge) + weight;

		this.edgeWeights.put(edge, newWeight);

		// ignore this.absoluteEdgeWeights !
		return newWeight;
	}

	@Override
	public Graph generate() {
		final EventReader reader = new EventReader(this.eventsFilepath);

		Event currentEvent;
		for (int currentNumberOfEvents = 0; currentNumberOfEvents < this.maxNumberOfEvents; currentNumberOfEvents++) {
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

			if ((this.numberOfLinesOfOneTimestep >= 1)
					&& (((currentNumberOfEvents + 1) % this.numberOfLinesOfOneTimestep) == 0))
				// again numberOfLinesOfOneTimestep lines red, increase
				// timestamp by timestep
				this.graph.setTimestamp(this.graph.getTimestamp()
						+ this.timestep);

			this.mappings.map(currentEvent);

			this.addNodesForColumns(currentEvent);

			this.addEdgesForColumns(currentEvent);
		}

		return this.graph;
	}

}
