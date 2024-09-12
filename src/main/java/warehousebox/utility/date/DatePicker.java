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
package warehousebox.utility.date;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import warehousebox.utility.date.DateDeselectedListener;
import warehousebox.utility.date.DateLabelFormatter;
import warehousebox.utility.date.DateListener;

/**
 *
 * @author Saleh
 */
public class DatePicker {

    private JDatePickerImpl fieldDatePicker;
    private UtilDateModel dateModel;
    private JDatePanelImpl datePanel;
    private LocalDate date;
    private ArrayList<DateListener> dateListeners;
    private ArrayList<DateDeselectedListener> dateDeselectedListeners;

    public DatePicker() {

        super();

        dateListeners = new ArrayList<>();
        dateDeselectedListeners = new ArrayList<>();

        dateModel = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        datePanel = new JDatePanelImpl(dateModel, p);

        fieldDatePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        fieldDatePicker.addActionListener(new DateSelectActionHandler());
    }

    public JDatePickerImpl getDatePicker() {
        return fieldDatePicker;
    }

    public void setTodayAsDefault() {
        LocalDate today = LocalDate.now();
        dateModel.setDate(today.getYear(), today.getMonthValue() - 1, today.getDayOfMonth());
        dateModel.setSelected(true);
        this.date = today;
    }

    public void setDateValue(LocalDate date) {
        dateModel.setDate(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        dateModel.setSelected(true);
        this.date = date;
    }

    public LocalDate getDefaultToday() {
        return this.date;
    }

    public void addDateListener(DateListener dateListner) {
        dateListeners.add(dateListner);
    }

    private void notifyDateChange(LocalDate date) {
        dateListeners.forEach((dateListener) -> {
            dateListener.dateChanged(date);
        });
    }

    public void addDateDeselectedListener(DateDeselectedListener ddesl) {
        dateDeselectedListeners.add(ddesl);
    }

    private void notifyDateDeselected() {
        dateDeselectedListeners.forEach((ddesl) -> {
            ddesl.dateDeselected();
        });
    }

    public LocalDate getDate() {
        return this.date;
    }

    public void setEnabled(boolean enabled) {
        fieldDatePicker.getComponent(1).setEnabled(enabled);
    }

    private class DateSelectActionHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Date selectedDate = (Date) fieldDatePicker.getModel().getValue();
            if (selectedDate != null) {
                date = LocalDate.ofInstant(selectedDate.toInstant(), ZoneId.systemDefault());
                notifyDateChange(date);
            } else {
                notifyDateDeselected();
            }
        }

    }
}
