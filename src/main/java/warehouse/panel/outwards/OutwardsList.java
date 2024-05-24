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
import warehouse.db.CRUDOutwards;
import warehouse.db.model.Outward;
import warehouse.singularlisting.Listable;
import warehouse.singularlisting.ListableConsumer;

/**
 *
 * @author Saleh
 */
public class OutwardsList extends JPanel
        implements
        OutwardCRUDListener,
        ListableConsumer,
        ItemsSearchListener {

    private OutwardTableModel model;
    private JTable table;
    private JScrollPane scrollTable;
    private Integer selectedModelRow;
    private ArrayList<RowIdSelectionListener> rowIdSelectionListeners;
    private List<OutwardDeleteListener> outwardDeleteListeners;
    private Listable listableImplementation;
    private final JPopupMenu popupMenu;
    private final JMenuItem menuOutwardEdit,
            menuOutwardDelete;
    private JButton btnLoadMore;
    private int searchResultTotalRowsCount,
            incrementedReturnedRowsCount,
            rowIndex,
            tableRow;
    private NameAndSpecDisplayFields nameAndSpecDisplayFields;
    private OutwardDialog outwarEditdDialog;

    public OutwardsList() {

        setLayout(new BorderLayout());
        rowIdSelectionListeners = new ArrayList<>();
        outwardDeleteListeners = new ArrayList<>();
        model = new OutwardTableModel();

        popupMenu = new JPopupMenu();
        popupMenu.addPopupMenuListener(new RowMouseRightClickHandler());
        menuOutwardEdit = new JMenuItem("Edit...");
        menuOutwardEdit.addActionListener(new PopupMenuItemActionHandler());
        popupMenu.add(menuOutwardEdit);

        menuOutwardDelete = new JMenuItem("Delete");
        menuOutwardDelete.addActionListener(new PopupMenuItemActionHandler());
        popupMenu.add(new JSeparator());
        popupMenu.add(menuOutwardDelete);

        table = new JTable(model);
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
        table.removeColumn(table.getColumnModel().getColumn(8));
        table.removeColumn(table.getColumnModel().getColumn(7));
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

        outwarEditdDialog = new OutwardDialog(null, "Update Outward", true);

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

    protected void setnameAndSpecDisplayFields(NameAndSpecDisplayFields nameAndSpecDisplayFields) {
        this.nameAndSpecDisplayFields = nameAndSpecDisplayFields;
    }

    @Override
    public void created(Outward outward) {
        model.addOutward(outward);
    }

    @Override
    public void updated(Outward outward, BigDecimal oldQuantity) {
        table.setValueAt(outward.getQuantity(), tableRow, 2);
        table.setValueAt(outward.getRecipient(), tableRow, 4);
        table.setValueAt(outward.getUsedFor(), tableRow, 5);
        table.setValueAt(outward.getDate(), tableRow, 6);
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
            model = new OutwardTableModel();
            table.setModel(model);
            table.removeColumn(table.getColumnModel().getColumn(8));
            table.removeColumn(table.getColumnModel().getColumn(7));
            incrementedReturnedRowsCount = 0;
        }
    }

    @Override
    public void notifySearchResultTotalRowsCount(int searchResultTotalRowsCount) {
        this.searchResultTotalRowsCount = searchResultTotalRowsCount;
        btnLoadMore.setEnabled(!(ItemsSearchLogic.getResultsPageLimit() >= searchResultTotalRowsCount));
    }

    @Override
    public void notifySearchResult(List<Outward> outwards) {
        /**
         * To organize order after new item insert. After creating new items,
         * new rows added to the model at run time to reflect newly created
         * items. However these rows are off order. So here we remove them, so
         * that they will be fetched through the following fetches or via search
         * requests.
         */
        if (model.getRowCount() > 0) {
            for (int i = incrementedReturnedRowsCount + 1; i <= rowIndex; i++) {
                model.removeOutward(incrementedReturnedRowsCount);
            }
        }

        List<Outward> outwardsMetaRecords = outwards;

        int size = outwardsMetaRecords.size();
        incrementedReturnedRowsCount += size;
        rowIndex = incrementedReturnedRowsCount;
        for (int i = 0; i < size; i++) {
            Outward outward = outwardsMetaRecords.get(i);
            model.addOutward(outward);
        }
        btnLoadMore.setEnabled(!(incrementedReturnedRowsCount >= searchResultTotalRowsCount));
    }

    public void addOutwardDeleteListener(OutwardDeleteListener outwardDeleteListener) {
        this.outwardDeleteListeners.add(outwardDeleteListener);
    }

    public void notifyOutwardDeleted(Outward outward) {
        this.outwardDeleteListeners.forEach((outwardDeleteListener) -> {
            outwardDeleteListener.deleted(outward);
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
                    int viewRow = table.getSelectedRow();
                    if (viewRow > -1) {
                        int itemNameColumnIndex = 7;
                        int itemSpecificationColumnIndex = 8;
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
            Outward outward = model.getOutward(modelIndex);
            JMenuItem source = (JMenuItem) e.getSource();
            if (source == menuOutwardEdit) {
                outwarEditdDialog.setOutwardToFormFields(outward);
                outwarEditdDialog.setVisible(true);
            } else if (source == menuOutwardDelete) {
                int reply = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure to delete this record?",
                        "DELETE!",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (reply == JOptionPane.YES_OPTION) {
                    boolean deleted = CRUDOutwards.delete(outward);
                    if (deleted) {
                        model.removeOutward(modelIndex);
                        notifyOutwardDeleted(outward);
                        JOptionPane.showMessageDialog(
                                null,
                                "Outward deleted successfully",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(
                                null,
                                "Issue: Outward was not deleted",
                                "Failure",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }
    }

}
