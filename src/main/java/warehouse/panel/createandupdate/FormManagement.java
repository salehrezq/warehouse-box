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
package warehouse.panel.createandupdate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JPanel;
import utility.filemanage.ImageFileManager;
import warehouse.db.CRUDImages;
import warehouse.db.CRUDItems;
import warehouse.db.model.Image;
import warehouse.db.model.Item;
import warehouse.db.model.QuantityUnit;
import warehouse.panel.items.ItemCRUDListener;

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

    private class NavigateButtonsListener implements ActionListener {

        Item item;
        List<Image> images, imagesRetrievedFromDB;
        boolean isUpdateOperation = false;

        @Override
        public void actionPerformed(ActionEvent e) {
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
                        item.setSpecification((String) c.collect().get("specs"));
                        QuantityUnit qty = (QuantityUnit) c.collect().get("unit");
                        item.setUnitId(qty.getId());
                    } else if (c instanceof ItemFormImage) {
                        images = (ArrayList<Image>) c.collect().get("images");
                        imagesRetrievedFromDB = (ArrayList<Image>) c.collect().get("imagesRetrievedFromDB");
                    }
                });

                System.out.println(item.getId());
                System.out.println(item.getName());
                System.out.println(item.getSpecification());
                System.out.println(item.getUnitId());

                boolean isUpdated = false;

                if (isUpdateOperation) {
                    // Is update operation
                    isUpdated = CRUDItems.update(item);
                    notifyUpdated(isUpdated ? item : null);
                    System.out.println(isUpdated ? "Updated" : "Not updated");

                    // Images which were deselected during form update process.
                    List<Image> deselectedImages = imagesRetrievedFromDB.stream()
                            .filter(deslectedImage -> !images.contains(deslectedImage))
                            .collect(Collectors.toList());

                    // Delete actual files from file system of deselected images
                    deselectedImages.forEach(imageTobeRemoved
                            -> ImageFileManager.delete(imageTobeRemoved.getImageName()));

                    // Delete operation for DB image records using file names.
                    if (!deselectedImages.isEmpty()) {
                        CRUDImages.delete(deselectedImages);
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
                        CRUDImages.update(imagesRetrievedFromDBTobeUpdateProperies);
                    }

                    // New images that were selected during form update process
                    List<Image> newSelectedImagesToBeUploaded = images.stream()
                            .filter(image -> !imagesRetrievedFromDB.contains(image))
                            .collect(Collectors.toList());

                    // Create operation for the new selected images
                    if (!newSelectedImagesToBeUploaded.isEmpty()) {
                        CRUDImages.create(newSelectedImagesToBeUploaded, item.getId());
                    }
                } else {
                    // Is create operation
                    Item createdItem = CRUDItems.create(item);
                    System.out.println("Newly created Item id " + createdItem.getId());
                    int idOfCreatedItem = createdItem.getId();
                    boolean isCreated = idOfCreatedItem > 0;
                    notifyCreated(isCreated ? createdItem : null);
                    if (isCreated) {
                        // Create images
                        CRUDImages.create(images, idOfCreatedItem);
                    }
                }
            }
            btnPrevious.setEnabled(navigateTracker > 0);
            btnNext.setEnabled(navigateTracker < formLastStep);
            btnSubmit.setEnabled(navigateTracker == formLastStep);
        }
    }
}
