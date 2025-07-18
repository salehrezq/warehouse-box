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
package warehousebox.panel.outbounds.recipientsform;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import warehousebox.utility.scrollbarthin.ScrollBarThin;
import warehousebox.db.CRUDRecipients;
import warehousebox.db.QueryWordsProcessor;
import warehousebox.db.model.Recipient;
import warehousebox.panel.menus.ResultLimitSizePreference;

/**
 *
 * @author Saleh
 */
public class RecipientFormForFilters extends JPanel {

    private JPanel container, panelSearch, panelList;
    private JLabel label;
    private JTextField tfSearch;
    private ScrollBarThin scrollBarThinTfSearch;
    private JButton btnSearch, btnLoadMore;// btnOK;
    private ListOfRecipients listOfListable;
    private JList listing;
    //   private RecipientsImagePanel recipientsImagePanel;
    // private ActionListener btnListener;
    private RecipientFormForFilters thisListableItemManageClass;
    private int searchResultTotalRowsCount, incrementedReturnedRowsCount;
    private static int LIMIT,
            OFFSET;
    private String[] searchedWordsImmutableCopy;
    private JDialog dialoge;
    private ArrayList<RecipientFormForFiltersListener> listableItemFormForFiltersListeners;
    private Preferences prefs;
    private String prefsOK_key;

    public RecipientFormForFilters() {
        //   super(owner, title, modal);

        listableItemFormForFiltersListeners = new ArrayList<>();

        thisListableItemManageClass = RecipientFormForFilters.this;
        setLayout(new BorderLayout());
        panelSearch = new JPanel();
        panelList = new JPanel(new BorderLayout());
        container = new JPanel(new BorderLayout());
        //  btnListener = new BtnListener();

        label = new JLabel("Recipient");
        // Setup Text field search:
        tfSearch = new JTextField(28);
        tfSearch.getDocument().addDocumentListener(new TextFieldContentReactHandler());
        scrollBarThinTfSearch = new ScrollBarThin(Adjustable.HORIZONTAL);
        scrollBarThinTfSearch.setModel(tfSearch.getHorizontalVisibility());
        Box boxSearchField = Box.createVerticalBox();
        boxSearchField.add(tfSearch);
        boxSearchField.add(scrollBarThinTfSearch);
        // Button search
        btnSearch = new JButton("Get all");
        btnSearch.addActionListener(new BtnSearchHandler());
        listOfListable = new ListOfRecipients();
        listing = listOfListable.getJList();
        listing.addMouseListener(new MouseJListHandler());
        listing.addListSelectionListener(new ListSelectionHandler());
        //  btnClose.addActionListener(btnListener);

        panelSearch.add(label);
        panelSearch.add(boxSearchField);
        panelSearch.add(btnSearch);

        btnLoadMore = new JButton("Load more");
        btnLoadMore.setEnabled(false);
        btnLoadMore.addActionListener(new BtnLoadMoreHandler());

        panelList.add(listOfListable.getListScrolledPane(), BorderLayout.CENTER);
        panelList.add(btnLoadMore, BorderLayout.PAGE_END);

        container.add(panelSearch, BorderLayout.PAGE_START);
        container.add(panelList, BorderLayout.CENTER);
        this.setMinimumSize(new Dimension(480, 300));
        add(container, BorderLayout.CENTER);
    }

    public void setDialoge(JDialog dialog) {
        this.dialoge = dialog;
    }

    public void setPreferencesKey(String prefskey) {
        this.prefsOK_key = prefskey;
    }

    public void setPreferences(Preferences prefs) {
        this.prefs = prefs;
    }

    public JList getlist() {
        return listing;
    }

    /**
     * Used to add element preview selected for editing
     *
     * @param listable
     */
    public void setPreviewSelected(Recipient listable) {
        listOfListable.setPreviewSelected(listable);
    }

    public Recipient getSelectedValue() {
        return this.listOfListable.getSelectedValue();
    }

    public void clearFields() {
        tfSearch.setText("");
    }

    public void setListablePreferredSize(int with, int height) {
        listOfListable.setPreferredSize(with, height);
    }

    public void addListableItemFormForFiltersListener(RecipientFormForFiltersListener listableItemFormForFiltersListener) {
        this.listableItemFormForFiltersListeners.add(listableItemFormForFiltersListener);
    }

    public void notifySelectedListable(Recipient listable) {
        this.listableItemFormForFiltersListeners.forEach((listableItemFormForFiltersListener) -> {
            listableItemFormForFiltersListener.selectedListable(listable);
        });
    }

    public void resetFields() {
        tfSearch.setText("");
        btnLoadMore.setEnabled(false);
        // notifyloadMoreEnabled(btnLoadMore.isEnabled());
        listOfListable.removeAllElements();
    }

    private void resetFieldsAfterValidation() {
        btnLoadMore.setEnabled(false);
        listOfListable.removeAllElements();
    }

    private class BtnSearchHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String[] searchedWords = QueryWordsProcessor.getArrayOfWords(tfSearch.getText());
            searchedWordsImmutableCopy = searchedWords;

            if (searchedWords.length < 1 || (tfSearch.getText().isBlank() && tfSearch.getText().length() > 0)) {
                resetFieldsAfterValidation();
                JOptionPane.showMessageDialog(
                        null,
                        "Search query is not valid for search",
                        "Write some search query.",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            searchResultTotalRowsCount = CRUDRecipients.searchResultRowsCount(searchedWords);
            LIMIT = ResultLimitSizePreference.getResultLimitSize();
            btnLoadMore.setEnabled(!(LIMIT >= searchResultTotalRowsCount));
            listOfListable.removeAllElements();
            OFFSET = 0;
            incrementedReturnedRowsCount = 0;
            List<Recipient> listables = CRUDRecipients.search(searchedWords, LIMIT, OFFSET);
            if (listables.isEmpty()) {
                JOptionPane.showMessageDialog(RecipientFormForFilters.this, "No matched results!", "Info",
                        JOptionPane.PLAIN_MESSAGE);
            }
            incrementedReturnedRowsCount += listables.size();
            listables.forEach(listable -> {
                listOfListable.addElement(listable);
            });
        }
    }

    private class BtnLoadMoreHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            OFFSET += LIMIT;
            List<Recipient> listables = CRUDRecipients.search(searchedWordsImmutableCopy, LIMIT, OFFSET);
            incrementedReturnedRowsCount += listables.size();
            listables.forEach(listable -> {
                listOfListable.addElement(listable);
            });
            btnLoadMore.setEnabled(!(incrementedReturnedRowsCount >= searchResultTotalRowsCount));
        }
    }

    private class BtnOKHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            notifySelectedListable(listOfListable.getSelectedValue());
            dialoge.setVisible(false);
            if (listOfListable.getSelectedValue() != null) {
                prefs.putInt(prefsOK_key, listOfListable.getSelectedValue().getId());
            } else {
                prefs.putInt(prefsOK_key, 0);
            }
        }
    }

    private class MouseJListHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                listing.setSelectedIndex(listing.locationToIndex(e.getPoint()));
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            JList list = (JList) e.getSource();
            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                int index = list.locationToIndex(e.getPoint());
                Recipient recipient = (Recipient) list.getModel().getElementAt(index);
                //    recipientsImagePanel.setImageOfSelectedItem(recipient.getId());
            }
        }
    }

    private class ListSelectionHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent event) {
            JList list = (JList) event.getSource();
            if (!event.getValueIsAdjusting()) {
                Recipient listable = (Recipient) list.getSelectedValue();
                if (listable != null) {
                    //  btnOK.setEnabled(true);
                } else {
                    //  btnOK.setEnabled(false);
                }
            }
        }
    }

    private class TextFieldContentReactHandler implements DocumentListener {

        public void changed() {
            btnSearch.setText(!tfSearch.getText().equals("") ? "Search" : "Get all");
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
