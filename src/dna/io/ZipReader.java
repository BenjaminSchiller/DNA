package dna.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ZipReader extends Reader {

	private FileSystem zipFile;

	public ZipReader(FileSystem fs, String dir, String filename)
			throws IOException, FileNotFoundException {
//		super(dir, filename);
		this.zipFile = fs;

		// if directory does not exist, create it
		Path innerDir = fs.getPath(dir);
		if (!Files.exists(innerDir))
			throw new FileNotFoundException(innerDir.toString());

		// if file does not exist, create it
		Path innerFile = fs.getPath(dir + filename);
		if (!Files.exists(innerFile))
			throw new FileNotFoundException(innerFile.toString());

		// create buffered reader
		this.reader = Files
				.newBufferedReader(innerFile, StandardCharsets.UTF_8);
	}

	public void closeFileSystem() throws IOException {
		this.zipFile.close();
	}

	public void closeAll() throws IOException {
		this.reader.close();
		this.zipFile.close();
	}

	/** Creates a zip filesystem for a batch in the specified directory. **/
	public static FileSystem getBatchFileSystem(String fsDir, long timestamp)
			throws Throwable {
		return getFileSystem(fsDir,
				dna.io.filesystem.Files.getBatchFilename(timestamp));
	}

	/** Creates a zip filesystem for a run in the specified directory. **/
	public static FileSystem getRunFileSystem(String fsDir, int run)
			throws Throwable {
		return getFileSystem(fsDir, dna.io.filesystem.Files.getRunFilename(run));
	}

	/** Creates a zip filesystem for a specified directory and filename. **/
	public static FileSystem getFileSystem(String fsDir, String filename)
			throws Throwable {
		Path fileSystemDir = Paths.get(fsDir);
		if (!Files.exists(fileSystemDir))
			throw new FileNotFoundException(fileSystemDir.toString());

		Path fsFile = Paths.get(fsDir + filename);
		System.out.println("TESTING: " + fsFile + "   ex:"
				+ Files.exists(fsFile) + "    isDir:"
				+ Files.isDirectory(fsFile));
		if (!Files.exists(fsFile))
			throw new FileNotFoundException(fsFile.toString());

		// init filesystem settings
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		URI fsFileUri = URI.create("jar:" + fsFile.toUri().toString());

		// create filesystem
		return FileSystems.newFileSystem(fsFileUri, env);
	}

}
