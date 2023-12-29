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
import warehouse.db.CRUDQuantityUnit;
import warehouse.db.model.QuantityUnit;

/**
 *
 * @author Saleh
 */
public class ManageQuantityUnitDialog extends Dialog {

    private JPanel panel;
    private MigLayout mig;
    private JLabel lbQuantityUnit;
    private JTextField tfQuantityUnit;
    private JButton btnSubmit, btnClose;
    private List list;
    private JList listUnits;
    private QuantityUnit quantityUnit;
    private ActionListener btnListner;
    private ManageQuantityUnitDialog quantityUnitDialog;

    public ManageQuantityUnitDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        quantityUnitDialog = ManageQuantityUnitDialog.this;
        mig = new MigLayout("center center");
        panel = new JPanel(mig);
        btnListner = new BtnListener();
        lbQuantityUnit = new JLabel("Quantity unit:");
        tfQuantityUnit = new JTextField(15);
        btnSubmit = new JButton("Submit");
        btnSubmit.addActionListener(btnListner);
        list = new List();
        listUnits = list.getJList();
        listUnits.addMouseListener(new MouseJListHandler());
        btnClose = new JButton("Close X");
        btnClose.addActionListener(btnListner);

        panel.add(lbQuantityUnit);
        panel.add(tfQuantityUnit);
        panel.add(btnSubmit, "wrap");
        panel.add(list.getListScrolledPane(), "span");
        panel.add(btnClose);
        add(panel);
        pack();
    }

    public void rePopulateUnitsList() {
        list.removeAllElements();
        ArrayList<QuantityUnit> quantityUnits = CRUDQuantityUnit.getAll();
        quantityUnits.forEach(unit -> {
            list.addElement(unit);
        });
    }

    private class BtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == btnSubmit) {
                quantityUnit = new QuantityUnit();
                quantityUnit.setUnit(tfQuantityUnit.getText());
                if (CRUDQuantityUnit.isExist(quantityUnit)) {
                    JOptionPane.showMessageDialog(quantityUnitDialog,
                            "Unit " + tfQuantityUnit.getText() + " is already exist!.",
                            "Duplicate unit",
                            JOptionPane.ERROR_MESSAGE);
                    //ManageSourceLocationDialog.this.dispose();
                } else {
                    if (CRUDQuantityUnit.create(quantityUnit) == 1) {
                        rePopulateUnitsList();
                        JOptionPane.showMessageDialog(quantityUnitDialog,
                                "Unit " + tfQuantityUnit.getText() + " was added successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        tfQuantityUnit.setText(null);
                    } else {
                        JOptionPane.showMessageDialog(quantityUnitDialog,
                                "Some problem happened, unit CANNOT be added!.",
                                "Failure",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else if (source == btnClose) {
                quantityUnitDialog.dispose();
            }
        }
    }

    private class MouseJListHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                listUnits.setSelectedIndex(listUnits.locationToIndex(e.getPoint()));

                JPopupMenu menu = new JPopupMenu();
                JMenuItem itemRemove = new JMenuItem("Remove");
                itemRemove.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // This could probably be improved, but assuming you
                        // also keep the values in an ArrayList, you can 
                        // remove the element with this:
                        //array_list.remove(listbox.getSelectedValue());
                        //listbox.setListData((String[]) array_list.toArray(new String[array_list.size()]));
                        System.out.println("Remove the element in position " + listUnits.getSelectedValue());
                    }
                });
                menu.add(itemRemove);
                menu.show(listUnits, e.getPoint().x, e.getPoint().y);
            }
        }

    }
}
