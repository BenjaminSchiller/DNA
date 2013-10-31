package dna.graph.generators.google;

import java.util.HashMap;

public class MappingDto {

	public final HashMap<String, Integer> mapping;
	public final HashMap<Integer, Integer> count;
	public final HashMap<Integer, Long> lastSeen;
	public final GraphNodeAdditionType add;
	public final GraphNodeDeletionType[] del;
	public final String name;
	public final int deleteAfter;
	public final int insertAfter;
	public final int nodeLabelCounter;

	public MappingDto(String name, HashMap<String, Integer> mapping,
			HashMap<Integer, Integer> count, HashMap<Integer, Long> lastSeen,
			int nodeLabelCounter, GraphNodeAdditionType add,
			GraphNodeDeletionType[] del, int insertAfter, int deleteAfter) {

		this.mapping = mapping;
		this.count = count;
		this.lastSeen = lastSeen;
		this.nodeLabelCounter = nodeLabelCounter;
		this.name = name;
		this.add = add;
		this.del = del;
		this.deleteAfter = deleteAfter;
		this.insertAfter = insertAfter;
	}
}
