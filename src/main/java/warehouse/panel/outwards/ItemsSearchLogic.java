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
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
    private CheckBoxHandler checkBoxHandler;

    public ItemsSearchLogic() {
        recipientFilterDialog = new RecipientFilterDialog();
        recipientFilterDialog.setDialogeToListableItemFormForFilters();
        itemsSearchListeners = new ArrayList<>();
        searchFilters = new SearchFilters();
        checkBoxHandler = new CheckBoxHandler();
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
        this.checkNameFilter.addActionListener(checkBoxHandler);
    }

    protected void setCheckSpecificationFilter(JCheckBox checkSpecificationFilter) {
        this.checkSpecificationFilter = checkSpecificationFilter;
        this.checkSpecificationFilter.setSelected(true);
        this.checkSpecificationFilter.addActionListener(checkBoxHandler);
    }

    protected void setBtnRecipientFilter(JLabel btnRecipientFilter) {
        this.btnRecipientFilter = btnRecipientFilter;
        this.btnRecipientFilter.addMouseListener(new MouseEventsHandler());
    }

    protected void setDateRangeFilter(DateRange dateRange) {
        this.dateRange = dateRange;
        dateChangeHandler = new DateChangeHandler();
        this.dateRange.getDatePickerStart().addDateChangeListener(dateChangeHandler);
        this.dateRange.getDatePickerEnd().addDateChangeListener(dateChangeHandler);
        this.dateRange.getCheckDateFilter().addActionListener(checkBoxHandler);
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
