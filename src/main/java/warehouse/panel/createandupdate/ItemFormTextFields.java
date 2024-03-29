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

import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import warehouse.singularlisting.SingularAttributedListForm;
import warehouse.db.model.QuantityUnit;

/**
 *
 * @author Saleh
 */
public class ItemFormTextFields extends JPanel implements Collectable {

    private JTextField tfName, tfSpecs;
    private JLabel lbName, lbSpecs;
    private SingularAttributedListForm itemFormQuantityUnit;
    private Map data;

    public ItemFormTextFields() {

        setLayout(new MigLayout("center center"));
        data = new HashMap<String, String>();

        lbName = new JLabel("Name");
        lbSpecs = new JLabel("Specs");

        tfName = new JTextField(40);
        tfSpecs = new JTextField(40);

        itemFormQuantityUnit = new SingularAttributedListForm();
        itemFormQuantityUnit.setListableImpl(new QuantityUnit());
        itemFormQuantityUnit.setListDimentions(250, 250);

        add(lbName);
        add(tfName, "wrap");
        add(lbSpecs);
        add(tfSpecs, "wrap");
        add(itemFormQuantityUnit, "center, span 2");
    }

    @Override
    public Map collect() {
        data.put("name", tfName.getText());
        data.put("specs", tfSpecs.getText());
        data.put("unit", itemFormQuantityUnit.getSelectedValue());
        return data;
    }

    public void clearFields() {
        tfName.setText("");
        tfSpecs.setText("");
        itemFormQuantityUnit.clearFields();
    }
}
