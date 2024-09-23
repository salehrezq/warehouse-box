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
package warehousebox.utility.singularlisting;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import warehousebox.utility.scrollbarthin.ScrollBarThin;
import warehousebox.db.CRUDListable;
import warehousebox.panel.menus.ResultLimitSizePreference;

/**
 *
 * @author Saleh
 */
public class ListableItemForm extends JPanel implements ListableConsumer {

    private JPanel container, panelSearch, panelList;
    private JLabel label;
    private JTextField tfSearch;
    private ScrollBarThin scrollBarThinTfSearch;
    private JButton btnSearch, btnLoadMore;
    private ListOfListable listOfListable;
    private JList listing;
    private Listable listableImplementation;
    // private ActionListener btnListener;
    private ListableItemForm thisListableItemManageClass;
    private int searchResultTotalRowsCount, incrementedReturnedRowsCount;
    private static int LIMIT,
            OFFSET;
    private String searchQueryImmutableCopy;
    private List<LoadMoreEnabledListener> loadMoreEnabledListeners;

    public ListableItemForm() {
        //   super(owner, title, modal);

        loadMoreEnabledListeners = new ArrayList<>();
        thisListableItemManageClass = ListableItemForm.this;
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
        add(container, BorderLayout.CENTER);
        this.setMinimumSize(new Dimension(480, 350));
    }

    @Override
    public void setListableImpl(Listable listable) {
        this.listableImplementation = listable;
    }

    public void setLabelText(String labelText) {
        label.setText(labelText);
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

    public void resetFields() {
        tfSearch.setText("");
        btnLoadMore.setEnabled(false);
        notifyloadMoreEnabled(btnLoadMore.isEnabled());
        listOfListable.removeAllElements();
    }

    public void setListablePreferredSize(int with, int height) {
        listOfListable.setPreferredSize(with, height);
    }

    public JTextField getTfSearch() {
        return tfSearch;
    }

    public JButton getBtnSearch() {
        return btnSearch;
    }

    public JButton getBtnLoadMore() {
        return btnLoadMore;
    }

    public JList getlist() {
        return listOfListable.getJList();
    }

    public void addLoadMoreEnabledListener(LoadMoreEnabledListener lmel) {
        this.loadMoreEnabledListeners.add(lmel);
    }

    public void notifyloadMoreEnabled(boolean enabled) {
        this.loadMoreEnabledListeners.forEach((lmel) -> {
            lmel.loadMoreEnabled(enabled);
        });
    }

    private class BtnSearchHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            searchQueryImmutableCopy = tfSearch.getText();
            searchResultTotalRowsCount = CRUDListable.searchResultRowsCount(listableImplementation, tfSearch.getText());
            LIMIT = ResultLimitSizePreference.getResultLimitSize();
            btnLoadMore.setEnabled(!(LIMIT >= searchResultTotalRowsCount));
            notifyloadMoreEnabled(btnLoadMore.isEnabled());
            listOfListable.removeAllElements();
            OFFSET = 0;
            incrementedReturnedRowsCount = 0;
            List<Listable> listables = CRUDListable.search(listableImplementation, tfSearch.getText(), LIMIT, OFFSET);
            if (listables.isEmpty()) {
                JOptionPane.showMessageDialog(ListableItemForm.this, "No matched results!", "Info",
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
            List<Listable> listables = CRUDListable.search(listableImplementation, searchQueryImmutableCopy, LIMIT, OFFSET);
            incrementedReturnedRowsCount += listables.size();
            listables.forEach(listable -> {
                listOfListable.addElement(listable);
            });
            btnLoadMore.setEnabled(!(incrementedReturnedRowsCount >= searchResultTotalRowsCount));
            notifyloadMoreEnabled(btnLoadMore.isEnabled());
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

}
