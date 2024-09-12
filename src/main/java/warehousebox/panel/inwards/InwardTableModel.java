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
package warehousebox.panel.inwards;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import warehousebox.db.model.Inward;
import warehousebox.db.model.Item;
import warehousebox.db.model.QuantityUnit;
import warehousebox.db.model.Source;

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

    private List<Inward> inwards;

    public InwardTableModel() {
        inwards = new ArrayList<>();
    }

    public InwardTableModel(List<Inward> inwards) {
        this.inwards = inwards;
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
        return inwards.size();
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
        Inward inward = getInward(row);

        return switch (column) {
            case 0 ->
                inward.getId();
            case 1 ->
                inward.getItem().getId();
            case 2 ->
                inward.getQuantity();
            case 3 ->
                inward.getItem().getQuantityUnit().getName();
            case 4 ->
                inward.getSource().getName();
            case 5 ->
                inward.getDate();
            case 6 ->
                inward.getItem().getName();
            case 7 ->
                inward.getItem().getSpecification();
            default ->
                null;
        };
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        Inward inward = getInward(row);

        switch (column) {
            case 0 ->
                inward.setId((int) value);
            case 1 ->
                inward.setItem((Item) value); // Item Id
            case 2 ->
                inward.setQuantity((BigDecimal) value);
            case 3 ->
                inward.setItem((Item) value); // Item Quantity unit
            case 4 ->
                inward.setSource((Source) value);
            case 5 ->
                inward.setDate((LocalDate) value);
            case 6 ->
                inward.setItem((Item) value); // Item Name
            case 7 ->
                inward.setItem((Item) value); // Item Specification
        }
        fireTableCellUpdated(row, column);
    }

    public Inward getInward(int row) {
        return inwards.get(row);
    }

    public void addInward(Inward inward) {
        insertInward(getRowCount(), inward);
    }

    public void insertInward(int row, Inward inward) {
        inwards.add(row, inward);
        fireTableRowsInserted(row, row);
    }

    public void removeInward(int row) {
        inwards.remove(row);
        fireTableRowsDeleted(row, row);
    }

}
