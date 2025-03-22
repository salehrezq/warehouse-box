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

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.time.LocalDate;
import java.util.Locale;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Saleh
 */
public class DateRange {

    private JPanel container;
    private DatePicker datePickerStart,
            datePickerEnd;
    private DatePickerSettings datePickerSettingsStart,
            datePickerSettingsEnd;
    private JLabel lbDateStart,
            lbDateEnd;
    private JCheckBox checkDateFilter;

    public DateRange() {
        checkDateFilter = new JCheckBox("Date range:");
        datePickerStart = new DatePicker();
        datePickerEnd = new DatePicker();
        datePickerSettingsStart = new DatePickerSettings();
        datePickerSettingsStart.setAllowEmptyDates(false);
        datePickerSettingsStart.setAllowKeyboardEditing(false);
        datePickerSettingsStart.setLocale(Locale.ENGLISH);
        datePickerSettingsStart.setFormatForDatesCommonEra("dd-MMM-uuuu");

        datePickerSettingsEnd = new DatePickerSettings();
        datePickerSettingsEnd.setAllowEmptyDates(false);
        datePickerSettingsEnd.setAllowKeyboardEditing(false);
        datePickerSettingsEnd.setLocale(Locale.ENGLISH);
        datePickerSettingsEnd.setFormatForDatesCommonEra("dd-MMM-uuuu");

        datePickerStart.setSettings(datePickerSettingsStart);
        datePickerEnd.setSettings(datePickerSettingsEnd);

        lbDateStart = new JLabel("Start date");
        lbDateEnd = new JLabel("End date");

        container = new JPanel();
        container.add(checkDateFilter);
        container.add(lbDateStart);
        container.add(datePickerStart);
        container.add(lbDateEnd);
        container.add(datePickerEnd);
    }

    protected JPanel getContainer() {
        return container;
    }

    protected DatePicker getDatePickerStart() {
        return this.datePickerStart;
    }

    protected DatePicker getDatePickerEnd() {
        return datePickerEnd;
    }

    protected JCheckBox getCheckDateFilter() {
        return checkDateFilter;
    }

    protected LocalDate getTodayDate() {
        return LocalDate.now();
    }

    protected void setEnabled(boolean enable) {
        datePickerStart.setEnabled(enable);
        datePickerEnd.setEnabled(enable);
        checkDateFilter.setEnabled(enable);
    }

    protected void setSelected(boolean selected) {
        checkDateFilter.setSelected(selected);
    }
}
