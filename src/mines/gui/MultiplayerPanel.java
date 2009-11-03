/**
 * File Description -
 * Date: 19-nov-2006
 */
package mines.gui;

import mines.model.ClientSession;
import mines.model.Player;
import mines.model.GameSession;
import mines.thread.ClientThread;
import mines.util.ImageLoader;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * MultiplayerPanel - This class models...
 *
 * @author <a href="mailto:nnombela@gmail.com">Nicolas Nombela</a>
 * @version 1.0
 */
public class MultiplayerPanel extends JPanel {
    private ClientSession session;
    private ImageLoader images;
    private ClientThread thread;

    public SessionPanel sessionPanel;
    public ChatPanel chatPanel;
    public FindPlayerPanel findPlayerPanel;

    public JTabbedPane tabbedPane;
    private Border focusGained, focusLost, emptyBorder;

    private String serverHost;
    private int serverPort, clientPort;

    public MultiplayerPanel(ClientSession session, ImageLoader images, String serverHost, int serverPort, int clientPort) {
        this.session = session;
        this.images = images;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.clientPort = clientPort;
        UIManager.put("TextField.disabledBackground", new ColorUIResource(210, 210, 210));
        UIManager.put("TextField.inactiveForeground", new ColorUIResource(150, 150, 150));
        //UIManager.put("Button.font", new FontUIResource("Dialog",Font.BOLD, 12));
        createGui();
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            createThread();
        } else {
            destroy();
        }
    }

    public void destroy() {
        if (thread != null && session.isLogged()) {
            thread.sendLogoutServer(null);            
        }
        destroyThread();
    }

    private void createThread() {
        if (thread == null || !thread.isAlive()) {
            try {
                thread = new ClientThread((MinesPanel)getParent(), serverHost, serverPort, clientPort);
                session.setEventSender(thread);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();  //Todo
            }
        }
    }

    private void destroyThread() {
        if (thread != null) {
            session.setEventSender(null);
            thread.halt();
            thread = null;
        }
    }

    private void createGui() {
        setOpaque(true);
        setBackground(Color.LIGHT_GRAY);
        setBorder(BorderFactory.createLoweredBevelBorder());
        tabbedPane = new JTabbedPane();
        this.emptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        this.focusGained = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(tabbedPane.getBackground(), 3), emptyBorder);
        this.focusLost = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBackground().darker()), emptyBorder);
        sessionPanel = new SessionPanel();
        tabbedPane.addTab("Play", null, sessionPanel, "Tooltip for Play tab");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_P);
                
        chatPanel = new ChatPanel();
        tabbedPane.addTab("Chat", null, chatPanel, "Tooltip for Chat tab");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_C);

        findPlayerPanel = new FindPlayerPanel();
        tabbedPane.addTab("Find", null, findPlayerPanel, "Tooltip for Find tab");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_F);

        tabbedPane.setSelectedIndex(0);

        setLayout(new GridLayout(1, 1)); 
        add(tabbedPane);
    }

    public void setSize(int xSize, int ySize) {
        super.setSize(xSize, ySize);
        sessionPanel.setSize(xSize, ySize);
        //joinPanel.setSize(xSize, ySize);
        chatPanel.setSize(xSize, ySize);
        findPlayerPanel.setSize(xSize, ySize);
    }

    public static void setBounds(JButton[] buttons) {
        Dimension size = buttons[0].getParent().getSize();
        int length = (size.width - 10 - (buttons.length + 1) * 5) / buttons.length;
        for(int i = 0; i < buttons.length; ++i) {
            buttons[i].setBounds((i + 1) * 5 + i * length, size.height - 65, length, 30);
        }
    }

    public static void enterPressesWhenFocused(JComponent component) {
        component.registerKeyboardAction(
            component.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);

        component.registerKeyboardAction(
            component.getActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                JComponent.WHEN_FOCUSED);
    }

    //
    public class SessionPanel extends JPanel {
        public JoinPanel joinPanel = new JoinPanel();
        public FindSessionPanel findSessionPanel = new FindSessionPanel();
        public PlayPanel playPanel = new PlayPanel();
        
        SessionPanel() {
            super(null);
            setBackground(Color.LIGHT_GRAY);
            add(joinPanel);
            add(findSessionPanel);
            add(playPanel);

            setVisible(joinPanel);
        }

        public void setSize(int xSize, int ySize) {
            joinPanel.setSize(xSize, ySize);
            findSessionPanel.setSize(xSize, ySize);
            playPanel.setSize(xSize, ySize);
        }

        public void setVisible(JPanel panel) {
            joinPanel.setVisible(panel == joinPanel);
            findSessionPanel.setVisible(panel == findSessionPanel);
            playPanel.setVisible(panel == playPanel);
        }
    }

    // ----------- Login Panel ---------

    public class FindSessionPanel extends JPanel implements ActionListener {
        private JLabel label;
        public ListModel listModel = new DefaultListModel();
        private JList list;
        private JScrollPane scrollPane;
        private JButton searchButton;
        private JButton joinButton;
        private JButton backButton;

        FindSessionPanel() {
            super(null);
            setOpaque(true);
            setBackground(Color.LIGHT_GRAY);

            createGui();
        }

        public void setSize(int xSize, int ySize) {
            super.setSize(xSize, ySize);
            label.setBounds(5, 0, xSize - 20 , 20);
            scrollPane.setBounds(0, 20, xSize - 6, ySize - 90);
            MultiplayerPanel.setBounds(new JButton[] {backButton, searchButton, joinButton});
        }

        private void createGui() {
            label = new JLabel("Sessions found", JLabel.CENTER);
            add(label);

            list = new JList(listModel);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setCellRenderer(new SessionListRenderer());
            scrollPane = new JScrollPane(list);
            add(scrollPane);

            searchButton = new RoundButton("Search");
            searchButton.setBorder(emptyBorder);
            add(searchButton);
            searchButton.addActionListener(this);

            joinButton = new RoundButton("Join");
            joinButton.setBorder(emptyBorder);
            add(joinButton);
            joinButton.addActionListener(this);

            backButton = new RoundButton("Back");
            backButton.setBorder(emptyBorder);
            add(backButton);
            backButton.addActionListener(this);
        }

        public void setListModel(ListModel listModel) {
            this.listModel = listModel;
            list.setModel(listModel);
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Search")) {
                thread.sendSessionList(null);
            } else
            if (e.getActionCommand().equals("Join")) {
                if (list.getSelectedIndex() != -1) {
                    GameSession session = (GameSession)list.getSelectedValue();
                    thread.sendJoinSession(null, session.getCreator());
                }
            } else
            if (e.getActionCommand().equals("Back")) {
                SessionPanel sessionPanel = (SessionPanel)getParent();
                sessionPanel.setVisible(sessionPanel.joinPanel);
            }
        }
    }

    public class FindPlayerPanel extends JPanel implements ActionListener {
        private JLabel label;
        public ListModel listModel = new DefaultListModel();
        private JList list;
        private JScrollPane scrollPane;
        private JButton searchButton;
        //private JButton inviteButton;

        FindPlayerPanel() {
            super(null);
            setOpaque(true);
            setBackground(Color.LIGHT_GRAY);

            createGui();
        }

        public void setListModel(ListModel listModel) {
            this.listModel = listModel;
            list.setModel(listModel);
        }

        public void setSize(int xSize, int ySize) {
            super.setSize(xSize, ySize);
            label.setBounds(5, 0, xSize - 20 , 20);
            scrollPane.setBounds(0, 20, xSize - 6, ySize - 90);
            MultiplayerPanel.setBounds(new JButton[] {searchButton});
        }

        private void createGui() {
            label = new JLabel("Players found", JLabel.CENTER);
            add(label);

            list = new JList(listModel);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setCellRenderer(new PlayerListRenderer());
            scrollPane = new JScrollPane(list);
            add(scrollPane);

            searchButton = new RoundButton("Search for players");
            searchButton.setBorder(emptyBorder);
            add(searchButton);
            searchButton.addActionListener(this);

//            inviteButton = new RoundButton("Invite");
//            inviteButton.setBorder(emptyBorder);
//            add(inviteButton);
//            inviteButton.addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(searchButton)) {
                thread.sendPlayerList(null);
            }
//            else if (e.getSource().equals(inviteButton)) {
//                if (list.getSelectedIndex() != -1) {
//                    Player player = (Player)list.getSelectedValue();
//                    //thread.sendJoinSession(session.getCreator());
//                }
//            }
        }
    }


    // ----------- Chat Panel ----------

    public class ChatPanel extends JPanel implements ListSelectionListener, ActionListener {
        public DefaultListModel listModel = new DefaultListModel();
        private JList messages;
        private JScrollPane scrollPane;

        private JTextField talkTextField;
        private JButton talkButton;

        ChatPanel() {
            super(null);
            setOpaque(true);
            setBackground(Color.LIGHT_GRAY);

            createGui();
        }

        public void setSize(int xSize, int ySize) {
            super.setSize(xSize, ySize);
            scrollPane.setBounds(0, 0, xSize - 6, ySize - 70);

            talkTextField.setBounds(3, ySize - 66, xSize - 60, 30);
            talkButton.setBounds(xSize - 52, ySize - 66, 40, 30);
        }

        private void createGui() {
            messages = new JList(listModel);
            messages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            messages.setCellRenderer(new ChatListRenderer());
            messages.addListSelectionListener(this);
            scrollPane = new JScrollPane(messages);
            add(scrollPane);

            talkTextField = new JTextField("");
            talkTextField.setBorder(focusLost);
            add(talkTextField);
            talkTextField.addActionListener(this);
            talkTextField.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    talkTextField.setText("");
                    talkTextField.setBorder(focusGained);
                }
                public void focusLost(FocusEvent e) {
                    talkTextField.setBorder(focusLost);
                }
            });
            talkButton = new RoundButton("Talk");
            talkButton.setBorder(emptyBorder);
            add(talkButton);
            talkButton.addActionListener(this);

            enterPressesWhenFocused(talkButton);
        }

        public void valueChanged(ListSelectionEvent e) {
            //Todo
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(talkButton) || e.getSource().equals(talkTextField)) {
                if (!talkTextField.getText().equals("")) {
                    thread.sendTalkMessage(null, null, talkTextField.getText());
                    talkTextField.setText("");
                }
            }
        }
    }

    // ----------- Join Panel -----------

    public class JoinPanel extends JPanel  implements ActionListener {
        public JTextField usernameTextField;
        public JButton loginLogoutButton;

        public JTextField sessionNameTextField;
        public JButton createButton;
        public JButton searchButton;



        JoinPanel() {
            super(null);
            setOpaque(true);
            setBackground(Color.LIGHT_GRAY);

            createGui();
        }

        public void setSize(int xSize, int ySize) {
            super.setSize(xSize, ySize);
            usernameTextField.setBounds(5, 10, xSize - 75, 30);
            loginLogoutButton.setBounds(xSize - 67, 10, 55, 30);

            sessionNameTextField.setBounds(5, 50, xSize - 75, 30);
            createButton.setBounds(xSize - 67, 50, 55, 30);

            MultiplayerPanel.setBounds(new JButton[] {searchButton});
        }

        private void createGui() {
            usernameTextField = new JTextField("Type screen name");
            usernameTextField.setBorder(focusLost);
            //usernameTextField.setFont(getFont().deriveFont(Font.BOLD, 12f));
            usernameTextField.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    usernameTextField.setText("");
                    usernameTextField.setBorder(focusGained);
                    loginLogoutButton.setEnabled(true);
                }
                public void focusLost(FocusEvent e) {
                    usernameTextField.setBorder(focusLost);
                }
            });
            add(usernameTextField);
            usernameTextField.addActionListener(this);

            loginLogoutButton = new RoundButton("Login");
            loginLogoutButton.setBorder(emptyBorder);
            loginLogoutButton.setEnabled(false);
            add(loginLogoutButton);
            loginLogoutButton.addActionListener(this);

            sessionNameTextField = new JTextField("Type session name");
            sessionNameTextField.setBorder(focusLost);
            //sessionNameTextField.setFont(getFont().deriveFont(Font.BOLD, 12f));
               sessionNameTextField.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    sessionNameTextField.setText("");
                    sessionNameTextField.setBorder(focusGained);
                    createButton.setEnabled(true);
                }
                public void focusLost(FocusEvent e) {
                    sessionNameTextField.setBorder(focusLost);
                }
            });
            sessionNameTextField.setEnabled(false);
            add(sessionNameTextField);
            sessionNameTextField.addActionListener(this);

            createButton = new RoundButton("Create");
            createButton.setBorder(emptyBorder);
            createButton.setEnabled(false);
            add(createButton);
            createButton.addActionListener(this);

            searchButton = new RoundButton("Search for sessions");
            searchButton.setBorder(emptyBorder);
            searchButton.setEnabled(false);
            add(searchButton);
            searchButton.addActionListener(this);

            enterPressesWhenFocused(loginLogoutButton);
            enterPressesWhenFocused(createButton);
        }

        public void actionPerformed(ActionEvent e) {
            Player self = session.getMyself();
            if (e.getActionCommand().equals("Login") || e.getSource().equals(usernameTextField)) {
                String username = usernameTextField.getText();
                if (!username.equals("")) {
                    thread.sendLoginServer(null, username);
                }
            } else if (e.getActionCommand().equals("Create") || e.getSource().equals(sessionNameTextField)) {
                String text = sessionNameTextField.getText();
                if (!text.equals("")) {
                    int gameMode = session.isStandaloneGameMode() ? GameSession.FLAG_GAME_MODE : session.getGameMode();
                    thread.sendCreateSession(null, self, text, gameMode, session.getAutoFlag());
                }
            } else if (e.getActionCommand().equals("Logout")) {
                thread.sendLogoutServer(null);
            } else if (e.getSource().equals(searchButton)) {
                SessionPanel sessionPanel = (SessionPanel)getParent();
                sessionPanel.setVisible(sessionPanel.findSessionPanel);
                thread.sendSessionList(null);
            }
        }
    }


    // ----------- Session Panel ------------

    public class PlayPanel extends JPanel implements ActionListener, ListSelectionListener {
        public DefaultListModel listModel = new DefaultListModel();
        public SessionRenderer renderer = new SessionRenderer();
        public JLabel playersLabel;
        private JList players;
        private JScrollPane scrollPane;
        private JButton quitButton;
        private JButton kickButton;

        PlayPanel() {
            super(null);
            setOpaque(true);
            setBackground(Color.LIGHT_GRAY);
            //setBorder(BorderFactory.createLoweredBevelBorder());

            createGui();
            session.addTimerActionListener(this);
        }

        public void setSize(int xSize, int ySize) {
            super.setSize(xSize, ySize);
            playersLabel.setBounds(5, 0, xSize - 6 , 20);
            scrollPane.setBounds(0, 20, xSize - 6, ySize - 90);
            MultiplayerPanel.setBounds(new JButton[] {kickButton, quitButton});
        }

        private void createGui() {
            playersLabel = new JLabel("Session", JLabel.CENTER);
            add(playersLabel);

            players = new JList(listModel);
            players.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            players.setCellRenderer(renderer);
            players.addListSelectionListener(this);
            scrollPane = new JScrollPane(players);
            add(scrollPane);

            kickButton = new RoundButton("Kick");
            kickButton.setBorder(emptyBorder);
            add(kickButton);
            kickButton.addActionListener(this);

            quitButton = new RoundButton("Quit");
            quitButton.setBorder(emptyBorder);
            add(quitButton);
            quitButton.addActionListener(this);

        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(quitButton)) {
                Player self = session.getMyself();
                thread.sendQuitSession(null, self);
            } else if (e.getSource().equals(kickButton)) {
                if (players.getSelectedIndex() != -1) {
                    Player player = (Player)players.getSelectedValue();
                    if (!player.equals(session.getMyself()) && session.isSelfCreator()) {
                        thread.sendQuitSession(null, player);
                    }
                }
            } else {
                players.repaint();
            }
        }

        public void valueChanged(ListSelectionEvent e) {
            //Todo
        }
    }
}

