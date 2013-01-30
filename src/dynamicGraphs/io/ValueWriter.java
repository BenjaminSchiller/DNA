package dynamicGraphs.io;

import java.io.IOException;

public class ValueWriter {
	public static void write(int value, String filename) throws IOException {
		ValueWriter.write(Integer.toString(value), filename);
	}

	public static void write(long value, String filename) throws IOException {
		ValueWriter.write(Long.toString(value), filename);
	}

	public static void write(double value, String filename) throws IOException {
		ValueWriter.write(Double.toString(value), filename);
	}

	public static void write(String value, String filename) throws IOException {
		Writer w = new Writer(filename);
		w.writeln(value);
		w.close();
	}
}
