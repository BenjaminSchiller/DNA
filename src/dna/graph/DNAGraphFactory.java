package dna.graph;

import dna.graph.datastructures.GraphDataStructure;
import dna.util.Config;

public class DNAGraphFactory {

	public enum DNAGraphType {
		BITSY_NON_DURABLE("Bitsy graph database", true),
		BITSY_DURABLE("Bitsy graph database", false),
		CONFIG("Read graph properties from config file", true),
		DNA("The standard DNA Graph", false), 
		NEO4J2("Neo4j graph database", false), 
		ORIENTDBNOTX("OrientDB graph database without transactions", false), 
		TINKERGRAPH("Tinkergraph graph database", true);

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

	public static IGraph newGraphinstance(DNAGraphType graphType,
			String name, long timestamp, GraphDataStructure gds) {
		if (!DNAGraphFactory.areCombatible(graphType, gds)) {
			throw new RuntimeException("The chosen graph type and the node type for the graph datastructure are incompatible.");
		}
		
		switch (graphType) {
		case CONFIG:
			DNAGraphType type = DNAGraphType.DNA;
			int operationsPerCommit = 0;
			boolean clearWorkSpace = false;
			String workspace = null;
			try{
				type = Enum.valueOf(DNAGraphType.class, Config.get("GRAPHTYPE"));
			} catch(Exception ex) {}
			try{
				operationsPerCommit = Integer.parseInt(Config.get("OPERATIONS_PER_COMMIT").trim());
			} catch(Exception ex) {}
			try{
				clearWorkSpace = Boolean.parseBoolean(Config.get("CLEAR_WORKSPACE_ON_CLOSE"));
			} catch(Exception ex) {}
			try{
				workspace = Config.get("WORKSPACE").trim();
			} catch(Exception ex) {}
			
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
			throw new RuntimeException("Choose a valid dna graph type!");
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
			DNAGraphType type = DNAGraphType.DNA;
			int operationsPerCommit = 0;
			boolean clearWorkSpace = false;
			String workspace = null;
			try{
				type = Enum.valueOf(DNAGraphType.class, Config.get("GRAPHTYPE"));
			} catch(Exception ex) {}
			try{
				operationsPerCommit = Integer.parseInt(Config.get("OPERATIONS_PER_COMMIT").trim());
			} catch(Exception ex) {}

			try{
				clearWorkSpace = Boolean.parseBoolean(Config.get("CLEAR_WORKSPACE_ON_CLOSE"));
			} catch(Exception ex) {}
			try{
				workspace = Config.get(workspace).trim();
			} catch(Exception ex) {}
						
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
			throw new RuntimeException("Choose a valid dna graph type!");
		}
	}
	
	public static IGraph newGraphinstance(DNAGraphType graphType,
			String name, long timestamp, GraphDataStructure gds, int operationsPerCommit, boolean clearWorkSpace, String workSpace) {
		if (!DNAGraphFactory.areCombatible(graphType, gds)) {
			throw new RuntimeException("The chosen graph type and the node type for the graph datastructure are incompatible.");
		}
		
		switch (graphType) {
		case CONFIG:
			DNAGraphType type = DNAGraphType.DNA;
			try{
				type = Enum.valueOf(DNAGraphType.class, Config.get("GRAPHTYPE"));
			} catch(Exception ex) {}
			
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
			throw new RuntimeException("Choose a valid dna graph type!");
		}
	}
	
	public static boolean areCombatible(DNAGraphType graphType, GraphDataStructure gds) {
		return graphType == DNAGraphType.DNA && !gds.usesGraphDatabase()
				|| !(graphType == DNAGraphType.DNA) && gds.usesGraphDatabase();
	}
	
}
