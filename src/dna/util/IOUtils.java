package dna.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class IOUtils {
	/**
	 * 
	 * @param dir
	 *            directory to read
	 * @param filter
	 *            optional FilenameFilter to filter the files in $dir
	 * @return list of all the files in $dir that match the optional $filter,
	 *         the list is sorted using Arrays.sort(...)
	 */
	public static String[] getFilenames(String dir, FilenameFilter filter) {
		File[] files;
		if (filter == null) {
			files = (new File(dir)).listFiles();
		} else {
			files = (new File(dir)).listFiles(filter);
		}
		String[] filenames = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			filenames[i] = files[i].getName();
		}
		Arrays.sort(filenames);
		return filenames;
	}

	/**
	 * Returns an InputStream to the file at the given path inside the given
	 * JarFile.
	 **/
	public static InputStream getInputStreamFromJar(JarFile jar, String path)
			throws IOException {
		return IOUtils.getInputStreamFromJar(jar, path, false);
	}

	/**
	 * Returns an InputStream to the file at the given path inside the given
	 * JarFile. <br>
	 * <br>
	 * 
	 * <b>Example:</b><br>
	 * path = 'config/vis/test.cfg'<br>
	 * <br>
	 * 
	 * If the prune-flag is set, the method will first consider<br>
	 * - 'config/vis/test.cfg'<br>
	 * and then<br>
	 * - 'vis/test.cfg'<br>
	 * 
	 * If it is not set it will only consider the first case.<br>
	 * 
	 **/
	public static InputStream getInputStreamFromJar(JarFile jar, String path,
			boolean pruneFirstDir) throws IOException {
		// check if entry at path exists, if yes return input stream
		JarEntry entry = jar.getJarEntry(path);
		if (entry != null)
			return jar.getInputStream(entry);

		if (!pruneFirstDir)
			return null;

		// if not, build relative path with first directory missing
		String[] splits = path.split("/");
		String relativePath = "";

		if (splits.length == 1)
			relativePath = splits[0];
		else {
			// build relative path: skip first entry -> start with i = 1
			for (int i = 1; i < splits.length; i++) {
				relativePath += splits[i];
				if (i < splits.length - 1)
					relativePath += "/";
			}
		}

		entry = jar.getJarEntry(relativePath);
		if (entry != null)
			return jar.getInputStream(jar.getJarEntry(relativePath));
		else {
			Log.warn("no file inside .jar found at " + path + " or "
					+ relativePath);
			return null;
		}
	}

	/** Returns if the current program is run from jar or binary. **/
	public static boolean isRunFromJar() {
		try {
			Path pPath = Paths.get(Config.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI());
			if (pPath.getFileName().toString().endsWith(".jar"))
				return true;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Instantiates a new JarFile object pointing at the current jar file. <br>
	 * 
	 * Note: DO NOT FORGET to close the JarFile after use to prevent data leaks.
	 **/
	public static JarFile getExecutionJarFile() throws URISyntaxException,
			IOException {
		Path p = Paths.get(Config.class.getProtectionDomain().getCodeSource()
				.getLocation().toURI());

		return new JarFile(p.toFile(), false);
	}
	
	/**
	 * Removes root directory and the files and directories under the given
	 * path recursively. If the path represents a file the file will
	 * be deleted.
	 *
	 * @param path the root dir or the file 
	 * @return true, if successful
	 */
	public static boolean removeRecursive(String path) {
		return IOUtils.removeRecursive(path, 0, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * Removes root directory and the files and directories under the given
	 * path recursively till the maximum depth. If the path represents a file 
	 * the file will be deleted.
	 *
	 * @param path the root dir or the file
	 * @param maximumDepth the maximum depth
	 * @return true, if successful<br>
	 * false, if the maximum depth was reached and there are still files
	 * or subdirectories
	 */
	public static boolean removeRecursive(String path, int maximumDepth) {
		return IOUtils.removeRecursive(path, 0, 0, maximumDepth);
	}

	/**
	 * Removes the files and directories under the given path recursively 
	 * starting at <i>startDepth</i> till the <i>endDepth</i> is reached. If the path represents a file 
	 * the file will be deleted.
	 *
	 * @param path the root dir or the file
	 * @param startDepth the start depth
	 * @param endDepth the end depth
	 * @return true, if successful<br>
	 * false, if the end depth was reached and there are still files
	 * or subdirectories
	 */
	public static boolean removeRecursive(String path, int startDepth, int endDepth) {
		return IOUtils.removeRecursive(path, startDepth, 0, endDepth);
	}	
	
	/**
	 * Removes the recursive.
	 *
	 * @param path the root dir
	 * @param startDepth the start depth
	 * @param currentDepth the current depth
	 * @param endDepth the end depth
	 * @return true, if successful
	 */
	private static boolean removeRecursive(String path, int startDepth, int currentDepth, int endDepth) {
		boolean result = true;
		File root = new File(path);			
		
		if (!root.exists()) return false;			
		
		if (root.isDirectory()) {
			for (File f: root.listFiles()) {				
				if (f.isFile() && startDepth <= currentDepth){
					f.delete();						
					result = result && !f.exists();					
					if (!result) return false;
				} else {
					if (endDepth >= currentDepth) {							
						result = result && removeRecursive(f.getAbsolutePath(), startDepth, currentDepth + 1 , endDepth);
						if (!result) return false;
					}
				}
			}
		}		
		
		if (startDepth <= currentDepth) {
			root.delete();
			result = result && !root.exists();
		}
		
		return result;
	}
	
	public static String getPathForOS(String path){
		if (OsCheck.isWindows()) {
			return path.replaceAll("((?<!\\\\)\\\\(?!\\\\))|(\\/)", "\\\\\\\\");
		} else {
			return path.replaceAll("([\\\\]+)", "/");
		}
	}
	
	public static String getPathDelimiterForOS(){		
		return File.separator;
	}
}
