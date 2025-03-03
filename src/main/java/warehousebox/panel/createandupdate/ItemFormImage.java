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
package warehousebox.panel.createandupdate;

import warehousebox.utility.imagefilechooser.ImageRemovedListener;
import warehousebox.utility.imagefilechooser.IMGFileChooser;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import warehousebox.utility.horizontalspinner.Renderer;
import warehousebox.utility.horizontalspinner.SpinnerH;
import warehousebox.utility.imagefilechooser.FilesSelectionLimitListener;
import warehousebox.utility.imagepane.ScrollableScalableImageContainer;
import warehousebox.utility.imagefilechooser.ImagesSelectedListener;
import warehousebox.db.CRUDImages;
import warehousebox.db.model.Image;

/**
 *
 * @author Saleh
 */
@SuppressWarnings("LeakingThisInConstructor")
public class ItemFormImage implements
        ImagesSelectedListener,
        Collectable,
        FilesSelectionLimitListener {

    private JPanel panelContainer, panelContols;
    private JButton btnBrowse, btnSetDefaultImage;
    private JLabel btnRemove;
    private ImageIcon imageIconRemoveNormal, imageIconRemoveHover, imageIconRemovePress;
    private ScrollableScalableImageContainer scalableImageContainer;
    private IMGFileChooser iMGFileChooser;
    private BufferedImage imageSelected;
    private Map data;
    private SpinnerH spinnerH;
    private HashMap<Integer, Image> imagesMap;
    private List<Image> imagesSelected, imagesRetrievedFromDB;
    private int spinnerValueOnSpinning;
    private ArrayList<ImageRemovedListener> imageRemovedListeners;

    public ItemFormImage() {
        imageRemovedListeners = new ArrayList<>();
        data = new HashMap<String, BufferedImage>();
        imagesMap = new HashMap<>();
        panelContainer = new JPanel(new BorderLayout());
        scalableImageContainer = new ScrollableScalableImageContainer();
        scalableImageContainer.noImageFeedback();
        panelContols = new JPanel();
        btnBrowse = new JButton("Browse...");
        spinnerH = new SpinnerH();
        spinnerH.setModel(0, 0, 0, 1);
        spinnerH.getSpinner().addChangeListener(new JSpinnerHandler());
        imageIconRemoveNormal = new ImageIcon(getClass().getResource("/images/remove-icon/remove-normal.png"));
        imageIconRemoveHover = new ImageIcon(getClass().getResource("/images/remove-icon/remove-hovered.png"));
        imageIconRemovePress = new ImageIcon(getClass().getResource("/images/remove-icon/remove-pressed.png"));
        btnRemove = new JLabel();
        btnRemove.addMouseListener(new MouseEventsHandler());
        btnRemove.setOpaque(false);
        btnRemove.setIcon(imageIconRemoveNormal);
        btnSetDefaultImage = new JButton("Set default");
        btnSetDefaultImage.setEnabled(false);
        btnSetDefaultImage.addActionListener(new SetDefaultImageHandler());
        panelContols.add(btnBrowse);
        panelContols.add(spinnerH.getSpinner());
        panelContols.add(btnSetDefaultImage);
        panelContols.add(Box.createHorizontalStrut(50));
        panelContols.add(btnRemove);
        panelContainer.add(scalableImageContainer.getContainer(), BorderLayout.CENTER);
        panelContainer.add(panelContols, BorderLayout.PAGE_END);
        iMGFileChooser = new IMGFileChooser();
        btnBrowse.addActionListener(iMGFileChooser);
    }

    public JScrollPane getScrollableScalableImageContainer() {
        return scalableImageContainer.getContainer();
    }

    public JPanel getFormContainer() {
        return panelContainer;
    }

    @Override
    public void imagesSelected(List<Image> images) {
        imagesSelected = images;
        int imagesCount = images.size();
        int spinnerValue = 0;
        if (imagesCount > 0) {
            for (Image image : images) {
                imagesMap.put(image.getPosition(), image);
                if (image.getPosition() == imagesCount) {
                    scalableImageContainer.setImage(image);
                    spinnerValue = image.getPosition();
                    spinnerValueOnSpinning = spinnerValue;
                    btnSetDefaultImage.setEnabled(!image.isDefaultImage());
                }
            }
        } else {
            spinnerValueOnSpinning = 0;
            imagesMap.clear();
            scalableImageContainer.setImage(null);
            scalableImageContainer.noImageFeedback();
        }
        spinnerH.setModel(spinnerValue, (imagesCount > 0) ? 1 : 0, imagesCount, 1);
    }

    @Override
    public void imagesSelectedAfterImageRemoved(List<Image> images, Image removedImage) {
        imagesSelected = images;
        int imagesCount = images.size();
        int spinnerValue = 0;

        if (imagesCount > 0) {
            images.forEach(image -> {
                imagesMap.put(image.getPosition(), image);
            });

            Image imageReplacesRemovedImage;
            if ((removedImage.getPosition() - 1) != imagesCount) {
                // The removed image was at any position other than the end.
                imageReplacesRemovedImage = images.stream().filter(image -> image.getPosition() == removedImage.getPosition()).findFirst().get();
            } else {
                // The removed image was only at the end position.
                imageReplacesRemovedImage = images.stream().filter(image -> image.getPosition() == imagesCount).findFirst().get();
            }

            if (imageReplacesRemovedImage != null) {
                scalableImageContainer.setImage(imageReplacesRemovedImage);
                spinnerValue = imageReplacesRemovedImage.getPosition();
                spinnerValueOnSpinning = spinnerValue;
                btnSetDefaultImage.setEnabled(!imageReplacesRemovedImage.isDefaultImage());
            }
        } else {
            spinnerValueOnSpinning = 0;
            imagesMap.clear();
            scalableImageContainer.setImage(null);
            scalableImageContainer.noImageFeedback();
        }
        spinnerH.setModel(spinnerValue, (imagesCount > 0) ? 1 : 0, imagesCount, 1);
    }

    private void loadedImages(List<Image> images) {
        imagesSelected = images;
        int imagesCount = images.size();
        int spinnerValue = 0;
        if (imagesCount > 0) {
            for (Image image : images) {
                imagesMap.put(image.getPosition(), image);
                if (image.isDefaultImage()) {
                    scalableImageContainer.setImage(image);
                    spinnerValue = image.getPosition();
                    spinnerValueOnSpinning = spinnerValue;
                    btnSetDefaultImage.setEnabled(false);
                }
            }
        } else {
            spinnerValueOnSpinning = 0;
            imagesMap.clear();
            scalableImageContainer.setImage(null);
            scalableImageContainer.noImageFeedback();
        }
        spinnerH.setModel(spinnerValue, (imagesCount > 0) ? 1 : 0, imagesCount, 1);
    }

    protected void loadItemImages(int itemId) {
        imagesSelected = CRUDImages.getImagesByItemId(itemId);
        imagesRetrievedFromDB = new ArrayList<>(imagesSelected);
        loadedImages(imagesSelected);
        iMGFileChooser.setUpLoadedImagesForUpdate(imagesSelected);
        // iMGFileChooser notify it about files state
        // cashe alvailable files in case user cancel updating, so we perserve them
        // noify iMGFileChooser about images order and count
        // accomdate it however you do it
    }

    @Override
    public Map collect() {
        data.put("images", imagesSelected);
        data.put("imagesRetrievedFromDB", imagesRetrievedFromDB);
        return data;
    }

    @Override
    public void limitReached() {
        btnBrowse.setEnabled(false);
    }

    @Override
    public void limitReset() {
        btnBrowse.setEnabled(true);
    }

    protected JButton getBtnBrowse() {
        return btnBrowse;
    }

    public void resetFields() {
        spinnerValueOnSpinning = 0;
        imagesMap.clear();
        scalableImageContainer.setImage(null);
        scalableImageContainer.noImageFeedback();
        spinnerH.setModel(0, 0, 0, 1);
        btnSetDefaultImage.setEnabled(false);
    }

    public IMGFileChooser getIMGFileChooser() {
        return iMGFileChooser;
    }

    public void addImageRemovedListener(ImageRemovedListener imageRemovedListener) {
        this.imageRemovedListeners.add(imageRemovedListener);
    }

    public void notifyImageRemoved(Image removedImage) {
        this.imageRemovedListeners.forEach((imageRemovedListener) -> {
            imageRemovedListener.imageRemoved(removedImage);
        });
    }

    private class SetDefaultImageHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Image imageToBeSetAsDefault = imagesMap.get(spinnerValueOnSpinning);
            imagesSelected.stream().forEach(image -> {
                if (imageToBeSetAsDefault == image) {
                    image.setDefaultImage(true);
                } else {
                    image.setDefaultImage(false);
                }
            });
            btnSetDefaultImage.setEnabled(false);
        }
    }

    private class JSpinnerHandler implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            if (!imagesMap.isEmpty()) {
                JSpinner spinner = (JSpinner) e.getSource();
                Renderer renderer = (Renderer) spinner.getValue();
                spinnerValueOnSpinning = renderer.getValue();
                Image image = imagesMap.get(spinnerValueOnSpinning);
                btnSetDefaultImage.setEnabled(!image.isDefaultImage());
                scalableImageContainer.setImage(image);
            }
        }
    }

    private class MouseEventsHandler extends MouseAdapter {

        private boolean hovered = false;

        @Override
        public void mouseClicked(MouseEvent e) {
            notifyImageRemoved(imagesMap.get(spinnerValueOnSpinning));
        }

        @Override
        public void mousePressed(MouseEvent e) {
            btnRemove.setIcon(imageIconRemovePress);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            btnRemove.setIcon((hovered) ? imageIconRemoveHover : imageIconRemoveNormal);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            hovered = true;
            btnRemove.setIcon(imageIconRemoveHover);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            hovered = false;
            btnRemove.setIcon(imageIconRemoveNormal);
        }

    }

}
