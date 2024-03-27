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
package warehouse.items.update;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import warehouse.db.model.QuantityUnit;

/**
 *
 * @author Saleh
 */
public class UpdateItemDialog extends JDialog {

    private ItemForm itemForm;
    private FormManagement formManagement;
    private ItemFormTextFields itemFormTextFields;

    public UpdateItemDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        itemForm = new ItemForm();
        formManagement = itemForm.getFormManagement();
        formManagement.addNavigationListener(itemForm);
        itemFormTextFields = itemForm.getItemFormTextFields();
        add(itemForm);
        pack();
        this.addWindowListener(new ClosingWindowHandler());
    }

    public FormManagement getFormManagement() {
        return formManagement;
    }

    public void setTfName(String itemName) {
        itemFormTextFields.setTfName(itemName);
    }

    public void setTfSpecs(String itemSpecs) {
        itemFormTextFields.setTfSpecs(itemSpecs);
    }

    public void setUnitName(QuantityUnit quantityUnit) {
        itemFormTextFields.setPreviewSelected(quantityUnit);
    }

    private class ClosingWindowHandler extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            itemForm.clearFields();
        }
    }

}
