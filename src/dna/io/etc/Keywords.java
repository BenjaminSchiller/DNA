package dna.io.etc;

public class Keywords {
	public static final String pre = "# ";

	public static final String batchData = "BatchData";

	public static final String graphGraph = "Graph";

	public static final String graphNodes = "Nodes";

	public static final String graphListOfNodes = "List of Nodes";

	public static final String graphEdges = "Edges";

	public static final String graphListOfEdges = "List of Edges";

	public static final String graphTimestamp = "Timestamp";

	public static final String nodeWeightDelimiter = "@";

	public static final String directedEdgeDelimiter = "->";

	public static final String undirectedEdgeDelimiter = "<->";

	public static final String edgeWeightDelimiter = "@";

	public static final String distributionDelimiter = "	";

	public static final String dataDelimiter = "	";

	public static final String aggregatedDataDelimiter = "	";

	public static final String updateDelimiter1 = "#";

	public static final String updateDelimiter2 = ";";

	public static final String plotDataDelimiter = "	";

	public static String asLine(String keyword) {
		return Keywords.pre + keyword;
	}
}
