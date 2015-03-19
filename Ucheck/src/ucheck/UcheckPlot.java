package ucheck;

import gp.GpDataset;
import gp.classification.ProbitRegressionPosterior;
import model.Trajectory;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;

public final class UcheckPlot {

	public void plotSmoothedMC(ProbitRegressionPosterior result,
			smoothedMC.Parameter[] params, double beta) {
		final GpDataset input = result.getInputData();
		final int d = input.getDimension();
		final int n = input.getSize();

		final double[] probabilities = result.getClassProbabilities();
		final double[] lowerBoundValues = result.getLowerBound(beta);
		final double[] upperBoundValues = result.getUpperBound(beta);

		if (d == 1) {
			JavaPlot plot = new JavaPlot();
			final double[][] muDat = new double[n][2];
			final double[][] lbDat = new double[n][2];
			final double[][] ubDat = new double[n][2];
			for (int i = 0; i < n; i++) {
				final double x = input.getInstance(i)[0];
				muDat[i][0] = x;
				lbDat[i][0] = x;
				ubDat[i][0] = x;
				muDat[i][1] = probabilities[i];
				lbDat[i][1] = lowerBoundValues[i];
				ubDat[i][1] = upperBoundValues[i];
			}
			final DataSetPlot muPlot = new DataSetPlot(muDat);
			final DataSetPlot lbPlot = new DataSetPlot(lbDat);
			final DataSetPlot ubPlot = new DataSetPlot(ubDat);
			final PlotStyle muStyle = new PlotStyle();
			final PlotStyle lbStyle = new PlotStyle();
			final PlotStyle ubStyle = new PlotStyle();
			muStyle.setStyle(Style.LINES);
			muStyle.set("lw 1.5 lc 1");
			lbStyle.setStyle(Style.LINES);
			lbStyle.set("lt 3 lc -1");
			ubStyle.setStyle(Style.LINES);
			ubStyle.set("lt 3 lc -1");
			muPlot.setPlotStyle(muStyle);
			lbPlot.setPlotStyle(lbStyle);
			ubPlot.setPlotStyle(ubStyle);
			plot.addPlot(muPlot);
			plot.addPlot(lbPlot);
			plot.addPlot(ubPlot);

			muPlot.setTitle("Estimate");
			lbPlot.setTitle("Confidence bounds");
			ubPlot.setTitle("");
			plot.getAxis("x").setLabel(params[0].getName());
			plot.getAxis("y").setLabel("Satisfaction Probability");
			plot.plot();
		}

		if (d == 2) {
			JavaPlot plot = new JavaPlot(true);
			plot.set("pm3d", "map");
			plot.set("size", "square");
			plot.set("palette", "rgbformulae 22,13, -31");
			plot.set("zlabel", "rotate");

			final double[][] muDat = new double[n][3];
			final double[][] lbDat = new double[n][3];
			final double[][] ubDat = new double[n][3];

			for (int i = 0; i < n; i++) {
				final double x1 = input.getInstance(i)[0];
				final double x2 = input.getInstance(i)[1];
				muDat[i][0] = x1;
				lbDat[i][0] = x1;
				ubDat[i][0] = x1;
				muDat[i][1] = x2;
				lbDat[i][1] = x2;
				ubDat[i][1] = x2;
				muDat[i][2] = probabilities[i];
				lbDat[i][2] = lowerBoundValues[i];
				ubDat[i][2] = upperBoundValues[i];
			}

			final PlotStyle style = new PlotStyle();
			style.setStyle(Style.PM3D);
			final DataSetPlot muPlot = new DataSetPlot(muDat);
			muPlot.setPlotStyle(style);
			plot.addPlot(muPlot);

			muPlot.setTitle("");
			plot.getAxis("x").setLabel(params[0].getName());
			plot.getAxis("y").setLabel(params[1].getName());
			plot.getAxis("z").setLabel("Satisfaction Probability");

			plot.plot();
			// System.out.println(); System.out.println(plot.getCommands());
		}
	}

	public void plot(Trajectory x, String... names) {
		JavaPlot plot = new JavaPlot();
		final int vars = x.getValues().length;
		if (names.length == 0) {
			names = new String[vars];
			for (int var = 0; var < vars; var++)
				names[var] = x.getContext().getVariables()[var].getName();
		}
		final int n = x.getTimes().length;
		for (int var = 0; var < vars; var++) {
			final String name = x.getContext().getVariables()[var].getName();
			boolean found = false;
			for (final String currName : names)
				if (name.equals(currName)) {
					found = true;
					break;
				}
			if (!found)
				continue;
			final PointDataSet<Double> set = new PointDataSet<Double>(n);
			for (int i = 0; i < n; i++)
				set.add(new Point<Double>(x.getTimes()[i], x.getValues(var)[i]));
			final PlotStyle style = new PlotStyle();
			style.setStyle(Style.LINES);
			final DataSetPlot currPlot = new DataSetPlot(set);
			currPlot.setPlotStyle(style);
			currPlot.setTitle(name);
			plot.addPlot(currPlot);
		}
		plot.getAxis("x").setLabel("Time");
		plot.getAxis("y").setLabel("Population");
		plot.plot();
	}

}
