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
package warehousebox.panel.inbounds;

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
import warehousebox.utility.singularlisting.ListableItemForm;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import warehousebox.db.CRUDInbounds;
import warehousebox.db.model.Inbound;
import warehousebox.db.model.ItemMeta;
import warehousebox.db.model.Source;

/**
 *
 * @author Saleh
 */
public class InboundDialog extends JDialog {

    private JPanel container;
    private JTextField tfQuantity;
    private ListableItemForm formFieldSource;
    private JLabel lbQuantity, lbQuantityUnit, lbBalance, lbDate;
    private int itemId;
    private JButton btnSubmit;
    private DatePicker datePicker;
    private DatePickerSettings datePickerSettings;
    private LocalDate selectedDate;
    private DateChangeHandler dateChangeHandler;
    private static ArrayList<InboundCRUDListener> inboundCRUDListeners;
    private ItemMeta itemMeta;
    private Inbound inbound;
    private QuantityValidateHandler quantityValidateHandler;
    private final Pattern pattern = Pattern.compile("(^[0-9]{1,}(\\.[0-9]{1,2})?)");
    private final Color colorError = new Color(255, 255, 0);

    public InboundDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        inboundCRUDListeners = new ArrayList<>();
        container = new JPanel();
        container.setLayout(new MigLayout("center center"));

        quantityValidateHandler = new QuantityValidateHandler();

        lbQuantityUnit = new JLabel();
        lbQuantity = new JLabel("Quantity");
        tfQuantity = new JTextField(5);
        tfQuantity.getDocument().addDocumentListener(quantityValidateHandler);

        lbBalance = new JLabel();

        formFieldSource = new ListableItemForm();
        formFieldSource.setLabelText("Source");
        formFieldSource.setListableImpl(new Source());
        formFieldSource.setListablePreferredSize(300, 300);

        lbDate = new JLabel("Date");
        datePicker = new DatePicker();
        dateChangeHandler = new DateChangeHandler();
        datePicker.addDateChangeListener(dateChangeHandler);
        this.setupDateField(datePicker);

        btnSubmit = new JButton("Submit addition");
        btnSubmit.setEnabled(false);
        btnSubmit.addActionListener(new BtnSubmitHandler());

        container.add(lbQuantity);
        container.add(tfQuantity, "grow");
        container.add(lbQuantityUnit);
        container.add(lbBalance, "wrap");
        container.add(formFieldSource, "span 4,wrap");
        container.add(lbDate);
        container.add(datePicker, "span 3, wrap");
        container.add(btnSubmit, "span 4, center, gapy 10");
        add(container);
        this.setMinimumSize(new Dimension(520, 520));
        this.addWindowListener(new ClosingWindowHandler());
    }

    private void setupDateField(DatePicker datePicker) {
        datePickerSettings = new DatePickerSettings();
        datePickerSettings.setAllowEmptyDates(false);
        datePickerSettings.setAllowKeyboardEditing(false);
        datePickerSettings.setLocale(Locale.ENGLISH);
        datePickerSettings.setFormatForDatesCommonEra("dd-MMM-uuuu");
        datePicker.setSettings(datePickerSettings);
        selectedDate = datePicker.getDate();
    }

    protected void setInboundToFormFields(Inbound inbound) {
        this.inbound = inbound;
        tfQuantity.setText(inbound.getQuantity().toPlainString());
        formFieldSource.setPreviewSelected(inbound.getSource());
        datePicker.setDate(inbound.getDate());
    }

    public void setItemMeta(ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
        lbQuantityUnit.setText(itemMeta.getQuantityUnit().getName());
        BigDecimal balance = itemMeta.getBalance();
        lbBalance.setText(" | Balance: " + balance.toPlainString());
    }

    public void addInboundCRUDListener(InboundCRUDListener inboundCRUDListener) {
        this.inboundCRUDListeners.add(inboundCRUDListener);
    }

    public void notifyCreated(Inbound inbound) {
        this.inboundCRUDListeners.forEach((inboundCRUDListener) -> {
            inboundCRUDListener.created(inbound);
        });
    }

    public void notifyUpdated(Inbound inbound, BigDecimal oldQuantity) {
        this.inboundCRUDListeners.forEach((inboundCRUDListener) -> {
            inboundCRUDListener.updated(inbound, oldQuantity);
        });
    }

    private class DateChangeHandler implements DateChangeListener {

        @Override
        public void dateChanged(DateChangeEvent event) {
            selectedDate = datePicker.getDate();
        }
    }

    private void resetFields() {
        tfQuantity.setText("");
        tfQuantity.setBackground(Color.WHITE);
        formFieldSource.resetFields();
        datePicker.setDateToToday();
    }

    private void tfQuantityValidateHandler() {
        if (pattern.matcher(tfQuantity.getText()).matches()) {
            tfQuantity.setBackground(Color.WHITE);
            btnSubmit.setEnabled(true);
        } else {
            tfQuantity.setBackground(colorError);
            btnSubmit.setEnabled(false);
        }
    }

    private class BtnSubmitHandler implements ActionListener {

        boolean isFieldsFilled;

        @Override
        public void actionPerformed(ActionEvent e) {
            isFieldsFilled = (!tfQuantity.getText().isBlank() && formFieldSource.getSelectedValue() != null);

            if (isFieldsFilled == false) {
                String message = "Missing fields:\n";
                if (tfQuantity.getText().isBlank()) {
                    message += "\n";
                    message += "- Quantity";
                }
                if (formFieldSource.getSelectedValue() == null) {
                    message += "\n";
                    message += "- Source";
                }

                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (inbound == null) {
                BigDecimal bigDecimal = new BigDecimal(tfQuantity.getText());
                Inbound inbound = new Inbound();
                inbound.setItem(itemMeta);
                inbound.setQuantity(bigDecimal);
                inbound.setDate(selectedDate);
                Source source = (Source) formFieldSource.getSelectedValue();
                inbound.setSource(source);
                Inbound inboundRetrieved = CRUDInbounds.create(inbound);
                notifyCreated(inboundRetrieved);
                if (inboundRetrieved != null) {
                    InboundDialog.this.dispose();
                    JOptionPane.showMessageDialog(
                            null,
                            "Inbound created successfully. You can find it on a next search",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            "Issue: Inbound was not added",
                            "Failure",
                            JOptionPane.WARNING_MESSAGE);
                }
            } else {
                BigDecimal oldQuantity = inbound.getQuantity();
                BigDecimal bigDecimal = new BigDecimal(tfQuantity.getText());
                inbound.setQuantity(bigDecimal);
                Source source = (Source) formFieldSource.getSelectedValue();
                inbound.setSource(source);
                inbound.setDate(selectedDate);
                boolean update = CRUDInbounds.update(inbound);
                if (update) {
                    InboundDialog.this.dispose();
                    JOptionPane.showMessageDialog(
                            null,
                            "Inbound updated successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    notifyUpdated(inbound, oldQuantity);
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            "Issue: Inbound was not updated",
                            "Failure",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
            resetFields();
        }
    }

    private class QuantityValidateHandler implements DocumentListener {

        private void validate(DocumentEvent docEvent) {
            if (docEvent.getDocument() == tfQuantity.getDocument()) {
                tfQuantityValidateHandler();
            }
        }

        @Override
        public void insertUpdate(DocumentEvent docEvent) {
            validate(docEvent);
        }

        @Override
        public void removeUpdate(DocumentEvent docEvent) {
            validate(docEvent);
        }

        @Override
        public void changedUpdate(DocumentEvent docEvent) {
            validate(docEvent);
        }
    }

    private class ClosingWindowHandler extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            resetFields();
        }
    }
}
