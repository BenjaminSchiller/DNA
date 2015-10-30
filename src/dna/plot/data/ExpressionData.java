package dna.plot.data;

import dna.plot.PlotConfig;

/**
 * @author Rwilmes
 * 
 */
public class ExpressionData extends PlotData {

	private String expression;
	private String[] variables;
	private String[] domains;

	public ExpressionData(String exprName, String formular, PlotStyle style,
			String title, String generalDomain, String source) {
		super(exprName, formular, style, title, source);

		// parse variables form formular
		String[] split = formular.split("\\$");
		this.variables = new String[split.length / 2];
		this.domains = new String[variables.length];
		for (int j = 0; j < this.variables.length; j++) {
			String var = split[j * 2 + 1];
			String[] varSplit = var.split(PlotConfig.customPlotDomainDelimiter);
			if (varSplit.length == 2) {
				// if it got domain, take it
				this.domains[j] = varSplit[0];
				this.variables[j] = varSplit[1];
			} else {
				// if not, take statistics as domain
				this.domains[j] = generalDomain;
				this.variables[j] = var;
			}
		}

		// build expression without domains
		this.expression = "";
		for (int i = 0; i < split.length; i++) {
			if ((i & 1) == 0)
				this.expression += split[i];
			else
				this.expression += variables[i / 2];
		}
	}

	@Override
	public boolean isStyleValid() {
		return !this.style.equals(PlotStyle.candlesticks)
				&& !this.style.equals(PlotStyle.yerrorbars);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			String scalingX, String scalingY, boolean noTitle) {
		return this.getEntry(lt, lw, offsetX, offsetY, scalingX, scalingY,
				this.style, noTitle);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			String scalingX, String scalingY,
			DistributionPlotType distPlotType, boolean noTitle) {
		return this.getEntry(lt, lw, offsetX, offsetY, scalingX, scalingY,
				noTitle);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			String scalingX, String scalingY, DistributionPlotType type,
			PlotStyle style, boolean noTitle) {
		// plot style
		PlotStyle styleTemp;
		DistributionPlotType distPlotType;
		if (style == null)
			styleTemp = this.style;
		else
			styleTemp = style;

		if (type == null) {
			if (this.plotAsCdf)
				distPlotType = DistributionPlotType.cdfOnly;
			else
				distPlotType = DistributionPlotType.distOnly;
		} else {
			distPlotType = type;
		}

		// data location
		String dataLoc = null;
		if (super.dataLocation.equals(PlotDataLocation.scriptFile))
			dataLoc = "'-'";
		if (super.dataLocation.equals(PlotDataLocation.dataFile))
			dataLoc = '"' + super.dataPath + '"';

		// data point scaling
		String xpoint = "$1 + ";
		String ypoint = "$2 + ";
		if (!scalingX.equals("null"))
			xpoint = scalingX.replace("x", "$1") + " + ";
		if (!scalingY.equals("null"))
			ypoint = scalingY.replace("y", "$2") + " + ";

		// build stringbuffer
		StringBuffer buff = new StringBuffer();

		// if candlesticks, plot like candlesticks plot
		if (styleTemp.equals(PlotStyle.candlesticks)) {
			String x = "(" + xpoint + offsetX + ")";
			String box_min = "($9 + " + offsetY + ")";
			String whisker_min = "($3 + " + offsetY + ")";
			String whisker_high = "($4 + " + offsetY + ")";
			String box_high = "($10 + " + offsetY + ")";
			String median = "($5 + " + offsetY + ")";

			buff.append(dataLoc + " using " + x + ":" + box_min + ":"
					+ whisker_min + ":" + whisker_high + ":" + box_high
					+ " with candlesticks");
			buff.append(" lt " + lt + " lw " + lw);
			if (noTitle || title == null)
				buff.append(" notitle");
			else
				buff.append(" title \"" + this.title + "\"");
			// buff.append(" whiskerbars, \\\n");
			// buff.append(dataLoc + " using " + x + ":" + median + ":" + median
			// + ":" + median + ":" + median + " with candlesticks");
			// buff.append(" lt " + lt + " lw " + lw + " notitle");
		} else {
			// else: plot normal
			if (distPlotType.equals(DistributionPlotType.cdfOnly))
				buff.append(dataLoc + " using (" + xpoint + offsetX + "):("
						+ ypoint + offsetY + ") smooth cumulative with "
						+ styleTemp);
			else
				buff.append(dataLoc + " using (" + xpoint + offsetX + "):("
						+ ypoint + offsetY + ") with " + styleTemp);
			buff.append(" lt " + lt + " lw " + lw);
			if (noTitle || title == null)
				buff.append(" notitle");
			else
				buff.append(" title \"" + this.title + "\"");
		}
		return buff.toString();
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			String scalingX, String scalingY, PlotStyle style, boolean noTitle) {
		// plot style
		PlotStyle styleTemp;
		if (style == null)
			styleTemp = this.style;
		else
			styleTemp = style;

		// data location
		String dataLoc = null;
		if (super.dataLocation.equals(PlotDataLocation.scriptFile))
			dataLoc = "'-'";
		if (super.dataLocation.equals(PlotDataLocation.dataFile))
			dataLoc = '"' + super.dataPath + '"';

		// data point scaling
		String xpoint = "$1 + ";
		String ypoint = "$2 + ";
		if (!scalingX.equals("null"))
			xpoint = scalingX.replace("x", "$1") + " + ";
		if (!scalingY.equals("null"))
			ypoint = scalingY.replace("y", "$2") + " + ";

		// build stringbuffer
		StringBuffer buff = new StringBuffer();
		buff.append(dataLoc + " using (" + xpoint + offsetX + "):(" + ypoint
				+ offsetY + ") with " + styleTemp);
		buff.append(" lt " + lt + " lw " + lw);
		if (noTitle || title == null)
			buff.append(" notitle");
		else
			buff.append(" title \"" + this.title + "\"");
		return buff.toString();
	}

	public String getExpression() {
		return super.domain;
	}

	public String getExpressionWithoutMarks() {
		return this.expression;
	}

	public String[] getVariables() {
		return variables;
	}

	public String[] getDomains() {
		return domains;
	}
}
