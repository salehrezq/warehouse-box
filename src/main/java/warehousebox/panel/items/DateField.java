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
package warehousebox.panel.items;

import java.time.LocalDate;
import org.jdatepicker.impl.JDatePickerImpl;
import warehousebox.utility.date.DateDeselectedListener;
import warehousebox.utility.date.DateListener;
import warehousebox.utility.date.DatePicker;

/**
 *
 * @author Saleh
 */
public class DateField implements
        DateListener,
        DateDeselectedListener {

    private DatePicker datePicker;
    private LocalDate selectedDate;

    public DateField() {
        setupfield();
        // initial setting

    }

    private void setupfield() {
        datePicker = new DatePicker();
        datePicker.setTodayAsDefault();
        datePicker.addDateListener(this);
        datePicker.addDateDeselectedListener(this);
    }

    public JDatePickerImpl getDatePicker() {
        return datePicker.getDatePicker();
    }

    private void setDate(LocalDate date) {
        this.selectedDate = date;
    }

    public LocalDate getDate() {
        return this.selectedDate;
    }

    @Override
    public void dateChanged(LocalDate date) {
        setDate(date);
    }

    @Override
    public void dateDeselected() {
        System.out.println("date dateDeselected");
    }

}
