package dna.graph.generators.google;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import dna.io.Reader;

public class MappingReader {

	public static MappingDto read(String dir, String filename) {
		Reader reader = null;
		HashMap<String, Integer> mapping = new HashMap<>();
		HashMap<Integer, Long> lastSeen = new HashMap<>();
		HashMap<Integer, Integer> count = new HashMap<>();
		GraphNodeAdditionType additionType = null;
		GraphNodeDeletionType deletionType[] = null;
		String name = "";
		int nodeLabelCounter = 0;
		int insertAfter = 0;
		int deleteAfter = 0;
		try {
			reader = new Reader(dir, filename);
			reader.readString();
			name = reader.readString();
			reader.readString();
			nodeLabelCounter = reader.readInt();
			reader.readString();
			additionType = getAddType(reader.readString());
			reader.readString();
			String line;
			HashSet<GraphNodeDeletionType> temp = new HashSet<GraphNodeDeletionType>();
			while (!(line = reader.readString())
					.equals("UserId;;;InternalMapping;;;lastseen;;;count;;;")) {
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
			deleteAfter = reader.readInt();
			reader.readString();
			String[] inputs;
			while ((line = reader.readString()) != null) {
				inputs = line.split(";;;");
				mapping.put(inputs[0], Integer.parseInt(inputs[1]));
				lastSeen.put(Integer.parseInt(inputs[1]),
						Long.parseLong(inputs[2]));
				count.put(Integer.parseInt(inputs[1]),
						Integer.parseInt(inputs[3]));

			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new MappingDto(name, mapping, count, lastSeen, nodeLabelCounter,
				additionType, deletionType, insertAfter, deleteAfter);

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
		} else if (readString.equals("AfterNTimesOnlyCrawled")) {
			return GraphNodeAdditionType.AfterNTimesOnlyCrawled;
		} else if (readString.equals("EverySeenNodeOnlyCrawled")) {
			return GraphNodeAdditionType.EverySeenNodeOnlyCrawled;
		}
		throw new IOException();
	}
}
