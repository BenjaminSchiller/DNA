package dna.graph.generators.google;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import dna.io.Reader;

public class MappingReader {

	public static MappingDto read(String dir, String filename) {
		Reader reader = null;
		HashMap<Integer, Long> lastSeen = new HashMap<>();
		HashMap<Integer, Integer> count = new HashMap<>();
		GraphNodeAdditionType additionType = null;
		GraphNodeDeletionType deletionType[] = null;
		String name = "";
		int insertAfter = 0;
		long deleteAfter = 0L;
		try {
			reader = new Reader(dir, filename);
			reader.readString();
			name = reader.readString();
			reader.readString();
			additionType = getAddType(reader.readString());
			reader.readString();
			String line;
			HashSet<GraphNodeDeletionType> temp = new HashSet<GraphNodeDeletionType>();
			while (!(line = reader.readString())
					.equals("InternalMapping;;;lastseen;;;count;;;")) {
				temp.add(getDelType(reader.readString()));
			}
			int counter = 0;
			deletionType = new GraphNodeDeletionType[temp.size()];
			for (GraphNodeDeletionType type : temp) {
				deletionType[counter++] = type;
			}
			reader.readString();
			insertAfter = reader.readInt();
			reader.readString();
			deleteAfter = reader.readLong();
			reader.readString();
			String[] inputs;
			while ((line = reader.readString()) != null) {
				inputs = line.split(";;;");
				lastSeen.put(Integer.parseInt(inputs[0]),
						Long.parseLong(inputs[1]));
				count.put(Integer.parseInt(inputs[0]),
						Integer.parseInt(inputs[2]));

			}

			return new MappingDto(name, count, lastSeen, additionType,
					deletionType, insertAfter, deleteAfter);

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

	}

	private static GraphNodeDeletionType getDelType(String readString)
			throws IOException {

		if (readString.equals("AfterNTimes")) {
			return GraphNodeDeletionType.AfterNTimes;
		} else if (readString.equals("EmptyNodes")) {
			return GraphNodeDeletionType.EmptyNodes;
		} else if (readString.equals("NoDeletions")) {
			return GraphNodeDeletionType.NoDeletions;
		} else if (readString.equals("NotSeenInBatch")) {
			return GraphNodeDeletionType.NotSeenInBatch;
		}
		throw new IOException();
	}

	private static GraphNodeAdditionType getAddType(String readString)
			throws IOException {
		if (readString.equals("AfterNTimes")) {
			return GraphNodeAdditionType.AfterNTimes;
		} else if (readString.equals("EverySeenNode")) {
			return GraphNodeAdditionType.EverySeenNode;
		}
		throw new IOException();
	}
}
