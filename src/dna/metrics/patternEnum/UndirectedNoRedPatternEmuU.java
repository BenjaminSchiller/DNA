package dna.metrics.patternEnum;

import dna.metrics.patternEnum.subgfinder.NoRedTraverser;

public class UndirectedNoRedPatternEmuU extends PatternEnumU {
	
	/**
	 * Counts all undirected patterns in a graph with size {@code motifSize}.
	 * Uses {@link NoRedTraverser}.
	 * 
	 * @param motifSize
	 */
	public UndirectedNoRedPatternEmuU(int motifSize) {
		this(motifSize, true, true);
	}
	
	/**
	 * Provides the possibility to deactivate the counting of patterns. Can be useful for testing.
	 * @see #UndirectedNoRedPatternEmuU(int)
	 * 
	 * @param motifSize
	 * @param countPatterns
	 * @param initPatternCounter
	 */
	public UndirectedNoRedPatternEmuU(int motifSize, boolean countPatterns,
			boolean initPatternCounter) {
		super(motifSize, false, new NoRedTraverser(), countPatterns, initPatternCounter);
	}
}
