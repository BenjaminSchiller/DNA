package dna.series;

public class SeriesStats {

	public static final String nodesToAdd = "nodesToAdd";
	public static final String addedNodes = "addedNodes";
	public static final String nodesToRemove = "nodesToRemove";
	public static final String removedNodes = "removedNodes";
	public static final String nodeWeightsToUpdate = "nodeWeightsToUpdate";
	public static final String updatedNodeWeights = "updatedNodeWeights";

	public static final String edgesToAdd = "edgesToAdd";
	public static final String addedEdges = "addedEdges";
	public static final String edgesToRemove = "edgesToRemove";
	public static final String removedEdges = "removedEdges";
	public static final String edgeWeightsToUpdate = "edgeWeightsToUpdate";
	public static final String updatedEdgeWeights = "updatedEdgeWeights";

	public static final String deletedNodeAdditions = "deletedNodeAdditions";
	public static final String deletedEdgeAdditions = "deletedEdgeAdditions";
	public static final String deletedNodeRemovals = "deletedNodeRemovals";
	public static final String deletedEdgeRemovals = "deletedEdgeRemovals";
	public static final String deletedNodeWeightUpdates = "deletedNodeWeightUpdates";
	public static final String deletedEdgeWeightUpdates = "deletedEdgeWeightUpdates";

	public static final String randomSeed = "randomSeed";

	public static final String memory = "memory";
	
	public static final String totalRuntime = "total";
	public static final String metricsRuntime = "metrics";
	public static final String graphGenerationRuntime = "graphGeneration";
	public static final String batchGenerationRuntime = "batchGeneration";
	public static final String sumRuntime = "sum";
	public static final String overheadRuntime = "overhead";

	public static final String[] statisticsToPlot = new String[] { memory };

	public static final String[] generalRuntimesPlot = new String[] {
			totalRuntime, metricsRuntime, batchGenerationRuntime,
			sumRuntime, overheadRuntime };

}
