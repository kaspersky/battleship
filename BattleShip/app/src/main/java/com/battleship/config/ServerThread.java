package com.battleship.config;

import android.content.Intent;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import com.battleship.app.GameActivity;

/**
 * Created by vladimir on 5/4/2014.
 */
public class ServerThread implements Runnable{

    public void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void switchViews() {
        GlobalState.getInstance().gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GlobalState.getInstance().gameActivity.switchViews();
            }
        });
    }

    public void displayFinalMessage(final String msg) {
        GlobalState.getInstance().gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GlobalState.getInstance().gameActivity.displayFinalMessage(msg);
            }
        });
    }

    public void run() {
        try {
            String[] serverConfig = GlobalState.getInstance().server.split(":");
            System.out.println("Connecting to " + serverConfig[0] + ":" + serverConfig[1]);
            Socket echoSocket = new Socket(InetAddress.getByName(serverConfig[0]), Integer.parseInt(serverConfig[1]));
            GlobalState.getInstance().socketOut = new PrintWriter(echoSocket.getOutputStream(), true);
            GlobalState.getInstance().socketIn = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            System.out.println("Connected");
            GlobalState.getInstance().newGameActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    GlobalState.getInstance().newGameActivity.start.setText("Registering...");
                }
            });
            GlobalState.getInstance().socketOut.println("name:" + GlobalState.getInstance().playerName);
            GlobalState.getInstance().newGameActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    GlobalState.getInstance().newGameActivity.start.setText("Waiting for enemy...");
                }
            });
            String resp = GlobalState.getInstance().socketIn.readLine();
            String [] resps = resp.split(":");
            GlobalState.getInstance().enemyName = resps[1];
            System.out.println("Read: " + resp);
            if (Integer.parseInt(resps[2]) == 1)
                GlobalState.getInstance().to_move = true;
            else
                GlobalState.getInstance().to_move = false;
            Intent intent = new Intent(GlobalState.getInstance().newGameActivity.getApplicationContext(), GameActivity.class);
            GlobalState.getInstance().newGameActivity.startActivity(intent);

            while(true) {
                String data = GlobalState.getInstance().socketIn.readLine();
                System.out.println(data);
                resps = data.split(":");
                System.out.println(resps[0]);
                if(resps[0].compareTo("response") == 0) {
                    System.out.println("response branch");
                    if(resps[1].compareTo("missed") == 0) {
                        final int x = Integer.parseInt(resps[2]);
                        final int y = Integer.parseInt(resps[3]);
                        GlobalState.getInstance().game.inform(x, y, CellState.MISSED);
                                //enemy_board[x][y].state = CellState.MISSED;
                        GlobalState.getInstance().gameActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageView[][] map = GlobalState.getInstance().gameActivity.enemyMap;
                                GlobalState.getInstance().gameActivity.updateMap(
                                        GlobalState.getInstance().game.enemy.enemy_board, map);
                                GlobalState.getInstance().to_move = false;
                            }
                        });

                        sleep(1000);
                        switchViews();
                    } else if(resps[1].compareTo("damaged") == 0) {
                        final int x = Integer.parseInt(resps[2]);
                        final int y = Integer.parseInt(resps[3]);
                        GlobalState.getInstance().game.inform(x, y, CellState.DAMAGED);
                                //enemy.enemy_board[x][y].state = CellState.DAMAGED;
                        GlobalState.getInstance().gameActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageView[][] map = GlobalState.getInstance().gameActivity.enemyMap;
                                GlobalState.getInstance().gameActivity.updateMap(
                                        GlobalState.getInstance().game.enemy.enemy_board, map);
                                GlobalState.getInstance().gameActivity.setClickable(false);
                            }
                        });
                    } else if(resps[1].compareTo("dead") == 0) {
                        final int x = Integer.parseInt(resps[2]);
                        final int y = Integer.parseInt(resps[3]);
                        GlobalState.getInstance().game.inform(x, y, CellState.DEAD);
                        //);enemy_board[x][y].state = CellState.DEAD;
                        GlobalState.getInstance().gameActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageView[][] map = GlobalState.getInstance().gameActivity.enemyMap;
                                GlobalState.getInstance().gameActivity.updateMap(
                                        GlobalState.getInstance().game.enemy.enemy_board, map);
                                GlobalState.getInstance().gameActivity.setClickable(false);
                            }
                        });
                    }
                } else if(resps[0].compareTo("move") == 0) {
                    System.out.println("move branch");
                    final int x = Integer.parseInt(resps[1]);
                    final int y = Integer.parseInt(resps[2]);
                    CellState r = GlobalState.getInstance().game.get_hit(x, y);
                    String response = "response:";
                    if(r == CellState.MISSED) {
                        response += "missed";
                        GlobalState.getInstance().game.me.enemy_board[x][y].state = CellState.MISSED;
                        GlobalState.getInstance().gameActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageView[][] map = GlobalState.getInstance().gameActivity.myMap;
                                GlobalState.getInstance().gameActivity.updateMap(
                                        GlobalState.getInstance().game.me.enemy_board, map);
                                GlobalState.getInstance().to_move = true;
                            }
                        });

                        sleep(1000);
                        switchViews();

                    } else if(r == CellState.DAMAGED) {
                        response += "damaged";
                        GlobalState.getInstance().game.me.enemy_board[x][y].state = CellState.DAMAGED;
                        GlobalState.getInstance().gameActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageView[][] map = GlobalState.getInstance().gameActivity.myMap;
                                GlobalState.getInstance().gameActivity.updateMap(
                                        GlobalState.getInstance().game.me.enemy_board, map);
                              }
                        });
                    } else if(r == CellState.DEAD) {
                        response += "dead";
                        GlobalState.getInstance().game.me.enemy_board[x][y].state = CellState.DEAD;
                        GlobalState.getInstance().gameActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageView[][] map = GlobalState.getInstance().gameActivity.myMap;
                                GlobalState.getInstance().gameActivity.updateMap(
                                        GlobalState.getInstance().game.me.enemy_board, map);
                            }
                        });
                    } else if(r == CellState.LOST) {
                        response = "result:lost";
                        displayFinalMessage("You Lost!");
                        sleep(1000);
                        GlobalState.getInstance().gameActivity.finish();
                    }
                    response += ":" + x + ":" + y;
                    GlobalState.getInstance().socketOut.println(response);
                } else if(resps[0].compareTo("result") == 0) {
                    System.out.println("result branch");
                    if(resps[1].compareTo("win") == 0) {
                        displayFinalMessage("You Won!");
                        sleep(1000);
                        GlobalState.getInstance().gameActivity.finish();
                    }
                }
            }

        }
        catch (Exception e) {
            GlobalState.getInstance().newGameActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    GlobalState.getInstance().newGameActivity.start.setText("Error connecting. Try again.");
                    GlobalState.getInstance().newGameActivity.start.setEnabled(true);
                }
            });
            e.printStackTrace();
            System.out.println("Error connecting to server");
        }

    }
}
