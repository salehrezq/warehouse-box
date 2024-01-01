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
package warehouse.panel.items;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Saleh
 */
public class ImagePlace implements MouseWheelListener {

    JPanel panel;
    JScrollPane imageScroll;
    /**
     * Displays the image.
     */
    JLabel lbImage;
    Dimension size;
    double scale = 0.2;
    public BufferedImage image;

    public void loadImage(String imagePAth) {
        size = new Dimension(10, 10);

        try {
            image = ImageIO.read(new File(imagePAth));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setImage(Image image) {
        lbImage.setIcon(new ImageIcon(image));
    }

    public void initComponents() {
        if (panel == null) {
            panel = new JPanel(new BorderLayout());
//            panel.setBackground(Color.green);
//            panel.setBorder(new EmptyBorder(5, 5, 5, 5));
            panel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
            lbImage = new JLabel();
            lbImage.setBorder(BorderFactory.createLineBorder(Color.WHITE, 6));
            lbImage.setHorizontalAlignment(JLabel.CENTER);
            lbImage.setVerticalAlignment(JLabel.CENTER);

//            JPanel imageCenter = new JPanel(new GridBagLayout());
//            imageCenter.add(lbImage);
            panel.add(lbImage, BorderLayout.CENTER);
            imageScroll = new JScrollPane();
            imageScroll.setViewportView(panel);
//            imageScroll.setBackground(Color.GREEN);
            //     imageScroll.setPreferredSize(new Dimension(100, 100));
//            panel.add(imageScroll, BorderLayout.CENTER);
            imageScroll.addMouseWheelListener(this);
        }
    }

    public Container getGui() {
        return imageScroll;
    }

    protected void paintImage() {
        int w = panel.getWidth();
        int h = panel.getHeight();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        BufferedImage bi = new BufferedImage(
                (int) (imageWidth * scale),
                (int) (imageHeight * scale),
                image.getType());
        Graphics2D g2 = bi.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

//          g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        double x = (w - scale * imageWidth) / 2;
        double y = (h - scale * imageHeight) / 2;
        AffineTransform at = AffineTransform.getTranslateInstance(0, 0);
        at.scale(scale, scale);
        g2.drawRenderedImage(image, at);
        setImage(bi);
    }

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
        } else {
            // scrollPanelUsed.getListeners(MouseWheelListener.class)[0].mouseWheelMoved(e);
            //  getParent().dispatchEvent(e);
        }
    }
}
