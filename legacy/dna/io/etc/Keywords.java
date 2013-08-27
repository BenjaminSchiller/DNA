package dna.io.etc;

import dna.util.Config;

public class Keywords {
	public static final String graphGraph =  Config.get("KEYWORDS_GRAPH_GRAPH");

	public static final String graphNodes = Config.get("KEYWORDS_GRAPH_NODES");

	public static final String graphListOfNodes = Config.get("KEYWORDS_GRAPH_LISTOFNODES");

	public static final String graphEdges = Config.get("KEYWORDS_GRAPH_EDGES");

	public static final String graphListOfEdges = Config.get("KEYWORDS_GRAPH_LISTOFEDGES");

	public static final String graphTimestamp = Config.get("KEYWORDS_GRAPH_TIMESTAMP");

	public static final String nodeWeightDelimiter = Config.get("KEYWORDS_NODEWEIGHT_DELIMITER");

	public static final String directedEdgeDelimiter = Config.get("KEYWORDS_DIRECTEDEDGE_DELIMITER");

	public static final String undirectedEdgeDelimiter = Config.get("KEYWORDS_UNDIRECTEDEDGE_DELIMITER");

	public static final String edgeWeightDelimiter = Config.get("KEYWORDS_EDGEWEIGHT_DELIMITER");

	public static String asLine(String keyword) {
		return Config.get("KEYWORDS_PRE") + keyword;
	}
}
