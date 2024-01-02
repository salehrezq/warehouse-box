/*
 * The MIT License
 *
 * Copyright 2024 Saleh.
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
package warehouse.panel.createandupdate;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import warehouse.panel.items.ScrollableScalableImageContainer;

/**
 *
 * @author Saleh
 */
public class ItemFormImage implements ImageSelectedListner {

    private JPanel panelContainer, panelImage, panelContols;
    private JButton btnBrowse;
    private JLabel lbImagePreview;
    private ScrollableScalableImageContainer scalableImageContainer;

    public ItemFormImage() {
        panelContainer = new JPanel(new BorderLayout());
        panelImage = new JPanel(new BorderLayout());
        panelContols = new JPanel();
        panelContainer.add(panelImage, BorderLayout.CENTER);
        panelContainer.add(panelContols, BorderLayout.PAGE_END);
        scalableImageContainer = new ScrollableScalableImageContainer();
        scalableImageContainer.loadImage("C:/ImageTest/pp.jpg");
        // imagePlace.initComponents();
        panelImage.add(scalableImageContainer.getContainer(), BorderLayout.CENTER);
        scalableImageContainer.paintImage();
    }

    public JScrollPane getScrollableScalableImageContainer() {
        return scalableImageContainer.getContainer();
    }

    @Override
    public void imageSelected(byte[] photoInBytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(photoInBytes);
        try {
            BufferedImage image = ImageIO.read(bis);
            lbImagePreview.setIcon(new ImageIcon(image));
        } catch (IOException ex) {
            Logger.getLogger(ItemFormImage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
