package dna.graph.generators.google;

import java.io.IOException;
import java.util.HashMap;

import dna.io.Reader;

public class InternalMappingReader {

	public static boolean read(String dir, String filename,
			HashMap<String, Integer> mapping) {
		Reader reader = null;

		try {
			reader = new Reader(dir, filename);
			reader.readString();
			String[] inputs;
			String line;
			while (!(line = reader.readString()).equals("NODELABELCOUNTER")) {
				inputs = line.split(";;;");
				if (!mapping.containsKey(inputs[0])) {
					mapping.put(inputs[0], Integer.parseInt(inputs[1]));
				}

			}

			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

	}
}
