package dynamicGraphs.io;

import java.io.IOException;

public class ValueWriter {
	public static void write(int value, String path) throws IOException {
		ValueWriter.write(Integer.toString(value), path);
	}

	public static void write(long value, String path) throws IOException {
		ValueWriter.write(Long.toString(value), path);
	}

	public static void write(double value, String path) throws IOException {
		ValueWriter.write(Double.toString(value), path);
	}

	public static void write(String value, String path) throws IOException {
		Writer w = new Writer(path);
		w.writeln(value);
		w.close();
	}
}
