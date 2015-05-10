package dna.metrics.patternEnum;

import dna.metrics.patternEnum.subgfinder.SimpleTraverser;

public class UndirectedSimplePatternEmuU extends PatternEnumU {
	
	/**
	 * Counts all undirected patterns in a graph with size {@code motifSize}.
	 * Uses {@link SimpleTraverser}.
	 * 
	 * @param motifSize
	 */
	public UndirectedSimplePatternEmuU(int motifSize) {
		this(motifSize, true, true);
	}
	
	/**
	 * Provides the possibility to deactivate the counting of patterns. Can be useful for testing.
	 * @see #UndirectedSimplePatternEmuU(int)
	 * 
	 * @param motifSize
	 * @param countPatterns
	 * @param initPatternCounter
	 */
	public UndirectedSimplePatternEmuU(int motifSize, boolean countPatterns,
			boolean initPatternCounter) {
		super(motifSize, false, new SimpleTraverser(), countPatterns, initPatternCounter);
	}
}
