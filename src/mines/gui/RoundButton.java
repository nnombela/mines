package mines.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.*;
import javax.swing.*;

/**
 * RoundButton class
 * <p/>
 * This class is used to ...
 *
 * @author <a href="mailto:nnombela@gmail.com">Nicolas Nombela</a>
 * @since 29-jul-2006
 */
public class RoundButton extends JButton {
    private static final int ARC_SIZE = 10;

    private BufferedImage pressed, notPressed;

    public RoundButton(String text) {
        super(text);
        setOpaque(true);
        setContentAreaFilled(false);
    }

    public void setBackground(Color color) {
        super.setBackground(color);
    }

    public void setSize(int width, int height) {
        super.setSize(width, height);
    }

    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y , width, height);
    }

    private static BufferedImage createImage(Color color, Dimension size, boolean isPressed) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage image = gc.createCompatibleImage(size.width, size.height, Transparency.TRANSLUCENT);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        paintButton(g, color, size, isPressed);
        g.dispose();
        return image;
    }

    private static GradientPaint getGradientPaint(Color color1, Color color2, Dimension size) {
        return new GradientPaint(new Point2D.Double(size.width / 2, 0), color1,
                new Point2D.Double(size.width / 2, size.height), color2);
    }

    private static void paintButton(Graphics2D g, Color color, Dimension size, boolean isPressed) {
        GradientPaint gradient = isPressed?
                getGradientPaint(color.darker(), color.brighter(), size) :
                getGradientPaint(color.brighter(), color.darker(), size);

        g.setPaint(gradient);
        g.fillRoundRect(0, 0, size.width - 1, size.height - 1, ARC_SIZE, ARC_SIZE);

        g.setColor(color.darker().darker());
        g.drawRoundRect(0, 0, size.width - 1, size.height - 1, ARC_SIZE, ARC_SIZE);
    }

    private synchronized void createImages() {
        if (pressed != null) {
            this.pressed.flush();
            this.notPressed.flush();
        }
        this.pressed = createImage(getBackground(), getSize(), true);
        this.notPressed = createImage(getBackground(), getSize(), false);
    }

    private BufferedImage getImage() {
        return getModel().isPressed()? pressed : notPressed;
    }

    protected void paintComponent(Graphics g) {
        Dimension size = getSize();
        if (pressed == null || pressed.getWidth() != size.width || pressed.getHeight() != size.height) {
            createImages();
        }
        g.drawImage(getImage(), 0, 0, null);
        super.paintComponent(g);
    }

    protected void paintBorder(Graphics g) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());

        JPanel panel = new JPanel ();

        JButton cool = new RoundButton("cool");
        panel.add(cool);

        JButton big = new RoundButton("big");
        big.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(big);

        frame.getContentPane().add(panel);
        frame.setSize(150, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

