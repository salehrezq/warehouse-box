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
package warehouse.panel.items;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import warehouse.db.CRUDItems;
import warehouse.db.model.ItemMeta;

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
    private Map<String, Boolean> searchFilters;

    public ItemsSearchLogic() {
        itemsSearchListeners = new ArrayList<>();
        searchFilters = new HashMap<>();
        searchFilters.put("code", Boolean.FALSE);
        searchFilters.put("name", Boolean.TRUE);
        searchFilters.put("specification", Boolean.TRUE);
    }

    protected void setTfSearchQuery(JTextField tfSearchQuery) {
        this.tfSearchQuery = tfSearchQuery;
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
    public void notifySearchResult(List<ItemMeta> itemsMeta) {
        this.itemsSearchListeners.forEach((itemsSearchListener) -> {
            itemsSearchListener.notifySearchResult(itemsMeta);
        });
    }

    private class SearchHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("SearchHandler");
            previousSearchQuery = searchQuery;
            searchQuery = tfSearchQuery.getText();
            OFFSET = 0;
            notifyOFFSET(OFFSET);
            notifySearchResultTotalRowsCount(CRUDItems.searchResultRowsCount(searchQuery, searchFilters));
            notifySearchResult(CRUDItems.search(searchQuery, searchFilters, LIMIT, OFFSET));
        }
    }

    private class LoadMoreHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            OFFSET += LIMIT;
            notifySearchResult(CRUDItems.search(searchQuery, searchFilters, LIMIT, OFFSET));
        }
    }

    private class CheckBoxHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JCheckBox source = (JCheckBox) e.getSource();

            boolean isAnyChecked = checkCodeFilter.isSelected() || checkNameFilter.isSelected() || checkSpecificationFilter.isSelected();
            btnSearch.setText(isAnyChecked ? "Search" : "Get all");
            tfSearchQuery.setEnabled(isAnyChecked);

            if (source == checkCodeFilter) {
                boolean isCodeSelected = checkCodeFilter.isSelected();
                checkNameFilter.setEnabled(!isCodeSelected);
                checkSpecificationFilter.setEnabled(!isCodeSelected);
                searchFilters.put("code", isCodeSelected);
                if (checkCodeFilter.isSelected()) {
                    checkNameFilter.setSelected(false);
                    checkSpecificationFilter.setSelected(false);
                    searchFilters.put("name", false);
                    searchFilters.put("specification", false);
                }
            } else {
                if (source == checkNameFilter || source == checkSpecificationFilter) {
                    boolean isNameORSpecificationSelected = (checkNameFilter.isSelected() || checkSpecificationFilter.isSelected());
                    boolean isNameANDSpecificationBothDeselected = !checkNameFilter.isSelected() && !checkSpecificationFilter.isSelected();
                    if (isNameORSpecificationSelected) {
                        checkCodeFilter.setEnabled(false);
                        checkCodeFilter.setSelected(false);
                        searchFilters.put("code", false);
                    }
                    if (isNameANDSpecificationBothDeselected) {
                        checkCodeFilter.setEnabled(true);
                    }
                    searchFilters.put("name", checkNameFilter.isSelected());
                    searchFilters.put("specification", checkSpecificationFilter.isSelected());
                }
            }
        }
    }

}
