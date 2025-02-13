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
package warehousebox.panel.items;

import warehousebox.panel.outbounds.OutboundDialog;
import warehousebox.panel.inbounds.InboundDialog;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import warehousebox.utility.filemanage.ImageFileManager;
import warehousebox.db.CRUDImages;
import warehousebox.db.CRUDItems;
import warehousebox.db.model.Image;
import warehousebox.db.model.Inbound;
import warehousebox.db.model.Item;
import warehousebox.db.model.ItemMeta;
import warehousebox.db.model.Outbound;
import warehousebox.db.model.QuantityUnit;
import warehousebox.panel.createandupdate.ItemCreateUpdateDialog;
import warehousebox.panel.inbounds.InboundCRUDListener;
import warehousebox.panel.inbounds.InboundDeleteListener;
import warehousebox.panel.menus.ListableUpdateListener;
import warehousebox.panel.menus.ResultLimitSizePreference;
import warehousebox.panel.outbounds.OutboundCRUDListener;
import warehousebox.panel.outbounds.OutboundDeleteListener;
import warehousebox.panel.outbounds.OutboundScrapDialog;
import warehousebox.utility.singularlisting.Listable;

/**
 *
 * @author Saleh
 */
public class ItemsList extends JPanel
        implements
        ItemCRUDListener,
        ItemsSearchListener,
        InboundCRUDListener,
        OutboundCRUDListener,
        InboundDeleteListener,
        OutboundDeleteListener,
        ListableUpdateListener {

    private ItemTableModel model;
    private JTable table;
    private JScrollPane scrollTable;
    private Integer selectedModelRow;
    private ArrayList<RowIdSelectionListener> rowIdSelectionListeners;
    private final JPopupMenu popupMenu;
    private final JMenuItem menuItemInboundsOfSelectedItem,
            menuItemOutboundOfSelectedItem,
            menuItemOutboundScrapOfSelectedItem,
            menuItemUpdateItem,
            menuItemDeleteItem;
    private InboundDialog inboundCreateDialog;
    private OutboundDialog outboundCreateDialog;
    private OutboundScrapDialog outboundScrapDialog;
    private ItemCreateUpdateDialog updateItemDialog;
    private JButton btnLoadMore;
    private int searchResultTotalRowsCount,
            incrementedReturnedRowsCount,
            rowIndex,
            tableRow;
    private NameAndSpecDisplayFields nameAndSpecDisplayFields;
    private PopupMenuItemActionHandler popupMenuItemActionHandler;

    public ItemsList() {

        setLayout(new BorderLayout());
        rowIdSelectionListeners = new ArrayList<>();
        model = new ItemTableModel();

        popupMenuItemActionHandler = new PopupMenuItemActionHandler();
        popupMenu = new JPopupMenu();
        popupMenu.addPopupMenuListener(new RowMouseRightClickHandler());
        menuItemInboundsOfSelectedItem = new JMenuItem("Inbounds");
        menuItemInboundsOfSelectedItem.addActionListener(popupMenuItemActionHandler);
        menuItemOutboundOfSelectedItem = new JMenuItem("Outbounds");
        menuItemOutboundOfSelectedItem.addActionListener(popupMenuItemActionHandler);
        menuItemOutboundScrapOfSelectedItem = new JMenuItem("Scrap");
        menuItemOutboundScrapOfSelectedItem.addActionListener(popupMenuItemActionHandler);
        menuItemUpdateItem = new JMenuItem("Edit item");
        menuItemUpdateItem.addActionListener(popupMenuItemActionHandler);
        menuItemDeleteItem = new JMenuItem("Delete");
        menuItemDeleteItem.addActionListener(popupMenuItemActionHandler);
        popupMenu.add(menuItemInboundsOfSelectedItem);
        popupMenu.add(menuItemOutboundOfSelectedItem);
        popupMenu.addSeparator();
        popupMenu.add(menuItemOutboundScrapOfSelectedItem);
        popupMenu.addSeparator();
        popupMenu.add(menuItemUpdateItem);
        popupMenu.addSeparator();
        popupMenu.add(menuItemDeleteItem);

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
        inboundCreateDialog = new InboundDialog(null, "Inbound", true);
        outboundCreateDialog = new OutboundDialog(null, "Outbound", true);
        outboundScrapDialog = new OutboundScrapDialog(null, "Scrap", true);
        updateItemDialog = new ItemCreateUpdateDialog(null, "Update item", true);
    }

    public ItemCreateUpdateDialog getUpdateItemDialog() {
        return updateItemDialog;
    }

    protected JButton getBtnLoadMore() {
        return btnLoadMore;
    }

    public InboundDialog getInboundCreateDialog() {
        return this.inboundCreateDialog;
    }

    public OutboundDialog getOutboundCreateDialog() {
        return this.outboundCreateDialog;
    }

    public OutboundScrapDialog getOutboundScrapDialog() {
        return outboundScrapDialog;
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
        itemMeta.setBalance(new BigDecimal("0.00"));
        itemMeta.setQuantityUnit(item.getQuantityUnit());
        model.addItemMeta(itemMeta);
        rowIndex++;
    }

    @Override
    public void updated(Item updatedItem) {
        table.setValueAt(updatedItem.getName(), tableRow, 1);
        table.setValueAt(updatedItem.getSpecification(), tableRow, 2);
        // 3 is calculated value not relevant on the update here
        table.setValueAt(updatedItem.getQuantityUnit(), tableRow, 4);
        nameAndSpecDisplayFields.setTfItemNameText(updatedItem.getName());
        nameAndSpecDisplayFields.setTfItemSpecificationsText(updatedItem.getSpecification());
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
        btnLoadMore.setEnabled(!(ResultLimitSizePreference.getResultLimitSize() >= searchResultTotalRowsCount));
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

    public void notifySelectedRowHasBeenDeleted(int rowId) {
        this.rowIdSelectionListeners.forEach((item) -> {
            item.selectedRowHasBeenDeleted(rowId);
        });
    }

    /**
     * React on Inbound create.
     *
     * @param inbound
     */
    @Override
    public void created(Inbound inbound) {
        BigDecimal quantity = (BigDecimal) table.getValueAt(tableRow, 3);
        quantity = quantity.add(inbound.getQuantity());
        table.setValueAt(quantity, tableRow, 3);
    }

    /**
     * React on Inbound update.
     *
     * @param inbound
     * @param oldQuantity
     */
    @Override
    public void updated(Inbound inbound, BigDecimal oldQuantity) {
        ItemMeta itemMeta = model.getItemMetaById(inbound.getItem().getId());
        if (itemMeta != null) {
            BigDecimal quantityDifference = inbound.getQuantity().subtract(oldQuantity);
            int compare = quantityDifference.compareTo(BigDecimal.ZERO);
            BigDecimal updatedBalance = itemMeta.getBalance();
            switch (compare) {
                case 1 -> {
                    updatedBalance = updatedBalance.add(quantityDifference.abs());
                    itemMeta.setBalance(updatedBalance);
                }
                case -1 -> {
                    updatedBalance = updatedBalance.subtract(quantityDifference.abs());
                    itemMeta.setBalance(updatedBalance);
                }
            }
        }
    }

    /**
     * React on Outbound create.
     *
     * @param outbound
     */
    @Override
    public void created(Outbound outbound) {
        BigDecimal quantity = (BigDecimal) table.getValueAt(tableRow, 3);
        quantity = quantity.subtract(outbound.getQuantity());
        table.setValueAt(quantity, tableRow, 3);
    }

    /**
     * React on Outbound update.
     *
     * @param outbound
     * @param oldQuantity
     */
    @Override
    public void updated(Outbound outbound, BigDecimal oldQuantity) {
        ItemMeta itemMeta = model.getItemMetaById(outbound.getItem().getId());
        if (itemMeta != null) {
            BigDecimal quantityDifference = outbound.getQuantity().subtract(oldQuantity);
            int compare = quantityDifference.compareTo(BigDecimal.ZERO);
            BigDecimal updatedBalance = itemMeta.getBalance();
            switch (compare) {
                case 1 -> {
                    updatedBalance = updatedBalance.subtract(quantityDifference.abs());
                    itemMeta.setBalance(updatedBalance);
                }
                case -1 -> {
                    updatedBalance = updatedBalance.add(quantityDifference.abs());
                    itemMeta.setBalance(updatedBalance);
                }
            }
        }
    }

    public void returnableReturn(Outbound outbound, BigDecimal oldQuantity) {
        System.out.println("returnableReturn");
    }

    @Override
    public void deleted(Inbound inbound) {
        ItemMeta itemMeta = model.getItemMetaById(inbound.getItem().getId());
        if (itemMeta != null) {
            BigDecimal updatedBalance = itemMeta.getBalance().subtract(inbound.getQuantity());
            itemMeta.setBalance(updatedBalance);
        }
    }

    @Override
    public void deleted(Outbound outbound) {
        ItemMeta itemMeta = model.getItemMetaById(outbound.getItem().getId());
        if (itemMeta != null) {
            BigDecimal updatedBalance = itemMeta.getBalance().add(outbound.getQuantity());
            itemMeta.setBalance(updatedBalance);
        }
    }

    @Override
    public void listableUpdated(Listable listable, String oldlistableName) {
        String dbEntityName = listable.getDBEntityName();
        Integer column = null;
        if (dbEntityName.equals("quantity_unit")) {
            column = 4;
        }
        if (column != null && column == 4) {
            for (int row = 0; row < model.getRowCount(); row++) {
                String quantityUnit = (String) table.getValueAt(row, column);
                if (quantityUnit.equals(oldlistableName)) {
                    table.setValueAt(listable, row, column);
                }
            }
        }
    }

    private void setEnabledMenuList(boolean enable) {
        menuItemInboundsOfSelectedItem.setEnabled(enable);
        menuItemOutboundOfSelectedItem.setEnabled(enable);
        menuItemOutboundScrapOfSelectedItem.setEnabled(enable);
        menuItemUpdateItem.setEnabled(enable);
        menuItemDeleteItem.setEnabled(enable);
    }

    private class RowSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int viewRow = table.getSelectedRow();
                if (viewRow > -1) {
                    tableRow = table.getSelectedRow();
                    int itemNameColumnIndex = 1;
                    int itemSpecificationColumnIndex = 2;
                    selectedModelRow = table.convertRowIndexToModel(viewRow);
                    String itemNameObject = (String) table.getModel().getValueAt(selectedModelRow, itemNameColumnIndex);
                    String itemSpecificationObject = (String) table.getModel().getValueAt(selectedModelRow, itemSpecificationColumnIndex);
                    nameAndSpecDisplayFields.setTfItemNameText(itemNameObject);
                    nameAndSpecDisplayFields.setTfItemSpecificationsText(itemSpecificationObject);
                }
            } else {
                DefaultListSelectionModel selectionModel = (DefaultListSelectionModel) e.getSource();
                if (selectionModel.isSelectionEmpty() && !e.getValueIsAdjusting()) {
                    nameAndSpecDisplayFields.setTfItemNameText("");
                    nameAndSpecDisplayFields.setTfItemSpecificationsText("");
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
                notifySelectedRowId(itemId);
            }
        }
    }

    private class RowMouseRightClickHandler implements PopupMenuListener {

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

            SwingUtilities.invokeLater(() -> {
                int rowAtPoint = table.rowAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), table));
                setEnabledMenuList(rowAtPoint > -1);
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
            ItemMeta itemMeta = model.getItemMeta(selectedModelRow);
            Object source = e.getSource();
            if (source == menuItemInboundsOfSelectedItem) {
                inboundCreateDialog.setItemMeta(itemMeta);
                inboundCreateDialog.setVisible(true);
            } else if (source == menuItemOutboundOfSelectedItem) {
                outboundCreateDialog.setItemMeta(itemMeta);
                outboundCreateDialog.setVisible(true);
            } else if (source == menuItemOutboundScrapOfSelectedItem) {
                outboundScrapDialog.setItemMeta(itemMeta);
                outboundScrapDialog.setVisible(true);
            } else if (source == menuItemUpdateItem) {
                updateItemDialog.setTfName(itemMeta.getName());
                updateItemDialog.setTfSpecs(itemMeta.getSpecification());
                QuantityUnit quantityUnit = new QuantityUnit();
                quantityUnit.setId(itemMeta.getQuantityUnit().getId());
                quantityUnit.setName(itemMeta.getQuantityUnit().getName());
                updateItemDialog.setItemIdForUpdate(itemMeta.getId());
                updateItemDialog.setUnitName(quantityUnit);
                updateItemDialog.setItemImages(itemMeta.getId());
                updateItemDialog.setVisible(true);
            } else if (source == menuItemDeleteItem) {
                boolean isInUse = CRUDItems.isInUse(itemMeta);
                if (isInUse) {
                    JOptionPane.showMessageDialog(null,
                            "Item cannot be deleted because it is in use.",
                            "In use!",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    int reply = JOptionPane.showConfirmDialog(
                            null,
                            "Are you sure to delete this item?",
                            "DELETE!",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (reply == JOptionPane.YES_OPTION) {
                        List<Image> relatedImagesToBeRemoved = CRUDImages.getImagesByItemId(itemMeta.getId());
                        CRUDImages.deleteByItem(itemMeta);
                        boolean deleted = CRUDItems.delete(itemMeta);
                        if (deleted) {
                            model.removeItemMeta(selectedModelRow);
                            relatedImagesToBeRemoved.forEach(imageTobeRemoved
                                    -> ImageFileManager.delete(imageTobeRemoved.getImageName()));
                            notifySelectedRowHasBeenDeleted(itemMeta.getId());
                            nameAndSpecDisplayFields.setTfItemNameText("");
                            nameAndSpecDisplayFields.setTfItemSpecificationsText("");
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Item has been deleted successfully",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Issue: item was not deleted",
                                    "Failure",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }
        }
    }

}
