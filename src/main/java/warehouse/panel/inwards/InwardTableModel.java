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
package warehouse.panel.inwards;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import warehouse.db.model.InwardMeta;
import warehouse.db.model.Item;
import warehouse.db.model.QuantityUnit;
import warehouse.db.model.Source;

/**
 *
 * @author Saleh
 */
public class InwardTableModel extends AbstractTableModel {

    private String[] columnNames
            = {
                "ID",
                "Item ID",
                "Qty",
                "Unit",
                "Source",
                "Date",
                "Name",
                "Specification"
            };

    private List<InwardMeta> inwardsMeta;

    public InwardTableModel() {
        inwardsMeta = new ArrayList<>();
    }

    public InwardTableModel(List<InwardMeta> itemMeta) {
        this.inwardsMeta = itemMeta;
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
        return inwardsMeta.size();
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 3 ->
                QuantityUnit.class;
            case 4 ->
                Source.class;
            case 5 ->
                LocalDate.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int column) {
        InwardMeta inwardMeta = getInwardMeta(row);

        return switch (column) {
            case 0 ->
                inwardMeta.getInwardId();
            case 1 ->
                inwardMeta.getItem().getId();
            case 2 ->
                inwardMeta.getQuantity();
            case 3 ->
                inwardMeta.getItem().getQuantityUnit().getName();
            case 4 ->
                inwardMeta.getSource().getName();
            case 5 ->
                inwardMeta.getDate();
            case 6 ->
                inwardMeta.getItem().getName();
            case 7 ->
                inwardMeta.getItem().getSpecification();
            default ->
                null;
        };
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        InwardMeta inwardMeta = getInwardMeta(row);

        switch (column) {
            case 0 ->
                inwardMeta.setInwardId((int) value);
            case 1 ->
                inwardMeta.setItem((Item) value); // Item Id
            case 2 ->
                inwardMeta.setQuantity((BigDecimal) value);
            case 3 ->
                inwardMeta.setItem((Item) value); // Item Quantity unit
            case 4 ->
                inwardMeta.setSource((Source) value);
            case 5 ->
                inwardMeta.setDate((LocalDate) value);
            case 6 ->
                inwardMeta.setItem((Item) value); // Item Name
            case 7 ->
                inwardMeta.setItem((Item) value); // Item Specification
        }
        fireTableCellUpdated(row, column);
    }

    public InwardMeta getInwardMeta(int row) {
        return inwardsMeta.get(row);
    }

    public void addInwardMeta(InwardMeta inwardMeta) {
        insertInwardMeta(getRowCount(), inwardMeta);
    }

    public void insertInwardMeta(int row, InwardMeta inwardMeta) {
        inwardsMeta.add(row, inwardMeta);
        fireTableRowsInserted(row, row);
    }

    public void removeInwardMeta(int row) {
        inwardsMeta.remove(row);
        fireTableRowsDeleted(row, row);
    }

}
