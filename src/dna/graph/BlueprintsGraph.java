package dna.graph;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.SystemUtils;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.lambdazen.bitsy.BitsyGraph;
import com.orientechnologies.orient.core.Orient;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphFactory;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

import dna.graph.DNAGraphFactory.DNAGraphType;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.edges.DirectedBlueprintsEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.IGDBEdge;
import dna.graph.edges.UndirectedBlueprintsEdge;
import dna.graph.nodes.DirectedBlueprintsNode;
import dna.graph.nodes.IGDBNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedBlueprintsNode;
import dna.graph.weights.IWeighted;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.IWeightedNode;
import dna.util.IOUtils;
import dna.util.Log;
import dna.util.Rand;

/**
 * Wrapper class for graph databases that implement the tinkerpop blueprints
 * interface com.tinkerpop.blueprints.Graph.
 *
 * @author Matthias
 *
 */

public class BlueprintsGraph implements IGraph, IGDBGraph<Graph> {

	/**  clear workspace for persistent graphs on close. */
	private boolean clearWorkSpaceOnClose;

	/** The edge count. */
	private int edgeCount;

	/** The edges. */
	private HashMap<Object, IElement> edges = null;

	/** The graph data structure. */
	private GraphDataStructure gds;

	/** The blueprints graph. */
	private Graph graph;

	/** The graph type. */
	private DNAGraphFactory.DNAGraphType graphType;

	/** The max node index. */
	private int maxNodeIndex;

	/** The name. */
	private String name;

	/** The node count. */
	private int nodeCount;

	/** The nodes. */
	private HashMap<Object, IElement> nodes = null;

	private Boolean storeDNAElementsInGDB = false;

	/** The operations per commit. */
	private int operationsPerCommit;

	/** The operations since last commit. */
	private int operationsSinceLastCommit;

	/** The timestamp. */
	private long timestamp;

	/** The workspace dir. */
	private String workspaceDir = null;

	/**
	 * Instantiates a new blueprints graph.
	 *
	 * @param name the name of the graph
	 * @param timestamp the timestamp for the graph
	 * @param gds the graph data structure
	 * @param graphType
	 *            the graph database type<br>
	 *            supported are:
	 *            <ul>
	 *            <li>BITSY_DURABLE</li>
	 *            <li>BITSY_NON_DURABLE</li>
	 *            <li>NEO4J2</li>
	 *            <li>ORIENTDBNOTX</li>
	 *            <li>TINKERGRAPH</li>
	 *            </ul>
	 */
	public BlueprintsGraph(String name, long timestamp, GraphDataStructure gds,
			DNAGraphFactory.DNAGraphType graphType) {
		this.init(name, timestamp, gds, 0, 0, graphType, 0, true, null, false);
	}

	/**
	 * Instantiates a new blueprints graph.
	 *
	 * @param name the name of the graph
	 * @param timestamp the timestamp for the graph
	 * @param gds the graphdatastructure
	 * @param graphType the graph database type<br>
	 *            supported are:
	 *            <ul>
	 *            <li>BITSY_DURABLE</li>
	 *            <li>BITSY_NON_DURABLE</li>
	 *            <li>NEO4J2</li>
	 *            <li>ORIENTDBNOTX</li>
	 *            <li>TINKERGRAPH</li>
	 *            </ul>
	 * @param storeDNAElementsInGDB defines whether DNA elements (nodes, edges) should be stored in the
	 * 			  graph database or not, if possible<br>
	 * 			  applies only for:
	 * 			  <ul>
	 *            <li>BITSY_NON_DURABLE</li>
	 *            <li>TINKERGRAPH</li>
	 *            </ul>
	 */
	public BlueprintsGraph(String name, long timestamp, GraphDataStructure gds, DNAGraphFactory.DNAGraphType graphType,
			Boolean storeDNAElementsInGDB) {
		this.init(name, timestamp, gds, 0, 0, graphType, 0, false, "", storeDNAElementsInGDB);
	}


	/**
	 * Instantiates a new blueprints graph.
	 *
	 * @param graphType the graph database type<br>
	 *            supported are:
	 *            <ul>
	 *            <li>BITSY_DURABLE</li>
	 *            <li>BITSY_NON_DURABLE</li>
	 *            <li>NEO4J2</li>
	 *            <li>ORIENTDBNOTX</li>
	 *            <li>TINKERGRAPH</li>
	 *            </ul>
	 * @param name the name of the graph
	 * @param timestamp the timestamp for the graph
	 * @param gds the graphdatastructure
	 * @param operationsPerCommit defines how many operations are executed
	 * 			  till a commit will be executed
	 * 			  <ul>
	 *            <li>X < 0:no commit</li>
	 *            <li>X = 0:commit on close</li>
	 *            <li>X > 0:commit every X operations</li>
	 *            </ul>
	 * @param clearWorkSpace clear workspace after the graph was closed
	 * 			  applies only for :
	 * 			  <ul>
	 *            <li>BITSY_DURABLE</li>
	 *            <li>NEO4J2</li>
	 *            </ul>
	 * @param workspace the workspace directoy
	 * @param storeDNAElementsInGDB defines whether DNA elements (nodes, edges) should be stored in the
	 * 			  graph database or not, if possible<br>
	 * 			  applies only for:
	 * 			  <ul>
	 *            <li>BITSY_NON_DURABLE</li>
	 *            <li>TINKERGRAPH</li>
	 *            </ul>
	 */
	public BlueprintsGraph(String name, long timestamp, GraphDataStructure gds,
			DNAGraphFactory.DNAGraphType graphType, int operationsPerCommit,
			boolean clearWorkSpace, String workspace, Boolean storeDNAElementsInGDB) {
		this.init(name, timestamp, gds, 0, 0, graphType, operationsPerCommit, clearWorkSpace, workspace, storeDNAElementsInGDB);
	}

	/**
	 * Instantiates a new blueprints graph.
	 *
	 * @param graphType the graph database type<br>
	 *            supported are:
	 *            <ul>
	 *            <li>BITSY_DURABLE</li>
	 *            <li>BITSY_NON_DURABLE</li>
	 *            <li>NEO4J2</li>
	 *            <li>ORIENTDBNOTX</li>
	 *            <li>TINKERGRAPH</li>
	 *            </ul>
	 * @param name the name of the graph
	 * @param timestamp the timestamp for the graph
	 * @param gds the graphdatastructure
	 * @param nodeSize the number of nodes
	 * @param edgeSize the number of edges
	 * @param operationsPerCommit defines how many operations are executed
	 * 			  till a commit will be executed
	 * 			  <ul>
	 *            <li>X < 0:no commit</li>
	 *            <li>X = 0:commit on close</li>
	 *            <li>X > 0:commit every X operations</li>
	 *            </ul>
	 * @param clearWorkSpace clear workspace after the graph was closed
	 * 			  applies only for :
	 * 			  <ul>
	 *            <li>BITSY_DURABLE</li>
	 *            <li>NEO4J2</li>
	 *            </ul>
	 * @param workspace the workspace directoy
	 * @param storeDNAElementsInGDB defines whether DNA elements (nodes, edges) should be stored in the
	 * 			  graph database or not, if possible<br>
	 * 			  applies only for:
	 * 			  <ul>
	 *            <li>BITSY_NON_DURABLE</li>
	 *            <li>TINKERGRAPH</li>
	 *            </ul>
	 */
	public BlueprintsGraph(String name, long timestamp, GraphDataStructure gds, int nodeSize, int edgeSize,
			DNAGraphFactory.DNAGraphType graphType, int operationsPerCommit, boolean clearWorkSpace,
			String workspace, Boolean storeDNAElementsInGDB) {
		this.init(name, timestamp, gds, nodeSize, edgeSize, graphType, operationsPerCommit, clearWorkSpace, workspace, storeDNAElementsInGDB);
	}

	/**
	 * Instantiates a new blueprints graph.
	 *
	 * @param graphType the graph database type<br>
	 *            supported are:
	 *            <ul>
	 *            <li>BITSY_DURABLE</li>
	 *            <li>BITSY_NON_DURABLE</li>
	 *            <li>NEO4J2</li>
	 *            <li>ORIENTDBNOTX</li>
	 *            <li>TINKERGRAPH</li>
	 *            </ul>
	 * @param name the name of the graph
	 * @param timestamp the timestamp for the graph
	 * @param gds the graphdatastructure
	 * @param nodeSize the number of nodes
	 * @param edgeSize the number of edges
	 */
	public BlueprintsGraph(DNAGraphType graphType, String name, long timestamp, GraphDataStructure gds,
			int nodeSize, int edgeSize) {
		this.init(name, timestamp, gds, nodeSize, edgeSize, graphType, 0, false, "", false);
	}

	/**
	 * Returns the blueprints graph database describe with the .
	 *
	 * @param conf the configuration file
	 * @return the blueprints graph database
	 */
	public static Graph getGDB(Configuration conf) {
		return GraphFactory.open(conf);
	}

	/**
	 * Returns a new instance of a graph database which was given by the
	 * parameter 'graphType'.
	 *
	 * @param graphType            the graph database type<br>
	 *            supported are:
	 *            <ul>
	 *            <li>BITSY_DURABLE</li>
	 *            <li>BITSY_NON_DURABLE</li>
	 *            <li>NEO4J2</li>
	 *            <li>ORIENTDBNOTX</li>
	 *            <li>TINKERGRAPH</li>
	 *            </ul>
	 * @param name the name
	 * @param workspace the workspace
	 * @return a new instance of the {@link Graph}
	 */
	public static Graph getGDB(DNAGraphType graphType, String name, String workspace) {
		Configuration conf = new BaseConfiguration();

		switch (graphType) {
		case BITSY_DURABLE:
			if (workspace == "") {
				workspace = SystemUtils.getJavaIoTmpDir().getAbsolutePath()
						+ IOUtils.getPathForOS("/GDB/" + name + "/");
			}
			Path dbPath = Paths.get(workspace);

			new File(dbPath.toAbsolutePath().toString()).mkdirs();
			return new BitsyGraph(dbPath);
		case BITSY_NON_DURABLE:
			return new BitsyGraph();
		case NEO4J2:
			if (workspace == "") {
				workspace = SystemUtils.getJavaIoTmpDir().getAbsolutePath()
						+ IOUtils.getPathForOS("/GDB/" + name + "/");
			}

			conf.setProperty("blueprints.graph", "com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph");
			conf.setProperty("blueprints.neo4j.directory", workspace + name + "/");
			break;
		case ORIENTDBNOTX:
			Orient.instance().startup();
			conf.setProperty("blueprints.graph", "com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx");
			conf.setProperty("blueprints.orientdb.url",
					"memory:" + name + new Timestamp(System.currentTimeMillis()).toString());
			break;
		case TINKERGRAPH:
			conf.setProperty("blueprints.graph", "com.tinkerpop.blueprints.impls.tg.TinkerGraph");
			break;
		default:
			throw new RuntimeException("Choose a valid database!");
		}
		return GraphFactory.open(conf);
	}

	/**
	 * Returns the DNAGraphType to a given {@link Graph} instance.
	 *
	 * @param graph
	 *            the graph instance
	 * @return the type of graph
	 */
	public static DNAGraphType getTypeOfGraph(com.tinkerpop.blueprints.Graph graph) {
		if (graph instanceof BitsyGraph)
			if (((BitsyGraph) graph).isPersistent())
				return DNAGraphType.BITSY_DURABLE;
			else
				return DNAGraphType.BITSY_NON_DURABLE;
		else if (graph instanceof Neo4j2Graph)
			return DNAGraphType.NEO4J2;
		else if (graph instanceof OrientGraphNoTx)
			return DNAGraphType.ORIENTDBNOTX;
		else if (graph instanceof TinkerGraph)
			return DNAGraphType.TINKERGRAPH;

		return null;
	}

	/**
	 * Supported dna graph types.
	 *
	 * @return the collection
	 */
	public static Collection<DNAGraphType> supportedDNAGraphTypes() {
		List<DNAGraphType> result = new ArrayList<DNAGraphType>();
		result.add(DNAGraphType.BITSY_DURABLE);
		result.add(DNAGraphType.BITSY_NON_DURABLE);
		result.add(DNAGraphType.NEO4J2);
		result.add(DNAGraphType.ORIENTDBNOTX);
		result.add(DNAGraphType.TINKERGRAPH);

		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#addEdge(dna.graph.edges.Edge)
	 */
	@Override
	public boolean addEdge(Edge e) {
		if (e == null || !(e instanceof IGDBEdge) || (e instanceof IGDBEdge && this.containsEdge(e))) {
			return false;
		}

		Vertex src = (Vertex) ((IGDBNode<?>) e.getN1()).getGDBNode();
		Vertex dst = (Vertex) ((IGDBNode<?>) e.getN2()).getGDBNode();
		com.tinkerpop.blueprints.Edge edge = this.graph.addEdge(null, src, dst, "IGDBEdge");

		if (edge != null) {
			// commit
			operationsSinceLastCommit++;
			if (operationsPerCommit == operationsSinceLastCommit && this.graphType != DNAGraphType.BITSY_DURABLE
					&& this.graphType != DNAGraphType.BITSY_NON_DURABLE) {
				this.commit();
				operationsSinceLastCommit = 0;
			}

			this.edgeCount++;

			if (e instanceof DirectedBlueprintsEdge)
				((DirectedBlueprintsEdge) e).setGDBEdgeId(edge.getId());
			else if (e instanceof UndirectedBlueprintsEdge)
				((UndirectedBlueprintsEdge) e).setGDBEdgeId(edge.getId());

			((IGDBEdge<?>) e).setGraph(this);

			edge.setProperty("directed", this.isDirected());

			if (e instanceof IWeightedEdge) {
				edge.setProperty("weight", ((IWeighted) e).getWeight().asString());
			}

			if (this.storeInGDB()) {
				edge.setProperty("edge", e);
			} else {
				edges.put(edge.getId(), e);
			}

			src = null;
			dst = null;
			edge = null;

			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#addNode(dna.graph.nodes.Node)
	 */
	@Override
	public boolean addNode(Node n) {
		if (n == null || !(n instanceof IGDBNode) || (n instanceof IGDBNode && this.containsNode(n))) {
			return false;
		}

		Vertex vertex = this.graph.addVertex(null);

		if (vertex != null) {
			// commit
			operationsSinceLastCommit++;
			if (operationsPerCommit == operationsSinceLastCommit && this.graphType != DNAGraphType.BITSY_DURABLE
					&& this.graphType != DNAGraphType.BITSY_NON_DURABLE) {
				this.commit();
				operationsSinceLastCommit = 0;
			}

			this.nodeCount++;

			if (n.getIndex() > this.maxNodeIndex) {
				this.maxNodeIndex = n.getIndex();
			}

			if (n instanceof DirectedBlueprintsNode) {
				((DirectedBlueprintsNode) n).setGDBNodeId(vertex.getId());
			} else if (n instanceof UndirectedBlueprintsNode) {
				((UndirectedBlueprintsNode) n).setGDBNodeId(vertex.getId());
			}

			((IGDBNode<?>) n).setGraph(this);

			vertex.setProperty("index", n.getIndex());
			vertex.setProperty("directed", this.isDirected());

			if (n instanceof IWeightedNode) {
				vertex.setProperty("weight", ((IWeighted) n).getWeight().asString());
			}

			if (this.storeInGDB()) {
				vertex.setProperty("node", n);
			} else {
				nodes.put(vertex.getId(), n);
			}

			vertex = null;
			n = null;

			return true;
		}

		return false;
	}

	/**
	 * Defines whether or not the workspace should be cleared.
	 *
	 * @return the clearWorkSpaceOnClose
	 */
	public boolean clearWorkSpaceOnClose() {
		return clearWorkSpaceOnClose;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#close()
	 */
	public void close() {
		if (this.graph != null) {
			if (operationsPerCommit >= 0)
				this.commit();

			switch (graphType) {
			case ORIENTDBNOTX:
				((OrientGraphNoTx) this.graph).drop();
				this.graph.shutdown();
				if (Orient.instance().getStorages().isEmpty())
					Orient.instance().shutdown();
				break;
			default:
				this.graph.shutdown();
				break;
			}

			if (clearWorkSpaceOnClose()) {
				try {
					if (workspaceDir != null && !(workspaceDir.trim().isEmpty())) {
						File workSpaceDir = new File(this.workspaceDir);
						if (workSpaceDir.exists())
							IOUtils.removeRecursive(workSpaceDir.getAbsolutePath(), 10);
					}
				} catch (Exception ex) {

				}
			}
		}
		this.graph = null;
		System.gc();
		System.gc();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGDBGraph#commit()
	 */
	@Override
	public void commit() {
		switch (graphType) {
		case BITSY_DURABLE:
		case BITSY_NON_DURABLE:
			((BitsyGraph) this.graph).commit();
			break;
		case NEO4J2:
			((Neo4j2Graph) this.graph).commit();
			break;
		case ORIENTDBNOTX:
			((OrientGraphNoTx) this.graph).commit();
			break;
		case TINKERGRAPH:
			break;
		default:
			break;
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#containsEdge(dna.graph.edges.Edge)
	 */
	@Override
	public boolean containsEdge(Edge edge) {
		if (edge instanceof IGDBEdge)
			return containsEdge(edge.getN1(), edge.getN2());

		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#containsEdge(int, int)
	 */
	@Override
	public boolean containsEdge(int n1, int n2) {
		return containsEdge(getNode(n1), getNode(n2));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#containsEdge(dna.graph.nodes.Node,
	 * dna.graph.nodes.Node)
	 */
	@Override
	public boolean containsEdge(Node n1, Node n2) {
		if (n1 instanceof IGDBNode<?> && n2 instanceof IGDBNode<?>) {
			return containsEdge((Vertex) ((IGDBNode<?>) n1).getGDBNode(), (Vertex) ((IGDBNode<?>) n2).getGDBNode());
		}

		return false;
	}

	/**
	 * Contains edge.
	 *
	 * @param v1 vertex 1
	 * @param v2 vertex 2
	 * @return true, if successful
	 */
	private boolean containsEdge(Vertex v1, Vertex v2) {
		if (v1 == null || v2 == null)
			return false;

		return getEdge(v1, v2) != null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#containsNode(dna.graph.nodes.Node)
	 */
	@Override
	public boolean containsNode(Node n) {
		if (n instanceof IGDBNode) {
			return ((IGDBNode<?>) n).getGDBNode() != null
					&& this.graph.getVertex(((Vertex) ((IGDBNode<?>) n).getGDBNode()).getId()) != null;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#containsNodes(dna.graph.edges.Edge)
	 */
	@Override
	public boolean containsNodes(Edge e) {
		if (this.isDirected()) {
			if (e instanceof DirectedBlueprintsEdge) {
				return containsNode(((DirectedBlueprintsEdge) e).getDst())
						&& containsNode(((DirectedBlueprintsEdge) e).getSrc());
			}
		} else if (e instanceof UndirectedBlueprintsEdge) {
			return containsNode(((UndirectedBlueprintsEdge) e).getNode1())
					&& containsNode(((UndirectedBlueprintsEdge) e).getNode2());
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		Log.debug("Running equality check for graphs");

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		BlueprintsGraph other = (BlueprintsGraph) obj;

		if (gds == null) {
			if (other.gds != null) {
				return false;
			}
		} else if (!gds.equals(other.gds)) {
			return false;
		}
		if (timestamp != other.timestamp) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}

		Log.debug("Basics equal, going for edges and nodes");

		if (getEdges() == null) {
			if (other.getEdges() != null) {
				return false;
			}
		} else if (!Iterables.elementsEqual(getEdges(), other.getEdges())) {
			Log.debug("Edges not equal (type: " + getEdges().getClass() + ")");
			return false;
		}
		if (getNodes() == null) {
			if (other.getNodes() != null) {
				return false;
			}
		} else if (!Iterables.elementsEqual(getNodes(), other.getNodes())) {
			Log.debug("Nodes not equal (type: " + getNodes().getClass() + ")");
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		try {
			close();
		} finally {
			super.finalize();
		}
	}

	/**
	 * Gets the edge.
	 *
	 * @param e
	 *            the e
	 * @return the edge
	 */
	public Edge getEdge(com.tinkerpop.blueprints.Edge e) {
		if (e != null) {
			if (this.storeInGDB()) {
				return e.getProperty("edge");
			} else {
				return (Edge) edges.get(e.getId());
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#getEdge(dna.graph.nodes.Node, dna.graph.nodes.Node)
	 */
	@Override
	public Edge getEdge(Node n1, Node n2) {
		Iterable<com.tinkerpop.blueprints.Edge> gdbEdges = this.graph.getEdges();
		Vertex src = null;
		Vertex dst = null;

		try {
			for (com.tinkerpop.blueprints.Edge e : gdbEdges) {
				src = e.getVertex(Direction.OUT);
				dst = e.getVertex(Direction.IN);

				if ((int) src.getProperty("index") == n1.getIndex()
						&& (int) dst.getProperty("index") == n2.getIndex()) {

					return getEdge(e);
				}
			}

			return null;
		} finally {
			gdbEdges = null;
			src = null;
			dst = null;
		}
	}

	/**
	 * Gets the edge.
	 *
	 * @param v1 the first node
	 * @param v2 the second node
	 * @return the edge
	 */
	private Edge getEdge(Vertex v1, Vertex v2) {
		Iterable<com.tinkerpop.blueprints.Edge> edges = v1.getEdges(Direction.OUT, "IGDBEdge");
		try {
			for (com.tinkerpop.blueprints.Edge e : edges) {
				if (e.getVertex(Direction.IN).equals(v2))
					return getEdge(e);
			}

			return null;
		} finally {
			edges = null;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#getEdgeCount()
	 */
	@Override
	public int getEdgeCount() {
		return this.edgeCount;
	}

	/**
	 * Gets the edge count from db.
	 *
	 * @return the edge count from db
	 */
	public int getEdgeCountFromDB() {
		return Iterables.size(this.graph.getEdges());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#getEdges()
	 */
	@Override
	public Iterable<IElement> getEdges() {
		if (!this.storeInGDB()) {
			return edges.values();
		}

		return Iterables.transform(this.graph.getEdges(), new Function<com.tinkerpop.blueprints.Edge, IElement>() {
			@Override
			public IElement apply(final com.tinkerpop.blueprints.Edge input) {
				return getEdge(input);
			}
		});
	}

	/**
	 * Returns the graph database edge associated with the given id.
	 *
	 * @param gbdEdgeId the graph database edge id
	 * @return the graph database edge
	 */
	public com.tinkerpop.blueprints.Edge getGDBEdge(Object gdbEdgeId) {
		if (this.graph == null || gdbEdgeId == null)
			return null;
		else
			return this.graph.getEdge(gdbEdgeId);
	}

	/**
	 * Returns the graph database node associated with the given id.
	 *
	 * @param gdbNodeId the graph database node id
	 * @return the graph database node
	 */
	public Vertex getGDBNode(Object gdbNodeId) {
		if (this.graph == null || gdbNodeId == null)
			return null;
		else
			return this.graph.getVertex(gdbNodeId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGDBGraph#getGraphDatabaseInstance()
	 */
	@Override
	public Graph getGraphDatabaseInstance() {
		return this.graph;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGDBGraph#getGraphDatabaseType()
	 */
	@Override
	public DNAGraphType getGraphDatabaseType() {
		return this.graphType;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#getGraphDatastructures()
	 */
	@Override
	public GraphDataStructure getGraphDatastructures() {
		return this.gds;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#getType()
	 */
	@Override
	public DNAGraphType getInstanceType() {
		return this.graphType;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#getMaxEdgeCount()
	 */
	@Override
	public BigInteger getMaxEdgeCount() {
		int nodeCnt = this.getNodeCount();

		BigInteger res = BigInteger.valueOf(nodeCnt);
		res = res.multiply(BigInteger.valueOf(nodeCnt - 1));
		if (!this.isDirected()) {
			res = res.divide(BigInteger.valueOf(2));
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#getMaxNodeIndex()
	 */
	@Override
	public int getMaxNodeIndex() {
		return this.maxNodeIndex;
	}

	/**
	 * Gets the max node index from db.
	 *
	 * @return the max node index from db
	 */
	public int getMaxNodeIndexFromDB() {
		int max = -1;
		for (IElement node : this.getNodes()) {
			maxNodeIndex = Math.max(max, ((Node) node).getIndex());
		}
		return max;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#getNode(int)
	 */
	@Override
	public Node getNode(int index) {
		Iterable<Vertex> vertices = this.graph.getVertices("index", index);

		//at most one Vertex should be included
		Vertex v = Iterables.getFirst(vertices, null);

		return v == null ? null : getNode(v);
	}

	/**
	 * Returns the node associated with the vertex.
	 *
	 * @param v vertex
	 * @return the node
	 */
	public Node getNode(Vertex v) {
		if (v != null) {
			if (this.storeInGDB()) {
				return v.getProperty("node");
			} else {
				return (Node) nodes.get(v.getId());
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#getNodeCount()
	 */
	@Override
	public int getNodeCount() {
		return nodeCount;
	}

	/**
	 * Gets the node count from database.
	 *
	 * @return the node count from database
	 */
	public int getNodeCountFromDB() {
		return Iterables.size(this.graph.getVertices());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#getNodes()
	 */
	@Override
	public Iterable<IElement> getNodes() {
		if (!this.storeInGDB()) {
			return nodes.values();
		}

		return Iterables.transform(this.graph.getVertices(), new Function<Vertex, IElement>() {
			@Override
			public IElement apply(final Vertex input) {
				return getNode(input);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#getRandomEdge()
	 */
	@Override
	public Edge getRandomEdge() {
		if (getEdgeCount() <= 0)
			return null;

		if (this.storeInGDB()) {
			Iterable<com.tinkerpop.blueprints.Edge> gdbEdges = this.graph.getEdges();

			com.tinkerpop.blueprints.Edge edge = Iterables.get(gdbEdges, Rand.rand.nextInt(Iterables.size(gdbEdges)));
			while (edge == null) {
				edge = Iterables.get(gdbEdges, Rand.rand.nextInt(Iterables.size(gdbEdges)));
			}
			return getEdge(edge);
		} else {
			return (Edge) Iterables.get(edges.values(), Rand.rand.nextInt(edges.size()));
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#getRandomNode()
	 */
	@Override
	public Node getRandomNode() {
		if (getNodeCount() <= 0)
			return null;

		if (this.storeInGDB()) {
			Iterable<Vertex> gdbVertices = this.graph.getVertices();

			Vertex vertex = Iterables.get(gdbVertices, Rand.rand.nextInt(Iterables.size(gdbVertices)));
			while (vertex == null) {
				vertex = Iterables.get(gdbVertices, Rand.rand.nextInt(Iterables.size(gdbVertices)));
			}
			return getNode(vertex);
		} else {
			return (Node) Iterables.get(nodes.values(), Rand.rand.nextInt(nodes.size()));
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#getTimestamp()
	 */
	@Override
	public long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * Gets the workspace dir.
	 *
	 * @return the workspace
	 */
	public String getWorkspaceDir() {
		return workspaceDir;
	}

	/**
	 * Initialize all necessary variables.
	 *
	 * @param name the name of the graph
	 * @param timestamp the timestamp for the graph
	 * @param gds the graphdatastructure
	 * @param graphType the graph database type<br>
	 *            supported are:
	 *            <ul>
	 *            <li>BITSY_DURABLE</li>
	 *            <li>BITSY_NON_DURABLE</li>
	 *            <li>NEO4J2</li>
	 *            <li>ORIENTDBNOTX</li>
	 *            <li>TINKERGRAPH</li>
	 *            </ul>
	 * @param operationsPerCommit 			  defines how many operations are executed
	 * 			  till a commit will be executed
	 * 			  <ul>
	 *            <li>X < 0:no commit</li>
	 *            <li>X = 0:commit on close</li>
	 *            <li>X > 0:commit every X operations</li>
	 *            </ul>
	 * @param clearWorkSpaceOnClose the clear work space on close
	 * @param workspace the workspace directoy
	 */
	private void init(String name, long timestamp, GraphDataStructure gds, int nodeSize, int edgeSize,
			DNAGraphType graphType, int operationsPerCommit, boolean clearWorkSpaceOnClose,
			String workspace, Boolean storeDNAElementsInGDB) {
		this.name = name;
		this.timestamp = timestamp;
		this.gds = gds;
		this.graphType = graphType;
		this.maxNodeIndex = -1;
		this.nodeCount = 0;
		this.edgeCount = 0;
		this.operationsPerCommit = operationsPerCommit;
		this.operationsSinceLastCommit = 0;
		this.storeDNAElementsInGDB = storeDNAElementsInGDB;
		this.clearWorkSpaceOnClose = clearWorkSpaceOnClose;
		this.workspaceDir = workspace;
		if (!this.storeInGDB()) {
			// Vertex.getId() --> Node
			nodes = new HashMap<Object,IElement>(nodeSize <= 0 ? 16 : nodeSize);
			// Edge.getId() --> Edge
			edges = new HashMap<Object,IElement>(edgeSize <= 0 ? 16 : edgeSize);
		}
		this.graph = BlueprintsGraph.getGDB(this.graphType, name, workspace);
	}

	private boolean storeInGDB()
	{
		return this.graphType.supportsObjectAsProperty() && storeDNAElementsInGDB;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#isDirected()
	 */
	@Override
	public boolean isDirected() {
		return gds.createsDirected();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#print()
	 */
	@Override
	public void print() {
		System.out.println(this.toString());
		System.out.println("  V = " + this.getNodes());
		System.out.println("  E = " + this.getEdges());

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#printAll()
	 */
	@Override
	public void printAll() {
		System.out.println(this.toString());
		printV();
		printE();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#printE()
	 */
	@Override
	public void printE() {
		System.out.println(this.toString());
		Iterator<IElement> iterator = this.getEdges().iterator();
		while (iterator.hasNext()) {
			System.out.println("  " + iterator.next());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#printV()
	 */
	@Override
	public void printV() {
		System.out.println(this.toString());
		Iterator<IElement> iterator = this.getNodes().iterator();
		while (iterator.hasNext()) {
			System.out.println("  " + iterator.next());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#removeEdge(dna.graph.edges.Edge)
	 */
	@Override
	public boolean removeEdge(Edge e) {
		if (!(e instanceof IGDBEdge))
			return false;

		com.tinkerpop.blueprints.Edge edge = (com.tinkerpop.blueprints.Edge) ((IGDBEdge<?>) e).getGDBEdge();

		if (edge == null)
			return false;

		this.graph.removeEdge(edge);

		if (!this.storeInGDB()) {
			edges.remove(edge.getId());
		}

		if (this.getGraphDatabaseType() == DNAGraphType.NEO4J2) {
			this.commit();
		}

		if (this.graph.getEdge(edge.getId()) == null) {
			((IGDBEdge<?>) e).setGDBEdgeId(null);
			this.edgeCount--;
			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#removeNode(dna.graph.nodes.Node)
	 */
	@Override
	public boolean removeNode(Node n) {

		if (!(n instanceof IGDBNode))
			return false;

		Vertex v = (Vertex) ((IGDBNode<?>) n).getGDBNode();

		if (v == null)
			return false;

		this.graph.removeVertex(v);

		if (this.getGraphDatabaseType() == DNAGraphType.NEO4J2) {
			this.commit();
		}

		if (this.graph.getVertex(v.getId()) == null) {
			((IGDBNode<?>) n).setGDBNodeId(null);

			if (!this.storeInGDB()) {
				nodes.remove(v.getId());
			}

			if (n.getIndex() == this.maxNodeIndex) {
				this.maxNodeIndex = this.getMaxNodeIndexFromDB();
			}
			v = null;
			nodeCount--;
			return true;
		}

		return false;
	}

	/**
	 * Sets the clear work space on close.
	 *
	 * @param clearWorkSpaceOnClose            the clearWorkSpaceOnClose to set
	 */
	public void setClearWorkSpaceOnClose(boolean clearWorkSpaceOnClose) {
		this.clearWorkSpaceOnClose = clearWorkSpaceOnClose;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * dna.graph.IGDBGraph#setGraphDatabaseInstance(com.tinkerpop.blueprints.
	 * Graph)
	 */
	/*
	 */
	@Override
	public void setGraphDatabaseInstance(Graph graph) {
		this.graph = graph;
		this.graphType = BlueprintsGraph.getTypeOfGraph(graph);
		this.maxNodeIndex = this.getMaxNodeIndexFromDB();
		this.nodeCount = this.getNodeCountFromDB();
		this.edgeCount = this.getEdgeCountFromDB();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGDBGraph#setGraphDatabaseType(dna.graph.DNAGraphFactory.
	 * DNAGraphType)
	 */
	@Override
	public void setGraphDatabaseType(DNAGraphType gdb) {
		this.graphType = gdb;
		this.graph = BlueprintsGraph.getGDB(gdb, this.name, "");
		this.maxNodeIndex = this.getMaxNodeIndexFromDB();
		this.nodeCount = this.getNodeCountFromDB();
		this.edgeCount = this.getEdgeCountFromDB();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#setTimestamp(long)
	 */
	@Override
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dna.graph.IGraph#switchDataStructure(dna.graph.datastructures.
	 * DataStructure.ListType, java.lang.Class)
	 */
	@Override
	public void switchDataStructure(ListType type, Class<? extends IDataStructure> newDatastructureType) {
		Log.warn("Switching datastructure is not possible for " + this.getClass().getName());
	}
}
