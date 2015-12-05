package dna.graph;

import dna.graph.datastructures.GraphDataStructure;
import dna.util.Config;

/**
 * A factory for creating DNAGraph objects.
 */
public class DNAGraphFactory {
	
	/**
	 * The Enum DNAGraphType.
	 */
	public enum DNAGraphType {
		
		/** The bitsy graph databse (durable, stores data on disk). */
		BITSY_DURABLE("Bitsy graph database", false),
		
		/** The bitsy graph database (in memory only). */
		BITSY_NON_DURABLE("Bitsy graph database", false),
		
		/** Read graph database from the config file. */
		CONFIG("Read graph properties from config file", true),
		
		/** The standard DNA graph type. */
		DNA("The standard DNA Graph", false), 
		
		/** The Neo4J2 graph database. */
		NEO4J2("Neo4j graph database", false), 
		
		/** The OrientDB graph database with no transaction. */
		ORIENTDBNOTX("OrientDB graph database without transactions", false), 
		
		/** The Tinkergraph graph database (reference implementation from Tinkerpop Blueprints). */
		TINKERGRAPH("Tinkergraph graph database", false);

		/** The description. */
		private final String description;		
		
		/** The supports object as property. */
		private final Boolean supportsObjectAsProperty;

		/**
		 * Instantiates a new DNA graph type.
		 *
		 * @param description the description
		 * @param supportsObjectAsProperty the supports object as property
		 */
		private DNAGraphType(String description, Boolean supportsObjectAsProperty) {
			this.description = description;
			this.supportsObjectAsProperty = supportsObjectAsProperty;
		}

		/**
		 * Gets the description.
		 *
		 * @return the description
		 */
		public String getDescription() {
			return this.description;
		}
		
		/**
		 * Supports object as property.
		 *
		 * @return true, if database supports java objects as properties 
		 */
		public Boolean supportsObjectAsProperty() {
			return this.supportsObjectAsProperty;
		}
	}
	
	/** The clear work space. */
	private static boolean clearWorkSpace = false;
	
	/** The operations per commit. */
	private static int operationsPerCommit = 0;
	
	/** The type. */
	private static DNAGraphType type = null;
	
	/** The workspace. */
	private static String workspace = null;
	
	/**
	 * Checks if the graph type and graph data structure are compatible.
	 *
	 * @param graphType the graph type
	 * @param gds the graph data structure
	 * @return true, if compatible
	 */
	public static boolean areCombatible(DNAGraphType graphType, GraphDataStructure gds) {
		return graphType == DNAGraphType.DNA && !gds.usesGraphDatabase()
				|| !(graphType == DNAGraphType.DNA) && gds.usesGraphDatabase();
	}

	/**
	 * Creates a new graph instance of the given type with specified parameters.
	 *
	 * @param graphType the graph type
	 * @param name the name
	 * @param timestamp the timestamp
	 * @param gds the graph data structure 
	 * @return a new graph instance
	 */
	public static IGraph newGraphinstance(DNAGraphType graphType,
			String name, long timestamp, GraphDataStructure gds) {
		if (!DNAGraphFactory.areCombatible(graphType, gds)) {
			throw new RuntimeException("The chosen graph type and the node type for the graph datastructure are incompatible.");
		}
		
		switch (graphType) {
		case CONFIG:
			readValuesFromConfig();
			
			if (!DNAGraphFactory.areCombatible(type, gds))
				throw new RuntimeException("The chosen graph type and the node type for the graph datastructure are incompatible.");
						
		    return newGraphinstance(type, name, timestamp, gds, operationsPerCommit, clearWorkSpace, workspace);			
		case DNA:
			return new Graph(name, timestamp, gds);
		case BITSY_DURABLE:
		case BITSY_NON_DURABLE:
		case NEO4J2:
		case ORIENTDBNOTX:
		case TINKERGRAPH:
			return new BlueprintsGraph(name, timestamp, gds, graphType);
		default:
			throw new RuntimeException("Choose a valid DNA graph type!");
		}
	}

	/**
	 * Creates a new graph instance of the given type with specified parameters.
	 *
	 * @param graphType the graph type
	 * @param name the name
	 * @param timestamp the timestamp
	 * @param gds the graph data structure
	 * @param operationsPerCommit defines the number of operations until a commit will be executed
	 * @param clearWorkSpace defines whether the work space should be cleared on close or not
	 * @param workSpace the path to the working directory
	 * @return a new graph instance
	 */
	public static IGraph newGraphinstance(DNAGraphType graphType,
			String name, long timestamp, GraphDataStructure gds, 
			int operationsPerCommit, boolean clearWorkSpace, String workSpace) {
		if (!DNAGraphFactory.areCombatible(graphType, gds)) {
			throw new RuntimeException("The chosen graph type and the node type for the graph datastructure are incompatible.");
		}
		
		switch (graphType) {
		case CONFIG:			
			readValuesFromConfig();
			
			if (!DNAGraphFactory.areCombatible(type, gds))
				throw new RuntimeException("The chosen graph type and the node type for the graph datastructure are incompatible.");
					
			return newGraphinstance(type, name, timestamp, gds, operationsPerCommit, clearWorkSpace, workSpace);
		case DNA:
			throw new RuntimeException("The graph type DNA is not applicable for.");
		case BITSY_DURABLE:
		case BITSY_NON_DURABLE:
		case NEO4J2:
		case ORIENTDBNOTX:
		case TINKERGRAPH:
			return new BlueprintsGraph(name, timestamp, gds, graphType, operationsPerCommit, clearWorkSpace, workSpace);
		default:
			throw new RuntimeException("Choose a valid DNA graph type!");
		}
	}
	
	/**
	 * Creates a new graph instance of the given type with specified parameters.
	 *
	 * @param graphType the graph type
	 * @param name the name
	 * @param timestamp the timestamp
	 * @param gds the graph data structure 
	 * @param nodeSize the number of nodes
	 * @param edgeSize the number of edges
	 * @return a new graph instance
	 */
	public static IGraph newGraphinstance(DNAGraphType graphType,
			String name, long timestamp, GraphDataStructure gds, int nodeSize,
			int edgeSize) {
		if (!DNAGraphFactory.areCombatible(graphType, gds)) {
			throw new RuntimeException("The chosen graph type and the node type for the graph datastructure are incompatible.");
		}
		
		switch (graphType) {
		case CONFIG:			
			readValuesFromConfig();
						
			return newGraphinstance(type, name, timestamp, gds, operationsPerCommit, clearWorkSpace, workspace);
		case DNA:
			return new Graph(name, timestamp, gds, nodeSize, edgeSize);
		case BITSY_DURABLE:
		case BITSY_NON_DURABLE:
		case NEO4J2:
		case ORIENTDBNOTX:
		case TINKERGRAPH:
			return newGraphinstance(graphType, name, timestamp, gds);
		default:
			throw new RuntimeException("Choose a valid DNA graph type!");
		}
	}
	
	/**
	 * Read values from configuration file.
	 */
	private static void readValuesFromConfig(){		
		try{
			type = Enum.valueOf(DNAGraphType.class, Config.get("GRAPHTYPE"));
		} catch(Exception ex) {
			type = null;
		}
		try{
			operationsPerCommit = Integer.parseInt(Config.get("OPERATIONS_PER_COMMIT").trim());
		} catch(Exception ex) {
			operationsPerCommit = 0;
		}
		try{
			clearWorkSpace = Boolean.parseBoolean(Config.get("CLEAR_WORKSPACE_ON_CLOSE"));
		} catch(Exception ex) {
			clearWorkSpace = false;
		}
		try{
			workspace = Config.get("WORKSPACE").trim();
		} catch(Exception ex) {
			workspace = null;
		}
	}
	
}
