package dna.graph.datastructures.count;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.datastructures.DataStructure.ListType;

/**
 * 
 * holds the operation counts for V and E
 * 
 * @author benni
 *
 */
public abstract class OperationCounts {

	public OperationCount V = new OperationCount(ListType.GlobalNodeList);
	public OperationCount E = new OperationCount(ListType.GlobalEdgeList);

	public abstract String getValues();

	public static final String suffix = ".dat";

	public abstract void writeValues(String dir) throws IOException;

	public abstract void readValues(String dir) throws IOException;

	public abstract void setSizes(Graph g);
}
