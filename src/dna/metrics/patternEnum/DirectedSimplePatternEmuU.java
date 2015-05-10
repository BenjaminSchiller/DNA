package dna.metrics.patternEnum;

import dna.metrics.patternEnum.subgfinder.SimpleTraverser;

public class DirectedSimplePatternEmuU extends PatternEnumU {
	
	/**
	 * Counts all directed patterns in a graph with size {@code motifSize}.
	 * Uses {@link SimpleTraverser}.
	 * 
	 * @param motifSize
	 */
	public DirectedSimplePatternEmuU(int motifSize) {
		this(motifSize, true, true);
	}
	
	/**
	 * Provides the possibility to deactivate the counting of patterns. Can be useful for testing.
	 * @see #DirectedSimplePatternEmuU(int)
	 * 
	 * @param motifSize
	 * @param countPatterns
	 * @param initPatternCounter
	 */
	public DirectedSimplePatternEmuU(int motifSize, boolean countPatterns,
			boolean initPatternCounter) {
		super(motifSize, true, new SimpleTraverser(), countPatterns, initPatternCounter);
	}
}
