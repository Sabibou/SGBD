package com.uca.dao;

import com.uca.entity.CellEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CellDAO {

    public List<CellEntity> getCellAlive(int session) throws SQLException {

        Connection connection = _Connector.getUserConnection(session);

        ArrayList<CellEntity> cells = new ArrayList<>();

        try{

            PreparedStatement statement = connection.prepareStatement("Select x, y from plateau WHERE etat=1;");
            ResultSet r = statement.executeQuery();


            while(r.next()){

                CellEntity c = new CellEntity(r.getInt("x"), r.getInt("y"));
                cells.add(c);
            }

        }
        catch (SQLException e){

            e.printStackTrace();
        }

        return cells;

    }

    public void nextGeneration(CellEntity c, boolean isAlive, int session, List<CellEntity> cellsToChange){

        int count = countNeighboursAlive(c, isAlive, session, cellsToChange);

        if(isAlive){
            if(count < 2 || count > 3){ //la cellule meurt
                cellsToChange.add(c);
            }
        }
        else{
            if(count == 3){ //la cellule vit
                cellsToChange.add(c);
            }
        }
    }

    private int countNeighboursAlive(CellEntity c, boolean isAlive, int session, List<CellEntity> cellsToChange){

        try{

            Connection connection = _Connector.getUserConnection(session);

            int count = 0;

            int[] X = {1, 1, 0, -1, -1, -1, 0, 1};
            int[] Y = {0, 1, 1, 1, 0, -1, -1, -1};

            for(int i=0; i<8; i++){

                PreparedStatement statement = connection.prepareStatement("Select etat from plateau WHERE x=? and y=?;");
                statement.setInt(1, c.getX() + X[i]);
                statement.setInt(2, c.getY() + Y[i]);
                ResultSet r = statement.executeQuery();

                if(r.next()){

                    count+=r.getInt("etat");

                    if(isAlive && r.getInt("etat") == 0){

                        nextGeneration(new CellEntity(c.getX() + X[i], c.getY() + Y[i]), false, session, cellsToChange);
                    }
                }

            }
            System.out.println("alive " + isAlive + " x=" + c.getX() + " y=" + c.getY() + "  " + count);
            return count;
        }
        catch (SQLException e){

            e.printStackTrace();
        }

        return -1;
    }

    public int getState(CellEntity c, int session){

        try{

            Connection connection = _Connector.getUserConnection(session);

            PreparedStatement statement = connection.prepareStatement("Select etat from plateau WHERE x=? and y=?;");
            statement.setInt(1, c.getX());
            statement.setInt(2, c.getY());
            ResultSet r = statement.executeQuery();

            r.next();

            return r.getInt("etat");

        }
        catch (SQLException e){

            e.printStackTrace();
        }

        return -1;

    }
    public void setAlive(CellEntity c, int session){

        try{

            Connection connection = _Connector.getUserConnection(session);

            PreparedStatement statement = connection.prepareStatement("UPDATE plateau SET etat=1 WHERE x=? and y=?;");
            statement.setInt(1, c.getX());
            statement.setInt(2, c.getY());
            statement.executeUpdate();

        }
        catch (SQLException e){

            e.printStackTrace();
        }

    }

    public void setDead(CellEntity c, int session){

        try{
            Connection connection = _Connector.getUserConnection(session);

            PreparedStatement statement = connection.prepareStatement("UPDATE plateau SET etat=0 WHERE x=? and y=?;");
            statement.setInt(1, c.getX());
            statement.setInt(2, c.getY());
            statement.executeUpdate();

        }
        catch (SQLException e){

            e.printStackTrace();
        }

    }

    public void setAllDead(int session){

        try{

            Connection connection = _Connector.getUserConnection(session);

            PreparedStatement statement = connection.prepareStatement("UPDATE plateau SET etat=0;");
            statement.executeUpdate();

        }
        catch (SQLException e){

            e.printStackTrace();
        }
    }

    public void save(int session){

        try{

            Connection connection = _Connector.getUserConnection(session);

            connection.commit();

        }
        catch (SQLException e){

            e.printStackTrace();
        }
    }

    public void cancel(int session){

        try{

            Connection connection = _Connector.getUserConnection(session);

            connection.rollback();

        }
        catch (SQLException e){

            e.printStackTrace();
        }
    }



}
