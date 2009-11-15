package mines.model;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * ClientSession - This class models...
 * @author <a href="mailto:nnombela@gmail.com">Nicolas Nombela</a>
 * @version 1.0
 */
public class ClientSession extends GameSession implements ActionListener {
    private List<Cell> queue = new ArrayList<Cell>();
    private Timer timer;
    private int elapseTime;

    public ClientSession() {
        this.timer = new Timer(1000, this);
        this.board = new Board();
        this.players = new ArrayList<Player>(2);
        init();
    }

    private void init() {
        Player myself = new Player(1, null, "myself");
        init(myself, myself, "local", GameSession.STANDALONE_GAME_MODE, false);
    }

    public synchronized void init(Player myself, Player creator, String name, int gameMode, boolean autoFlags) {
        super.init(myself, creator, name, gameMode, autoFlags);
        join(myself);
        join(creator);
    }

    public synchronized void join(Player player) {
        if (player != null && findPlayer(player.getId()) == null) {
            players.add(player);
            player.setSession(this);
        }
    }

    public synchronized void quit(Player player) {
        if (player.equals(creator) || player.equals(myself)) {
            gameOver();
            init();
        } else {
            players.remove(player);
        }
    }

    public synchronized void ready(Player player) {
        if (players.contains(player)) {
            player.setReady(true);
        }
        if (player.equals(myself)) {
            winner = null;
        }
        if (!timer.isRunning()) {
            elapseTime = 0;
        }
        cleanBoard();
    }

    private void cleanBoard() {
        for(Cell cell : board.getMines()) {
            if (cell.isUncovered()) {
                board.uncover(cell, null);
                queue.add(cell);
            }
        }
        for(Cell cell : board.getCovered()) {
            if (cell.hasFlag()) {
                board.removeFlag(cell);
                queue.add(cell);
            }
        }
    }

    public void uncoverCell(int x, int y, Player by) {
        uncoverByOthers(board.getCell(x, y), by);
     }

    public void setFlag(int x, int y, Player by) {
        setFlag(board.getCell(x, y), by);
    }

    public synchronized void initBoard(int xSize, int ySize, int numMines) {
        board.init(xSize, ySize, numMines);
        addAllToQueue();
    }

   
    public synchronized void resetBoard(List<Cell> mines) {
        board.reset(mines);
        addAllToQueue();
    }

    public void talk(Player player, String message) {
        //Todo
    }

    public void finishGame(Player looser) {
        looser.setReady(false);
        looser.setScore(0);
        if (isStandaloneGameMode()) {
            gameOver();
        }
    }

    public void gameOver(Player winner) {
        if (winner != null) {
            winner.incrementGamesWon();
        }
        for(Player player : players) {
            player.setReady(false);
        }
        this.winner = winner;
        gameOver();
    }

    public void addTimerActionListener(ActionListener listener) {
        timer.addActionListener(listener);
    }

    public int getElapseSeconds() {
        return elapseTime;
    }


    public synchronized void pause() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }

    public synchronized void resume() {
        if (!timer.isRunning() && getMyself().isReady() && hasStarted()) {
            timer.start();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.timer) {
            ++this.elapseTime;
        }
    }

    public void addAllToQueue() {
        this.queue = board.getAllCells();
    }

    public List<Cell> getQueue() {
        return this.queue;
    }

    public List<Cell> drainQueue() {
        List<Cell> queue = this.queue;
        this.queue = new ArrayList<Cell>(board.getSize());
        return queue;
    }

    public synchronized void restart() {
        if (isStandaloneGameMode()) {
            gameOver();
            resetBoard(board.getShuffleMines());
            ready(getMyself());
        } else {
            getEventSender().sendPlayerReady(null, getMyself());
        }
    }

    public void toggleFlag(Cell cell) {
        if (cell.isCovered()) {
            if (!cell.hasFlag()) {
                setFlagByMyself(cell);
            } else {
                if (getGameMode() != GameSession.FLAG_GAME_MODE) {
                    board.removeFlag(cell);
                    queue.add(cell);
                }
            }
        }
    }

    protected void setFlagByMyself(Cell cell) {
        if (getGameMode() == GameSession.FLAG_GAME_MODE) {
            if (myself.isReady() && hasStarted()) {
                setFlag(cell, myself);
                getEventSender().sendSetFlag(null, cell);
                if (!cell.hasMine()) {
                    finishGame(myself);
                }
            }
        } else {
            setFlag(cell, myself);
        }
    }

    public void setFlag(Cell cell, Player by) {
        if (getGameMode() == GameSession.FLAG_GAME_MODE) {
            updateScoreFlags(cell, by);
        }
        board.setFlag(cell, by);
        queue.add(cell);
    }

    public void gameOver() {
        timer.stop();
        if (isWinnerMyself()) {
            board.setFlagOnMines(myself);
        }
        addAllToQueue();
    }

    public synchronized void uncoverByMyself(Cell cell) {
        if (isStandaloneGameMode() || (myself.isReady() && hasStarted())) {
            uncoverByMyself(cell, myself);
        } else {
            getEventSender().sendUncoverCell(null, new Cell(cell.getX(), cell.getY(), myself));
        }
    }

    public void uncoverByMyself(Cell cell, Player player) {
        if (cell.isCovered() && !cell.hasFlag() && player.isReady()) {
            uncover(cell, player);
            if (autoFlags) {
                checkAdjacentAutoFlag(cell);
            }
            if (isStandaloneGameMode()) {
                if (cell.hasMine()) {
                    finishGame(player);
                } else if (board.getCovered().size() == 0) {
                    gameOver(player);
                }
            } else {
                getEventSender().sendUncoverCell(null, cell);
            }
            if (cell.getNumAdjacentMines() == 0) {
                for (Cell adj : cell.getAdjacents()) {
                    uncoverByMyself(adj, player);
                }
            }
        }
    }



    public synchronized void uncoverByOthers(Cell cell, Player uncoveredBy) {
        uncover(cell, uncoveredBy);
        if (cell.getNumAdjacentMines() == 0) {
            for (Cell adj : cell.getAdjacents()) {
                if (adj.isCovered()) {
                    uncoverByOthers(adj, uncoveredBy);
                }
            }
        }
    }

    public synchronized void uncoverAdjacentsIfMinesEqualsFlags(Cell cell) {
        if (cell.isUncovered() && cell.isAdjacentMinesEqualFlags()) {
            for (Cell adj : cell.getAdjacents()) {
                uncoverByMyself(adj);
            }
        }
    }


    public void uncover(Cell cell, Player uncoveredBy) {
        if (board.getUncovered().size()  == 0) {
            timer.start();
            elapseTime = 0;            
        }
        if (cell.hasFlag()) {
            board.removeFlag(cell);  // maybe should be game over
        }
        if (getGameMode() == GameSession.UNCOVER_GAME_MODE) {
            updateScoreUncover(cell, uncoveredBy);
        }
        board.uncover(cell, uncoveredBy);
        queue.add(cell);
    }

    private void updateScoreUncover(Cell cell, Player player) {
        if (cell.uncoveredBy != null) {
            cell.uncoveredBy.decrementScore();
        }
        if (!cell.hasMine) {
            player.incrementScore();
        }
    }

    private void updateScoreFlags(Cell cell, Player by) {
        if (cell.setFlagBy != null) {
            cell.setFlagBy.decrementScore();
        }
        if (cell.hasMine) {
            by.incrementScore();
        }
    }


}
