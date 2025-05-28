/*
 * The MIT License
 *
 * Copyright 2025 Saleh.
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
package warehousebox.panel.menus.recipients.form;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.coobird.thumbnailator.Thumbnails;
import warehousebox.db.model.RecipientImage;
import warehousebox.panel.menus.recipients.form.imagefilechooser.ImageSelectedListener;

/**
 *
 * @author Saleh
 */
public class RecipientsImagePanel implements ImageSelectedListener {

    private JPanel container;
    private JLabel lbImage;
    BufferedImage bufferedImage;

    public RecipientsImagePanel() {
        container = new JPanel();
        lbImage = new JLabel();
        lbImage.setHorizontalAlignment(JLabel.CENTER);
        lbImage.setVerticalAlignment(JLabel.CENTER);
        container.add(lbImage);
    }

    protected JPanel getContainer() {
        return container;
    }

    @Override
    public void imageSelected(RecipientImage recipientImage) {
        try {
            BufferedImage originalImage = ImageIO.read(recipientImage.getImageFile());
            bufferedImage = Thumbnails.of(originalImage)
                    .size(190, 200)
                    .asBufferedImage();

            if (bufferedImage == null) {
                lbImage.setIcon(null);
                return;
            }
            lbImage.setIcon(new ImageIcon(bufferedImage));
        } catch (IOException ex) {
            Logger.getLogger(RecipientsImagePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
