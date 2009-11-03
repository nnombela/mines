package mines.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;


/**
 * Boxes class
 * <p/>
 * This class is used to ...
 *
 * @author <a href="mailto:nnombela@gmail.com">Nicol&acute;s Nombela</a>
 * @since 24-jun-2006
 */
public class Board {
    public static final int BEGGINER = 1;
    public static final int MEDIUM = 2;
    public static final int ADVANCE = 3;
    public static final int GIANT = 4;

    private int xSize, ySize;
    private Cell[][] array;
    private int numMines;
    private int numFlagsLeft;
    private List<Cell> mines;
    private List<Cell> covered;
    private List<Cell> uncovered;

    public void init(int level) {
        switch(level) {
            case 1 : init(9, 9, 10); break;
            case 2 : init(16, 16, 40); break;
            case 3 : init(30, 16, 99); break;
            case 4 : init(46, 20, 220); break;
            default : init(30, 16, 99);
        }
    }

    public void init(int xSize, int ySize, int numMines) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.numMines = numMines;
        this.array = new Cell[xSize][ySize];
        this.uncovered = new ArrayList<Cell>(xSize * ySize);
        for(int x = 0; x < xSize; ++x) {
            for(int y = 0; y < ySize; ++y) {
                array[x][y] = new Cell(x, y);
            }
        }
        for(int x = 0; x < xSize; ++x) {
            for(int y = 0; y < ySize; ++y) {
                setAdjacents(array[x][y]);
            }
        }
    }

    public List<Cell> getMines() {
        return this.mines;
    }

    public List<Cell> getCovered() {
        return this.covered;
    }

    public List<Cell> getUncovered() {
        return this.uncovered;
    }

    public void uncover(Cell cell, Player player) {
        if (cell.uncoveredBy == null) {
            for (Cell adj : cell.getAdjacents()) {
                ++adj.numAdjacentUncovered;
            }
            covered.remove(cell);
            uncovered.add(cell);
        }
        cell.uncoveredBy = player;

    }

    public boolean hasAdjacentCovered(Cell cell) {
        for (Cell adj : cell.getAdjacents()) {
            if (!adj.hasMine() && adj.isCovered()) {
                return true;
            }
        }
        return false;
    }

    public List<Cell> getShuffleMines() {
        List<Cell> cells = getAllCells();
        Collections.shuffle(cells);
        return cells.subList(0, getNumMines());
    }

    public void reset(List<Cell> mines) {
        List<Cell> cells = resetAllCells();
        for(Cell cell : mines) {
            putMine(cell);
            cells.remove(cell);
        }
        Collections.shuffle(cells);        
        this.mines = mines;
        this.covered = cells;
        this.uncovered.clear();
        this.numFlagsLeft = numMines;
    }

    private List<Cell> resetAllCells() {
        List<Cell> cells = getAllCells();
        for(Cell cell : cells) {
            cell.reset();
        }
        return cells;
    }

    public int getNumMines() {
        return this.numMines;
    }

    public int getNumFlagsLeft() {
        return this.numFlagsLeft;
    }

    public int getNumFlags() {
        return numMines - numFlagsLeft;
    }

    public int getSize() {
        return xSize * ySize;
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }

    public Cell getCellInRange(int x, int y) {
        if (isInRange(x, y)) {
            return getCell(x, y);
        } else {
            return null;
        }
    }

    public Cell getCell(int x, int y) {
        return array[x][y];
    }

    public List<Cell> getAllCells() {
        List<Cell> cells = new ArrayList<Cell>(xSize * ySize);
        for(int x = 0; x < xSize; ++x) {
            for(int y = 0; y < ySize; ++y) {
                cells.add(getCell(x, y));
            }
        }
        return cells;
    }

    public boolean isInRange(int x, int y) {
        return (x >= 0 && x < xSize && y >= 0 && y < ySize);
    }

    private void addAdjacent(List<Cell> list, int x, int y) {
        if (isInRange(x, y)) {
            list.add(getCell(x, y));
        }
    }

    public void setAdjacents(Cell cell) {
        int x = cell.getX(), y = cell.getY();
        
        List<Cell> adjacents = new ArrayList<Cell>(8);
        addAdjacent(adjacents, x - 1, y - 1);
        addAdjacent(adjacents, x - 1, y);
        addAdjacent(adjacents, x - 1, y + 1);
        addAdjacent(adjacents, x, y - 1);
        addAdjacent(adjacents, x, y + 1);
        addAdjacent(adjacents, x + 1, y - 1);
        addAdjacent(adjacents, x + 1, y);
        addAdjacent(adjacents, x + 1, y + 1);

        cell.setAdjacents(adjacents);
    }

    private void putMine(Cell cell) {
        cell.hasMine = true;
        for (Cell adj : cell.getAdjacents()) {
            ++adj.numAdjacentMines;
        }
    }

    public void setFlag(Cell cell, Player player) {
        if (cell.hasFlag == false) {
            cell.hasFlag = true;
            --numFlagsLeft;
            for (Cell adj : cell.getAdjacents()) {
                ++adj.numAdjacentFlags;
            }
        }
        cell.setFlagBy = player;
    }

    public void removeFlag(Cell cell) {
        if (cell.hasFlag == true) {
            cell.hasFlag = false;
            ++numFlagsLeft;
            for (Cell adj : cell.getAdjacents()) {
                --adj.numAdjacentFlags;
            }
        }
        cell.setFlagBy = null;
    }

    public void setFlagOnMines(Player player) {
        for(Cell cell : mines) {
            if (!cell.hasFlag()) {
                setFlag(cell, player);
            }
        }
    }
}

