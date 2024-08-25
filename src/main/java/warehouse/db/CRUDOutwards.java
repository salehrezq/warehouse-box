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
import warehouse.db.model.Item;
import warehouse.db.model.Outward;
import warehouse.db.model.QuantityUnit;
import warehouse.db.model.Recipient;
import warehouse.panel.outwards.SearchFilters;

/**
 *
 * @author Saleh
 */
public class CRUDOutwards {

    private static Connection con;

    public static Outward create(Outward outward) {
        String sql = "INSERT INTO outwards (item_id, quantity, recipient_id, for, date) VALUES (?, ?, ?, ?, ?)";
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setInt(1, outward.getItem().getId());
            p.setBigDecimal(2, outward.getQuantity());
            p.setInt(3, outward.getRecipient().getId());
            p.setString(4, outward.getUsedFor());
            p.setObject(5, outward.getDate());
            p.executeUpdate();

            try (ResultSet generatedKeys = p.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    outward.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Obtaining outward ID failed.");
                }
            }
            con.commit();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDOutwards.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return outward;
    }

    private static String formulateSearchFilters(SearchFilters searchFilters) {
        String sqlFilter = " WHERE";
        boolean isSearchisQueryBlank = searchFilters.getSearchQuery().isBlank();
        boolean isCodeFilter = searchFilters.isCodeFilter();
        boolean isNameFilter = searchFilters.isNameFilter();
        boolean isSpecificationFilter = searchFilters.isSpecificationFilter();
        boolean isRecipientFilter = searchFilters.isRecipientFilter();
        boolean isDateRangeFilter = searchFilters.isEnabledDateRangeFilter();
        boolean isAnyFilterOn = isCodeFilter || isNameFilter || isSpecificationFilter || isRecipientFilter;

        if ((!isAnyFilterOn || isSearchisQueryBlank) && !(isDateRangeFilter || isRecipientFilter)) {
            sqlFilter = "";
            return sqlFilter;
        }
        if (isDateRangeFilter) {
            sqlFilter += " (date >= ? AND date <= ?)";
            if (isCodeFilter || isNameFilter || isSpecificationFilter || isRecipientFilter) {
                sqlFilter += " AND";
            }
        }
        if (isRecipientFilter) {
            sqlFilter += " recipient_id = ?";
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
        boolean isRecipientFilter = searchFilters.isRecipientFilter();
        boolean isDateRangeFilter = searchFilters.isEnabledDateRangeFilter();
        PreparedStatement p = preparedStatementWrapper.getPreparedStatement();

        boolean isAnyFilterOn = isCodeFilter || isNameFilter || isSpecificationFilter || isRecipientFilter;

        if ((!isAnyFilterOn || searchQuery.isBlank()) && !(isDateRangeFilter || isRecipientFilter)) {
            return preparedStatementWrapper;
        }
        if (isDateRangeFilter) {
            p.setObject(preparedStatementWrapper.incrementParameterIndex(), searchFilters.getDateRangeStart());
            p.setObject(preparedStatementWrapper.incrementParameterIndex(), searchFilters.getDateRangeEnd());
        }
        if (isRecipientFilter) {
            p.setInt(preparedStatementWrapper.incrementParameterIndex(), searchFilters.getRecipient().getId());
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

    public static List<Outward> search(SearchFilters searchFilters, int LIMIT, int OFFSET) {
        List<Outward> outwards = new ArrayList<>();
        try {
            String sql = "SELECT o.id AS outward_id, o.item_id AS item_id,"
                    + " o.quantity, u.id AS qunit_id, u.name AS qunit_name,"
                    + " r.id AS recipient_id, r.name AS recipient_name, o.for,"
                    + " o.date, i.name AS item_name, i.specification AS item_specs"
                    + " FROM outwards AS o JOIN items AS i JOIN quantity_unit AS u JOIN recipients AS r"
                    + " ON (o.item_id = i.id) AND (i.unit_id = u.id) AND (r.id = o.recipient_id)"
                    + formulateSearchFilters(searchFilters)
                    + " ORDER BY o.date ASC, o.id ASC"
                    + " LIMIT ? OFFSET ?";
            con = Connect.getConnection();
            PreparedStatement p;
            p = con.prepareStatement(sql);
            PreparedStatementWrapper preparedStatementWrapper
                    = formulateSearchPreparedStatement(searchFilters, new PreparedStatementWrapper(p));
            int parameterIndex = preparedStatementWrapper.getParameterIndex();
            p.setInt(++parameterIndex, LIMIT);
            p.setInt(++parameterIndex, OFFSET);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                Outward outward = new Outward();
                Item item = new Item();
                item.setId(result.getInt("item_id"));
                item.setName(result.getString("item_name"));
                item.setSpecification(result.getString("item_specs"));
                QuantityUnit quantityUnit = new QuantityUnit();
                quantityUnit.setId(result.getInt("qunit_id"));
                quantityUnit.setName(result.getString("qunit_name"));
                item.setQuantityUnit(quantityUnit);
                outward.setId(result.getInt("outward_id"));
                outward.setItem(item);
                outward.setQuantity(result.getBigDecimal("quantity"));
                Recipient recipient = new Recipient();
                recipient.setId(result.getInt("recipient_id"));
                recipient.setName(result.getString("recipient_name"));
                outward.setRecipient(recipient);
                outward.setDate(result.getDate("date").toLocalDate());
                outward.setUsedFor(result.getString("for"));
                outwards.add(outward);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDOutwards.class.getName()).log(Level.SEVERE, null, ex);
        }
        return outwards;
    }

    public static int searchResultRowsCount(SearchFilters searchFilters) {
        int searchResultRowsCount = 0;
        try {
            String sql = "SELECT COUNT(outwards.id) AS search_result_rows_count"
                    + " FROM outwards JOIN items AS i"
                    + " ON outwards.item_id = i.id"
                    + formulateSearchFilters(searchFilters);

            con = Connect.getConnection();
            PreparedStatement p;
            p = con.prepareStatement(sql);
            formulateSearchPreparedStatement(searchFilters, new PreparedStatementWrapper(p));
            ResultSet result = p.executeQuery();
            while (result.next()) {
                searchResultRowsCount = result.getInt("search_result_rows_count");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDOutwards.class.getName()).log(Level.SEVERE, null, ex);
        }
        return searchResultRowsCount;
    }

    public static boolean update(Outward outward) {
        int update = 0;
        String sql = "UPDATE outwards"
                + " SET quantity = ?, recipient_id = ?, for = ?, date = ?"
                + " WHERE id = ?";
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql);
            p.setBigDecimal(1, outward.getQuantity());
            p.setInt(2, outward.getRecipient().getId());
            p.setString(3, outward.getUsedFor());
            p.setObject(4, outward.getDate());
            p.setInt(5, outward.getId());
            update = p.executeUpdate();
            con.commit();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDOutwards.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return (update > 0);
    }

    public static boolean delete(Outward outward) {
        int delete = 0;
        String sql = "DELETE FROM outwards WHERE id = ?";
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql);
            p.setInt(1, outward.getId());
            delete = p.executeUpdate();
            con.commit();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDOutwards.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return (delete > 0);
    }

}
