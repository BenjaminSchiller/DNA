package dna.graph.generators.zalando.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import dna.graph.generators.zalando.HashSetMap;
import dna.graph.generators.zalando.data.Event;
import dna.graph.generators.zalando.data.EventColumn;
import dna.util.Log;

/**
 * A filter for {@link Old_Event}s.
 * 
 * <h1>How to use</h1>
 * <p>
 * Define an default behavior with {@link #EventFilter(boolean)} and adjust it
 * with {@link #addExclusion(Old_EventColumn, Object)}. Use
 * {@link #passes(Old_Event)} to check if an {@link Old_Event} matches the
 * defined criteria.
 * </p>
 */
public class EventFilter {

	/** If this is true, all {@link Old_Event}s passes this filter by default. */
	private boolean permeable;
	/**
	 * All values for the {@link Old_EventColumn}s that possibly annul
	 * {@link #permeable}.
	 */
	private HashSetMap<EventColumn, Object> exclusions;

	/**
	 * Creates an {@link EventFilter} with empty lists for allowed and denied
	 * values.
	 * <p>
	 * Depending on {@code permeable} all or no {@link Old_Event}s pass this
	 * filter. Use {@link #removeExclusion(Old_EventColumn, Object)} and
	 * {@link #addExclusion(Old_EventColumn, Object)} to limit this default
	 * setting.
	 * </p>
	 * 
	 * @param permeable
	 *            If this is true, all events pass this filter by default. If
	 *            this is false, no event passes this filter by default.
	 */
	EventFilter(boolean permeable) {
		this.permeable = permeable;
		this.exclusions = new HashSetMap<EventColumn, Object>();
	}

	/**
	 * Adds the given value to the set of values for given
	 * {@link Old_EventColumn} that possibly annul {@link #permeable}.
	 * 
	 * @param eventColumn
	 *            The {@link Old_EventColumn} to which the given value should be
	 *            added.
	 * @param value
	 *            The value to add for given {@link Old_EventColumn}.
	 */
	void addExclusion(EventColumn eventColumn, Object value) {
		this.exclusions.add(eventColumn, value);
	}

	/**
	 * An {@link Old_Event} passes the filter, if and only if
	 * <ul>
	 * <li>the filter is {@link #permeable} and given event is not covered by
	 * {@link #exclusions} or</li>
	 * <li>the filter is not {@link #permeable} but given event is covered by
	 * {@link #exclusions}</li>
	 * </ul>
	 * An {@link Old_Event} is covered by {@link exclusions} if for at least one
	 * {@link Old_EventColumn} there is at least one value that equals the value
	 * in this column of the given event.
	 * 
	 * @param event
	 *            The {@link Old_Event} to check.
	 * @return True if given event passes the filter, else false.
	 */
	public boolean passes(Event event) {
		boolean exclusionsExist = exclusions.size() > 0;

		boolean eventCoveredByExclusions = false;
		for (EventColumn c : this.exclusions.keys()) {
			// there is at least one exclusion for EventColumn c, check if
			// value of c in event is one of it
			eventCoveredByExclusions |= this.exclusions.containsValueForKey(c,
					event.get(c));

			if (eventCoveredByExclusions)
				break;
		}

		if (this.permeable) {
			if (exclusionsExist)
				return !eventCoveredByExclusions;
			else
				return true;
		} else {
			if (exclusionsExist)
				return eventCoveredByExclusions;
			else
				return false;
		}
	}

	/**
	 * Removes the given value from the set of values for given
	 * {@link Old_EventColumn} that possibly annul {@link #permeable}.
	 * 
	 * @param eventColumn
	 *            The {@link Old_EventColumn} from which the given value should
	 *            be removed.
	 * @param value
	 *            The value to remove from given {@link Old_EventColumn}.
	 */
	void removeExclusion(EventColumn eventColumn, Object value) {
		this.exclusions.remove(eventColumn, value, true);
	}

	// TODOD Doku
	public static EventFilter fromFile(String properties) {
		Properties p = new Properties();
		FileInputStream fis;
		try {
			fis = new FileInputStream(properties);
			p.load(fis);
		} catch (IOException e) {
			Log.error("Failure while loading filter properties. "
					+ "Returning null (results in usage of unfiltered data).");
			return null;
		}

		final boolean permeable = Boolean.valueOf(p.getProperty("permeable"));

		final EventFilter f = new EventFilter(permeable);

		final String[] exclusions = p.getProperty("exclusions").split(";");
		for (String e : exclusions) {
			String[] e_ = e.split(",");
			f.addExclusion(EventColumn.valueOf(e_[0]), e_[1]);
		}

		return f;
	}

}
