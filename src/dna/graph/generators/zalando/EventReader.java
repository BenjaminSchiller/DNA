package dna.graph.generators.zalando;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import dna.util.Log;

/**
 * Reads a Zalando log file and creates an {@link Event} for each line.
 */
public class EventReader {

	private BufferedReader reader;

	/**
	 * Creates an {@link EventReader} to read in events of given file.
	 * 
	 * @param filepath
	 *            The path of the file to read in.
	 */
	public EventReader(String filepath) {
		try {
			this.reader = new BufferedReader(new FileReader(filepath));
		} catch (FileNotFoundException e) {
			Log.error("Failure while creating EventReader for file " + filepath
					+ ". Is the path correct?");
		}
	}

	/**
	 * Creates an {@link EventReader} to read in events of given file, starting
	 * at given line number (starting at 0).
	 * <p>
	 * Please note that this constructor may be inefficient: to "jump" to the
	 * given line, this constructor calls {@link #readNext()} up to
	 * {@code lineNumber} -1 times.
	 * </p>
	 * 
	 * @param eventsFilepath
	 *            The path of the file to read in.
	 * @param lineNumber
	 *            The line number of the first event to get, when calling
	 *            {@link #readNext()}. The first line number of file is 0.
	 *            {@link #readNext()} will return {@code null} if the given line
	 *            number is greater than the total number of lines in file.
	 */
	public EventReader(String eventsFilepath, int lineNumber) {
		this(eventsFilepath);

		// jump to given line number by reading all lines before ...
		for (int i = 0; i < lineNumber; i++)
			if (this.readNext() == null)
				// ... but break if any unexpected behavior occurs.
				break;
	}

	/**
	 * Closes this {@link EventReader}.
	 * <p>
	 * You do not need to call this method if you read all lines of the file,
	 * because {@link EventReader#readNext()} closes the reader if there are no
	 * more lines to read.
	 * </p>
	 */
	public void close() {
		if (this.reader != null) {
			try {
				this.reader.close();

				this.reader = null;
			} catch (IOException e) {
				Log.warn("Failure while closing EventReader.");
			}
		}
	}

	/**
	 * Reads the next line, i.e. the next event of the file.
	 * <p>
	 * If there are no more lines to read, the reader is closed automatically.
	 * </p>
	 * 
	 * @return The read line as {@link Event} or {@code null} if either reader
	 *         is closed or there is no (more) line to read.
	 */
	public Event readNext() {
		if (this.reader == null) {
			// reader is closed
			Log.warn("Read not possible, the reader is closed.");
			return null;
		}

		String line = null;
		try {
			line = this.reader.readLine();
		} catch (IOException e) {
			Log.error("Failure while reading next line.");
			return null;
		}

		if (line == null) {
			Log.warn("Read not possible, there are no lines to read.");
			this.close();
			return null;
		}

		try {
			return new Event(line);
		} catch (Exception e) {
			Log.error("Failure while creating Event with line of current data. Line ommitted and jumped to the next.");
			return this.readNext();
		}
	}

}
