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
package warehousebox.db.model;

import java.awt.image.BufferedImage;
import java.io.File;
import warehousebox.db.CRUDRecipientsImages;
import warehousebox.utility.filemanage.ImageFileManager;

/**
 *
 * @author Saleh
 */
public class RecipientImage {

    private int id;
    private int recipientId;
    private String imageName;

    /**
     * Following members are helper to the program, not mapped in the database.
     */
    private BufferedImage bufferedImage;
    private BufferedImage bufferedImageThumbnailed;
    private File imageFile;

    public RecipientImage() {

    }

    public RecipientImage(RecipientImage recipientImage) {
        this.id = recipientImage.getId();
        this.recipientId = recipientImage.getRecipientId();
        this.imageName = recipientImage.getImageName();
        this.bufferedImage = recipientImage.getBufferedImage();
        this.bufferedImageThumbnailed = recipientImage.getBufferedImageThumbnailed();
        this.imageFile = recipientImage.getImageFile();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
        this.setBufferedImage(ImageFileManager.loadImage(imageName, CRUDRecipientsImages.DIRECTORYNAME));
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public BufferedImage getBufferedImageThumbnailed() {
        return bufferedImageThumbnailed;
    }

    public void setBufferedImageThumbnailed(BufferedImage bufferedImageThumbnailed) {
        this.bufferedImageThumbnailed = bufferedImageThumbnailed;
    }
}
