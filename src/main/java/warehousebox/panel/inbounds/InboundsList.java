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
package warehousebox.panel.inbounds;

import warehousebox.panel.items.RowIdSelectionListener;
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
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import warehousebox.db.CRUDInbounds;
import warehousebox.db.model.Inbound;
import warehousebox.db.model.Item;
import warehousebox.db.model.QuantityUnit;
import warehousebox.panel.menus.ListableUpdateListener;
import warehousebox.panel.menus.ResultLimitSizePreference;
import warehousebox.utility.singularlisting.Listable;
import warehousebox.utility.singularlisting.ListableConsumer;

/**
 *
 * @author Saleh
 */
public class InboundsList extends JPanel
        implements
        InboundCRUDListener,
        ListableConsumer,
        ItemsSearchListener,
        ListableUpdateListener {

    private InboundTableModel model;
    private JTable table;
    private JScrollPane scrollTable;
    private Integer selectedModelRow;
    private ArrayList<RowIdSelectionListener> rowIdSelectionListeners;
    private List<InboundDeleteListener> inboundDeleteListeners;
    private Listable listableImplementation;
    private final JPopupMenu popupMenu;
    private final JMenuItem menuInboundEdit,
            menuInboundDelete;
    private JButton btnLoadMore;
    private int searchResultTotalRowsCount,
            incrementedReturnedRowsCount,
            rowIndex,
            tableRow;
    private RowAttributesDisplay rowAttributesDisplay;
    private InboundDialog inboundEditDialog;

    public InboundsList() {

        setLayout(new BorderLayout());
        rowIdSelectionListeners = new ArrayList<>();
        inboundDeleteListeners = new ArrayList<>();
        model = new InboundTableModel();

        popupMenu = new JPopupMenu();
        popupMenu.addPopupMenuListener(new RowMouseRightClickHandler());
        menuInboundEdit = new JMenuItem("Edit...");
        menuInboundEdit.addActionListener(new PopupMenuItemActionHandler());
        popupMenu.add(menuInboundEdit);

        menuInboundDelete = new JMenuItem("Delete");
        menuInboundDelete.addActionListener(new PopupMenuItemActionHandler());
        popupMenu.add(new JSeparator());
        popupMenu.add(menuInboundDelete);

        table = new JTable(model);
        table.addMouseListener(new ItemRowDoubleClickHandler());
        table.getSelectionModel().addListSelectionListener(new RowSelectionListener());
        table.setFont(new Font("SansSerif", Font.BOLD, 14));
        table.setFillsViewportHeight(true);
        /**
         * The order of columns removal is important. The columns are 0-based
         * indexes. To remove the last column from a table of 8 columns you need
         * to remove the column at the index (7). After that removal, the model
         * has 7 columns remaining, so to remove the last column again, you need
         * to remove the column at the index (6).
         */
        table.removeColumn(table.getColumnModel().getColumn(7));
        table.removeColumn(table.getColumnModel().getColumn(6));
        table.getColumnModel().getColumn(0).setPreferredWidth(2);
        table.getColumnModel().getColumn(1).setPreferredWidth(2);
        table.getColumnModel().getColumn(2).setPreferredWidth(2);
        table.getColumnModel().getColumn(3).setPreferredWidth(1);
        table.getColumnModel().getColumn(4).setPreferredWidth(2);
        table.getColumnModel().getColumn(5).setPreferredWidth(2);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setComponentPopupMenu(popupMenu);
        scrollTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollTable, BorderLayout.CENTER);

        inboundEditDialog = new InboundDialog(null, "Update Inbound", true);

        btnLoadMore = new JButton("Load more");
        btnLoadMore.setEnabled(false);
        add(btnLoadMore, BorderLayout.PAGE_END);
    }

    @Override
    public void setListableImpl(Listable listable) {
        this.listableImplementation = listable;
    }

    protected JButton getBtnLoadMore() {
        return btnLoadMore;
    }

    protected void setnameAndSpecDisplayFields(RowAttributesDisplay rowAttributesDisplay) {
        this.rowAttributesDisplay = rowAttributesDisplay;
    }

    @Override
    public void created(Inbound inbound) {
        // Not necessary and it causes visual duplicate
        //  model.addInbound(inbound);
    }

    @Override
    public void updated(Inbound inbound, BigDecimal oldQuantity) {
        table.setValueAt(inbound.getQuantity(), tableRow, 2);
        table.setValueAt(inbound.getSource(), tableRow, 4);
        table.setValueAt(inbound.getDate(), tableRow, 5);
        rowAttributesDisplay.setTfItemSourceText(inbound.getSource().getName());
    }

    public void addRowIdSelectionListener(RowIdSelectionListener var) {
        this.rowIdSelectionListeners.add(var);
    }

    public void notifySelectedRowId(int rowId) {
        this.rowIdSelectionListeners.forEach((item) -> {
            item.selectedRowId(rowId);
        });
    }

    @Override
    public void notifyOFFSET(int OFFSET) {
        if (OFFSET == 0) {
            model = new InboundTableModel();
            table.setModel(model);
            table.removeColumn(table.getColumnModel().getColumn(7));
            table.removeColumn(table.getColumnModel().getColumn(6));
            incrementedReturnedRowsCount = 0;
        }
    }

    @Override
    public void notifySearchResultTotalRowsCount(int searchResultTotalRowsCount) {
        this.searchResultTotalRowsCount = searchResultTotalRowsCount;
        btnLoadMore.setEnabled(!(ResultLimitSizePreference.getResultLimitSize() >= searchResultTotalRowsCount));
    }

    @Override
    public void notifySearchResult(List<Inbound> inbounds) {
        /**
         * To organize order after new item insert. After creating new items,
         * new rows added to the model at run time to reflect newly created
         * items. However these rows are off order. So here we remove them, so
         * that they will be fetched through the following fetches or via search
         * requests.
         */
        if (model.getRowCount() > 0) {
            for (int i = incrementedReturnedRowsCount + 1; i <= rowIndex; i++) {
                model.removeInbound(incrementedReturnedRowsCount);
            }
        }

        List<Inbound> inboundsRecords = inbounds;
        Object[] modelRow = new Object[8];

        int size = inboundsRecords.size();
        incrementedReturnedRowsCount += size;
        rowIndex = incrementedReturnedRowsCount;
        for (int i = 0; i < size; i++) {
            Inbound inbound = inboundsRecords.get(i);
            model.addInbound(inbound);
        }
        btnLoadMore.setEnabled(!(incrementedReturnedRowsCount >= searchResultTotalRowsCount));
    }

    public void addInboundDeleteListener(InboundDeleteListener inboundDeleteListener) {
        this.inboundDeleteListeners.add(inboundDeleteListener);
    }

    public void notifyInboundDeleted(Inbound inbound) {
        this.inboundDeleteListeners.forEach((inboundDeleteListener) -> {
            inboundDeleteListener.deleted(inbound);
        });
    }

    @Override
    public void listableUpdated(Listable listable, String oldlistableName) {
        String dbEntityName = listable.getDBEntityName();
        Integer column = null;
        if (dbEntityName.equals("quantity_unit")) {
            column = 3;
        } else if (dbEntityName.equals("source")) {
            column = 4;
        }
        if (column != null) {
            if (column == 3) {
                for (int row = 0; row < model.getRowCount(); row++) {
                    String quantityUnit = (String) table.getValueAt(row, column);
                    if (quantityUnit.equals(oldlistableName)) {
                        Item item = model.getInbound(row).getItem();
                        item.setQuantityUnit((QuantityUnit) listable);
                        table.setValueAt(item, row, column);
                    }
                }
            } else if (column == 4) {
                for (int row = 0; row < model.getRowCount(); row++) {
                    String source = (String) table.getValueAt(row, column);
                    if (source.equals(oldlistableName)) {
                        table.setValueAt(listable, row, column);
                    }
                }
            }
        }
    }

    private void setEnabledMenuList(boolean enable) {
        menuInboundEdit.setEnabled(enable);
        menuInboundDelete.setEnabled(enable);
    }

    private class RowSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                DefaultListSelectionModel selectionModel = (DefaultListSelectionModel) e.getSource();
                if (selectionModel.isSelectionEmpty()) {
                    // Table row de-selection occurred
                } else {
                    int viewRow = table.getSelectedRow();
                    if (viewRow > -1) {
                        int itemNameColumnIndex = 6;
                        int itemSpecificationColumnIndex = 7;
                        int itemSourceColumnIndex = 4;

                        selectedModelRow = table.convertRowIndexToModel(viewRow);

                        String itemNameObject = (String) table.getModel().getValueAt(selectedModelRow, itemNameColumnIndex);
                        rowAttributesDisplay.setTfItemNameText(itemNameObject);

                        String itemSpecificationObject = (String) table.getModel().getValueAt(selectedModelRow, itemSpecificationColumnIndex);
                        rowAttributesDisplay.setTfItemSpecificationsText(itemSpecificationObject);

                        String itemSourceObject = (String) table.getModel().getValueAt(selectedModelRow, itemSourceColumnIndex);
                        rowAttributesDisplay.setTfItemSourceText(itemSourceObject);
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
            tableRow = table.getSelectedRow();
            int modelIndex = table.convertRowIndexToModel(tableRow);
            Inbound inbound = model.getInbound(modelIndex);

            JMenuItem source = (JMenuItem) e.getSource();
            if (source == menuInboundEdit) {
                inboundEditDialog.setInboundToFormFields(inbound);
                inboundEditDialog.setVisible(true);
            } else if (source == menuInboundDelete) {
                int reply = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure to delete this record?",
                        "DELETE!",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (reply == JOptionPane.YES_OPTION) {
                    boolean deleted = CRUDInbounds.delete(inbound);
                    if (deleted) {
                        model.removeInbound(modelIndex);
                        notifyInboundDeleted(inbound);
                        rowAttributesDisplay.setTfItemNameText("");
                        rowAttributesDisplay.setTfItemSourceText("");
                        rowAttributesDisplay.setTfItemSpecificationsText("");
                        JOptionPane.showMessageDialog(
                                null,
                                "Inbound deleted successfully",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(
                                null,
                                "Issue: Inbound was not deleted",
                                "Failure",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }
    }

}