package dna.graph.generators.zalando.parser;

import dna.graph.generators.zalando.data.EventColumn;

public class DefaultEventFilter extends EventFilter {

	public DefaultEventFilter() {

		// despite some exclusions, use all events
		super(true);

		// actions

		this.addExclusion(EventColumn.ACTION, "VIEW");
		this.addExclusion(EventColumn.ACTION, "CART");
		this.addExclusion(EventColumn.ACTION, "WISH");
		this.addExclusion(EventColumn.ACTION, "DELETE_FROM_CART");
		this.addExclusion(EventColumn.ACTION, "DELETE_FROM_WISHLIST");
		
		this.addExclusion(EventColumn.ACTION, "RECOPDS"); // no user triggered event
		this.addExclusion(EventColumn.ACTION, "VIEWRECO"); // no user triggered event

		// commodity groups

		this.addExclusion(EventColumn.COMMODITYGROUP2, "Markierung");
		this.addExclusion(EventColumn.COMMODITYGROUP2, "Altdatenmigration");

		this.addExclusion(EventColumn.COMMODITYGROUP3,
				"Artikel ohne Bewegungen");
		this.addExclusion(EventColumn.COMMODITYGROUP3, "gelöschte Artikel");
		this.addExclusion(EventColumn.COMMODITYGROUP3, "Betrugs-Honeypot");
		this.addExclusion(EventColumn.COMMODITYGROUP3, "Löschliste");

		this.addExclusion(EventColumn.COMMODITYGROUP4,
				"Artikel ohne Bewegungen");
		this.addExclusion(EventColumn.COMMODITYGROUP4, "gelöschte Artikel");
		this.addExclusion(EventColumn.COMMODITYGROUP4, "Betrugs-Honeypot");

		// products

		/*
		 * FIXME should be only 100% correct for events where product_ids
		 * contain exactly one id (not true for RECOPDS, compare other FIXMEs);
		 * otherwise ElementColumn.PRODUCTIDSmust be used and arrays compared
		 */
		this.addExclusion(EventColumn.PRODUCTID, 2007281);
		this.addExclusion(EventColumn.PRODUCTID, 2932423);
		this.addExclusion(EventColumn.PRODUCTID, 3044541);
		this.addExclusion(EventColumn.PRODUCTID, 1544141);

	}
}
