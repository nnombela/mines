package mines.gui;

import mines.model.Board;
import mines.model.ClientSession;
import mines.model.GameSession;
import mines.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * MinesPanel class
 * <p/>
 * This class is used to ...
 *
 * @author <a href="mailto:nnombela@gmail.com">Nicolas Nombela</a>
 * @since 24-jun-2006
 */
public class MinesPanel extends JPanel implements ActionListener {
    static final String IMAGE_DIR = "/";
    static final String FIELDS_FILE = "fields.gif";
    static final String FIELDS_WHITE_FILE = "fields_white.gif";
    static final String DIGITS_FILE = "digits.gif";
    static final String BUTTONS_FILE = "buttons.gif";

    public static final int MARGIN = 8, BORDER_WIDTH = 2;
    public static final int MULTIPLAYER_PANEL_SIZE = 200;

    private ClientSession session = new ClientSession();
    private ImageLoader images = new ImageLoader();

    public MinesJApplet jApplet;
    public BoardComponent boardComponent;
    public ScoreBoardComponent scoreBoardComponent;
    public MultiplayerPanel multiplayerPanel;

    public MinesPanel(MinesJApplet jApplet, String serverHost, int serverPort, int clientPort) {
        super(null);
        loadImages();
        this.jApplet = jApplet;
        this.boardComponent = new BoardComponent(session, images);
        this.scoreBoardComponent = new ScoreBoardComponent(boardComponent, session, images);
        this.multiplayerPanel = new MultiplayerPanel(session, images, serverHost, serverPort, clientPort);
        add(scoreBoardComponent);
        add(boardComponent);
        add(multiplayerPanel);
        multiplayerPanel.setVisible(true);
        // advance
        init(Board.ADVANCE);
    }

    public GameSession getSession() {
        return this.session;
    }
    
    public void init(int level) {
        if (session.isStandaloneGameMode()) {
            session.getBoard().init(level);
            resize();
            session.restart();
        } else if (session.isSelfCreator()) {
            session.gameOver(null);
            session.getBoard().init(level);
            resize();
            session.getEventSender().sendInitBoard(null, session.getBoard());
        } else {
            System.err.println("Could not init board");
        }
    }

    public void resize() {
        int xSize = images.getImages("fields")[0].getWidth(null) * session.getBoard().getXSize();
        int ySize = images.getImages("fields")[0].getHeight(null) * session.getBoard().getYSize();

        int ySizeStatus = (int)(1.5 * images.getImages("digits")[0].getHeight(null));

        int xSizeMultiplayer = multiplayerPanel.isVisible() ?
                MULTIPLAYER_PANEL_SIZE  + MARGIN + BORDER_WIDTH: 0;

        setSize(new Dimension(xSize + 2 * MARGIN + 2 * BORDER_WIDTH + xSizeMultiplayer,
                ySize + ySizeStatus + 3 * MARGIN + 2 * BORDER_WIDTH));
        setBackground(Color.LIGHT_GRAY);
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        this.scoreBoardComponent.setLocation(MARGIN, MARGIN);
        this.boardComponent.setLocation(MARGIN, 2 * MARGIN + ySizeStatus);
        this.multiplayerPanel.setLocation(2 * MARGIN + xSize + 2 * BORDER_WIDTH, MARGIN);

        this.scoreBoardComponent.setSize(xSize, ySizeStatus);
        this.boardComponent.setSize(xSize, ySize);
        this.multiplayerPanel.setSize(MULTIPLAYER_PANEL_SIZE, ySize + ySizeStatus + MARGIN + 2 * BORDER_WIDTH);
    }
 

    private void loadImages() {
        try {
            images.loadStripImage(IMAGE_DIR, FIELDS_FILE, 16);
            images.loadStripImage(IMAGE_DIR, FIELDS_WHITE_FILE, 16);
            images.loadStripImage(IMAGE_DIR, DIGITS_FILE, 11);
            images.loadStripImage(IMAGE_DIR, BUTTONS_FILE, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        session.resume();
    }

    public void pause() {
        session.pause();
    }

    public void destroy() {
        multiplayerPanel.destroy();
    }

    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        if (source.getActionCommand().equals(MinesJApplet.BEGGINER)) {
            init(Board.BEGGINER);
        } else if (source.getActionCommand().equals(MinesJApplet.MEDIUM)) {
            init(Board.MEDIUM);
        } else if (source.getActionCommand().equals(MinesJApplet.ADVANCE)) {
            init(Board.ADVANCE);
        } else if (source.getActionCommand().equals(MinesJApplet.GIANT)) {
            init(Board.GIANT);
        } else if (source.getActionCommand().equals(MinesJApplet.MULTI_PLAYER)) {
            multiplayerPanel.setVisible(true);
            resize();
        } else if (source.getActionCommand().equals(MinesJApplet.SINGLE_PLAYER)) {
            multiplayerPanel.setVisible(false);
            resize();
        } else if (source.getActionCommand().equals(MinesJApplet.FLAG)) {
            session.setGameMode(GameSession.FLAG_GAME_MODE);
        } else if (source.getActionCommand().equals(MinesJApplet.UNCOVER)) {
            session.setGameMode(GameSession.UNCOVER_GAME_MODE);
        } else if (source.getActionCommand().equals(MinesJApplet.AUTO_FLAG)) {
            if (session.isStandaloneGameMode() ||
                    (session.isSelfCreator() && !session.hasStarted())) {
                session.setAutoFlag(source.isSelected());                
            }
        } else if (source.getActionCommand().equals(MinesJApplet.ABOUT)) {
            session.getEventSender().sendAbout(null);
        }
    }
}
