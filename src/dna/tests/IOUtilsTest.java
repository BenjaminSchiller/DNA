package dna.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import dna.util.IOUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class IOUtilsTest.
 */
public class IOUtilsTest {

	/** The folder. */
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	/** The delimiter. */
	private String delimiter;
	
	/**
	 * Instantiates a new IO utils test.
	 */
	public IOUtilsTest() {
		delimiter = IOUtils.getPathDelimiterForOS();
	}

	/**
	 * Sets the up.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Before
	public void setUp() throws IOException {
		String emptyfolders = "";
		String foldersWithFiles = "";		
		for (int i = 1 ; i <= 5; i++) {
			emptyfolders += "empty" + i + delimiter;
			new File(folder.getRoot() + delimiter + emptyfolders).mkdirs();
			foldersWithFiles += "withFile" + i + delimiter; 
			new File(folder.getRoot() + delimiter + foldersWithFiles).mkdirs();
			File.createTempFile("temp" + i, ".tmp", new File(folder.getRoot() + delimiter + foldersWithFiles));
		}				
	}

	/**
	 * Removes the recursive.
	 */
	@Test
	public void removeRecursive()
	{
		assertTrue(IOUtils.removeRecursive(folder.getRoot().getAbsolutePath()));
		assertFalse(folder.getRoot().exists());
	}
	
	/**
	 * Removes the recursive max depth.
	 */
	@Test
	public void removeRecursiveMaxDepth()
	{
		assertFalse(IOUtils.removeRecursive(folder.getRoot().getAbsolutePath(), 3));
		assertTrue(folder.getRoot().exists());
		assertTrue((new File(IOUtils.getPathForOS(folder.getRoot() + delimiter + "empty1\\empty2\\empty3\\empty4\\empty5"))).exists());
		assertTrue((new File(IOUtils.getPathForOS(folder.getRoot() + delimiter + "withFile1\\withFile2\\withFile3\\withFile4\\withFile5"))).exists());
	}
	
	/**
	 * Removes the recursive from start to end.
	 */
	@Test
	public void removeRecursiveFromStartToEnd()
	{
		assertTrue(IOUtils.removeRecursive(folder.getRoot().getAbsolutePath(), 3, 10));
		assertTrue(folder.getRoot().exists());
		assertTrue((new File(IOUtils.getPathForOS(folder.getRoot() + delimiter + "empty1/empty2"))).exists());
		assertFalse((new File(IOUtils.getPathForOS(folder.getRoot() + delimiter + "empty1/empty2/empty3"))).exists());
		assertTrue((new File(IOUtils.getPathForOS(folder.getRoot() + delimiter + "withFile1/withFile2"))).exists());
		assertFalse((new File(IOUtils.getPathForOS(folder.getRoot() + delimiter + "withFile1/withFile2/withFile3"))).exists());
	}
	
	@Test 
	public void getPathForOS() {
		String path = IOUtils.getPathForOS("data/test/test");
		String expected = "data" + delimiter + "test" + delimiter + "test";
		assertEquals(path, expected);
	}
	
	/**
	 * Tear down.
	 */
	@After
	public void tearDown() {
		folder.delete();
	}
	
}
