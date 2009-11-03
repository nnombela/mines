package mines.util;

import javax.imageio.ImageIO;
import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


/**
 * Images class
 * <p/>
 * This class is used to ...
 *
 * @author <a href="mailto:nnombela@germinus.com">Nicolas Nombela</a>
 * @since 28-jun-2006
 */
public class ImageLoader {
    private static final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().
            getDefaultScreenDevice().getDefaultConfiguration();

    private Map<String, Image[]> images =  new HashMap<String, Image[]>();

    public void loadImage(String dir, String fileName) throws IOException {
        loadStripImage(dir, fileName, 1);
    }

    public void loadStripImage(String dir, String fileName, int num) throws IOException {
        BufferedImage stripImg  = ImageIO.read(getClass().getResource(dir  + fileName));

        int width = stripImg.getWidth() / num, height = stripImg.getHeight();
        int transparency = stripImg.getColorModel().getTransparency();

        Image[] array = new Image[num];
        for (int i = 0; i < num; i++) {
            BufferedImage img = gc.createCompatibleImage(width, height, transparency);

            Graphics2D g = img.createGraphics();
            g.drawImage(stripImg, 0, 0, width, height, i * width, 0, (i + 1) * width, height, null);
            g.dispose();

            array[i] = img;
        }
        String name = fileName.substring(0, fileName.indexOf('.'));
        images.put(name, array);
    }


    public Image[] getImages(String name) {
        return images.get(name);
    }

}
