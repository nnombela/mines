package mines.model;

import java.util.List;

/**
 * Box class
 * <p/>
 * This class is used to ...
 *
 * @author <a href="mailto:nnombela@gmail.com">Nicol&acute;s Nombela</a>
 * @since 25-jun-2006
 */
public class Cell {
    private int x;
    private int y;
    boolean hasMine = false;
    boolean hasFlag = false;
    Player uncoveredBy;
    Player setFlagBy;

    int numAdjacentMines = 0;
    int numAdjacentFlags = 0;
    int numAdjacentUncovered = 0;

    List<Cell> adjacents;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Cell(int x, int y, Player uncoveredBy) {
        this(x, y);
        this.uncoveredBy = uncoveredBy;
    }

    public void setAdjacents(List<Cell> adjacents) {
        this.adjacents = adjacents;
    }

    public List<Cell> getAdjacents() {
        return this.adjacents;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void reset() {
        this.hasMine = false;
        this.hasFlag = false;
        this.uncoveredBy = null;
        this.setFlagBy = null;
        this.numAdjacentMines = 0;
        this.numAdjacentFlags = 0;
        this.numAdjacentUncovered = 0;
    }
    
    public Player getUncoveredBy() {
        return this.uncoveredBy;
    }

    public Player getSetFlagBy() {
        return this.setFlagBy;
    }

    public boolean hasMine() {
        return this.hasMine;
    }

    public boolean hasFlag() {
        return this.hasFlag;
    }

    public boolean isCovered() {
        return this.uncoveredBy == null;
    }

    public boolean isUncovered() {
        return this.uncoveredBy != null;
    }

    public int getNumAdjacentMines() {
        return numAdjacentMines;
    }

    public int getNumAdjacentFlags() {
        return numAdjacentMines;
    }

    public int getNumAdjacentUncovered() {
        return numAdjacentUncovered;
    }

    public boolean isAdjacentMinesEqualFlags() {
        return numAdjacentMines == numAdjacentFlags;
    }

    public boolean isAdjacentMinesEqualCovereds() {
        return numAdjacentUncovered + numAdjacentMines == 8;
    }
}