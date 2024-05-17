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

import warehouse.panel.outwards.OutwardDialog;
import warehouse.panel.inwards.InwardDialog;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
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
import warehouse.db.model.Item;
import warehouse.db.model.ItemMeta;
import warehouse.db.model.QuantityUnit;
import warehouse.panel.createandupdate.ItemCreateUpdateDialog;

/**
 *
 * @author Saleh
 */
public class ItemsList extends JPanel
        implements
        ItemCRUDListener,
        ItemsSearchListener {

    private ItemTableModel model;
    private JTable table;
    private JScrollPane scrollTable;
    private Integer selectedModelRow;
    private ArrayList<RowIdSelectionListener> rowIdSelectionListeners;
    private final JPopupMenu popupMenu;
    private final JMenuItem menuItemInwardsOfSelectedItem,
            menuItemOutwardOfSelectedItem,
            menuItemUpdateItem;
    private InwardDialog inwardCreateDialog;
    private OutwardDialog outwardCreateDialog;
    private ItemCreateUpdateDialog updateItemDialog;
    private JButton btnLoadMore;
    private int searchResultTotalRowsCount,
            incrementedReturnedRowsCount,
            rowIndex,
            tableRow;
    private NameAndSpecDisplayFields nameAndSpecDisplayFields;

    public ItemsList() {

        setLayout(new BorderLayout());
        rowIdSelectionListeners = new ArrayList<>();
        model = new ItemTableModel();

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
        table.getSelectionModel().addListSelectionListener(new RowSelectionListener());
        table.setFont(new Font("SansSerif", Font.BOLD, 14));
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(1);
        table.getColumnModel().getColumn(1).setPreferredWidth(1);
        table.getColumnModel().getColumn(2).setPreferredWidth(1);
        table.getColumnModel().getColumn(3).setPreferredWidth(1);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setComponentPopupMenu(popupMenu);
        scrollTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollTable, BorderLayout.CENTER);

        btnLoadMore = new JButton("Load more");
        btnLoadMore.setEnabled(false);
        //  btnLoadMore.addActionListener(new LoadMoreHandler());
        add(btnLoadMore, BorderLayout.PAGE_END);
        inwardCreateDialog = new InwardDialog(null, "Inward", true);
        outwardCreateDialog = new OutwardDialog(null, "Outward", true);
        updateItemDialog = new ItemCreateUpdateDialog(null, "Update item", true);
    }

    public ItemCreateUpdateDialog getUpdateItemDialog() {
        return updateItemDialog;
    }

    protected JButton getBtnLoadMore() {
        return btnLoadMore;
    }

    public InwardDialog getInwardCreateDialog() {
        return this.inwardCreateDialog;
    }

    public OutwardDialog getOutwardCreateDialog() {
        return this.outwardCreateDialog;
    }

    protected void setnameAndSpecDisplayFields(NameAndSpecDisplayFields nameAndSpecDisplayFields) {
        this.nameAndSpecDisplayFields = nameAndSpecDisplayFields;
    }

    @Override
    public void created(Item item) {
        ItemMeta itemMeta = new ItemMeta();
        itemMeta.setId(item.getId());
        itemMeta.setName(item.getName());
        itemMeta.setSpecification(item.getSpecification());
        itemMeta.setBalance(BigDecimal.ONE);
        itemMeta.setQuantityUnit(item.getQuantityUnit());
        model.addItemMeta(itemMeta);
        rowIndex++;
    }

    @Override
    public void updated(Item updatedItem) {
        // "Code", "Name", "Specification", "Balance", "unit_id", "Unit"
        // QuantityUnit unit = (QuantityUnit) CRUDListable.getById(new QuantityUnit(), updatedItem.getQuantityUnit().getId());
        table.setValueAt(updatedItem.getName(), tableRow, 1);
        table.setValueAt(updatedItem.getSpecification(), tableRow, 2);
        // 3 is calculated value not relevant on the update here
        table.setValueAt(updatedItem.getQuantityUnit().getId(), tableRow, 4);
        table.setValueAt(updatedItem.getQuantityUnit().getName(), tableRow, 5);
    }

    @Override
    public void notifyOFFSET(int OFFSET) {
        if (OFFSET == 0) {
            model = new ItemTableModel();
            table.setModel(model);
            incrementedReturnedRowsCount = 0;
        }
    }

    @Override
    public void notifySearchResultTotalRowsCount(int searchResultTotalRowsCount) {
        this.searchResultTotalRowsCount = searchResultTotalRowsCount;
        btnLoadMore.setEnabled(!(ItemsSearchLogic.getResultsPageLimit() >= searchResultTotalRowsCount));
    }

    @Override
    public void notifySearchResult(List<ItemMeta> itemsMeta) {
        /**
         * To organize order after new item insert. After creating new items,
         * new rows added to the model at run time to reflect newly created
         * items. However these rows are off order. So here we remove them, so
         * that they will be fetched through the following fetches or via search
         * requests.
         */
        if (model.getRowCount() > 0) {
            for (int i = incrementedReturnedRowsCount + 1; i <= rowIndex; i++) {
                model.removeItemMeta(incrementedReturnedRowsCount);
            }
        }
        List<ItemMeta> itemsMetaRecords = itemsMeta;
        int size = itemsMetaRecords.size();
        incrementedReturnedRowsCount += size;
        rowIndex = incrementedReturnedRowsCount;
        for (int i = 0; i < size; i++) {
            ItemMeta itemMeta = itemsMetaRecords.get(i);
            model.addItemMeta(itemMeta);
        }
        btnLoadMore.setEnabled(!(incrementedReturnedRowsCount >= searchResultTotalRowsCount));
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

                        int itemNameColumnIndex = 1;
                        int itemSpecificationColumnIndex = 2;

                        selectedModelRow = table.convertRowIndexToModel(viewRow);
                        String itemNameObject = (String) table.getModel().getValueAt(selectedModelRow, itemNameColumnIndex);
                        String itemSpecificationObject = (String) table.getModel().getValueAt(selectedModelRow, itemSpecificationColumnIndex);
                        nameAndSpecDisplayFields.setTfItemNameText(itemNameObject);
                        nameAndSpecDisplayFields.setTfItemSpecificationsText(itemSpecificationObject);
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

    private class PopupMenuItemActionHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            tableRow = table.getSelectedRow();
            int modelRowIndex = table.convertRowIndexToModel(tableRow);
            ItemMeta itemMeta = model.getItemMeta(modelRowIndex);

            Object source = e.getSource();
            if (source == menuItemInwardsOfSelectedItem) {
                inwardCreateDialog.setItemMeta(itemMeta);
                inwardCreateDialog.setVisible(true);
            } else if (source == menuItemOutwardOfSelectedItem) {
                outwardCreateDialog.setItemMeta(itemMeta);
                outwardCreateDialog.setVisible(true);
            } else if (source == menuItemUpdateItem) {
                updateItemDialog.setTfName(itemMeta.getName());
                updateItemDialog.setTfSpecs(itemMeta.getSpecification());
                QuantityUnit quantityUnit = new QuantityUnit();
                quantityUnit.setId(itemMeta.getQuantityUnit().getId());
                quantityUnit.setName(itemMeta.getQuantityUnit().getName());
                updateItemDialog.setItemIdForUpdate(itemMeta.getId());
                updateItemDialog.getFormManagement().addItemCRUDListener(ItemsList.this);
                updateItemDialog.setUnitName(quantityUnit);
                updateItemDialog.setItemImages(itemMeta.getId());
                updateItemDialog.setVisible(true);
            }
        }
    }

}
