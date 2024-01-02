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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

/**
 *
 * @author Saleh
 */
public class IMGFileChooser implements ActionListener {

    private Component parent;
    private JFileChooser fileChooser;
    private byte[] photoInBytes;
    private BufferedImage image;
    private Preferences prefs;
    private static final String LAST_USED_FOLDER = "lastusedfolder";
    private java.util.List<ImageSelectedListner> imageSelectedListners;

    public IMGFileChooser() {
        this.imageSelectedListners = new ArrayList<>();
    }

    public void setParentComponent(Component parent) {
        this.parent = parent;
    }

    public byte[] getPhotoInBytes() {
        return photoInBytes;
    }

    public void setPhotoInBytes(byte[] aPhotoInBytes) {
        photoInBytes = aPhotoInBytes;
    }

    public BufferedImage getBufferedImage() {
        return image;
    }

    public void addImageSelectedListner(ImageSelectedListner var) {
        this.imageSelectedListners.add(var);
    }

    public void notifyImageSelected(byte[] photoInBytesvar) {
        this.imageSelectedListners.forEach((implementer) -> {
            implementer.imageSelected(photoInBytesvar);
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (fileChooser == null) {
            prefs = Preferences.userRoot().node(getClass().getName());
            fileChooser = new JFileChooser(prefs.get(LAST_USED_FOLDER, new File(".").getAbsolutePath()));
            fileChooser.addChoosableFileFilter(new ImageFilter());
            fileChooser.setAcceptAllFileFilterUsed(false);
        }

        int returnedValue = fileChooser.showDialog(parent, "Select employee image");

        if (returnedValue == JFileChooser.APPROVE_OPTION) {
            try {
                File f = fileChooser.getSelectedFile();
                image = ImageIO.read(f);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", baos);
                baos.flush();
                photoInBytes = baos.toByteArray();
                notifyImageSelected(photoInBytes);
                prefs.put(LAST_USED_FOLDER, fileChooser.getSelectedFile().getParent());
            } catch (IOException ex) {
                Logger.getLogger(IMGFileChooser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
