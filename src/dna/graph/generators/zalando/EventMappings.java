package dna.graph.generators.zalando;

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
 * unique {@code int}s. The uniqueness holds for all mapped values of
 * all groups.
 * </p>
 */
public class EventMappings {

	private Mapping<String> mapping;
	
	private EventColumn[][] eventColumnGroups;

	/**
	 * @param groups
	 *            All columns that should be mapped must be in {@code groups}. A
	 *            group which column values should be mapped together is an
	 *            element ({@code EventColumn[]}) in {@code groups}.
	 */
	public EventMappings(EventColumn[][] groups) {
		this.mapping = new Mapping<String>();

		this.eventColumnGroups = groups;
	}

	/**
	 * Maps the values of each {@link EventColumn} group for given event to an
	 * <u>unique</u> {@code int} >= 0.
	 * 
	 * @param event
	 *            The {@link Event} to map.
	 */
	public void map(Event event) {
		for (EventColumn[] eventColumnGroup : this.eventColumnGroups)
			this.mapping.map(this
					.eventColumnGroupValue(eventColumnGroup, event));
	}

	/**
	 * Returns the value of given {@link EventColumn} group for given
	 * {@link Event} in the format <i>key1=value1,key2=value2,...</i> For
	 * instance <i>SKU=ah67,MARKE=jx3sz7</i>
	 * 
	 * @return Value of each {@link EventColumn} in given {@link EventColumn}
	 *         group for given {@link Event} concatenated.
	 * 
	 * @see EventMappings_NeuOhnePrefix EventColumn groups
	 */
	private String eventColumnGroupValue(EventColumn[] eventColumnGroup,
			Event event) {
		final StringBuffer sb = new StringBuffer();

		for (EventColumn eventColumn : eventColumnGroup) {
			sb.append(eventColumn);
			sb.append("=");
			sb.append(event.get(eventColumn));
			sb.append(",");
		}

		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * @return The mapped value for given {@link EventColumn} of given
	 *         {@link Event} or <i>-1</i> if any of the given parameters are not
	 *         mapped.
	 * 
	 * @see EventMappings_NeuOhnePrefix Difference between mappings and global mappings
	 * @see #map(Event) Mapping of Events
	 */
	public int getMapping(EventColumn eventColumn, Event event) {
		return this.getMapping(new EventColumn[] { eventColumn }, event);
	}

	/**
	 * @return The mapped value for given {@link EventColumn} group of given
	 *         {@link Event} or <i>-1</i> if any of the given parameters are not
	 *         mapped.
	 * 
	 * @see EventMappings_NeuOhnePrefix EventColumn groups, Difference between mappings and
	 *      global mappings
	 * @see #map(Event) Mapping of Events
	 */
	public int getMapping(EventColumn[] eventColumnGroup, Event event) {
		final int mapping = this.mapping.getMapping(this.eventColumnGroupValue(
				eventColumnGroup, event));
		if (mapping == -1) {
			Log.error("Given event is not mapped yet for given event columns.");
			return -1;
		}
		return mapping;
	}

}
