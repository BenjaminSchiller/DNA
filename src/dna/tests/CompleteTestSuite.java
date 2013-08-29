package dna.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   DatastructureTester.class,
   GraphTester.class,
   GeneratorsTest.class,
   EdgeSupertypeTest.class
})
public class CompleteTestSuite {

}
