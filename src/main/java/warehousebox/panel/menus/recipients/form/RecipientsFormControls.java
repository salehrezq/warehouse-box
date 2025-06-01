/*
 * The MIT License
 *
 * Copyright 2025 Saleh.
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
package warehousebox.panel.menus.recipients.form;

import java.awt.Adjustable;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import warehousebox.utility.scrollbarthin.ScrollBarThin;

/**
 *
 * @author Saleh
 */
public class RecipientsFormControls {

    private JPanel container;
    private JLabel lbName;
    private JTextField tfName;
    private RecipientsImagePanel recipientsImagePanel;
    private JButton btnBrowse, btnSubmit;
    private ScrollBarThin scrollBarThinTfName;

    public RecipientsFormControls() {
        container = new JPanel();
        container.setLayout(new MigLayout("center center"));
        lbName = new JLabel("Name");
        tfName = new JTextField(30);
        recipientsImagePanel = new RecipientsImagePanel();
        btnBrowse = new JButton("Browse...");
        btnSubmit = new JButton("Submit");
        scrollBarThinTfName = new ScrollBarThin(Adjustable.HORIZONTAL);
        scrollBarThinTfName.setModel(tfName.getHorizontalVisibility());
        Box boxNameField = Box.createVerticalBox();
        boxNameField.add(tfName);
        boxNameField.add(scrollBarThinTfName);
        container.add(lbName);
        container.add(boxNameField, "wrap 15");
        container.add(recipientsImagePanel.getContainer(), "span 2, center, wrap 5");
        container.add(btnBrowse, "span 2, center");
        container.setSize(500, 700);
    }

    protected JPanel getContainer() {
        return container;
    }

    protected JButton getBtnBrowse() {
        return btnBrowse;
    }

    protected RecipientsImagePanel getRecipientsImagePanel() {
        return recipientsImagePanel;
    }

    protected JButton getBtnSubmit() {
        return btnSubmit;
    }

}
