/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Erbi-Admin
 */
class graph {

    protected JPanel parent;
    protected ChartPanel cp;
    protected JFreeChart chart;
    protected XYSeriesCollection xyDataset;
    protected boolean visibility = true;
    protected String title;
    protected String y_label;

    graph(JPanel p, XYSeries data_to_graph, String title, String y_label) {
        parent = p;
        this.title = title;
        this.y_label = y_label;
        create_chart(data_to_graph);
    }

    private void create_chart(XYSeries data) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    xyDataset = new XYSeriesCollection();
                    if (data != null) {
                        xyDataset.addSeries(data);
                    }
                    chart = ChartFactory.createXYLineChart(title, "Time", y_label, xyDataset, PlotOrientation.VERTICAL, false, false, false);
                    chart.setBackgroundPaint(null);
                    XYPlot xyPlot = (XYPlot) chart.getPlot();
                    xyPlot.setDomainCrosshairVisible(false);
                    xyPlot.setRangeCrosshairVisible(false);
                    xyPlot.setBackgroundPaint(Color.white);
                    org.jfree.chart.axis.ValueAxis range = xyPlot.getRangeAxis();

                    if (data != null && data.getMinX() < data.getMaxX() && data.getMinY() < data.getMaxY()) {
                        range.setRange(data.getMinY() - 0.1, data.getMaxY() + 0.1);
                        org.jfree.chart.axis.ValueAxis domain = xyPlot.getDomainAxis();
                        domain.setRange(data.getMinX() - 0.1, data.getMaxX() + 0.1);
                    }
                    cp = new ChartPanel(chart) {
                        @Override
                        public Dimension getPreferredSize() {
                            return new Dimension(parent.getWidth(), parent.getHeight());
                        }
                    };
                    cp.setLocation(0, 0);
                    cp.setSize(parent.getWidth(), parent.getHeight());
                    cp.setVisible(visibility);
                    parent.add(cp);
                    parent.revalidate();
                    parent.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void update_data(XYSeries data) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                xyDataset.removeAllSeries();
                xyDataset.addSeries(data);
            }
        });
    }
    protected void add_max_min(XYSeries max_data, XYSeries min_data) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                xyDataset.addSeries(max_data);
                xyDataset.addSeries(min_data);
             cp.restoreAutoRangeBounds();
            }
        });
    }
    

    protected void show_chart() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                visibility = true;
                cp.setVisible(visibility);
            }
        });
    }

    protected void hide_chart() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                visibility = false;
                cp.setVisible(visibility);
            }
        });
    }

    protected void refresh() {
        cp.setLocation(0, 0);
        cp.setSize(parent.getWidth(), parent.getHeight());
    }
}
