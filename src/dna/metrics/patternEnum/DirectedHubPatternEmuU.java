package dna.metrics.patternEnum;

import dna.metrics.patternEnum.subgfinder.HubTraverser;
import dna.metrics.patternEnum.subgfinder.hub.manage.HubManager.HubChooseAlg;

public class DirectedHubPatternEmuU extends PatternEnumU {
	
	/**
	 * Counts all directed patterns in a graph with size {@code motifSize}.
	 * Uses {@link HubTraverser}.
	 * 
	 * @param motifSize
	 * @param hca
	 * @param minHubDegree
	 * @param maxHubRate
	 * @param hubUpdateInterval
	 */
	public DirectedHubPatternEmuU(int motifSize,
			HubChooseAlg hca, int minHubDegree, double maxHubRate, int hubUpdateInterval) {
		this(motifSize, hca, minHubDegree, maxHubRate, hubUpdateInterval, true, true);
	}
	
	/**
	 * Provides the possibility to deactivate the counting of patterns. Can be useful for testing.
	 * @see #DirectedHubPatternEmuU(int, HubChooseAlg, int, double, int)
	 * 
	 * @param motifSize
	 * @param countPatterns
	 * @param initPatternCounter
	 */
	public DirectedHubPatternEmuU(int motifSize,
			HubChooseAlg hca, int minHubDegree, double maxHubRate, int hubUpdateInterval,
			boolean countPatterns, boolean initPatternCounter) {
		super(motifSize, true, null, countPatterns, initPatternCounter);
		
		HubTraverser traverser = new HubTraverser(getGraph(), hca, minHubDegree, maxHubRate,
				hubUpdateInterval);
		this.traverser = traverser;
		addListener(traverser.getHubManager());
	}
}
