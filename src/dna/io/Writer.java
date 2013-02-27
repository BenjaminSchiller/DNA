package dna.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import dna.settings.Keywords;

/**
 * Simple file writer that writes data to a file one line at a time.
 * 
 * @author benni
 * 
 */
public class Writer {
	private BufferedWriter writer;

	public Writer(String dir, String filename) throws IOException {
		(new File(dir)).mkdirs();
		this.writer = new BufferedWriter(new FileWriter(dir + filename));
	}

	public void writeln(String line) throws IOException {
		this.writer.write(line + "\n");
	}

	public void writeln(int line) throws IOException {
		this.writer.write(line + "\n");
	}

	public void writeln(long line) throws IOException {
		this.writer.write(line + "\n");
	}

	public void writeln(double line) throws IOException {
		this.writer.write(line + "\n");
	}

	public void writeKeyword(String keyword) throws IOException {
		this.writer.write(Keywords.asLine(keyword) + "\n");
	}

	public void close() throws IOException {
		this.writer.close();
	}
}
