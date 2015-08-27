package dna.metrics.patternEnum.subgfinder.hub.manage;

import dna.graph.Graph;

/**
 * @author Bastian Laur
 *
 */
public interface IHubAmountChecker {
	int checkOptimalHubAmount(Graph graph, int storedPathCount, int motifSize,
			float averageDegree, double maxHubRatio);
}
