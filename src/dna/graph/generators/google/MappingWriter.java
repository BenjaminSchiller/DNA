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
}
