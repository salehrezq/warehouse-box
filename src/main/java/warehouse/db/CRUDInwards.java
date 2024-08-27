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
package warehouse.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import warehouse.db.model.Inward;
import warehouse.db.model.Item;
import warehouse.db.model.QuantityUnit;
import warehouse.db.model.Source;
import warehouse.panel.inwards.SearchFilters;

/**
 *
 * @author Saleh
 */
public class CRUDInwards {

    public static Inward create(Inward inward) {
        String sql = "INSERT INTO inwards (item_id, quantity, date, source_id) VALUES (?, ?, ?, ?)";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setInt(1, inward.getItem().getId());
            p.setBigDecimal(2, inward.getQuantity());
            p.setObject(3, java.sql.Date.valueOf(inward.getDate()));
            p.setInt(4, inward.getSource().getId());
            p.executeUpdate();

            try (ResultSet generatedKeys = p.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    inward.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Obtaining inward ID failed.");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDInwards.class.getName()).log(Level.SEVERE, null, ex);
        }
        return inward;
    }

    private static String formulateSearchFilters(SearchFilters searchFilters) {
        String sqlFilter = " WHERE";
        boolean isSearchisQueryBlank = searchFilters.getSearchQuery().isBlank();
        boolean isCodeFilter = searchFilters.isCodeFilter();
        boolean isNameFilter = searchFilters.isNameFilter();
        boolean isSpecificationFilter = searchFilters.isSpecificationFilter();
        boolean isDateRangeFilter = searchFilters.isEnabledDateRangeFilter();
        boolean isSourceFilter = searchFilters.isSourceFilter();
        boolean isAnyFilterOn = isCodeFilter || isNameFilter || isSpecificationFilter || isSourceFilter;

        if ((!isAnyFilterOn || isSearchisQueryBlank) && !(isDateRangeFilter || isSourceFilter)) {
            sqlFilter = "";
            return sqlFilter;
        }
        if (isDateRangeFilter) {
            sqlFilter += " (date >= ? AND date <= ?)";
            if (isCodeFilter || isNameFilter || isSpecificationFilter || isSourceFilter) {
                sqlFilter += " AND";
            }
        }
        if (isSourceFilter) {
            sqlFilter += " source_id = ?";
            if (isCodeFilter || isNameFilter || isSpecificationFilter) {
                sqlFilter += " AND";
            }
        }
        if (isCodeFilter) {
            sqlFilter += " i.id = ?";
            return sqlFilter;
        }
        if (isNameFilter) {
            sqlFilter += " (i.name LIKE ?";
            sqlFilter += (isSpecificationFilter) ? " OR" : ")";
        }
        if (isSpecificationFilter) {
            sqlFilter += isNameFilter ? "" : "(";
            sqlFilter += " i.specification LIKE ?)";
        }
        return sqlFilter;
    }

    private static PreparedStatementWrapper formulateSearchPreparedStatement(SearchFilters searchFilters, PreparedStatementWrapper preparedStatementWrapper) throws SQLException {
        String searchQuery = searchFilters.getSearchQuery();
        boolean isCodeFilter = searchFilters.isCodeFilter();
        boolean isNameFilter = searchFilters.isNameFilter();
        boolean isSpecificationFilter = searchFilters.isSpecificationFilter();
        boolean isDateRangeFilter = searchFilters.isEnabledDateRangeFilter();
        boolean isSourceFilter = searchFilters.isSourceFilter();
        PreparedStatement p = preparedStatementWrapper.getPreparedStatement();

        boolean isAnyFilterOn = isCodeFilter || isNameFilter || isSpecificationFilter || isSourceFilter;

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
        if (isCodeFilter) {
            p.setInt(preparedStatementWrapper.incrementParameterIndex(), Integer.parseInt(searchQuery));
        }
        if (isNameFilter) {
            p.setString(preparedStatementWrapper.incrementParameterIndex(), "%" + searchQuery + "%");
        }
        if (isSpecificationFilter) {
            p.setString(preparedStatementWrapper.incrementParameterIndex(), "%" + searchQuery + "%");
        }
        return preparedStatementWrapper;
    }

    public static List<Inward> search(SearchFilters searchFilters, int LIMIT, int OFFSET) {
        List<Inward> inwards = new ArrayList<>();

        String sql = "SELECT inwards.item_id AS item_id, inwards.id AS inward_id,"
                + " inwards.quantity, u.id AS unit_id ,u.name AS unit_name, s.id AS source_id, s.information AS source_information,"
                + " inwards.date, i.name AS item_name, i.specification AS item_specs"
                + " FROM inwards JOIN items AS i ON inwards.item_id = i.id"
                + " JOIN quantity_unit AS u ON i.unit_id = u.id"
                + " JOIN source AS s ON s.id = inwards.source_id"
                + formulateSearchFilters(searchFilters)
                + " ORDER BY inwards.date ASC, inwards.id ASC"
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
                    Inward inward = new Inward();
                    Item item = new Item();
                    item.setId(result.getInt("item_id"));
                    item.setName(result.getString("item_name"));
                    item.setSpecification(result.getString("item_specs"));
                    QuantityUnit quantityUnit = new QuantityUnit();
                    quantityUnit.setId(result.getInt("unit_id"));
                    quantityUnit.setName(result.getString("unit_name"));
                    item.setQuantityUnit(quantityUnit);
                    inward.setId(result.getInt("inward_id"));
                    inward.setItem(item);
                    Source source = new Source();
                    source.setId(result.getInt("source_id"));
                    source.setName(result.getString("source_information"));
                    inward.setSource(source);
                    inward.setQuantity(result.getBigDecimal("quantity"));
                    inward.setDate(result.getDate("date").toLocalDate());
                    inwards.add(inward);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
        }
        return inwards;
    }

    public static int searchResultRowsCount(SearchFilters searchFilters) {
        int searchResultRowsCount = 0;

        String sql = "SELECT COUNT(inwards.id) AS search_result_rows_count"
                + " FROM inwards JOIN items AS i"
                + " ON inwards.item_id = i.id"
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

    public static boolean update(Inward inward) {
        int update = 0;

        String sql = "UPDATE inwards"
                + " SET quantity = ?, source_id = ?, date = ?"
                + " WHERE id = ?";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql);
            p.setBigDecimal(1, inward.getQuantity());
            p.setInt(2, inward.getSource().getId());
            p.setObject(3, java.sql.Date.valueOf(inward.getDate()));
            p.setInt(4, inward.getId());
            update = p.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDInwards.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (update > 0);
    }

    public static boolean delete(Inward inward) {
        int delete = 0;

        String sql = "DELETE FROM inwards WHERE id = ?";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql);
            p.setInt(1, inward.getId());
            delete = p.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDInwards.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (delete > 0);
    }

}
