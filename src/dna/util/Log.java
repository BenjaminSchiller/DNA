package dna.util;

public class Log {
	// output prefixes
	public static final String debugPrefix = "debug: ";
	public static final String warningPrefix = "warn: ";
	public static final String errorPrefix = "error: ";
	public static final String infoPrefix = "~";

	// log levels
	public static enum LogLevel {
		error, warn, info, debug
	};

	private static LogLevel logLevel = LogLevel.info;

	public static void setLogLevel(LogLevel logLevel) {
		Log.logLevel = logLevel;
	}

	public static void error(String msg) {
		System.err.println(Log.errorPrefix + msg.replace("\n", "\n error: "));
	}

	public static void warn(String msg) {
		if (logLevel == LogLevel.warn || logLevel == LogLevel.info
				|| logLevel == LogLevel.debug) {
			print(msg, Log.warningPrefix);
		}
	}

	public static void info(String msg) {
		if (logLevel == LogLevel.info || logLevel == LogLevel.debug) {
			print(msg, Log.infoPrefix + " ");
		}
	}

	public static void infoSep() {
		if (logLevel == LogLevel.info || logLevel == LogLevel.debug) {
			print("~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~", "");
		}
	}

	public static void debug(String msg) {
		if (logLevel == LogLevel.debug) {
			print(msg, Log.debugPrefix);
		}
	}

	protected static void print(String msg, String pre) {
		System.out.println(pre + msg.replace("\n", "\n" + pre));
	}
}
