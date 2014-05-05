package com.battleship.config;

import java.util.ArrayList;

public class Board {
    public static final int N = 10;
    public Cell [][] enemy_board;
    public ArrayList<Ship> ships;

    public Board() {
        enemy_board = new Cell[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                enemy_board[i][j] = new Cell(i, j);
        ships = new ArrayList<Ship>();
    }

    public String toString() {
        String ret = "";
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++)
                ret += "" + this.enemy_board[i][j] + " ";
            ret += "\n";
        }
        return ret;
    }

    public static void main(String [] args) {
        System.out.println("Hello, world!");
        Board a = new Board();
        System.out.println(a);
        boolean [][] board = new boolean[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                board[i][j] = false;
        board[0][0] = true;
        board[0][1] = true;
        board[0][2] = true;
        board[0][3] = true;
        board[2][0] = true;
        board[2][1] = true;
        board[2][2] = true;
        board[4][0] = true;
        board[4][1] = true;
        board[4][2] = true;
        board[6][0] = true;
        board[6][1] = true;
        board[8][0] = true;
        board[8][1] = true;
        board[0][5] = true;
        board[0][6] = true;
        board[0][8] = true;
        board[2][4] = true;
        board[2][6] = true;
        board[2][8] = true;
        ArrayList<Ship> ships = a.load_board(board);
        System.out.println("" + ships.size() + " " + a.fullShips(ships));
    }

    public Cell getCell(int x, int y) {
        if (x < 0 || x >= 10 || y < 0 || y >= 10)
            return null;
        return enemy_board[x][y];
    }

    public void markDead(Cell source) {
        if (source == null)
            return;
        boolean cont = false;
        if (source.state == CellState.DAMAGED) {
            source.state = CellState.DEAD;
            cont = true;
        }
        if (source.state == CellState.DEAD)
            cont = true;
        if (source.state == CellState.UNDEFINED)
            source.state = CellState.MISSED;
        if (cont)
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0)
                    continue;
                Cell c = getCell(source.x + i, source.y + j);
                if (c != null && c.state == CellState.DEAD)
                    continue;
                markDead(c);
            }
    }

    private ArrayList<Cell> extractCells(boolean [][] board, int x, int y) {
        ArrayList<Cell> ret = new ArrayList<Cell>();
        if (x < 0 || x >= N || y < 0 || y >= N || !board[x][y])
            return ret;
        board[x][y] = false;
        ret.add(enemy_board[x][y]);
        for (int i = x - 1; i <= x + 1; i++)
            for (int j = y - 1; j <= y + 1; j++) {
                if (i == x && j == y)
                    continue;
                ArrayList<Cell> tmp = extractCells(board, i, j);
                for (int k = 0; k < tmp.size(); k++)
                    ret.add(tmp.get(k));
            }
        return ret;
    }

    private Ship extractShip(boolean [][] board, int x, int y) {
        ArrayList<Cell> ship_cells = extractCells(board, x, y);
        Ship ret = new Ship(ship_cells);
        if (ret.isValid())
            return ret;
        return null;
    }

    public ArrayList<Ship> load_board(boolean [][] board) {
        ArrayList<Ship> ships = new ArrayList<Ship>();
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                if (board[i][j] == false)
                    continue;
                Ship s = extractShip(board, i, j);
                if (s != null)
                    ships.add(s);
                else
                    return new ArrayList<Ship>();
            }
        return ships;
    }

    public boolean fullShips(ArrayList<Ship> ships) {
        int [] sizes = new int[4];
        for (int i = 0; i < 4; i++)
            sizes[i] = 0;
        for (Ship s : ships)
            sizes[s.cells.size() - 1]++;
        for (int i = 0; i < 4; i++) {
            if (sizes[i] != 4 - i)
                return false;
        }
        return true;
    }

    public boolean applyShips(ArrayList<Ship> ships) {
        if (!fullShips(ships)) {
            System.out.println("wtf");
            return false;
        }
        for (Ship s : ships) {
            s.bind();
            this.ships.add(s);
        }
        return true;
    }
}

