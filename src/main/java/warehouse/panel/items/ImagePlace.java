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
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Saleh
 */
public class ImagePlace extends JComponent implements ChangeListener {

    JPanel panel;
    /**
     * Displays the image.
     */
    JLabel lbImage;
    Dimension size;
    double scale = 0.1;
    public BufferedImage image;

    public void loadImage(String imagePAth) {
        size = new Dimension(10, 10);
        setBackground(Color.black);
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
            panel.setBorder(new EmptyBorder(5, 5, 5, 5));
            lbImage = new JLabel();
            JPanel imageCenter = new JPanel(new GridBagLayout());
            imageCenter.add(lbImage);
            JScrollPane imageScroll = new JScrollPane(imageCenter);
            imageScroll.setPreferredSize(new Dimension(300, 100));
            panel.add(imageScroll, BorderLayout.CENTER);
        }
    }

    public Container getGui() {

        return panel;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        int value = ((JSlider) e.getSource()).getValue();
        scale = value / 100.0;
        paintImage();
    }

    protected void paintImage() {
        int w = getWidth();
        int h = getHeight();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        BufferedImage bi = new BufferedImage(
                (int) (imageWidth * scale),
                (int) (imageHeight * scale),
                image.getType());
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        double x = (w - scale * imageWidth) / 2;
        double y = (h - scale * imageHeight) / 2;
        AffineTransform at = AffineTransform.getTranslateInstance(0, 0);
        at.scale(scale, scale);
        g2.drawRenderedImage(image, at);
        setImage(bi);
    }

    @Override
    public Dimension getPreferredSize() {
        int w = (int) (scale * size.width);
        int h = (int) (scale * size.height);
        return new Dimension(w, h);
    }

    private JSlider getControl() {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 500, 10);
        slider.setMajorTickSpacing(50);
        slider.setMinorTickSpacing(25);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(this);
        return slider;
    }

    //  public static void main(String[] args) {
    //  ImagePlace app = new ImagePlace();
    //  JFrame frame = new JFrame();
    //   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //   frame.setContentPane(app.getGui());
    //  app.setImage(app.image);
    //   app.paintImage();
    // frame.getContentPane().add(new JScrollPane(app));  
    //  frame.getContentPane().add(app.getControl(), "Last");
    //  frame.setSize(700, 500);
    //  frame.setLocation(200, 200);
    //  frame.setVisible(true);
    //  }
}
