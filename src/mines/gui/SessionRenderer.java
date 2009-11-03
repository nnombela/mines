/**
 * File Description -
 * Date: 08-dic-2006
 */
package mines.gui;

import mines.model.Player;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * ChatListRenderer - This class models...
 *
 * @author <a href="mailto:nnombela@gmail.com">Nicolas Nombela</a>
 * @version 1.0
 */
public class SessionRenderer extends JComponent implements ListCellRenderer {
    private static final Color LIGHT_GRAY_COLOR = new Color(240, 240, 240);

    private Player player;
    private boolean selected;
    private Map<String, Image> cache = new HashMap<String, Image>();

    public SessionRenderer() {
        setOpaque(true);
        //setFont(new Font("Monospaced", Font.PLAIN, 10));
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void update(Graphics g) {
        super.update(g);
    }

    public void clearCache() {
        for (Image image : cache.values()) {
            image.flush();
        }
        cache.clear();
    }

    private Image getImageFromCache(String key) {
        Image image = cache.get(key);
        if (image == null) {
            image = createImage();
            cache.put(key, image);
        }
        return image;
    }

    protected void paintComponent(Graphics g) {
        //super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g.drawImage(getImageFromCache(player.getUsername() + player.isReady()), 0, 0, null);

        if (selected) {
            g2d.setColor(getBackground());
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
        }

        FontMetrics metrics = g2d.getFontMetrics();
        int zeroWidth = metrics.charWidth('0');
        int height = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

        g2d.setColor(Color.BLUE);
        g2d.drawString(String.valueOf(player.getScore()), getWidth() - 7 * zeroWidth, height);
        g2d.setColor(Color.BLACK);
        g2d.drawString(String.valueOf(player.getGamesWon()), getWidth() - 2 * zeroWidth, height);
    }

    private void paintCacheImage(Graphics2D g) {
        if (!getBackground().equals(getParent().getBackground())) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        g.setColor(player.isReady()? Color.GREEN : Color.RED);
        int d = getHeight() / 2;
        g.fillOval(d / 2, d / 2, d, d);

        FontMetrics metrics = g.getFontMetrics();
        int zeroWidth = metrics.charWidth('0');
        int height = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

        g.setColor(getForeground());
        g.drawString(player.getUsername(), 3 * d, height);
        if (player.isSessionSelf()) {
            g.drawLine(3 * d, height + 1, 3 * d + metrics.stringWidth(player.getUsername()), height + 1);
        }
        g.drawString("|", getWidth() - 4 * zeroWidth, height);

    }

    private Image createImage() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage image = gc.createCompatibleImage(getWidth(), getHeight(), Transparency.TRANSLUCENT);

        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        paintCacheImage(g);
        g.dispose();

        return image;
    }

    public Dimension getPreferredSize() {
        return new Dimension(0, 20);
    }

    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean hasFocus) {

        if (!isSelected) {
            if (index % 2 == 0) {
                setBackground(LIGHT_GRAY_COLOR);
            } else {
                setBackground(Color.WHITE);
            }
            setForeground(list.getForeground());
        } else {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }
        setPlayer((Player)value);
        setSelected(isSelected);
        return this;
    }
}
