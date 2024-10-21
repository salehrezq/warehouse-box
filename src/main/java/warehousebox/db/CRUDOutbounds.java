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
import warehousebox.db.model.Item;
import warehousebox.db.model.Outbound;
import warehousebox.db.model.QuantityUnit;
import warehousebox.db.model.Recipient;
import warehousebox.panel.outbounds.SearchFilters;

/**
 *
 * @author Saleh
 */
public class CRUDOutbounds {

    public static Outbound create(Outbound outbound) {
        String sql = "INSERT INTO outbounds (item_id, quantity, recipient_id, used_for, date) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setInt(1, outbound.getItem().getId());
            p.setBigDecimal(2, outbound.getQuantity());
            p.setInt(3, outbound.getRecipient().getId());
            p.setString(4, outbound.getUsedFor());
            p.setObject(5, java.sql.Date.valueOf(outbound.getDate()));
            p.executeUpdate();

            try (ResultSet generatedKeys = p.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    outbound.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Obtaining outbound ID failed.");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDOutbounds.class.getName()).log(Level.SEVERE, null, ex);
        }
        return outbound;
    }

    private static String formulateSearchFilters(SearchFilters searchFilters) {
        String sqlFilter = " WHERE";
        boolean isSearchisQueryBlank = searchFilters.getSearchQuery().isBlank();
        boolean isIdFilter = searchFilters.isIdFilter();
        boolean isNameFilter = searchFilters.isNameFilter();
        boolean isSpecificationFilter = searchFilters.isSpecificationFilter();
        boolean isRecipientFilter = searchFilters.isRecipientFilter();
        boolean isDateRangeFilter = searchFilters.isEnabledDateRangeFilter();
        boolean isAnyFilterOn = isIdFilter || isNameFilter || isSpecificationFilter || isRecipientFilter;

        if ((!isAnyFilterOn || isSearchisQueryBlank) && !(isDateRangeFilter || isRecipientFilter)) {
            sqlFilter = "";
            return sqlFilter;
        }
        if (isDateRangeFilter) {
            sqlFilter += " (date >= ? AND date <= ?)";
            if (isIdFilter || isNameFilter || isSpecificationFilter || isRecipientFilter) {
                sqlFilter += " AND";
            }
        }
        if (isRecipientFilter) {
            sqlFilter += " recipient_id = ?";
            if (isIdFilter || isNameFilter || isSpecificationFilter) {
                sqlFilter += " AND";
            }
        }
        if (isIdFilter) {
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
        boolean isIdFilter = searchFilters.isIdFilter();
        boolean isNameFilter = searchFilters.isNameFilter();
        boolean isSpecificationFilter = searchFilters.isSpecificationFilter();
        boolean isRecipientFilter = searchFilters.isRecipientFilter();
        boolean isDateRangeFilter = searchFilters.isEnabledDateRangeFilter();
        PreparedStatement p = preparedStatementWrapper.getPreparedStatement();

        boolean isAnyFilterOn = isIdFilter || isNameFilter || isSpecificationFilter || isRecipientFilter;

        if ((!isAnyFilterOn || searchQuery.isBlank()) && !(isDateRangeFilter || isRecipientFilter)) {
            return preparedStatementWrapper;
        }
        if (isDateRangeFilter) {
            p.setObject(preparedStatementWrapper.incrementParameterIndex(), java.sql.Date.valueOf(searchFilters.getDateRangeStart()));
            p.setObject(preparedStatementWrapper.incrementParameterIndex(), java.sql.Date.valueOf(searchFilters.getDateRangeEnd()));
        }
        if (isRecipientFilter) {
            p.setInt(preparedStatementWrapper.incrementParameterIndex(), searchFilters.getRecipient().getId());
        }
        if (isIdFilter) {
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

    public static List<Outbound> search(SearchFilters searchFilters, int LIMIT, int OFFSET) {
        List<Outbound> outbounds = new ArrayList<>();

        String sql = "SELECT o.id AS outbound_id, o.item_id AS item_id,"
                + " o.quantity, u.id AS qunit_id, u.name AS qunit_name,"
                + " r.id AS recipient_id, r.name AS recipient_name, o.used_for,"
                + " o.date, i.name AS item_name, i.specification AS item_specs"
                + " FROM outbounds AS o JOIN items AS i ON o.item_id = i.id JOIN quantity_unit AS u ON i.unit_id = u.id JOIN recipients AS r ON r.id = o.recipient_id"
                + formulateSearchFilters(searchFilters)
                + " ORDER BY o.date ASC, o.id ASC"
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
                    Outbound outbound = new Outbound();
                    Item item = new Item();
                    item.setId(result.getInt("item_id"));
                    item.setName(result.getString("item_name"));
                    item.setSpecification(result.getString("item_specs"));
                    QuantityUnit quantityUnit = new QuantityUnit();
                    quantityUnit.setId(result.getInt("qunit_id"));
                    quantityUnit.setName(result.getString("qunit_name"));
                    item.setQuantityUnit(quantityUnit);
                    outbound.setId(result.getInt("outbound_id"));
                    outbound.setItem(item);
                    outbound.setQuantity(result.getBigDecimal("quantity"));
                    Recipient recipient = new Recipient();
                    recipient.setId(result.getInt("recipient_id"));
                    recipient.setName(result.getString("recipient_name"));
                    outbound.setRecipient(recipient);
                    outbound.setDate(result.getDate("date").toLocalDate());
                    outbound.setUsedFor(result.getString("used_for"));
                    outbounds.add(outbound);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDOutbounds.class.getName()).log(Level.SEVERE, null, ex);
        }
        return outbounds;
    }

    public static int searchResultRowsCount(SearchFilters searchFilters) {
        int searchResultRowsCount = 0;

        String sql = "SELECT COUNT(outbounds.id) AS search_result_rows_count"
                + " FROM outbounds JOIN items AS i"
                + " ON outbounds.item_id = i.id"
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
            Logger.getLogger(CRUDOutbounds.class.getName()).log(Level.SEVERE, null, ex);
        }
        return searchResultRowsCount;
    }

    public static boolean update(Outbound outbound) {
        int update = 0;

        String sql = "UPDATE outbounds"
                + " SET quantity = ?, recipient_id = ?, used_for = ?, date = ?"
                + " WHERE id = ?";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql);
            p.setBigDecimal(1, outbound.getQuantity());
            p.setInt(2, outbound.getRecipient().getId());
            p.setString(3, outbound.getUsedFor());
            p.setObject(4, java.sql.Date.valueOf(outbound.getDate()));
            p.setInt(5, outbound.getId());
            update = p.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDOutbounds.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (update > 0);
    }

    public static boolean delete(Outbound outbound) {
        int delete = 0;

        String sql = "DELETE FROM outbounds WHERE id = ?";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql);
            p.setInt(1, outbound.getId());
            delete = p.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDOutbounds.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (delete > 0);
    }

}