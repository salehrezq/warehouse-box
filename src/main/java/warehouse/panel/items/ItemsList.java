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
package warehouse.panel.items;

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
import warehouse.db.CRUDItems;
import warehouse.db.CreateListener;
import warehouse.db.model.ItemMeta;
import warehouse.db.model.QuantityUnit;
import warehouse.panel.createandupdate.CreateItemDialog;
import warehouse.singularlisting.Listable;
import warehouse.singularlisting.ListableConsumer;

/**
 *
 * @author Saleh
 */
public class ItemsList extends JPanel implements CreateListener, ListableConsumer {

    private DefaultTableModel model;
    private JTable table;
    private JScrollPane scrollTable;
    private Integer selectedModelRow;
    private ArrayList<RowIdSelectionListener> rowIdSelectionListeners;
    private Listable listableImplementation;
    private final JPopupMenu popupMenu;
    private final JMenuItem menuItemInwardsOfSelectedItem,
            menuItemOutwardOfSelectedItem,
            menuItemUpdateItem;
    private InwardDialog inwardDialog;
    private OutwardDialog outwardDialog;
    private CreateItemDialog updateItemDialog;

    public ItemsList() {

        setLayout(new BorderLayout());
        rowIdSelectionListeners = new ArrayList<>();
        model = new DefaultTableModel(new String[]{"Code", "Name", "Specification", "Balance", "unit_id", "Unit"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Disable cells editing.
                return false;
            }
        };

        popupMenu = new JPopupMenu();
        popupMenu.addPopupMenuListener(new RowMouseRightClickHandler());
        menuItemInwardsOfSelectedItem = new JMenuItem("Inwards");
        menuItemInwardsOfSelectedItem.addActionListener(new PopupMenuItemActionHandler());
        menuItemOutwardOfSelectedItem = new JMenuItem("Outwards");
        menuItemOutwardOfSelectedItem.addActionListener(new PopupMenuItemActionHandler());
        menuItemUpdateItem = new JMenuItem("Edit item");
        menuItemUpdateItem.addActionListener(new PopupMenuItemActionHandler());
        popupMenu.add(menuItemInwardsOfSelectedItem);
        popupMenu.add(menuItemOutwardOfSelectedItem);
        popupMenu.addSeparator();
        popupMenu.add(menuItemUpdateItem);

        table = new JTable(model);
        table.addMouseListener(new ItemRowDoubleClickHandler());
        //    table.getSelectionModel().addListSelectionListener(new RowSelectionListener());
        table.setFont(new Font("SansSerif", Font.BOLD, 14));
        table.setFillsViewportHeight(true);
        // Hide column number 4 which holds unit_id values
        table.removeColumn(table.getColumnModel().getColumn(4));
        table.getColumnModel().getColumn(0).setPreferredWidth(1);
        table.getColumnModel().getColumn(1).setPreferredWidth(1);
        table.getColumnModel().getColumn(2).setPreferredWidth(1);
        table.getColumnModel().getColumn(3).setPreferredWidth(1);
        table.getColumnModel().getColumn(4).setPreferredWidth(1);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setComponentPopupMenu(popupMenu);
        scrollTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollTable, BorderLayout.CENTER);
        inwardDialog = new InwardDialog(null, "Inward", true);
        outwardDialog = new OutwardDialog(null, "Outward", true);
        updateItemDialog = new CreateItemDialog(null, "Update item", true);
    }

    @Override
    public void setListableImpl(Listable listable) {
        this.listableImplementation = listable;
        created();
    }

    public InwardDialog getInwardDialog() {
        return this.inwardDialog;
    }

    public OutwardDialog getOutwardDialog() {
        return this.outwardDialog;
    }

    @Override
    public void created() {
        System.out.println("Refresh items to reflect newly created item");
        // Clear the model every time, to append fresh results
        // and not accumulate on previous results
        model.setRowCount(0);
        List<ItemMeta> itemsMetaRecords = new ArrayList();
        itemsMetaRecords = CRUDItems.getMetaAll();
        Object[] modelRow = new Object[6];

        int size = itemsMetaRecords.size();
        for (int i = 0; i < size; i++) {
            ItemMeta itemMeta = itemsMetaRecords.get(i);
            modelRow[0] = itemMeta.getId(); //code
            modelRow[1] = itemMeta.getName();
            modelRow[2] = itemMeta.getSpecification();
            modelRow[3] = itemMeta.getBalance();
            modelRow[4] = itemMeta.getUnitId(); // will be hidden column
            modelRow[5] = itemMeta.getUnit();
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
                int itemIdColumnIndex = 0;
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

    private Object getColumnOfSelectedRow(int column) {
        selectedModelRow = table.convertRowIndexToModel(table.getSelectedRow());
        return table.getModel().getValueAt(selectedModelRow, column);
    }

    private class PopupMenuItemActionHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //"Code", "Name", "Specification", "Balance", "unit_id", "Unit"
            int itemIdColumn = 0;
            int itemNameColumn = 1;
            int itemSpecsColumn = 2;
            int itemBalanceColumn = 3; // not needed
            int itemUnitIdColumn = 4; // hiddenColumn
            int itemUnitColumn = 5;
            // Retrieving values from columns
            int itemId = (Integer) getColumnOfSelectedRow(itemIdColumn);
            String itemName = (String) getColumnOfSelectedRow(itemNameColumn);
            String itemSpecs = (String) getColumnOfSelectedRow(itemSpecsColumn);
            int unitId = (Integer) getColumnOfSelectedRow(itemUnitIdColumn);
            String itemUnit = (String) getColumnOfSelectedRow(itemUnitColumn);
            System.out.println("unit " + itemUnit);

            Object source = e.getSource();
            if (source == menuItemInwardsOfSelectedItem) {
                inwardDialog.setItemId(itemId);
                inwardDialog.setItemUnit(itemUnit);
                inwardDialog.setVisible(true);
            } else if (source == menuItemOutwardOfSelectedItem) {
                outwardDialog.setItemId(itemId);
                outwardDialog.setItemUnit(itemUnit);
                outwardDialog.setVisible(true);
            } else if (source == menuItemUpdateItem) {
                updateItemDialog.setTfName(itemName);
                updateItemDialog.setTfSpecs(itemSpecs);
                QuantityUnit quantityUnit = new QuantityUnit();
                quantityUnit.setId(unitId);
                quantityUnit.setName(itemUnit);
                updateItemDialog.setItemIdForUpdate(itemId);
                updateItemDialog.setUnitName(quantityUnit);
                updateItemDialog.setItemImages(itemId);
                updateItemDialog.setVisible(true);
            }

            System.out.println(itemId);
        }
    }

}
