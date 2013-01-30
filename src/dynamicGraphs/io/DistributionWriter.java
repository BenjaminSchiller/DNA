package dynamicGraphs.io;

import java.io.IOException;

public class DistributionWriter {
	public static void write(int[] values, String filename) throws IOException {
		Writer w = new Writer(filename);
		for (int i = 0; i < values.length; i++) {
			w.writeln(i + Keywords.distributionDelimiter + values[i]);
		}
		w.close();
	}

	public static void write(long[] values, String filename) throws IOException {
		Writer w = new Writer(filename);
		for (int i = 0; i < values.length; i++) {
			w.writeln(i + Keywords.distributionDelimiter + values[i]);
		}
		w.close();
	}

	public static void write(double[] values, String filename)
			throws IOException {
		Writer w = new Writer(filename);
		for (int i = 0; i < values.length; i++) {
			w.writeln(i + Keywords.distributionDelimiter + values[i]);
		}
		w.close();
	}
}
