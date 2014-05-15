package com.battleship.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.battleship.app.GameActivity;
import com.battleship.app.NewGameActivity;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class GlobalState {
    private static GlobalState instance = null;
    public boolean[][] boolMap = new boolean[10][10];
    public String playerName = "Vladimir";
    public String enemyName = "";
    public String server = "swarm.cs.pub.ro:50050";
    public Board emptyBoard = new Board();
    public GameActivity gameActivity;
    public NewGameActivity newGameActivity;
    public Game game;
    public boolean to_move = false;
    public BufferedReader socketIn;
    public PrintWriter socketOut;

    protected GlobalState() {
    }

    public static synchronized GlobalState getInstance(){
        if(null == instance){
            instance = new GlobalState();
        }
        return instance;
    }

    public void setPlayerName(String name) {
        playerName = name;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setServerAddress(String address) {
        server = address;
    }

    public String getServerAddress() {
        return server;
    }
}
