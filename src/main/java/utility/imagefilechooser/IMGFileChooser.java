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
package utility.imagefilechooser;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import warehouse.db.model.Image;

/**
 *
 * @author Saleh
 */
public class IMGFileChooser implements ActionListener {

    private Component parent;
    private JFileChooser fileChooser;
    private Preferences prefs;
    private static final String LAST_USED_FOLDER = "lastusedfolder";
    private java.util.List<ImagesSelectedListener> imagesSelectedListeners;

    public IMGFileChooser() {
        this.imagesSelectedListeners = new ArrayList<>();
    }

    public void setParentComponent(Component parent) {
        this.parent = parent;
    }

    public void addImageSelectedListener(ImagesSelectedListener imagesSelectedListener) {
        this.imagesSelectedListeners.add(imagesSelectedListener);
    }

    public void notifyImagesSelected(ArrayList<Image> bufferedImages) {
        this.imagesSelectedListeners.forEach((imagesSelectedListener) -> {
            imagesSelectedListener.imagesSelected(bufferedImages);
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (fileChooser == null) {
            prefs = Preferences.userRoot().node(getClass().getName());
            fileChooser = new JFileChooser(prefs.get(LAST_USED_FOLDER, new File(".").getAbsolutePath()));
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.addChoosableFileFilter(new ImageFilter());
            fileChooser.setAcceptAllFileFilterUsed(false);
        }

        int returnedValue = fileChooser.showDialog(parent, "Select image");

        if (returnedValue == JFileChooser.APPROVE_OPTION) {
            try {
                File[] files = fileChooser.getSelectedFiles();
                ArrayList<Image> images = new ArrayList<>();
                int length = files.length;
                for (int i = 0; i < length; i++) {
                    BufferedImage bufferedImage = ImageIO.read(files[i]);
                    Image image = new Image();
                    if (i == 0) {
                        // Set the first selected image as the default one
                        image.setDefaultImage(true);
                    }
                    image.setOrder(i + 1);
                    image.setBufferedImage(bufferedImage);
                    images.add(image);
                }
                notifyImagesSelected(images);
                prefs.put(LAST_USED_FOLDER, fileChooser.getSelectedFile().getParent());
            } catch (IOException ex) {
                Logger.getLogger(IMGFileChooser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
