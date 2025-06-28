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
package warehousebox.utility.recipientslisting;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import warehousebox.db.model.Recipient;

/**
 *
 * @author Saleh
 */
public class ListOfRecipients {

    private JList list;
    private DefaultListModel listModel;
    private JScrollPane scrollPane;

    public ListOfRecipients() {
        list = new JList();
        list.setCellRenderer(new ListCellQuantityUnitRenderer());
        listModel = new DefaultListModel();
        list.setModel(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.setVisibleRowCount(5);
        scrollPane = new JScrollPane(list);
    }

    public Component getListScrolledPane() {
        return scrollPane;
    }

    public JList getJList() {
        return this.list;
    }

    public void addElement(Recipient listable) {
        listModel.addElement(listable);
    }

    public Recipient removeElement(int index) {
        return (Recipient) listModel.remove(index);
    }

    public void removeAllElements() {
        listModel.removeAllElements();
    }

    protected void setPreferredSize(int with, int height) {
        scrollPane.setPreferredSize(new Dimension(with, height));
    }

    protected void setPreviewSelected(Recipient listable) {
        removeAllElements();
        addElement(listable);
        // Set the only added element selected
        list.getSelectionModel().setSelectionInterval(0, 0);
    }

    public Recipient getSelectedValue() {
        return (Recipient) list.getSelectedValue();
    }

    private class ListCellQuantityUnitRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list,
                Object item,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            super.getListCellRendererComponent(list,
                    item,
                    index,
                    isSelected,
                    cellHasFocus);
            if (item != null && (item instanceof Recipient)) {
                Recipient listable = (Recipient) item;
                setText(listable.getName());
            }
            return this;
        }
    }

}
