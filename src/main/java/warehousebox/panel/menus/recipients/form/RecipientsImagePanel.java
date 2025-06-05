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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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
    private BufferedImage bufferedImageThumbnailed;
    private final BufferedImage bufferedImagePlaceholder;
    private RecipientImage recipientImage;

    public RecipientsImagePanel() {
        container = new JPanel();
        lbImage = new JLabel();
        lbImage.setMinimumSize(new Dimension(190, 200));
        lbImage.setHorizontalAlignment(JLabel.CENTER);
        lbImage.setVerticalAlignment(JLabel.CENTER);
        bufferedImagePlaceholder
                = thumbnail(readImageFromResource(getClass().getResource("/images/avatar-placeholder/avatar.png")));
        lbImage.setIcon(new ImageIcon(bufferedImagePlaceholder));
        container.add(lbImage);
    }

    protected JPanel getContainer() {
        return container;
    }

    protected RecipientImage getRecipientImage() {
        return recipientImage;
    }

    private BufferedImage thumbnail(BufferedImage image) {
        try {
            return Thumbnails.of(image)
                    .size(190, 200)
                    .asBufferedImage();
        } catch (IOException ex) {
            Logger.getLogger(RecipientsImagePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private BufferedImage readImageFromFile(File file) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException ex) {
            Logger.getLogger(RecipientsImagePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bufferedImage;
    }

    public void removeSelectedImage() {
        recipientImage = null;
        bufferedImageThumbnailed = null;
        lbImage.setIcon(new ImageIcon(bufferedImagePlaceholder));
    }

    private BufferedImage readImageFromResource(URL url) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(url);
            return bufferedImage;
        } catch (IOException ex) {
            Logger.getLogger(RecipientsImagePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bufferedImage;
    }

    @Override
    public void imageSelected(RecipientImage recipientImage) {
        this.recipientImage = recipientImage;
        BufferedImage originalImage = readImageFromFile(recipientImage.getImageFile());
        bufferedImageThumbnailed = thumbnail(originalImage);

        if (bufferedImageThumbnailed == null) {
            lbImage.setIcon(null);
            return;
        }
        lbImage.setIcon(new ImageIcon(bufferedImageThumbnailed));
    }
}
