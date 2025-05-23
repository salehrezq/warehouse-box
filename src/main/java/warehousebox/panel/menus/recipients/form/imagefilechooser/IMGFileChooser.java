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
package warehousebox.panel.menus.recipients.form.imagefilechooser;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import warehousebox.db.model.RecipientImage;

/**
 *
 * @author Saleh
 */
public class IMGFileChooser implements ActionListener {

    private JFileChooser fileChooser;
    private Preferences prefs;
    private static final String LAST_USED_FOLDER = "lastusedfolder";
    private Component parent;
    private List<ImageSelectedListener> imageSelectedListeners;

    public IMGFileChooser() {
        imageSelectedListeners = new ArrayList<>();
    }

    public void setParentComponent(Component parent) {
        this.parent = parent;
    }

    public void addImageSelectedListener(ImageSelectedListener imageSelectedListener) {
        this.imageSelectedListeners.add(imageSelectedListener);
    }

    public void notifyImageSelected(RecipientImage recipientImage) {
        this.imageSelectedListeners.forEach((imageSelectedListener) -> {
            imageSelectedListener.imageSelected(recipientImage);
        });
    }

    private void clearFileChooserCurrentSelection() {
        File f = new File("");
        File[] filesf = {f};
        fileChooser.setSelectedFile(f);
        fileChooser.setSelectedFiles(filesf);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (fileChooser == null) {
            prefs = Preferences.userRoot().node(getClass().getName());
            fileChooser = new JFileChooser(prefs.get(LAST_USED_FOLDER, new File(".").getAbsolutePath()));
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.addChoosableFileFilter(new ImageFilter());
            fileChooser.setAcceptAllFileFilterUsed(false);
        }
        int returnedValue = fileChooser.showDialog(parent, "Select image");
        if (returnedValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            RecipientImage recipientImage = new RecipientImage();
            BufferedImage bufferedImage;
            try {
                bufferedImage = ImageIO.read(file);
                recipientImage.setBufferedImage(bufferedImage);
            } catch (IOException ex) {
                Logger.getLogger(IMGFileChooser.class.getName()).log(Level.SEVERE, null, ex);
            }
            recipientImage.setImageFile(file);
            notifyImageSelected(recipientImage);
            prefs.put(LAST_USED_FOLDER, fileChooser.getSelectedFile().getParent());
        }
        clearFileChooserCurrentSelection();
    }

}
