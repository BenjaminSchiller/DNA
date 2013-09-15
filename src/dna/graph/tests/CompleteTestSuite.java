package dna.graph.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   DatastructureTester.class,
   GraphTester.class,
   GeneratorsTest.class,
   EdgeSupertypeTest.class,
   BatchTest.class,
   ProfilerTest.class,
   SingleTests.class
})
public class CompleteTestSuite {

}
