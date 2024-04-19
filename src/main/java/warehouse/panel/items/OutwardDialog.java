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
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import warehouse.singularlisting.SingularAttributedListForm;
import utility.date.DateDeselectedListener;
import utility.date.DateListener;
import utility.date.DatePicker;
import warehouse.db.CRUDOutwards;
import warehouse.db.model.ItemMeta;
import warehouse.db.model.Outward;
import warehouse.db.model.Recipient;
import warehouse.panel.outwards.OutwardCRUDListener;

/**
 *
 * @author Saleh
 */
public class OutwardDialog extends JDialog implements
        DateListener,
        DateDeselectedListener {

    private JPanel container;
    private JTextField tfQuantity, tfUsedFor;
    private SingularAttributedListForm formFieldRecipient;
    private JLabel lbQuantity, lbQuantityUnit, lbUsedFor, lbSource, lbDate;
    private JButton btnSubmit;
    private DatePicker datePicker;
    private LocalDate selectedDate;
    private ArrayList<OutwardCRUDListener> outwardCRUDListeners;
    private ItemMeta itemMeta;

    public OutwardDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        outwardCRUDListeners = new ArrayList<>();
        container = new JPanel();
        container.setLayout(new MigLayout("center center"));

        lbQuantityUnit = new JLabel();
        lbQuantity = new JLabel("Quantity");
        tfQuantity = new JTextField(5);

        lbUsedFor = new JLabel("Used for");
        tfUsedFor = new JTextField(15);

        formFieldRecipient = new SingularAttributedListForm();
        formFieldRecipient.setListableImpl(new Recipient());
        formFieldRecipient.setListDimentions(300, 300);

        lbDate = new JLabel("Date");
        datePicker = new DatePicker();
        this.setupDateField(datePicker);

        btnSubmit = new JButton("Submit addition");
        btnSubmit.addActionListener(new BtnSubmitHandler());

        container.add(lbQuantity);
        container.add(tfQuantity);
        container.add(lbQuantityUnit, "wrap");
        container.add(lbUsedFor);
        container.add(tfUsedFor, "grow, span 2, wrap");
        container.add(formFieldRecipient, "span 3,wrap");
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

    public void setItemMeta(ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
        lbQuantityUnit.setText(itemMeta.getUnit());
    }

    @Override
    public void dateChanged(LocalDate date) {
        selectedDate = date;
    }

    @Override
    public void dateDeselected() {
        System.out.println("dateDeselected");
    }

    public void addOutwardCRUDListener(OutwardCRUDListener outwardCRUDListener) {
        this.outwardCRUDListeners.add(outwardCRUDListener);
    }

    public void notifyCreated(Outward outward, ItemMeta relatedItemMeta) {
        this.outwardCRUDListeners.forEach((outwardCRUDListener) -> {
            outwardCRUDListener.created(outward, relatedItemMeta);
        });
    }

    private class BtnSubmitHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            BigDecimal quantity = new BigDecimal(tfQuantity.getText());
            Outward itemOutward = new Outward();
            itemOutward.setItemId(itemMeta.getId());
            itemOutward.setQuantity(quantity);
            itemOutward.setUsedFor(tfUsedFor.getText());
            itemOutward.setDate(selectedDate);
            Recipient recipient = (Recipient) formFieldRecipient.getSelectedValue();
            itemOutward.setRecipientId(recipient.getId());
            Outward outward = CRUDOutwards.create(itemOutward);
            if (outward != null) {
                notifyCreated(itemOutward, itemMeta);
                OutwardDialog.this.dispose();
                JOptionPane.showMessageDialog(
                        null,
                        "Outward added successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Issue: Outward was not added",
                        "Failure",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
