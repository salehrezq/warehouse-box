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
package warehouse.panel.items.nameandspecsdisplay;

import utility.scrollbarthin.ScrollBarThin;
import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

/**
 *
 * @author Saleh
 */
public class NameAndSpecDisplayFields {

    private JPanel container;
    private JTextField tfItemName,
            tfItemSpecifications;
    private ScrollBarThin scrollBarTfName,
            scrollBarTfSpecifications;
    private JSplitPane splitpane;
    private final Color colorTextField = new Color(84, 84, 84);

    public NameAndSpecDisplayFields() {
        container = new JPanel(new BorderLayout());
        tfItemName = new JTextField(1);
        tfItemName.setEnabled(false);
        tfItemName.setDisabledTextColor(colorTextField);
        tfItemSpecifications = new JTextField(30);
        tfItemSpecifications.setEnabled(false);
        tfItemSpecifications.setDisabledTextColor(colorTextField);

        scrollBarTfName = new ScrollBarThin(Adjustable.HORIZONTAL);
        scrollBarTfName.setModel(tfItemName.getHorizontalVisibility());
        scrollBarTfSpecifications = new ScrollBarThin(Adjustable.HORIZONTAL);
        scrollBarTfSpecifications.setModel(tfItemSpecifications.getHorizontalVisibility());

        Box boxNameField = Box.createVerticalBox();
        boxNameField.add(tfItemName);
        boxNameField.add(scrollBarTfName);
        Box boxSpecsField = Box.createVerticalBox();
        boxSpecsField.add(tfItemSpecifications);
        boxSpecsField.add(scrollBarTfSpecifications);
        // box.add(Box.createVerticalGlue());
        // container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitpane.setDividerSize(3);
        splitpane.setTopComponent(boxNameField);
        splitpane.setBottomComponent(boxSpecsField);
        container.add(splitpane, BorderLayout.NORTH);
    }

    public void setTfItemNameText(String name) {
        tfItemName.setText(name);
    }

    public void setTfItemSpecificationsText(String specs) {
        tfItemSpecifications.setText(specs);
    }

    public JPanel getContainer() {
        return this.container;
    }

}
