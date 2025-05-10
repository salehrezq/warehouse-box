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
package warehousebox.panel.menus.recipients;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import warehousebox.panel.menus.recipients.form.RecipientsCreateUpdateDialog;

/**
 *
 * @author Saleh
 */
public class RecipientsControls {

    private JPanel container;
    private JPanel controls;
    private JButton btnAdd, btnSearchQuery;
    private JTextField tfSearch;
    private RecipientsList recipientsList;
    private RecipientsCreateUpdateDialog recipientsCreateUpdateDialog;

    public RecipientsControls(Frame owner) {
        container = new JPanel(new BorderLayout());
        controls = new JPanel();
        btnAdd = new JButton("Add");
        recipientsCreateUpdateDialog = new RecipientsCreateUpdateDialog(owner, "Create", true);
        btnSearchQuery = new JButton("Get all");
        tfSearch = new JTextField(20);
        recipientsList = new RecipientsList();
        controls.add(btnAdd);
        controls.add(tfSearch);
        controls.add(btnSearchQuery);
        container.add(controls, BorderLayout.PAGE_START);
        container.add(recipientsList.getListScrolledPane(), BorderLayout.CENTER);
    }

    protected JPanel getContainer() {
        return container;
    }

    public JButton getBtnAdd() {
        return btnAdd;
    }

    public JButton getBtnSearchQuery() {
        return btnSearchQuery;
    }

    public JTextField getTfSearch() {
        return tfSearch;
    }

    public RecipientsCreateUpdateDialog getRecipientsCreateUpdateDialog() {
        return recipientsCreateUpdateDialog;
    }

    public void setRecipientsCreateUpdateDialog(RecipientsCreateUpdateDialog recipientsCreateUpdateDialog) {
        this.recipientsCreateUpdateDialog = recipientsCreateUpdateDialog;
    }

    private class AddRecipientHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            recipientsCreateUpdateDialog.setVisible(true);
        }

    }

}
