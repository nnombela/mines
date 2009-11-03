package mines.gui;

import mines.model.ClientSession;
import mines.util.ImageLoader;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.*;
import java.awt.*;

/**
 * MinesPanel class
 * <p/>
 * This class is used to ...
 *
 * @author <a href="mailto:nnombela@gmail.com">Nicolas Nombela</a>
 * @since 24-jun-2006
 */
public class ScoreBoardComponent extends BackBufferComponent implements ActionListener {
    private static final Border numberBorder = BorderFactory.createLoweredBevelBorder();

    private ClientSession session;
    BoardComponent boardComponent;
    private JButton reset;

    private int yBox;
    private int xSeconds, xMinesLeft;

    private Icon[] icons = new Icon[5];
    private Image[] digits;
    private int[] seconds;
    private int[] minesLeft;

    public ScoreBoardComponent(BoardComponent boardComponent, ClientSession session, ImageLoader images) {
        this.boardComponent = boardComponent;
        this.session = session;

        setBackground(Color.LIGHT_GRAY);
        setForeground(Color.BLACK);
        session.addTimerActionListener(this);

        digits = images.getImages("digits");
        //digits_blue = images.getImages("digits_blue");
        Image[] buttons = images.getImages("buttons");
        for(int i = 0; i < 5; ++i) {
            icons[i] = new ImageIcon(buttons[i]);
        }

        addResetButton();
    }

    private void addResetButton() {
        reset = new JButton(icons[0]);
        reset.setPressedIcon(icons[4]);
        reset.setSize(icons[0].getIconWidth(), icons[0].getIconHeight());
        reset.setBorder(BorderFactory.createRaisedBevelBorder());
        add(reset);

        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                session.restart();
                getParent().repaint();
            }
        });
    }

    public void setSize(int width, int height) {
        Border border = BorderFactory.createLoweredBevelBorder();
        setBorder(border);
        Insets borderInsets = border.getBorderInsets(this);
        width += borderInsets.left + borderInsets.right;
        height += borderInsets.top + borderInsets.bottom;
        super.setSize(width, height);

        xMinesLeft = width / 18 ;
        xSeconds  = ((17 * width)  / 18) - 3 * digits[0].getWidth(null);
        yBox = (height - digits[0].getHeight(null)) / 2;
        reset.setLocation((width - reset.getWidth()) / 2 ,
                (height - reset.getHeight()) / 2);

        clearBackBuffer();
    }

    private void paintNumberBorder(Graphics g, int x, int y) {
        Insets insets = numberBorder.getBorderInsets(this);
        numberBorder.paintBorder(this, g, x - insets.left, y - insets.top,
                digits[0].getWidth(null) * 3 + insets.left + insets.right,
                digits[0].getHeight(null) + insets.top + insets.bottom);
    }

    private Icon getIcon() {
        return session.getMyself().isReady() ?
                boardComponent.isCellPressed() ? icons[3] : icons[0] :
                session.isWinnerMyself()? icons[2] : icons[1];
    }

    protected void paintComponent(Graphics g) {
        reset.setIcon(getIcon());
        super.paintComponent(g);
    }

    public void restoreBackBuffer() {
        Graphics2D g = createBackBufferGraphics();
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        initNumbers();
        paintNumberBorders(g);
        g.dispose();
    }

    private void initNumbers() {
        this.seconds = new int[] { 10, 10, 10 };
        this.minesLeft = new int[] { 10, 10, 10 };
    }

    private void paintNumberBorders(Graphics2D g) {
        paintNumberBorder(g, xSeconds, yBox);
        paintNumberBorder(g, xMinesLeft, yBox);
    }

    protected void renderBackBuffer() {
        Graphics2D g = createBackBufferGraphics();
        seconds = paintNumbers(g, digits, session.getElapseSeconds(),
                seconds, xSeconds, yBox);
        minesLeft = paintNumbers(g, digits, session.getBoard().getNumFlagsLeft(),
                minesLeft, xMinesLeft, yBox);
        g.dispose();
    }


    private static int[] getDigits(int number) {
        int digit0 = number % 10;
        int digit1 = (number / 10) % 10;
        int digit2 = (number / 100) % 10;
        return new int[] { digit2, digit1, digit0 };
    }

    private int[] paintNumbers(Graphics2D g, Image[] digits, int number, int[] numbers, int x, int y) {
        int[] newNumbers = getDigits(number);
        for(int i = 0; i < 3; ++i) {
            if (newNumbers[i] != numbers[i]) {
                paintNumber(g, digits, newNumbers[i], i, x, y);
            }
        }
        return newNumbers;
    }

    private void paintNumber(Graphics2D g, Image[] digits, int digit, int i, int x, int y) {
        Image image = (digit < 0 || digit > 9)? digits[10] : digits[digit];
        g.drawImage(image, x + i * image.getWidth(null), y , null);
    }

    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
