package dna.plot.data;

import dna.plot.Gnuplot.PlotStyle;


/**
 * @author benni
 * 
 */
public class MinimumData extends PlotData {

	public MinimumData(String data, PlotStyle style, String title) {
		super(data, style, title);
	}

	@Override
	public boolean isStyleValid() {
		return !this.style.equals(PlotStyle.candlesticks)
				&& !this.style.equals(PlotStyle.yerrorbars);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY) {
		StringBuffer buff = new StringBuffer();
		buff.append("'" + this.data + "' using ($1 + " + offsetX + "):($3 + "
				+ offsetY + ") with " + this.style);
		buff.append(" lt " + lt + " lw " + lw);
		buff.append(title == null ? " notitle" : " title \"" + this.title
				+ "\"");
		return buff.toString();
	}

}
