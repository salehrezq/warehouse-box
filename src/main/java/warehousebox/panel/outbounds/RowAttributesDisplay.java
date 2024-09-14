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
package warehousebox.panel.outbounds;

import warehousebox.utility.scrollbarthin.ScrollBarThin;
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
            tfItemRecipient,
            tfItemUsedFor;
    private ScrollBarThin scrollBarTfName,
            scrollBarTfSpecifications,
            scrollBarTfItemRecipient,
            scrollBarTfItemUsedFor;
    private JSplitPane splitpane1, splitPane2;
    private final Color colorTextField = new Color(84, 84, 84);

    public RowAttributesDisplay() {
        container = new JPanel(new BorderLayout());
        tfItemName = new JTextField(1);
        tfItemName.setEnabled(false);
        tfItemName.setDisabledTextColor(colorTextField);
        tfItemSpecifications = new JTextField(30);
        tfItemSpecifications.setEnabled(false);
        tfItemSpecifications.setDisabledTextColor(colorTextField);
        tfItemRecipient = new JTextField(2);
        tfItemRecipient.setEnabled(false);
        tfItemRecipient.setDisabledTextColor(colorTextField);
        tfItemUsedFor = new JTextField(20);
        tfItemUsedFor.setEnabled(false);
        tfItemUsedFor.setDisabledTextColor(colorTextField);

        scrollBarTfName = new ScrollBarThin(Adjustable.HORIZONTAL);
        scrollBarTfName.setModel(tfItemName.getHorizontalVisibility());
        scrollBarTfSpecifications = new ScrollBarThin(Adjustable.HORIZONTAL);
        scrollBarTfSpecifications.setModel(tfItemSpecifications.getHorizontalVisibility());
        scrollBarTfItemRecipient = new ScrollBarThin(Adjustable.HORIZONTAL);
        scrollBarTfItemRecipient.setModel(tfItemRecipient.getHorizontalVisibility());
        scrollBarTfItemUsedFor = new ScrollBarThin(Adjustable.HORIZONTAL);
        scrollBarTfItemUsedFor.setModel(tfItemUsedFor.getHorizontalVisibility());

        Box boxContainer = Box.createVerticalBox();

        Box boxNameField = Box.createVerticalBox();
        boxNameField.add(tfItemName);
        boxNameField.add(scrollBarTfName);
        Box boxSpecsField = Box.createVerticalBox();
        boxSpecsField.add(tfItemSpecifications);
        boxSpecsField.add(scrollBarTfSpecifications);

        splitpane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitpane1.setDividerSize(3);
        splitpane1.setDividerLocation(130);
        splitpane1.setTopComponent(boxNameField);
        splitpane1.setBottomComponent(boxSpecsField);

        Box boxItemRecipientField = Box.createVerticalBox();
        boxItemRecipientField.add(tfItemRecipient);
        boxItemRecipientField.add(scrollBarTfItemRecipient);

        Box boxItemUsedForField = Box.createVerticalBox();
        boxItemUsedForField.add(tfItemUsedFor);
        boxItemUsedForField.add(scrollBarTfItemUsedFor);

        splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane2.setDividerSize(3);
        splitPane2.setDividerLocation(130);
        splitPane2.setTopComponent(boxItemRecipientField);
        splitPane2.setBottomComponent(boxItemUsedForField);

        boxContainer.add(splitpane1);
        boxContainer.add(splitPane2);

        container.add(boxContainer, BorderLayout.NORTH);
    }

    protected void setTfItemNameText(String name) {
        tfItemName.setText(name);
    }

    protected void setTfItemSpecificationsText(String specs) {
        tfItemSpecifications.setText(specs);
    }

    public void setTfRecipientText(String recipient) {
        this.tfItemRecipient.setText(recipient);
    }

    public void setTfUsedForText(String usedFor) {
        this.tfItemUsedFor.setText(usedFor);
    }

    protected JPanel getContainer() {
        return this.container;
    }

}
