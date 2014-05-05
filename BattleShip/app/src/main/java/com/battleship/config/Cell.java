package com.battleship.config;

public class Cell implements Comparable {

    public Ship parent;
    public CellState state;
    int x, y;

    public Cell(Ship parent, CellState state, int x, int y) {
        this.parent = parent;
        this.state = state;
        this.x = x;
        this.y = y;
    }
    public Cell() {
        this(null, CellState.UNDEFINED, -1, -1);
    }
    public Cell(int x, int y) {
        this();
        this.x = x;
        this.y = y;
    }
    public void markMissed() {
        if (state == CellState.UNDEFINED)
            state = CellState.MISSED;
    }
    public String toString() {
        if (state == CellState.UNDEFINED)
            return ".";
        if (state == CellState.INTACT)
            return "I";
        if (state == CellState.DEAD)
            return "D";
        if (state == CellState.DAMAGED)
            return "d";
        if (state == CellState.MISSED)
            return "M";
        return " ";
    }
    public int compareTo(Object obj) {
        Cell ref = (Cell) obj;
        if (x < ref.x)
            return -1;
        if (x > ref.x)
            return 1;
        if (y < ref.y)
            return -1;
        if (y > ref.y)
            return 1;
        return 0;
    }
}



