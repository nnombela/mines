package mines.thread;

import mines.model.*;
import mines.gui.*;
import mines.net.*;

import javax.swing.*;
import java.net.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;


/**
 * ClientUdpThread class
 * <p/>
 * This class is used to ...
 *
 * @author <a href="mailto:nnombela@gmail.com">Nicolas Nombela</a>
 * @since 09-jul-2006
 */
public class ClientThread extends UdpThread implements EventSender, EventReceiver {
    private static final int DEFAULT_RANGE = 5;

    private Player server;
    private GameSession session;
    private MinesPanel minesPanel;
    private NotifyGlassPane glassPane;

    public ClientThread(MinesPanel minesPanel, String serverHost, int serverPort, int clientPort) throws IOException {
        super(clientPort, DEFAULT_RANGE);
        this.server = new Player(new InetSocketAddress(serverHost, serverPort), "server");
        this.minesPanel = minesPanel;
        this.session = minesPanel.getSession();
        this.glassPane = (NotifyGlassPane)minesPanel.jApplet.getGlassPane();
    }


    protected void process(InetSocketAddress address) {
        byte event = buffer.get();
        if (!address.equals(server.getAddress())) {
            System.err.println("Received event from unknown address " + address);
            return;
        }

        switch(event) {
            case mines.net.Event.UNCOVER_CELL:
                if (session.isStandaloneGameMode()) {
                    sendLogoutServer(null);
                } else {
                    receiveUncoverCell(null);
                    minesPanel.boardComponent.repaint();
                }
                return;
            case mines.net.Event.SET_FLAG:
                if (session.isStandaloneGameMode()) {
                    sendLogoutServer(null);
                } else {
                    receiveSetFlag(null);
                    minesPanel.boardComponent.repaint();
                }
                return;
            case mines.net.Event.LOGIN_SERVER:
                receiveLoginServer(null);
                break;
            case mines.net.Event.LOGOUT_SERVER:
                receiveLogoutServer(null);
                break;
            case mines.net.Event.RESTART_BOARD:
                receiveRestartBoard(null);
                break;
            case mines.net.Event.INIT_BOARD:
                receiveInitBoard(null);
                break;
            case mines.net.Event.PLAYER_LIST:
                receivePlayerList(null);
                break;
            case mines.net.Event.SESSION_LIST:
                receiveSessionList(null);
                break;
            case mines.net.Event.PLAYER_READY:
                receivePlayerReady(null);
                break;
            case mines.net.Event.JOIN_SESSION:
                receiveJoinSession(null);
                break;
            case mines.net.Event.CREATE_SESSION:
                receiveCreateSession(null);
               break;
            case mines.net.Event.TALK_MESSAGE:
                receiveTalkMessage(null);
                minesPanel.multiplayerPanel.chatPanel.repaint();
                return;
            case mines.net.Event.QUIT_SESSION:
                receiveQuitSession(null);
               break;
            case mines.net.Event.FINISH_GAME:
                receiveFinishGame(null);
               break;
            case mines.net.Event.COUNTDOWN_GAME:
                receiveCountdownGame(null);
               break;
            case mines.net.Event.GAME_OVER:
                receiveGameOver(null);
               break;
            case mines.net.Event.HEART_BEAT:
                receiveHeartbeat(null);
               break;
            case mines.net.Event.ABOUT:
                receiveAbout(null);
               break;
            case mines.net.Event.ERROR_SERVER:
                receiveErrorServer(null);
               break;
            default:
                System.err.println("Unknown event " + event);
        }
        minesPanel.repaint();
    }

    public void receiveLoginServer(Player from){
        System.out.println("LOGIN EVENT");
        final String username = buffer.getString();
        session.getMyself().setUsername(username);
        session.setLogged(true);

        EventQueue.invokeLater(new Runnable() {
             public void run() {
                 MultiplayerPanel.JoinPanel joinPanel = minesPanel.multiplayerPanel.sessionPanel.joinPanel;
                 joinPanel.loginLogoutButton.setText("Logout");
                 joinPanel.usernameTextField.setEnabled(false);
                 joinPanel.sessionNameTextField.setEnabled(true);
                 joinPanel.searchButton.setEnabled(true);
                 glassPane.showMultipleMessage(new String[] {
                         "You are now logged in as " + username,
                         "You can search for sessions or create a new one"});
             }
        });
    }

    public void receiveLogoutServer(Player from) {
        System.out.println("LOGOUT EVENT");
        if (session.hasStarted()) {
            session.gameOver(null);
        }
        session.setGameMode(GameSession.STANDALONE_GAME_MODE);
        session.setLogged(false);

        EventQueue.invokeLater(new Runnable() {
             public void run() {
                 MultiplayerPanel.JoinPanel joinPanel = minesPanel.multiplayerPanel.sessionPanel.joinPanel;
                 joinPanel.loginLogoutButton.setText("Login");
                 joinPanel.usernameTextField.setEnabled(true);
                 joinPanel.sessionNameTextField.setEnabled(false);
                 joinPanel.createButton.setEnabled(false);
                 joinPanel.searchButton.setEnabled(false);
                 glassPane.showMessage("You are now logged out", 3);
             }
        });
    }

    public void receiveJoinSession(Player from) {
        //System.out.println("JOIN SESSION EVENT");
        final Player player = buffer.getPlayer(false);
        session.join(player);
        final Player joined = session.findPlayer(player.getId());
        joined.setReady(buffer.getBoolean());
        joined.setGamesWon(buffer.getInt());

        EventQueue.invokeLater(new Runnable() {
             public void run() {
                 MultiplayerPanel.SessionPanel sessionPanel = minesPanel.multiplayerPanel.sessionPanel;
                 sessionPanel.playPanel.listModel.addElement(joined);
                 sessionPanel.setVisible(sessionPanel.playPanel);
             }
        });
    }

    public void receiveCreateSession(Player from) {
        //System.out.println("CREATE SESSION EVENT");
        final Player self = buffer.getPlayer(false);
        final Player creator = buffer.getPlayer(false);
        final String sessionName = buffer.getString();
        final int gameMode = buffer.getInt();
        final boolean autoFlags = buffer.getBoolean();
        if (session.hasStarted()) {
            session.gameOver(null);
        }
        session.init(self, creator, sessionName, gameMode, autoFlags);

        EventQueue.invokeLater(new Runnable() {
             public void run() {
                 minesPanel.jApplet.modeMenuItems[gameMode ==  GameSession.FLAG_GAME_MODE ? 0 : 1].setSelected(true);
                 minesPanel.jApplet.autoFlagsMenuItem.setSelected(autoFlags);
                 minesPanel.jApplet.setEnabledButtons(false);
                 MultiplayerPanel.SessionPanel sessionPanel = minesPanel.multiplayerPanel.sessionPanel;
                 sessionPanel.playPanel.playersLabel.setText(
                         session.getName() + " by " + session.getCreator().getUsername());
                 sessionPanel.setVisible(sessionPanel.playPanel);

                 if (session.isSelfCreator()) {
                     glassPane.showMessage("You have created the session " + sessionName, 3);
                 } else {
                     String remember = session.getGameMode() == GameSession.FLAG_GAME_MODE? 
                             "Remember: You score one point for every flag set"
                           : "Remember: You score one point for every cell uncover";
                     glassPane.showMultipleMessage(new String[] {
                             "You have joined the session " + sessionName ,
                             remember,
                             "Press the smile button to get ready"});
                 }
             }
        });
    }

    public void receiveGameOver(Player from) {
        //System.out.println("GAME OVER EVENT");
        final int id = buffer.getInt();
        final Player winner = (server.getId() == id) ? server : session.findPlayer(id);
        final Player myself = session.getMyself();

        if (winner == null) {
            System.err.println("Received game over, but session has not started");
            return;
        }

        session.gameOver(winner);

        EventQueue.invokeLater(new Runnable() {
             public void run() {
                 if (myself.equals(winner)) {
                     glassPane.showMessage("Congratulations! You are the winner", 6);
                 } else {
                     glassPane.showMessage("Game over! The winner is " + winner.getUsername(), 6);
                 }
             }
        });
    }

    public void receiveFinishGame(Player from) {
        //System.out.println("FINISH GAME EVENT");
        final int id = buffer.getInt();
        final Player looser = session.findPlayer(id);
        final Player myself = session.getMyself();

        session.finishGame(looser);

        EventQueue.invokeLater(new Runnable() {
             public void run() {
                 if (myself.equals(looser)) {
                     glassPane.showMultipleMessage(new String[] {
                             "You have lost your points!",
                             "Press the smile button to rejoin the game"});
                 }
             }
        });
    }

    public void receivePlayerReady(Player from) {
        //System.out.println("PLAYER READY EVENT");
        final int id = buffer.getInt();
        final Player player = session.findPlayer(id);
        int score = buffer.getInt();
        player.setScore(score);
        session.ready(player);
    }

    public void receiveCountdownGame(Player from) {
        //System.out.println("COUNTDOWN GAME EVENT");
        EventQueue.invokeLater(new Runnable() {
             public void run() {
                 glassPane.showCountdown();
             }
        });
    }

    public void receiveQuitSession(Player from) {
        //System.out.println("QUIT SESSION EVENT");
        final int id = buffer.getInt();
        final Player player = session.findPlayer(id);
        final Player creator = session.getCreator();
        final Player myself = session.getMyself();
        final boolean hasStarted = session.hasStarted();

        if (player == null) {
            System.err.println("Received quit session, but session has not started");
            return;
        }

        session.quit(player);

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                MultiplayerPanel.SessionPanel sessionPanel = minesPanel.multiplayerPanel.sessionPanel;
                if (player.equals(myself) || player.equals(creator)) {
                    sessionPanel.playPanel.listModel.clear();
                    sessionPanel.playPanel.renderer.clearCache();
                    sessionPanel.setVisible(sessionPanel.joinPanel);
                } else {
                    sessionPanel.playPanel.listModel.removeElement(player);
                }
                if (player.equals(myself)) {
                    glassPane.showMessage("You have quit the session", 3);
                    minesPanel.jApplet.setEnabledButtons(true);
                } else
                if (player.equals(creator)) {
                    glassPane.showMessage("Session creator " + player.getUsername() + " has ended the session", 3);
                    minesPanel.jApplet.setEnabledButtons(true);
                } else
                if (!hasStarted) {
                    glassPane.showMessage("Player " + player.getUsername() + " has quit the session", 1);
                }
             }
        });



    }

    public void receiveRestartBoard(Player from) {
        //System.out.println("RESTART BOARD EVENT");
        int minesLength = buffer.getInt();
        List<Cell> mines = new ArrayList<Cell>(minesLength);
        Board board = session.getBoard();
        for(int i = 0; i < minesLength; ++i) {
            int x = buffer.getInt();
            int y = buffer.getInt();
            mines.add(board.getCell(x, y));
        }
        session.resetBoard(mines);

        int uncoverLength = buffer.getInt();
        for(int i = 0; i < uncoverLength; ++i) {
            receiveUncoverCell(from);
        }
        int flagsLength = buffer.getInt();
        for(int i = 0; i < flagsLength; ++i) {
            receiveSetFlag(from);
        }
    }

    public void receiveInitBoard(Player from) {
        //System.out.println("INIT BOARD EVENT");
        final int xBoardSize = buffer.getInt();
        final int yBoardSize = buffer.getInt();
        final int numMines = buffer.getInt();

        if (session.hasStarted()) {
            session.gameOver(null);
        }

        session.initBoard(xBoardSize, yBoardSize, numMines);

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                int levelIndex = numMines == 10? 0 : numMines == 40? 1 : numMines == 99? 2 : numMines == 220? 3 : 0;
                minesPanel.jApplet.levelMenuItems[levelIndex].setSelected(true);
                minesPanel.resize();
            }
        });
    }

    public void receiveTalkMessage(Player from) {
        //System.out.println("TALK EVENT");
        final int id = buffer.getInt();
        final String message = buffer.getString();
        final Player player = session.findPlayer(id);
        final Player myself = session.getMyself();

        session.talk(player, message);

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                DefaultListModel listModel = minesPanel.multiplayerPanel.chatPanel.listModel;
                if (player.equals(myself)) {
                    listModel.addElement(">> " + message);
                } else {
                    listModel.addElement("> " + message);
                }
            }
        });
    }

    public void receiveUncoverCell(Player from) {
        //System.out.println("UNCOVER CELL EVENT");
        int x = buffer.getInt();
        int y = buffer.getInt();
        int id = buffer.getInt();
        Player uncoveredBy = (server.getId() == id) ? server : session.findPlayer(id);
        if (uncoveredBy != null) {
            session.uncoverCell(x, y, uncoveredBy);
        } else {
            System.err.println("Received uncover cell by player with unknown id" + id);
        }
    }

    public void receiveSetFlag(Player from) {
        //System.out.println("SET FLAG EVENT");
        int x = buffer.getInt();
        int y = buffer.getInt();
        int id = buffer.getInt();
        Player setFlagBy = (server.getId() == id) ? server : session.findPlayer(id);
        if (setFlagBy != null) {
            session.setFlag(x, y, setFlagBy);
        } else {
            System.err.println("Received set flag by player with unknown id" + id);
        }
    }

    public void receiveSessionList(Player from) {
        //System.out.println("SESSION LIST EVENT");
        final int size = buffer.getInt();
        final DefaultListModel listModel = new DefaultListModel();
        for(int i = 0; i < size; ++i) {
            String name = buffer.getString();
            Player creator = buffer.getPlayer(false);
            int numPlayers = buffer.getInt();
            boolean hasStarted = buffer.getBoolean();
            listModel.addElement(new PlayerSession(name, creator, numPlayers, hasStarted));
        }
        EventQueue.invokeLater(new Runnable() {
             public void run() {
                 minesPanel.multiplayerPanel.sessionPanel.findSessionPanel.setListModel(listModel);
             }
        });
    }

    public void receivePlayerList(Player from) {
        //System.out.println("PLAYER LIST EVENT");
        final int size = buffer.getInt();
        final DefaultListModel listModel = new DefaultListModel();
        for(int i = 0; i < size; ++i) {
            Player player = buffer.getPlayer(false);
            String name = buffer.getString();
            if (!name.equals("NULL")) {
                player.setSession(new PlayerSession(name));
            }
            listModel.addElement(player);
        }
        EventQueue.invokeLater(new Runnable() {
             public void run() {
                 minesPanel.multiplayerPanel.findPlayerPanel.setListModel(listModel);
             }
        });
    }

    public void receiveAbout(Player from) {
        final String[] about = buffer.getMessage();

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                glassPane.showMultipleMessage(about);
            }
        });
    }

    public void receiveHeartbeat(Player from) {
        session.getEventSender().sendHeartbeat(null);
    }

    public void receiveErrorServer(Player from) {
        //System.out.println("ERROR SERVER EVENT");
        final String message = buffer.getString();
        System.err.println("Error: " + message);

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                glassPane.showMessage("Error message: " + message, 3);
            }
        });

        if (session.isLogged() && message.equals(mines.net.Event.NOT_LOGGED_ERROR_CODE)) {
            receiveLogoutServer(from);
        }
    }

    //--------------------------------------------------------------------------

    private void sendEvent(byte event, Object obj) {
        buffer.put(event);
        if (obj instanceof String) {
            buffer.putString((String)obj);
        } else if (obj instanceof Player) {
            buffer.putPlayer((Player)obj);
        }
        send(server.getAddress());
    }

    public void sendLoginServer(Player to, String username) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                glassPane.showWaitingMessage();
            }
        });
        sendEvent(mines.net.Event.LOGIN_SERVER, username);
    }

    public void sendLogoutServer(Player to) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                glassPane.showWaitingMessage();
            }
        });
        sendEvent(mines.net.Event.LOGOUT_SERVER, null);
    }

    public void sendSessionList(Player to) {
        sendEvent(mines.net.Event.SESSION_LIST, null);
    }

    public void sendPlayerList(Player to) {
        sendEvent(mines.net.Event.PLAYER_LIST, null);
    }

    public void sendJoinSession(Player to, Player creator) {
        sendEvent(mines.net.Event.JOIN_SESSION, creator);
    }

    public void sendTalkMessage(Player to, Player player, String message) {
        sendEvent(mines.net.Event.TALK_MESSAGE, message);
    }

    public void sendCreateSession(Player to, Player player, String sessionName, int gameMode, boolean autoFlags) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                glassPane.showWaitingMessage();
            }
        });

        buffer.put(mines.net.Event.CREATE_SESSION);
        buffer.putPlayer(server);
        buffer.putString(sessionName);
        buffer.putInt(gameMode);
        buffer.putBoolean(autoFlags);
        send(server.getAddress());
    }

    public void sendQuitSession(Player to, final Player player) {
        final String message = player.equals(session.getMyself()) ? "Are you sure you want to quit the session" :
               "Are you sure you want " + player.getUsername() + " to be kicked";

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                glassPane.showConfirm(message,
                    new NotifyGlassPane.CounterCommand() {
                        public void accept(NotifyGlassPane panel) {
                            sendEvent(mines.net.Event.QUIT_SESSION, player);
                        }
                    });
            }
        }); 
    }

    public void sendPlayerReady(Player to, Player player) {
        //getGlassPane().showWaitingMessage();
        sendEvent(mines.net.Event.PLAYER_READY, null);
    }

    public void sendInitBoard(Player to, Board board) {
        buffer.put(mines.net.Event.INIT_BOARD);
        buffer.putInt(board.getXSize());
        buffer.putInt(board.getYSize());
        buffer.putInt(board.getNumMines());
        send(server.getAddress());
    }

    public void sendUncoverCell(Player to, Cell cell) {
        buffer.put(mines.net.Event.UNCOVER_CELL);
        buffer.putInt(cell.getX());
        buffer.putInt(cell.getY());
        buffer.putInt(cell.getUncoveredBy().getId());
        send(server.getAddress());
    }

    public void sendSetFlag(Player to, Cell cell) {
        buffer.put(mines.net.Event.SET_FLAG);
        buffer.putInt(cell.getX());
        buffer.putInt(cell.getY());
        buffer.putInt(cell.getSetFlagBy().getId());
        send(server.getAddress());
    }


    public void sendHeartbeat(Player to) {
        buffer.put(mines.net.Event.HEART_BEAT);
        send(server.getAddress());
    }

    public void sendAbout(Player to) {
        buffer.put(mines.net.Event.ABOUT);
        send(server.getAddress());
    }

    public void sendGameOver(Player to, Player winner) {
        //Todo
    }

    public void sendFinishGame(Player to, Player looser) {
        //Todo
    }

    public void sendCountdownGame(Player to) {
        //Todo
    }

    public void sendRestartBoard(Player to, Board board) {
        //Todo
    }

    public void sendError(Player to, String message) {
        //Todo
    }
}
