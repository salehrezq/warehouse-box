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
package warehousebox.panel.outbounds;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import warehousebox.db.model.Item;
import warehousebox.db.model.Outbound;
import warehousebox.db.model.QuantityUnit;
import warehousebox.db.model.Recipient;

/**
 *
 * @author Saleh
 */
public class OutboundTableModel extends AbstractTableModel {

    private String[] columnNames
            = {
                "ID",
                "Item ID",
                "Qty",
                "Unit",
                "Issuance type",
                "Recipient",
                "For",
                "Date",
                "Name",
                "Specification"
            };

    private List<Outbound> outbounds;

    public OutboundTableModel() {
        outbounds = new ArrayList<>();
    }

    public OutboundTableModel(List<Outbound> outbounds) {
        this.outbounds = outbounds;
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
        return outbounds.size();
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
            case 5 ->
                Recipient.class;
            case 7 ->
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
        Outbound inbound = getOutbound(row);

        return switch (column) {
            case 0 ->
                inbound.getId();
            case 1 ->
                inbound.getItem().getId();
            case 2 ->
                inbound.getQuantity();
            case 3 ->
                inbound.getItem().getQuantityUnit().getName();
            case 4 ->
                getIssuanceTypeDescriptiveValue(inbound.getIssuanceType());
            case 5 ->
                inbound.getRecipient().getName();
            case 6 ->
                inbound.getUsedFor();
            case 7 ->
                inbound.getDate();
            case 8 ->
                inbound.getItem().getName();
            case 9 ->
                inbound.getItem().getSpecification();
            default ->
                null;
        };
    }

    /**
     * Get descriptive text of the issuanceType numeric (short) value. This is
     * helpful so that we can update the description anytime.
     *
     * @return String
     */
    private String getIssuanceTypeDescriptiveValue(short issuanceType) {
        return switch (issuanceType) {
            case (short) 0 ->
                "Not set yet";
            case (short) 1 ->
                "Consumable";
            case (short) 2 ->
                "Returnable";
            case (short) 3 ->
                "Scrap";
            default ->
                "Unknown";
        };
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        Outbound outbound = getOutbound(row);

        switch (column) {
            case 0 ->
                outbound.setId((int) value);
            case 1 ->
                outbound.setItem((Item) value); // Item Id
            case 2 ->
                outbound.setQuantity((BigDecimal) value);
            case 3 ->
                outbound.setItem((Item) value); // Item Quantity unit
            case 4 ->
                outbound.setIssuanceType((short) value);
            case 5 ->
                outbound.setRecipient((Recipient) value);
            case 6 ->
                outbound.setUsedFor((String) value);
            case 7 ->
                outbound.setDate((LocalDate) value); // 
            case 8 ->
                outbound.setItem((Item) value); // Item Name
            case 9 ->
                outbound.setItem((Item) value); // Item Specification
        }
        fireTableCellUpdated(row, column);
    }

    public Outbound getOutbound(int row) {
        return outbounds.get(row);
    }

    public void addOutbound(Outbound inbound) {
        insertOutbound(getRowCount(), inbound);
    }

    public void insertOutbound(int row, Outbound outbound) {
        outbounds.add(row, outbound);
        fireTableRowsInserted(row, row);
    }

    public void removeOutbound(int row) {
        outbounds.remove(row);
        fireTableRowsDeleted(row, row);
    }

}
