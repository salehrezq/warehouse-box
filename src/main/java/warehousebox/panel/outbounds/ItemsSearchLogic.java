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
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import warehousebox.db.CRUDOutbounds;
import warehousebox.db.CRUDRecipients;
import warehousebox.db.model.Outbound;
import warehousebox.db.model.Recipient;
import warehousebox.db.model.RecipientImage;
import warehousebox.panel.menus.ResultLimitSizePreference;
import warehousebox.panel.menus.recipients.RecipientsImagePanel;
import warehousebox.utility.recipientslisting.RecipientFormForFilters;
import warehousebox.utility.recipientslisting.RecipientFormForFiltersListener;

/**
 *
 * @author Saleh
 */
public class ItemsSearchLogic implements
        RecipientFormForFiltersListener,
        RecipientIdOfOutboundSelectedRowListener {

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
            isAnyIssuanceTypesCheckboxesSelected,
            isDateRangeCheckSelected,
            isDateRangeCheckSelectedCopy;
    private DocumentListener matchDigitsOnly, textFieldContentReactHandler;
    private final Pattern pattern = Pattern.compile("\\d+");
    private DateRange dateRange;
    private RecipientsImagePanel recipientsImagePanel;
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
    private RecipientImage recipientImageScrapper;

    public ItemsSearchLogic(ItemsSearchPane itemSearchPane) {
        prefs = Preferences.userRoot().node(getClass().getName());
        recipientFilterDialog = new RecipientFilterDialog();
        recipientFilterDialog.setDialogeToListableItemFormForFilters();
        recipientFilterDialog.setPreferences(prefs);
        recipientFilterDialog.setPreferencesKey(PREFS_RECIPIENT_OK);
        itemsSearchListeners = new ArrayList<>();
        searchFilters = new SearchFilters();
        checkBoxFiltersHandler = new CheckBoxFiltersHandler();
        matchDigitsOnly = new MatchDigitsOnlyHandler();
        textFieldContentReactHandler = new TextFieldContentReactHandler();
        searchFilters.setOutboundIdFiler(false);
        searchFilters.setItemIdFilter(false);
        searchFilters.setNameFilter(true);
        searchFilters.setSpecificationFilter(true);
        searchFilters.setNoteFilter(true);
        searchFilters.setConsumableFilter(true);
        searchFilters.setReturnableFilter(true);
        searchFilters.setScrapFilter(true);
        searchFilters.enableDateRangeFilter(false);

        recipientImageScrapper = new RecipientImage();
        try {
            recipientImageScrapper.setBufferedImage(ImageIO.read(getClass().getResource("/images/avatar-placeholder/scrapper.jpg")));
        } catch (IOException ex) {
            Logger.getLogger(ItemsSearchLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
        recipientsImagePanel = itemSearchPane.getRecipientsImagePanel();
    }

    protected RecipientFormForFilters getListableItemFormForFilters() {
        return recipientFilterDialog.getListableItemFormForFilters();
    }

    protected void setTfSearchQuery(JTextField tfSearchQuery) {
        this.tfSearchQuery = tfSearchQuery;
        isIdChecked = false;
        this.tfSearchQuery.getDocument().addDocumentListener(matchDigitsOnly);
        this.tfSearchQuery.getDocument().addDocumentListener(textFieldContentReactHandler);
    }

    /**
     * Return true if any search filter is selected. Check all search filters
     * except the outbound id filter.
     */
    private boolean isAnySearchFiltersSelected() {
        return !tfSearchQuery.getText().equals("")
                || checkItemIdFilter.isSelected()
                || checkNameFilter.isSelected()
                || checkSpecificationFilter.isSelected()
                || checkNoteFilter.isSelected()
                || isRecipientSelected
                || checkConsumableFilter.isSelected()
                || checkReturnableFilter.isSelected()
                || checkScrapFilter.isSelected()
                || isDateRangeCheckSelected;
    }

    protected void setTfRecipientFilter(JTextField tfRecipientFilter) {
        this.tfRecipientFilter = tfRecipientFilter;
        int recipientId = prefs.getInt(PREFS_RECIPIENT_OK, 0);
        recipient = null;
        if (recipientId > 0) {
            recipient = CRUDRecipients.getById(recipientId);
        }
        searchFilters.setRecipient(recipient);
        this.tfRecipientFilter.setText((recipient != null) ? recipient.getName() : "");
        if (searchFilters.isRecipientFilter()) {
            recipientsImagePanel.setImageOfSelectedItem(recipientId);
        } else {
            recipientsImagePanel.setImagePlaceholder();
        }
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
        isDateRangeCheckSelectedCopy = prefs.getBoolean(PREFS_DATE_RANGE_FILTER, false);
        this.dateRange.setSelected(isDateRangeCheckSelectedCopy);
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

    public void notifyResetTableRows() {
        this.itemsSearchListeners.forEach((itemsSearchListener) -> {
            itemsSearchListener.resetTableRows();
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
    public void selectedListable(Recipient listable) {
        if (listable != null) {
            tfRecipientFilter.setText(listable.getName());
            searchFilters.setRecipient((Recipient) listable);
            isRecipientSelected = true;
            recipientsImagePanel.setImageOfSelectedItem(listable.getId());
        } else {
            tfRecipientFilter.setText("");
            searchFilters.setRecipient(null);
            isRecipientSelected = false;
        }
        this.recipient = (Recipient) listable;
        btnSearch.setText(isAnySearchFiltersSelected() ? "Search" : "Get all");
    }

    @Override
    public void selectedRecipientIdOfOutboundRow(int recipientId) {
        recipientsImagePanel.setImageOfSelectedItem(recipientId);
    }

    @Override
    public void selectedRecipientScrapperOfOutboundRow() {
        recipientsImagePanel.setScrapperImageOfSelectedOutbound(recipientImageScrapper);
    }

    private class SearchHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            previousSearchQuery = searchQuery;
            searchQuery = tfSearchQuery.getText();
            searchFilters.setSearchQuery(searchQuery);
            searchFiltersImmutableCopy = new SearchFilters(searchFilters);
            OFFSET = 0;
            notifyResetTableRows();
            if (searchFilters.isOutboundIdFiler() || searchFilters.isItemIdFilter()) {
                if (!pattern.matcher(searchQuery).matches()) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Input must be digits.",
                            "Invalide input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            String[] searchedWords = searchFilters.getSearchQuery();
            if (searchedWords.length < 1 || (tfSearchQuery.getText().isBlank() && tfSearchQuery.getText().length() > 0)) {
                JOptionPane.showMessageDialog(
                        null,
                        "Search query is not valid for search",
                        "Write some search query.",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (searchFilters.isNameFilter() || searchFilters.isSpecificationFilter() || searchFilters.isNoteFilter()) {
                if (searchQuery.isBlank() || searchFilters.getSearchQuery().length < 1) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Write some search query.",
                            "Search query is empty",
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
        btnSearchText();
    }

    private void checkBoxOutboundIdFilterReact() {
        boolean isOutboundIdCheckBoxSelected = checkOutboundIdFilter.isSelected();
        if (isOutboundIdCheckBoxSelected) {
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
        checkItemIdFilter.setEnabled(!isOutboundIdCheckBoxSelected);
        checkNameFilter.setEnabled(!isOutboundIdCheckBoxSelected);
        checkSpecificationFilter.setEnabled(!isOutboundIdCheckBoxSelected);
        checkNoteFilter.setEnabled(!isOutboundIdCheckBoxSelected);
        checkConsumableFilter.setEnabled(!isOutboundIdCheckBoxSelected);
        checkReturnableFilter.setEnabled(!isOutboundIdCheckBoxSelected);
        checkScrapFilter.setEnabled(!isOutboundIdCheckBoxSelected);
        btnRecipientFilter.setEnabled(!isOutboundIdCheckBoxSelected);
        if (recipient != null && !isOutboundIdCheckBoxSelected) {
            tfRecipientFilter.setText(recipient.getName());
            searchFilters.setRecipient(recipient);
        }
        dateRange.setEnabled(!isOutboundIdCheckBoxSelected);
        dateRange.setSelected(isDateRangeCheckSelectedCopy && !isOutboundIdCheckBoxSelected);
        searchFilters.setOutboundIdFiler(isOutboundIdCheckBoxSelected);
    }

    private void checkBoxItemIdFilterReact() {
        boolean isItemIdFilterSelected = checkItemIdFilter.isSelected();
        if (isItemIdFilterSelected) {
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
        checkOutboundIdFilter.setEnabled(!isItemIdFilterSelected);
        checkNameFilter.setEnabled(!isItemIdFilterSelected && !checkOutboundIdFilter.isSelected());
        checkSpecificationFilter.setEnabled(!isItemIdFilterSelected && !checkOutboundIdFilter.isSelected());
        checkNoteFilter.setEnabled(!isItemIdFilterSelected && !checkOutboundIdFilter.isSelected());
        btnRecipientFilter.setEnabled(true && !checkOutboundIdFilter.isSelected());
        dateRange.setEnabled(!checkOutboundIdFilter.isSelected() || isItemIdFilterSelected);
        dateRange.setSelected(isDateRangeCheckSelectedCopy);
        checkConsumableFilter.setEnabled(true && !checkOutboundIdFilter.isSelected());
        checkReturnableFilter.setEnabled(true && !checkOutboundIdFilter.isSelected());
        checkScrapFilter.setEnabled(true && !checkOutboundIdFilter.isSelected());
        searchFilters.setItemIdFilter(isItemIdFilterSelected);
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
            checkItemIdFilter.setEnabled(!checkOutboundIdFilter.isSelected());
            checkOutboundIdFilter.setEnabled(!checkItemIdFilter.isSelected());
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
                && !checkItemIdFilter.isSelected()
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
                || checkNoteFilter.isSelected();

        isAnyIssuanceTypesCheckboxesSelected
                = checkConsumableFilter.isSelected()
                || checkReturnableFilter.isSelected()
                || checkScrapFilter.isSelected();

        searchFilters.enableDateRangeFilter(dateRange.getCheckDateFilter().isSelected());
        isIdChecked = checkOutboundIdFilter.isSelected() || checkItemIdFilter.isSelected();
        isRecipientSelected = !tfRecipientFilter.getText().isBlank();
        isDateRangeCheckSelected = dateRange.getCheckDateFilter().isSelected();
        tfSearchQueryIdChecker();
    }

    private void btnSearchText() {
        boolean isCheckOutboundIdFilterSelected = checkOutboundIdFilter.isSelected();
        String btnSearchText = "Not Set";

        if (isAnySearchFiltersSelected()) {
            btnSearchText = "Search";
        } else if (!isCheckOutboundIdFilterSelected && !isAnySearchFiltersSelected()) {
            btnSearchText = "Get all";
        } else if (isCheckOutboundIdFilterSelected) {
            btnSearchText = "Get";
        }
        btnSearch.setText(btnSearchText);
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
                isDateRangeCheckSelectedCopy = isDateRangeCheckSelected;
            }
            checkBoxFiltersAlwaysInvoke();
            btnSearchText();
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
                btnSearch.setText(isAnySearchFiltersSelected() ? "Search" : "Get all");
                prefs.putInt(PREFS_RECIPIENT_OK, 0);
                recipientsImagePanel.setImagePlaceholder();
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

    private class TextFieldContentReactHandler implements DocumentListener {

        public void changed() {
            if (checkOutboundIdFilter.isSelected()) {
                btnSearch.setText("Get");
            } else {
                btnSearch.setText(isAnySearchFiltersSelected() ? "Search" : "Get all");
            }
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            changed();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            changed();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            changed();
        }
    }
}
