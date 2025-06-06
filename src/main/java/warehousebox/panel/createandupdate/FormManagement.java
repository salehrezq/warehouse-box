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
package warehousebox.panel.createandupdate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import warehousebox.utility.filemanage.ImageFileManager;
import warehousebox.db.CRUDImages;
import warehousebox.db.CRUDItems;
import warehousebox.db.model.Image;
import warehousebox.db.model.Item;
import warehousebox.db.model.QuantityUnit;
import warehousebox.panel.items.ItemCRUDListener;

/**
 *
 * @author Saleh
 */
public class FormManagement extends JPanel {

    private JButton btnNext, btnPrevious, btnSubmit;
    private ArrayList<Navigatable> navigatables;
    private NavigateButtonsListener navigateButtonsListener;
    private int formLastStep;
    private int navigateTracker;
    private ArrayList<Collectable> collectables;
    private List<ItemCRUDListener> itemCRUDListeners;
    private ItemForm itemForm;

    public FormManagement(ArrayList<Collectable> collectables) {

        this.collectables = collectables;
        navigatables = new ArrayList<>();
        itemCRUDListeners = new ArrayList<>();
        btnNext = new JButton("Next>>");
        btnPrevious = new JButton("<<Previous");
        btnSubmit = new JButton("Submit");
        navigateButtonsListener = new NavigateButtonsListener();
        btnNext.addActionListener(navigateButtonsListener);
        btnPrevious.addActionListener(navigateButtonsListener);
        btnSubmit.addActionListener(navigateButtonsListener);
        btnSubmit.setEnabled(false);
        btnPrevious.setEnabled(false);

        add(btnPrevious);
        add(btnNext);
        add(btnSubmit);
    }

    public void setFormLastStep(int formLastStep) {
        this.formLastStep = formLastStep - 1;
    }

    public void addNavigationListener(Navigatable navigatable) {
        this.navigatables.add(navigatable);
    }

    protected void setItemForm(ItemForm itemForm) {
        this.itemForm = itemForm;
    }

    /**
     * Reset the form navigation controls.
     */
    protected void resetFormNavigation() {
        navigateTracker = 0;
        btnPrevious.setEnabled(false);
        btnNext.setEnabled(true);
        btnSubmit.setEnabled(false);
    }

    public void notifyNext() {
        this.navigatables.forEach((form) -> {
            form.next();
        });
    }

    public void notifyPrevious() {
        this.navigatables.forEach((form) -> {
            form.previous();
        });
    }

    public void addItemCRUDListener(ItemCRUDListener itemCRUDListener) {
        this.itemCRUDListeners.add(itemCRUDListener);
    }

    public void notifyCreated(Item createdItem) {
        this.itemCRUDListeners.forEach((itemCRUDListener) -> {
            itemCRUDListener.created(createdItem);
        });
    }

    public void notifyUpdated(Item updatedItem) {
        this.itemCRUDListeners.forEach((itemCRUDListener) -> {
            itemCRUDListener.updated(updatedItem);
        });
    }

    protected JButton getBtnNext() {
        return btnNext;
    }

    protected JButton getBtnPrevious() {
        return btnPrevious;
    }

    protected JButton getBtnSubmit() {
        return btnSubmit;
    }

    private class NavigateButtonsListener implements ActionListener {

        Item item;
        List<Image> images, imagesRetrievedFromDB;
        boolean isUpdateOperation = false;
        List<Boolean> isFieldsFilled;
        List<Boolean> isFieldsNotExceed255;

        @Override
        public void actionPerformed(ActionEvent e) {
            isFieldsFilled = new ArrayList<>();
            isFieldsFilled.add(null);
            isFieldsFilled.add(null);
            isFieldsFilled.add(null);
            isFieldsNotExceed255 = new ArrayList<>();
            isFieldsNotExceed255.add(null);
            isFieldsNotExceed255.add(null);
            JButton btnNavigate = (JButton) e.getSource();
            if (btnNavigate == btnNext) {
                navigateTracker++;
                notifyNext();
            } else if (btnNavigate == btnPrevious) {
                navigateTracker--;
                notifyPrevious();
            } else if (btnNavigate == btnSubmit) {
                item = new Item();
                images = new ArrayList<>();
                collectables.forEach((c) -> {
                    if (c instanceof ItemFormTextFields) {
                        // If item id is avalible then it is meant for an update operation. 
                        Object itemId = c.collect().get("id");
                        if (itemId != null) {
                            /**
                             * Variable to hold operation type. Will detect item
                             * id, if item id is available, then it is meant for
                             * update operation, otherwise if it is not
                             * available, then it is meant for create operation.
                             */
                            isUpdateOperation = true;
                            item.setId((int) itemId);
                        }
                        item.setName((String) c.collect().get("name"));
                        item.setSpecification((String) c.collect().get("specification"));
                        QuantityUnit quamtityUnit = (QuantityUnit) c.collect().get("quantityUnit");
                        item.setQuantityUnit(quamtityUnit);
                        /**
                         * Validate fields.
                         */
                        isFieldsFilled.set(0, !item.getName().isBlank());
                        isFieldsFilled.set(1, !item.getSpecification().isBlank());
                        isFieldsFilled.set(2, (item.getQuantityUnit() != null) && (item.getQuantityUnit().getId() > 0));
                        isFieldsNotExceed255.set(0, item.getName().length() < 256);
                        isFieldsNotExceed255.set(1, item.getSpecification().length() < 256);
                    } else if (c instanceof ItemFormImage
                            && isFieldsFilled.stream().allMatch(fieldCheck -> fieldCheck.equals(Boolean.TRUE))
                            && isFieldsNotExceed255.stream().allMatch(fieldCheck -> fieldCheck.equals(Boolean.TRUE))) {
                        images = (ArrayList<Image>) c.collect().get("images");
                        imagesRetrievedFromDB = (ArrayList<Image>) c.collect().get("imagesRetrievedFromDB");
                    }
                });
                if (isFieldsFilled.stream().anyMatch(fieldCheck -> fieldCheck.equals(Boolean.FALSE))
                        || isFieldsNotExceed255.stream().anyMatch(fieldCheck -> fieldCheck.equals(Boolean.FALSE))) {
                    String message = "Correct these faulty fields:\n";
                    if (!isFieldsFilled.get(0)) {
                        message += "- Name is missing\n";
                    } else if (!isFieldsNotExceed255.get(0)) {
                        message += "- Name exeeds the limit of 255 charachters\n";
                    }
                    if (!isFieldsFilled.get(1)) {
                        message += "- Specification is missing\n";
                    } else if (!isFieldsNotExceed255.get(1)) {
                        message += "- Specification exeeds the limit of 255 charachters\n";
                    }
                    if (!isFieldsFilled.get(2)) {
                        message += "- Quantity is missing\n";
                    }
                    JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean isItemUpdate = false;
                boolean isItemImagesUpdate = false;
                boolean isItemImagesCreate = false;
                boolean isItemsImagesDelete = false;

                if (isUpdateOperation) {
                    // Is update operation
                    isItemUpdate = CRUDItems.update(item);
                    // Images which were deselected during form update process.
                    List<Image> deselectedImages = imagesRetrievedFromDB.stream()
                            .filter(deslectedImage -> !images.contains(deslectedImage))
                            .collect(Collectors.toList());

                    // Delete actual files from file system of deselected images
                    deselectedImages.forEach(imageTobeRemoved
                            -> ImageFileManager.delete(imageTobeRemoved.getImageName(), CRUDImages.DIRECTORYNAME));

                    // Delete operation for DB image records using file names.
                    if (!deselectedImages.isEmpty()) {
                        int imagesDelete = CRUDImages.delete(deselectedImages);
                        isItemsImagesDelete = (imagesDelete > 0);
                    }

                    // Images that were retrieved from database and not removed,
                    // but their properties may change in case of selecting new images
                    // and/or removing some of the retrieved images. Because some properties
                    // are mutual between item images
                    List<Image> imagesRetrievedFromDBTobeUpdateProperies = images.stream()
                            .filter(dbRetrievedImage -> imagesRetrievedFromDB.contains(dbRetrievedImage))
                            .collect(Collectors.toList());

                    // Update operation for properties of the database retrieved images;
                    if (!imagesRetrievedFromDBTobeUpdateProperies.isEmpty()) {
                        int imagesUpdate = CRUDImages.update(imagesRetrievedFromDBTobeUpdateProperies);
                        isItemImagesUpdate = (imagesUpdate > 0);
                    }

                    // New images that were selected during form update process
                    List<Image> newSelectedImagesToBeUploaded = images.stream()
                            .filter(image -> !imagesRetrievedFromDB.contains(image))
                            .collect(Collectors.toList());

                    // Create operation for the new selected images
                    if (!newSelectedImagesToBeUploaded.isEmpty()) {
                        int imagesCreate = CRUDImages.create(newSelectedImagesToBeUploaded, item.getId());
                        isItemImagesCreate = (imagesCreate > 0);
                    }

                    boolean anyItemRelevantUpdate
                            = isItemUpdate
                            || isItemImagesCreate
                            || isItemImagesUpdate
                            || isItemsImagesDelete;

                    notifyUpdated(anyItemRelevantUpdate ? item : null);
                } else {
                    // Is create operation
                    Item createdItem = CRUDItems.create(item);
                    int idOfCreatedItem = createdItem.getId();
                    boolean isCreated = idOfCreatedItem > 0;
                    if (isCreated) {
                        // Create images
                        if (images != null) {
                            CRUDImages.create(images, idOfCreatedItem);
                        }
                    }
                    notifyCreated(isCreated ? createdItem : null);
                }
                // After submit, reset fields.
                itemForm.resetFields();
                itemForm.first();
                navigateTracker = 0;
            }
            btnPrevious.setEnabled(navigateTracker > 0);
            btnNext.setEnabled(navigateTracker < formLastStep);
            btnSubmit.setEnabled(navigateTracker == formLastStep);
        }
    }
}
