package dna.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {
	private BufferedWriter writer;

	public Writer(String path) throws IOException {
		(new File(path)).getParentFile().mkdirs();
		this.writer = new BufferedWriter(new FileWriter(path));
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
