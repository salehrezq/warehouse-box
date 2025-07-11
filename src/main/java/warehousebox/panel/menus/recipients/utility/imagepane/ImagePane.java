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
package warehousebox.panel.menus.recipients.utility.imagepane;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import net.coobird.thumbnailator.Thumbnails;
import warehousebox.db.model.RecipientImage;

/**
 *
 * @author Saleh
 */
public class ImagePane {

    private JLabel lbImage;
    private RecipientImage recipientImage;
    public BufferedImage bufferedImage;
    private NoImageResponse noImageResponse;
    private LoadingFeedback loadingFeedback;
    private int width, height;

    public ImagePane(int width, int height) {
        this.width = width;
        this.height = height;
        lbImage = new JLabel();
        lbImage.setMaximumSize(new Dimension(width, height));
        lbImage.setHorizontalAlignment(JLabel.CENTER);
        lbImage.setVerticalAlignment(JLabel.CENTER);
        noImageResponse = new NoImageResponse(lbImage);
        loadingFeedback = new LoadingFeedback(lbImage);
    }

    public void setImageLoading128px() {
        loadingFeedback.setImageLoading128px();
    }

    public void setImageLoading64px() {
        loadingFeedback.setImageLoading64px();
    }

    public void setImage(RecipientImage image) {
        this.recipientImage = image;
        bufferedImage = (recipientImage != null) ? recipientImage.getBufferedImage() : null;
        setImageIcone(bufferedImage);
    }

    public void setImageIcone(BufferedImage image) {
        if (image == null) {
            lbImage.setIcon(null);
            return;
        }
        try {
            image = Thumbnails.of(image)
                    .size(width, height)
                    .asBufferedImage();
            lbImage.setIcon(new ImageIcon(image));
        } catch (IOException ex) {
            Logger.getLogger(ImagePane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void noImageResponseAnimated() {
        noImageResponse.noImageAnimate();
    }

    public void noImageFeedback() {
        noImageResponse.noImageStillFrame();
    }

    public void stopAnimation() {
        noImageResponse.stopAnimation();
    }

    public void loading() {
        loadingFeedback.loading();
    }

    public JLabel getContainer() {
        return lbImage;
    }

}
