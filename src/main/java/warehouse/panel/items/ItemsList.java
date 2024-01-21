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

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import warehouse.db.CRUDItems;
import warehouse.db.CRUDQuantityUnit;
import warehouse.db.CreateListener;
import warehouse.db.model.Item;

/**
 *
 * @author Saleh
 */
public class ItemsList extends JPanel implements CreateListener {

    private DefaultTableModel model;
    private JTable table;
    private JScrollPane scrollTable;

    public ItemsList() {

        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"Code", "Name", "Specification", "Unit"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Disable cells editing.
                return false;
            }
        };
        table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.BOLD, 14));
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(1);
        table.getColumnModel().getColumn(1).setPreferredWidth(1);
        table.getColumnModel().getColumn(2).setPreferredWidth(1);
        table.getColumnModel().getColumn(3).setPreferredWidth(1);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        scrollTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollTable, BorderLayout.CENTER);
    }

    @Override
    public void created() {
        System.out.println("Refresh items to reflect newly created item");
        // Clear the model every time, to append fresh results
        // and not accumulate on previous results
        model.setRowCount(0);
        List<Item> itemsRecords = new ArrayList();
        itemsRecords = CRUDItems.getAll();
        Object[] modelRow = new Object[5];

        int size = itemsRecords.size();
        for (int i = 0; i < size; i++) {
            Item item = itemsRecords.get(i);
            modelRow[0] = item.getId(); //code
            modelRow[1] = item.getName();
            modelRow[2] = item.getSpecification();
            modelRow[3] = CRUDQuantityUnit.getById(item.getUnit()).getUnit();
            model.addRow(modelRow);
        }
    }
}
