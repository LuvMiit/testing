package org.niias.genpreds;

import sun.applet.AppletResourceLoader;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GenPreds {

    private static final String userName = "asrb";
    private static final String pass = "asrb";

    private Connection conn;

    private GenPreds() throws SQLException{
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", userName);
        connectionProps.put("password", pass);

        this.conn = DriverManager.getConnection(
                "jdbc:postgresql://192.168.255.178:5432/asrb",
                connectionProps);
    }

    public void gen() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("select pred_id, sname from ic00.h_pred where pred_id in (select distinct h_vertsv2.pred_n_id from ic00.h_vertsv2 where pred_v_id = 2507 and cor_tip <> 'D' and date_kd = '9999-12-31') and date_kd = '9999-12-31'  and gr_id = 7");
        stmt.execute();
        ResultSet rs =  stmt.getResultSet();
        while(rs.next()){
            System.out.print(rs.getInt(1) + "," + processNames(rs.getString(2)));
            int predId = rs.getInt(1);
            int count = levelCounter(predId, 0);
            System.out.println(", " + count);

        }
    }

    public static void main(String[] args) throws SQLException {
        GenPreds preds = new GenPreds();
        preds.gen();
    }

    private String processNames(String srcName){
        return srcName.replaceAll("АО|ОАО|\"|\\s+", "");
    }

    private int levelCounter(int predId, int count) throws SQLException {
        //System.out.println("--" + predId);
        PreparedStatement stmt = conn.prepareStatement("select pred_id from ic00.h_pred where pred_id in (select distinct h_vertsv2.pred_n_id from ic00.h_vertsv2 where pred_v_id = ? and cor_tip <> 'D' and date_kd = '9999-12-31') and date_kd = '9999-12-31';");
        stmt.setInt(1, predId);
        List<Integer> preds = new ArrayList<>();
        stmt.execute();
        ResultSet rs = stmt.getResultSet();
        preds.add(0);
        while (rs.next())
        {
            int id = rs.getInt(1);
            preds.add(1 + levelCounter(id, 0));
        }

        int max = preds.stream().max(Integer::compare).get();
        return max + count;
    }


}
