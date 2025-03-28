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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import warehousebox.db.CRUDInbounds;
import warehousebox.db.CRUDListable;
import warehousebox.db.model.Inbound;
import warehousebox.db.model.Source;
import warehousebox.panel.menus.ResultLimitSizePreference;
import warehousebox.utility.singularlisting.Listable;
import warehousebox.utility.singularlisting.ListableItemFormForFilters;
import warehousebox.utility.singularlisting.ListableItemFormForFiltersListener;

/**
 *
 * @author Saleh
 */
public class ItemsSearchLogic implements ListableItemFormForFiltersListener {

    private List<ItemsSearchListener> itemsSearchListeners;
    private String searchQuery, previousSearchQuery;
    private static int LIMIT,
            OFFSET;
    private JTextField tfSearchQuery, tfSourceFilter;
    private JLabel btnRemoveSource;
    private JButton btnSearch;
    private JButton btnLoadMore;
    private JCheckBox checkInboundIdFilter,
            checkItemIdFilter,
            checkNameFilter,
            checkSpecificationFilter;
    private JButton btnSourceFilter;
    private ImageIcon imageIconRemoveNormal, imageIconRemoveHover, imageIconRemovePress;
    private SourceFilterDialog sourceFilterDialog;
    private SearchFilters searchFilters, searchFiltersImmutableCopy;
    boolean isIdChecked,
            isAnyTextRelatedCheckboxesSelected,
            isSourceSelected,
            isDateRangeCheckSelected,
            isDateRangeCheckSelectedCopy;
    private MatchDigitsOnlyHandler matchDigitsOnly;
    private final Pattern pattern = Pattern.compile("\\d+");
    private DateRange dateRange;
    private LocalDate oldDateStart, oldDateEnd;
    private DateChangeHandler dateChangeHandler;
    private CheckBoxFiltersHandler checkBoxFiltersHandler;
    private static final String PREFS_INBOUND_ID_FILTER = "checkInboundIdFilter";
    private static final String PREFS_ITEM_ID_FILTER = "checkItemIdFilter";
    private static final String PREFS_NAME_FILTER = "checkNameFilter";
    private static final String PREFS_SPECIFICATION_FILTER = "checkSpecificationFilter";
    private static final String PREFS_DATE_RANGE_FILTER = "checkDateRangeFilter";
    private static final String PREFS_DATE_START_FILTER = "checkDateStartFilter";
    private static final String PREFS_DATE_END_FILTER = "checkDateEndFilter";
    private static final String PREFS_SOURCE_OK = "sourceOk";
    private Preferences prefs;
    private final Color colorError = new Color(255, 255, 0);
    private Source source;

    public ItemsSearchLogic() {
        prefs = Preferences.userRoot().node(getClass().getName());
        sourceFilterDialog = new SourceFilterDialog();
        sourceFilterDialog.setDialogeToListableItemFormForFilters();
        sourceFilterDialog.setPreferences(prefs);
        sourceFilterDialog.setPreferencesKey(PREFS_SOURCE_OK);
        itemsSearchListeners = new ArrayList<>();
        searchFilters = new SearchFilters();
        checkBoxFiltersHandler = new CheckBoxFiltersHandler();
        matchDigitsOnly = new MatchDigitsOnlyHandler();
        searchFilters.setInboundIdFilter(false);
        searchFilters.setItemIdFilter(false);
        searchFilters.setNameFilter(true);
        searchFilters.setSpecificationFilter(true);
        searchFilters.enableDateRangeFilter(false);
    }

    protected ListableItemFormForFilters getListableItemFormForFilters() {
        return sourceFilterDialog.getListableItemFormForFilters();
    }

    protected void setTfSearchQuery(JTextField tfSearchQuery) {
        this.tfSearchQuery = tfSearchQuery;
        isIdChecked = false;
        this.tfSearchQuery.getDocument().addDocumentListener(matchDigitsOnly);
    }

    protected void setTfSourceFilter(JTextField tfSourceFilter) {
        this.tfSourceFilter = tfSourceFilter;
        int sourceId = prefs.getInt(PREFS_SOURCE_OK, 0);
        Source source = null;
        if (sourceId > 0) {
            source = (Source) CRUDListable.getById(new Source(), sourceId);
        }
        this.source = source;
        searchFilters.setSource(source);
        this.tfSourceFilter.setText((source != null) ? source.getName() : "");
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
        checkInboundIdFilter = checkfilters[0];
        checkItemIdFilter = checkfilters[1];
        checkNameFilter = checkfilters[2];
        checkSpecificationFilter = checkfilters[3];

        checkInboundIdFilter.addActionListener(checkBoxFiltersHandler);
        checkItemIdFilter.addActionListener(checkBoxFiltersHandler);
        checkNameFilter.addActionListener(checkBoxFiltersHandler);
        checkSpecificationFilter.addActionListener(checkBoxFiltersHandler);

        this.checkInboundIdFilter.setSelected(prefs.getBoolean(PREFS_INBOUND_ID_FILTER, false));
        this.checkItemIdFilter.setSelected(prefs.getBoolean(PREFS_ITEM_ID_FILTER, false));
        this.checkNameFilter.setSelected(prefs.getBoolean(PREFS_NAME_FILTER, false));
        this.checkSpecificationFilter.setSelected(prefs.getBoolean(PREFS_SPECIFICATION_FILTER, false));
    }

    protected void setBtnSourceFilter(JButton btnSourceFilter) {
        this.btnSourceFilter = btnSourceFilter;
        this.btnSourceFilter.addActionListener(new ButtonSourceHandler());
    }

    protected void setBtnRemoveSource(JLabel btnRemoveSource) {
        this.btnRemoveSource = btnRemoveSource;
        this.btnRemoveSource.addMouseListener(new RemoveSourceMouseEventsHandler());
    }

    protected void setImageIconRemoveNormal(ImageIcon imageIconRemoveNormal) {
        this.imageIconRemoveNormal = imageIconRemoveNormal;

    }

    protected void setImageIconRemoveHover(ImageIcon imageIconRemoveHover) {
        this.imageIconRemoveHover = imageIconRemoveHover;
    }

    protected void setImageIconRemovePress(ImageIcon imageIconRemovePress) {
        this.imageIconRemovePress = imageIconRemovePress;
    }

    protected void setDateRangeFilter(DateRange dateRange) {
        this.dateRange = dateRange;

        isDateRangeCheckSelectedCopy = prefs.getBoolean(PREFS_DATE_RANGE_FILTER, false);
        this.dateRange.getCheckDateFilter().setSelected(isDateRangeCheckSelectedCopy);
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

    public void notifySearchResult(List data) {
        this.itemsSearchListeners.forEach((itemsSearchListener) -> {
            itemsSearchListener.notifySearchResult(data);
        });
    }

    @Override
    public void selectedListable(Listable listable) {
        if (listable != null) {
            tfSourceFilter.setText(listable.getName());
            searchFilters.setSource((Source) listable);
            isSourceSelected = true;
        } else {
            tfSourceFilter.setText("");
            searchFilters.setSource(null);
            isSourceSelected = false;
        }
        this.source = (Source) listable;
        boolean boolSum = isAnyTextRelatedCheckboxesSelected || isSourceSelected || isDateRangeCheckSelected;
        btnSearch.setText(boolSum ? "Search" : "Get all");
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
                if (searchQuery.isBlank() || searchFilters.getSearchQuery().length < 1) {
                    btnLoadMore.setEnabled(false);
                    JOptionPane.showMessageDialog(
                            null,
                            "Write some search query.",
                            "Search query is empty",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else if (searchFilters.isInboundIdFilter() || searchFilters.isItemIdFilter()) {
                if (!pattern.matcher(searchQuery).matches()) {
                    btnLoadMore.setEnabled(false);
                    JOptionPane.showMessageDialog(
                            null,
                            "Input must be digits.",
                            "Invalide input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            notifySearchResultTotalRowsCount(CRUDInbounds.searchResultRowsCount(searchFilters));
            LIMIT = ResultLimitSizePreference.getResultLimitSize();
            List<Inbound> searchResults = CRUDInbounds.search(searchFilters, LIMIT, OFFSET);
            if (searchResults.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No matched results!", "Info",
                        JOptionPane.PLAIN_MESSAGE);
            }
            notifySearchResult(searchResults);
        }
    }

    private class LoadMoreHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            OFFSET += LIMIT;
            notifySearchResult(CRUDInbounds.search(searchFiltersImmutableCopy, LIMIT, OFFSET));
        }
    }

    private void tfSearchQueryIdChecker() {
        if (isIdChecked) {
            if (pattern.matcher(tfSearchQuery.getText()).matches()) {
                tfSearchQuery.setBackground(Color.WHITE);
            } else {
                tfSearchQuery.setBackground(colorError);
            }
        } else if (!isIdChecked) {
            tfSearchQuery.setBackground(Color.WHITE);
        }
    }

    protected void initializeFiltersReactToRetrievedPreferences() {
        checkBoxInBoundIdFilterReact();
        checkBoxItemIdFilterReact();
        checkBoxesNameAndSpecificationFiltersReact();
        checkBoxFiltersAlwaysInvoke();
    }

    private void checkBoxInBoundIdFilterReact() {
        boolean isInboundIdCheckBoxSelected = checkInboundIdFilter.isSelected();
        boolean isItemIdCheckBoxSelected = checkItemIdFilter.isSelected();
        checkNameFilter.setEnabled(!isInboundIdCheckBoxSelected && !isItemIdCheckBoxSelected);
        checkSpecificationFilter.setEnabled(!isInboundIdCheckBoxSelected && !isItemIdCheckBoxSelected);
        searchFilters.setInboundIdFilter(isInboundIdCheckBoxSelected);
        btnSourceFilter.setEnabled(!isInboundIdCheckBoxSelected);
        if (source != null && !isInboundIdCheckBoxSelected) {
            tfSourceFilter.setText(source.getName());
            searchFilters.setSource(source);
        }
        dateRange.setEnabled(!isInboundIdCheckBoxSelected);
        dateRange.setSelected(isDateRangeCheckSelectedCopy && !isInboundIdCheckBoxSelected);
        if (checkInboundIdFilter.isSelected()) {
            checkItemIdFilter.setSelected(false);
            checkNameFilter.setSelected(false);
            checkSpecificationFilter.setSelected(false);
            searchFilters.setItemIdFilter(false);
            searchFilters.setNameFilter(false);
            searchFilters.setSpecificationFilter(false);
            tfSourceFilter.setText("");
            searchFilters.setSource(null);
        }
    }

    private void checkBoxItemIdFilterReact() {
        boolean isItemIdCheckBoxSelected = checkItemIdFilter.isSelected();
        boolean isInboundIdCheckBoxSelected = checkInboundIdFilter.isSelected();
        checkNameFilter.setEnabled(!isItemIdCheckBoxSelected && !isInboundIdCheckBoxSelected);
        checkSpecificationFilter.setEnabled(!isItemIdCheckBoxSelected && !isInboundIdCheckBoxSelected);
        searchFilters.setItemIdFilter(isItemIdCheckBoxSelected);
        btnSourceFilter.setEnabled(true && !isInboundIdCheckBoxSelected);
        dateRange.setEnabled(!isInboundIdCheckBoxSelected || isItemIdCheckBoxSelected);
        dateRange.setSelected(isDateRangeCheckSelectedCopy);
        if (checkItemIdFilter.isSelected()) {
            checkInboundIdFilter.setSelected(false);
            checkNameFilter.setSelected(false);
            checkSpecificationFilter.setSelected(false);
            searchFilters.setInboundIdFilter(false);
            searchFilters.setNameFilter(false);
            searchFilters.setSpecificationFilter(false);
            // Source filter control
            btnSourceFilter.setEnabled(true);
            if (source != null) {
                tfSourceFilter.setText(source.getName());
                searchFilters.setSource(source);
            }
        }
    }

    private void checkBoxesNameAndSpecificationFiltersReact() {
        boolean isNameORSpecificationSelected = (checkNameFilter.isSelected() || checkSpecificationFilter.isSelected());
        boolean isNameANDSpecificationBothDeselected = !checkNameFilter.isSelected() && !checkSpecificationFilter.isSelected();
        if (isNameORSpecificationSelected) {
            checkInboundIdFilter.setEnabled(false);
            checkInboundIdFilter.setSelected(false);
            checkItemIdFilter.setEnabled(false);
            checkItemIdFilter.setSelected(false);
            searchFilters.setInboundIdFilter(false);
            searchFilters.setItemIdFilter(false);
        }
        if (isNameANDSpecificationBothDeselected) {
            checkItemIdFilter.setEnabled(true);
            checkInboundIdFilter.setEnabled(true);
        }
        searchFilters.setNameFilter(checkNameFilter.isSelected());
        searchFilters.setSpecificationFilter(checkSpecificationFilter.isSelected());
    }

    private void checkBoxFiltersAlwaysInvoke() {
        isAnyTextRelatedCheckboxesSelected = checkInboundIdFilter.isSelected()
                || checkItemIdFilter.isSelected()
                || checkNameFilter.isSelected()
                || checkSpecificationFilter.isSelected();

        btnSearch.setText(isAnyTextRelatedCheckboxesSelected ? "Search" : "Get all");
        tfSearchQuery.setEnabled(isAnyTextRelatedCheckboxesSelected);
        searchFilters.enableDateRangeFilter(dateRange.getCheckDateFilter().isSelected());
        isIdChecked = checkInboundIdFilter.isSelected() || checkItemIdFilter.isSelected();
        isSourceSelected = !tfSourceFilter.getText().isBlank();
        isDateRangeCheckSelected = dateRange.getCheckDateFilter().isSelected();
        boolean boolSum = isAnyTextRelatedCheckboxesSelected || isSourceSelected || isDateRangeCheckSelected;
        btnSearch.setText(boolSum ? "Search" : "Get all");
        tfSearchQueryIdChecker();
    }

    private class CheckBoxFiltersHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JCheckBox source = (JCheckBox) e.getSource();

            if (source == checkInboundIdFilter) {
                checkBoxInBoundIdFilterReact();
            } else if (source == checkItemIdFilter) {
                checkBoxItemIdFilterReact();
            } else if (source == checkNameFilter || source == checkSpecificationFilter) {
                checkBoxesNameAndSpecificationFiltersReact();
            } else if (source == dateRange.getCheckDateFilter()) {
                isDateRangeCheckSelected = dateRange.getCheckDateFilter().isSelected();
                isDateRangeCheckSelectedCopy = isDateRangeCheckSelected;
                boolean boolSum = isAnyTextRelatedCheckboxesSelected || isSourceSelected || isDateRangeCheckSelected;
                btnSearch.setText(boolSum ? "Search" : "Get all");
            }
            checkBoxFiltersAlwaysInvoke();
            prefs.putBoolean(PREFS_INBOUND_ID_FILTER, checkInboundIdFilter.isSelected());
            prefs.putBoolean(PREFS_ITEM_ID_FILTER, checkItemIdFilter.isSelected());
            prefs.putBoolean(PREFS_NAME_FILTER, checkNameFilter.isSelected());
            prefs.putBoolean(PREFS_SPECIFICATION_FILTER, checkSpecificationFilter.isSelected());
            prefs.putBoolean(PREFS_DATE_RANGE_FILTER, dateRange.getCheckDateFilter().isSelected());
        }
    }

    private class MatchDigitsOnlyHandler implements DocumentListener {

        private void check(DocumentEvent e) {
            if (e.getDocument() == tfSearchQuery.getDocument()) {
                tfSearchQueryIdChecker();
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

    private class ButtonSourceHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            sourceFilterDialog.setVisible(true);
        }
    }

    private class RemoveSourceMouseEventsHandler extends MouseAdapter {

        private boolean hovered = false;

        @Override
        public void mouseClicked(MouseEvent e) {
            /**
             * checkInboundIdFilter.isSelected. If true then disable the button,
             * otherwise it is enabled.
             */
            if (!checkInboundIdFilter.isSelected()) {
                searchFilters.setSource(null);
                tfSourceFilter.setText("");
                isSourceSelected = false;
                boolean boolSum = isAnyTextRelatedCheckboxesSelected || isSourceSelected || isDateRangeCheckSelected;
                btnSearch.setText(boolSum ? "Search" : "Get all");
                prefs.putInt(PREFS_SOURCE_OK, 0);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            btnRemoveSource.setIcon(imageIconRemovePress);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            btnRemoveSource.setIcon((hovered) ? imageIconRemoveHover : imageIconRemoveNormal);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            hovered = true;
            btnRemoveSource.setIcon(imageIconRemoveHover);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            hovered = false;
            btnRemoveSource.setIcon(imageIconRemoveNormal);
        }

    }
}
