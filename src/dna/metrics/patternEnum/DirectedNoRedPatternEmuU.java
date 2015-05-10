package dna.metrics.patternEnum;

import dna.metrics.patternEnum.subgfinder.NoRedTraverser;

public class DirectedNoRedPatternEmuU extends PatternEnumU {
	
	/**
	 * Counts all directed patterns in a graph with size {@code motifSize}.
	 * Uses {@link NoRedTraverser}.
	 * 
	 * @param motifSize
	 */
	public DirectedNoRedPatternEmuU(int motifSize) {
		this(motifSize, true, true);
	}
	
	/**
	 * Provides the possibility to deactivate the counting of patterns. Can be useful for testing.
	 * @see #DirectedNoRedPatternEmuU(int)
	 * 
	 * @param motifSize
	 * @param countPatterns
	 * @param initPatternCounter
	 */
	public DirectedNoRedPatternEmuU(int motifSize, boolean countPatterns,
			boolean initPatternCounter) {
		super(motifSize, true, new NoRedTraverser(), countPatterns, initPatternCounter);
	}
}
