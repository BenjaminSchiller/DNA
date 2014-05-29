package dna.profiler;

import java.util.EnumMap;

import dna.util.Config;

public class ProfilerGranularity {
	public enum Options {
		DISABLED, AGGREGATIONONLY, EACHMETRIC, EACHBATCHGENERATION, EACHUPDATETYPE, EACHBATCH, EACHRUN, EACHSERIES, ALL
	};

	private static EnumMap<Options, Boolean> usedOptions = null;

	public static void init() {
		usedOptions = new EnumMap<>(Options.class);
		for (Options o : Options.values()) {
			usedOptions.put(o, false);
		}

		String key = Config.get("RECOMMENDER_GRANULARITY");
		String[] splitted = key.split(";");
		for (String singleVal : splitted) {
			Options oParsed;
			try {
				oParsed = Options.valueOf(singleVal);
			} catch (IllegalArgumentException e) {
				RuntimeException rt = new RuntimeException(
						"Could not parse profiler granularity option "
								+ singleVal + " from configuration");
				throw rt;
			}
			usedOptions.put(oParsed, true);
		}
	}

	public static boolean isEnabled(Options o) {
		if (usedOptions == null)
			init();
		if (usedOptions.get(Options.DISABLED))
			return false;
		if (usedOptions.get(Options.ALL))
			return true;
		return usedOptions.get(o);
	}

	public static boolean all() {
		return isEnabled(Options.ALL);
	}

	public static boolean disabled() {
		return isEnabled(Options.DISABLED);
	}

}
