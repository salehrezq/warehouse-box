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

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import warehouse.db.model.Item;
import warehouse.db.model.QuantityUnit;
import warehouse.panel.items.ItemCRUDListener;

/**
 *
 * @author Saleh
 */
public class ItemCreateUpdateDialog extends JDialog implements ItemCRUDListener {

    private ItemForm itemForm;
    private FormManagement formManagement;
    private ItemFormTextFields itemFormTextFields;

    public ItemCreateUpdateDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        itemForm = new ItemForm();
        formManagement = itemForm.getFormManagement();
        formManagement.addItemCRUDListener(ItemCreateUpdateDialog.this);
        formManagement.addNavigationListener(itemForm);
        itemFormTextFields = itemForm.getItemFormTextFields();
        add(itemForm);
        pack();
        this.addWindowListener(new ClosingWindowHandler());
    }

    public FormManagement getFormManagement() {
        return formManagement;
    }

    /**
     * Method for update item process.
     *
     * @param itemIdForUpdate
     */
    public void setItemIdForUpdate(int itemIdForUpdate) {
        itemFormTextFields.setItemIdForUpdate(itemIdForUpdate);
    }

    /**
     * Method for update item process.
     *
     * @param itemName
     */
    public void setTfName(String itemName) {
        itemFormTextFields.setTfName(itemName);
    }

    /**
     * Method for update item process.
     *
     * @param itemSpecs
     */
    public void setTfSpecs(String itemSpecs) {
        itemFormTextFields.setTfSpecs(itemSpecs);
    }

    /**
     * Method for update item process.
     *
     * @param quantityUnit
     */
    public void setUnitName(QuantityUnit quantityUnit) {
        itemFormTextFields.setSelectedUnit(quantityUnit);
    }

    /**
     * Method for update item process.
     *
     * @param itemId
     */
    public void setItemImages(int itemId) {
        itemForm.getItemFormImage().loadItemImages(itemId);
    }

    @Override
    public void created(Item createdItem) {
        System.out.println("ItemCreateUpdateDialog created: process and dismiss");
    }

    @Override
    public void updated(Item updatedItem) {
        System.out.println("ItemCreateUpdateDialog updated: process and dismiss");
    }

    private class ClosingWindowHandler extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            itemForm.clearFields();
        }
    }

}
