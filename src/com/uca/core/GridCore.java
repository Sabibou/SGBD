package com.uca.core;

import com.uca.dao.*;
import com.uca.entity.*;
import spark.servlet.SparkApplication;

import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

public class GridCore {

    public static List<CellEntity> getGrid(int session) throws SQLException {

        return new GridEntity(CellCore.getCellsAlive(session)).getCells();
    }

    public static void setListAlive(List<CellEntity> list, int session){

        for(CellEntity c : list){

            new CellDAO().setAlive(c, session);
        }
    }

    public static void nextGeneration(int session) throws SQLException {

        LinkedList<CellEntity> cellsToChange = new LinkedList<>();

        List<CellEntity> cellsAlive = CellCore.getCellsAlive(session);

        for(CellEntity c : cellsAlive){
            System.out.println("new cell");
            CellCore.nextGeneration(c, true, session, cellsToChange);
        }

        for(CellEntity c : cellsToChange){

            CellCore.changeState(c, session);
        }
    }

    /**
     * Décode le contenu d'un fichier RLE sous forme de cases à partir d'un URL
     * @param url - url d'un fichier RLE, ex : https://conwaylife.com/patterns/5blink.rle
     */
    public static List<CellEntity> decodeRLEUrl(String url) throws Exception {
        URL u = new URL(url);
        BufferedReader in = new BufferedReader(
        new InputStreamReader(u.openStream()));

        StringBuffer sb = new StringBuffer();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
            System.out.println("line : " + inputLine);
            sb.append("\n");
        }
        
        in.close();

        return decodeRLE(sb.toString());
    }
    
    /**
     * Décode le contenu d'un fichier RLE sous forme de cases
     * @param rle - un chaîne représentant une serialisation RLE
     */
    public static List<CellEntity> decodeRLE(String rle) {
        List<CellEntity> cells = new ArrayList<>();
        boolean ignore = false;
        int step = 1;
        int x = 50;
        int y = 50;
        String number;
        Pattern pattern = Pattern.compile("^[0-9]+");
        int i = -1; 
        while (i < rle.length() - 1) {
            i++;
            if (ignore) {
                if (rle.charAt(i) == '\n') {
                    ignore = false;
                }
                continue;
            }
            switch (rle.charAt(i)) {
            case '#':
            case 'x':
            case '!':
                ignore = true;
                continue;
            case '$':
                x = 50;
                y += step;
                step = 1;
                continue;
            case 'b':
                x += step;
                step = 1;
                continue;
            case 'o':
                for (int j = 0; j < step; j++) {
                    CellEntity c = new CellEntity(x++, y);
                    System.out.println(c);
                    cells.add(c);
                }
                System.out.println(rle.substring(Math.max(0, rle.lastIndexOf("$",i)))); 
                step = 1;
                continue;
            }
            Matcher matcher = pattern.matcher(rle.substring(i));
            if (matcher.find()) {
                number = matcher.group();
                step = Integer.parseInt(number);
                i += number.length() - 1;
            }
        }
        return cells;
    }
}
