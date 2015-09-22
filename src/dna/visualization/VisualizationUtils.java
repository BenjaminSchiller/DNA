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
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.monte.media.Buffer;
import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.avi.AVIWriter;
import org.monte.media.math.Rational;

import dna.util.Config;
import dna.util.Log;
import dna.visualization.graph.GraphPanel;
import dna.visualization.graph.GraphVisualization;

public class VisualizationUtils {

	/** Captures a screenshot of the most current GraphVisualization-Frame. **/
	public static void captureScreenshot(boolean waitForStabilization) {
		GraphVisualization.getCurrentGraphPanel().makeScreenshot(
				waitForStabilization);
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

	/** Capture a screenshot of the JFrame. **/
	public static void captureScreenshot(Component c, String dstDir,
			String filename, String format) {
		String name = c.getName();
		String suffix = "." + format;

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
		GraphVisualization.getCurrentGraphPanel().makeVideo();
	}

	/** Stops the video recording on the most current GraphVisualization-Frame. **/
	public static void stopVideo() throws InterruptedException, IOException {
		GraphVisualization.getCurrentGraphPanel().stopVideo();
	}

	/** Pauses the video recording process on the most current GraphVis-Frame. **/
	public static void pauseVideo() {
		GraphVisualization.getCurrentGraphPanel().pause();
	}

	/** Resumes the video recording process on the most current GraphVis-Frame. **/
	public static void resumeVideo() {
		GraphVisualization.getCurrentGraphPanel().resume();
	}

	/** Captures a video from the given Component. **/
	public static void captureVideo(Component c) throws InterruptedException,
			IOException {
		VisualizationUtils.captureVideo(c,
				VisualizationUtils.getVideoPath(c.getName()), null,
				Config.getInt("GRAPH_VIS_VIDEO_MAXIMUM_LENGTH_IN_SECONDS"),
				Config.getInt("GRAPH_VIS_VIDEO_DEFAULT_FPS"));
	}

	/** Captures a video from the given Component to the destination-path. **/
	public static void captureVideo(Component c, String dstDir)
			throws InterruptedException, IOException {
		VisualizationUtils.captureVideo(c, dstDir, null,
				Config.getInt("GRAPH_VIS_VIDEO_MAXIMUM_LENGTH_IN_SECONDS"),
				Config.getInt("GRAPH_VIS_VIDEO_DEFAULT_FPS"));
	}

	/** Captures a video from the given Component to the destination-path. **/
	public static void captureVideo(Component c, String dstDir,
			String filename, int timeInSeconds, int fps)
			throws InterruptedException, IOException {
		Log.info("capturing " + timeInSeconds + "s video from '" + c.getName()
				+ "'");
		String dstPath = dstDir;
		if (filename != null)
			dstPath += filename;

		long screenshotInterval = (long) Math.floor(1000 / fps);

		int amount = timeInSeconds * fps;

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
		VisualizationUtils.renderVideo(f, images);
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

	/** Returns the video path for the name. **/
	public static String getVideoPath(String name) {
		// craft filename
		String dir = Config.get("GRAPH_VIS_VIDEO_DIR");
		String suffix = Config.get("GRAPH_VIS_VIDEO_SUFFIX");
		DateFormat df = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
		String filename = name + "-" + df.format(new Date());

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

		protected Thread t;
		protected boolean running;
		protected boolean paused;

		protected JPanel callingPanel;

		protected Component srcComponent;
		protected String dstPath;
		protected int timeInSeconds;
		protected int fps;

		/** Run method. **/
		public void run() {
			try {
				// capture video
				captureVideo(srcComponent, dstPath, timeInSeconds, fps);

				if (this.callingPanel != null) {
					if (this.callingPanel instanceof GraphPanel)
						((GraphPanel) this.callingPanel).recordingStopped();

					// add other panels here for proper termination handling
				}
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}

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
				this.running = true;
				this.paused = false;
				this.t = new Thread(this, "VideoRecorder-Thread"
						+ random.nextFloat());
				this.t.start();
			}
		}

		/** Stops the current recording. **/
		public void stop() {
			this.running = false;
			this.paused = false;
		}

		/** Pauses the current recording. **/
		public void pause() {
			this.paused = true;
		}

		/** Resumes the current recording. **/
		public void resume() {
			this.paused = false;
		}

		/** Toggles the pause function. **/
		public void togglePause() {
			this.paused = !this.paused;
		}

		/** Sets if paused or not. **/
		public void setPause(boolean pauseEnabled) {
			this.paused = pauseEnabled;
		}

		/** Updates the destination path. **/
		public void updateDestinationPath() {
			this.dstPath = VisualizationUtils.getVideoPath(this.srcComponent
					.getName());
		}

		/** Updates the recording component. **/
		public void updateSourceComponent(Component srcComponent) {
			this.srcComponent = srcComponent;
		}

		public VideoRecorder(JPanel callingPanel, Component srcComponent) {
			this(callingPanel, srcComponent, VisualizationUtils
					.getVideoPath(srcComponent.getName()));
		}

		public VideoRecorder(JPanel callingPanel, Component srcComponent,
				String dstPath) {
			this(callingPanel, srcComponent, dstPath, Config
					.getInt("GRAPH_VIS_VIDEO_MAXIMUM_LENGTH_IN_SECONDS"),
					Config.getInt("GRAPH_VIS_VIDEO_DEFAULT_FPS"));
		}

		public VideoRecorder(JPanel callingPanel, Component srcComponent,
				String dstPath, int timeInSeconds, int fps) {
			this.callingPanel = callingPanel;
			this.srcComponent = srcComponent;
			this.dstPath = dstPath;
			this.timeInSeconds = timeInSeconds;
			this.fps = fps;
			this.paused = false;
		}

		/** Update the progress at the calling panel. **/
		protected void updateVideoProgress(double percent) {
			if (this.callingPanel != null) {
				if (this.callingPanel instanceof GraphPanel)
					((GraphPanel) this.callingPanel)
							.updateVideoProgress(percent);

				// add other panels here for progress update broadcasting
			}
		}

		/** Update the elapsed time of the video recording. **/
		protected void updateElapsedVideoTime(int seconds) {
			if (this.callingPanel != null) {
				if (this.callingPanel instanceof GraphPanel)
					((GraphPanel) this.callingPanel)
							.updateElapsedVideoTime(seconds);

				// add other panels here for progress update broadcasting
			}
		}

		/** Called when the capturing progress starts rendering. **/
		protected void updateVideoProgressRendering(String text) {
			if (this.callingPanel != null) {
				if (this.callingPanel instanceof GraphPanel)
					((GraphPanel) this.callingPanel).setVideoButtonText(text);

				// add other panels here for progress update broadcasting
			}
		}

		/** Captures a video from the given JFrame to the destination-path. **/
		protected void captureVideo(Component c, String dstPath,
				int timeInSeconds, int fps) throws InterruptedException,
				IOException {
			Log.info("GraphVis - capturing video to '" + dstPath + "'");
			long screenshotInterval = (long) Math.floor(1000 / fps);

			int amount = timeInSeconds * fps;

			// collect images
			BufferedImage[] images = new BufferedImage[amount];
			int counter = 0;
			int seconds = 0;
			for (int i = 0; i < amount; i++) {
				while (this.paused && this.running)
					Thread.sleep(100);
				if (!this.running)
					break;
				long start = System.currentTimeMillis();

				// take screenshot
				images[i] = VisualizationUtils.getScreenshot(c);

				// update progress approx. each second
				counter++;
				if (counter == fps) {
					this.updateElapsedVideoTime(seconds);
					counter = 0;
					seconds++;
				}

				long diff = System.currentTimeMillis() - start;
				if (diff < screenshotInterval)
					Thread.sleep(screenshotInterval - diff);

				while (this.paused && this.running)
					Thread.sleep(100);
				if (!this.running)
					break;
			}

			// render video
			File f = new File(dstPath);
			updateVideoProgressRendering("rendering");
			Log.info("rendering video to " + dstPath);
			VisualizationUtils.renderVideo(f, images);
			Log.info("video rendering done");

			// free space
			images = null;
		}
	}

}
