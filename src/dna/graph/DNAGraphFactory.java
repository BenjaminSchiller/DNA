package dna.graph;

import dna.graph.datastructures.GraphDataStructure;
import dna.util.Config;

public class DNAGraphFactory {

	public enum DNAGraphType {
		
		/** The bitsy graph databse (durable, stores data on disk). */
		BITSY_DURABLE("Bitsy graph database", false),
		
		/** The bitsy graph database (in memory only). */
		BITSY_NON_DURABLE("Bitsy graph database", false),
		
		/** Read graph database from the config file. */
		CONFIG("Read graph properties from config file", true),
		DNA("The standard DNA Graph", false), 
		NEO4J2("Neo4j graph database", false), 
		ORIENTDBNOTX("OrientDB graph database without transactions", false), 
		
		/** The Tinkergraph graph database (reference implementation from Tinkerpop Blueprints). */
		TINKERGRAPH("Tinkergraph graph database", false);

		private final String description;		
		private final Boolean supportsObjectAsProperty;

		private DNAGraphType(String description, Boolean supportsObjectAsProperty) {
			this.description = description;
			this.supportsObjectAsProperty = supportsObjectAsProperty;
		}

		public String getDescription() {
			return this.description;
		}
		
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

	public static IGraph newGraphinstance(DNAGraphType graphType,
			String name, long timestamp, GraphDataStructure gds, int nodeSize,
			int edgeSize) {
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
