/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

/**
 *
 * @author KyleTrocki
 */
public class txt_to_db {

    protected String pod_sn;
    protected String innoc_date;

    public txt_to_db(database db) throws FileNotFoundException, IOException, SQLException {
        FileReader fr = new FileReader("C:\\Users\\KyleTrocki\\Documents\\Example Data for DB\\pod_2_data_0_2019_10_16.txt");
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        int cnt = 0;
        db.connect();
        while (line != null) {
            if (line.contains("SN:")) {
                pod_sn = line.split("SN:")[1].split(",")[0];
            } else if (line.contains("Inoculation Time:")) {
                innoc_date = line.split("Inoculation Time: ")[1].split(" ")[0].split("-")[1] + line.split("Inoculation Time: ")[1].split(" ")[0].split("-")[2] + line.split("Inoculation Time: ")[1].split(" ")[0].split("-")[0].replaceFirst("20", "");
               // db.new_exp(pod_sn, innoc_date);
            }
            
            if (!line.contains("%")) {
                String[] data = line.split("\t");
                for (int i = 0; i < data.length - 1; i++) {
                  //  System.out.println(i);
                    db.add_bulk_datapoint(db.tables[i], pod_sn, innoc_date, String.valueOf(cnt), String.valueOf(data[i]));
                }
                cnt++;
            }

            line = br.readLine();
        }
        br.close();
        fr.close();
        db.conn.close();
    }
}
