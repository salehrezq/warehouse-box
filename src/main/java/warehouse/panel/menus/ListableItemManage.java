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
import java.util.ArrayList;
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
    private JButton btnSubmit, btnClose, btnCreate, btnLoadMore;
    private List list;
    private JList listing;
    private Listable listableImplementation;
    private ActionListener btnListener;
    private ListableItemManage thisListableItemManageClass;

    public ListableItemManage(Frame owner, String title, boolean modal) {
        super(owner, title, modal);

        thisListableItemManageClass = ListableItemManage.this;

        panelSearch = new JPanel();
        panelList = new JPanel(new BorderLayout());
        panelCreate = new JPanel();
        container = new JPanel(new BorderLayout());
        btnListener = new BtnListener();

        label = new JLabel();
        tfSearch = new JTextField(25);
        btnSubmit = new JButton("Search");
        btnSubmit.addActionListener(btnListener);
        list = new List();
        listing = list.getJList();
        listing.addMouseListener(new MouseJListHandler());
        btnClose = new JButton("Close X");
        btnClose.addActionListener(btnListener);

        panelSearch.add(label);
        panelSearch.add(tfSearch);
        panelSearch.add(btnSubmit);

        btnLoadMore = new JButton("Load more");
        panelList.add(list.getListScrolledPane(), BorderLayout.CENTER);
        panelList.add(btnLoadMore, BorderLayout.PAGE_END);

        tfCreate = new JTextField(25);
        btnCreate = new JButton("Create");

        panelCreate.add(tfCreate);
        panelCreate.add(btnCreate);

        container.add(panelSearch, BorderLayout.PAGE_START);
        container.add(panelList, BorderLayout.CENTER);
        container.add(panelCreate, BorderLayout.PAGE_END);
        add(container);
        this.setMinimumSize(new Dimension(480, 350));
    }

    @Override
    public void setListableImpl(Listable listable) {
        this.listableImplementation = listable;
        label.setText(listable.getLabel());
    }

    public void rePopulateUnitsList() {
        list.removeAllElements();
        ArrayList<Listable> listables = CRUDListable.getAll(listableImplementation);
        listables.forEach(listableItem -> {
            list.addElement(listableItem);
        });
    }

    private class BtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == btnSubmit) {
                listableImplementation.setName(tfSearch.getText());
                if (CRUDListable.isExist(listableImplementation)) {
                    JOptionPane.showMessageDialog(thisListableItemManageClass,
                            listableImplementation.getLabel() + " " + tfSearch.getText() + " is already exist!.",
                            "Duplicate entry",
                            JOptionPane.ERROR_MESSAGE);
                    //ManageSourceLocationDialog.this.dispose();
                } else {
                    if (CRUDListable.create(listableImplementation) == 1) {
                        rePopulateUnitsList();
                        JOptionPane.showMessageDialog(thisListableItemManageClass,
                                listableImplementation.getLabel() + " " + tfSearch.getText() + " was added successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        tfSearch.setText(null);
                    } else {
                        JOptionPane.showMessageDialog(thisListableItemManageClass,
                                "Some problem happened; " + listableImplementation.getLabel() + " CANNOT be added!.",
                                "Failure",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else if (source == btnClose) {
                thisListableItemManageClass.dispose();
            }
        }
    }

    private class MouseJListHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                listing.setSelectedIndex(listing.locationToIndex(e.getPoint()));

                JPopupMenu menu = new JPopupMenu();
                JMenuItem itemRemove = new JMenuItem("Remove");
                itemRemove.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // This could probably be improved, but assuming you
                        // also keep the values in an ArrayList, you can 
                        // remove the element with this:
                        //array_list.remove(listbox.getSelectedValue());
                        //listbox.setListData((String[]) array_list.toArray(new String[array_list.size()]));
                        System.out.println("Remove the element in position " + listing.getSelectedValue());
                    }
                });
                menu.add(itemRemove);
                menu.show(listing, e.getPoint().x, e.getPoint().y);
            }
        }

    }
}
