package src;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author KyleTrocki
 */
public class COL_DB extends JFrame {

    protected database db;
    protected graph graph;
    protected JComboBox graph_select;

    public COL_DB() throws SQLException, IOException {
       // db = new database();
        show_app();
    }

    public void show_app() throws SQLException {
        setSize(1200, 900);
        setLocation(100, 50);
        setLayout(null);
        setTitle("DB MVP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        JPanel chart = new JPanel();
        chart.setLayout(null);
        add(chart);
        chart.setSize((int) (this.getWidth() * .9), (int) (this.getHeight() * .25));
        chart.setLocation(20, 20);
        chart.setVisible(true);
        db = new database();

        String[][] pods = db.get_runs();
        String[] pod_nums = new String[pods.length];
        String[] pod_inoc = new String[pods.length];
        for (int i = 0; i < pods.length; i++) {
            pod_nums[i] = pods[i][0];
            pod_inoc[i] = pods[i][1];

        }
        JComboBox pod_select = new JComboBox(pod_nums);
        pod_select.setSize((int) (this.getWidth() * .3), (int) (this.getHeight() * .023));
        pod_select.setLocation((int) (this.getWidth() * .5 - pod_select.getWidth() / 2), (int) ((this.getHeight() * .33) + 20 + -3 * pod_select.getHeight()));
        pod_select.setSelectedIndex(0);

        graph_select = new JComboBox(db.tables);
        graph_select.setSize((int) (this.getWidth() * .3), (int) (this.getHeight() * .023));
        graph_select.setLocation((int) (this.getWidth() * .5 - graph_select.getWidth() / 2), (int) ((this.getHeight() * .33) + 20 + -1 * graph_select.getHeight()));
        graph_select.setSelectedIndex(5);
        graph_select.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 long timeBefore = System.currentTimeMillis();
                 if (graph != null) {
                    graph.hide_chart();
                }
                try {
                    XYSeries d = db.get_data(pod_nums[pod_select.getSelectedIndex()], pod_inoc[pod_select.getSelectedIndex()], db.tables[graph_select.getSelectedIndex()]);
                    graph = new graph(chart, db.get_data(pod_nums[pod_select.getSelectedIndex()], pod_inoc[pod_select.getSelectedIndex()], db.tables[graph_select.getSelectedIndex()]), db.tables[graph_select.getSelectedIndex()], "READING");
                 long timeAfter = System.currentTimeMillis();
                    System.out.println(timeAfter - timeBefore);
                } catch (SQLException ex) {
                    Logger.getLogger(COL_DB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        pod_select.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    long timeBefore = System.currentTimeMillis();
                    if (graph != null) {
                        graph.hide_chart();
                    }

                    if (graph_select.getSelectedIndex() >= 0) {
                        XYSeries d = db.get_data(pod_nums[pod_select.getSelectedIndex()], pod_inoc[pod_select.getSelectedIndex()], db.tables[graph_select.getSelectedIndex()]);
         
                        graph = new graph(chart, db.get_data(pod_nums[pod_select.getSelectedIndex()], pod_inoc[pod_select.getSelectedIndex()], db.tables[graph_select.getSelectedIndex()]), db.tables[graph_select.getSelectedIndex()], "READING");
                    }
                    long timeAfter = System.currentTimeMillis();
                    System.out.println(timeAfter - timeBefore);
                } catch (SQLException ex) {
                    Logger.getLogger(COL_DB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        add(pod_select);
        add(graph_select);
        pod_select.setSelectedIndex(0);
         
    }

    public static void main(String[] args) throws SQLException, IOException {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new COL_DB();
                } catch (IOException | SQLException ex) {
                    Logger.getLogger(COL_DB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

}
