package com.battleship.config;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.*;

public class Game {
    public Board me, enemy;

    public Game(boolean [][] board) {
        me = new Board();
        boolean [][] tmp = new boolean[10][10];
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                tmp[i][j] = board[i][j];
        ArrayList<Ship> my_ships = me.load_board(tmp);
        me.applyShips(my_ships);

        enemy = new Board();
    }

    public void inform(int x, int y, CellState state) {
        Cell c = enemy.getCell(x, y);
        if (c == null)
            return;
        c.state = state;
        if (c.state == CellState.DEAD) {
            enemy.markDead(c);
        }
    }

    public CellState get_hit(int x, int y) {
        if (me.enemy_board[x][y].parent == null) {
            me.enemy_board[x][y].state = CellState.MISSED;
            return CellState.MISSED;
        }
        me.enemy_board[x][y].state = CellState.DAMAGED;
        if (me.enemy_board[x][y].parent.status() == CellState.DEAD) {
            me.enemy_board[x][y].parent.kill();
            for (Cell c : me.enemy_board[x][y].parent.cells) {
                for (int i = -1; i <= 1; i++)
                    for (int j = -1; j <= 1; j++) {
                        if (i == 0 && j == 0)
                            continue;
                        Cell tmp = me.getCell(c.x + i, c.y + j);
                        if (tmp != null)
                            tmp.markMissed();
                    }
            }
        }
        boolean allDead = true;
        for (Ship s : me.ships) {
            if (s.status() != CellState.DEAD) {
                allDead = false;
                break;
            }
        }
        if (allDead) {
            return CellState.LOST;
        }
        return me.enemy_board[x][y].state;
    }

    public String toString() {
        return "" + me + "\n" + enemy + "\n";
    }

    static boolean [][] readBoardFromDisk(String filename) {
        boolean [][] ret = null;
        try
        {
            Scanner in = new Scanner(new FileReader(filename));
            ret = new boolean[10][10];
            for (int i = 0; i < 10; i++)
                for (int j = 0; j < 10; j++) {
                    int k = in.nextInt();
                    if (k == 0)
                        ret[i][j] = false;
                    else
                        ret[i][j] = true;
                }
        }
        catch (Exception e)
        {
        }
        return ret;
    }

    public static void main(String [] args) {
        boolean [][] board = readBoardFromDisk("board");
        Game a = new Game(board);
        System.out.println(a.get_hit(0, 1));
        for (Ship s : a.me.ships)
            System.out.println(s.status() + " " + s);
        a.get_hit(0, 4);
        a.get_hit(0, 6);
        a.get_hit(2, 0);
        a.get_hit(2, 4);
        a.get_hit(2, 5);
        a.get_hit(2, 6);
        a.get_hit(3, 0);
        a.get_hit(4, 6);
        a.get_hit(5, 1);
        a.get_hit(5, 2);
        a.get_hit(5, 6);
        a.get_hit(6, 6);
        a.get_hit(7, 2);
        a.get_hit(7, 6);
        a.get_hit(9, 1);
        a.get_hit(9, 2);
        a.get_hit(9, 7);
        a.get_hit(9, 8);
        for (Ship s : a.me.ships)
            System.out.println(s.status());
        CellState x = a.get_hit(9, 9);
        if (x == CellState.LOST)
            System.out.println('y');
        else if (x == CellState.DEAD)
            System.out.println('?');
        System.out.println(a);
    }
}
