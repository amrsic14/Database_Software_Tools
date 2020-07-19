/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acamr
 */
public class DBConnection {

    private static final String USERNAME = "sa";
    private static final String PASSWORD = "123";
    private static final String DATABASE = "ma160414";
    private static final int PORT = 1433;
    private static final String SERVER = "localhost";

    private static final String CONNURL
            = "jdbc:sqlserver://" + SERVER + ":" + PORT + ";databaseName=" + DATABASE;

    private static Connection conn;

    private DBConnection() {
        try {
            conn = DriverManager.getConnection(CONNURL, USERNAME, PASSWORD);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection getConnection() {
        return conn;
    }

    private static DBConnection db = null;

    public static DBConnection getInstance() {
        if (db == null) {
            db = new DBConnection();
        }
        return db;
    }
    
}
