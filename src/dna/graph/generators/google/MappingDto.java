package dna.graph.generators.google;

import java.util.HashMap;

public class MappingDto {

	public final HashMap<Integer, Integer> count;
	public final HashMap<Integer, Long> lastSeen;
	public final GraphNodeAdditionType add;
	public final GraphNodeDeletionType[] del;
	public final String name;
	public final long deleteAfter;
	public final int insertAfter;

	public MappingDto(String name, HashMap<Integer, Integer> count,
			HashMap<Integer, Long> lastSeen, GraphNodeAdditionType add,
			GraphNodeDeletionType[] del, int insertAfter, long deleteAfter) {

		this.count = count;
		this.lastSeen = lastSeen;
		this.name = name;
		this.add = add;
		this.del = del;
		this.deleteAfter = deleteAfter;
		this.insertAfter = insertAfter;
	}
}
