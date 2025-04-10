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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import warehousebox.db.CRUDItems;
import warehousebox.db.model.ItemMeta;
import warehousebox.panel.menus.ResultLimitSizePreference;

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
    private JCheckBox checkIdFilter,
            checkNameFilter,
            checkSpecificationFilter;
    private SearchFilters searchFilters, searchFiltersImmutableCopy;
    boolean isIdChecked;
    private DocumentListener matchDigitsOnly, textFieldContentReactHandler;
    private final Pattern pattern = Pattern.compile("\\d+");
    private Preferences prefs;
    private static final String PREFS_CODE_FILTER = "checkIdFilter";
    private static final String PREFS_NAME_FILTER = "checkNameFilter";
    private static final String PREFS_SPECIFICATION_FILTER = "checkSpecificationFilter";
    private CheckBoxFiltersHandler checkBoxFiltersHandler;
    private final Color colorError = new Color(255, 255, 0);

    public ItemsSearchLogic() {
        checkBoxFiltersHandler = new CheckBoxFiltersHandler();
        itemsSearchListeners = new ArrayList<>();
        searchFilters = new SearchFilters();
        matchDigitsOnly = new MatchDigitsOnlyHandler();
        textFieldContentReactHandler = new TextFieldContentReactHandler();
        searchFilters.setIdFilter(false);
        searchFilters.setNameFilter(true);
        searchFilters.setSpecificationFilter(true);
        prefs = Preferences.userRoot().node(getClass().getName());
    }

    protected void setTfSearchQuery(JTextField tfSearchQuery) {
        this.tfSearchQuery = tfSearchQuery;
        isIdChecked = false;
        this.tfSearchQuery.getDocument().addDocumentListener(matchDigitsOnly);
        this.tfSearchQuery.getDocument().addDocumentListener(textFieldContentReactHandler);
    }

    protected void setBtnSearch(JButton btnSearch) {
        this.btnSearch = btnSearch;
        this.btnSearch.setText("Get all");
        this.btnSearch.addActionListener(new SearchHandler());
    }

    private boolean isTfSearchQueryEmpty() {
        return tfSearchQuery.getText().equals("");
    }

    /**
     * Return true if any search filter is selected. Check all search filters
     * except the item id filter.
     */
    private boolean isAnySearchFiltersSelected() {
        return !isTfSearchQueryEmpty()
                || checkNameFilter.isSelected()
                || checkSpecificationFilter.isSelected();
    }

    protected void setBtnLoadMore(JButton btnLoadMore) {
        this.btnLoadMore = btnLoadMore;
        this.btnLoadMore.addActionListener(new LoadMoreHandler());
    }

    protected void setCheckFilters(JCheckBox... checks) {
        checkIdFilter = checks[0];
        checkNameFilter = checks[1];
        checkSpecificationFilter = checks[2];

        checkIdFilter.addActionListener(checkBoxFiltersHandler);
        checkNameFilter.addActionListener(checkBoxFiltersHandler);
        checkSpecificationFilter.addActionListener(checkBoxFiltersHandler);

        this.checkIdFilter.setSelected(prefs.getBoolean(PREFS_CODE_FILTER, false));
        this.checkNameFilter.setSelected(prefs.getBoolean(PREFS_NAME_FILTER, false));
        this.checkSpecificationFilter.setSelected(prefs.getBoolean(PREFS_SPECIFICATION_FILTER, false));

        initializeFiltersReactToRetrievedPreferences();
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
    public void notifySearchResult(List<ItemMeta> itemsMeta) {
        this.itemsSearchListeners.forEach((itemsSearchListener) -> {
            itemsSearchListener.notifySearchResult(itemsMeta);
        });
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
                btnLoadMore.setEnabled(false);
                if (searchQuery.isBlank() || searchFilters.getSearchQuery().length < 1) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Write some search query.",
                            "Search query is empty",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else if (searchFilters.isIdFilter()) {
                if (!pattern.matcher(searchQuery).matches()) {
                    btnLoadMore.setEnabled(false);
                    JOptionPane.showMessageDialog(
                            null,
                            "Input must be digits.",
                            "Invalide input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else if (!searchFilters.isIdFilter()
                    && !searchFilters.isNameFilter()
                    && !searchFilters.isSpecificationFilter()) {
                if ((!searchQuery.isEmpty() && searchQuery.isBlank()) || searchFilters.getSearchQuery().length < 1) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Search query is not valid for search",
                            "Write some search query.",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            notifySearchResultTotalRowsCount(CRUDItems.searchResultRowsCount(searchFilters));
            LIMIT = ResultLimitSizePreference.getResultLimitSize();
            List<ItemMeta> searchResults = CRUDItems.search(searchFilters, LIMIT, OFFSET);
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
            notifySearchResult(CRUDItems.search(searchFiltersImmutableCopy, LIMIT, OFFSET));
        }
    }

    private void tfSearchQueryIdChecker() {
        if (isIdChecked) {
            if (pattern.matcher(tfSearchQuery.getText()).matches()) {
                tfSearchQuery.setBackground(Color.WHITE);
                btnSearch.setEnabled(true);
            } else {
                tfSearchQuery.setBackground(colorError);
                btnSearch.setEnabled(false);
            }
        } else if (!isIdChecked) {
            tfSearchQuery.setBackground(Color.WHITE);
            btnSearch.setEnabled(true);
        }
    }

    private void initializeFiltersReactToRetrievedPreferences() {
        checkBoxidFilterReact();
        checkBoxesNameAndSpecificationFiltersReact();
        checkBoxFiltersAlwaysInvoke();
        btnSearchText();
    }

    private void checkBoxidFilterReact() {
        boolean isIdSelected = checkIdFilter.isSelected();
        checkNameFilter.setEnabled(!isIdSelected);
        checkSpecificationFilter.setEnabled(!isIdSelected);
        searchFilters.setIdFilter(isIdSelected);
        if (isIdSelected) {
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
            checkIdFilter.setEnabled(false);
            checkIdFilter.setSelected(false);
            searchFilters.setIdFilter(false);
        }
        if (isNameANDSpecificationBothDeselected) {
            checkIdFilter.setEnabled(true);
        }
        searchFilters.setNameFilter(checkNameFilter.isSelected());
        searchFilters.setSpecificationFilter(checkSpecificationFilter.isSelected());
    }

    private void checkBoxFiltersAlwaysInvoke() {
        isIdChecked = checkIdFilter.isSelected();
        tfSearchQueryIdChecker();
    }

    private void btnSearchText() {
        boolean isItemIdSelected = checkIdFilter.isSelected();
        String btnSearchText = "Not Set";

        if (isAnySearchFiltersSelected()) {
            btnSearchText = "Search";
        } else if (!isItemIdSelected && !isAnySearchFiltersSelected()) {
            btnSearchText = "Get all";
        } else if (isItemIdSelected) {
            btnSearchText = "Get";
        }
        btnSearch.setText(btnSearchText);
    }

    private class CheckBoxFiltersHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JCheckBox source = (JCheckBox) e.getSource();
            if (source == checkIdFilter) {
                checkBoxidFilterReact();
            } else {
                if (source == checkNameFilter || source == checkSpecificationFilter) {
                    checkBoxesNameAndSpecificationFiltersReact();
                }
            }
            checkBoxFiltersAlwaysInvoke();
            btnSearchText();
            prefs.putBoolean(PREFS_CODE_FILTER, checkIdFilter.isSelected());
            prefs.putBoolean(PREFS_NAME_FILTER, checkNameFilter.isSelected());
            prefs.putBoolean(PREFS_SPECIFICATION_FILTER, checkSpecificationFilter.isSelected());
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

    private class TextFieldContentReactHandler implements DocumentListener {

        public void changed() {
            if (checkIdFilter.isSelected()) {
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
