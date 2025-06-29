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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import warehousebox.db.CRUDRecipients;
import warehousebox.db.model.Recipient;
import warehousebox.panel.menus.ResultLimitSizePreference;
import warehousebox.panel.menus.recipients.form.RecipientsCreateUpdateDialog;

/**
 *
 * @author Saleh
 */
public class RecipientsLogic {

    private JButton btnAdd, btnSearchQuery, btnLoadMore;
    private JTextField tfSearch;
    private RecipientsList recipientsList;
    private JList listing;
    private RecipientsCreateUpdateDialog recipientsCreateUpdateDialog;
    private int searchResultTotalRowsCount, incrementedReturnedRowsCount;
    private static int LIMIT, OFFSET;
    private String searchQueryImmutableCopy;
    private RecipientsImagePanel recipientsImagePanel;
    private PopupMenuHandler popupMenuHandler;

    public RecipientsLogic(RecipientsControls rc) {
        btnAdd = rc.getBtnAdd();
        recipientsCreateUpdateDialog = rc.getRecipientsCreateUpdateDialog();
        tfSearch = rc.getTfSearch();
        btnSearchQuery = rc.getBtnSearchQuery();
        btnAdd.addActionListener(new AddRecipientHandler());
        recipientsList = rc.getRecipientsList();
        btnLoadMore = rc.getBtnLoadMore();
        recipientsImagePanel = rc.getRecipientsImagePanel();

        listing = recipientsList.getJList();
        tfSearch.getDocument().addDocumentListener(new TextFieldContentReactHandler());
        btnSearchQuery.addActionListener(new SearchHandler());
        btnLoadMore.addActionListener(new LoadMoreHandler());
        listing.addMouseListener(new ListDoubleClickHandler());

        popupMenuHandler = new PopupMenuHandler(rc);
        popupMenuHandler.setUp();
    }

    private class AddRecipientHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            recipientsCreateUpdateDialog.setVisible(true);
        }

    }

    private class SearchHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            searchQueryImmutableCopy = tfSearch.getText();
            searchResultTotalRowsCount = CRUDRecipients.searchResultRowsCount(tfSearch.getText());
            LIMIT = ResultLimitSizePreference.getResultLimitSize();
            btnLoadMore.setEnabled(!(LIMIT >= searchResultTotalRowsCount));
            recipientsList.removeAllElements();
            OFFSET = 0;
            incrementedReturnedRowsCount = 0;
            List<Recipient> recipients = CRUDRecipients.search(tfSearch.getText(), LIMIT, OFFSET);
            if (recipients.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No matched results!", "Info",
                        JOptionPane.PLAIN_MESSAGE);
            }
            incrementedReturnedRowsCount += recipients.size();
            recipients.forEach(recipient -> {
                recipientsList.addElement(recipient);
            });
        }
    }

    private class LoadMoreHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            OFFSET += LIMIT;
            List<Recipient> recipients = CRUDRecipients.search(searchQueryImmutableCopy, LIMIT, OFFSET);
            incrementedReturnedRowsCount += recipients.size();
            recipients.forEach(listable -> {
                recipientsList.addElement(listable);
            });
            btnLoadMore.setEnabled(!(incrementedReturnedRowsCount >= searchResultTotalRowsCount));
        }
    }

    private class ListDoubleClickHandler extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            JList list = (JList) e.getSource();
            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                int index = list.locationToIndex(e.getPoint());
                Recipient recipient = (Recipient) list.getModel().getElementAt(index);
                recipientsImagePanel.setImageOfSelectedItem(recipient.getId());
            }
        }
    }

    private class TextFieldContentReactHandler implements DocumentListener {

        public void changed() {
            btnSearchQuery.setText(!tfSearch.getText().equals("") ? "Search" : "Get all");
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            changed();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            changed();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            changed();
        }
    }
}
