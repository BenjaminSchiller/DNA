package dna.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import dna.series.SeriesGeneration;
import dna.util.Config;

/**
 * Simple file writer that writes data to a file one line at a time.
 * 
 * @author benni
 * 
 */
public class Writer {
	protected BufferedWriter writer;

	public Writer(String dir, String filename) throws IOException {
		(new File(dir)).mkdirs();
		this.writer = new BufferedWriter(new FileWriter(dir + filename));
	}

	public Writer() {

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

	@SuppressWarnings("rawtypes")
	public void writeln(Class line) throws IOException {
		this.writer.write(line.getCanonicalName() + "\n");
	}

	public void writeKeyword(String keyword) throws IOException {
		this.writer.write(getKeywordAsLine(keyword) + "\n");
	}

	public void writeComment(String comment) throws IOException {
		this.writer.write(getCommentAsLine(comment) + "\n");
	}

	public void close() throws IOException {
		this.writer.close();
	}

	public static String getKeywordAsLine(String keyword) {
		return Config.get("KEYWORD_PREFIX") + keyword;
	}

	public static String getCommentAsLine(String comment) {
		return Config.get("COMMENT_PREFIX") + comment;
	}

	/**
	 * Returns either a Writer or a ZipWriter. Depends if the static
	 * BatchData.fs FileSystem is set or not. If it is set, a ZipWriter for the
	 * FileSystem will be returned.
	 */
	public static Writer getWriter(String dir, String filename)
			throws IOException {
		if (SeriesGeneration.writeFileSystem == null)
			return new Writer(dir, filename);
		else
			return new ZipWriter(SeriesGeneration.writeFileSystem, dir,
					filename);
	}
}
