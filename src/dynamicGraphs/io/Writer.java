package dynamicGraphs.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {
	private BufferedWriter writer;

	public Writer(String filename) throws IOException {
		this.writer = new BufferedWriter(new FileWriter(filename));
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

	public void writeKeyword(String keyword) throws IOException {
		this.writer.write(Keywords.asLine(keyword) + "\n");
	}

	public void close() throws IOException {
		this.writer.close();
	}
}
