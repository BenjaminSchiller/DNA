package dna.io.etc;

public class Keywords {
	public static final String pre = "# ";

	public static final String graphName = "Name";

	public static final String graphNodes = "Nodes";

	public static final String graphTimestamp = "Timestamp";

	public static final String graphEdges = "Edges";

	public static final String graphListOfEdges = "List of Edges";

	public static final String edgeDelimiter = " ";

	public static final String directedEdgeDelimiter = " -> ";

	public static final String undirectedEdgeDelimiter = " <-> ";

	public static final String diffNodes = "Nodes";

	public static final String diffFrom = "From Timestamp";

	public static final String diffTo = "To Timestamp";

	public static final String diffAddedEdges = "Added Edges";

	public static final String diffRemovedEdges = "Removed Edges";

	public static final String diffListOfAddedEdges = "List of added Edges";

	public static final String diffListofRemovedEdges = "List of removed Edges";

	public static final String distributionDelimiter = "	";

	public static final String dataDelimiter = "	";

	public static final String updateDelimiter1 = "#";

	public static final String updateDelimiter2 = ";";

	public static String asLine(String keyword) {
		return Keywords.pre + keyword;
	}
}
