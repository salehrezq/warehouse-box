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
package warehousebox.panel.outbounds;

import warehousebox.panel.items.RowIdSelectionListener;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import warehousebox.db.CRUDOutbounds;
import warehousebox.db.model.Item;
import warehousebox.db.model.Outbound;
import warehousebox.db.model.QuantityUnit;
import warehousebox.db.model.Recipient;
import warehousebox.panel.menus.ListableUpdateListener;
import warehousebox.panel.menus.ResultLimitSizePreference;
import warehousebox.utility.singularlisting.Listable;
import warehousebox.utility.singularlisting.ListableConsumer;

/**
 *
 * @author Saleh
 */
public class OutboundsList extends JPanel
        implements
        OutboundCRUDListener,
        ListableConsumer,
        ItemsSearchListener,
        ListableUpdateListener {

    private OutboundTableModel model;
    private JTable table;
    private JScrollPane scrollTable;
    private Integer selectedModelRow;
    private ArrayList<RowIdSelectionListener> rowIdSelectionListeners;
    private List<OutboundDeleteListener> outboundDeleteListeners;
    private List<RecipientIdOfOutboundSelectedRowListener> recipientIdOfOutboundSelectedRowListeners;
    private Listable listableImplementation;
    private final JPopupMenu popupMenu;
    private final JMenuItem menuOutboundEdit,
            menuOutboundDelete;
    private JButton btnLoadMore;
    private int searchResultTotalRowsCount,
            incrementedReturnedRowsCount,
            rowIndex,
            tableRow;
    private RowAttributesDisplay rowAttributesDisplay;
    private OutboundDialog outboundEditdDialog;
    private OutboundScrapDialog outboundScrapDialog;
    private Point mousePoint;

    public OutboundsList() {

        setLayout(new BorderLayout());
        rowIdSelectionListeners = new ArrayList<>();
        outboundDeleteListeners = new ArrayList<>();
        recipientIdOfOutboundSelectedRowListeners = new ArrayList<>();
        model = new OutboundTableModel();

        popupMenu = new JPopupMenu();
        popupMenu.addPopupMenuListener(new RowMouseRightClickHandler());
        menuOutboundEdit = new JMenuItem("Edit...");
        menuOutboundEdit.addActionListener(new PopupMenuItemActionHandler());
        popupMenu.add(menuOutboundEdit);

        menuOutboundDelete = new JMenuItem("Delete");
        menuOutboundDelete.addActionListener(new PopupMenuItemActionHandler());
        popupMenu.add(new JSeparator());
        popupMenu.add(menuOutboundDelete);

        table = new JTable(model);
        table.addMouseListener(new MousePointHandler());
        table.addMouseListener(new ItemRowDoubleClickHandler());
        table.getSelectionModel().addListSelectionListener(new RowSelectionListener());
        table.setFont(new Font("SansSerif", Font.BOLD, 14));
        table.setFillsViewportHeight(true);
        /**
         * The order of columns removal is important. The columns are 0-based
         * indexes. To remove the last column from a table of 9 columns you need
         * to remove the column at the index (8). After that removal, the model
         * has 8 columns remaining, so to remove the last column again, you need
         * to remove the column at the index (7).
         */
        table.removeColumn(table.getColumnModel().getColumn(9));
        table.removeColumn(table.getColumnModel().getColumn(8));
        table.removeColumn(table.getColumnModel().getColumn(6));
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

        InputMap inputMapJTable = table.getInputMap();
        inputMapJTable.put(KeyStroke.getKeyStroke(KeyEvent.VK_CONTEXT_MENU, 0), "popupmenu");

        ActionMap actionMapJTable = table.getActionMap();
        actionMapJTable.put("popupmenu", actionPopupMenu);

        outboundEditdDialog = new OutboundDialog(null, "Update Outbound", true);
        outboundScrapDialog = new OutboundScrapDialog(null, "Update Scrap Outbound", true);

        btnLoadMore = new JButton("Load more");
        btnLoadMore.setEnabled(false);
        add(btnLoadMore, BorderLayout.PAGE_END);
    }

    Action actionPopupMenu = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // empty action
        }
    };

    @Override
    public void setListableImpl(Listable listable) {
        this.listableImplementation = listable;
    }

    protected JButton getBtnLoadMore() {
        return btnLoadMore;
    }

    protected void setRowAttributesDisplay(RowAttributesDisplay rowAttributesDisplay) {
        this.rowAttributesDisplay = rowAttributesDisplay;
    }

    @Override
    public void created(Outbound outbound) {
        // Not necessary and it causes visual duplicate
        // model.addOutbound(outbound);
    }

    @Override
    public void updated(Outbound outbound, BigDecimal oldQuantity) {
        table.setValueAt(outbound.getQuantity(), tableRow, 2);
        table.setValueAt(outbound.getIssuanceType(), tableRow, 4);
        table.setValueAt(outbound.getRecipient(), tableRow, 5);
        table.setValueAt(outbound.getDate(), tableRow, 6);
        rowAttributesDisplay.setTfRecipientText(outbound.getRecipient().getName());
        rowAttributesDisplay.setTfNoteText(outbound.getNote());
    }

    public void returnableReturn(Outbound outbound, BigDecimal oldQuantity) {

    }

    public void addRowIdSelectionListener(RowIdSelectionListener var) {
        this.rowIdSelectionListeners.add(var);
    }

    public void notifySelectedRowId(int rowId) {
        this.rowIdSelectionListeners.forEach((item) -> {
            item.selectedRowId(rowId);
        });
    }

    private void notifySelectedRowHasBeenDeleted() {
        this.rowIdSelectionListeners.forEach((item) -> {
            item.selectedRowHasBeenDeleted();
        });
    }

    @Override
    public void resetTableRows() {
        model.removeAllElements();
        btnLoadMore.setEnabled(false);
        incrementedReturnedRowsCount = 0;
    }

    @Override
    public void notifySearchResultTotalRowsCount(int searchResultTotalRowsCount) {
        this.searchResultTotalRowsCount = searchResultTotalRowsCount;
        btnLoadMore.setEnabled(!(ResultLimitSizePreference.getResultLimitSize() >= searchResultTotalRowsCount));
    }

    @Override
    public void notifySearchResult(List<Outbound> outbounds) {
        /**
         * To organize order after new item insert. After creating new items,
         * new rows added to the model at run time to reflect newly created
         * items. However these rows are off order. So here we remove them, so
         * that they will be fetched through the following fetches or via search
         * requests.
         */
        if (model.getRowCount() > 0) {
            for (int i = incrementedReturnedRowsCount + 1; i <= rowIndex; i++) {
                model.removeOutbound(incrementedReturnedRowsCount);
            }
        }

        List<Outbound> outboundsMetaRecords = outbounds;

        int size = outboundsMetaRecords.size();
        incrementedReturnedRowsCount += size;
        rowIndex = incrementedReturnedRowsCount;
        for (int i = 0; i < size; i++) {
            Outbound outbound = outboundsMetaRecords.get(i);
            model.addOutbound(outbound);
        }
        btnLoadMore.setEnabled(!(incrementedReturnedRowsCount >= searchResultTotalRowsCount));
    }

    public void addOutboundDeleteListener(OutboundDeleteListener outboundDeleteListener) {
        this.outboundDeleteListeners.add(outboundDeleteListener);
    }

    public void notifyOutboundDeleted(Outbound outbound) {
        this.outboundDeleteListeners.forEach((outboundDeleteListener) -> {
            outboundDeleteListener.deleted(outbound);
        });
    }

    public void addRecipientIdOfOutboundSelectedRowListener(RecipientIdOfOutboundSelectedRowListener recipientIdOfOutboundSelectedRowListener) {
        this.recipientIdOfOutboundSelectedRowListeners.add(recipientIdOfOutboundSelectedRowListener);
    }

    public void notifySelectedRecipientIdOfOutboundRow(int id) {
        this.recipientIdOfOutboundSelectedRowListeners.forEach((recipientIdOfOutboundSelectedRowListener) -> {
            recipientIdOfOutboundSelectedRowListener.selectedRecipientIdOfOutboundRow(id);
        });
    }

    public void notifySelectedRecipientScrapperOfOutboundRow() {
        this.recipientIdOfOutboundSelectedRowListeners.forEach((recipientIdOfOutboundSelectedRowListener) -> {
            recipientIdOfOutboundSelectedRowListener.selectedRecipientScrapperOfOutboundRow();
        });
    }

    @Override
    public void listableUpdated(Listable listable, String oldlistableName) {
        String dbEntityName = listable.getDBEntityName();
        Integer column = null;
        if (dbEntityName.equals("quantity_unit")) {
            column = 3;
        } else if (dbEntityName.equals("recipients")) {
            column = 5;
        }
        if (column != null) {
            if (column == 3) {
                for (int row = 0; row < model.getRowCount(); row++) {
                    String quantityUnit = (String) table.getValueAt(row, column);
                    if (quantityUnit.equals(oldlistableName)) {
                        Item item = model.getOutbound(row).getItem();
                        item.setQuantityUnit((QuantityUnit) listable);
                        table.setValueAt(item, row, column);
                    }
                }
            } else if (column == 5) {
                for (int row = 0; row < model.getRowCount(); row++) {
                    String recipient = (String) table.getValueAt(row, column);
                    if (recipient.equals(oldlistableName)) {
                        table.setValueAt(listable, row, column);
                    }
                }
            }
        }
    }

    private void setEnabledMenuList(boolean enable) {
        menuOutboundEdit.setEnabled(enable);
        menuOutboundDelete.setEnabled(enable);
    }

    private class RowSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int viewRow = table.getSelectedRow();
                if (viewRow > -1) {
                    int itemNameColumnIndex = 8;
                    int itemSpecificationColumnIndex = 9;
                    int itemRecipientColumnIndex = 5;
                    int itemNoteColumnIndex = 6;

                    selectedModelRow = table.convertRowIndexToModel(viewRow);

                    String itemNameObject = (String) table.getModel().getValueAt(selectedModelRow, itemNameColumnIndex);
                    rowAttributesDisplay.setTfItemNameText(itemNameObject);

                    String itemSpecificationObject = (String) table.getModel().getValueAt(selectedModelRow, itemSpecificationColumnIndex);
                    rowAttributesDisplay.setTfItemSpecificationsText(itemSpecificationObject);

                    String itemRecipientObject = (String) table.getModel().getValueAt(selectedModelRow, itemRecipientColumnIndex);
                    rowAttributesDisplay.setTfRecipientText(itemRecipientObject);

                    String itemNoteObject = (String) table.getModel().getValueAt(selectedModelRow, itemNoteColumnIndex);
                    rowAttributesDisplay.setTfNoteText(itemNoteObject);
                }
            } else {
                DefaultListSelectionModel selectionModel = (DefaultListSelectionModel) e.getSource();
                if (selectionModel.isSelectionEmpty() && !e.getValueIsAdjusting()) {
                    rowAttributesDisplay.setTfItemNameText("");
                    rowAttributesDisplay.setTfItemSpecificationsText("");
                    rowAttributesDisplay.setTfNoteText("");
                    rowAttributesDisplay.setTfRecipientText("");
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
                notifySelectedRowId(itemId);

                Recipient recipient = model.getOutbound(selectedModelRow).getRecipient();
                if (recipient != null) {
                    if (recipient.getName().equals("Scrapper")) {
                        notifySelectedRecipientScrapperOfOutboundRow();
                    } else {
                        notifySelectedRecipientIdOfOutboundRow(recipient.getId());
                    }
                }
            }
        }
    }

    private class RowMouseRightClickHandler implements PopupMenuListener {

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            SwingUtilities.invokeLater(() -> {
                if (mousePoint == null) {
                    return;
                }
                int rowAtPoint = table.rowAtPoint(mousePoint);
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

    private class MousePointHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                int row = table.rowAtPoint(e.getPoint());
                setEnabledMenuList(row > -1);
                mousePoint = (row >= 0) ? e.getPoint() : null;
            }
        }
    }

    private class PopupMenuItemActionHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            tableRow = table.getSelectedRow();
            int modelIndex = table.convertRowIndexToModel(tableRow);
            Outbound outbound = model.getOutbound(modelIndex);
            JMenuItem source = (JMenuItem) e.getSource();
            if (source == menuOutboundEdit) {
                short issuanceType = outbound.getIssuanceType();
                switch (issuanceType) {
                    case (short) 1: // 1 Consumable
                    case (short) 2: // 2 Returnable
                        outboundEditdDialog.setOutboundToFormFields(outbound);
                        outboundEditdDialog.setVisible(true);
                        break;
                    case (short) 3: // 3 Scrap
                        outboundScrapDialog.setOutboundScrapToFormFields(outbound);
                        outboundScrapDialog.setVisible(true);
                        break;
                    default:
                        JOptionPane.showMessageDialog(
                                null,
                                "Issue: Unknown outbound type",
                                "Failure",
                                JOptionPane.WARNING_MESSAGE);
                        break;
                }
            } else if (source == menuOutboundDelete) {
                int reply = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure to delete this record?",
                        "DELETE!",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (reply == JOptionPane.YES_OPTION) {
                    boolean deleted = CRUDOutbounds.delete(outbound);
                    if (deleted) {
                        model.removeOutbound(modelIndex);
                        notifyOutboundDeleted(outbound);
                        notifySelectedRowHasBeenDeleted();
                        rowAttributesDisplay.setTfItemNameText("");
                        rowAttributesDisplay.setTfItemSpecificationsText("");
                        rowAttributesDisplay.setTfNoteText("");
                        rowAttributesDisplay.setTfRecipientText("");
                        JOptionPane.showMessageDialog(
                                null,
                                "Outbound deleted successfully",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(
                                null,
                                "Issue: Outbound was not deleted",
                                "Failure",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }
    }

}
