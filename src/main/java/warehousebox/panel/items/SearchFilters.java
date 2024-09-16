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

/**
 *
 * @author Saleh
 */
public class SearchFilters {

    private String searchQuery;
    private boolean idFilter;
    private boolean nameFilter;
    private boolean specificationFilter;

    public SearchFilters() {
    }

    /**
     * Copy constructor to clone an immutable instance.
     *
     * @param searchFilters
     */
    public SearchFilters(SearchFilters searchFilters) {
        this.searchQuery = searchFilters.searchQuery;
        this.idFilter = searchFilters.idFilter;
        this.nameFilter = searchFilters.nameFilter;
        this.specificationFilter = searchFilters.specificationFilter;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Boolean isIdFilter() {
        return idFilter;
    }

    public void setIdFilter(boolean idFilter) {
        this.idFilter = idFilter;
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
}
