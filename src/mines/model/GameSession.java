package mines.model;

import mines.net.EventSender;

import java.util.List;
import java.util.ArrayList;

/**
 * File Description -
 * Date: 21-oct-2006
 */
public abstract class GameSession {
    public static final int STANDALONE_GAME_MODE = 0;
    public static final int UNCOVER_GAME_MODE = 1;
    public static final int FLAG_GAME_MODE = 2;

    protected String name;
    protected Player myself;
    protected Player creator;
    protected Player winner;
    protected boolean isLogged;
    protected List<Player> players;
    protected EventSender eventSender;
    protected int gameMode = FLAG_GAME_MODE;
    protected boolean autoFlags;
    protected Board board;

    public Board getBoard() {
        return board;
    }

    public boolean isStandaloneGameMode() {
        return gameMode == STANDALONE_GAME_MODE;
    }

    public int getGameMode() {
        return gameMode;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    public void setAutoFlag(boolean autoFlags) {
        this.autoFlags = autoFlags;
    }

    public boolean getAutoFlag() {
        return this.autoFlags;
    }

    public void setEventSender(EventSender eventSender) {
        this.eventSender = eventSender;
    }

    public EventSender getEventSender() {
        return this.eventSender;
    }

    public Player getWinner() {
        return this.winner;
    }

    public boolean isWinnerMyself() {
        return myself.equals(winner);
    }

    public String getName() {
        return this.name;
    }

    public boolean isSelfCreator() {
        return myself.equals(creator);
    }

    public Player getCreator() {
        return creator;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getNumPlayers() {
        return players.size();
    }

    public Player getMyself() {
        return this.myself;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean logged) {
        isLogged = logged;
    }
    
    public Player findPlayer(int id) {
        for(Player player : players) {
            if (player.getId() == id) {
                return player;
            }
        }
        return null;
    }

    public boolean hasFinished() {
        return getGameMode() == GameSession.FLAG_GAME_MODE && board.getNumFlagsLeft() == 0
                || getGameMode() == GameSession.UNCOVER_GAME_MODE && board.getCovered().size() == 0;
    }

    public boolean hasStarted() {
        return board.getUncovered().size() > 0;
    }

    public void init(Player myself, Player creator, String name, int gameMode, boolean autoFlags) {
        this.myself = myself;
        this.creator = creator;
        this.name = name;
        this.gameMode = gameMode;
        this.autoFlags = autoFlags;
        this.players.clear();
    }

    public abstract void join(Player player);

    public abstract void quit(Player player);

    public abstract void ready(Player player);

    public abstract void uncoverCell(int x, int y, Player byPlayer);

    public abstract void setFlag(int x, int y, Player byPlayer);

    public abstract void initBoard(int xSize, int ySize, int numMines);

    public abstract void resetBoard(List<Cell> mines);

    public abstract void talk(Player player, String message);

    public abstract void gameOver(Player player);

    public abstract void finishGame(Player winner);

    protected abstract void setFlagByMyself(Cell cell);

    protected void checkAdjacentAutoFlag(Cell cell) {
        setAutoFlag(cell);
        for (Cell adj : cell.getAdjacents()) {
            setAutoFlag(adj);
        }
    }

    private void setAutoFlag(Cell cell) {
        if (cell.isUncovered() && cell.isAdjacentMinesEqualCovereds()) {
            for (Cell adj : cell.getAdjacents()) {
                if (adj.isCovered() && !adj.hasFlag()) {
                    setFlagByMyself(adj);
                }
            }
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(name);
        builder.append(" (").append(creator.getUsername()).append(", ").append(getNumPlayers()).append(", ").
                append(hasStarted()? "playing" : "waiting").append(")");
        return builder.toString();
    }

}
