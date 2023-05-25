package com.uca;

import com.uca.core.CellCore;
import com.uca.dao._Initializer;
import com.uca.entity.GridEntity;
import com.uca.gui.*;

import com.uca.core.GridCore;
import com.uca.entity.CellEntity;

import com.google.gson.Gson;

import static spark.Spark.*;
import spark.*;

public class StartServer {

    public static void main(String[] args) {
        //Configuration de Spark
        staticFiles.location("/static/");
        port(8081);

        // Création de la base de données, si besoin
        _Initializer.Init();

        /**
         * Définition des routes
         */

        // index de l'application
        get("/", (req, res) -> {
                return IndexGUI.getIndex();
            });

        // retourne l'état de la grille
        get("/grid", (req, res) -> {
                res.type("application/json");
                System.out.println(getSession(req));
                return new Gson().toJson(GridCore.getGrid(getSession(req)));
            });

        // inverse l'état d'une cellule 
        put("/grid/change", (req, res) -> {
                Gson gson = new Gson();
                CellEntity selectedCell = (CellEntity) gson.fromJson(req.body(), CellEntity.class);
                CellCore.changeState(selectedCell, getSession(req));
                return "";

            });

        // sauvegarde les modifications de la grille 
        post("/grid/save", (req, res) -> {
                CellCore.save(getSession(req));
                return "";
            });

        // annule les modifications de la grille 
        post("/grid/cancel", (req, res) -> {
                System.out.println(getSession(req));
                CellCore.cancel(getSession(req));
                return "";
            });

        // charge un fichier rle depuis un URL
        put("/grid/rle", (req, res) -> {
                String RLEUrl = req.body();
                GridCore.setListAlive(GridCore.decodeRLEUrl(RLEUrl), getSession(req));
                return "";
            });

        // vide la grille
        post("/grid/empty", (req, res) -> {
                CellCore.setAllDead(getSession(req));
                return "";
            });

        // met à jour la grille en la remplaçant par la génération suivante
        post("/grid/next", (req, res) -> {
                GridCore.nextGeneration(getSession(req));
                return "";
            });

    }

    /**
     * retourne le numéro de session
     * il y a un numéro de session différent pour chaque onglet de navigateur
     * ouvert sur l'application
     */
    public static int getSession(Request req) {
        return Integer.parseInt(req.queryParams("session"));
    }
}
