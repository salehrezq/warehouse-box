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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Saleh
 */
public class ItemForm extends JPanel {

    private final JPanel panelItemForm;
    private JComponent[] componentsRefrence;
    private JTextField tfCode, tfName, tfSpecs, tfLocation, tfStore, tfUnit;
    private final String tfInitialValue = "0";

    public ItemForm() {

        setLayout(new GridBagLayout());
        GridBagConstraints c;

        JComponent[] components = {
            tfCode = new JTextField(10),
            tfName = new JTextField(10),
            tfSpecs = new JTextField(10),
            tfLocation = new JTextField(10),
            tfStore = new JTextField(10),
            tfUnit = new JTextField(10)
        };

        componentsRefrence = components;

        String[] labels = {
            "Code",
            "Name",
            "Specifications",
            "Location",
            "Store",
            "Unit"};

        for (JComponent component : componentsRefrence) {
            JTextField tf = (JTextField) component;
            //  tf.setEditable(false);
            //  tf.setText(tfInitialValue);
        }

        panelItemForm = (JPanel) TwoColumnsLabelsAndFields.getTwoColumnLayout(labels, components);
        c = new GridBagConstraints();
        c.gridy = 0;
        c.insets = new Insets(20, 0, 0, 0);
        c.anchor = GridBagConstraints.PAGE_START;
        add(panelItemForm, c);
    }
}
