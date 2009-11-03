package mines.thread;

import mines.net.EventSender;
import mines.net.EventReceiver;
import mines.model.Player;
import mines.model.Board;
import mines.model.Cell;
import mines.model.GameSession;
import mines.gui.MinesPanel;

/**
 * TODO: Implementing this should simplified a lot of code
 * User: nnombela
 * Date: 14-dic-2008
 * Time: 21:38:50
 */
public class StandAloneThead extends Thread implements EventReceiver, EventSender {
    private GameSession session;

    public StandAloneThead(MinesPanel minesPanel) {
        this.session = minesPanel.getSession();
    }

    public void receiveLoginServer(Player from) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void receiveLogoutServer(Player from) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void receiveJoinSession(Player from) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void receiveCreateSession(Player from) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void receiveFinishGame(Player from) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void receiveGameOver(Player from) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void receivePlayerReady(Player from) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void receiveCountdownGame(Player from) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void receiveQuitSession(Player from) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void receiveRestartBoard(Player from) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void receiveInitBoard(Player from) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void receiveTalkMessage(Player from) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void receiveUncoverCell(Player from) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void receiveSetFlag(Player from) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void receiveSessionList(Player from) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void receivePlayerList(Player from) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void receiveHeartbeat(Player from) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void receiveErrorServer(Player from) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void receiveAbout(Player from) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendLoginServer(Player to, String username) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendLogoutServer(Player to) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendSessionList(Player to) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendPlayerList(Player to) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendJoinSession(Player to, Player player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendTalkMessage(Player to, Player from, String message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendCreateSession(Player to, Player creator, String sessionName, int gameMode, boolean autoFlags) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendQuitSession(Player to, Player player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendPlayerReady(Player to, Player player) {
        session.gameOver(null);
        session.resetBoard(session.getBoard().getShuffleMines());
        session.ready(player);
    }

    public void sendInitBoard(Player to, Board board) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendUncoverCell(Player to, Cell cell) {
        if (cell.hasMine()) {
            session.finishGame(to);
            session.gameOver(null);
        } else if (session.getBoard().getCovered().size() == 0) {
            session.gameOver(to);
        }
    }

    public void sendSetFlag(Player to, Cell cell) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendGameOver(Player to, Player winner) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendFinishGame(Player to, Player looser) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendCountdownGame(Player to) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendRestartBoard(Player to, Board board) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendError(Player to, String message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendHeartbeat(Player to) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendAbout(Player to) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
