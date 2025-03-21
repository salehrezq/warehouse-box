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
import warehousebox.db.CRUDListable;
import warehousebox.db.CRUDOutbounds;
import warehousebox.db.model.Outbound;
import warehousebox.db.model.Recipient;
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
    private JTextField tfSearchQuery, tfRecipientFilter;
    private JLabel btnRemoveRecipient;
    private JButton btnSearch;
    private JButton btnLoadMore;
    private JCheckBox checkOutboundIdFilter,
            checkItemIdFilter,
            checkNameFilter,
            checkSpecificationFilter,
            checkNoteFilter,
            checkConsumableFilter,
            checkReturnableFilter,
            checkScrapFilter;
    private JButton btnRecipientFilter;
    private ImageIcon imageIconRemoveNormal, imageIconRemoveHover, imageIconRemovePress;
    private RecipientFilterDialog recipientFilterDialog;
    private SearchFilters searchFilters, searchFiltersImmutableCopy;
    boolean isIdChecked,
            isAnyTextRelatedCheckboxesSelected,
            isRecipientSelected,
            isDateRangeCheckSelected;
    private MatchDigitsOnlyHandler matchDigitsOnly;
    private final Pattern pattern = Pattern.compile("\\d+");
    private DateRange dateRange;
    private LocalDate oldDateStart, oldDateEnd;
    private DateChangeHandler dateChangeHandler;
    private CheckBoxFiltersHandler checkBoxFiltersHandler;
    private static final String PREFS_OUTBOUND_ID_FILTER = "checkOutboundIdFilter";
    private static final String PREFS_ITEM_ID_FILTER = "checkItemIdFilter";
    private static final String PREFS_NAME_FILTER = "checkNameFilter";
    private static final String PREFS_SPECIFICATION_FILTER = "checkSpecificationFilter";
    private static final String PREFS_NOTE_FILTER = "checkNoteFilter";
    private static final String PREFS_CONSUMABLE_FILTER = "checkConsumableFilter";
    private static final String PREFS_RETURNABLE_FILTER = "checkReturnableFilter";
    private static final String PREFS_SCRAP_FILTER = "checkScrapFilter";
    private static final String PREFS_DATE_RANGE_FILTER = "checkDateRangeFilter";
    private static final String PREFS_DATE_START_FILTER = "checkDateStartFilter";
    private static final String PREFS_DATE_END_FILTER = "checkDateEndFilter";
    private static final String PREFS_RECIPIENT_OK = "recipientOk";
    private Preferences prefs;
    private final Color colorError = new Color(255, 255, 0);
    private Recipient recipient;

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
        searchFilters.setOutboundIdFiler(false);
        searchFilters.setItemIdFilter(false);
        searchFilters.setNameFilter(true);
        searchFilters.setSpecificationFilter(true);
        searchFilters.setNoteFilter(true);
        searchFilters.setConsumableFilter(true);
        searchFilters.setReturnableFilter(true);
        searchFilters.setScrapFilter(true);
        searchFilters.enableDateRangeFilter(false);
    }

    protected ListableItemFormForFilters getListableItemFormForFilters() {
        return recipientFilterDialog.getListableItemFormForFilters();
    }

    protected void setTfSearchQuery(JTextField tfSearchQuery) {
        this.tfSearchQuery = tfSearchQuery;
        isIdChecked = false;
        this.tfSearchQuery.getDocument().addDocumentListener(matchDigitsOnly);
    }

    protected void setTfRecipientFilter(JTextField tfRecipientFilter) {
        this.tfRecipientFilter = tfRecipientFilter;
        int recipientId = prefs.getInt(PREFS_RECIPIENT_OK, 0);
        Recipient recipient = null;
        if (recipientId > 0) {
            recipient = (Recipient) CRUDListable.getById(new Recipient(), recipientId);
        }
        searchFilters.setRecipient(recipient);
        this.recipient = recipient;
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
        checkOutboundIdFilter = checkfilters[0];
        checkItemIdFilter = checkfilters[1];
        checkNameFilter = checkfilters[2];
        checkSpecificationFilter = checkfilters[3];
        checkNoteFilter = checkfilters[4];
        checkConsumableFilter = checkfilters[5];
        checkReturnableFilter = checkfilters[6];
        checkScrapFilter = checkfilters[7];

        checkOutboundIdFilter.addActionListener(checkBoxFiltersHandler);
        checkItemIdFilter.addActionListener(checkBoxFiltersHandler);
        checkNameFilter.addActionListener(checkBoxFiltersHandler);
        checkSpecificationFilter.addActionListener(checkBoxFiltersHandler);
        checkNoteFilter.addActionListener(checkBoxFiltersHandler);
        checkConsumableFilter.addActionListener(checkBoxFiltersHandler);
        checkReturnableFilter.addActionListener(checkBoxFiltersHandler);
        checkScrapFilter.addActionListener(checkBoxFiltersHandler);

        this.checkOutboundIdFilter.setSelected(prefs.getBoolean(PREFS_OUTBOUND_ID_FILTER, false));
        this.checkItemIdFilter.setSelected(prefs.getBoolean(PREFS_ITEM_ID_FILTER, false));
        this.checkNameFilter.setSelected(prefs.getBoolean(PREFS_NAME_FILTER, false));
        this.checkSpecificationFilter.setSelected(prefs.getBoolean(PREFS_SPECIFICATION_FILTER, false));
        this.checkNoteFilter.setSelected(prefs.getBoolean(PREFS_NOTE_FILTER, false));
        this.checkConsumableFilter.setSelected(prefs.getBoolean(PREFS_CONSUMABLE_FILTER, false));
        this.checkReturnableFilter.setSelected(prefs.getBoolean(PREFS_RETURNABLE_FILTER, false));
        this.checkScrapFilter.setSelected(prefs.getBoolean(PREFS_SCRAP_FILTER, false));
    }

    protected void setBtnRecipientFilter(JButton btnRecipientFilter) {
        this.btnRecipientFilter = btnRecipientFilter;
        this.btnRecipientFilter.addActionListener(new ButtonRecipientHandler());
    }

    protected void setBtnRemoveRecipient(JLabel btnRemoveRecipient) {
        this.btnRemoveRecipient = btnRemoveRecipient;
        this.btnRemoveRecipient.addMouseListener(new RemoveRecipientMouseEventsHandler());
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
            tfRecipientFilter.setText(listable.getName());
            searchFilters.setRecipient((Recipient) listable);
            isRecipientSelected = true;
        } else {
            tfRecipientFilter.setText("");
            searchFilters.setRecipient(null);
            isRecipientSelected = false;
        }
        this.recipient = (Recipient) listable;
        boolean boolSum = isAnyTextRelatedCheckboxesSelected || isRecipientSelected || isDateRangeCheckSelected;
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
            if (searchFilters.isNameFilter() || searchFilters.isSpecificationFilter() || searchFilters.isNoteFilter()) {
                if (searchQuery.isBlank()) {
                    btnLoadMore.setEnabled(false);
                    JOptionPane.showMessageDialog(
                            null,
                            "Write some search query.",
                            "Search query is empty",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else if (searchFilters.isOutboundIdFiler() || searchFilters.isItemIdFilter()) {
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
            notifySearchResultTotalRowsCount(CRUDOutbounds.searchResultRowsCount(searchFilters));
            LIMIT = ResultLimitSizePreference.getResultLimitSize();
            List<Outbound> searchResults = CRUDOutbounds.search(searchFilters, LIMIT, OFFSET);
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
            notifySearchResult(CRUDOutbounds.search(searchFiltersImmutableCopy, LIMIT, OFFSET));
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
        checkBoxOutboundIdFilterReact();
        checkBoxItemIdFilterReact();
        checkBoxesNameAndSpecificationFiltersReact();
        checkBoxesIssuanceTypesFiltersReact();
        checkBoxFiltersAlwaysInvoke();
    }

    private void checkBoxOutboundIdFilterReact() {
        boolean isIdSelected = checkOutboundIdFilter.isSelected();
        checkNameFilter.setEnabled(!isIdSelected);
        checkSpecificationFilter.setEnabled(!isIdSelected);
        checkNoteFilter.setEnabled(!isIdSelected);
        checkConsumableFilter.setEnabled(!isIdSelected);
        checkReturnableFilter.setEnabled(!isIdSelected);
        checkScrapFilter.setEnabled(!isIdSelected);
        btnRecipientFilter.setEnabled(!isIdSelected);
        if (recipient != null && !isIdSelected) {
            tfRecipientFilter.setText(recipient.getName());
            searchFilters.setRecipient(recipient);
        }
        searchFilters.setOutboundIdFiler(isIdSelected);
        if (checkOutboundIdFilter.isSelected()) {
            checkItemIdFilter.setSelected(false);
            checkNameFilter.setSelected(false);
            checkSpecificationFilter.setSelected(false);
            checkNoteFilter.setSelected(false);
            checkConsumableFilter.setSelected(false);
            checkReturnableFilter.setSelected(false);
            checkScrapFilter.setSelected(false);
            searchFilters.setItemIdFilter(false);
            searchFilters.setNameFilter(false);
            searchFilters.setSpecificationFilter(false);
            searchFilters.setNoteFilter(false);
            searchFilters.setConsumableFilter(false);
            searchFilters.setReturnableFilter(false);
            searchFilters.setScrapFilter(false);
            tfRecipientFilter.setText("");
            searchFilters.setRecipient(null);
        }
    }

    private void checkBoxItemIdFilterReact() {
        boolean isIdSelected = checkItemIdFilter.isSelected();
        checkNameFilter.setEnabled(!isIdSelected);
        checkSpecificationFilter.setEnabled(!isIdSelected);
        checkNoteFilter.setEnabled(!isIdSelected);
        btnRecipientFilter.setEnabled(true && !checkOutboundIdFilter.isSelected());
        checkConsumableFilter.setEnabled(true);
        checkReturnableFilter.setEnabled(true);
        checkScrapFilter.setEnabled(true);
        searchFilters.setItemIdFilter(isIdSelected);
        if (checkItemIdFilter.isSelected()) {
            checkOutboundIdFilter.setSelected(false);
            checkNameFilter.setSelected(false);
            checkSpecificationFilter.setSelected(false);
            checkNoteFilter.setSelected(false);
            searchFilters.setOutboundIdFiler(false);
            searchFilters.setNameFilter(false);
            searchFilters.setSpecificationFilter(false);
            searchFilters.setNoteFilter(false);
            // Recipient filter control
            btnRecipientFilter.setEnabled(true);
            if (recipient != null) {
                tfRecipientFilter.setText(recipient.getName());
                searchFilters.setRecipient(recipient);
            }

        }
    }

    private void checkBoxesNameAndSpecificationFiltersReact() {
        boolean isNameORSpecificationSelected = (checkNameFilter.isSelected() || checkSpecificationFilter.isSelected() || checkNoteFilter.isSelected());
        boolean isNameANDSpecificationBothDeselected = !checkNameFilter.isSelected() && !checkSpecificationFilter.isSelected() && !checkNoteFilter.isSelected();
        if (isNameORSpecificationSelected) {
            checkOutboundIdFilter.setEnabled(false);
            checkOutboundIdFilter.setSelected(false);
            checkItemIdFilter.setEnabled(false);
            checkItemIdFilter.setSelected(false);
            searchFilters.setOutboundIdFiler(false);
            searchFilters.setItemIdFilter(false);
        }
        if (isNameANDSpecificationBothDeselected) {
            checkItemIdFilter.setEnabled(true);
            checkOutboundIdFilter.setEnabled(true);
        }
        searchFilters.setNameFilter(checkNameFilter.isSelected());
        searchFilters.setSpecificationFilter(checkSpecificationFilter.isSelected());
        searchFilters.setNoteFilter(checkNoteFilter.isSelected());
    }

    private void checkBoxesIssuanceTypesFiltersReact() {
        boolean isAnyIssuanceTypesSelected = checkConsumableFilter.isSelected()
                || checkReturnableFilter.isSelected()
                || checkScrapFilter.isSelected();
        boolean isAllIssuanceTypesDeselected = !checkConsumableFilter.isSelected()
                && !checkReturnableFilter.isSelected()
                && !checkScrapFilter.isSelected();

        if (isAnyIssuanceTypesSelected) {
            checkOutboundIdFilter.setEnabled(false);
            checkOutboundIdFilter.setSelected(false);
            searchFilters.setOutboundIdFiler(false);
        }
        if (isAllIssuanceTypesDeselected
                && !checkNameFilter.isSelected()
                && !checkSpecificationFilter.isSelected()
                && !checkNoteFilter.isSelected()) {
            checkOutboundIdFilter.setEnabled(true);
        }
        searchFilters.setConsumableFilter(checkConsumableFilter.isSelected());
        searchFilters.setReturnableFilter(checkReturnableFilter.isSelected());
        searchFilters.setScrapFilter(checkScrapFilter.isSelected());
    }

    private void checkBoxFiltersAlwaysInvoke() {
        isAnyTextRelatedCheckboxesSelected
                = checkOutboundIdFilter.isSelected()
                || checkItemIdFilter.isSelected()
                || checkNameFilter.isSelected()
                || checkSpecificationFilter.isSelected()
                || checkNoteFilter.isSelected()
                || checkConsumableFilter.isSelected()
                || checkReturnableFilter.isSelected()
                || checkScrapFilter.isSelected();
        btnSearch.setText(isAnyTextRelatedCheckboxesSelected ? "Search" : "Get all");
        tfSearchQuery.setEnabled(isAnyTextRelatedCheckboxesSelected);
        searchFilters.enableDateRangeFilter(dateRange.getCheckDateFilter().isSelected());
        isIdChecked = checkOutboundIdFilter.isSelected() || checkItemIdFilter.isSelected();
        isRecipientSelected = !tfRecipientFilter.getText().isBlank();
        isDateRangeCheckSelected = dateRange.getCheckDateFilter().isSelected();
        boolean boolSum = isAnyTextRelatedCheckboxesSelected || isRecipientSelected || isDateRangeCheckSelected;
        btnSearch.setText(boolSum ? "Search" : "Get all");
        tfSearchQueryIdChecker();
    }

    private class CheckBoxFiltersHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JCheckBox source = (JCheckBox) e.getSource();

            if (source == checkOutboundIdFilter) {
                checkBoxOutboundIdFilterReact();
            } else if (source == checkItemIdFilter) {
                checkBoxItemIdFilterReact();
            } else if (source == checkNameFilter || source == checkSpecificationFilter || source == checkNoteFilter) {
                checkBoxesNameAndSpecificationFiltersReact();
            } else if (source == checkConsumableFilter
                    || source == checkReturnableFilter
                    || source == checkScrapFilter) {
                checkBoxesIssuanceTypesFiltersReact();
            } else if (source == dateRange.getCheckDateFilter()) {
                isDateRangeCheckSelected = dateRange.getCheckDateFilter().isSelected();
                boolean boolSum = isAnyTextRelatedCheckboxesSelected || isRecipientSelected || isDateRangeCheckSelected;
                btnSearch.setText(boolSum ? "Search" : "Get all");
            }
            checkBoxFiltersAlwaysInvoke();
            prefs.putBoolean(PREFS_OUTBOUND_ID_FILTER, checkOutboundIdFilter.isSelected());
            prefs.putBoolean(PREFS_ITEM_ID_FILTER, checkItemIdFilter.isSelected());
            prefs.putBoolean(PREFS_NAME_FILTER, checkNameFilter.isSelected());
            prefs.putBoolean(PREFS_SPECIFICATION_FILTER, checkSpecificationFilter.isSelected());
            prefs.putBoolean(PREFS_NOTE_FILTER, checkNoteFilter.isSelected());
            prefs.putBoolean(PREFS_CONSUMABLE_FILTER, checkConsumableFilter.isSelected());
            prefs.putBoolean(PREFS_RETURNABLE_FILTER, checkReturnableFilter.isSelected());
            prefs.putBoolean(PREFS_SCRAP_FILTER, checkScrapFilter.isSelected());
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
//     recipientFilterDialog.setVisible(true);

    private class ButtonRecipientHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            recipientFilterDialog.setVisible(true);
        }

    }

    private class RemoveRecipientMouseEventsHandler extends MouseAdapter {

        private boolean hovered = false;

        @Override
        public void mouseClicked(MouseEvent e) {
            /**
             * checkOutboundIdFilter.isSelected. If true then disable the
             * button, otherwise it is enabled.
             */
            if (!checkOutboundIdFilter.isSelected()) {
                searchFilters.setRecipient(null);
                tfRecipientFilter.setText("");
                recipient = null;
                isRecipientSelected = false;
                boolean boolSum = isAnyTextRelatedCheckboxesSelected || isRecipientSelected || isDateRangeCheckSelected;
                btnSearch.setText(boolSum ? "Search" : "Get all");
                prefs.putInt(PREFS_RECIPIENT_OK, 0);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            btnRemoveRecipient.setIcon(imageIconRemovePress);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            btnRemoveRecipient.setIcon((hovered) ? imageIconRemoveHover : imageIconRemoveNormal);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            hovered = true;
            btnRemoveRecipient.setIcon(imageIconRemoveHover);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            hovered = false;
            btnRemoveRecipient.setIcon(imageIconRemoveNormal);
        }

    }
}
