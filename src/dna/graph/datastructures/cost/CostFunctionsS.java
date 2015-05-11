package dna.graph.datastructures.cost;

import java.io.IOException;

import dna.graph.IElement;
import dna.graph.datastructures.IDataStructure;

/**
 * 
 * the the same data structure, the measured runtimes and resulting cost
 * functions highly depend on the type of the stored element (edge or node).
 * therefore, this class holds the cost functions for a specific data structure
 * ds for different data types (currently nodes and edges).
 * 
 * we do not distinguish between directed / undirected and weighted / unweighted
 * elements as this does not have any impact ont the mearued runtimes.
 * 
 * @author benni
 *
 */
public class CostFunctionsS {
	public Class<? extends IDataStructure> ds;
	public CostFunctions nodes;
	public CostFunctions edges;

	public CostFunctionsS(Class<? extends IDataStructure> ds,
			CostFunctions nodes, CostFunctions edges) {
		this.ds = ds;
		this.nodes = nodes;
		this.edges = edges;
	}

	public static CostFunctionsS read(String mainDataDir,
			Class<? extends IDataStructure> ds,
			Class<? extends IElement> dtNodes, Class<? extends IElement> dtEdges)
			throws NumberFormatException, IOException {
		CostFunctions nodes = CostFunctions.read(mainDataDir, ds, dtNodes);
		CostFunctions edges = CostFunctions.read(mainDataDir, ds, dtEdges);
		return new CostFunctionsS(ds, nodes, edges);
	}

}
