package dna.metrics.patternEnum.subgfinder.hub.manage;

import dna.graph.Graph;

public class HubAmountCheckerImpl implements IHubAmountChecker {
	
	@Override
	public int checkOptimalHubAmount(Graph graph, int storedPathCount, int motifSize,
			float averageDegree, double maxHubRatio) {
		int maxHubsToAdd = ((int) Math.floor(graph.getNodeCount() * maxHubRatio)) - storedPathCount;
		return maxHubsToAdd;
	}
}
