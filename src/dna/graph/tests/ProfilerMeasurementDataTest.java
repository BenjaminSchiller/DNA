package dna.graph.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import dna.profiler.ProfilerMeasurementData;
import dna.profiler.complexity.AddedComplexity;
import dna.profiler.complexity.Complexity;
import dna.profiler.complexity.ComplexityType;
import dna.profiler.complexity.ComplexityType.Base;
import dna.profiler.complexity.ComplexityType.Type;

public class ProfilerMeasurementDataTest {
	  @Rule
	  public ExpectedException exception = ExpectedException.none();
	
	// TODO Test proper parsing
	// TODO Test that there are no infinite loops
	
	@Test
	public void testParseEmptyInput() {
		ProfilerMeasurementData.loadFromProperties(new Properties());
	}
	
	@Test
	public void testGetUnknownKeyFromEmptyList() {
		ProfilerMeasurementData.loadFromProperties(new Properties());
		assertNull(ProfilerMeasurementData.get("Unknown"));
	}
	
	@Test
	public void testGetUnknownKeyFromNonEmptyList() {
		Properties p = new Properties();
		p.setProperty("Key", "");
		ProfilerMeasurementData.loadFromProperties(p);
		assertNull(ProfilerMeasurementData.get("Unknown"));
	}
	
	@Test
	public void testParsingOfEmptyKey() {
		Complexity c = ProfilerMeasurementData.parseComplexityString("");
		assertEquals(new Complexity(), c);
	}

	@Test(expected=RuntimeException.class)
	public void testParsingOfBrokenKey() {
		Complexity c = ProfilerMeasurementData.parseComplexityString("10");
	}
	
	@Test
	public void testParsingOfSimpleString() {
		Complexity c = ProfilerMeasurementData.parseComplexityString("1 Static");
		assertEquals(new Complexity(1, new ComplexityType(Type.Static, null)), c);
	}
	
	@Test
	public void testParsingOfNestedType() {
		Properties p = new Properties();
		p.setProperty("Get", "1 Linear + 2 Contains");
		p.setProperty("Remove", "2 Get + 1 Static");
		p.setProperty("Contains", "2 Linear");
		ProfilerMeasurementData.loadFromProperties(p);
		Complexity c = ProfilerMeasurementData.get("Get");
		c.setBase(ComplexityType.Base.Degree);
		
		Complexity getComp = new AddedComplexity(new Complexity(1,
				new ComplexityType(Type.Linear, Base.Degree)), new Complexity(
				4, new ComplexityType(Type.Linear, Base.Degree)));
		assertEquals(getComp, c);
		
		c = ProfilerMeasurementData.get("Remove");
		c.setBase(ComplexityType.Base.Degree);
		
		Complexity removeComp = new AddedComplexity(
				new AddedComplexity(new Complexity(2, new ComplexityType(
						Type.Linear, Base.Degree)), new Complexity(8,
						new ComplexityType(Type.Linear, Base.Degree))),
				new Complexity(1, new ComplexityType(Type.Static, Base.Degree)));
		assertEquals(removeComp, c);
	}
}
