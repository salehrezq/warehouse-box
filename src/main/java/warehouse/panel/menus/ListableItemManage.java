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
package warehouse.panel.menus;

import java.awt.BorderLayout;
import java.awt.Dimension;
import warehouse.singularlisting.ListableConsumer;
import warehouse.singularlisting.Listable;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import warehouse.db.CRUDListable;

/**
 *
 * @author Saleh
 */
public class ListableItemManage extends JDialog implements ListableConsumer {

    private JPanel container, panelSearch, panelList, panelCreate;
    private JLabel label;
    private JTextField tfSearch, tfCreate;
    private JButton btnSearch, btnClose, btnSubmit, btnLoadMore;
    private ListOfListable listOfListable;
    private JList listing;
    private Listable listableImplementation;
    // private ActionListener btnListener;
    private ListableItemManage thisListableItemManageClass;
    private int searchResultTotalRowsCount, incrementedReturnedRowsCount;
    private static int LIMIT,
            OFFSET;
    private String searchQueryImmutableCopy;
    private final JPopupMenu popupMenu;
    private final JMenuItem menuListableRemove;
    private final JMenuItem menuListableEdit;
    private ListableItemEditDialog listableItemEditDialog;
    private PopupMenuListableHandler popupMenuListableHandler;
    private Listable listable;

    public ListableItemManage(Frame owner, String title, boolean modal) {
        super(owner, title, modal);

        LIMIT = 3;

        popupMenuListableHandler = new PopupMenuListableHandler();
        thisListableItemManageClass = ListableItemManage.this;
        listableItemEditDialog = new ListableItemEditDialog(null, "Edit", true);
        popupMenu = new JPopupMenu();
        popupMenu.addMouseListener(new MouseJListHandler());
        menuListableEdit = new JMenuItem("Edit");
        menuListableEdit.addActionListener(popupMenuListableHandler);
        menuListableRemove = new JMenuItem("Remove");
        menuListableRemove.addActionListener(popupMenuListableHandler);

        popupMenu.add(menuListableEdit);
        popupMenu.addSeparator();
        popupMenu.add(menuListableRemove);

        panelSearch = new JPanel();
        panelList = new JPanel(new BorderLayout());
        panelCreate = new JPanel();
        container = new JPanel(new BorderLayout());
        //  btnListener = new BtnListener();

        label = new JLabel();
        tfSearch = new JTextField(25);
        btnSearch = new JButton("Search");
        btnSearch.addActionListener(new BtnSearchHandler());
        listOfListable = new ListOfListable();
        listing = listOfListable.getJList();
        listing.addMouseListener(new MouseJListHandler());
        btnClose = new JButton("Close X");
        //  btnClose.addActionListener(btnListener);

        panelSearch.add(label);
        panelSearch.add(tfSearch);
        panelSearch.add(btnSearch);

        btnLoadMore = new JButton("Load more");
        btnLoadMore.setEnabled(false);
        btnLoadMore.addActionListener(new BtnLoadMoreHandler());
        panelList.add(listOfListable.getListScrolledPane(), BorderLayout.CENTER);
        panelList.add(btnLoadMore, BorderLayout.PAGE_END);

        tfCreate = new JTextField(25);
        btnSubmit = new JButton("Create");
        btnSubmit.addActionListener(new BtnSubmit());

        panelCreate.add(tfCreate);
        panelCreate.add(btnSubmit);

        container.add(panelSearch, BorderLayout.PAGE_START);
        container.add(panelList, BorderLayout.CENTER);
        container.add(panelCreate, BorderLayout.PAGE_END);
        add(container);
        this.setMinimumSize(new Dimension(480, 350));
    }

    @Override
    public void setListableImpl(Listable listable) {
        this.listableImplementation = listable;
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

    private class BtnSubmit implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            listableImplementation.setName(tfCreate.getText());
            if (CRUDListable.isExist(listableImplementation)) {
                JOptionPane.showMessageDialog(thisListableItemManageClass,
                        listableImplementation.getLabel() + " " + tfCreate.getText() + " is already exist!.",
                        "Duplicate entry",
                        JOptionPane.ERROR_MESSAGE);
                //ManageSourceLocationDialog.this.dispose();
            } else {
                boolean created = CRUDListable.create(listableImplementation);
                if (created) {
                    JOptionPane.showMessageDialog(thisListableItemManageClass,
                            listableImplementation.getLabel() + " " + tfCreate.getText() + " "
                            + "was added successfully. You can find it on a next search.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    tfCreate.setText(null);
                } else {
                    JOptionPane.showMessageDialog(thisListableItemManageClass,
                            "Some problem happened; " + listableImplementation.getLabel() + " CANNOT be added!.",
                            "Failure",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class MouseJListHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                listing.setSelectedIndex(listing.locationToIndex(e.getPoint()));
                popupMenu.show(listing, e.getPoint().x, e.getPoint().y);
            }
        }
    }

    private class PopupMenuListableHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem source = (JMenuItem) e.getSource();
            listable = (Listable) listing.getSelectedValue();
            if (source == menuListableRemove) {
                boolean isInUse = CRUDListable.isListableInUse(listable);
                if (isInUse) {
                    JOptionPane.showMessageDialog(
                            thisListableItemManageClass,
                            listableImplementation.getLabel() + " cannot be deleted because it is in use.",
                            "In use!",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    int reply = JOptionPane.showConfirmDialog(
                            null,
                            "Are you sure to delete this " + listableImplementation.getLabel(),
                            "DELETE!",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (reply == JOptionPane.YES_OPTION) {
                        boolean deleted = CRUDListable.delete(listable);
                        if (deleted) {
                            listOfListable.removeElement(listing.getSelectedIndex());
                            // notifyOutwardDeleted(outward);
                            JOptionPane.showMessageDialog(
                                    null,
                                    listableImplementation.getLabel() + " deleted successfully",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Issue: " + listableImplementation.getLabel() + " was not deleted",
                                    "Failure",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            } else if (source == menuListableEdit) {
                listableItemEditDialog.setTfListableText(listable);
                listableItemEditDialog.setVisible(true);
            }
        }
    }
}
