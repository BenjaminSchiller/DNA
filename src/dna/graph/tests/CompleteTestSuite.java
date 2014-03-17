package dna.graph.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ DatastructureTester.class, GraphTester.class,
		GeneratorsTest.class, EdgeSupertypeTest.class, BatchTest.class,
		ProfilerTest.class, ComplexityTests.class, SingleTests.class, BenchmarkResultsTest.class })
public class CompleteTestSuite {

}
