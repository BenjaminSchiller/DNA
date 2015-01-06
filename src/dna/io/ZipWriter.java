package dna.io;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import dna.util.Log;

/**
 * The ZipWriter is used to write several files into the same ZipFile. It uses
 * the ZipFileSystemProvider introduced in Java SE 7.
 * 
 * @author RWilmes
 * @date 16.03.2014
 */
public class ZipWriter extends Writer {

	private FileSystem zipFile;
	public static FileSystem writeFileSystem;

	private static FileSystem getFileSystem(URI uri, Map<String, ?> env)
			throws IOException {
		FileSystem fs;
		try {
			fs = FileSystems.newFileSystem(uri, env);
		} catch (FileSystemAlreadyExistsException e) {
			fs = FileSystems.getFileSystem(uri);
		}
		return fs;
	}

	public ZipWriter(String fsDir, String fsFileName, String dir,
			String filename) throws IOException {
		// if filesystem directory does not exist, create it
		Path fileSystemDir = Paths.get(fsDir);
		if (!Files.exists(fileSystemDir))
			Files.createDirectories(fileSystemDir);

		// if filesystem file does not exist, create it
		Path fsFile = Paths.get(fsDir + fsFileName);
		if (!Files.exists(fsFile))
			createZip(fsFile);

		// init filesystem settings
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		URI fsFileUri = URI.create("jar:" + fsFile.toUri().toString());

		// create filesystem
		FileSystem fs = getFileSystem(fsFileUri, env);
		this.zipFile = fs;

		// if directory does not exist, create it
		Path innerDir = fs.getPath(dir);
		if (!Files.exists(innerDir))
			Files.createDirectories(innerDir);

		// if file does not exist, create it
		Path innerFile = fs.getPath(dir + filename);
		if (!Files.exists(innerFile))
			Files.createFile(innerFile);

		// init buffered writer options
		OpenOption[] options = { StandardOpenOption.WRITE,
				StandardOpenOption.CREATE };

		// create buffered write
		this.writer = Files.newBufferedWriter(innerFile,
				StandardCharsets.UTF_8, options);
	}

	public ZipWriter(FileSystem fs, String dir, String filename)
			throws IOException {
		this.zipFile = fs;

		// if directory does not exist, create it
		Path innerDir = fs.getPath(dir);
		if (!Files.exists(innerDir))
			Files.createDirectories(innerDir);

		// if file does not exist, create it
		Path innerFile = fs.getPath(dir + filename);
		if (!Files.exists(innerFile))
			Files.createFile(innerFile);

		// init buffered writer options
		OpenOption[] options = { StandardOpenOption.WRITE,
				StandardOpenOption.CREATE };

		// create buffered writer
		this.writer = Files.newBufferedWriter(innerFile,
				StandardCharsets.UTF_8, options);
	}

	public void closeFileSystem() throws IOException {
		this.zipFile.close();
	}

	public void closeAll() throws IOException {
		this.writer.close();
		this.zipFile.close();
	}

	/** Creates an empty zip file on the specified path. **/
	public static void createZip(Path zipLocation) throws IOException {
		Map<String, String> env = new HashMap<String, String>();
		// check if file exists
		env.put("create", String.valueOf(!zipLocation.toFile().exists()));
		// use a Zip filesystem URI
		URI fileUri = zipLocation.toUri(); // here

		try {
			URI zipUri = new URI("jar:" + fileUri.getScheme(),
					fileUri.getPath(), null);

			// try with resource
			try (FileSystem zipfs = getFileSystem(zipUri, env)) {
			}
		} catch (URISyntaxException e) {
			Log.error("Failed to create zip file on path "
					+ zipLocation.toString() + ".");
			e.printStackTrace();
		}
	}

	/** Creates a zip filesystem for a batch in the specified directory. **/
	public static FileSystem createBatchFileSystem(String fsDir, String suffix,
			long timestamp) throws IOException {
		return createFileSystem(fsDir,
				dna.io.filesystem.Files.getBatchFilename(timestamp) + suffix);
	}

	/** Creates a zip filesystem for a run in the specified directory. **/
	public static FileSystem createRunFileSystem(String fsDir, String suffix,
			int run) throws IOException {
		return createFileSystem(fsDir,
				dna.io.filesystem.Files.getRunFilename(run) + suffix);
	}

	/** Creates a zip filesystem for a specified directory and filename. **/
	public static FileSystem createFileSystem(String fsDir, String filename)
			throws IOException {
		// chick if dir exists
		Path fileSystemDir = Paths.get(fsDir);
		if (!Files.exists(fileSystemDir))
			Files.createDirectories(fileSystemDir);

		// chick if file exists
		Path fsFile = Paths.get(fsDir + filename);
		if (!Files.exists(fsFile))
			ZipWriter.createZip(fsFile);

		// init filesystem settings
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		URI fsFileUri = URI.create("jar:" + fsFile.toUri().toString());

		// create filesystem
		return getFileSystem(fsFileUri, env);
	}
}
