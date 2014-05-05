package com.battleship.config;
import java.util.ArrayList;
import java.util.Collections;

public class Ship {
    public ArrayList<Cell> cells;
    public Ship(ArrayList<Cell> cells) {
        if (cells == null) {
            this.cells = null;
            return;
        }
        this.cells = new ArrayList<Cell>();
        for (int i = 0; i < cells.size(); i++)
            this.cells.add(cells.get(i));
    }
    public Ship() {
        this(null);
    }
    public void bind() {
        for (Cell c : cells) {
            c.parent = this;
            c.state = CellState.INTACT;
        }
    }
    public void kill() {
        for (Cell c : cells)
            c.state = CellState.DEAD;
    }
    CellState status() {
        int damaged = 0;
        for (Cell c : cells) {
            if (c.state == CellState.UNDEFINED)
                return CellState.UNDEFINED;
            if (c.state == CellState.DAMAGED || c.state == CellState.DEAD)
                damaged++;
        }
        if (damaged == cells.size())
            return CellState.DEAD;
        if (damaged == 0)
            return CellState.INTACT;
        return CellState.DAMAGED;
    }
    public boolean isValid() {
        if (cells == null)
            return false;
        if (cells.size() < 1 || cells.size() > 4)
            return false;
        for (int i = 0; i < cells.size(); i++) {
            if (cells.get(i).x < 0 || cells.get(i).x >= Board.N || cells.get(i).y < 0 || cells.get(i).y >= Board.N)
                return false;
            for (int j = 0; j < cells.size(); j++)
                if (i != j && cells.get(i) == cells.get(j))
                    return false;
        }
        Collections.sort(cells);

        boolean xx = true;
        boolean yy = true;
        for (int i = 1; i < cells.size(); i++) {
            if (cells.get(i).x != cells.get(0).x)
                xx = false;
            if (cells.get(i).y - cells.get(0).y != i)
                xx = false;
            if (cells.get(i).y != cells.get(0).y)
                yy = false;
            if (cells.get(i).x - cells.get(0).x != i)
                yy = false;
        }

        if (!xx && !yy)
            return false;

        return true;
    }
}

