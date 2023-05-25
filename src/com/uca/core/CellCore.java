package com.uca.core;

import com.uca.dao.CellDAO;
import com.uca.entity.CellEntity;

import java.sql.SQLException;
import java.util.List;

public class CellCore {

    /*
    public static CellEntity getCell(int x, int y){

        return new CellDAO().getCell(x, y);
    }*/

    public static List<CellEntity> getCellsAlive(int session) throws SQLException {

        return new CellDAO().getCellAlive(session);
    }

    public static void changeState(CellEntity c, int session){

        if(new CellDAO().getState(c, session) == 1){

            new CellDAO().setDead(c, session);
        }
        else{

            new CellDAO().setAlive(c, session);
        }
    }

    public static void setAllDead(int session){

        new CellDAO().setAllDead(session);
    }

    public static void save(int session){

        new CellDAO().save(session);
    }

    public static void cancel(int session){

        new CellDAO().cancel(session);
    }

    public static void nextGeneration(CellEntity c, boolean isAlive, int session, List<CellEntity> cellsToChange){

        new CellDAO().nextGeneration(c, isAlive, session, cellsToChange);
    }
}
