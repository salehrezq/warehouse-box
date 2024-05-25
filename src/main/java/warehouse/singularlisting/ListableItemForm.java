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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import warehouse.db.CRUDListable;

/**
 *
 * @author Saleh
 */
public class ListableItemForm extends JPanel implements ListableConsumer {

    private JPanel container, panelSearch, panelList;
    private JLabel label;
    private JTextField tfSearch;
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

    public ListableItemForm() {
        //   super(owner, title, modal);

        LIMIT = 3;

        thisListableItemManageClass = ListableItemForm.this;

        panelSearch = new JPanel();
        panelList = new JPanel(new BorderLayout());
        container = new JPanel(new BorderLayout());
        //  btnListener = new BtnListener();

        label = new JLabel();
        tfSearch = new JTextField(25);
        btnSearch = new JButton("Search");
        btnSearch.addActionListener(new BtnSearchHandler());
        listOfListable = new ListOfListable();
        listing = listOfListable.getJList();
        listing.addMouseListener(new MouseJListHandler());
        //  btnClose.addActionListener(btnListener);

        panelSearch.add(label);
        panelSearch.add(tfSearch);
        panelSearch.add(btnSearch);

        btnLoadMore = new JButton("Load more");
        btnLoadMore.setEnabled(false);
        btnLoadMore.addActionListener(new BtnLoadMoreHandler());
        panelList.add(listOfListable.getListScrolledPane(), BorderLayout.CENTER);
        panelList.add(btnLoadMore, BorderLayout.PAGE_END);

        container.add(panelSearch, BorderLayout.PAGE_START);
        container.add(panelList, BorderLayout.CENTER);
        add(container);
        this.setMinimumSize(new Dimension(480, 350));
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

    private class MouseJListHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                listing.setSelectedIndex(listing.locationToIndex(e.getPoint()));
            }
        }
    }

}
