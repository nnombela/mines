/**
 * File Description -
 * Date: 25-nov-2006
 */
package mines.gui;

import javax.swing.*;
import java.awt.*;

/**
 * ChatListRenderer - This class models...
 *
 * @author <a href="mailto:nnombela@gmail.com">Nicolas Nombela</a>
 * @version 1.0
 */
public class ChatListRenderer extends DefaultListCellRenderer {
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
        
        if (value.toString().startsWith(">>")) {
            label.setForeground(Color.BLUE);
        } else if(value.toString().startsWith(">")) {
            label.setForeground(Color.BLACK);
        }

        return label;
    }
}