/*
 * The MIT License
 *
 * Copyright 2024 Saleh.
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
package warehousebox.panel.menus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import warehousebox.db.CRUDListable;
import warehousebox.utility.singularlisting.Listable;

/**
 *
 * @author Saleh
 */
public class ListableItemEditDialog extends JDialog {

    private JTextField tfListableText;
    private JButton btnEditSave;
    private JButton btnCancel;
    private BtnHandler btnHandler;
    private String oldText;
    private Listable listable;
    private static ArrayList<ListableUpdateListener> listableUpdateListeners;

    public ListableItemEditDialog(JFrame owner, String title, boolean modal) {
        super(owner, title, modal);

        listableUpdateListeners = new ArrayList<>();
        // Initialize the text field
        tfListableText = new JTextField(20);

        btnHandler = new BtnHandler();
        // Initialize the buttons
        btnEditSave = new JButton("Save");
        btnCancel = new JButton("Cancel");

        btnEditSave.addActionListener(btnHandler);
        btnCancel.addActionListener(btnHandler);

        setLayout(new BorderLayout());
        add(tfListableText, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(btnEditSave);
        buttonPanel.add(btnCancel);
        // Add the button panel to the bottom
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public void addListableUpdateListener(ListableUpdateListener listableUpdateListener) {
        listableUpdateListeners.add(listableUpdateListener);
    }

    public void notifyListableUpdate(Listable listable, String oldlistableName) {
        listableUpdateListeners.forEach((listableUpdateListener) -> {
            listableUpdateListener.listableUpdated(listable, oldlistableName);
        });
    }

    protected void setTfListableText(Listable listable) {
        this.listable = listable;
        oldText = listable.getName();
        tfListableText.setText(oldText);
    }

    private class BtnHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton) e.getSource();
            if (source == btnEditSave) {
                String currentText = tfListableText.getText().trim();
                if (!oldText.equals(currentText)) {
                    listable.setName(currentText);
                    boolean isExist = CRUDListable.isExist(listable);
                    System.out.println("CRUDListable.isExist " + isExist);
                    if (isExist) {
                        listable.setName(oldText);
                        JOptionPane.showMessageDialog(
                                null,
                                listable.getLabel() + " is already available. Try another value",
                                "Available!",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        boolean updated = CRUDListable.update(listable);
                        if (updated) {
                            notifyListableUpdate(listable, oldText);
                            JOptionPane.showMessageDialog(
                                    null,
                                    listable.getLabel() + " updated successfully",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Issue: " + listable.getLabel() + " was not updated due to unkown issue.",
                                    "Failure",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                } else {
                    dispose();
                }
            } else if (source == btnCancel) {
                dispose();
            }
        }
    }
}
