package dna.graph.generators.zalando;

/**
 * All columns of an {@link Event}.
 */
public enum EventColumn {

	SKU, FAMILY_SKU, MARKE, FARBE, WARENGRUPPE_1, WARENGRUPPE_2, WARENGRUPPE_3, WARENGRUPPE_4, WARENGRUPPE_5, PERMANENT_COOKIE_ID, SESSION_ID, AKTION, ANZAHL, PREISLAGE;

	/**
	 * All possible values for column {@link #AKTION}.
	 */
	enum Aktionen {
		VIEW, CART, SALE, WISH, VIEWRECO, DELETE_FROM_WISHLIST, DELETE_FROM_CART
	}

	/**
	 * This method allows to easily access {@link #WARENGRUPPE_1} to
	 * {@link #WARENGRUPPE_5} in iterations like for-loops.
	 * 
	 * @param i
	 *            The index of the Warengruppe to return.
	 * @return {@code WARENGRUPPE_}<i>i+1</i> or {@code null} if given index is
	 *         < 0 or > 4
	 */
	static EventColumn Warengruppe(int i) {
		switch (i) {
		case 0:
			return EventColumn.WARENGRUPPE_1;
		case 1:
			return EventColumn.WARENGRUPPE_2;
		case 2:
			return EventColumn.WARENGRUPPE_3;
		case 3:
			return EventColumn.WARENGRUPPE_4;
		case 4:
			return EventColumn.WARENGRUPPE_5;
		default:
			return null;
		}
	}

}
