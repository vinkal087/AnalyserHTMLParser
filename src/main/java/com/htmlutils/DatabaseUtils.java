package com.htmlutils;

/**
 * Created by vvishnoi on 2/4/17.
 */

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.ResourceBundle;


public class DatabaseUtils {
    final static Logger logger = Logger.getLogger(DatabaseUtils.class);


    public static void insertToDB(List<String> queries) throws Exception{
        ResourceBundle bundle = ResourceBundle.getBundle("database");
        Statement stmt=null;
        Connection con=null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@" + bundle.getString("DATABASE_HOST") + ":" + bundle.getString("DATABASE_PORT") +
                            ":" + bundle.getString("DATABASE_NAME"), bundle.getString("DATABASE_USER"),
                    bundle.getString("DATABASE_PASSWORD"));
            stmt = con.createStatement();
            logger.info("Executing Queries");
            for (int i = 0; i < queries.size(); i++) {
                String query = queries.get(i);
                try {
                    stmt.executeUpdate(query);
                }
                catch (Exception e){

                }
            }
            con.commit();
        }
        finally {

            if (stmt != null) {
                stmt.close();
            }

            if (con != null) {
                con.close();
            }

        }


    }
}
