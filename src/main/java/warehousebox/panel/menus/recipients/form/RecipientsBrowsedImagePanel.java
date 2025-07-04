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
public class RecipientsBrowsedImagePanel implements ImageSelectedListener {

    private JPanel container;
    private JLabel lbImage;
    private BufferedImage bufferedImageThumbnailed;
    private final BufferedImage bufferedImagePlaceholder;
    private RecipientImage recipientImage;
    private RecipientImage recipientImageFromDB;
    /**
     * Image presence either loaded from db or selected through browsing; both
     * is true state, otherwise it is false state
     */
    private boolean isImagePresence;
    /**
     * When image is selected through browsing it is true state, and when image
     * loaded from db, or removed where placeholder image is placed instead it
     * is false state
     */
    private boolean isImageSelected;
    /**
     * Image loaded through database name; the image is already available in the
     * app directory with its name recorded in the database
     */
    private boolean isImageLoaded;

    private boolean isImageRemoved;

    public RecipientsBrowsedImagePanel() {
        container = new JPanel();
        lbImage = new JLabel();
        lbImage.setMinimumSize(new Dimension(128, 128));
        lbImage.setHorizontalAlignment(JLabel.CENTER);
        lbImage.setVerticalAlignment(JLabel.CENTER);
        bufferedImagePlaceholder
                = thumbnail(readImageFromResource(getClass().getResource("/images/avatar-placeholder/avatar.png")));
        lbImage.setIcon(new ImageIcon(bufferedImagePlaceholder));
        isImagePresence = false;
        isImageSelected = false;
        container.add(lbImage);
    }

    protected JPanel getContainer() {
        return container;
    }

    protected RecipientImage getRecipientImage() {
        return recipientImage;
    }

    protected void setRecipientImage(RecipientImage recipientImage) {
        this.recipientImage = recipientImage;
    }

    protected RecipientImage getRecipientImageFromDB() {
        return recipientImageFromDB;
    }

    public boolean isImagePresence() {
        return isImagePresence;
    }

    public boolean isImageSelected() {
        return isImageSelected;
    }

    public boolean isImageLoaded() {
        return isImageLoaded;
    }

    public boolean isImageRemoved() {
        return isImageRemoved;
    }

    private BufferedImage thumbnail(BufferedImage image) {
        try {
            return Thumbnails.of(image)
                    .size(128, 128)
                    .asBufferedImage();
        } catch (IOException ex) {
            Logger.getLogger(RecipientsBrowsedImagePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private BufferedImage readImageFromFile(File file) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException ex) {
            Logger.getLogger(RecipientsBrowsedImagePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bufferedImage;
    }

    protected void removeSelectedImage() {
        recipientImage = null;
        bufferedImageThumbnailed = null;
        lbImage.setIcon(new ImageIcon(bufferedImagePlaceholder));
        isImageRemoved = true;
        isImagePresence = false;
        isImageSelected = false;
    }

    protected void resetBooleans() {
        isImageLoaded = false;
        isImageSelected = false;
        isImageRemoved = false;
    }

    private BufferedImage readImageFromResource(URL url) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(url);
            return bufferedImage;
        } catch (IOException ex) {
            Logger.getLogger(RecipientsBrowsedImagePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bufferedImage;
    }

    /**
     * Load image through its name in the database. The image itself was stored
     * previously in the app directory.
     *
     * @param recipientImage
     */
    public void imageLoaded(RecipientImage recipientImage) {
        if (recipientImage != null) {
            recipientImageFromDB = new RecipientImage(recipientImage);
            this.recipientImage = recipientImage;
            bufferedImageThumbnailed = this.recipientImage.getBufferedImage();

            if (bufferedImageThumbnailed == null) {
                lbImage.setIcon(null);
                return;
            }
            isImageLoaded = true;
            this.recipientImage.setBufferedImageThumbnailed(bufferedImageThumbnailed);
            lbImage.setIcon(new ImageIcon(bufferedImageThumbnailed));
        } else {
            isImageLoaded = false;
        }
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
        isImageRemoved = false;
        isImageSelected = true;
        this.recipientImage.setBufferedImageThumbnailed(bufferedImageThumbnailed);
        lbImage.setIcon(new ImageIcon(bufferedImageThumbnailed));
    }
}
