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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import warehouse.db.CRUDImages;
import warehouse.db.model.Image;

/**
 *
 * @author Saleh
 */
public class ScrollableScalableImageContainer {

    private JScrollPane scrollableContainer;
    private JLabel lbImage;
    BigDecimal scale, increment, upper, lower;
    private Image image;
    public BufferedImage bufferedImage;
    private MouseWheelMovedHandler mouseWheelMovedHandler;
    private KeyStroke keyStrokeCTRLRelease;
    private ActionMap actionMap;
    private InputMap inputMap;
    private ImageScaleSaveHandler imageScaleSaveHandler;
    private NoImageResponse noImageResponse;

    public ScrollableScalableImageContainer() {
        increment = new BigDecimal("0.05");
        upper = new BigDecimal("1.5");
        lower = new BigDecimal("0.05");
        lbImage = new JLabel();
        lbImage.setHorizontalAlignment(JLabel.CENTER);
        lbImage.setVerticalAlignment(JLabel.CENTER);
        scrollableContainer = new JScrollPane();
        scrollableContainer.setViewportView(lbImage);
        mouseWheelMovedHandler = new MouseWheelMovedHandler();
        scrollableContainer.addMouseWheelListener(mouseWheelMovedHandler);
        keyStrokeCTRLRelease = KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, 0, true);
        inputMap = lbImage.getInputMap(JComponent.WHEN_FOCUSED);
        actionMap = lbImage.getActionMap();
        inputMap.put(keyStrokeCTRLRelease, keyStrokeCTRLRelease.toString());
        imageScaleSaveHandler = new ImageScaleSaveHandler();
        actionMap.put(keyStrokeCTRLRelease.toString(), imageScaleSaveHandler);
        noImageResponse = new NoImageResponse(lbImage);
    }

    public void setImage(Image image) {
        this.image = image;
        scale = (image != null) ? image.getScale() : new BigDecimal("0.6");
        bufferedImage = (image != null) ? image.getBufferedImage() : null;
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

    public void noImageResponseAnimated() {
        noImageResponse.noImageAnimate();
    }

    public void noImageFeedback() {
        noImageResponse.noImageStillFrame();
    }

    public JScrollPane getContainer() {
        return scrollableContainer;
    }

    private int multiply(int value, BigDecimal scale) {
        return scale.multiply(new BigDecimal(value)).setScale(2, RoundingMode.HALF_UP).intValue();
    }

    public void paintImage() {
        if (bufferedImage == null) {
            setImageIcone(null);
            return;
        }
        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();
        BufferedImage bi = new BufferedImage(
                (int) (multiply(imageWidth, scale)),
                (int) (multiply(imageHeight, scale)),
                bufferedImage.getType());
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
//          g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        AffineTransform at = AffineTransform.getTranslateInstance(0, 0);
        at.scale(scale.doubleValue(), scale.doubleValue());
        g2.drawRenderedImage(bufferedImage, at);
        setImageIcone(bi);
    }

    private class MouseWheelMovedHandler implements MouseWheelListener {

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (image != null) {
                JScrollPane scrollPane = (JScrollPane) e.getSource();
                if (e.isControlDown()) {
                    lbImage.requestFocus();
                    scrollPane.setWheelScrollingEnabled(false);
                    if (e.getWheelRotation() < 0) {
                        // up
                        if (scale.compareTo(upper) == -1) {
                            scale = scale.add(increment);
                            paintImage();
                        }
                    } else {
                        //down
                        if (scale.compareTo(lower) == 1) {
                            scale = scale.subtract(increment);
                            paintImage();
                        }
                    }
                } else {
                    scrollPane.setWheelScrollingEnabled(true);
                }
            }
        }
    }

    private class ImageScaleSaveHandler extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (image != null) {
                image.setScale(scale);
                CRUDImages.updateScale(image);
                System.out.println("saved scale " + scale);
            }
        }
    }
}
