package dna.visualization;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

import org.monte.media.Buffer;
import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.avi.AVIWriter;
import org.monte.media.math.Rational;

import dna.io.filesystem.Dir;
import dna.io.filesystem.Files;
import dna.io.filter.SuffixFilenameFilter;
import dna.util.Config;
import dna.util.Log;
import dna.visualization.config.graph.CaptureConfig;
import dna.visualization.graph.GraphPanel;
import dna.visualization.graph.GraphVisualization;

public class VisualizationUtils {

	/** Captures a screenshot of the most current GraphVisualization-Frame. **/
	public static void captureScreenshot(boolean waitForStabilization) {
		GraphVisualization.getCurrentGraphPanel().captureScreenshot(
				waitForStabilization);
	}

	/** Captures a screenshot of the most current GraphVisualization-Frame. **/
	public static void captureScreenshot(boolean waitForStabilization,
			String dstDir, String filename) {
		GraphVisualization.getCurrentGraphPanel().captureScreenshot(
				waitForStabilization, dstDir, filename);
	}

	/** Capture a screenshot of the JFrame. **/
	public static void captureScreenshot(Component c) {
		VisualizationUtils.captureScreenshot(c,
				Config.get("GRAPH_VIS_SCREENSHOT_DIR"), null,
				Config.get("GRAPH_VIS_SCREENSHOT_FORMAT"));
	}

	/** Capture a screenshot of the JFrame. **/
	public static void captureScreenshot(Component c, String dstDir,
			String filename) {
		VisualizationUtils.captureScreenshot(c, dstDir, filename,
				Config.get("GRAPH_VIS_SCREENSHOT_FORMAT"));
	}

	/** Captures a screenshot after some milliseconds. **/
	public static void captureScreenshotInMilliseconds(Component c,
			String dstDir, String filename, long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		VisualizationUtils.captureScreenshot(c, dstDir, filename,
				Config.get("GRAPH_VIS_SCREENSHOT_FORMAT"));
	}

	/** Capture a screenshot of the JFrame. **/
	public static void captureScreenshot(Component c, String dstDir,
			String filename, String format) {
		String name = c.getName();
		String suffix = Config.get("FILE_NAME_DELIMITER") + format;

		// create dir
		File f = new File(dstDir);
		if (!f.exists() && !f.isFile())
			f.mkdirs();

		// get date format
		DateFormat df = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");

		// check filename is set
		if (filename == null)
			filename = name + "-" + df.format(new Date());

		try {
			Robot robot = new Robot();

			// get bounds from component
			Point pos = c.getLocationOnScreen();
			Rectangle captureRect = c.getBounds();
			captureRect.setBounds(pos.x, pos.y, captureRect.width,
					captureRect.height);
			BufferedImage screenFullImage = robot
					.createScreenCapture(captureRect);

			// get name
			File file = new File(dstDir + filename + suffix);
			int id = 0;
			while (file.exists()) {
				id++;
				file = new File(dstDir + filename + "_" + id + suffix);
			}

			Log.info("GraphVis - saving screenshot to '" + file.getPath() + "'");
			ImageIO.write(screenFullImage, format, file);
		} catch (AWTException | IOException ex) {
			System.err.println(ex);
		}
	}

	/** Captures a video of the most current GraphVisualization-Frame. **/
	public static void captureVideo() throws InterruptedException, IOException {
		GraphVisualization.getCurrentGraphPanel().captureVideo();
	}

	/** Captures a video of the most current GraphVisualization-Frame. **/
	public static void captureVideo(int lengthInSeconds)
			throws InterruptedException, IOException {
		GraphPanel p = GraphVisualization.getCurrentGraphPanel();
		VisualizationUtils.captureVideo(p,
				VisualizationUtils.getVideoPath(p.getName()), null,
				lengthInSeconds,
				Config.getInt("GRAPH_VIS_VIDEO_DEFAULT_RECORDING_FPS"),
				Config.getInt("GRAPH_VIS_VIDEO_DEFAULT_FPS"));
	}

	/** Captures a video of the most current GraphVisualization-Frame. **/
	public static void captureVideo(String dstDir, String filename,
			int lengthInSeconds, int recordFps, int videoFps)
			throws InterruptedException, IOException {
		VisualizationUtils.captureVideo(
				GraphVisualization.getCurrentGraphPanel(), dstDir, filename,
				lengthInSeconds, recordFps, videoFps);
	}

	/** Stops the video recording on the most current GraphVisualization-Frame. **/
	public static void stopVideo() throws InterruptedException, IOException {
		GraphVisualization.getCurrentGraphPanel().stopVideo();
	}

	/** Pauses the video recording process on the most current GraphVis-Frame. **/
	public static void pauseVideo() {
		GraphVisualization.getCurrentGraphPanel().pauseVideo();
	}

	/** Resumes the video recording process on the most current GraphVis-Frame. **/
	public static void resumeVideo() {
		GraphVisualization.getCurrentGraphPanel().resumeVideo();
	}

	/** Captures a video from the given Component. **/
	public static void captureVideo(Component c) throws InterruptedException,
			IOException {
		VisualizationUtils.captureVideo(c,
				VisualizationUtils.getVideoPath(c.getName()), null,
				Config.getInt("GRAPH_VIS_VIDEO_MAXIMUM_LENGTH_IN_SECONDS"),
				Config.getInt("GRAPH_VIS_VIDEO_DEFAULT_RECORDING_FPS"),
				Config.getInt("GRAPH_VIS_VIDEO_DEFAULT_FPS"));
	}

	/** Captures a video from the given Component to the destination-path. **/
	public static void captureVideo(Component c, String dstDir)
			throws InterruptedException, IOException {
		VisualizationUtils.captureVideo(c, dstDir, null,
				Config.getInt("GRAPH_VIS_VIDEO_MAXIMUM_LENGTH_IN_SECONDS"),
				Config.getInt("GRAPH_VIS_VIDEO_DEFAULT_RECORDING_FPS"),
				Config.getInt("GRAPH_VIS_VIDEO_DEFAULT_FPS"));
	}

	/** Captures a video from the given Component to the destination-path. **/
	public static void captureVideo(Component c, String dstDir,
			int lengthInSeconds) throws InterruptedException, IOException {
		VisualizationUtils.captureVideo(c, dstDir, null, lengthInSeconds,
				Config.getInt("GRAPH_VIS_VIDEO_DEFAULT_RECORDING_FPS"),
				Config.getInt("GRAPH_VIS_VIDEO_DEFAULT_FPS"));
	}

	/** Captures a video from the given Component to the destination-path. **/
	public static void captureVideo(Component c, String dstDir,
			String filename, int lengthInSeconds, int recordFps, int videoFps)
			throws InterruptedException, IOException {
		Log.info("capturing " + lengthInSeconds + "s video from '"
				+ c.getName() + "'");
		String dstPath = dstDir;
		if (filename != null)
			dstPath += filename;

		long screenshotInterval = (long) Math.floor(1000 / recordFps);

		int amount = lengthInSeconds * videoFps;

		BufferedImage[] images = new BufferedImage[amount];

		for (int i = 0; i < amount; i++) {
			long start = System.currentTimeMillis();
			images[i] = VisualizationUtils.getScreenshot(c);
			long diff = System.currentTimeMillis() - start;
			if (diff < screenshotInterval)
				Thread.sleep(screenshotInterval - diff);
		}

		File f = new File(dstPath);
		Log.info("rendering video to " + dstDir);
		VisualizationUtils.renderVideo(f, images, videoFps);
		Log.info("video rendering done");
		images = null;
		System.gc();
	}

	/** Renders a video from the given jpeg frames. **/
	public static void renderVideo(File file, BufferedImage[] frames)
			throws IOException {
		VisualizationUtils.renderVideo(file, frames,
				Config.getInt("GRAPH_VIS_VIDEO_DEFAULT_FPS"),
				Config.getBoolean("GRAPH_VIS_VIDEO_USE_TECHSMITH"));
	}

	/** Renders a video from the given jpeg frames. **/
	public static void renderVideo(File file, BufferedImage[] frames, int fps)
			throws IOException {
		VisualizationUtils.renderVideo(file, frames, fps,
				Config.getBoolean("GRAPH_VIS_VIDEO_USE_TECHSMITH"));
	}

	/** Renders a video from the given jpeg frames. **/
	public static void renderVideo(File file, BufferedImage[] frames,
			boolean useTecHSmithCodec) throws IOException {
		VisualizationUtils
				.renderVideo(file, frames,
						Config.getInt("GRAPH_VIS_VIDEO_DEFAULT_FPS"),
						useTecHSmithCodec);
	}

	/** Renders a video from the given jpeg frames. **/
	public static void renderVideo(File file, BufferedImage[] frames, int fps,
			boolean useTechSmithCodec) throws IOException {
		// check if directory exists
		if (!file.getParentFile().exists())
			file.mkdirs();

		// craft format
		AVIWriter out = new AVIWriter(file);
		// MovieWriter out = Registry.getInstance().getWriter(file);

		Object encodingFormat;
		if (useTechSmithCodec)
			encodingFormat = org.monte.media.VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE;
		else
			encodingFormat = org.monte.media.VideoFormatKeys.ENCODING_AVI_MJPG;

		Format format = new Format(
				org.monte.media.VideoFormatKeys.MediaTypeKey, MediaType.VIDEO,
				org.monte.media.VideoFormatKeys.EncodingKey, encodingFormat,
				org.monte.media.VideoFormatKeys.FrameRateKey, new Rational(fps,
						1), org.monte.media.VideoFormatKeys.WidthKey,
				frames[0].getWidth(),
				org.monte.media.VideoFormatKeys.HeightKey,
				frames[0].getHeight(),
				org.monte.media.VideoFormatKeys.DepthKey, 24);
		int track = out.addTrack(format);

		// write video vile
		try {
			out.addTrack(format);

			Buffer buf = new Buffer();
			buf.format = new Format(
					org.monte.media.VideoFormatKeys.DataClassKey,
					BufferedImage.class);
			buf.sampleDuration = format.get(
					org.monte.media.VideoFormatKeys.FrameRateKey).inverse();
			for (int i = 0; i < frames.length; i++) {
				buf.data = frames[i];
				out.write(track, buf);
			}
		} finally {
			out.close();
		}
	}

	/** Renders a video from the given jpeg frames. **/
	public static void renderVideoFromDirectory(File file, String dir,
			String imageFormat, int fps, boolean useTechSmithCodec)
			throws IOException {
		// check if directory exists
		if (!file.getParentFile().exists())
			file.mkdirs();

		// craft format
		AVIWriter out = new AVIWriter(file);
		// MovieWriter out = Registry.getInstance().getWriter(file);

		Object encodingFormat;
		if (useTechSmithCodec)
			encodingFormat = org.monte.media.VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE;
		else
			encodingFormat = org.monte.media.VideoFormatKeys.ENCODING_AVI_MJPG;

		// read first image
		String[] images = VisualizationUtils.getImages(dir, imageFormat);
		BufferedImage init = ImageIO.read(new File(dir + images[0]));

		Format format = new Format(
				org.monte.media.VideoFormatKeys.MediaTypeKey, MediaType.VIDEO,
				org.monte.media.VideoFormatKeys.EncodingKey, encodingFormat,
				org.monte.media.VideoFormatKeys.FrameRateKey, new Rational(fps,
						1), org.monte.media.VideoFormatKeys.WidthKey,
				init.getWidth(), org.monte.media.VideoFormatKeys.HeightKey,
				init.getHeight(), org.monte.media.VideoFormatKeys.DepthKey, 24);
		int track = out.addTrack(format);

		// write video vile
		try {
			out.addTrack(format);

			Buffer buf = new Buffer();
			buf.format = new Format(
					org.monte.media.VideoFormatKeys.DataClassKey,
					BufferedImage.class);
			buf.sampleDuration = format.get(
					org.monte.media.VideoFormatKeys.FrameRateKey).inverse();

			for (int i = 0; i < images.length; i++) {
				BufferedImage image = ImageIO.read(new File(dir + images[i]));
				buf.data = image;
				out.write(track, buf);
			}
		} finally {
			// close output
			out.close();

			// remove temp dir
			File tempDir = new File(dir);
			Files.delete(tempDir);
		}
	}

	public static String[] getImages(String dir, String format) {
		return (new File(dir)).list(new SuffixFilenameFilter(Config
				.get("FILE_NAME_DELIMITER") + format));
	}

	/** Returns the video path for the name. **/
	public static String getVideoPath(String name) {
		// craft filename
		String dir = Config.get("GRAPH_VIS_VIDEO_DIR");
		String suffix = Config.get("GRAPH_VIS_VIDEO_SUFFIX");
		DateFormat df = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
		String filename = name + "-" + df.format(new Date());

		if (!Config.get("GRAPH_VIS_VIDEO_FILENAME").equals("null"))
			return dir + Config.get("GRAPH_VIS_VIDEO_FILENAME") + suffix;

		// get name
		File file = new File(dir + filename + suffix);
		int id = 0;
		while (file.exists()) {
			id++;
			file = new File(dir + filename + "_" + id + suffix);
		}

		return file.getPath();
	}

	/** Returns the video path for the component. **/
	public static String getVideoPath(Component c) {
		String dir;
		String suffix;
		String filename;
		if (c instanceof GraphPanel) {
			dir = ((GraphPanel) c).getGraphPanelConfig().getCaptureConfig()
					.getVideoDir();
			suffix = ((GraphPanel) c).getGraphPanelConfig().getCaptureConfig()
					.getVideoSuffix();
			filename = ((GraphPanel) c).getGraphPanelConfig()
					.getCaptureConfig().getVideoFilename();
		} else {
			dir = Config.get("GRAPH_VIS_VIDEO_DIR");
			suffix = Config.get("GRAPH_VIS_VIDEO_SUFFIX");
			filename = Config.get("GRAPH_VIS_VIDEO_FILENAME");
		}

		if (!filename.equals("null"))
			return dir + filename + suffix;

		// craft filename
		DateFormat df = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
		filename = c.getName() + "-" + df.format(new Date());

		// get name
		File file = new File(dir + filename + suffix);
		int id = 0;
		while (file.exists()) {
			id++;
			file = new File(dir + filename + "_" + id + suffix);
		}

		return file.getPath();
	}

	/** Captures a screenshot from the given JFrame. **/
	public static BufferedImage getScreenshot(Component c) {
		try {
			Robot robot = new Robot();

			// get bounds from component
			Point pos = c.getLocationOnScreen();
			Rectangle captureRect = c.getBounds();
			captureRect.setBounds(pos.x, pos.y, captureRect.width,
					captureRect.height);
			BufferedImage screenFullImage = robot
					.createScreenCapture(captureRect);

			return screenFullImage;
		} catch (AWTException ex) {
			System.err.println(ex);
		}

		return null;
	}

	/**
	 * VideoRecorder class is used to capture a video from a JFrame in a
	 * separate thread.
	 **/
	public static class VideoRecorder implements Runnable {

		public enum RecordMode {
			normal, steps
		}

		protected Thread t;
		protected int id;
		protected String tempDir;
		protected boolean bufferOnFilesystem;
		protected boolean running;
		protected boolean blocked;
		protected boolean paused;

		protected RecordMode mode;
		protected int stepsLeft;

		protected RecordMode tempMode;

		protected ArrayList<Component> registeredComponents;

		protected Component srcComponent;
		protected String dstPath;
		protected int timeInSeconds;
		protected int recordFps;
		protected int videoFps;

		// constructors
		public VideoRecorder(Component srcComponent) {
			this(srcComponent, VisualizationUtils.getVideoPath(srcComponent));
		}

		public VideoRecorder(Component srcComponent, String dstPath) {
			this(
					srcComponent,
					dstPath,
					Config.getInt("GRAPH_VIS_VIDEO_MAXIMUM_LENGTH_IN_SECONDS"),
					Config.getInt("GRAPH_VIS_VIDEO_DEFAULT_RECORDING_FPS"),
					Config.getInt("GRAPH_VIS_VIDEO_DEFAULT_FPS"),
					Config.getBoolean("GRAPH_VIS_VIDEO_BUFFER_IMAGES_ON_FILESYSTEM"),
					RecordMode.normal);
		}

		public VideoRecorder(Component srcComponent, CaptureConfig cfg,
				RecordMode recordMode) {
			this(srcComponent, VisualizationUtils.getVideoPath(srcComponent),
					cfg.getVideoMaximumLength(), cfg.getVideoRecordFps(), cfg
							.getVideoFileFps(), cfg.isVideoBufferImagesOnFs(),
					recordMode);
		}

		public VideoRecorder(Component srcComponent, String dstPath,
				int timeInSeconds, int recordFps, int videoFps,
				boolean bufferOnFilesystem, RecordMode mode) {
			this.srcComponent = srcComponent;
			this.dstPath = dstPath;
			this.timeInSeconds = timeInSeconds;
			this.recordFps = recordFps;
			this.videoFps = videoFps;
			this.bufferOnFilesystem = bufferOnFilesystem;
			this.blocked = false;
			this.paused = false;
			this.mode = mode;
			this.tempMode = mode;
			this.registeredComponents = new ArrayList<Component>();
		}

		/*
		 * METHODS
		 */

		/** Run method. **/
		public void run() {
			try {
				// capture video
				captureVideo(srcComponent, dstPath, timeInSeconds, recordFps,
						videoFps, mode);
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}

			// report
			this.reportVideoStopped();

			// if temporary record mode has been set, update
			this.mode = tempMode;

			// end of run
			this.running = false;
			this.paused = false;
			this.t = null;
			return;
		}

		/** Starts a new recording. **/
		public void start() {
			if (this.t == null) {
				Random random = new Random();
				this.id = Math.abs(random.nextInt());
				this.running = true;
				this.paused = false;
				this.t = new Thread(this, "VideoRecorder-Thread" + this.id);
				this.t.start();
			}
		}

		/** Stops the current recording. **/
		public void stop(boolean forced) {
			if (forced) {
				this.running = false;
				this.paused = false;
			} else {
				while (this.stepsLeft > 0) {
					this.blocked = true;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				this.blocked = false;
				this.running = false;
				this.paused = false;
			}
		}

		/** Stops the current recording. Note: Forces immediate stop. **/
		public void stop() {
			this.stop(true);
		}

		/** Pauses the current recording. **/
		public void pause() {
			if (this.running) {
				this.paused = true;

				// report
				this.reportVideoPaused();
			}
		}

		/** Resumes the current recording. **/
		public void resume() {
			if (this.running) {
				this.paused = false;

				// report
				this.reportVideoResumed();
			}
		}

		/** Toggles the pause function. **/
		public void togglePause() {
			if (this.paused)
				this.resume();
			else
				this.pause();
		}

		/** Sets if paused or not. **/
		public void setPause(boolean pauseEnabled) {
			this.paused = pauseEnabled;
		}

		/** Sets the record mode. Will take effect after recording is inished. **/
		public void setRecordMode(RecordMode mode) {
			if (this.running)
				this.tempMode = mode;
			else
				this.mode = mode;
		}

		/** Returns the record mode. **/
		public RecordMode getRecordMode() {
			return this.mode;
		}

		/** Adds a step to the recording. Only works in steps mode. **/
		public void addStep() {
			if (this.mode.equals(RecordMode.steps) && !this.blocked)
				this.stepsLeft++;
		}

		/** Adds steps to the recording. Only works in steps mode. **/
		public void addSteps(int steps) {
			if (this.mode.equals(RecordMode.steps) && !this.blocked) {
				this.stepsLeft += steps;
			}
		}

		/** Returns how many steps are left. **/
		public int getStepsLeft() {
			return this.stepsLeft;
		}

		/** Updates the destination path. **/
		public void updateDestinationPath() {
			this.dstPath = VisualizationUtils.getVideoPath(this.srcComponent
					.getName());
		}

		/** Updates the destination path. **/
		public void updateDestinationPath(Component srcComponent) {
			this.dstPath = VisualizationUtils.getVideoPath(srcComponent);
		}

		/** Updates the recording component. **/
		public void updateSourceComponent(Component srcComponent) {
			this.srcComponent = srcComponent;
		}

		/** Updates the configurable parameters. **/
		public void updateConfiguration(CaptureConfig cfg) {
			this.timeInSeconds = cfg.getVideoMaximumLength();
			this.recordFps = cfg.getVideoRecordFps();
			this.videoFps = cfg.getVideoFileFps();
			this.bufferOnFilesystem = cfg.isVideoBufferImagesOnFs();
		}

		/** Captures the video step by step. **/
		protected void captureVideoStepByStep(Component c, String dstPath,
				int videoFps) throws InterruptedException, IOException {
			// report
			this.reportVideoStarted();
			Log.info("GraphVis - capturing video to '" + dstPath + "'");

			// if buffer
			if (this.bufferOnFilesystem) {
				// create tempDir
				this.tempDir = this.createTempDir();
			}

			DateFormat df = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
			String format = "png";

			ArrayList<BufferedImage> imagesList = new ArrayList<BufferedImage>();
			int counter = 0;
			BufferedImage image;
			while (this.running) {
				if (this.paused)
					Thread.sleep(100);
				if (!this.running)
					break;

				// if steps left
				if (this.stepsLeft > 0) {
					// make screenshot
					image = VisualizationUtils.getScreenshot(c);

					// if buffer
					if (this.bufferOnFilesystem) {
						// write
						this.writeImage(image, df, format);
					} else {
						// buffer in list
						imagesList.add(image);
					}

					// free space
					image = null;

					// update progress
					counter++;
					this.updateVideoProgressScreenshotsTaken(counter);

					stepsLeft--;
				} else {
					// sleep shortly
					Thread.sleep(50);
				}
			}

			// render video
			this.renderVideo(imagesList, format, dstPath, videoFps,
					Config.getBoolean("GRAPH_VIS_VIDEO_USE_TECHSMITH"));
		}

		/** Captures a video from the given JFrame to the destination-path. **/
		protected void captureVideo(Component c, String dstPath,
				int timeInSeconds, int recordFps, int videoFps, RecordMode mode)
				throws InterruptedException, IOException {
			if (mode.equals(RecordMode.steps)) {
				captureVideoStepByStep(c, dstPath, videoFps);
			} else {
				// report
				this.reportVideoStarted();

				// if buffer
				if (this.bufferOnFilesystem) {
					// create tempDir
					this.tempDir = this.createTempDir();
				}

				DateFormat df = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
				String format = "png";

				Log.info("GraphVis - capturing video to '" + dstPath + "'");
				long screenshotInterval = (long) Math.floor(1000 / recordFps);

				int maxAmount = timeInSeconds * videoFps;

				// collect images
				ArrayList<BufferedImage> imagesList = new ArrayList<BufferedImage>();
				BufferedImage image;
				int bufferThreshold = 10;
				int buffered = 0;
				int counter = 0;
				int seconds = 0;
				this.updateElapsedVideoTime(0);

				while (this.running) {
					// if paused, pause
					while (this.paused && this.running)
						Thread.sleep(100);

					// if stoppped, stop
					if (!this.running)
						break;

					// start time
					long start = System.currentTimeMillis();

					// take screenshot
					image = VisualizationUtils.getScreenshot(c);

					// if buffer
					if (this.bufferOnFilesystem) {
						if (buffered >= bufferThreshold) {
							for (int i = 0; i < buffered; i++) {
								this.writeImage(imagesList.get(i), df, format);
							}
							imagesList.clear();
							buffered = 0;
						} else {
							imagesList.add(image);
							buffered++;
						}

						// // write
						// this.writeImage(image, df, format);
					} else {
						// buffer in list
						imagesList.add(image);
					}

					// free space
					image = null;

					// update progress approx. each second
					counter++;
					if (counter == recordFps) {
						this.updateElapsedVideoTime(seconds);
						counter = 0;
						seconds++;
					}

					// if max amount of frames reached
					if (counter >= maxAmount)
						this.stop();

					long diff = System.currentTimeMillis() - start;
					// System.out.println(diff + "\t" + screenshotInterval);
					if (diff < screenshotInterval)
						Thread.sleep(screenshotInterval - diff);

					// if stopped, stop
					if (!this.running) {
						// write already buffered images
						if (this.bufferOnFilesystem && buffered > 0) {
							for (int i = 0; i < buffered; i++) {
								this.writeImage(imagesList.get(i), df, format);
							}
							imagesList.clear();
						}
						break;
					}
				}

				// render video
				this.renderVideo(imagesList, "png", dstPath, videoFps,
						Config.getBoolean("GRAPH_VIS_VIDEO_USE_TECHSMITH"));
			}
		}

		/** Renders the given images to the specified location. **/
		protected void renderVideo(ArrayList<BufferedImage> imagesList,
				String imageFormat, String dstPath, int videoFps,
				boolean useTechSmith) throws IOException {
			File f = new File(dstPath);
			updateVideoProgressRendering("rendering");
			Log.info("rendering video to " + dstPath);

			// if buffer on filesystem
			if (this.bufferOnFilesystem) {
				VisualizationUtils.renderVideoFromDirectory(f, this.tempDir,
						imageFormat, videoFps, useTechSmith);
			} else {
				// convert list to array
				BufferedImage[] images = imagesList
						.toArray(new BufferedImage[imagesList.size()]);
				imagesList = null;

				VisualizationUtils.renderVideo(f, images, videoFps,
						useTechSmith);

				// free space
				images = null;
			}

			Log.info("video rendering done");
		}

		/** Writes the image to the filesystem. **/
		protected void writeImage(BufferedImage image, DateFormat df,
				String format) throws IOException {
			// check filename is set
			String filename = this.srcComponent.getName() + "-"
					+ df.format(new Date());

			// get name
			File file = new File(tempDir + filename
					+ Config.get("FILE_NAME_DELIMITER") + format);
			int id = 0;
			while (file.exists()) {
				id++;
				file = new File(tempDir + filename + "_" + id
						+ Config.get("FILE_NAME_DELIMITER") + format);
			}

			// write
			ImageIO.write(image, format, file);
		}

		/** Register a new component for status broadcasting. **/
		public void registerComponent(Component c) {
			if (!this.registeredComponents.contains(c))
				this.registeredComponents.add(c);
		}

		/** Removes the component from the registered components. **/
		public void unregisterComponent(Component c) {
			if (this.registeredComponents.contains(c))
				this.registeredComponents.remove(c);
		}

		/** Reports video started to all registered components. **/
		protected void reportVideoStarted() {
			for (Component c : this.registeredComponents) {
				if (c instanceof GraphPanel)
					((GraphPanel) c).reportVideoStarted();

				// add other panels here for proper video handling.
			}
		}

		/** Reports video stopped to all registered components. **/
		protected void reportVideoStopped() {
			for (Component c : this.registeredComponents) {
				if (c instanceof GraphPanel)
					((GraphPanel) c).reportVideoStopped();

				// add other panels here for proper video handling.
			}
		}

		/** Reports video paused to all registered components. **/
		protected void reportVideoPaused() {
			for (Component c : this.registeredComponents) {
				if (c instanceof GraphPanel)
					((GraphPanel) c).reportVideoPaused();

				// add other panels here for proper pause handling.
			}
		}

		/** Reports video resumed to all registered components. **/
		protected void reportVideoResumed() {
			for (Component c : this.registeredComponents) {
				if (c instanceof GraphPanel)
					((GraphPanel) c).reportVideoResumed();

				// add other panels here for proper pause handling.
			}
		}

		/** Update the progress at the registered components. **/
		protected void updateVideoProgressScreenshotsTaken(int amount) {
			for (Component c : this.registeredComponents) {
				if (c instanceof GraphPanel)
					((GraphPanel) c).updateVideoProgressText("" + amount);

				// add other panels here for progress update broadcasting.
			}

		}

		/** Update the progress at the registered components. **/
		protected void updateVideoProgress(double percent) {
			for (Component c : this.registeredComponents) {
				if (c instanceof GraphPanel)
					((GraphPanel) c).updateVideoProgress(percent);

				// add other panels here for progress update broadcasting.
			}
		}

		/** Update the elapsed time of the video recording. **/
		protected void updateElapsedVideoTime(int seconds) {
			for (Component c : this.registeredComponents) {
				if (c instanceof GraphPanel)
					((GraphPanel) c).updateElapsedVideoTime(seconds);

				// add other panels here for progress update broadcasting.
			}
		}

		/** Called when the capturing progress starts rendering. **/
		protected void updateVideoProgressRendering(String text) {
			for (Component c : this.registeredComponents) {
				if (c instanceof GraphPanel)
					((GraphPanel) c).setVideoButtonText(text);

				// add other panels here for progress update broadcasting.
			}
		}

		/** Returns the temp-Dir of the video-recorder. **/
		protected String createTempDir() {
			DateFormat df = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");

			String tempDir = Config.get("GRAPH_VIS_VIDEO_DIR") + this.id
					+ Config.get("FILE_NAME_DELIMITER") + df.format(new Date())
					+ Dir.tempSuffix + Dir.delimiter;

			File f = new File(tempDir);
			if (!f.exists() && !f.isFile())
				f.mkdirs();

			return tempDir;
		}
	}

}
