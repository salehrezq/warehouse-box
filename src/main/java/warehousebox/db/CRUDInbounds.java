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
package warehousebox.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import warehousebox.db.model.Inbound;
import warehousebox.db.model.Item;
import warehousebox.db.model.QuantityUnit;
import warehousebox.db.model.Source;
import warehousebox.panel.inbounds.SearchFilters;

/**
 *
 * @author Saleh
 */
public class CRUDInbounds {

    private static String[] searchedWords;
    private static int wordsLength;

    public static Inbound create(Inbound inbound) {
        String sql = "INSERT INTO inbounds (item_id, quantity, date, source_id) VALUES (?, ?, ?, ?)";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setInt(1, inbound.getItem().getId());
            p.setBigDecimal(2, inbound.getQuantity());
            p.setObject(3, java.sql.Date.valueOf(inbound.getDate()));
            p.setInt(4, inbound.getSource().getId());
            p.executeUpdate();

            try (ResultSet generatedKeys = p.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    inbound.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Obtaining inbound ID failed.");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDInbounds.class.getName()).log(Level.SEVERE, null, ex);
        }
        return inbound;
    }

    private static String formulateSearchFilters(SearchFilters searchFilters) {
        String sqlFilter = " WHERE ";
        boolean isSearchisQueryBlank = searchFilters.getSearchQuery().isBlank();
        boolean isIdFilter = searchFilters.isIdFilter();
        boolean isNameFilter = searchFilters.isNameFilter();
        boolean isSpecificationFilter = searchFilters.isSpecificationFilter();
        boolean isDateRangeFilter = searchFilters.isEnabledDateRangeFilter();
        boolean isSourceFilter = searchFilters.isSourceFilter();
        boolean isAnyFilterOn = isIdFilter || isNameFilter || isSpecificationFilter || isSourceFilter;

        if ((!isAnyFilterOn || isSearchisQueryBlank) && !(isDateRangeFilter || isSourceFilter)) {
            sqlFilter = "";
            return sqlFilter;
        }
        if (isDateRangeFilter) {
            sqlFilter += "(date >= ? AND date <= ?)";
            if (isIdFilter || isNameFilter || isSpecificationFilter || isSourceFilter) {
                sqlFilter += " AND";
            }
        }
        if (isSourceFilter) {
            sqlFilter += (isDateRangeFilter) ? " " : "";
            sqlFilter += "(source_id = ?)";
            if (isIdFilter || isNameFilter || isSpecificationFilter) {
                sqlFilter += " AND";
            }
        }
        if (isIdFilter) {
            sqlFilter += (isDateRangeFilter || isSourceFilter) ? " " : "";
            sqlFilter += "(i.id = ?)";
            return sqlFilter;
        } else if (isNameFilter || isSpecificationFilter) {
            searchedWords = SearchFormatter.getArrayOfWords(searchFilters.getSearchQuery());
            wordsLength = searchedWords.length;

            String query;
            if (isNameFilter && !isSpecificationFilter) {
                query = "i.name LIKE ?";
            } else if (isSpecificationFilter && !isNameFilter) {
                query = "i.specification LIKE ?";
            } else {
                query = "(i.name || ' ' || i.specification) LIKE ?";
            }
            sqlFilter += (isSourceFilter || isDateRangeFilter) ? " " : "";
            sqlFilter += wordsLength > 1 ? "(" : "";
            for (var i = 0; i < wordsLength; i++) {
                sqlFilter += query;
                sqlFilter += (wordsLength > 1 && i == 0) ? ")" : "";
                sqlFilter += (i > 0) ? ")" : "";
                sqlFilter += (i < (wordsLength - 1)) ? " AND (" : "";
            }
        }
        return sqlFilter;
    }

    private static PreparedStatementWrapper formulateSearchPreparedStatement(SearchFilters searchFilters, PreparedStatementWrapper preparedStatementWrapper) throws SQLException {
        String searchQuery = searchFilters.getSearchQuery();
        boolean isIdFilter = searchFilters.isIdFilter();
        boolean isNameFilter = searchFilters.isNameFilter();
        boolean isSpecificationFilter = searchFilters.isSpecificationFilter();
        boolean isDateRangeFilter = searchFilters.isEnabledDateRangeFilter();
        boolean isSourceFilter = searchFilters.isSourceFilter();
        PreparedStatement p = preparedStatementWrapper.getPreparedStatement();

        boolean isAnyFilterOn = isIdFilter || isNameFilter || isSpecificationFilter || isSourceFilter;

        if ((!isAnyFilterOn || searchQuery.isBlank()) && !(isDateRangeFilter || isSourceFilter)) {
            return preparedStatementWrapper;
        }
        if (isDateRangeFilter) {
            p.setObject(preparedStatementWrapper.incrementParameterIndex(), java.sql.Date.valueOf(searchFilters.getDateRangeStart()));
            p.setObject(preparedStatementWrapper.incrementParameterIndex(), java.sql.Date.valueOf(searchFilters.getDateRangeEnd()));
        }
        if (isSourceFilter) {
            p.setInt(preparedStatementWrapper.incrementParameterIndex(), searchFilters.getSource().getId());
        }
        if (isIdFilter) {
            p.setInt(preparedStatementWrapper.incrementParameterIndex(), Integer.parseInt(searchQuery));
        } else if (isNameFilter || isSpecificationFilter) {
            for (int i = 0; i < wordsLength; i++) {
                p.setString(preparedStatementWrapper.incrementParameterIndex(), "%" + searchedWords[i] + "%");
            }
        }
        return preparedStatementWrapper;
    }

    public static List<Inbound> search(SearchFilters searchFilters, int LIMIT, int OFFSET) {
        List<Inbound> inbounds = new ArrayList<>();

        String sql = "SELECT inbounds.item_id AS item_id, inbounds.id AS inbound_id,"
                + " inbounds.quantity, u.id AS unit_id, u.name AS unit_name, s.id AS source_id, s.information AS source_information,"
                + " inbounds.date, i.name AS item_name, i.specification AS item_specs"
                + " FROM inbounds JOIN items AS i ON inbounds.item_id = i.id"
                + " JOIN quantity_unit AS u ON i.unit_id = u.id"
                + " JOIN source AS s ON s.id = inbounds.source_id"
                + formulateSearchFilters(searchFilters)
                + " ORDER BY inbounds.date ASC, inbounds.id ASC"
                + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p;
            p = con.prepareStatement(sql);
            PreparedStatementWrapper preparedStatementWrapper
                    = formulateSearchPreparedStatement(searchFilters, new PreparedStatementWrapper(p));
            int parameterIndex = preparedStatementWrapper.getParameterIndex();
            p.setInt(++parameterIndex, OFFSET);
            p.setInt(++parameterIndex, LIMIT);

            try (ResultSet result = p.executeQuery()) {
                while (result.next()) {
                    Inbound inbound = new Inbound();
                    Item item = new Item();
                    item.setId(result.getInt("item_id"));
                    item.setName(result.getString("item_name"));
                    item.setSpecification(result.getString("item_specs"));
                    QuantityUnit quantityUnit = new QuantityUnit();
                    quantityUnit.setId(result.getInt("unit_id"));
                    quantityUnit.setName(result.getString("unit_name"));
                    item.setQuantityUnit(quantityUnit);
                    inbound.setId(result.getInt("inbound_id"));
                    inbound.setItem(item);
                    Source source = new Source();
                    source.setId(result.getInt("source_id"));
                    source.setName(result.getString("source_information"));
                    inbound.setSource(source);
                    inbound.setQuantity(result.getBigDecimal("quantity"));
                    inbound.setDate(result.getDate("date").toLocalDate());
                    inbounds.add(inbound);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
        }
        return inbounds;
    }

    public static int searchResultRowsCount(SearchFilters searchFilters) {
        int searchResultRowsCount = 0;

        String sql = "SELECT COUNT(inbounds.id) AS search_result_rows_count"
                + " FROM inbounds JOIN items AS i"
                + " ON inbounds.item_id = i.id"
                + formulateSearchFilters(searchFilters);

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p;
            p = con.prepareStatement(sql);
            formulateSearchPreparedStatement(searchFilters, new PreparedStatementWrapper(p));
            try (ResultSet result = p.executeQuery()) {
                while (result.next()) {
                    searchResultRowsCount = result.getInt("search_result_rows_count");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
        }
        return searchResultRowsCount;
    }

    public static boolean update(Inbound inbound) {
        int update = 0;

        String sql = "UPDATE inbounds"
                + " SET quantity = ?, source_id = ?, date = ?"
                + " WHERE id = ?";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql);
            p.setBigDecimal(1, inbound.getQuantity());
            p.setInt(2, inbound.getSource().getId());
            p.setObject(3, java.sql.Date.valueOf(inbound.getDate()));
            p.setInt(4, inbound.getId());
            update = p.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDInbounds.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (update > 0);
    }

    public static boolean delete(Inbound inbound) {
        int delete = 0;

        String sql = "DELETE FROM inbounds WHERE id = ?";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql);
            p.setInt(1, inbound.getId());
            delete = p.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDInbounds.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (delete > 0);
    }

}
