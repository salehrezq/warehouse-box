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

import java.time.LocalDate;
import warehouse.db.model.Source;

/**
 *
 * @author Saleh
 */
public class SearchFilters {

    private String searchQuery;
    private boolean codeFilter;
    private boolean nameFilter;
    private boolean specificationFilter;
    private Source source;
    // Date range
    private boolean enableDateRangeFilter;
    private LocalDate dateRangeStart;
    private LocalDate dateRangeEnd;

    public SearchFilters() {
    }

    /**
     * Copy constructor to clone an immutable instance.
     *
     * @param searchFilters
     */
    public SearchFilters(SearchFilters searchFilters) {
        this.searchQuery = searchFilters.searchQuery;
        this.codeFilter = searchFilters.codeFilter;
        this.nameFilter = searchFilters.nameFilter;
        this.specificationFilter = searchFilters.specificationFilter;
        this.source = searchFilters.source;
        this.enableDateRangeFilter = searchFilters.enableDateRangeFilter;
        this.dateRangeStart = searchFilters.dateRangeStart;
        this.dateRangeEnd = searchFilters.dateRangeEnd;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Boolean isCodeFilter() {
        return codeFilter;
    }

    public void setCodeFilter(boolean codeFilter) {
        this.codeFilter = codeFilter;
    }

    public boolean isNameFilter() {
        return nameFilter;
    }

    public void setNameFilter(boolean nameFilter) {
        this.nameFilter = nameFilter;
    }

    public boolean isSpecificationFilter() {
        return specificationFilter;
    }

    public void setSpecificationFilter(boolean specificationFilter) {
        this.specificationFilter = specificationFilter;
    }

    public boolean isSourceFilter() {
        return !(source == null || source.getId() < 1);
    }

    protected void setSource(Source source) {
        this.source = source;
    }

    public Source getSource() {
        return source;
    }

    public boolean isEnabledDateRangeFilter() {
        return enableDateRangeFilter;
    }

    public void enableDateRangeFilter(boolean enable) {
        this.enableDateRangeFilter = enable;
    }

    public LocalDate getDateRangeStart() {
        return dateRangeStart;
    }

    public void setDateRangeStart(LocalDate dateRangeStart) {
        this.dateRangeStart = dateRangeStart;
    }

    public LocalDate getDateRangeEnd() {
        return dateRangeEnd;
    }

    public void setDateRangeEnd(LocalDate dateRangeEnd) {
        this.dateRangeEnd = dateRangeEnd;
    }

}
