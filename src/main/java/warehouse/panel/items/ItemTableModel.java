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
package warehouse.panel.items;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import warehouse.db.model.ItemMeta;
import warehouse.db.model.QuantityUnit;

/**
 *
 * @author Saleh
 */
public class ItemTableModel extends AbstractTableModel {

    private String[] columnNames
            = {
                "ID",
                "Name",
                "Specification",
                "Balance",
                "Unit"
            };

    private List<ItemMeta> itemsMeta;

    public ItemTableModel() {
        itemsMeta = new ArrayList<>();
    }

    public ItemTableModel(List<ItemMeta> itemMeta) {
        this.itemsMeta = itemMeta;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        return itemsMeta.size();
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 3:
                return BigDecimal.class;
            case 4:
                return QuantityUnit.class;
            default:
                return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int column) {
        ItemMeta itemMeta = getItemMeta(row);

        return switch (column) {
            case 0 ->
                itemMeta.getId();
            case 1 ->
                itemMeta.getName();
            case 2 ->
                itemMeta.getSpecification();
            case 3 ->
                itemMeta.getBalance();
            case 4 ->
                itemMeta.getQuantityUnit().getName();
            default ->
                null;
        };
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        ItemMeta itemMeta = getItemMeta(row);
        switch (column) {
            case 0 ->
                itemMeta.setId((int) value);
            case 1 ->
                itemMeta.setName((String) value);
            case 2 ->
                itemMeta.setSpecification((String) value);
            case 3 ->
                itemMeta.setBalance((BigDecimal) value);
            case 4 ->
                itemMeta.setQuantityUnit((QuantityUnit) value);
        }

        fireTableCellUpdated(row, column);
    }

    public ItemMeta getItemMeta(int row) {
        return itemsMeta.get(row);
    }

    public void addItemMeta(ItemMeta itemMeta) {
        insertItemMeta(getRowCount(), itemMeta);
    }

    public void insertItemMeta(int row, ItemMeta itemMeta) {
        itemsMeta.add(row, itemMeta);
        fireTableRowsInserted(row, row);
    }

    public void removeItemMeta(int row) {
        itemsMeta.remove(row);
        fireTableRowsDeleted(row, row);
    }

}
