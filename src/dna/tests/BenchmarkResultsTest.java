package dna.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import org.junit.Test;

import dna.profiler.datatypes.benchmarkresults.BenchmarkingResult;
import dna.profiler.datatypes.benchmarkresults.BenchmarkingResultsMap;
import dna.profiler.datatypes.benchmarkresults.strategies.BoundaryStrategy;
import dna.profiler.datatypes.benchmarkresults.strategies.BoundaryStrategy.BucketSelector;
import dna.profiler.datatypes.benchmarkresults.strategies.BoundaryStrategy.ListAggregator;
import dna.util.Config;

public class BenchmarkResultsTest {
	@Test
	public void testParsing() {
		String val = "500=0.7,0.7,0.7;1000=1.3,1.4,1.5";
		BenchmarkingResult res = (BenchmarkingResult) BenchmarkingResult
				.parseString("res", val);

		TreeMap<Integer, ArrayList<Double>> entryMap = new TreeMap<Integer, ArrayList<Double>>();
		BenchmarkingResult expected = new BenchmarkingResult("res", entryMap);
		expected.addToMap(500, Arrays.asList(0.7, 0.7, 0.7));
		expected.addToMap(1000, Arrays.asList(1.3, 1.4, 1.5));
		assertEquals(expected, res);
	}

	@Test
	public void testIfMaxValueUpperBoundaryStrategyWorks() {
		String val = "500=0.7,0.8,0.9;1000=1.6,1.4,1.5";
		
		Config.overwrite("RECOMMENDER_BUCKETSELECTOR", "Upper");
		Config.overwrite("RECOMMENDER_LISTAGGREGATOR", "Max");
		
		BenchmarkingResult res = (BenchmarkingResult) BenchmarkingResult
				.parseString("", val);

		res.setValues(1, 40, null);
		BenchmarkingResultsMap results = (BenchmarkingResultsMap) res.getMap();
		assertEquals(0.9, results.getValue(), 0.1);

		res.setValues(1, 499.9, null);
		results = (BenchmarkingResultsMap) res.getMap();
		assertEquals(0.9, results.getValue(), 0.1);

		res.setValues(1, 500.001, null);
		results = (BenchmarkingResultsMap) res.getMap();
		assertEquals(1.6, results.getValue(), 0.1);

		res.setValues(1, 999.999, null);
		results = (BenchmarkingResultsMap) res.getMap();
		assertEquals(1.6, results.getValue(), 0.1);
	}

	@Test
	public void testIfMinValueLowerBoundaryStrategyWorks() {
		String val = "500=0.7,0.8,0.9;1000=1.6,1.4,1.5";
		
		Config.overwrite("RECOMMENDER_BUCKETSELECTOR", "Lower");
		Config.overwrite("RECOMMENDER_LISTAGGREGATOR", "Min");
		
		BenchmarkingResult res = (BenchmarkingResult) BenchmarkingResult
				.parseString("", val);

		res.setValues(1, 40, null);
		BenchmarkingResultsMap results = (BenchmarkingResultsMap) res.getMap();
		assertEquals(0.7, results.getValue(), 0.1);

		res.setValues(1, 499.9, null);
		results = (BenchmarkingResultsMap) res.getMap();
		assertEquals(0.7, results.getValue(), 0.1);

		res.setValues(1, 500, null);
		results = (BenchmarkingResultsMap) res.getMap();
		assertEquals(0.7, results.getValue(), 0.1);

		res.setValues(1, 999.999, null);
		results = (BenchmarkingResultsMap) res.getMap();
		assertEquals(0.7, results.getValue(), 0.1);

		res.setValues(1, 1000, null);
		results = (BenchmarkingResultsMap) res.getMap();
		assertEquals(1.4, results.getValue(), 0.1);

		res.setValues(1, 2000, null);
		results = (BenchmarkingResultsMap) res.getMap();
		assertEquals(1.4, results.getValue(), 0.1);
	}

	@Test
	public void testCorrectInterpolation() {
		String val = "1=1;2=2";
		
		Config.overwrite("RECOMMENDER_BUCKETSELECTOR", "Interpolate");
		Config.overwrite("RECOMMENDER_LISTAGGREGATOR", "Mean");
		
		BenchmarkingResult res = (BenchmarkingResult) BenchmarkingResult
				.parseString("", val);

		res.setValues(1, 0.1, null);
		BenchmarkingResultsMap results = (BenchmarkingResultsMap) res.getMap();
		assertEquals(0.1, results.getValue(), 0.1);

		res.setValues(1, 1, null);
		results = (BenchmarkingResultsMap) res.getMap();
		assertEquals(1, results.getValue(), 0.1);

		res.setValues(1, 1.1, null);
		results = (BenchmarkingResultsMap) res.getMap();
		assertEquals(1.1, results.getValue(), 0.1);

		res.setValues(1, 1.5, null);
		results = (BenchmarkingResultsMap) res.getMap();
		assertEquals(1.5, results.getValue(), 0.1);

		res.setValues(1, 2, null);
		results = (BenchmarkingResultsMap) res.getMap();
		assertEquals(2, results.getValue(), 0.1);

		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setErr(new PrintStream(outContent));
		res.setValues(1, 10, null);
		results = (BenchmarkingResultsMap) res.getMap();
		assertTrue(outContent.toString().contains("erroneous"));
		assertEquals(10, results.getValue(), 0.1);
	}
}
