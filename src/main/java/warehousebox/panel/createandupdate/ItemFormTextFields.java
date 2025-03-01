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

import java.awt.Adjustable;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import warehousebox.utility.scrollbarthin.ScrollBarThin;
import warehousebox.db.model.QuantityUnit;
import warehousebox.utility.singularlisting.ListableItemForm;

/**
 *
 * @author Saleh
 */
public class ItemFormTextFields extends JPanel implements Collectable {

    private JTextField tfName, tfSpecs;
    private ScrollBarThin scrollBarThinTfName, scrollBarThinTfSpecs;
    private JLabel lbName, lbSpecs;
    private ListableItemForm itemFormQuantityUnit;
    private Map data;
    private int itemIdForUpdate;

    public ItemFormTextFields() {

        setLayout(new MigLayout("center center"));
        data = new HashMap<String, String>();

        lbName = new JLabel("Name");
        lbSpecs = new JLabel("Specs");
        // Setup Text field name
        tfName = new JTextField(40);
        scrollBarThinTfName = new ScrollBarThin(Adjustable.HORIZONTAL);
        scrollBarThinTfName.setModel(tfName.getHorizontalVisibility());
        Box boxNameField = Box.createVerticalBox();
        boxNameField.add(tfName);
        boxNameField.add(scrollBarThinTfName);
        // Setup Text field specs
        tfSpecs = new JTextField(40);
        scrollBarThinTfSpecs = new ScrollBarThin(Adjustable.HORIZONTAL);
        scrollBarThinTfSpecs.setModel(tfSpecs.getHorizontalVisibility());
        Box boxSpecsField = Box.createVerticalBox();
        boxSpecsField.add(tfSpecs);
        boxSpecsField.add(scrollBarThinTfSpecs);

        itemFormQuantityUnit = new ListableItemForm();
        itemFormQuantityUnit.setLabelText("Unit");
        itemFormQuantityUnit.setListableImpl(new QuantityUnit());

        add(lbName);
        add(boxNameField, "wrap");
        add(lbSpecs);
        add(boxSpecsField, "wrap");
        add(itemFormQuantityUnit, "center, span 2");
    }

    public void setItemIdForUpdate(int itemId) {
        this.itemIdForUpdate = itemId;
    }

    public void setTfName(String itemName) {
        tfName.setText(itemName);
    }

    public void setTfSpecs(String itemSpecs) {
        tfSpecs.setText(itemSpecs);
    }

    public void setSelectedUnit(QuantityUnit quantityUnit) {
        itemFormQuantityUnit.setPreviewSelected(quantityUnit);
    }

    protected ListableItemForm getListableItemForm() {
        return itemFormQuantityUnit;
    }

    protected JTextField getTfName() {
        return tfName;
    }

    protected JTextField getTfSpecs() {
        return tfSpecs;
    }

    @Override
    public Map collect() {
        // If class used for item update; this field will be populated elsewhere.
        if (itemIdForUpdate > 0) {
            data.put("id", itemIdForUpdate);
        }
        data.put("name", tfName.getText());
        data.put("specification", tfSpecs.getText());
        data.put("quantityUnit", itemFormQuantityUnit.getSelectedValue());
        return data;
    }

    public void resetFields() {
        tfName.setText("");
        tfSpecs.setText("");
        itemFormQuantityUnit.resetFields();
    }
}
