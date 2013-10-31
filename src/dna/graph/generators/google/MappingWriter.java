package dna.graph.generators.google;

import java.io.IOException;
import java.util.HashMap;

import dna.io.Writer;

public class MappingWriter {

	public static boolean write(MappingDto dto, String dir, String filename) {
		Writer writer = null;
		try {
			writer = new Writer(dir, filename);

			HashMap<String, Integer> mapping = dto.mapping;
			HashMap<Integer, Long> lastSeen = dto.lastSeen;
			HashMap<Integer, Integer> count = dto.count;

			writer.writeln("GraphName");
			writer.writeln(dto.name);
			writer.writeln("LABELCOUNTER");
			writer.writeln(dto.nodeLabelCounter);
			writer.writeln("ADDTYPE");
			writer.writeln(getAddType(dto.add));
			writer.writeln("DELTYPES");
			for (int i = 0; i < dto.del.length; i++) {
				writer.writeln(getDelType(dto.del[i]));
			}
			writer.writeln("INSERTAFTER");
			writer.writeln(dto.insertAfter);
			writer.writeln("DELETEAFTER");
			writer.writeln(dto.deleteAfter);
			writer.writeln("UserId;;;InternalMapping;;;lastseen;;;count;;;");
			for (String s : mapping.keySet()) {
				int nodeIndex = mapping.get(s);
				writer.writeln(s + ";;;" + mapping.get(s) + ";;;"
						+ lastSeen.get(nodeIndex) + ";;;"
						+ count.get(nodeIndex));
			}

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	private static String getDelType(GraphNodeDeletionType del) {

		if (del == GraphNodeDeletionType.AfterNTimes) {
			return "AfterNTimes";
		} else if (del == GraphNodeDeletionType.EmptyNodes) {
			return "EmptyNodes";
		} else if (del == GraphNodeDeletionType.NoDeletions) {
			return "NoDeletions";
		} else if (del == GraphNodeDeletionType.NotSeenInBatch) {
			return "NotSeenInBatch";
		}
		return "";
	}

	private static String getAddType(GraphNodeAdditionType add) {
		if (add == GraphNodeAdditionType.AfterNTimes) {
			return "AfterNTimes";
		} else if (add == GraphNodeAdditionType.EverySeenNode) {
			return "EverySeenNode";
		} else if (add == GraphNodeAdditionType.AfterNTimesOnlyCrawled) {
			return "AfterNTimesOnlyCrawled";
		} else if (add == GraphNodeAdditionType.EverySeenNodeOnlyCrawled) {
			return "EverySeenNodeOnlyCrawled";
		}
		return "";
	}
}
