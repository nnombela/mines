package mines.gui;

import javax.swing.*;
import java.awt.image.VolatileImage;
import java.awt.*;

/**
 * BackBufferComponent class
 * <p/>
 * This class is used to ...
 *
 * @author <a href="mailto:nnombela@gmail.com">Nicolas Nombela</a>
 * @since 08-jul-2006
 */
public abstract class BackBufferComponent extends JComponent {
    VolatileImage backBuffer;
    GraphicsConfiguration gc;

    public BackBufferComponent() {
        setOpaque(true);
        setDoubleBuffered(false);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
    }

    protected void clearBackBuffer() {
        if (backBuffer != null) {
            backBuffer.flush();
            backBuffer = null;
        }
    }

    private void createBackBuffer() {
        clearBackBuffer();
        backBuffer = gc.createCompatibleVolatileImage(getWidth(), getHeight());
        restoreBackBuffer();
    }

    protected void paintComponent(Graphics g) {
        if (backBuffer == null) {
            createBackBuffer();
        }
        do {
            checkBackBuffer();
            renderBackBuffer();
            g.drawImage(backBuffer, 0, 0, null);

        } while(backBuffer == null || backBuffer.contentsLost());
    }

    private void checkBackBuffer() {
        int valCode = backBuffer.validate(gc);
        if (valCode == VolatileImage.IMAGE_RESTORED) {
            restoreBackBuffer();
        } else if (valCode == VolatileImage.IMAGE_INCOMPATIBLE) {
            createBackBuffer();
        }
    }

    protected Graphics2D createBackBufferGraphics() {
        return backBuffer.createGraphics();
    }

    protected abstract void restoreBackBuffer();

    protected abstract void renderBackBuffer();
}
