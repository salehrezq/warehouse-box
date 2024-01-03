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

    private JPanel panelContainer, panelContols;
    private JButton btnBrowse;
    private JLabel lbImagePreview;
    private ScrollableScalableImageContainer scalableImageContainer;

    public ItemFormImage() {
        panelContainer = new JPanel(new BorderLayout());
        scalableImageContainer = new ScrollableScalableImageContainer();
        panelContols = new JPanel();
        btnBrowse = new JButton("Browse...");
        panelContols.add(btnBrowse);
        panelContainer.add(scalableImageContainer.getContainer(), BorderLayout.CENTER);
        panelContainer.add(panelContols, BorderLayout.PAGE_END);

        //  scalableImageContainer.paintImage();
    }

    public void setFormBtnAndImageContainerResponsivity(IMGFileChooser iMGFileChooserResponsivity) {
        this.btnBrowse.addActionListener(iMGFileChooserResponsivity);
        iMGFileChooserResponsivity.addImageSelectedListner(this);
    }

    public JScrollPane getScrollableScalableImageContainer() {
        return scalableImageContainer.getContainer();
    }

    public JPanel getFormContainer() {
        return panelContainer;
    }

    @Override
    public void imageSelected(BufferedImage bufferedImage) {
        //     ByteArrayInputStream bis = new ByteArrayInputStream(bufferedImage);
        //   try {
        //      BufferedImage image = ImageIO.read(bis);
//            lbImagePreview.setIcon(new ImageIcon(bufferedImage));
        scalableImageContainer.loadBufferedImage(bufferedImage);
        // } catch (IOException ex) {
        //      Logger.getLogger(ItemFormImage.class.getName()).log(Level.SEVERE, null, ex);
        //  }
    }

}
