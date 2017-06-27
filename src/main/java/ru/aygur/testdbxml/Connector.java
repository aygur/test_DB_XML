package ru.aygur.testdbxml;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by dmitrii on 01.06.17.
 */
public class Connector implements AutoCloseable {
    private static final String connectionPoint = "jdbc:mysql://localhost:3306/";

    private Connection conn;
    private String url;
    private Properties p = new Properties();

    public Connector(){
    }

    public Connector(String database, String user, String password) {
        this.url = connectionPoint + database;
        p.put("user", user);
        p.put("password", password);
    }

    public Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = (Connection) DriverManager.getConnection(url, p);
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException sqle) {
            sqle.printStackTrace();
            System.out.println("ClassNotFoundException - check installed jdbc driver");
            System.exit(1);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQLException - check available SQL server");
            System.exit(1);
        }
        return conn;
    }

    public void close() {
        try {
             conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Exception close connection");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Connector connector = (Connector) o;

        if (conn != null ? !conn.equals(connector.conn) : connector.conn != null) return false;
        if (!url.equals(connector.url)) return false;
        return p != null ? p.equals(connector.p) : connector.p == null;
    }

    @Override
    public int hashCode() {
        int result = conn != null ? conn.hashCode() : 0;
        result = 31 * result + url.hashCode();
        result = 31 * result + (p != null ? p.hashCode() : 0);
        return result;
    }
}

