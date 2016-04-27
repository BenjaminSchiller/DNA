package dna.visualization.config.graph;

import dna.visualization.config.JSON.JSONObject;

public class CaptureConfig {

	public enum CaptureArea {
		content, graph, full
	}

	protected CaptureArea captureArea;
	protected String videoDir;
	protected String videoSuffix;
	protected String videoFilename;
	protected int videoMaximumLength;
	protected int videoRecordFps;
	protected boolean videoAutoRecord;
	protected String screenshotDir;
	protected String screenshotFormat;
	protected double screenshotStabilityThreshold;
	protected int screenshotStabilityTimeout;
	protected int screenshotForegroundDelay;

	public CaptureConfig(CaptureArea captureArea, String videoDir,
			String videoSuffix, String videoFilename, int videoMaximumLength,
			int videoRecordFps, boolean videoAutoRecord, String screenshotDir,
			String screenshotFormat, double screenshotStabilityThreshold,
			int screenshotStabilityTimeout, int screenshotForegroundDelay) {
		this.captureArea = captureArea;
		this.videoDir = videoDir;
		this.videoSuffix = videoSuffix;
		this.videoFilename = videoFilename;
		this.videoMaximumLength = videoMaximumLength;
		this.videoRecordFps = videoRecordFps;
		this.videoAutoRecord = videoAutoRecord;
		this.screenshotDir = screenshotDir;
		this.screenshotFormat = screenshotFormat;
		this.screenshotStabilityThreshold = screenshotStabilityThreshold;
		this.screenshotStabilityTimeout = screenshotStabilityTimeout;
		this.screenshotForegroundDelay = screenshotForegroundDelay;
	}

	public static CaptureConfig getFromJSONObject(JSONObject o) {
		GraphPanelConfig defGraphPanelConfig = GraphPanelConfig.defaultGraphPanelConfig;

		CaptureArea captureArea = CaptureArea.content;
		String videoDir = "videos/";
		String videoSuffix = ".avi";
		String videoFilename = "null";
		int videoMaximumLength = 30;
		int videoRecordFps = 15;
		boolean videoAutoRecord = false;
		String screenshotDir = "images/";
		String screenshotFormat = "png";
		double screenshotStabilityThreshold = 0.9;
		int screenshotStabilityTimeout = 2000;
		int screenshotForegroundDelay = 100;

		if (defGraphPanelConfig != null) {
			CaptureConfig def = defGraphPanelConfig.getCaptureConfig();
			// get default values
			captureArea = def.getCaptureArea();
			videoDir = def.getVideoDir();
			videoSuffix = def.getVideoSuffix();
			videoFilename = def.getVideoFilename();
			videoMaximumLength = def.getVideoMaximumLength();
			videoRecordFps = def.getVideoRecordFps();
			videoAutoRecord = def.isVideoAutoRecord();
			screenshotDir = def.getScreenshotDir();
			screenshotFormat = def.getScreenshotFormat();
			screenshotStabilityThreshold = def
					.getScreenshotStabilityThreshold();
			screenshotStabilityTimeout = def.getScreenshotStabilityTimeout();
			screenshotForegroundDelay = def.getScreenshotForegroundDelay();
		}

		if (JSONObject.getNames(o) != null) {
			for (String s : JSONObject.getNames(o)) {
				switch (s) {
				case "CaptureArea":
					captureArea = CaptureArea.valueOf(o.getString(s));
					break;
				case "ScreenshotDir":
					screenshotDir = o.getString(s);
					break;
				case "ScreenshotForegroundDelay":
					screenshotForegroundDelay = o.getInt(s);
					break;
				case "ScreenshotFormat":
					screenshotFormat = o.getString(s);
					break;
				case "ScreenshotStabilityThreshold":
					screenshotStabilityThreshold = o.getDouble(s);
					break;
				case "ScreenshotStabilityTimeout":
					screenshotStabilityTimeout = o.getInt(s);
					break;
				case "VideoAutoRecord":
					videoAutoRecord = o.getBoolean(s);
					break;
				case "VideoDir":
					videoDir = o.getString(s);
					break;
				case "VideoFilename":
					videoFilename = o.getString(s);
					break;
				case "VideoMaximumLength":
					videoMaximumLength = o.getInt(s);
					break;
				case "VideoRecordFPS":
					videoRecordFps = o.getInt(s);
					break;
				case "VideoSuffix":
					videoSuffix = o.getString(s);
					break;
				}
			}
		}

		return new CaptureConfig(captureArea, videoDir, videoSuffix,
				videoFilename, videoMaximumLength, videoRecordFps,
				videoAutoRecord, screenshotDir, screenshotFormat,
				screenshotStabilityThreshold, screenshotStabilityTimeout,
				screenshotForegroundDelay);
	}

	public CaptureArea getCaptureArea() {
		return captureArea;
	}

	public String getVideoDir() {
		return videoDir;
	}

	public String getVideoSuffix() {
		return videoSuffix;
	}

	public String getVideoFilename() {
		return videoFilename;
	}

	public int getVideoMaximumLength() {
		return videoMaximumLength;
	}

	public int getVideoRecordFps() {
		return videoRecordFps;
	}

	public boolean isVideoAutoRecord() {
		return videoAutoRecord;
	}

	public String getScreenshotDir() {
		return screenshotDir;
	}

	public String getScreenshotFormat() {
		return screenshotFormat;
	}

	public double getScreenshotStabilityThreshold() {
		return screenshotStabilityThreshold;
	}

	public int getScreenshotStabilityTimeout() {
		return screenshotStabilityTimeout;
	}

	public int getScreenshotForegroundDelay() {
		return screenshotForegroundDelay;
	}

	public void setCaptureArea(CaptureArea captureArea) {
		this.captureArea = captureArea;
	}

	public void setVideoDir(String videoDir) {
		this.videoDir = videoDir;
	}

	public void setVideoSuffix(String videoSuffix) {
		this.videoSuffix = videoSuffix;
	}

	public void setVideoFilename(String videoFilename) {
		this.videoFilename = videoFilename;
	}

	public void setVideoMaximumLength(int videoMaximumLength) {
		this.videoMaximumLength = videoMaximumLength;
	}

	public void setVideoRecordFps(int videoRecordFps) {
		this.videoRecordFps = videoRecordFps;
	}

	public void setVideoAutoRecord(boolean videoAutoRecord) {
		this.videoAutoRecord = videoAutoRecord;
	}

	public void setScreenshotDir(String screenshotDir) {
		this.screenshotDir = screenshotDir;
	}

	public void setScreenshotFormat(String screenshotFormat) {
		this.screenshotFormat = screenshotFormat;
	}

	public void setScreenshotStabilityThreshold(
			double screenshotStabilityThreshold) {
		this.screenshotStabilityThreshold = screenshotStabilityThreshold;
	}

	public void setScreenshotStabilityTimeout(int screenshotStabilityTimeout) {
		this.screenshotStabilityTimeout = screenshotStabilityTimeout;
	}

	public void setScreenshotForegroundDelay(int screenshotForegroundDelay) {
		this.screenshotForegroundDelay = screenshotForegroundDelay;
	}

}
