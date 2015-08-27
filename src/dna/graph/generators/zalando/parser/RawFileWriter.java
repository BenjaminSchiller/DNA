package dna.graph.generators.zalando.parser;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

import dna.util.Log;

public class RawFileWriter {

	private FileOutputStream fileOutputStream;
	private GZIPOutputStream gzipOutputStream;
	private OutputStreamWriter outputStreamWriter;
	private BufferedWriter bufferedWriter;

	public RawFileWriter(String path, boolean isGzipped) {
		try {
			if (isGzipped)
				initGzFile(path);
			else
				initFile(path);
		} catch (IOException e) {
			Log.error("Failure while initializing reader.");
			this.close();
		}
	}

	public void close() {
		if (this.bufferedWriter != null) {
			try {
				this.bufferedWriter.close();
				this.bufferedWriter = null;
			} catch (IOException e) {
				Log.warn("Could not close BufferedWriter.");
			}
		}

		if (this.outputStreamWriter != null) {
			try {
				this.outputStreamWriter.close();
				this.outputStreamWriter = null;
			} catch (IOException e) {
				Log.warn("Could not close OutputStreamWriter.");
			}
		}

		if (this.gzipOutputStream != null) {
			try {
				this.gzipOutputStream.close();
				this.gzipOutputStream = null;
			} catch (IOException e) {
				Log.warn("Could not close GZIPOutputStream.");
			}
		}

		if (this.fileOutputStream != null) {
			try {
				this.fileOutputStream.close();
				this.fileOutputStream = null;
			} catch (IOException e) {
				Log.warn("Could not close FileOutputStream.");
			}
		}
	}

	private void initFile(String path) throws FileNotFoundException {
		this.fileOutputStream = new FileOutputStream(path);
		this.outputStreamWriter = new OutputStreamWriter(this.fileOutputStream);
		this.bufferedWriter = new BufferedWriter(this.outputStreamWriter);
	}

	private void initGzFile(String path) throws IOException {
		this.fileOutputStream = new FileOutputStream(path);
		this.gzipOutputStream = new GZIPOutputStream(this.fileOutputStream);
		this.outputStreamWriter = new OutputStreamWriter(this.gzipOutputStream);
		this.bufferedWriter = new BufferedWriter(this.outputStreamWriter);
	}

	public String writeLine(String line) {
		if (this.bufferedWriter == null) {
			// reader is closed
			Log.warn("Read not possible, the reader is closed.");
			return null;
		}

		try {
			this.bufferedWriter.write(line);
			this.bufferedWriter.newLine();
		} catch (IOException e) {
			Log.error("Failure while reading next line.");
			return null;
		}

		if (line == null) {
			Log.warn("No more lines to read. Writer will be closed.");
			this.close();
			return null;
		}

		return line;
	}

}
