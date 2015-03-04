package de.unistuttgart.vis.wearable.os.graph;

import java.util.Date;
import java.util.List;

public class GraphData {
	private String title;
	private Date[] x;
	private double[] y;

	public GraphData(String title, Date[] x, double[] y) {
		assert (x.length == y.length);
		this.title = title;
		this.x = x;
		this.y = y;
	}

	public Date[] getX() {
		return this.x;
	}

	public double[] getY() {
		return this.y;
	}

	public String getTitle() {
		return this.title;
	}
}
