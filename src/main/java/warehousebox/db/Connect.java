/*
 * The MIT License
 *
 * Copyright 2023 Saleh.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package warehousebox.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.flywaydb.core.Flyway;

/**
 *
 * @author Saleh
 */
public class Connect {

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    //  Database credentials
    private static final String DB_NAME = "warehouse-box-db";
    private static final String USER = "root";
    private static final String PASS = "$%^1Rt!&*d!21";

    private static final String DB_URL = "jdbc:derby:"
            + System.getProperty("user.home")
            + File.separator
            + DB_NAME + ";create=true;collation=TERRITORY_BASED:PRIMARY";

    private static final String DB_SHUTDOWN_URL = "jdbc:derby:"
            + System.getProperty("user.home")
            + File.separator
            + DB_NAME + ";user=" + USER + ";password=" + PASS + ";shutdown=true";

    // For atomic statements
    private static Connection conn;

    public static void buildDatabaseIfNotExist() {
        Flyway flyway = Flyway.configure().dataSource(DB_URL, USER, PASS).load();
        flyway.migrate();
    }

    public static Connection getConnection() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

    public static Connection getConnection(boolean usePreviousConnectionForAtomicCommit) {

        if (conn != null && usePreviousConnectionForAtomicCommit) {
            return conn;
        }

        conn = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

    public static void rollBack() {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void shutdown() {
        try {
            DriverManager.getConnection(DB_SHUTDOWN_URL);
        } catch (SQLException ex) {
            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
