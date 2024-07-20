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
package warehouse.panel.outwards;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import warehouse.db.CRUDListable;
import warehouse.db.CRUDOutwards;
import warehouse.db.model.Recipient;
import warehouse.singularlisting.Listable;
import warehouse.singularlisting.ListableItemFormForFilters;
import warehouse.singularlisting.ListableItemFormForFiltersListener;

/**
 *
 * @author Saleh
 */
public class ItemsSearchLogic implements ListableItemFormForFiltersListener {

    private List<ItemsSearchListener> itemsSearchListeners;
    private String searchQuery, previousSearchQuery;
    private static int LIMIT,
            OFFSET;
    private JTextField tfSearchQuery, tfRecipientFilter;
    private JButton btnSearch;
    private JButton btnLoadMore;
    private JCheckBox checkCodeFilter,
            checkNameFilter,
            checkSpecificationFilter;
    private JLabel btnRecipientFilter;
    private RecipientFilterDialog recipientFilterDialog;
    private SearchFilters searchFilters, searchFiltersImmutableCopy;
    boolean isCodeChecked;
    private MatchDigitsOnlyHandler matchDigitsOnly;
    private final Pattern pattern = Pattern.compile("\\d+");
    private DateRange dateRange;
    private LocalDate oldDateStart, oldDateEnd;
    private DateChangeHandler dateChangeHandler;
    private CheckBoxFiltersHandler checkBoxFiltersHandler;
    private static final String PREFS_CODE_FILTER = "checkCodeFilter";
    private static final String PREFS_NAME_FILTER = "checkNameFilter";
    private static final String PREFS_SPECIFICATION_FILTER = "checkSpecificationFilter";
    private static final String PREFS_DATE_RANGE_FILTER = "checkDateRangeFilter";
    private static final String PREFS_DATE_START_FILTER = "checkDateStartFilter";
    private static final String PREFS_DATE_END_FILTER = "checkDateEndFilter";
    private static final String PREFS_RECIPIENT_OK = "recipientOk";
    private Preferences prefs;

    public ItemsSearchLogic() {
        prefs = Preferences.userRoot().node(getClass().getName());
        recipientFilterDialog = new RecipientFilterDialog();
        recipientFilterDialog.setDialogeToListableItemFormForFilters();
        recipientFilterDialog.setPreferences(prefs);
        recipientFilterDialog.setPreferencesKey(PREFS_RECIPIENT_OK);
        itemsSearchListeners = new ArrayList<>();
        searchFilters = new SearchFilters();
        checkBoxFiltersHandler = new CheckBoxFiltersHandler();
        matchDigitsOnly = new MatchDigitsOnlyHandler();
        searchFilters.setCodeFilter(false);
        searchFilters.setNameFilter(true);
        searchFilters.setSpecificationFilter(true);
        searchFilters.enableDateRangeFilter(false);
    }

    protected ListableItemFormForFilters getListableItemFormForFilters() {
        return recipientFilterDialog.getListableItemFormForFilters();
    }

    protected void setTfSearchQuery(JTextField tfSearchQuery) {
        this.tfSearchQuery = tfSearchQuery;
        isCodeChecked = false;
        this.tfSearchQuery.getDocument().addDocumentListener(matchDigitsOnly);
    }

    protected void setTfRecipientFilter(JTextField tfRecipientFilter) {
        this.tfRecipientFilter = tfRecipientFilter;
        int recipientId = prefs.getInt(PREFS_RECIPIENT_OK, 0);
        Recipient recipient = null;
        if (recipientId > 0) {
            recipient = (Recipient) CRUDListable.getById(new Recipient(), recipientId);
        }
        this.tfRecipientFilter.setText((recipient != null) ? recipient.getName() : "");
    }

    protected void setBtnSearch(JButton btnSearch) {
        this.btnSearch = btnSearch;
        this.btnSearch.addActionListener(new SearchHandler());
    }

    protected void setBtnLoadMore(JButton btnLoadMore) {
        this.btnLoadMore = btnLoadMore;
        this.btnLoadMore.addActionListener(new LoadMoreHandler());
    }

    protected void setCheckFilters(JCheckBox... checkfilters) {
        checkCodeFilter = checkfilters[0];
        checkNameFilter = checkfilters[1];
        checkSpecificationFilter = checkfilters[2];

        checkCodeFilter.addActionListener(checkBoxFiltersHandler);
        checkNameFilter.addActionListener(checkBoxFiltersHandler);
        checkSpecificationFilter.addActionListener(checkBoxFiltersHandler);

        this.checkCodeFilter.setSelected(prefs.getBoolean(PREFS_CODE_FILTER, false));
        this.checkNameFilter.setSelected(prefs.getBoolean(PREFS_NAME_FILTER, false));
        this.checkSpecificationFilter.setSelected(prefs.getBoolean(PREFS_SPECIFICATION_FILTER, false));
    }

    protected void setBtnRecipientFilter(JLabel btnRecipientFilter) {
        this.btnRecipientFilter = btnRecipientFilter;
        this.btnRecipientFilter.addMouseListener(new MouseEventsHandler());
    }

    protected void setDateRangeFilter(DateRange dateRange) {
        this.dateRange = dateRange;

        this.dateRange.getCheckDateFilter().setSelected(prefs.getBoolean(PREFS_DATE_RANGE_FILTER, false));
        this.dateRange.getDatePickerStart().setDate(LocalDate.parse(prefs.get(PREFS_DATE_START_FILTER, this.dateRange.getTodayDate().toString())));
        this.dateRange.getDatePickerEnd().setDate(LocalDate.parse(prefs.get(PREFS_DATE_END_FILTER, this.dateRange.getTodayDate().toString())));

        dateChangeHandler = new DateChangeHandler();
        this.dateRange.getDatePickerStart().addDateChangeListener(dateChangeHandler);
        this.dateRange.getDatePickerEnd().addDateChangeListener(dateChangeHandler);
        this.dateRange.getCheckDateFilter().addActionListener(checkBoxFiltersHandler);

        oldDateStart = dateRange.getDatePickerStart().getDate();
        oldDateEnd = dateRange.getDatePickerEnd().getDate();
        searchFilters.setDateRangeStart(dateRange.getDatePickerStart().getDate());
        searchFilters.setDateRangeEnd(dateRange.getDatePickerEnd().getDate());
    }

    public static void setResultsPageLimit(int pageLimit) {
        ItemsSearchLogic.LIMIT = pageLimit;
    }

    public static int getResultsPageLimit() {
        return ItemsSearchLogic.LIMIT;
    }

    public void addItemSearchListener(ItemsSearchListener itemsSearchListener) {
        this.itemsSearchListeners.add(itemsSearchListener);
    }

    public void notifyOFFSET(int OFFSET) {
        this.itemsSearchListeners.forEach((itemsSearchListener) -> {
            itemsSearchListener.notifyOFFSET(OFFSET);
        });
    }

    public void notifySearchResultTotalRowsCount(int searchResultTotalRowsCount) {
        this.itemsSearchListeners.forEach((itemsSearchListener) -> {
            itemsSearchListener.notifySearchResultTotalRowsCount(searchResultTotalRowsCount);
        });
    }

//    public void notifySearchQuery(String currentQuery, String previousQuery) {
//        this.itemsSearchListeners.forEach((itemsSearchListener) -> {
//            itemsSearchListener.notifySearchQuery(currentQuery, previousQuery);
//        });
//    }
    public void notifySearchResult(List data) {
        this.itemsSearchListeners.forEach((itemsSearchListener) -> {
            itemsSearchListener.notifySearchResult(data);
        });
    }

    @Override
    public void selectedListable(Listable listable) {
        if (listable != null) {
            tfRecipientFilter.setText(listable.getName());
            searchFilters.setRecipient((Recipient) listable);
        } else {
            tfRecipientFilter.setText(null);
            searchFilters.setRecipient(null);
        }
    }

    private class SearchHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            previousSearchQuery = searchQuery;
            searchQuery = tfSearchQuery.getText();
            searchFilters.setSearchQuery(searchQuery);
            searchFiltersImmutableCopy = new SearchFilters(searchFilters);
            OFFSET = 0;
            notifyOFFSET(OFFSET);
            if (searchFilters.isNameFilter() || searchFilters.isSpecificationFilter()) {
                if (searchQuery.isBlank()) {
                    btnLoadMore.setEnabled(false);
                    JOptionPane.showMessageDialog(
                            null,
                            "Write some search query.",
                            "Search query is empty",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else if (searchFilters.isCodeFilter()) {
                if (!pattern.matcher(searchFilters.getSearchQuery()).matches()) {
                    btnLoadMore.setEnabled(false);
                    JOptionPane.showMessageDialog(
                            null,
                            "Input must be digits.",
                            "Invalide input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            notifySearchResultTotalRowsCount(CRUDOutwards.searchResultRowsCount(searchFilters));
            notifySearchResult(CRUDOutwards.search(searchFilters, LIMIT, OFFSET));
        }
    }

    private class LoadMoreHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            OFFSET += LIMIT;
            notifySearchResult(CRUDOutwards.search(searchFiltersImmutableCopy, LIMIT, OFFSET));
        }
    }

    private void tfSearchQueryCodeChecker() {
        if (isCodeChecked) {
            if (pattern.matcher(tfSearchQuery.getText()).matches()) {
                tfSearchQuery.setBackground(Color.WHITE);
            } else {
                tfSearchQuery.setBackground(new Color(255, 204, 204));
            }
        } else if (!isCodeChecked) {
            tfSearchQuery.setBackground(Color.WHITE);
        }
    }

    protected void initializeFiltersReactToRetrievedPreferences() {
        checkBoxcodeFilterReact();
        checkBoxesNameAndSpecificationFiltersReact();
        checkBoxFiltersAlwaysInvoke();
    }

    private void checkBoxcodeFilterReact() {
        boolean isCodeSelected = checkCodeFilter.isSelected();
        checkNameFilter.setEnabled(!isCodeSelected);
        checkSpecificationFilter.setEnabled(!isCodeSelected);
        searchFilters.setCodeFilter(isCodeSelected);
        if (checkCodeFilter.isSelected()) {
            checkNameFilter.setSelected(false);
            checkSpecificationFilter.setSelected(false);
            searchFilters.setNameFilter(false);
            searchFilters.setSpecificationFilter(false);
        }
    }

    private void checkBoxesNameAndSpecificationFiltersReact() {
        boolean isNameORSpecificationSelected = (checkNameFilter.isSelected() || checkSpecificationFilter.isSelected());
        boolean isNameANDSpecificationBothDeselected = !checkNameFilter.isSelected() && !checkSpecificationFilter.isSelected();
        if (isNameORSpecificationSelected) {
            checkCodeFilter.setEnabled(false);
            checkCodeFilter.setSelected(false);
            searchFilters.setCodeFilter(false);
        }
        if (isNameANDSpecificationBothDeselected) {
            checkCodeFilter.setEnabled(true);
        }
        searchFilters.setNameFilter(checkNameFilter.isSelected());
        searchFilters.setSpecificationFilter(checkSpecificationFilter.isSelected());
    }

    private void checkBoxFiltersAlwaysInvoke() {
        boolean isAnyChecked = checkCodeFilter.isSelected() || checkNameFilter.isSelected() || checkSpecificationFilter.isSelected();
        btnSearch.setText(isAnyChecked ? "Search" : "Get all");
        tfSearchQuery.setEnabled(isAnyChecked);
        searchFilters.enableDateRangeFilter(dateRange.getCheckDateFilter().isSelected());
        isCodeChecked = checkCodeFilter.isSelected();
        tfSearchQueryCodeChecker();
    }

    private class CheckBoxFiltersHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JCheckBox source = (JCheckBox) e.getSource();

            if (source == checkCodeFilter) {
                checkBoxcodeFilterReact();
            } else {
                if (source == checkNameFilter || source == checkSpecificationFilter) {
                    checkBoxesNameAndSpecificationFiltersReact();
                }
            }
            checkBoxFiltersAlwaysInvoke();
            prefs.putBoolean(PREFS_CODE_FILTER, checkCodeFilter.isSelected());
            prefs.putBoolean(PREFS_NAME_FILTER, checkNameFilter.isSelected());
            prefs.putBoolean(PREFS_SPECIFICATION_FILTER, checkSpecificationFilter.isSelected());
            prefs.putBoolean(PREFS_DATE_RANGE_FILTER, dateRange.getCheckDateFilter().isSelected());
        }
    }

    private class MatchDigitsOnlyHandler implements DocumentListener {

        private void check(DocumentEvent e) {
            if (e.getDocument() == tfSearchQuery.getDocument()) {
                tfSearchQueryCodeChecker();
            }
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            check(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            check(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            check(e);
        }
    }

    private class DateChangeHandler implements DateChangeListener {

        @Override
        public void dateChanged(DateChangeEvent event) {
            DatePicker datePicker = (DatePicker) event.getSource();

            if (datePicker == dateRange.getDatePickerStart()) {
                if (dateRange.getDatePickerStart().getDate().isAfter(dateRange.getDatePickerEnd().getDate())) {
                    dateRange.getDatePickerStart().setDate(oldDateStart);
                    JOptionPane.showMessageDialog(
                            null,
                            "Start date must be before end date.",
                            "Date range is wrong!",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else if (datePicker == dateRange.getDatePickerEnd()) {
                if (dateRange.getDatePickerEnd().getDate().isBefore(dateRange.getDatePickerStart().getDate())) {
                    dateRange.getDatePickerEnd().setDate(oldDateEnd);
                    JOptionPane.showMessageDialog(
                            null,
                            "End date must be after start date.",
                            "Date range is wrong!",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            oldDateStart = dateRange.getDatePickerStart().getDate();
            oldDateEnd = dateRange.getDatePickerEnd().getDate();

            prefs.put(PREFS_DATE_START_FILTER, dateRange.getDatePickerStart().getDate().toString());
            prefs.put(PREFS_DATE_END_FILTER, dateRange.getDatePickerEnd().getDate().toString());

            if (datePicker == dateRange.getDatePickerStart()) {
                searchFilters.setDateRangeStart(datePicker.getDate());
            }
            if (datePicker == dateRange.getDatePickerEnd()) {
                searchFilters.setDateRangeEnd(datePicker.getDate());
            }
        }
    }

    private class MouseEventsHandler extends MouseAdapter {

        private boolean hovered = false;

        @Override
        public void mouseClicked(MouseEvent e) {
            recipientFilterDialog.setVisible(true);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            btnRecipientFilter.setBackground(ItemsSearchPane.colorBtnSourcePressed);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            btnRecipientFilter.setBackground((hovered) ? ItemsSearchPane.colorBtnSourceHover : ItemsSearchPane.colorBtnSourceNormal);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            hovered = true;
            btnRecipientFilter.setBackground(ItemsSearchPane.colorBtnSourceHover);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            hovered = false;
            btnRecipientFilter.setBackground(ItemsSearchPane.colorBtnSourceNormal);
        }
    }
}
