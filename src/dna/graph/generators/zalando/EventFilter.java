package dna.graph.generators.zalando;

/**
 * A filter for {@link Event}s.
 * 
 * <h1>How to use</h1>
 * <p>
 * Define an default behavior with {@link #EventFilter(boolean)} and adjust it
 * with {@link #addExclusion(EventColumn, Object)}. Use {@link #passes(Event)}
 * to check if an {@link Event} matches the defined criteria.
 * </p>
 */
public class EventFilter {

	/** If this is true, all {@link Event}s passes this filter by default. */
	private boolean permeable;
	/**
	 * All values for the {@link EventColumn}s that possibly annul
	 * {@link #permeable}.
	 */
	private HashSetMap<EventColumn, Object> exclusions;

	/**
	 * Creates an {@link EventFilter} with empty lists for allowed and denied
	 * values.
	 * <p>
	 * Depending on {@code permeable} all or no {@link Event}s pass this filter.
	 * Use {@link #removeExclusion(EventColumn, Object)} and
	 * {@link #addExclusion(EventColumn, Object)} to limit this default setting.
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
	 * Adds the given value to the set of values for given {@link EventColumn}
	 * that possibly annul {@link #permeable}.
	 * 
	 * @param eventColumn
	 *            The {@link EventColumn} to which the given value should be
	 *            added.
	 * @param value
	 *            The value to add for given {@link EventColumn}.
	 */
	void addExclusion(EventColumn eventColumn, Object value) {
		this.exclusions.add(eventColumn, value);
	}

	/**
	 * An {@link Event} passes the filter, if and only if
	 * <ul>
	 * <li>the filter is {@link #permeable} and given event is not covered by
	 * {@link #exclusions} or</li>
	 * <li>the filter is not {@link #permeable} but given event is covered by
	 * {@link #exclusions}</li>
	 * </ul>
	 * An {@link Event} is covered by {@link exclusions} if and only if for each
	 * {@link EventColumn} there is at least one value that equals the value in
	 * this column of the given event.
	 * 
	 * @param event
	 *            The {@link Event} to check.
	 * @return True if given event passes the filter, else false.
	 */
	public boolean passes(Event event) {
		boolean exclusionsExist = exclusions.size() > 0;

		boolean eventCoveredByExclusions = true;
		for (EventColumn c : EventColumn.values()) {
			if (this.exclusions.containsKey(c)) {
				// there is at least one exclusion for EventColumn c, check if
				// value of c in event is one of it
				eventCoveredByExclusions &= this.exclusions
						.containsValueForKey(c, event.get(c));

				if (!eventCoveredByExclusions)
					// EventColumns are conjuncted (every column must match), so
					// break when the first column not covered by exclusions is
					// found
					break;
			}
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
	 * {@link EventColumn} that possibly annul {@link #permeable}.
	 * 
	 * @param eventColumn
	 *            The {@link EventColumn} from which the given value should be
	 *            removed.
	 * @param value
	 *            The value to remove from given {@link EventColumn}.
	 */
	void removeExclusion(EventColumn eventColumn, Object value) {
		this.exclusions.remove(eventColumn, value, true);
	}

}
