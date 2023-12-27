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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.miginfocom.swing.MigLayout;
import warehouse.db.CRUDSourceLocation;
import warehouse.db.model.SourceLocation;

/**
 *
 * @author Saleh
 */
public class ManageSourceLocationDialog extends Dialog {

    private JPanel panel;
    private MigLayout mig;
    private JLabel lbSourceLocation;
    private JTextField tfSourceLocation;
    private JButton btnSubmit, btnClose;
    private List list;
    private JList listLocations;
    private SourceLocation sourceLocation;
    private ActionListener btnListner;
    private ManageSourceLocationDialog sourceLocationDialog;

    public ManageSourceLocationDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        sourceLocationDialog = ManageSourceLocationDialog.this;
        mig = new MigLayout("center center");
        panel = new JPanel(mig);
        btnListner = new BtnListener();
        lbSourceLocation = new JLabel("Source location:");
        tfSourceLocation = new JTextField(15);
        btnSubmit = new JButton("Submit");
        btnSubmit.addActionListener(btnListner);
        list = new List();
        listLocations = list.getJList();
        listLocations.addMouseListener(new MouseJListHandler());
        btnClose = new JButton("Close X");
        btnClose.addActionListener(btnListner);

        panel.add(lbSourceLocation);
        panel.add(tfSourceLocation);
        panel.add(btnSubmit, "wrap");
        panel.add(list.getListScrolledPane(), "span");
        panel.add(btnClose);
        add(panel);
        pack();
    }

    public void rePopulateLocationsList() {
        list.removeAllElements();
        ArrayList<SourceLocation> sourceLocations = CRUDSourceLocation.getAll();
        sourceLocations.forEach(location -> {
            list.addElement(location);
        });
    }

    private class BtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == btnSubmit) {
                sourceLocation = new SourceLocation();
                sourceLocation.setLocation(tfSourceLocation.getText());
                if (CRUDSourceLocation.isExist(sourceLocation)) {
                    JOptionPane.showMessageDialog(sourceLocationDialog,
                            "Location " + tfSourceLocation.getText() + " is already exist!.",
                            "Duplicate location",
                            JOptionPane.ERROR_MESSAGE);
                    //ManageSourceLocationDialog.this.dispose();
                } else {
                    if (CRUDSourceLocation.create(sourceLocation) == 1) {
                        rePopulateLocationsList();
                        JOptionPane.showMessageDialog(sourceLocationDialog,
                                "Location " + tfSourceLocation.getText() + " was added successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        tfSourceLocation.setText(null);
                    } else {
                        JOptionPane.showMessageDialog(sourceLocationDialog,
                                "Some problem happened, location CANNOT be added!.",
                                "Failure",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else if (source == btnClose) {
                sourceLocationDialog.dispose();
            }
        }
    }

    private class MouseJListHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                listLocations.setSelectedIndex(listLocations.locationToIndex(e.getPoint()));

                JPopupMenu menu = new JPopupMenu();
                JMenuItem itemRemove = new JMenuItem("Remove");
                itemRemove.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // This could probably be improved, but assuming you
                        // also keep the values in an ArrayList, you can 
                        // remove the element with this:
                        //array_list.remove(listbox.getSelectedValue());
                        //listbox.setListData((String[]) array_list.toArray(new String[array_list.size()]));
                        System.out.println("Remove the element in position " + listLocations.getSelectedValue());
                    }
                });
                menu.add(itemRemove);
                menu.show(listLocations, e.getPoint().x, e.getPoint().y);
            }
        }

    }
}
