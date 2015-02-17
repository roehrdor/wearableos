package de.unistuttgart.vis.wearable.os.graph;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import de.unistuttgart.vis.wearable.os.api.PSensor;
import de.unistuttgart.vis.wearable.os.sensors.SensorData;

public class GraphRenderer {
	public static final int UPDATE_INTERVAL = 100;

	private interface GraphDataGenerator {
		public GraphData[] getData();
	}

	public static class ChartThreadTuple {
		private Thread thread;
		private View chart;

		public ChartThreadTuple(Thread thread, View chart) {

			this.thread = thread;
			this.chart = chart;
		}

		public View getChart() {
			return chart;
		}

		public Thread getThread() {
			return thread;
		}
	}

	private GraphRenderer() {
	}

	private static float legendTextSize = 18.0f;
	private static float labelTextSize = 20.0f;
	private static int backgroundColor = 0x444444;
	private static float lineWidth = 3.0f;

	private static ChartThreadTuple createView(Context context, String title,
			GraphType type, final GraphDataGenerator generator) {
		// List for multiple data and renderer
		final List<TimeSeries> series = new ArrayList<TimeSeries>();
		List<XYSeriesRenderer> renderList = new ArrayList<XYSeriesRenderer>();

		// cache for the latest time series
		TimeSeries latest;
		XYSeriesRenderer rendererCache;

		// iterate through all passed data
		for (GraphData d : generator.getData()) {
			// cache x and y axis
			double[] y;
			Date[] x;
			x = d.getX();
			y = d.getY();
			// Create new Timeseries
			series.add(latest = new TimeSeries(d.getTitle()));
			// Add values to time series
			for (int i = 0; i < x.length; ++i) {
				latest.add(x[i], y[i]);
			}
		}

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		int dimension = 0;
		for (TimeSeries s : series) {
			dataset.addSeries(s);
			renderList.add(rendererCache = new XYSeriesRenderer());
			rendererCache.setFillPoints(true);
			rendererCache.setLineWidth(lineWidth);
			mRenderer.addSeriesRenderer(rendererCache);

			rendererCache.setColor(getColor(dimension++));
			rendererCache.setDisplayBoundingPoints(true);
			rendererCache.setPointStyle(PointStyle.CIRCLE);
		}
		mRenderer.setLabelsTextSize(labelTextSize);
		mRenderer.setLegendTextSize(legendTextSize);
		mRenderer.setBackgroundColor(backgroundColor);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setInScroll(false);
		mRenderer.setPanEnabled(false, false);
		mRenderer.setZoomEnabled(false, false);
		mRenderer.setYLabelsPadding(18);
		mRenderer.setXTitle("time");
		mRenderer.setShowGrid(true);

		final GraphicalView view;
		switch (type) {
		case BAR:
			view = ChartFactory.getBarChartView(context, dataset, mRenderer,
					Type.DEFAULT);
			break;
		case CUBIC:
			view = ChartFactory.getCubeLineChartView(context, dataset,
					mRenderer, 1.0f);
			break;
		case LINE:
		default:
			view = ChartFactory.getTimeChartView(context, dataset, mRenderer,
					"HH:mm:ss");
		}

		Thread thread = new Thread() {
			@Override
			public void run() {

				while (!isInterrupted()) {

					try {
						Thread.sleep(UPDATE_INTERVAL);
					} catch (InterruptedException e) {
					}

					Iterator<TimeSeries> iterator = series.iterator();
					for (GraphData d : generator.getData()) {
						TimeSeries currentTimeSeries = iterator.next();

						currentTimeSeries.clear();

						Date[] x = d.getX();
						double[] y = d.getY();

						for (int i = 0; i < x.length; ++i) {
							currentTimeSeries.add(x[i], y[i]);
						}
					}

					view.repaint();

				}
			};
		};

		return new ChartThreadTuple(thread, view);
	}

	public static ChartThreadTuple createGraph(final PSensor sensor,
			Context context) {

		GraphDataGenerator generator = new GraphDataGenerator() {

			@Override
			public GraphData[] getData() {
				Vector<SensorData> data = sensor.getRawData();

				int numberOfDimensions = sensor.getSensorType().getDimension();

				final GraphData[] graphs = new GraphData[numberOfDimensions];
				for (int dimension = 0; dimension < numberOfDimensions; dimension++) {
					double[] values = new double[data.size()];
					Date[] dates = new Date[data.size()];
					for (int index = 0; index < data.size(); index++) {
						values[index] = data.get(index).getData()[dimension];
						dates[index] = data.get(index).getDate();
					}
					GraphData gd = new GraphData("Dimension: "
							+ (int) (dimension + 1), dates, values);
					graphs[dimension] = gd;
				}

				return graphs;
			}
		};

		return createView(context, "Samples", sensor.getGraphType(), generator);
	}

	private static int getColor(int dimension) {
		int color = 0;
		switch (dimension) {
		case 0:
			color = Color.BLUE;
			break;
		case 1:
			color = Color.GREEN;
			break;
		case 2:
			color = Color.RED;
			break;
		case 3:
			color = Color.YELLOW;
			break;
		case 4:
			color = Color.CYAN;
			break;
		case 5:
			color = Color.MAGENTA;
			break;
		case 6:
			color = Color.LTGRAY;
			break;
		case 7:
			color = Color.BLACK;
			break;
		case 8:
			color = Color.DKGRAY;
			break;
		default:
			color = Color.GRAY;
			break;
		}
		return color;
	}

}
