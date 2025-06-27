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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Pattern;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import warehousebox.db.CRUDOutbounds;
import warehousebox.db.model.ItemMeta;
import warehousebox.db.model.Outbound;
import warehousebox.db.model.Recipient;
import warehousebox.panel.menus.recipients.RecipientsImagePanel;

/**
 *
 * @author Saleh
 */
public class OutboundDialog extends JDialog {

    private JPanel container;
    private JTextField tfQuantity, tfNote;
    private ListableItemForm formFieldRecipient;
    private JLabel lbQuantity, lbQuantityUnit, lbBalance, lbNote, lbSource, lbDate;
    private JButton btnSubmit;
    private DatePicker datePicker;
    private DatePickerSettings datePickerSettings;
    private LocalDate selectedDate;
    private DateChangeHandler dateChangeHandler;
    private static ArrayList<OutboundCRUDListener> outboundCRUDListeners;
    private ItemMeta itemMeta;
    private Outbound outbound;
    private QuantityValidateHandler quantityValidateHandler;
    private final Pattern pattern = Pattern.compile("(^[0-9]{1,8}(\\.[0-9]{1,2})?)");
    private final Color colorError = new Color(255, 255, 0);
    private Vector issuanceTypeModel;
    private JComboBox comboIssuanceType;
    private final IssuanceTypeItem issuanceTypeNotSetYet = new IssuanceTypeItem((short) 0, "Issuance type...");
    private final IssuanceTypeItem issuanceTypeConsumable = new IssuanceTypeItem((short) 1, "Consumable");
    private final IssuanceTypeItem issuanceTypeReturnable = new IssuanceTypeItem((short) 2, "Returnable");
    private RecipientsImagePanel recipientsImagePanel;

    public OutboundDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        outboundCRUDListeners = new ArrayList<>();
        container = new JPanel();
        container.setLayout(new MigLayout("center center"));

        quantityValidateHandler = new QuantityValidateHandler();

        lbQuantityUnit = new JLabel();
        lbQuantity = new JLabel("Quantity");
        tfQuantity = new JTextField(5);
        tfQuantity.getDocument().addDocumentListener(quantityValidateHandler);

        lbBalance = new JLabel();

        lbNote = new JLabel("Used for");
        tfNote = new JTextField(15);

        formFieldRecipient = new ListableItemForm();
        formFieldRecipient.setLabelText("Recipient");
        formFieldRecipient.setListableImpl(new Recipient());
        formFieldRecipient.getlist().addMouseListener(new MouseJListHandler());

        issuanceTypeModel = new Vector();
        issuanceTypeModel.addElement(issuanceTypeNotSetYet);
        issuanceTypeModel.addElement(issuanceTypeConsumable);
        issuanceTypeModel.addElement(issuanceTypeReturnable);

        comboIssuanceType = new JComboBox(issuanceTypeModel);

        lbDate = new JLabel("Date");
        datePicker = new DatePicker();
        dateChangeHandler = new DateChangeHandler();
        datePicker.addDateChangeListener(dateChangeHandler);
        this.setupDateField(datePicker);

        recipientsImagePanel = new RecipientsImagePanel(64, 64);
        recipientsImagePanel.setImagePlaceholder();

        btnSubmit = new JButton("Submit");
        btnSubmit.setEnabled(false);
        btnSubmit.addActionListener(new BtnSubmitHandler());

        container.add(lbQuantity);
        container.add(tfQuantity, "grow");
        container.add(lbQuantityUnit);
        container.add(lbBalance, "wrap");
        container.add(lbNote);
        container.add(tfNote, "grow, span 3, wrap");
        container.add(formFieldRecipient, "span 4,wrap");
        container.add(comboIssuanceType, "span 2");
        container.add(lbDate, "span 2, split 2, align right");
        container.add(datePicker, "wrap");
        container.add(recipientsImagePanel.getContainer(), "span 4, center, wrap");
        container.add(btnSubmit, "span 4, center, gapy 5");
        add(container);
        this.setMinimumSize(new Dimension(520, 600));
        this.addWindowListener(new ClosingWindowHandler());
    }

    private void setupDateField(com.github.lgooddatepicker.components.DatePicker datePicker) {
        datePickerSettings = new DatePickerSettings();
        datePickerSettings.setAllowEmptyDates(false);
        datePickerSettings.setAllowKeyboardEditing(false);
        datePickerSettings.setLocale(Locale.ENGLISH);
        datePickerSettings.setFormatForDatesCommonEra("dd-MMM-uuuu");
        datePicker.setSettings(datePickerSettings);
        selectedDate = datePicker.getDate();
    }

    protected void setOutboundToFormFields(Outbound outbound) {
        this.outbound = outbound;
        tfQuantity.setText(outbound.getQuantity().toPlainString());
        formFieldRecipient.setPreviewSelected(outbound.getRecipient());
        tfNote.setText(outbound.getNote());
        comboIssuanceType.setSelectedItem(issuanceTypeModel.get(outbound.getIssuanceType()));
        datePicker.setDate(outbound.getDate());
    }

    public void setItemMeta(ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
        lbQuantityUnit.setText(itemMeta.getQuantityUnit().getName());
        BigDecimal balance = itemMeta.getBalance();
        lbBalance.setText(" | Balance: " + balance.toPlainString());
    }

    public void addOutboundCRUDListener(OutboundCRUDListener outboundCRUDListener) {
        outboundCRUDListeners.add(outboundCRUDListener);
    }

    public void notifyCreated(Outbound outbound) {
        outboundCRUDListeners.forEach((outboundCRUDListener) -> {
            outboundCRUDListener.created(outbound);
        });
    }

    public void notifyUpdated(Outbound outbound, BigDecimal oldQuantity) {
        outboundCRUDListeners.forEach((outboundCRUDListener) -> {
            outboundCRUDListener.updated(outbound, oldQuantity);
        });
    }

    private void resetFields() {
        tfQuantity.setText("");
        tfQuantity.setBackground(Color.WHITE);
        tfNote.setText("");
        formFieldRecipient.resetFields();
        recipientsImagePanel.setImagePlaceholder();
        comboIssuanceType.setSelectedItem(issuanceTypeNotSetYet);
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

    private class DateChangeHandler implements DateChangeListener {

        @Override
        public void dateChanged(DateChangeEvent event) {
            selectedDate = datePicker.getDate();
        }
    }

    private class BtnSubmitHandler implements ActionListener {

        boolean isFieldsFilled;

        @Override
        public void actionPerformed(ActionEvent e) {
            isFieldsFilled = (!tfQuantity.getText().isBlank() && !tfNote.getText().isBlank() && formFieldRecipient.getSelectedValue() != null)
                    && ((IssuanceTypeItem) comboIssuanceType.getSelectedItem()).getId() > (short) 0;

            if (isFieldsFilled == false) {
                String message = "Missing fields:\n";
                if (tfQuantity.getText().isBlank()) {
                    message += "\n";
                    message += "- Quantity";
                }
                if (tfNote.getText().isBlank()) {
                    message += "\n";
                    message += "- Used for";
                }
                if (formFieldRecipient.getSelectedValue() == null) {
                    message += "\n";
                    message += "- Recipient";
                }
                if (comboIssuanceType.getSelectedIndex() == 0) {
                    message += "\n";
                    message += "- Issuance type";
                }

                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (outbound == null) {
                BigDecimal quantity = new BigDecimal(tfQuantity.getText());
                Outbound itemOutbound = new Outbound();
                itemOutbound.setItem(itemMeta);
                itemOutbound.setQuantity(quantity);
                itemOutbound.setNote(tfNote.getText());
                itemOutbound.setDate(selectedDate);
                Recipient recipient = (Recipient) formFieldRecipient.getSelectedValue();
                itemOutbound.setRecipient(recipient);
                itemOutbound.setIssuanceType(((IssuanceTypeItem) comboIssuanceType.getSelectedItem()).getId());
                Outbound outbound = CRUDOutbounds.create(itemOutbound);
                if (outbound != null) {
                    notifyCreated(itemOutbound);
                    OutboundDialog.this.dispose();
                    JOptionPane.showMessageDialog(
                            null,
                            "Outbound created successfully. You can find it on a next search",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            "Issue: Outbound was not added",
                            "Failure",
                            JOptionPane.WARNING_MESSAGE);
                }
            } else {
                BigDecimal bigDecimal = new BigDecimal(tfQuantity.getText());
                BigDecimal oldQuantity = outbound.getQuantity();
                outbound.setQuantity(bigDecimal);
                Recipient recipient = (Recipient) formFieldRecipient.getSelectedValue();
                outbound.setRecipient(recipient);
                outbound.setNote(tfNote.getText());
                outbound.setDate(selectedDate);
                outbound.setIssuanceType(((IssuanceTypeItem) comboIssuanceType.getSelectedItem()).getId());
                boolean update = CRUDOutbounds.update(outbound);
                if (update) {
                    OutboundDialog.this.dispose();
                    JOptionPane.showMessageDialog(
                            null,
                            "Outbound updated successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    notifyUpdated(outbound, oldQuantity);
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            "Issue: Outbound was not updated",
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

    private class IssuanceTypeItem {

        private short id;
        private String description;

        public IssuanceTypeItem(short id, String description) {
            this.id = id;
            this.description = description;
        }

        public short getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    private class MouseJListHandler extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            JList list = (JList) e.getSource();
            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                int index = list.locationToIndex(e.getPoint());
                Recipient recipient = (Recipient) list.getModel().getElementAt(index);
                recipientsImagePanel.setImageOfSelectedItem(recipient.getId());
            }
        }
    }
}
