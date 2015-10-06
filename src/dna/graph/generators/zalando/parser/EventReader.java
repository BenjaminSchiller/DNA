package dna.graph.generators.zalando.parser;

import java.util.HashMap;
import java.util.Map;

import dna.graph.generators.zalando.data.Event;
import dna.graph.generators.zalando.data.LogEntry;
import dna.graph.generators.zalando.data.Product;
import dna.util.Log;

public class EventReader {

	Map<Integer, Product> products;

	private RawFileReader readerLog;

	public EventReader(String pathProducts, boolean isGzippedProducts,
			String pathLog, boolean isGzippedLog) {
		this.products = new HashMap<Integer, Product>();
		Log.info("In order to read the log, products must be parsed first. This is done now.");
		this.readProducts(pathProducts, isGzippedProducts);
		Log.info("Products parsed.");

		this.readerLog = new RawFileReader(pathLog, isGzippedLog);
	}

	public Event readNext() {
		final String line = this.readerLog.readLine();

		if (line != null) {
			try {
				final LogEntry l = new LogEntry(line);
				/*
				 * FIXME the following statements assumes that event covers ONLY
				 * ONE product (which is not true for RECOPDS)
				 */
				final Product p = this.products.get(l.getProduct_ids()[0]);
				if (p == null) {
					Log.error("User event concerns unknown product. Hence it is ommitted and reader tries next one.");
					return this.readNext();
				} else {
					return new Event(p, l);
				}
			} catch (Exception e) {
				Log.error("Failure while creating Event with line of current data. Line ommitted and jumped to the next.");
				return this.readNext();
			}
		}

		return null;
	}

	private void readProducts(String path, boolean isGzipped) {
		final RawFileReader readerProducts = new RawFileReader(path, isGzipped);
		String line = null;
		while ((line = readerProducts.readLine()) != null) {
			try {
				final Product p = new Product(line);
				this.products.put(p.getProductID(), p);
			} catch (Exception e) {
				Log.warn("Failure while creating Product with line of current data. Line ommitted and jumped to the next.");
			}
		}
	}

}
