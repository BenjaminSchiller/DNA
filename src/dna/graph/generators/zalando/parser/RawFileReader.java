package dna.graph.generators.zalando.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import dna.util.Log;

public class RawFileReader {

	private FileInputStream fileInputStream;

	private GZIPInputStream gzipInputStream;

	private InputStreamReader inputStreamReader;

	private BufferedReader bufferedReader;

	private void initGzFile(String path) throws IOException {
		this.fileInputStream = new FileInputStream(path);
		this.gzipInputStream = new GZIPInputStream(this.fileInputStream);
		this.inputStreamReader = new InputStreamReader(this.gzipInputStream);
		this.bufferedReader = new BufferedReader(this.inputStreamReader);
	}

	private void initFile(String path) throws FileNotFoundException {
		this.fileInputStream = new FileInputStream(path);
		this.inputStreamReader = new InputStreamReader(this.fileInputStream);
		this.bufferedReader = new BufferedReader(this.inputStreamReader);		
	}

	public RawFileReader(String path, boolean isGzipped) {
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
		if (this.bufferedReader != null) {
			try {
				this.bufferedReader.close();
				this.bufferedReader = null;
			} catch (IOException e) {
				Log.warn("Could not close BufferedReader.");
			}
		}

		if (this.inputStreamReader != null) {
			try {
				this.inputStreamReader.close();
				this.inputStreamReader = null;
			} catch (IOException e) {
				Log.warn("Could not close InputStreamReader.");
			}
		}

		if (this.gzipInputStream != null) {
			try {
				this.gzipInputStream.close();
				this.gzipInputStream = null;
			} catch (IOException e) {
				Log.warn("Could not close GZIPInputStream.");
			}
		}

		if (this.fileInputStream != null) {
			try {
				this.fileInputStream.close();
				this.fileInputStream = null;
			} catch (IOException e) {
				Log.warn("Could not close FileInputStream.");
			}
		}
	}

	public String readLine() {
		if (this.bufferedReader == null) {
			// reader is closed
			Log.warn("Read not possible, the reader is closed.");
			return null;
		}

		String line = null;
		try {
			line = this.bufferedReader.readLine();
		} catch (IOException e) {
			Log.error("Failure while reading next line.");
			return null;
		}

		if (line == null) {
			Log.warn("No more lines to read. Reader will be closed.");
			this.close();
			return null;
		}
		
		return line;
	}

}
