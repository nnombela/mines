package mines.model;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nnombela
 * Date: 14-dic-2008
 */
public class PlayerSession extends GameSession {
    private int numPlayers;
    private boolean hasStarted;

    public PlayerSession(String name) {
        this.name = name;
    }

    public PlayerSession(String name, Player creator, int numPlayers, boolean hasStarted) {
        this.name = name;
        this.creator = creator;
        this.numPlayers = numPlayers;
        this.hasStarted = hasStarted;
    }

    public boolean hasStarted() {
        return this.hasStarted;
    }

    public void join(Player player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void quit(Player player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void ready(Player player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void uncoverCell(int x, int y, Player byPlayer) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setFlag(int x, int y, Player byPlayer) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void initBoard(int xSize, int ySize, int numMines) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void resetBoard(List<Cell> mines) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void talk(Player player, String message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void gameOver(Player player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void finishGame(Player winner) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    protected void setFlagByMyself(Cell cell) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getNumPlayers() {
        return numPlayers;
    }
}
