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
package warehousebox.panel.outwards;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import warehousebox.db.model.Item;
import warehousebox.db.model.Outward;
import warehousebox.db.model.QuantityUnit;
import warehousebox.db.model.Recipient;

/**
 *
 * @author Saleh
 */
public class OutwardTableModel extends AbstractTableModel {

    private String[] columnNames
            = {
                "ID",
                "Item ID",
                "Qty",
                "Unit",
                "Recipient",
                "For",
                "Date",
                "Name",
                "Specification"
            };

    private List<Outward> outwards;

    public OutwardTableModel() {
        outwards = new ArrayList<>();
    }

    public OutwardTableModel(List<Outward> outwards) {
        this.outwards = outwards;
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
        return outwards.size();
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 1 ->
                Item.class;
            case 2 ->
                BigDecimal.class;
            case 3 ->
                QuantityUnit.class;
            case 4 ->
                Recipient.class;
            case 6 ->
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
        Outward inward = getOutward(row);

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
                inward.getRecipient().getName();
            case 5 ->
                inward.getUsedFor();
            case 6 ->
                inward.getDate();
            case 7 ->
                inward.getItem().getName();
            case 8 ->
                inward.getItem().getSpecification();
            default ->
                null;
        };
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        Outward outward = getOutward(row);

        switch (column) {
            case 0 ->
                outward.setId((int) value);
            case 1 ->
                outward.setItem((Item) value); // Item Id
            case 2 ->
                outward.setQuantity((BigDecimal) value);
            case 3 ->
                outward.setItem((Item) value); // Item Quantity unit
            case 4 ->
                outward.setRecipient((Recipient) value);
            case 5 ->
                outward.setUsedFor((String) value);
            case 6 ->
                outward.setDate((LocalDate) value); // 
            case 7 ->
                outward.setItem((Item) value); // Item Name
            case 8 ->
                outward.setItem((Item) value); // Item Specification
        }
        fireTableCellUpdated(row, column);
    }

    public Outward getOutward(int row) {
        return outwards.get(row);
    }

    public void addOutward(Outward inward) {
        insertOutward(getRowCount(), inward);
    }

    public void insertOutward(int row, Outward outward) {
        outwards.add(row, outward);
        fireTableRowsInserted(row, row);
    }

    public void removeOutward(int row) {
        outwards.remove(row);
        fireTableRowsDeleted(row, row);
    }

}
