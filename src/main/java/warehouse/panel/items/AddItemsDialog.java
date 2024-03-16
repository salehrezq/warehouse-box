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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import utility.date.DateDeselectedListener;
import utility.date.DateListener;
import utility.date.DatePicker;
import warehouse.db.CRUDAddedItems;
import warehouse.db.model.AddedItems;
import warehouse.db.model.Source;

/**
 *
 * @author Saleh
 */
public class AddItemsDialog extends JDialog implements
        DateListener,
        DateDeselectedListener {

    private JPanel container;
    private JTextField tfQuantity;
    private FormFieldSource formFieldSource;
    private JLabel lbQuantity, lbQuantityUnit, lbSource, lbDate;
    private String itemUnit;
    private int itemId;
    private JButton btnSubmit;
    private DatePicker datePicker;
    private LocalDate selectedDate;

    public AddItemsDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        container = new JPanel();
        container.setLayout(new MigLayout("center center"));

        lbQuantityUnit = new JLabel();
        lbQuantity = new JLabel("Quantity");
        tfQuantity = new JTextField(5);

        formFieldSource = new FormFieldSource();
        formFieldSource.setListableImpl(new Source());

        lbDate = new JLabel("Date");
        datePicker = new DatePicker();
        this.setupDateField(datePicker);

        btnSubmit = new JButton("Submit addition");
        btnSubmit.addActionListener(new BtnSubmitHandler());

        container.add(lbQuantity);
        container.add(tfQuantity, "grow");
        container.add(lbQuantityUnit, "wrap");
        container.add(formFieldSource, "span 3,wrap");
        container.add(lbDate);
        container.add(datePicker.getDatePicker(), "span 2, wrap");
        container.add(btnSubmit, "span 3, center, gapy 10");
        add(container);
        pack();
    }

    private void setupDateField(DatePicker datePicker) {
        datePicker.setTodayAsDefault();
        datePicker.addDateListener(this);
        datePicker.addDateDeselectedListener(this);
        selectedDate = datePicker.getDate();
    }

    public void setItemUnit(String itemUnit) {
        this.itemUnit = itemUnit;
        lbQuantityUnit.setText(itemUnit);
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    @Override
    public void dateChanged(LocalDate date) {
        selectedDate = date;
    }

    @Override
    public void dateDeselected() {
        System.out.println("dateDeselected");
    }

    private class BtnSubmitHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            BigDecimal bigDecimal = new BigDecimal(tfQuantity.getText());
            AddedItems itemsAdd = new AddedItems();
            itemsAdd.setItemId(itemId);
            itemsAdd.setQuantity(bigDecimal);
            itemsAdd.setDate(selectedDate);
            Source source = (Source) formFieldSource.getSelectedValue();
            itemsAdd.setSourceId(source.getId());
            CRUDAddedItems.create(itemsAdd);
        }
    }
}
