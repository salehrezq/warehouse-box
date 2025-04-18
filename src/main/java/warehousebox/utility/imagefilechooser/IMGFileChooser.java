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
package warehousebox.utility.imagefilechooser;

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
import warehousebox.db.model.Image;

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
    private List<Image> imagesSelectedByUser;
    private List<File> filesChosenByUser;

    public IMGFileChooser() {
        imagesSelectedByUser = new ArrayList<>();
        filesChosenByUser = new ArrayList<>();
        this.imagesSelectedListeners = new ArrayList<>();
        filesSelectionLimitListeners = new ArrayList<>();
    }

    public void resetFields() {
        imagesSelectedByUser.clear();
        filesChosenByUser.clear();
        incrementedFilesSelecionLength = 0;
        previousIncrementedFilesSelecionLength = 0;
    }

    public void setParentComponent(Component parent) {
        this.parent = parent;
    }

    public void addImageSelectedListener(ImagesSelectedListener imagesSelectedListener) {
        this.imagesSelectedListeners.add(imagesSelectedListener);
    }

    public void notifyImagesSelected(List<Image> bufferedImages) {
        this.imagesSelectedListeners.forEach((imagesSelectedListener) -> {
            imagesSelectedListener.imagesSelected(bufferedImages);
        });
    }

    public void notifyImagesSelectedAfterImageRemoved(List<Image> imagesSelected, Image removedImage) {
        this.imagesSelectedListeners.forEach((imagesSelectedListener) -> {
            imagesSelectedListener.imagesSelectedAfterImageRemoved(imagesSelected, removedImage);
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
            // across multiple files chooser opens
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

    public void setUpLoadedImagesForUpdate(List<Image> images) {
        incrementedFilesSelecionLength = images.size();
        previousIncrementedFilesSelecionLength = images.size();
        imagesSelectedByUser = images;
    }

    private List<Image> processSelectedImages(List<Image> images, File[] files) {
        /**
         * You have to make account for set order to adjust more order number
         * keep note of length increment.
         *
         * Later you will account for image remove
         */
        int length = files.length;
        //
        for (int i = 0; i < length; i++) {
            try {
                Image image = new Image();
                image.setImageFile(files[i]);
                BufferedImage bufferedImage = ImageIO.read(files[i]);
                // Maintain image order throgh different file chooser opens
                int imageOrder = i + previousIncrementedFilesSelecionLength + 1;
                image.setPosition(imageOrder);
                image.setBufferedImage(bufferedImage);
                images.add(image);
                // Set the first image, image of order one to be the default image.
                if (image.getPosition() == 1) {
                    image.setDefaultImage(true);
                }
            } catch (IOException ex) {
                Logger.getLogger(IMGFileChooser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        /**
         * Keep track of files count from the previous file chooser open, to
         * know which position to continue counting from, for the purpose of
         * ordering the files.
         */
        previousIncrementedFilesSelecionLength = incrementedFilesSelecionLength;
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

        int sizeBeforRemoval = imagesSelectedByUser.size();
        if (sizeBeforRemoval > 0) {
            int removedImageOrder = removedImage.getPosition();
            boolean removed = imagesSelectedByUser.removeIf(image -> image.getPosition() == removedImage.getPosition());
            int sizeAfterRemoval = imagesSelectedByUser.size();
            if (removed) {
                incrementedFilesSelecionLength -= 1;
                previousIncrementedFilesSelecionLength = incrementedFilesSelecionLength;
                notifyFilesSelectionLimitReset();
                // Check that there are more than one image in the ArrayList collection.
                if (sizeBeforRemoval > 1) {
                    /**
                     * Check that the removed image has any position other than
                     * the end of the ArrayList collection. If the removed image
                     * has any position other than the end of the ArrayList
                     * collection, it is required to recalculate the positions
                     * (images orders) to adjust all the images positions that
                     * come after the removed image in terms of their positions
                     * after the removed image, to fill the gab.
                     */
                    if (removedImageOrder < sizeBeforRemoval) {
                        for (int i = 0; i < sizeAfterRemoval; i++) {
                            Image image = imagesSelectedByUser.get(i);
                            imagesSelectedByUser.get(i).setPosition(
                                    (image.getPosition() > removedImageOrder)
                                    ? image.getPosition() - 1
                                    : image.getPosition());
                        }
                    }
                }
                /**
                 * If the removed image was a default image, then set the image
                 * of order 1 to be the default.
                 */
                if (sizeAfterRemoval > 0 && removedImage.isDefaultImage()) {
                    Image image = imagesSelectedByUser.get(0);
                    image.setDefaultImage(true);
                }
            }
            notifyImagesSelectedAfterImageRemoved(imagesSelectedByUser, removedImage);
        }
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
