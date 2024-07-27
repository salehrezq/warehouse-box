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
package warehouse.panel.inwards;

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
public class RowAttributesDisplay {

    private JPanel container;
    private JTextField tfItemName,
            tfItemSpecifications,
            tfItemSource;
    private ScrollBarThin scrollBarTfName,
            scrollBarTfSpecifications,
            scrollBarTfSource;
    private JSplitPane splitpane;
    private final Color colorTextField = new Color(84, 84, 84);

    public RowAttributesDisplay() {
        tfItemName = new JTextField();
        tfItemName.setEnabled(false);
        tfItemName.setDisabledTextColor(colorTextField);
        scrollBarTfName = new ScrollBarThin(Adjustable.HORIZONTAL);
        scrollBarTfName.setModel(tfItemName.getHorizontalVisibility());
        Box boxNameField = Box.createVerticalBox();
        boxNameField.add(tfItemName);
        boxNameField.add(scrollBarTfName);

        tfItemSpecifications = new JTextField();
        tfItemSpecifications.setEnabled(false);
        tfItemSpecifications.setDisabledTextColor(colorTextField);
        scrollBarTfSpecifications = new ScrollBarThin(Adjustable.HORIZONTAL);
        scrollBarTfSpecifications.setModel(tfItemSpecifications.getHorizontalVisibility());
        Box boxSpecsField = Box.createVerticalBox();
        boxSpecsField.add(tfItemSpecifications);
        boxSpecsField.add(scrollBarTfSpecifications);

        splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitpane.setDividerSize(3);
        splitpane.setDividerLocation(130);
        splitpane.setTopComponent(boxNameField);
        splitpane.setBottomComponent(boxSpecsField);
        JPanel panelHolder = new JPanel(new BorderLayout());
        panelHolder.add(splitpane);

        Box boxContainer = Box.createVerticalBox();
        boxContainer.add(panelHolder);

        tfItemSource = new JTextField();
        tfItemSource.setEnabled(false);
        tfItemSource.setDisabledTextColor(colorTextField);
        scrollBarTfSource = new ScrollBarThin(Adjustable.HORIZONTAL);
        scrollBarTfSource.setModel(tfItemSource.getHorizontalVisibility());
        Box boxItemSourceField = Box.createVerticalBox();
        boxItemSourceField.add(tfItemSource);
        boxItemSourceField.add(scrollBarTfSource);

        boxContainer.add(boxItemSourceField);

        container = new JPanel(new BorderLayout());
        container.add(boxContainer, BorderLayout.NORTH);
    }

    protected void setTfItemNameText(String name) {
        tfItemName.setText(name);
    }

    protected void setTfItemSpecificationsText(String specs) {
        tfItemSpecifications.setText(specs);
    }

    protected void setTfItemSourceText(String source) {
        tfItemSource.setText(source);
    }

    protected JPanel getContainer() {
        return this.container;
    }

}
