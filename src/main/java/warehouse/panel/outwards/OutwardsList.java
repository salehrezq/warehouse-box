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
package warehouse.panel.outwards;

import warehouse.panel.items.*;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import warehouse.db.CRUDOutwards;
import warehouse.db.CreateListener;
import warehouse.db.model.OutwardMeta;
import warehouse.singularlisting.Listable;
import warehouse.singularlisting.ListableConsumer;

/**
 *
 * @author Saleh
 */
public class OutwardsList extends JPanel implements CreateListener, ListableConsumer {

    private DefaultTableModel model;
    private JTable table;
    private JScrollPane scrollTable;
    private Integer selectedModelRow;
    private ArrayList<RowIdSelectionListener> rowIdSelectionListeners;
    private Listable listableImplementation;
    private final JPopupMenu popupMenu;
    private final JMenuItem menuItemAddOfSelectedItem;

    public OutwardsList() {

        setLayout(new BorderLayout());
        rowIdSelectionListeners = new ArrayList<>();
        model = new DefaultTableModel(new String[]{"Outward code", "Item code", "Qty.", "Unit", "Recipient", "For", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Disable cells editing.
                return false;
            }
        };

        popupMenu = new JPopupMenu();
        popupMenu.addPopupMenuListener(new RowMouseRightClickHandler());
        menuItemAddOfSelectedItem = new JMenuItem("Inwards");
        menuItemAddOfSelectedItem.addActionListener(new PopupMenuItemActionHandler());
        popupMenu.add(menuItemAddOfSelectedItem);

        table = new JTable(model);
        table.addMouseListener(new ItemRowDoubleClickHandler());
        //    table.getSelectionModel().addListSelectionListener(new RowSelectionListener());
        table.setFont(new Font("SansSerif", Font.BOLD, 14));
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(2);
        table.getColumnModel().getColumn(1).setPreferredWidth(2);
        table.getColumnModel().getColumn(2).setPreferredWidth(2);
        table.getColumnModel().getColumn(3).setPreferredWidth(1);
        table.getColumnModel().getColumn(4).setPreferredWidth(2);
        table.getColumnModel().getColumn(5).setPreferredWidth(2);
        table.getColumnModel().getColumn(6).setPreferredWidth(2);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setComponentPopupMenu(popupMenu);
        scrollTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollTable, BorderLayout.CENTER);
    }

    @Override
    public void setListableImpl(Listable listable) {
        this.listableImplementation = listable;
        created();
    }

    @Override
    public void created() {
        System.out.println("Refresh outwards to reflect newly inserted outwards");
        // Clear the model every time, to append fresh results
        // and not accumulate on previous results
        model.setRowCount(0);
        List<OutwardMeta> outwardsMetas = new ArrayList();
        outwardsMetas = CRUDOutwards.getAll();
        Object[] modelRow = new Object[7];
        int size = outwardsMetas.size();
        for (int i = 0; i < size; i++) {
            //{"Item code", "Outward code", "Qty.", "Unit", "Recipient", "For" ,"Date"}
            OutwardMeta inwardMeta = outwardsMetas.get(i);
            modelRow[0] = inwardMeta.getId();
            modelRow[1] = inwardMeta.getItemId();
            modelRow[2] = inwardMeta.getQuantity();
            modelRow[3] = inwardMeta.getUnitName();
            modelRow[4] = inwardMeta.getRecipient();
            modelRow[5] = inwardMeta.getUsedFor();
            modelRow[6] = inwardMeta.getDate();
            model.addRow(modelRow);
        }
    }

    public void addRowIdSelectionListener(RowIdSelectionListener var) {
        this.rowIdSelectionListeners.add(var);
    }

    public void notifySelectedRowId(int rowId) {
        this.rowIdSelectionListeners.forEach((item) -> {
            item.selectedRowId(rowId);
        });
    }

    private class RowSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                DefaultListSelectionModel selectionModel = (DefaultListSelectionModel) e.getSource();
                if (selectionModel.isSelectionEmpty()) {
                    // Table row de-selection occurred
                    System.out.println("row de-selected");
                } else {
                    System.out.println("row selected");
                    int viewRow = table.getSelectedRow();
                    if (viewRow > -1) {

                        int itemIdColumnIndex = 0;

                        selectedModelRow = table.convertRowIndexToModel(viewRow);
                        Object itemIdObject = table.getModel().getValueAt(selectedModelRow, itemIdColumnIndex);
                        Integer itemId = Integer.parseInt(itemIdObject.toString());
                        System.out.println("Item ID " + itemId);
                        notifySelectedRowId(itemId);
                    }
                }
            }
        }
    }

    private class ItemRowDoubleClickHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            JTable table = (JTable) mouseEvent.getSource();
            int viewRow = table.getSelectedRow();
            if (mouseEvent.getClickCount() == 2 && viewRow != -1) {
                int itemIdColumnIndex = 1;
                selectedModelRow = table.convertRowIndexToModel(viewRow);
                Object itemIdObject = table.getModel().getValueAt(selectedModelRow, itemIdColumnIndex);
                Integer itemId = Integer.parseInt(itemIdObject.toString());
                System.out.println("Item ID " + itemId);
                notifySelectedRowId(itemId);
            }
        }
    }

    private class RowMouseRightClickHandler implements PopupMenuListener {

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            SwingUtilities.invokeLater(() -> {
                int rowAtPoint = table.rowAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), table));
                if (rowAtPoint > -1) {
                    table.setRowSelectionInterval(rowAtPoint, rowAtPoint);
                }
            });
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            // throw new UnsupportedOperationException
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
            //throw new UnsupportedOperationException
        }
    }

    private class PopupMenuItemActionHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
//            int itemIdColumn = 0;
//            int itemUnitColumn = 3;
//            String itemUnit;
//            selectedModelRow = table.convertRowIndexToModel(table.getSelectedRow());
//            Object itemIdObj = table.getModel().getValueAt(selectedModelRow, itemIdColumn);
//            itemUnit = (String) table.getModel().getValueAt(selectedModelRow, itemUnitColumn);
//            System.out.println("unit " + itemUnit);
//            int itemId = Integer.parseInt(itemIdObj.toString());
//            AddItemsDialog addItemsDialog = new AddItemsDialog(null, "Add items", true);
//            addItemsDialog.setItemId(itemId);
//            addItemsDialog.setItemUnit(itemUnit);
//            addItemsDialog.setVisible(true);
            System.out.println("Placeholder to implement");
        }
    }

}
