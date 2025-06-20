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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import warehousebox.db.CRUDRecipients;
import warehousebox.db.CRUDRecipientsImages;
import warehousebox.db.model.Recipient;
import warehousebox.db.model.RecipientImage;
import warehousebox.panel.menus.recipients.form.imagefilechooser.IMGFileChooser;
import warehousebox.utility.filemanage.ImageFileManager;

/**
 *
 * @author Saleh
 */
public class RecipientsFormLogic {

    private JTextField tfName;
    private JButton btnBrowse, btnSubmit;
    private ImageIcon imageIconRemoveNormal, imageIconRemoveHover, imageIconRemovePress;
    private JLabel btnRemove;
    private IMGFileChooser iMGFileChooser;
    private RecipientsBrowsedImagePanel recipientsBrowsedImagePanel;
    private List<RecipientCRUDListener> recipientCRUDListeners;
    private RecipientsFormControls recipientsFormControls;
    private String recipientNameOld;

    public RecipientsFormLogic(RecipientsFormControls rc) {
        recipientsFormControls = rc;
        btnBrowse = rc.getBtnBrowse();
        imageIconRemoveNormal = rc.getImageIconRemoveNormal();
        imageIconRemoveHover = rc.getImageIconRemoveHover();
        imageIconRemovePress = rc.getImageIconRemovePress();
        btnRemove = rc.getBtnRemove();
        recipientsBrowsedImagePanel = rc.getRecipientsBrowsedImagePanel();
        btnSubmit = rc.getBtnSubmit();
        tfName = rc.getTfName();

        btnRemove.addMouseListener(new BtnRemoveHandler());
        recipientCRUDListeners = new ArrayList<>();
        iMGFileChooser = new IMGFileChooser();
        iMGFileChooser.addImageSelectedListener(recipientsBrowsedImagePanel);
        btnBrowse.addActionListener(iMGFileChooser);
        btnSubmit.addActionListener(new SubmitHandler());
    }

    public void addRecipientCRUDListener(RecipientCRUDListener recipientCRUDListener) {
        this.recipientCRUDListeners.add(recipientCRUDListener);
    }

    public void notifyRecipientCreated(Recipient recipient) {
        this.recipientCRUDListeners.forEach((recipientCRUDListener) -> {
            recipientCRUDListener.created(recipient);
        });
    }

    public void notifyRecipientUpdated(Recipient recipient) {
        this.recipientCRUDListeners.forEach((recipientCRUDListener) -> {
            recipientCRUDListener.updated(recipient);
        });
    }

    public void notifyNoCRUD() {
        this.recipientCRUDListeners.forEach((recipientCRUDListener) -> {
            recipientCRUDListener.noCRUD();
        });
    }

    protected void resetFields() {
        recipientsFormControls.setRecipient(null);
        tfName.setText("");
        recipientsBrowsedImagePanel.removeSelectedImage();
        recipientsBrowsedImagePanel.resetBooleans();
        recipientsBrowsedImagePanel.setRecipientImage(null);
    }

    private class SubmitHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (tfName.getText().isBlank()) {
                JOptionPane.showMessageDialog(null,
                        "Recipient name is missing!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (tfName.getText().length() > 255) {
                JOptionPane.showMessageDialog(null,
                        "Recipient name exeeds the limit of 255 charachters",
                        "Exceeded the limit",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            Recipient recipient = recipientsFormControls.getRecipient();
            if (recipient != null) {
                // Update operation
                recipientNameOld = recipient.getName();
                String recipientNameCurrent = tfName.getText();
                boolean isCurrentNameDifferentFromOldName = !recipientNameOld.equals(recipientNameCurrent);
                boolean noLoadedNoSelectedImage = !recipientsBrowsedImagePanel.isImageLoaded() && !recipientsBrowsedImagePanel.isImageSelected();
                String recipientImageNameOld = "";
                if (!isCurrentNameDifferentFromOldName && noLoadedNoSelectedImage) {
                    notifyNoCRUD();
                } else {
                    if (isCurrentNameDifferentFromOldName) {
                        recipient.setName(tfName.getText());
                        CRUDRecipients.update(recipient);
                    }
                    if (recipientsBrowsedImagePanel.isImageLoaded()
                            && !recipientsBrowsedImagePanel.isImageRemoved()
                            && !recipientsBrowsedImagePanel.isImageSelected()) {
                        System.out.println("case image loaded then no change");
                        // case image loaded then no change
                        notifyNoCRUD();
                    } else if (recipientsBrowsedImagePanel.isImageLoaded()
                            && recipientsBrowsedImagePanel.isImageRemoved()) {
                        // case image loaded then deleted, no another selected
                        System.out.println("case image loaded then deleted, no another selected");
                        RecipientImage recipientImageFromDB = recipientsBrowsedImagePanel.getRecipientImageFromDB();
                        if (recipientImageFromDB != null) {
                            // Delete RecipientImage from database
                            if (CRUDRecipientsImages.delete(recipientImageFromDB) > 0) {
                                // Delete the current image from file system
                                ImageFileManager.delete(recipientsBrowsedImagePanel
                                        .getRecipientImageFromDB()
                                        .getImageName(),
                                        CRUDRecipientsImages.DIRECTORYNAME);
                            } else {
                                JOptionPane.showMessageDialog(null,
                                        "The RecipientImage cannot be deleted due to some unknown issue!",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else if (recipientsBrowsedImagePanel.isImageLoaded()
                            && recipientsBrowsedImagePanel.isImageSelected()) {
                        // case image loaded then another selected
                        System.out.println("case image loaded then another selected");
                        // Get loaded image on recipient select
                        RecipientImage recipientImageFromDB = recipientsBrowsedImagePanel.getRecipientImageFromDB();
                        if (recipientImageFromDB != null) {
                            // Get name of current loaded image
                            recipientImageNameOld = recipientImageFromDB.getImageName();
                            // Delete the current image from file system
                            ImageFileManager.delete(recipientsBrowsedImagePanel
                                    .getRecipientImageFromDB()
                                    .getImageName(),
                                    CRUDRecipientsImages.DIRECTORYNAME);
                        }
                        // Get selected image through browsing
                        RecipientImage recipientImageSelected = recipientsBrowsedImagePanel.getRecipientImage();
                        if (recipientImageSelected != null) {
                            // Set name of the selected image to be the same as the old one
                            recipientImageSelected.setImageName(recipientImageNameOld);
                            // Copy the selected image to the app directory using the same old image name
                            BufferedImage bufferedImage = recipientImageSelected.getBufferedImageThumbnailed();
                            ImageFileManager.saveBufferedImageToFileSystem(
                                    bufferedImage,
                                    recipientImageNameOld,
                                    CRUDRecipientsImages.DIRECTORYNAME);
                        }
                    } else if (!recipientsBrowsedImagePanel.isImageLoaded()
                            && recipientsBrowsedImagePanel.isImageSelected()) {
                        System.out.println("case no image loaded, but image selected");
                        // case no image loaded, but image selected
                        // Get selected image through browsing
                        RecipientImage recipientImageSelected = recipientsBrowsedImagePanel.getRecipientImage();
                        if (recipientImageSelected != null) {
                            String newImageName = ImageFileManager.generateImageName(recipientImageSelected.getImageFile());
                            recipientImageSelected.setImageName(newImageName);
                            // Copy image to app directory
                            BufferedImage bufferedImage = recipientImageSelected.getBufferedImageThumbnailed();
                            ImageFileManager.saveBufferedImageToFileSystem(
                                    bufferedImage,
                                    newImageName,
                                    CRUDRecipientsImages.DIRECTORYNAME);
                            CRUDRecipientsImages.create(recipientImageSelected, recipient.getId());
                        }
                    }
                    notifyRecipientUpdated(recipient);
                }
                resetFields();
            } else {
                // Create operation
                recipient = new Recipient();
                recipient.setName(tfName.getText());
                if (CRUDRecipients.isExist(recipient)) {
                    JOptionPane.showMessageDialog(null,
                            "The recipient is already exist!",
                            "Duplicate entry", JOptionPane.ERROR_MESSAGE);
                } else {
                    recipient = CRUDRecipients.create(recipient);
                    if (recipient != null) {
                        RecipientImage recipientImageSelected = recipientsBrowsedImagePanel.getRecipientImage();
                        if (recipientImageSelected != null) {
                            String newImageName = ImageFileManager.generateImageName(recipientImageSelected.getImageFile());
                            recipientImageSelected.setImageName(newImageName);
                            // Copy image to app directory
                            BufferedImage bufferedImage = recipientImageSelected.getBufferedImageThumbnailed();
                            ImageFileManager.saveBufferedImageToFileSystem(
                                    bufferedImage,
                                    newImageName,
                                    CRUDRecipientsImages.DIRECTORYNAME);
                            CRUDRecipientsImages.create(recipientImageSelected, recipient.getId());
                        }
                        JOptionPane.showMessageDialog(
                                null,
                                "Recipient created successfully. You can find it on a next search",
                                "Created",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    notifyRecipientCreated(recipient);
                    resetFields();
                }
            }
        }
    }

    private class BtnRemoveHandler extends MouseAdapter {

        private boolean hovered = false;

        @Override
        public void mouseClicked(MouseEvent e) {
            recipientsBrowsedImagePanel.removeSelectedImage();
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
