/*
 * The MIT License
 *
 * Copyright 2023 Saleh.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package utility.imagepane;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import warehouse.db.model.Image;

/**
 *
 * @author Saleh
 */
public class ScrollableScalableImageContainer {

    private JScrollPane scrollableContainer;
    private JLabel lbImage;
    double scale = 0.1;
    public BufferedImage bufferedImage;
    private MouseWheelMovedHandler mouseWheelMovedHandler;

    public ScrollableScalableImageContainer() {
        lbImage = new JLabel();
        lbImage.setHorizontalAlignment(JLabel.CENTER);
        lbImage.setVerticalAlignment(JLabel.CENTER);
        scrollableContainer = new JScrollPane();
        scrollableContainer.setViewportView(lbImage);
        mouseWheelMovedHandler = new MouseWheelMovedHandler();
        scrollableContainer.addMouseWheelListener(mouseWheelMovedHandler);
    }

    public void setImage(Image image) {
        bufferedImage = image.getBufferedImage();
        setImageIcone(bufferedImage);
        paintImage();
    }

    private void setImageIcone(BufferedImage image) {
        if (image == null) {
            lbImage.setIcon(null);
            return;
        }
        lbImage.setIcon(new ImageIcon(image));
    }

    public JScrollPane getContainer() {
        return scrollableContainer;
    }

    public void paintImage() {
        if (bufferedImage == null) {
            setImageIcone(null);
            return;
        }
        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();
        BufferedImage bi = new BufferedImage(
                (int) (imageWidth * scale),
                (int) (imageHeight * scale),
                bufferedImage.getType());
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
//          g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        AffineTransform at = AffineTransform.getTranslateInstance(0, 0);
        at.scale(scale, scale);
        g2.drawRenderedImage(bufferedImage, at);
        setImageIcone(bi);
    }

    private class MouseWheelMovedHandler implements MouseWheelListener {

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.isControlDown()) {
                if (e.getWheelRotation() < 0) {
                    if (scale < 1.5) {
                        scale += 0.01;
                        paintImage();
                    }
                } else if (scale > 0.03) {
                    scale -= 0.01;
                    paintImage();
                }
            }
        }
    }
}
