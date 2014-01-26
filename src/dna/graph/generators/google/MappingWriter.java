package dna.graph.generators.google;

import java.io.IOException;
import java.util.HashMap;

import dna.io.Writer;

public class MappingWriter {

	public static boolean write(MappingDto dto, String dir, String filename) {
		Writer writer = null;
		try {
			writer = new Writer(dir, filename);

			HashMap<Integer, Long> lastSeen = dto.lastSeen;
			HashMap<Integer, Integer> count = dto.count;

			writer.writeln("GraphName");
			writer.writeln(dto.name);
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
			writer.writeln("InternalMapping;;;lastseen;;;count;;;");
			for (int i : lastSeen.keySet()) {
				writer.writeln(i + ";" + lastSeen.get(i) + ";" + count.get(i));
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
		}
		return "";
	}
}
