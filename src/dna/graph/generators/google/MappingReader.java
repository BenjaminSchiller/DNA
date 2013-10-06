package dna.graph.generators.google;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import dna.io.Reader;

public class MappingReader {

	public static MappingDto read(String dir, String filename) {
		Reader reader = null;
		HashMap<String, Integer> mapping = new HashMap<>();
		HashMap<Integer, Long> lastSeen = new HashMap<>();
		HashMap<Integer, Integer> count = new HashMap<>();
		String name = "";
		int nodeLabelCounter = 0;
		try {
			reader = new Reader(dir, filename);

			reader.readString();
			name = reader.readString();
			reader.readString();
			nodeLabelCounter = reader.readInt();
			reader.readString();
			String line;
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

		return new MappingDto(mapping, count, lastSeen, nodeLabelCounter, name);

	}
}
