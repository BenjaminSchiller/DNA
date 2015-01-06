package dna.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.sun.media.sound.InvalidFormatException;

import dna.util.Config;

/**
 * Simple file reader that reads data from a file one line at a time.
 * 
 * @author benni
 * 
 */
public class Reader {
	protected BufferedReader reader;

	private boolean skipComments;

	private String commentPrefix;

	public Reader(String dir, String filename) throws FileNotFoundException {
		this(dir, filename, Config.get("COMMENT_PREFIX"), true);
	}

	public Reader(String dir, String filename, boolean skipComments)
			throws FileNotFoundException {
		this(dir, filename, Config.get("COMMENT_PREFIX"), skipComments);
	}

	public Reader(String dir, String filename, String commentPrefix,
			boolean skipComments) throws FileNotFoundException {
		this.reader = new BufferedReader(new FileReader(dir + filename));
		this.skipComments = skipComments;
		this.commentPrefix = commentPrefix;
	}

	public Reader() {

	}

	public String readString() throws IOException {
		String line = this.reader.readLine();
		if (line == null) {
			return null;
		}
		if (this.skipComments && line.startsWith(this.commentPrefix)) {
			return this.readString();
		}
		return line;
	}

	public int readInt() throws NumberFormatException, IOException {
		return Integer.parseInt(this.readString());
	}

	public long readLong() throws NumberFormatException, IOException {
		return Long.parseLong(this.readString());
	}

	@SuppressWarnings("rawtypes")
	public Class readClass() throws ClassNotFoundException, IOException {
		return Class.forName(this.readString());
	}

	public void readKeyword(String keyword) throws IOException {
		String line = this.readString();
		if (!line.equals(Writer.getKeywordAsLine(keyword))) {
			throw new InvalidFormatException("Expected keyword '" + keyword
					+ "'");
		}
	}

	public void close() throws IOException {
		this.reader.close();
	}

	/**
	 * Returns either a Reader or a ZipReader. Depends if the static
	 * BatchData.fs FileSystem is set or not. If it is set, a ZipReader for the
	 * FileSystem will be returned.
	 */
	public static Reader getReader(String dir, String filename)
			throws IOException {
		if (ZipReader.readFileSystem == null)
			return new Reader(dir, filename);
		else
			return new ZipReader(ZipReader.readFileSystem, dir, filename);
	}
}
