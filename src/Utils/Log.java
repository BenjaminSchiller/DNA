package Utils;

public class Log {
	public static enum LogLevel {
		error, warn, info, debug
	};

	private static LogLevel logLevel = LogLevel.info;

	public static void setLogLevel(LogLevel logLevel) {
		Log.logLevel = logLevel;
	}

	public static void error(String msg) {
		System.err.println("error: " + msg.replace("\n", "\n error: "));
	}

	public static void warn(String msg) {
		if (logLevel == LogLevel.warn || logLevel == LogLevel.info
				|| logLevel == LogLevel.debug) {
			print(msg, "warn: ");
		}
	}

	public static void info(String msg) {
		if (logLevel == LogLevel.info || logLevel == LogLevel.debug) {
			print(msg, "~ ");
		}
	}

	public static void infoSep() {
		if (logLevel == LogLevel.info || logLevel == LogLevel.debug) {
			print("~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~", "");
		}
	}

	public static void debug(String msg) {
		if (logLevel == LogLevel.debug) {
			print(msg, "debug: ");
		}
	}

	protected static void print(String msg, String pre) {
		System.out.println(pre + msg.replace("\n", "\n" + pre));
	}
}
