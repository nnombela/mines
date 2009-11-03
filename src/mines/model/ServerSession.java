package mines.model;

import mines.net.EventSender;

import java.util.*;

/**
 * File Description -
 * Date: 08-oct-2006
 */
public class ServerSession extends GameSession {
    private static final long HEARTBEAT_ELAPSE_TIME = 10 * 1000;
    private static final long PLAY_ELAPSE_TIME = 3 * 1000;
    private static final long OPEN_ELAPSE_TIME = 1 * 1000;
    private static final long COUNTDOWN_ELAPSE_TIME = 5 * 1000;
    private static final long GET_READY_ELAPSE_TIME = 20 * 1000;

    private static final int INVALID_STATE = -1;
    private static final int GET_READY_STATE = 0;
    private static final int COUNTDOWN_STATE = 1;
    private static final int OPEN_STATE = 2;
    private static final int PLAY_STATE = 3;

    private int state = INVALID_STATE;
    private long lastEventTime;

    public ServerSession(EventSender sender) {
        this.board = new Board();
        this.players = new ArrayList<Player>(2);
        this.eventSender = sender;
    }

    private void updateLastEventTime() {
        this.lastEventTime = System.currentTimeMillis();
    }

    public synchronized void init(Player myself, Player creator, String name, int gameMode, boolean autoFlags) {
        super.init(myself, creator, name, gameMode, autoFlags);
        board.init(Board.ADVANCE);
        join(creator);
        resetBoard(board.getShuffleMines());
    }

    public synchronized void join(Player player) {
        if (player.getSession() != null || players.contains(player)) {
            System.out.println("quit previous session for player" + player.getUsername());
            quit(player);              
        }
        addPlayer(player);
        eventSender.sendCreateSession(player, creator, name, gameMode, autoFlags);
        if (player != creator) {
            eventSender.sendInitBoard(player, board);
        }
        for (Player other : players) {
            eventSender.sendJoinSession(player, other);
            if (other != player) {
                eventSender.sendJoinSession(other, player);
            }
        }
        if (hasStarted()) {
            eventSender.sendRestartBoard(player, board);
        }
        updateLastEventTime();
    }

    public synchronized void quit(Player player) {
        for (Player other : players) {
            eventSender.sendQuitSession(other, player);
        }
        if (player.equals(creator)) {
            this.state = INVALID_STATE;
            for(Player other : new ArrayList<Player>(players)) {
                removePlayer(other);
            }
        } else {
            removePlayer(player);            
        }
    }

    private void addPlayer(Player player) {
        player.setReady(false);
        player.setSession(this);
        players.add(player);
    }

    private void removePlayer(Player player) {
        player.setReady(false);
        player.setGamesWon(0);
        players.remove(player);
        player.setSession(null);
    }

    public synchronized void initBoard(int xSize, int ySize, int numMines) {
        board.init(xSize, ySize, numMines);
        for(Player player : players) {
            if (player != creator) {
                eventSender.sendInitBoard(player, board);
            }
        }
        resetBoard(board.getShuffleMines());
    }

    public synchronized void resetBoard(List<Cell> mines) {
        this.state = GET_READY_STATE;
        board.reset(mines);

        for(Player player : players) {
            player.setReady(false);
            player.setScore(0);
        }
    }

    public void talk(Player player, String message) {
        for(Player other : players) {
            eventSender.sendTalkMessage(other, player, message);
        }
    }

    public synchronized void ready(Player player) {
        updateLastEventTime();
        setReady(player);
        if (!hasStarted()) {
            if (state == GET_READY_STATE && checkAllPlayersAreReady()) {
                countdown();
            }
        } else {
            for(Player other : players) {
                if (other != player && other.isReady()) {
                    eventSender.sendPlayerReady(player, other);
                }
            }
        }
    }

    private boolean checkAllPlayersAreReady() {
        for(Player player: players) {
            if (player.isReady() == false) {
                return false;
            }
        }
        return true;
    }

    private void setReady(Player player) {
        player.setReady(true);
        for(Player other : players) {
            eventSender.sendPlayerReady(other, player);
        }
        if (!hasStarted()) {
            eventSender.sendRestartBoard(player, board);
        }
    }

    private void heartbeat() {
        for(Player player :  new ArrayList<Player>(players)) {
            if (player.getSession() == this) {
                if (player.getLastEventTime() == -1) {
                    System.out.println("Player " + player.getUsername() + " did not respond to last heartbeat");
                    quit(player);
                } else {
                    player.setLastEventTime(-1);
                    eventSender.sendHeartbeat(player);
                }
            }
        }
    }

    private void countdown() {
        updateLastEventTime();
        if (players.size() > 1 && creator.isReady()) {
            for (Player player : players) {
                if (!player.isReady()) {
                    setReady(player);
                }
            }
            this.state = COUNTDOWN_STATE;
            for (Player player : players) {            
                eventSender.sendCountdownGame(player);
            }
        }
    }

    public synchronized void gameOver(Player winner) {
        this.winner = winner;
        for(Player player : players) {
            eventSender.sendGameOver(player, winner);
        }
        winner.incrementGamesWon();
        resetBoard(board.getShuffleMines());
    }

    public void finishGame(Player looser) {
        if (looser.isReady()) {
            looser.setReady(false);
            looser.setScore(0);
            for(Player player : players) {
                eventSender.sendFinishGame(player, looser);
            }
        }
    }

    public boolean hasStarted() {
        return state > COUNTDOWN_STATE;
    }

    private synchronized void uncover(Cell cell, Player byPlayer) {
        board.uncover(cell, byPlayer);
        if (gameMode == UNCOVER_GAME_MODE) {
            byPlayer.incrementScore();
        }
        for(Player player : players) {
            if (player != byPlayer) {
                eventSender.sendUncoverCell(player, cell);
            }
        }
        if (board.getCovered().size() == 0) {
            gameOver(findWinner());
        }
    }

    private synchronized void setFlag(Cell cell, Player byPlayer) {
        board.setFlag(cell, byPlayer);
        if (gameMode == FLAG_GAME_MODE) {
            byPlayer.incrementScore();
        }
        for(Player player : players) {
            if (player != byPlayer) {
                eventSender.sendSetFlag(player, cell);
            }
        }
        if (board.getNumFlagsLeft() == 0) {
            gameOver(findWinner());
        }
    }

    private Player getLastPlayerThatUncovered() {
        ListIterator<Cell> iter = board.getUncovered().listIterator(board.getUncovered().size());
        while(iter.hasPrevious()) {
            Player player = iter.previous().getUncoveredBy();
            if (player != myself) {
                return player;
            }
        }
        return creator; // no want uncovered? well, the creator will be the winner
    }

    private Player findWinner() {
        Player winner = getLastPlayerThatUncovered();
        for(Player player : players) {
            if (player.getScore() > winner.getScore()) {
                winner = player;
            }
        }
        return winner;
    }

    public synchronized void uncoverCell(int x, int y, Player byPlayer) {
        Cell cell = board.getCell(x, y);
        if (cell.isCovered()) {
            updateLastEventTime();
            if (state == OPEN_STATE) {
                state = PLAY_STATE;
            }
            if (cell.hasMine()) {
                finishGame(byPlayer);
            } else {
                uncover(cell , byPlayer);
            }
        }
    }

    public void setFlag(int x, int y, Player byPlayer) {
        Cell cell = board.getCell(x, y);
        if (cell.isCovered()) {
            updateLastEventTime();
            if (state == OPEN_STATE) {
                state = PLAY_STATE;
            }
            if (!cell.hasMine()) {
                finishGame(byPlayer);
            } else {
                setFlag(cell , byPlayer);
            }
        }
    }


    private void uncoverCellByMyself(Cell cell) {
        if (hasStarted()) {
            uncover(cell, myself);
            if (autoFlags) {
                checkAdjacentAutoFlag(cell);
            }
            if (cell.getNumAdjacentMines() == 0) {
                for (Cell adj : cell.getAdjacents()) {
                    if (adj.isCovered()) {
                        uncoverCellByMyself(adj);
                    }
                }
            }
        }
    }

    protected void setFlagByMyself(Cell cell) {
        setFlag(cell, myself);
    }



    private synchronized void uncoverCellByServer() {
        if (board.getSize() > 0) {
            updateLastEventTime();
            uncoverCellByMyself(board.getCovered().get(0));
        }
    }

    public TimerTask getTimerTask() {
        return timerTask;
    }

    private TimerTask timerTask = new TimerTask() {
        public synchronized void run() {
            long elapseTime = System.currentTimeMillis() - lastEventTime;
            if (state == PLAY_STATE) {
                if (elapseTime >= PLAY_ELAPSE_TIME) {
                    uncoverCellByServer();
                }
            } else if (state == OPEN_STATE) {
                if (elapseTime >= OPEN_ELAPSE_TIME) {
                    uncoverCellByServer();
                }
            } else if (state == COUNTDOWN_STATE) {
                if (elapseTime >= COUNTDOWN_ELAPSE_TIME) {
                    state = OPEN_STATE;
                }
            } else if (state == GET_READY_STATE) {
                if (elapseTime >= GET_READY_ELAPSE_TIME) {
                    countdown();
                }
                if (elapseTime % HEARTBEAT_ELAPSE_TIME == 0) {
                    heartbeat();
                }
            }
        }
    };
}
