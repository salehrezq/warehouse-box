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
package warehouse.singularlisting;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import utility.scrollbarthin.ScrollBarThin;
import warehouse.db.CRUDListable;
import warehouse.db.model.Source;

/**
 *
 * @author Saleh
 */
public class ListableItemFormForFilters extends JPanel implements ListableConsumer {

    private JPanel container, panelSearch, panelControls, panelList;
    private JLabel label;
    private JTextField tfSearch;
    private ScrollBarThin scrollBarThinTfSearch;
    private JButton btnSearch, btnLoadMore, btnOK;
    private ListOfListable listOfListable;
    private JList listing;
    private Listable listableImplementation;
    // private ActionListener btnListener;
    private ListableItemFormForFilters thisListableItemManageClass;
    private int searchResultTotalRowsCount, incrementedReturnedRowsCount;
    private static int LIMIT,
            OFFSET;
    private String searchQueryImmutableCopy;
    private JDialog dialoge;
    private ArrayList<ListableItemFormForFiltersListener> listableItemFormForFiltersListeners;
    private Preferences prefs;
    private String prefsOK_key;

    public ListableItemFormForFilters() {
        //   super(owner, title, modal);

        listableItemFormForFiltersListeners = new ArrayList<>();
        LIMIT = 3;

        thisListableItemManageClass = ListableItemFormForFilters.this;
        setLayout(new BorderLayout());
        panelSearch = new JPanel();
        panelList = new JPanel(new BorderLayout());
        container = new JPanel(new BorderLayout());
        //  btnListener = new BtnListener();

        label = new JLabel();
        // Setup Text field search:
        tfSearch = new JTextField(25);
        scrollBarThinTfSearch = new ScrollBarThin(Adjustable.HORIZONTAL);
        scrollBarThinTfSearch.setModel(tfSearch.getHorizontalVisibility());
        Box boxSearchField = Box.createVerticalBox();
        boxSearchField.add(tfSearch);
        boxSearchField.add(scrollBarThinTfSearch);
        // Button search
        btnSearch = new JButton("Search");
        btnSearch.addActionListener(new BtnSearchHandler());
        listOfListable = new ListOfListable();
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

        btnOK = new JButton("OK");
        btnOK.setEnabled(false);
        btnOK.addActionListener(new BtnOKHandler());

        panelControls = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        panelControls.add(btnLoadMore, c);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        panelControls.add(btnOK, c);

        panelList.add(listOfListable.getListScrolledPane(), BorderLayout.CENTER);
        panelList.add(panelControls, BorderLayout.PAGE_END);

        container.add(panelSearch, BorderLayout.PAGE_START);
        container.add(panelList, BorderLayout.CENTER);
        add(container, BorderLayout.CENTER);
        this.setMinimumSize(new Dimension(480, 350));
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

    @Override
    public void setListableImpl(Listable listable) {
        this.listableImplementation = listable;
    }

    /**
     * Used to add element preview selected for editing
     *
     * @param listable
     */
    public void setPreviewSelected(Listable listable) {
        listOfListable.setPreviewSelected(listable);
    }

    public Listable getSelectedValue() {
        return this.listOfListable.getSelectedValue();
    }

    public void clearFields() {
        tfSearch.setText("");
    }

    public void setListablePreferredSize(int with, int height) {
        listOfListable.setPreferredSize(with, height);
    }

    public void addListableItemFormForFiltersListener(ListableItemFormForFiltersListener listableItemFormForFiltersListener) {
        this.listableItemFormForFiltersListeners.add(listableItemFormForFiltersListener);
    }

    public void notifySelectedListable(Listable listable) {
        this.listableItemFormForFiltersListeners.forEach((listableItemFormForFiltersListener) -> {
            listableItemFormForFiltersListener.selectedListable(listable);
        });
    }

    private class BtnSearchHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            searchQueryImmutableCopy = tfSearch.getText();
            searchResultTotalRowsCount = CRUDListable.searchResultRowsCount(listableImplementation, tfSearch.getText());
            btnLoadMore.setEnabled(!(LIMIT >= searchResultTotalRowsCount));
            listOfListable.removeAllElements();
            OFFSET = 0;
            incrementedReturnedRowsCount = 0;
            List<Listable> listables = CRUDListable.search(listableImplementation, tfSearch.getText(), LIMIT, OFFSET);
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
            List<Listable> listables = CRUDListable.search(listableImplementation, searchQueryImmutableCopy, LIMIT, OFFSET);
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
    }

    private class ListSelectionHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent event) {
            JList list = (JList) event.getSource();
            if (!event.getValueIsAdjusting()) {
                Source source = (Source) list.getSelectedValue();
                if (source != null && source instanceof Source) {
                    btnOK.setEnabled(true);
                } else {
                    btnOK.setEnabled(false);
                }
            }
        }
    }

}
