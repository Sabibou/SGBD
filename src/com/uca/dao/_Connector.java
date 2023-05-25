package com.uca.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class _Connector {

    private static String url = "jdbc:postgresql://localhost/life";
    private static String user = "roferreira1";
    private static String passwd = "r25032003";

    private static HashMap<Integer, Connection> connections = new HashMap<>();
    private static Connection connect;

    public static Connection getMainConnection(){
        if(connect == null){
            connect = getNewConnection();
        }
        return connect;
    }

    public static Connection getUserConnection(int session) throws SQLException {

        if (connections.containsKey(session)){

            return connections.get(session);
        }
        else{

            Connection newConnect = getNewConnection();
            connections.put(session, newConnect);
            return newConnect;
        }
    }

    private static Connection getNewConnection() {
        Connection c;
        try {
            c = DriverManager.getConnection(url, user, passwd);
            c.setAutoCommit(false);
            c.setTransactionIsolation(8);
            System.out.println("new connection");
        } catch (SQLException e) {
            System.err.println("Erreur en ouvrant une nouvelle connection.");
            throw new RuntimeException(e);
        }
        return c;
    }
}
