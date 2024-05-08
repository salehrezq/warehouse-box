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
package warehouse.panel.inwards;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import warehouse.db.CRUDInwards;

/**
 *
 * @author Saleh
 */
public class ItemsSearchLogic {

    private List<ItemsSearchListener> itemsSearchListeners;
    private String searchQuery, previousSearchQuery;
    private static int LIMIT,
            OFFSET;
    private JTextField tfSearchQuery;
    private JButton btnSearch;
    private JButton btnLoadMore;
    private JCheckBox checkCodeFilter,
            checkNameFilter,
            checkSpecificationFilter;
    private SearchFilters searchFilters, searchFiltersImmutableCopy;
    boolean isCodeChecked;
    private MatchDigitsOnlyHandler matchDigitsOnly;
    private final Pattern pattern = Pattern.compile("\\d+");
    private DateRange dateRange;
    private DateChangeHandler dateChangeHandler;
    private CheckBoxHandler checkBoxHandler;

    public ItemsSearchLogic() {
        itemsSearchListeners = new ArrayList<>();
        searchFilters = new SearchFilters();
        checkBoxHandler = new CheckBoxHandler();
        matchDigitsOnly = new MatchDigitsOnlyHandler();
        searchFilters.setCodeFilter(false);
        searchFilters.setNameFilter(true);
        searchFilters.setSpecificationFilter(true);
        searchFilters.enableDateRangeFilter(false);
    }

    protected void setTfSearchQuery(JTextField tfSearchQuery) {
        this.tfSearchQuery = tfSearchQuery;
        isCodeChecked = false;
        this.tfSearchQuery.getDocument().addDocumentListener(matchDigitsOnly);
    }

    protected void setBtnSearch(JButton btnSearch) {
        this.btnSearch = btnSearch;
        this.btnSearch.addActionListener(new SearchHandler());
    }

    protected void setBtnLoadMore(JButton btnLoadMore) {
        this.btnLoadMore = btnLoadMore;
        this.btnLoadMore.addActionListener(new LoadMoreHandler());
    }

    protected void setCheckCodeFilter(JCheckBox checkCodeFilter) {
        this.checkCodeFilter = checkCodeFilter;
        this.checkCodeFilter.addActionListener(new CheckBoxHandler());
    }

    protected void setCheckNameFilter(JCheckBox checkNameFilter) {
        this.checkNameFilter = checkNameFilter;
        this.checkNameFilter.setSelected(true);
        this.checkNameFilter.addActionListener(new CheckBoxHandler());
    }

    protected void setCheckSpecificationFilter(JCheckBox checkSpecificationFilter) {
        this.checkSpecificationFilter = checkSpecificationFilter;
        this.checkSpecificationFilter.setSelected(true);
        this.checkSpecificationFilter.addActionListener(new CheckBoxHandler());
    }

    protected void setDateRangeFilter(DateRange dateRange) {
        this.dateRange = dateRange;
        dateChangeHandler = new DateChangeHandler();
        this.dateRange.getDatePickerStart().addDateChangeListener(dateChangeHandler);
        this.dateRange.getDatePickerEnd().addDateChangeListener(dateChangeHandler);
        this.dateRange.getCheckDateFilter().addActionListener(checkBoxHandler);
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

    private class SearchHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("SearchHandler");
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
            notifySearchResultTotalRowsCount(CRUDInwards.searchResultRowsCount(searchFilters));
            notifySearchResult(CRUDInwards.search(searchFilters, LIMIT, OFFSET));
        }
    }

    private class LoadMoreHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            OFFSET += LIMIT;
            notifySearchResult(CRUDInwards.search(searchFiltersImmutableCopy, LIMIT, OFFSET));
        }
    }

    private void tfSearchQueryCodeChecker() {
        if (isCodeChecked) {
            System.out.println("isCodeChecked " + isCodeChecked);
            if (pattern.matcher(tfSearchQuery.getText()).matches()) {
                tfSearchQuery.setBackground(Color.WHITE);
            } else {
                tfSearchQuery.setBackground(new Color(255, 204, 204));
            }
        } else if (!isCodeChecked) {
            tfSearchQuery.setBackground(Color.WHITE);
        }
    }

    private class CheckBoxHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JCheckBox source = (JCheckBox) e.getSource();

            boolean isAnyChecked = checkCodeFilter.isSelected() || checkNameFilter.isSelected() || checkSpecificationFilter.isSelected();
            btnSearch.setText(isAnyChecked ? "Search" : "Get all");
            tfSearchQuery.setEnabled(isAnyChecked);

            searchFilters.enableDateRangeFilter(dateRange.getCheckDateFilter().isSelected());

            if (source == checkCodeFilter) {
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
            } else {
                if (source == checkNameFilter || source == checkSpecificationFilter) {
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
            }
            isCodeChecked = checkCodeFilter.isSelected();
            tfSearchQueryCodeChecker();
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
                searchFilters.setDateRangeStart(datePicker.getDate());
            }
            if (datePicker == dateRange.getDatePickerEnd()) {
                searchFilters.setDateRangeEnd(datePicker.getDate());
            }
        }
    }

}
