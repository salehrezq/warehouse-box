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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import warehouse.db.model.Image;
import warehouse.panel.createandupdate.ImageRemovedListener;

/**
 *
 * @author Saleh
 */
public class IMGFileChooser implements
        ActionListener,
        ImageRemovedListener {

    private Component parent;
    private JFileChooser fileChooser;
    private Preferences prefs;
    private static final String LAST_USED_FOLDER = "lastusedfolder";
    private java.util.List<ImagesSelectedListener> imagesSelectedListeners;
    private List<FilesSelectionLimitListener> filesSelectionLimitListeners;
    private static int maxSelectedFiles = 5;
    private File[] limitedFilesSelection;
    private int incrementedFilesSelecionLength, previousIncrementedFilesSelecionLength;
    private ArrayList<Image> imagesSelectedByUser;
    private List<File> filesChosenByUser;

    public IMGFileChooser() {
        imagesSelectedByUser = new ArrayList<>();
        filesChosenByUser = new ArrayList<>();
        this.imagesSelectedListeners = new ArrayList<>();
        filesSelectionLimitListeners = new ArrayList<>();
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

    public void addFilesSelectionLimitListener(FilesSelectionLimitListener filesSelectionLimitListener) {
        this.filesSelectionLimitListeners.add(filesSelectionLimitListener);
    }

    public void notifyFilesSelectionLimitReached() {
        this.filesSelectionLimitListeners.forEach((filesSelectionLimitListener) -> {
            filesSelectionLimitListener.limitReached();
        });
    }

    public void notifyFilesSelectionLimitReset() {
        this.filesSelectionLimitListeners.forEach((filesSelectionLimitListener) -> {
            filesSelectionLimitListener.limitReset();
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
            fileChooser.addPropertyChangeListener(
                    JFileChooser.SELECTED_FILES_CHANGED_PROPERTY,
                    new FileChooserSelectionLimitHandler());
        }
        int returnedValue = fileChooser.showDialog(parent, "Select image");
        if (returnedValue == JFileChooser.APPROVE_OPTION) {
            // Selection process already happened in PropertyChangeListener
            int length = filesChosenByUser.size();
            // Keep track of count of files selection
            // through multiple files chooser opens
            incrementedFilesSelecionLength += length;
            if (incrementedFilesSelecionLength >= maxSelectedFiles) {
                notifyFilesSelectionLimitReached();
            }
            imagesSelectedByUser = processSelectedImages(imagesSelectedByUser, filesChosenByUser.toArray(File[]::new));
            notifyImagesSelected(imagesSelectedByUser);
            prefs.put(LAST_USED_FOLDER, fileChooser.getSelectedFile().getParent());
        }
        clearFileChooserCurrentSelection();
    }

    private ArrayList<Image> processSelectedImages(ArrayList<Image> images, File[] files) {
        /**
         * You have to make account for set order to adjust more order number
         * keep note of length increment.
         *
         * Later you will account for image remove
         */
        int length = files.length;
        System.out.println("files length " + length);
        System.out.println("lastValue " + previousIncrementedFilesSelecionLength);
        //
        for (int i = 0; i < length; i++) {
            try {
                BufferedImage bufferedImage = ImageIO.read(files[i]);
                Image image = new Image();
                // Maintain image order throgh different file chooser opens
                int imageOrder = i + previousIncrementedFilesSelecionLength + 1;
                System.out.println("imageorder " + imageOrder);
                image.setOrder(imageOrder);
                image.setBufferedImage(bufferedImage);
                images.add(image);
                // Swap default image; maintain last selected image to be default image
                images.forEach(item -> item.setDefaultImage((item.getOrder() == imageOrder)));
            } catch (IOException ex) {
                Logger.getLogger(IMGFileChooser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Here to record the files selection length of the previous file chooser open
        previousIncrementedFilesSelecionLength = incrementedFilesSelecionLength;
        System.out.println("next order " + previousIncrementedFilesSelecionLength + 1);
        System.out.println("--------------------------");
        return images;
    }

    private void clearFileChooserCurrentSelection() {
        File f = new File("");
        File[] filesf = {f};
        fileChooser.setSelectedFile(f);
        fileChooser.setSelectedFiles(filesf);
    }

    @Override
    public void imageRemoved(Image removedImage) {
        System.out.println("Removed image has order value of: " + removedImage.getOrder());
    }

    private class FileChooserSelectionLimitHandler implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            /**
             * Ensure order of selection. Ensure that files added in the order
             * they were selected
             */
            List<File> selected = Arrays.asList(selectedFiles);
            Iterator<File> chosenIterator = filesChosenByUser.iterator();
            while (chosenIterator.hasNext()) {
                if (!selected.contains(chosenIterator.next())) {
                    chosenIterator.remove();
                }
            }
            for (File file : selected) {
                if (!filesChosenByUser.contains(file)) {
                    filesChosenByUser.add(file);
                }
            }
            /**
             * Maintains the file selection length limit. If the new selection
             * exceeds the limit, it prevents that by excluding that file, while
             * keeping the already selected files selected. Also it notifies the
             * user with a pop up message telling the limit.
             *
             * Note: the limit is cumulative such that multiple file chooser
             * opens are accounted.
             */
            int currentSelectionLength = selectedFiles.length;
            int currentAndIncrementedSelectionLengths = currentSelectionLength + incrementedFilesSelecionLength;
            if (currentAndIncrementedSelectionLengths == maxSelectedFiles) {
                limitedFilesSelection = new File[currentSelectionLength];
                System.arraycopy(selectedFiles, 0, limitedFilesSelection, 0, currentSelectionLength);
            } else if (currentAndIncrementedSelectionLengths > maxSelectedFiles) {
                fileChooser.setSelectedFiles(limitedFilesSelection);
                JOptionPane.showMessageDialog(fileChooser, "Only 5 selected files allowed.", "File chooser",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
