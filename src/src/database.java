package src;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author KyleTrocki
 */
public class database {

    //DATABASE VARS:
    private final String url = "jdbc:postgresql://127.0.0.1:5433/erbi_col_db";
    private final String user = "postgres";
    private final String password = "Pharyx2018";
    protected Connection conn = null;
    protected String[] tables = {"realtime", "timestamp_lowerbits", "cntrl_reg_lowerbits",
        "od", "od2", "ph", "do_meas", "bot_temp", "top_temp", "midtemp", "seq_ninjvec", "top_drive", "mid_drive", "odmeas",
        "odref", "od2meas", "od2ref", "sensor1_mag", "sensor1_phase", "sensor2_mag", "sensor2_phase", "sensor3_mag", "sensor3_phase",
        "sensor4_mag", "sensor4_phase", "available_ninj1", "mix3_pressure", "co2_duty_cycle", "ninj4", "cntrl_reg2", "pco2", "mix1_pressure",
        "mix2_pressure", "mix_setup_code", "o2_duty_cycle", "seq_ninj1", "seq_ninj2", "seq_ninj3", "seq_chan1", "seq_chan2", "seq_chan3",
        "ph_drive", "sensor4_phase2", "ninj1accum_lowerbits", "ninj2accum_lowerbits", "ninj3accum_lowerbits", "ninj4accum_lowerbits",
        "ninj5accum_lowerbits", "ph_setpoint", "pid_ph_setpoint", "pid2_setpoint", "pid3_setpoint", "piddo_setpoint", "flow_injection_timebase",
        "balance_master_flow_injections_per_timebase", "medium_channel", "feed_flow_injections_per_timebase", "feed_channel",
        "acidbase_flow_injections_per_timebase", "inj_code", "feed2_flow_injections_per_timebase", "feed2_channel", "turbidostat_output_drive",
        "turbidostat_setpoint", "sensor5_mag", "sensor5_phase", "seqninj4", "seq_chan4", "seq_chan5", "feed3_flow_injections_per_timebase",
        "feed3_channel", "feed4_flow_injections_per_timebase", "feed4_channel", "vol_per_inj", "od_scale_gui", "last_storage_address", "pfrac",
        "paccum_lowerbits", "paccum_upperbits", "realtime_upperbits", "time_index_upperbits", "ninjaccum1_upper_bits", "ninjaccum2_upper_bits",
        "ninjaccum3_upper_bits", "ninjaccum4_upper_bits", "ninjaccum5_upper_bits", "inoculation_start_lowerbits", "inoculation_start_upperbits",
        "evap_comp_flow_injections_per_timebase", "evap_comp_channel", "ph_deadband", "res_vac_pressure", "bottle_pressure", "valve_vac_pressure",
        "valve_pressure", "internal_temperature", "mixdelay_num_ext", "ph_phimax", "ninjaccum_flush1_lowerbits", "ninjaccum_flush1_upperbits",
        "ninjaccum_flush2_lowerbits", "ninjaccum_flush2_upperbits", "ninjaccum_flush3_lowerbits", "ninjaccum_flush3_upperbits",
        "ninjaccum_flush4_lowerbits", "ninjaccum_flush4_upperbits", "mix_vent_co2"};
    protected String dataType = "double precision"; // Some of ^ Can be combined if I use double precision since they are floats
    protected float[] test_data = new float[tables.length];
    protected float[] return_data = new float[tables.length];
    protected boolean duplicate = false;
    protected boolean always_add = true; // increment index till add
    protected XYSeries data_to_graph;

    public database() throws SQLException {
        // String test_pod = "238";
        // String test_date = "10142020";
        create_tables();
        //new_exp(test_pod, test_date);
    }

    public void create_tables() throws SQLException {
        connect();
        String createString;
        createString = "CREATE TABLE IF NOT EXISTS runs("
                + "\"POD_SN\" SMALLINT NOT NULL, "
                + "\"INOC_DATE\" INT NOT NULL, "
                + "\"id_pk\" INT PRIMARY KEY"
                + ");";
        conn.prepareStatement(createString).execute();
        for (int i = 0; i < tables.length; i++) {
            createString = "CREATE TABLE IF NOT EXISTS " + tables[i] + "("
                    + "\"index\" INT NOT NULL, "
                    + '"' + tables[i] + "\" " + dataType + " NOT NULL, "
                    + "\"id_fk\" INT NOT NULL, "
                    + "CONSTRAINT id_fk FOREIGN KEY(id_fk) REFERENCES runs(id_pk)"
                    + ");";
            conn.prepareStatement(createString).execute();
        }
        if (!conn.isClosed()) {
            conn.close();
        }
    }

    public void add_datapoint(String table, String pod_sn, String inoc_date, String index, String data) throws SQLException { // data[0] should be index, data[1] reading
        connect();
        String createString = "INSERT INTO " + table + "(\"index\", \"" + table + "\", \"id_fk\") VALUES (" + index + ", " + data + ", " + get_key(pod_sn, inoc_date) + ");";
        // System.out.println(createString);
        conn.prepareStatement(createString).execute();
        if (!conn.isClosed()) {
            conn.close();
        }
    }

    public void add_bulk_datapoint(String table, String pod_sn, String inoc_date, String index, String data) throws SQLException { // data[0] should be index, data[1] reading
        String createString = "INSERT INTO " + table + "(\"index\", \"" + table + "\", \"id_fk\") VALUES (" + index + ", " + data + ", " + get_key(pod_sn, inoc_date) + ");";
        // System.out.println(createString);
        conn.prepareStatement(createString).execute();
    }

    public void new_exp(String pod_sn, String inoc_date) throws SQLException {
        connect();
        String createString = "INSERT INTO runs(\"POD_SN\", \"INOC_DATE\", \"id_pk\") VALUES (" + pod_sn + ", " + inoc_date + ", " + get_key(pod_sn, inoc_date) + ");";
        System.out.println(createString);
        conn.prepareStatement(createString).execute();
        if (!conn.isClosed()) {
            conn.close();
        }
    }

    public String[][] get_runs() throws SQLException {     
        connect();
        String queryString = "SELECT DISTINCT \"POD_SN\", \"INOC_DATE\" FROM runs ORDER BY \"POD_SN\" ASC";
        ResultSet query_result = conn.prepareStatement(queryString).executeQuery();
        ArrayList<Integer> pod_sn = new ArrayList<Integer>();
         ArrayList<Integer> pod_inoc = new ArrayList<Integer>();
        while (query_result.next()) {
            pod_sn.add(query_result.getInt(1));
            pod_inoc.add(query_result.getInt(2));
        }
        String[][] results = new String[pod_sn.size()][2];
        for (int i = 0; i < pod_sn.size(); i++) {
            results[i][0] = String.valueOf(pod_sn.get(i));
            results[i][1] = String.valueOf(pod_inoc.get(i));
        }
        
        if (!conn.isClosed()) {
            conn.close();
        }
        return results;
    }

    public XYSeries get_data(String pod_sn, String inoc_date, String table) throws SQLException {
        connect();
        System.out.println("HERE");
        data_to_graph = new XYSeries("Main Data");
        String queryString = "SELECT * FROM " + table + " WHERE id_fk = " + get_key(pod_sn, inoc_date) + " ORDER BY index ASC;";
        System.out.println(queryString);
        ResultSet query_result = conn.prepareStatement(queryString).executeQuery();
        while (query_result.next()) {
            data_to_graph.add(query_result.getFloat(1), query_result.getFloat(2));
        }
        if (!conn.isClosed()) {
            conn.close();
        }
        return data_to_graph;
    }

    public String get_key(String pod_sn, String inoc_date) {
        return (String.format("%04d", Integer.valueOf(pod_sn)) + String.format("%06d", Integer.valueOf(inoc_date)));
    }

    protected Connection connect() {
        try {
            conn = DriverManager.getConnection(url, user, password);// MUST DOWNLOAD THE JDBC 4.2 JAR!
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
