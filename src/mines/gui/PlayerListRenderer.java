package mines.gui;

import mines.model.Player;
import javax.swing.*;
import java.awt.*;

/**
 * ChatListRenderer - This class models...
 *
 * @author <a href="mailto:nnombela@gmail.com">Nicolas Nombela</a>
 * @version 1.0
 */
public class PlayerListRenderer extends DefaultListCellRenderer {
    private static final Color LIGHT_GRAY_COLOR = new Color(240, 240, 240);

    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean hasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list,
                value, index, isSelected, hasFocus);
        if (!isSelected) {
            if (index % 2 == 0) {
                label.setBackground(LIGHT_GRAY_COLOR);
            } else {
                label.setBackground(Color.WHITE);
            }
        }

        Player player = (Player)value;

        if (player.getSession() != null) {
            label.setForeground(Color.RED);
            label.setText(player.getUsername() + " (playing at " + player.getSession().getName() + ")");
        } else {
            label.setForeground(Color.BLUE);
            label.setText(player.getUsername() + " (not playing)");
        }

        return label;
    }

    public Dimension getPreferredSize() {
        return new Dimension(0, 20);
    }
}
