package dna.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.sun.media.sound.InvalidFormatException;

import dna.io.etc.Keywords;

/**
 * Simple file reader that reads data from a file one line at a time.
 * 
 * @author benni
 * 
 */
public class Reader {
	private BufferedReader reader;

	public Reader(String dir, String filename) throws FileNotFoundException {
		this.reader = new BufferedReader(new FileReader(dir + filename));
	}

	public String readString() throws IOException {
		return this.reader.readLine();
	}

	public int readInt() throws NumberFormatException, IOException {
		return Integer.parseInt(this.readString());
	}

	public long readLong() throws NumberFormatException, IOException {
		return Long.parseLong(this.readString());
	}

	public void readKeyword(String keyword) throws IOException {
		String line = this.readString();
		if (!line.equals(Keywords.asLine(keyword))) {
			throw new InvalidFormatException("Expected keyword '" + keyword
					+ "'");
		}
	}

	public void close() throws IOException {
		this.reader.close();
	}
}
