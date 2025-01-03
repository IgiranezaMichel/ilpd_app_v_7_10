package models;


import java.sql.*;
import java.util.*;

/**
 * A Java MySQL SELECT statement example.
 * Demonstrates the use of a SQL SELECT statement against a
 * MySQL database, called from a Java program.
 *
 * Created by Alvin Alexander, http://alvinalexander.com
 */
public class RunQuery {
    public static Connection conn = null;
    public static String query_given = "";
    public RunQuery(String query_parameter) throws ClassNotFoundException, SQLException {
        String myDriver = "org.gjt.mm.mysql.Driver";
        String myUrl = "jdbc:mysql://localhost/university";
        Class.forName(myDriver);
        conn = DriverManager.getConnection(myUrl, "root", "");
        query_given = query_parameter;
    }
    public static ResultSet exec() throws ClassNotFoundException, SQLException {
        // create our mysql database connection


            // our SQL SELECT query.
            // if you only need a few columns, specify them by name instead of using "*"
            //String query = query_given;

            // create the java statement
            Statement st = conn.createStatement();

            // execute the query, and get a java resultset
            ResultSet rs = st.executeQuery(query_given);

            // iterate through the java resultset

        return  rs;
    }
    public static List getAll(ResultSet rSet) throws SQLException {
        List rows = new ArrayList();
        ResultSetMetaData rsmd = rSet.getMetaData();
        int columnCount = rsmd.getColumnCount();
        String[] colValues = new String[columnCount];
        Map<String, String> map = new HashMap<String, String>();
        int in = 0;
        while(rSet.next()) {
            for (int i=1; i <= columnCount ; i++) {
                String col_name = rsmd.getColumnName(i);
                colValues[i-1] = rSet.getString(col_name);
                map.put(rSet.getString("phrase"),rSet.getString(col_name));
            }
            rows.add(in,map);
        }
        return rows;
    }
    public static Map makeArray(ResultSet rs) throws SQLException {
        String[] rows = new String[rs.getFetchSize()];
        Map<String, String> map = new HashMap<String, String>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        while(rs.next())
        {
            for (int i=1; i <= columnCount ; i++) {
                String col_name = rsmd.getColumnName(i);
                //rows[rs.getString("phrase")] = rs.getString(col_name);
                map.put(rs.getString("phrase"),rs.getString(col_name));
            }
        }
        return map;
    }
}