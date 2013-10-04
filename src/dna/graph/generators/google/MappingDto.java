package dna.graph.generators.google;

import java.util.HashMap;

public class MappingDto {

	public final HashMap<String, Integer> mapping;
	public final HashMap<Integer, Integer> count;
	public final HashMap<Integer, Long> lastSeen;
	public final String name;
	public final int nodeLabelCounter;

	public MappingDto(HashMap<String, Integer> mapping,
			HashMap<Integer, Integer> count, HashMap<Integer, Long> lastSeen,
			int nodeLabelCounter, String name) {

		this.mapping = mapping;
		this.count = count;
		this.lastSeen = lastSeen;
		this.nodeLabelCounter = nodeLabelCounter;
		this.name = name;
	}
}
