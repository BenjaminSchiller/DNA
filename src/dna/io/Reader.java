package dna.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.sun.media.sound.InvalidFormatException;

import dna.series.SeriesGeneration;
import dna.util.Config;

/**
 * Simple file reader that reads data from a file one line at a time.
 * 
 * @author benni
 * 
 */
public class Reader {
	protected BufferedReader reader;

	public static boolean skipComments = true;

	public Reader(String dir, String filename) throws FileNotFoundException {
		this.reader = new BufferedReader(new FileReader(dir + filename));
	}

	public String readString() throws IOException {
		String line = this.reader.readLine();
		if (line == null) {
			return null;
		}
		if (skipComments && line.startsWith(Config.get("COMMENT_PREFIX"))) {
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
	public static Reader getReaader(String dir, String filename)
			throws IOException {
		if (SeriesGeneration.fs == null)
			return new Reader(dir, filename);
		else
			return new ZipReader(SeriesGeneration.fs, dir, filename);
	}
}
