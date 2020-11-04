package src;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.jfree.data.xy.XYSeries;
import org.postgresql.util.PSQLException;

/**
 *
 * @author KyleTrocki
 */
public class database {

    //DATABASE VARS:
    private final String url = "jdbc:postgresql://127.0.0.1:5433/DB_MVP";
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
        "ninjaccum_flush4_lowerbits", "ninjaccum_flush4_upperbits", "mix_vent_co2", "float_table"};
    protected String dataType = "float"; // Some of ^ Can be combined if I use double precision since they are floats
    protected float[] test_data = new float[tables.length];
    protected float[] return_data = new float[tables.length];
    protected boolean duplicate = false;
    protected boolean always_add = true; // increment index till add
    protected XYSeries data_to_graph;

    public database() throws SQLException {
        /*connect();
        int[] cols_to_get = {4, 5, 6, 7};
        System.out.println("Start");
        double[][] results_here = getCols_JOIN("22", "102920200120", cols_to_get);
        System.out.println("Done");
*/
        // create_indexes();
        // Change to make is to put the pod SN before the date. This way we have smaller values? May help wiht Data compression
        // create_tables();
        /*     int[] cols_to_get = {4,5,6,7};
        connect();
        System.out.println("Start");

        double[][] results_here = getCols("22", "102920200120", cols_to_get);

        System.out.println("DONE");
        if (!conn.isClosed()) {
            conn.close();
        } */
double[] fake_data = {379798, 55681, 12697, -0.0327, -0.0654, 6.9009, 0.7856, 37.719, 37, 36.977, 8192, 2.6543, 2.9858, 0.5773, 0.5352, 0.3098, 0.2733, 0.4612, -0.4841, 0.1249, -0.5974, 16, -128, 0, 0.0037, 14, 4.0371, 0.2725, 0, 27051, 6.9019, 3.692, 3.568, 272, 0, 0, 0, 0, 0, 4, 2, 0, 0.0037, 451, 0, 0, 5109, 311, 0, 8, 37, 37, 0.5, 20, 1.4004, 1, 1, 0, 0, 2179, 0, 2, 0, 15, 0.191, -0.5234, 2, 0, 1, 0, 0, 0, 0, 712.5, 1, 3776384, 0, 4369, 0, 376900, 337, 0, 0, 0, 0, 0, 32399, 344, 0.085, 1, 0.0498, 2.5117, 3.9414, -1.1367, 15.9297, 38.5187, 32, -0.3477, 180, 0, 0, 0, 180, 0, 530, 0, 4.8643, 5, 234.234};
       connect();
        String inoc_date = "102920200120";
        for (int pod = 85; pod < 150; pod++) {
            new_exp(String.valueOf(pod), inoc_date);
            connect();
            for (int i = 0; i < 45000; i++) {// 1 year of data
                for (int j = 0; j < tables.length; j++) {
                    add_bulk_datapoint2(tables[j], String.valueOf(pod), inoc_date, String.valueOf(i), String.valueOf(fake_data[j] + Math.random()));
                }
            }
        }
        if (!conn.isClosed()) {
            conn.close();
        }
    }

    public double[][] getCols(String pod_sn, String inoc_date, int[] cols_to_get) throws SQLException {
        double[][] result = null;
        for (int i = 0; i < cols_to_get.length; i++) {

            data_to_graph = new XYSeries("Main Data");
            String queryString = "SELECT * FROM " + tables[cols_to_get[i]] + " WHERE id_fk = " + get_key(pod_sn, inoc_date) + " ORDER BY index ASC;";
            ResultSet query_result = conn.prepareStatement(queryString, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery();
            if (i == 0) {
                query_result.last();    // moves cursor to the last row
                result = new double[cols_to_get.length][query_result.getRow()];
                query_result.beforeFirst(); // put cursor back to the front
            }
            int row_count = 0;
            while (query_result.next()) {
                result[i][row_count] = query_result.getDouble(tables[cols_to_get[i]]);
                row_count++;
            }

        }
        return result;
    }

    public double[][] getCols_JOIN(String pod_sn, String inoc_date, int[] cols_to_get) throws SQLException {
        double[][] result = null;
        String queryString = "SELECT ";
        for (int i = 0; i < cols_to_get.length; i++) {
            queryString = queryString.concat(tables[cols_to_get[i]] + "." + tables[cols_to_get[i]]);
            if (i != cols_to_get.length - 1) {
                queryString = queryString.concat(", ");
            } else {
                queryString = queryString.concat(" FROM ");
            }
        }
        for (int i = 0; i < cols_to_get.length; i++) {
            queryString = queryString.concat(tables[cols_to_get[i]]);
            if (i != cols_to_get.length - 1) {
                queryString = queryString.concat(", ");
            } else {
                queryString = queryString.concat(" WHERE ");
            }

        }
        String key = get_key(pod_sn, inoc_date);
        for (int i = 0; i < cols_to_get.length; i++) {
            queryString = queryString.concat(tables[cols_to_get[i]] + "." + "id_fk = " + key);
            if (i != cols_to_get.length - 1) {
                queryString = queryString.concat(", ");
            } else {
                queryString = queryString.concat(" ORDER BY index ASC;");
            }
        }
        System.out.println(queryString);
        ResultSet query_result = conn.prepareStatement(queryString, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery();
        int row_count = 0;
        while (query_result.next()) {
            for (int i = 0; i < cols_to_get.length; i++) {
                result[i][row_count] = query_result.getDouble(i);
            }
            row_count++;
        }
        return result;
    }

    public void create_indexes() throws SQLException {
        if (conn.isClosed()) {
            connect();
        }
        String createString;
        createString = "";
        for (int i = 3; i < tables.length; i++) {
            try {
                createString = "CREATE INDEX IX_tbl_" + tables[i] + " ON " + tables[i] + "(id_fk, index);";
                System.out.println(createString);
                conn.prepareStatement(createString).execute();
            } catch (PSQLException e) {
                continue;
            }//createString = "ALTER TABLE " + tables[i] + " CLUSTER ON INDEX IX_tbl" + tables[i] + ";";
            //System.out.println(createString);
            //conn.prepareStatement(createString).execute();

        }
    }

    public void create_tables() throws SQLException {
        if (conn.isClosed()) {
            connect();
        }
        String createString;
        createString = "CREATE TABLE IF NOT EXISTS runs("
                + "\"POD_SN\" SMALLINT NOT NULL, "
                + "\"INOC_DATE\" BIGINT NOT NULL, "
                + "\"id_pk\" BIGINT PRIMARY KEY"
                + ");";
        conn.prepareStatement(createString).execute();
        for (int i = 0; i < tables.length; i++) {
            createString = "CREATE TABLE IF NOT EXISTS " + tables[i] + "("
                    + "\"index\" INT NOT NULL, "
                    + '"' + tables[i] + "\" " + dataType + " NOT NULL, "
                    + "\"id_fk\" BIGINT NOT NULL, "
                    + "CONSTRAINT id_fk FOREIGN KEY(id_fk) REFERENCES runs(id_pk)"
                    + ");";
            conn.prepareStatement(createString).execute();
        }
    }

    public void add_datapoint(String table, String pod_sn, String inoc_date, String index, String data) throws SQLException { // data[0] should be index, data[1] reading
        if (conn.isClosed()) {
            connect();
        }
        String createString = "INSERT INTO " + table + "(\"index\", \"" + table + "\", \"id_fk\") VALUES (" + index + ", " + data + ", " + get_key(pod_sn, inoc_date) + ");";
        // System.out.println(createString);
        conn.prepareStatement(createString).execute();
    }

    public void add_bulk_datapoint(String table, String pod_sn, String inoc_date, String index, String data) throws SQLException { // data[0] should be index, data[1] reading
        String createString = "INSERT INTO " + table + "(\"index\", \"" + table + "\", \"id_fk\") VALUES (" + index + ", " + data + ", " + get_key(pod_sn, inoc_date) + ");";
        conn.prepareStatement(createString).execute();
    }

    public void add_bulk_datapoint2(String table, String pod_sn, String inoc_date, String index, String data) throws SQLException { // data[0] should be index, data[1] reading
        String createString = "INSERT INTO " + table + "(\"index\", \"" + table + "\", \"id_fk\") VALUES (" + index + ", " + data + ", " + get_key(pod_sn, inoc_date) + ");";
        conn.createStatement().execute(createString);
    }

    public void new_exp(String pod_sn, String inoc_date) throws SQLException {
        if (conn.isClosed()) {
            connect();
        }
        String createString = "INSERT INTO runs(\"POD_SN\", \"INOC_DATE\", \"id_pk\") VALUES (" + pod_sn + ", " + inoc_date + ", " + get_key(pod_sn, inoc_date) + ");";
        System.out.println(createString);
        conn.prepareStatement(createString).execute();
    }

    public String[][] get_runs() throws SQLException {
        if (conn.isClosed()) {
            connect();
        }
        String queryString = "SELECT DISTINCT \"POD_SN\", \"INOC_DATE\" FROM runs ORDER BY \"POD_SN\" ASC";
        ResultSet query_result = conn.prepareStatement(queryString).executeQuery();
        ArrayList<Integer> pod_sn = new ArrayList<Integer>();
        ArrayList<Long> pod_inoc = new ArrayList<Long>();
        while (query_result.next()) {
            pod_sn.add(query_result.getInt(1));
            pod_inoc.add(query_result.getLong(2));
        }
        String[][] results = new String[pod_sn.size()][2];
        for (int i = 0; i < pod_sn.size(); i++) {
            results[i][0] = String.valueOf(pod_sn.get(i));
            results[i][1] = String.valueOf(pod_inoc.get(i));
        }
        return results;
    }

    public XYSeries get_data(String pod_sn, String inoc_date, String table) throws SQLException {
        if (conn.isClosed()) {
            connect();
        }
        data_to_graph = new XYSeries("Main Data");
        String queryString = "SELECT " + table + " FROM " + table + " WHERE id_fk = " + get_key(pod_sn, inoc_date) + " ORDER BY index ASC;";
        //System.out.println(queryString);
        ResultSet query_result = conn.prepareStatement(queryString).executeQuery();
        int count = 0;
        while (query_result.next()) {
            data_to_graph.add(count, query_result.getFloat(1));
            count++;
        }
        return data_to_graph;

    }

    public String get_key(String pod_sn, String inoc_date) {
        return (String.format("%010d", Long.valueOf(inoc_date)) + String.format("%04d", Integer.valueOf(pod_sn))); // UP to 10k pods, innoc time is DDMMYYHHmm 
    }

    protected Connection connect() {
        try {
            conn = DriverManager.getConnection(url, user, password);// MUST DOWNLOAD THE JDBC 4.2 JAR!
            System.out.println("Connection SUCCESS " + !conn.isClosed());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
