package dna.graph.generators.zalando;

import java.util.HashMap;
import java.util.Map;

import dna.util.Log;

/**
 * Maps the string values of {@link Event}s to {@code int}s.
 * 
 * <h1>About EventColumn Groups</h1>
 * <p>
 * {@link EventColumn} groups are explicitly defined via
 * {@link #EventMappings(EventColumn[][])}. {@link #EventMappings()} implicitly
 * creates groups, each containing a single {@link EventColumn}.
 * </p>
 * <p>
 * The value of an {@link EventColumn} group is built by concatenating
 * (<i>"a"</i> + <i>"b"</i> -> <i>"ab"</i>) the value of each
 * {@link EventColumn} in the group.
 * </p>
 * 
 * <h1>About Mappings and Global Mappings</h1>
 * <p>
 * This class uses {@link Mapping}s for each {@link EventColumn} group.
 * Therefore each value of these groups for all {@link Event}s are mapped to
 * unique {@code int}s. But the uniqueness only holds for all mapped values of
 * one group, not for all groups together.
 * </p>
 * <p>
 * Use {@link #getGlobalMapping(EventColumn, Event)} and
 * {@link #getGlobalMapping(EventColumn[], Event)} instead of
 * {@link #getMapping(EventColumn, Event)} and
 * {@link #getMapping(EventColumn[], Event)} to get mapped values that are
 * unique for all {@link EventColumns}s and {@link Event}s. These globally
 * unique {@code int}s have an prefix for each {@link EventColumn} group
 * followed by the mapped value.
 * </p>
 */
public class EventMappings {

	// for calculating global prefixes

	/**
	 * {@link Mapping} of all {@link EventColumn} groups. Used by
	 * {@link #getGlobalMapping(EventColumn, Event)} and
	 * {@link #getGlobalMapping(EventColumn[], Event)} to calculate unique
	 * prefixes.
	 */
	private Mapping<EventColumn[]> eventColumnGroupMap;
	/**
	 * Base for prefix used by {@link #getGlobalMapping(EventColumn, Event)} and
	 * {@link #getGlobalMapping(EventColumn[], Event)} to ensure prefixes with
	 * same number of digits:<br>
	 * {@link #globalPrefixBase} <= prefix < {@link #globalPrefixBase} * 10.
	 */
	private int globalPrefixBase;

	// mappings

	/** {@link Mapping}s for the {@link EventColumn} groups. */
	private Map<EventColumn[], Mapping<String>> eventColumnGroupValueMaps;

	/**
	 * {@link EventMappings} where the values of each column are mapped
	 * individually.
	 */
	public EventMappings() {
		this.eventColumnGroupMap = new Mapping<EventColumn[]>();

		this.eventColumnGroupValueMaps = new HashMap<EventColumn[], Mapping<String>>();

		for (EventColumn eventColumn : EventColumn.values()) {
			this.eventColumnGroupMap.map(new EventColumn[] { eventColumn });

			this.eventColumnGroupValueMaps.put(
					new EventColumn[] { eventColumn }, new Mapping<String>());
		}

		this.calculateGlobalPrefixBase();
	}

	/**
	 * {@link EventMappings} where the values of given {@link EventColumn}
	 * groups are mapped individually.
	 * 
	 * @param groups
	 *            All columns that should be mapped must be in {@code groups}. A
	 *            group which column values should be mapped together is an
	 *            element ({@code EventColumn[]}) in {@code groups}.
	 */
	public EventMappings(EventColumn[][] groups) {
		this.eventColumnGroupMap = new Mapping<EventColumn[]>();

		this.eventColumnGroupValueMaps = new HashMap<EventColumn[], Mapping<String>>();

		for (EventColumn[] eventColumnGroup : groups) {
			this.eventColumnGroupMap.map(eventColumnGroup);

			this.eventColumnGroupValueMaps.put(eventColumnGroup,
					new Mapping<String>());
		}

		this.calculateGlobalPrefixBase();
	}

	/**
	 * Calculates {@link #globalPrefixBase} depending on the number of
	 * {@link EventColumn} groups, i.e. the number of prefixes needed for
	 * globally unique mappings.
	 */
	private void calculateGlobalPrefixBase() {
		this.globalPrefixBase = 1;
		while (this.eventColumnGroupMap.size() > this.globalPrefixBase * 9)
			this.globalPrefixBase *= 10;
		this.globalPrefixBase--;
	}

	/**
	 * Returns the value of given {@link EventColumn} group for given
	 * {@link Event}.
	 * 
	 * @return Value of each {@link EventColumn} in given {@link EventColumn}
	 *         group for given {@link Event} concatenated.
	 * 
	 * @see EventMappings EventColumn groups
	 */
	private String eventColumnGroupValue(EventColumn[] eventColumnGroup,
			Event event) {
		String eventColumnGroupValue = "";

		for (EventColumn eventColumn : eventColumnGroup)
			eventColumnGroupValue += event.get(eventColumn);

		return eventColumnGroupValue;
	}

	/**
	 * @return The mapped value for given {@link EventColumn} followed by its
	 *         value of given {@link Event}, e.g. <i>513</i> for a <i>5</i>
	 *         followed by a <i>13</i> or <i>-1</i> if any of the given
	 *         parameters are not mapped.
	 * 
	 * @see EventMappings Difference between mappings and global mappings
	 * @see #map(Event) Mapping of Events
	 */
	int getGlobalMapping(EventColumn eventColumn, Event event) {
		return this.getGlobalMapping(new EventColumn[] { eventColumn }, event);
	}

	/**
	 * @return The mapped value for given {@link EventColumn} group followed by
	 *         its value of given {@link Event}, e.g. <i>513</i> for a <i>5</i>
	 *         followed by a <i>13</i> or <i>-1</i> if any of the given
	 *         parameters are not mapped.
	 * 
	 * @see EventMappings EventColumn groups, Difference between mappings and
	 *      global mappings
	 * @see #map(Event) Mapping of Events
	 */
	public int getGlobalMapping(EventColumn[] eventColumnGroup, Event event) {
		if (!this.eventColumnGroupMap.contains(eventColumnGroup)) {
			Log.error("Given event columns are not mapped yet.");
			return -1;
		}
		final int prefix = this.globalPrefixBase
				+ this.eventColumnGroupMap.getMapping(eventColumnGroup) + 1;

		final int postfix = this.getMapping(eventColumnGroup, event);
		if (postfix == -1) {
			Log.error("Given event is not mapped yet for given event columns.");
			return -1;
		}

		return Integer.parseInt(String.valueOf(prefix) + ""
				+ String.valueOf(postfix));
	}

	/**
	 * @return The mapped value for given {@link EventColumn} of given
	 *         {@link Event} or <i>-1</i> if any of the given parameters are not
	 *         mapped.
	 * 
	 * @see EventMappings Difference between mappings and global mappings
	 * @see #map(Event) Mapping of Events
	 */
	int getMapping(EventColumn eventColumn, Event event) {
		return this.getMapping(new EventColumn[] { eventColumn }, event);
	}

	/**
	 * @return The mapped value for given {@link EventColumn} group of given
	 *         {@link Event} or <i>-1</i> if any of the given parameters are not
	 *         mapped.
	 * 
	 * @see EventMappings EventColumn groups, Difference between mappings and
	 *      global mappings
	 * @see #map(Event) Mapping of Events
	 */
	int getMapping(EventColumn[] eventColumnGroup, Event event) {
		if (!this.eventColumnGroupValueMaps.containsKey(eventColumnGroup)) {
			Log.error("Given event columns are not mapped yet.");
			return -1;
		}
		final Mapping<String> mappings = this.eventColumnGroupValueMaps
				.get(eventColumnGroup);

		final int mapping = mappings.getMapping(eventColumnGroupValue(
				eventColumnGroup, event));
		if (mapping == -1) {
			Log.error("Given event is not mapped yet for given event columns.");
			return -1;
		}

		return mapping;
	}

	/**
	 * @param globalMapping1
	 *            Any global mapping, mapped by this {@link EventMappings}.
	 * @param globalMapping2
	 *            Any global mapping, mapped by this {@link EventMappings}.
	 * @return True if the prefix of both global mappings is equal, else false.
	 */
	public boolean globalMappingPrefixIsEqual(int globalMapping1,
			int globalMapping2) {
		final String globalMapping1String = String.valueOf(globalMapping1);
		final String globalMapping2String = String.valueOf(globalMapping2);

		final int len = this.globalPrefixBase == 0 ? 1 : (int) (Math
				.log10(this.globalPrefixBase) + 1);

		return globalMapping1String.regionMatches(0, globalMapping2String, 0,
				len);
	}

	/**
	 * Maps the values of each {@link EventColumn} group for given event to an
	 * <u>unique</u> {@code int} >= 0.
	 * 
	 * @param event
	 *            The {@link Event} to map.
	 * 
	 * @see EventMappings EventColumn groups
	 */
	public void map(Event event) {
		for (EventColumn[] eventColumnGroup : this.eventColumnGroupValueMaps
				.keySet())
			this.eventColumnGroupValueMaps.get(eventColumnGroup).map(
					this.eventColumnGroupValue(eventColumnGroup, event));
	}

	/**
	 * Prints (via {@code System.out.println(...)}) the event column groups
	 * mapped with the number of mapped values for the specific group.
	 */
	public void printStatistic() {
		for (EventColumn[] eventColumnGroup : this.eventColumnGroupValueMaps
				.keySet()) {
			for (EventColumn eventColumn : eventColumnGroup) {
				System.out.print(eventColumn + " ");
			}
			System.out.print(": ");
			System.out.println(this.eventColumnGroupValueMaps.get(
					eventColumnGroup).size());
		}
	}

}
