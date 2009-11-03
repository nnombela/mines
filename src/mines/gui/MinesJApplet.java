package mines.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * MinesJApplet class
 * <p/>
 * This class is used to ...
 *
 * @author <a href="mailto:nnombela@gmail.com">Nicolas Nombela</a>
 * @since 24-jun-2006
 */
public class MinesJApplet extends JApplet {
    private MinesPanel minesPanel;
    public static final String BEGGINER = "Begginer";
    public static final String MEDIUM = "Medium";
    public static final String ADVANCE = "Advance";
    public static final String GIANT = "Giant";
    public static final String MULTI_PLAYER = "MultiPlayer";
    public static final String SINGLE_PLAYER = "SinglePlayer";
    public static final String AUTO_FLAG = "Auto-Flag";
    public static final String FLAG = "Flag";
    public static final String UNCOVER = "Uncover";
    public static final String ABOUT = "About";

    public JMenuBar menuBar = new JMenuBar();
    public JMenu levelMenu = new JMenu("Level");
    public JMenu modeMenu = new JMenu("Mode");
    public JMenu typeMenu = new JMenu("Type");
    public JMenu optionsMenu = new JMenu("Options");
    public JMenu helpMenu = new JMenu("Help");
    public JRadioButtonMenuItem[] levelMenuItems = new JRadioButtonMenuItem[3];
    public JRadioButtonMenuItem[] typeMenuItems = new JRadioButtonMenuItem[2];
    public JRadioButtonMenuItem[] modeMenuItems = new JRadioButtonMenuItem[2];
    public JCheckBoxMenuItem autoFlagsMenuItem;

    public void init() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    setGraphicUserInterface();
                }
            });
        } catch (Exception e) {
            System.err.println("setGraphicUserInterface() didn't successfully complete");
        }

    }

    private void setGraphicUserInterface() {
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        setGlassPane(new NotifyGlassPane());

        int serverPort = getParameter("SERVER_PORT") != null? Integer.parseInt(getParameter("SERVER_PORT")) : 1234;
        int clientPort = getParameter("CLIENT_PORT") != null? Integer.parseInt(getParameter("CLIENT_PORT")) : 1235;
        String host = getParameter("HOST") != null? getParameter("HOST") : getCodeBase().getHost();

        this.minesPanel = new MinesPanel(this, host, serverPort, clientPort);
        contentPane.add(minesPanel);

        setJMenuBar(createJMenuBar());
    }

    private JMenuBar createJMenuBar() {
        levelMenu.setMnemonic(KeyEvent.VK_L);

        ButtonGroup group = new ButtonGroup();

        levelMenuItems[0] = new JRadioButtonMenuItem(BEGGINER);
        levelMenuItems[0].setMnemonic(KeyEvent.VK_B);
        group.add(levelMenuItems[0]);
        levelMenu.add(levelMenuItems[0]);
        levelMenuItems[0].addActionListener(minesPanel);

        levelMenuItems[1] = new JRadioButtonMenuItem(MEDIUM);
        levelMenuItems[1].setMnemonic(KeyEvent.VK_E);
        group.add(levelMenuItems[1]);
        levelMenu.add(levelMenuItems[1]);
        levelMenuItems[1].addActionListener(minesPanel);

        levelMenuItems[2] = new JRadioButtonMenuItem(ADVANCE);
        levelMenuItems[2].setSelected(true);
        levelMenuItems[2].setMnemonic(KeyEvent.VK_A);
        group.add(levelMenuItems[2]);
        levelMenu.add(levelMenuItems[2]);
        levelMenuItems[2].addActionListener(minesPanel);

//        levelMenuItems[3] = new JRadioButtonMenuItem(GIANT);
//        levelMenuItems[3].setMnemonic(KeyEvent.VK_G);
//        group.add(levelMenuItems[3]);
//        levelMenu.add(levelMenuItems[3]);
//        levelMenuItems[3].addActionListener(minesPanel);

        menuBar.add(levelMenu);

        typeMenu.setMnemonic(KeyEvent.VK_T);

        ButtonGroup typeGroup = new ButtonGroup();
        typeMenuItems[0] = new JRadioButtonMenuItem(MULTI_PLAYER);
        typeMenuItems[0].setMnemonic(KeyEvent.VK_M);
        typeMenuItems[0].setSelected(true);
        typeGroup.add(typeMenuItems[0]);
        typeMenu.add(typeMenuItems[0]);
        typeMenuItems[0].addActionListener(minesPanel);

        typeMenuItems[1] = new JRadioButtonMenuItem(SINGLE_PLAYER);
        typeMenuItems[1].setMnemonic(KeyEvent.VK_S);
        typeGroup.add(typeMenuItems[1]);
        typeMenu.add(typeMenuItems[1]);
        typeMenuItems[1].addActionListener(minesPanel);

        menuBar.add(typeMenu);

        modeMenu.setMnemonic(KeyEvent.VK_D);

        ButtonGroup modeGroup = new ButtonGroup();
        modeMenuItems[0] = new JRadioButtonMenuItem(FLAG);
        modeMenuItems[0].setMnemonic(KeyEvent.VK_F);
        modeMenuItems[0].setSelected(true);
        modeGroup.add(modeMenuItems[0]);
        modeMenu.add(modeMenuItems[0]);
        modeMenuItems[0].addActionListener(minesPanel);

        modeMenuItems[1] = new JRadioButtonMenuItem(UNCOVER);
        modeMenuItems[1].setMnemonic(KeyEvent.VK_U);
        modeGroup.add(modeMenuItems[1]);
        modeMenu.add(modeMenuItems[1]);
        modeMenuItems[1].addActionListener(minesPanel);

        menuBar.add(modeMenu);

        optionsMenu.setMnemonic(KeyEvent.VK_O);

        autoFlagsMenuItem = new JCheckBoxMenuItem(AUTO_FLAG);
        autoFlagsMenuItem.setMnemonic(KeyEvent.VK_F);
        optionsMenu.add(autoFlagsMenuItem);
        autoFlagsMenuItem.addActionListener(minesPanel);

        menuBar.add(optionsMenu);

        helpMenu.setMnemonic(KeyEvent.VK_H);
        JMenuItem item = new JMenuItem(ABOUT);
        item.addActionListener(minesPanel);
        helpMenu.add(item);

        menuBar.add(helpMenu);

        return menuBar;
    }

    public void setEnabledButtons(boolean enabled) {
        for(JRadioButtonMenuItem item : levelMenuItems) {
            item.setEnabled(enabled);
        }
        for(JRadioButtonMenuItem item : typeMenuItems) {
            item.setEnabled(enabled);
        }
        for(JRadioButtonMenuItem item : modeMenuItems) {
            item.setEnabled(enabled);
        }
        autoFlagsMenuItem.setEnabled(enabled);
    }

    // -------------------- applet life cycle methods --------------

    public void start() {
        minesPanel.resume();
    }

    public void stop() {
        minesPanel.pause();
    }

    public void destroy() {
        minesPanel.destroy();
    }

}
