package com.uca.dao;

import java.sql.*;

public class _Initializer {
    // nom de la table contenant la grille
    final static String TABLE = "plateau";
    // taille de grille
    final static int SIZE = 100;

    /**
     * cette méthode permet d'initialiser en créant une table pour la grille si elle n'existe pas
     */
    public static void Init(){
        try{
            Connection connection = _Connector.getMainConnection();
            connection.setAutoCommit(false);

            //Init articles table
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS plateau (x int, y int, etat int, primary key(x,y)); ");
            int r = statement.executeUpdate();
            connection.commit();

            if(r>0){

                for(int i=0; i<SIZE; i++){

                    for(int j=0; j<SIZE; j++){

                        statement = connection.prepareStatement("INSERT INTO plateau (x, y, etat) values (?, ?, 0)");
                        statement.setInt(1, i);
                        statement.setInt(2, j);
                        statement.executeUpdate();
                    }
                }
                connection.commit();
            }


        }
        catch(SQLException e){
            e.printStackTrace();
        }

    }

    /**
     * teste si une table existe dans la base de données 
     */
    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName, new String[]{"TABLE"});
        return resultSet.next();
    }
}
