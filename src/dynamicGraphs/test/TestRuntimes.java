package dynamicGraphs.test;

import java.io.IOException;

import dynamicGraphs.util.RunTime;
import dynamicGraphs.util.RunTimes;

public class TestRuntimes {
	public static void main(String[] args) throws IOException {
		String d1 = "/Users/benni/TUD/Dynamic.Graphs/doc/"
				+ "datastructures.example/graph-test-1/" + "0/103/_metrics/";
		String d2 = "/Users/benni/TUD/Dynamic.Graphs/doc/"
				+ "datastructures.example/graph-test-1/" + "0/103/_metrics_/";
		RunTimes runtimes = RunTimes.read(d1);
		for (RunTime t : runtimes.getRuntimes()) {
			System.out.println(t);
		}
		runtimes.write(d2);
	}
}
