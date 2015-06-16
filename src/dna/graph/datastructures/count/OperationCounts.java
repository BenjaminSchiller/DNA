package dna.graph.datastructures.count;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.count.OperationCount.AggregationType;
import dna.util.Config;

/**
 * 
 * holds the operation counts for V and E
 * 
 * @author benni
 *
 */
public abstract class OperationCounts {

	public String name = null;

	public OperationCount V = new OperationCount(ListType.GlobalNodeList);
	public OperationCount E = new OperationCount(ListType.GlobalEdgeList);

	public static final String prefixV = Config.get("COUNTING_PREFIX_V");
	public static final String prefixE = Config.get("COUNTING_PREFIX_E");
	public static final String prefixIn = Config.get("COUNTING_PREFIX_IN");
	public static final String prefixOut = Config.get("COUNTING_PREFIX_OUT");
	public static final String prefixNeighbors = Config
			.get("COUNTING_PREFIX_NEIGHBORS");
	public static final String prefixAdj = Config.get("COUNTING_PREFIX_ADJ");

	public abstract void writeValues(String dir) throws IOException;

	public abstract void writeValues(String dir, String filename)
			throws IOException;

	public abstract void readValues(String dir) throws IOException;

	public abstract void readValues(String dir, String filename)
			throws IOException;

	public abstract void setSizes(Graph g);
	
	public abstract OperationCounts add(AggregationType at, OperationCounts... ocs);
}
