package mines.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

/**
 * NotifyGlassPane class
 * <p/>
 * This class is used to ...
 *
 * @author <a href="mailto:nnombela@gmail.com">Nicolas Nombela</a>
 * @since 29-jul-2006
 */
public class NotifyGlassPane extends JPanel implements ActionListener {
    private String message = "";
    private int fontSize = 24;
    private Color color = Color.WHITE;
    private JButton acceptButton = new RoundButton("ACCEPT");
    private JButton cancelButton = new RoundButton("CANCEL");
    private Timer timer;
    private int timerCounter;
    private CounterCommand counterCommand = new CounterCommand();
    private boolean showButtons = true;
    private boolean showBackground = true;

    public static class CounterCommand {
        public boolean execute(NotifyGlassPane panel) {
            return false;
        }

        public void init(NotifyGlassPane panel) {
        }
        public void finish(NotifyGlassPane panel) {
        }
        public void accept(NotifyGlassPane panel) {
        }
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public void setShowBackground(boolean showBackground) {
        this.showBackground = showBackground;
    }

    public synchronized void setCounterCommand(CounterCommand counterCommand) {
        if (counterCommand != null) {
            this.counterCommand = counterCommand;
        }
    }

    public void setTimerCounter(int timerCounter) {
        this.timerCounter = timerCounter;
    }

    public int getTimerCounter() {
        return this.timerCounter;
    }

    public void setShowButtons(boolean show) {
        this.showButtons = show;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotifyGlassPane() {
        super(null);
        setOpaque(false);

        timer = new Timer(1000, this);
        timer.setInitialDelay(0);

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Toolkit.getDefaultToolkit().beep();
            }
        });

        addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (isVisible()) {
                    Component component = e.getOppositeComponent();
                    if (component == null || component.getParent() != NotifyGlassPane.this) {
                        requestFocus();
                    }
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                Toolkit.getDefaultToolkit().beep();
            }

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    setVisible(false);
                }
            }
        });

        acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                counterCommand.accept(NotifyGlassPane.this);
                setVisible(false);
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        add(acceptButton);
        add(cancelButton);
    }

    public synchronized void setVisible(boolean flag) {
        super.setVisible(flag);
        if (flag) {
            counterCommand.init(NotifyGlassPane.this);
            timer.start();
            requestFocus();
        } else {
            timer.stop();
            counterCommand.finish(NotifyGlassPane.this);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (timerCounter > 0) {
            if (counterCommand.execute(NotifyGlassPane.this)) {
                repaint();
            }
        } else if (timerCounter == 0) {
            setVisible(false);
        }
        --timerCounter;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, fontSize));
        FontMetrics metrics = g2d.getFontMetrics();
        Rectangle2D stringBounds = metrics.getStringBounds(message, g2d);

        int width = (int) stringBounds.getWidth() + metrics.getHeight();
        int height = (int) stringBounds.getHeight() + metrics.getHeight();

        Dimension size = getSize();
        int x = (size.width - width) / 2;
        int y = (size.height - height) / 2;

        if (showButtons) {
            Rectangle2D bounds = metrics.getStringBounds(acceptButton.getText(), g2d);

            acceptButton.setLocation(x + width / 2 - 10 - (int)bounds.getWidth(), y + height + 10);
            cancelButton.setLocation(x + width / 2 + 10, y + height + 10);
            acceptButton.setSize((int)bounds.getWidth(), (int)bounds.getHeight());
            cancelButton.setSize((int)bounds.getWidth(), (int)bounds.getHeight());
            height += (int)bounds.getHeight() + 20;
        } else {
            acceptButton.setSize(0, 0);
            cancelButton.setSize(0, 0);
        }

        if (showBackground) {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x, y, width, height, 20, 20);
            g2d.setStroke(new BasicStroke(1));
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRoundRect(x, y, width, height, 20, 20);
        }
        
        int xString = (size.width - (int) stringBounds.getWidth()) / 2;
        int yString = (size.height / 2) + ((metrics.getAscent() - metrics.getDescent()) / 2);

        // Draw a shadow
        g2d.setColor(new Color(0, 0, 0, 170));
        g2d.drawString(message, xString + 2, yString + 2);

        // Draw the text
        g2d.setColor(color);
        g2d.drawString(message, xString, yString);
    }

// ---------- Utilities methods

    public void showConfirm(String message, CounterCommand command) {
        show(message, true, true, 24, Color.YELLOW, 60, command);
    }


    private static CounterCommand getMultiMessageListener(final String[] messages, final int timeInterval) {
        return new CounterCommand() {

            public void init(NotifyGlassPane panel) {
                panel.setTimerCounter(timeInterval * messages.length);
            }

            public boolean execute(NotifyGlassPane panel) {
                if (panel.getTimerCounter() % timeInterval == 0) {
                    int index = messages.length  - panel.getTimerCounter() / timeInterval;
                    panel.setMessage(messages[index]);
                    return true;
                }
                return false;
            }
        };
    }

    public void showMultipleMessage(String[] messages) {
        show(message, false, true, 24, Color.YELLOW, 0, getMultiMessageListener(messages, 2));
    }


    public void showMessage(String message, int time) {
        show(message, false, true, 24, Color.YELLOW, time, new CounterCommand());
    }

    private static CounterCommand waitingEndCommand = new CounterCommand() {
        public boolean execute(NotifyGlassPane panel) {
            if (panel.getTimerCounter() == 2) {
                panel.setColor(Color.YELLOW);
                panel.setMessage("Server does not response");
                return true;
            }
            return false;
        }
     };
    
    public void showWaitingMessage() {
        show("Waiting for server to response...", false, true, 24, Color.WHITE, 6, waitingEndCommand);
    }

    private synchronized void show(String message, boolean showButtons, boolean showBackground,
                      int fontSize, Color color, int counter, CounterCommand counterCommand) {
        setMessage(message);
        setShowButtons(showButtons);
        setShowBackground(showBackground);
        setFontSize(fontSize);
        setColor(color);
        setTimerCounter(counter);
        setCounterCommand(counterCommand);
        setVisible(true);
    }

    private static CounterCommand countdownCommand = new CounterCommand() {
        public boolean execute(NotifyGlassPane panel) {
            if (panel.getTimerCounter() == 3) {
                panel.setFontSize(100);
                panel.setColor(Color.YELLOW);
                panel.setShowBackground(true);
            }
            if (panel.getTimerCounter() < 4) {
                panel.setMessage(String.valueOf(panel.getTimerCounter()));
                return true;
            }
            return false;
        }
    };

    public void showCountdown() {
        show("Get Ready!", false, true, 48, Color.YELLOW, 6 , countdownCommand);
    }
}
