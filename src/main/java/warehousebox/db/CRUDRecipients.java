/*
 * The MIT License
 *
 * Copyright 2025 Saleh.
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
import warehousebox.db.model.Recipient;

/**
 *
 * @author Saleh
 */
public class CRUDRecipients {

    private static String[] searchedWords;
    private static int wordsLength;

    public static Recipient create(Recipient recipient) {
        String sql = "INSERT INTO recipients (name) VALUES (?)";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, recipient.getName());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    recipient.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating recipient failed, no ID obtained.");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDRecipients.class.getName()).log(Level.SEVERE, null, ex);
        }
        return recipient;
    }

    public static boolean isExist(Recipient recipient) {
        boolean exist = false;
        String sql = "SELECT COUNT(name) AS count FROM recipients WHERE name = ?";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement isExistStatement = con.prepareStatement(sql);
            isExistStatement.setString(1, recipient.getName());

            try (ResultSet result = isExistStatement.executeQuery()) {
                if (result.next()) {
                    exist = result.getInt("count") == 1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDRecipients.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exist;
    }

    public static boolean isInUse(Recipient recipient) {
        boolean isUsed = false;
        String sql = "SELECT id FROM outbounds WHERE recipient_id = ? FETCH FIRST 1 ROW ONLY";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql);
            p.setInt(1, recipient.getId());

            try (ResultSet result = p.executeQuery()) {
                while (result.next()) {
                    isUsed = true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDRecipients.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isUsed;
    }

    private static String formulateSearchFilters(String searchQuery) {
        String sqlFilter = " WHERE ";

        if (searchQuery.isBlank()) {
            sqlFilter = "";
            return sqlFilter;
        }

        searchedWords = QueryWordsProcessor.getArrayOfWords(searchQuery);
        wordsLength = searchedWords.length;

        String dbQuerySQL = "name LIKE ?";
        sqlFilter += wordsLength > 1 ? "(" : "";
        for (var i = 0; i < wordsLength; i++) {
            sqlFilter += dbQuerySQL;
            sqlFilter += (wordsLength > 1 && i == 0) ? ")" : "";
            sqlFilter += (i > 0) ? ")" : "";
            sqlFilter += (i < (wordsLength - 1)) ? " AND (" : "";
        }
        return sqlFilter;
    }

    private static PreparedStatementWrapper formulateSearchPreparedStatement(String searchQuery, PreparedStatementWrapper preparedStatementWrapper) throws SQLException {
        PreparedStatement p = preparedStatementWrapper.getPreparedStatement();
        if (searchQuery.isBlank()) {
            return preparedStatementWrapper;
        } else {
            for (int i = 0; i < wordsLength; i++) {
                p.setString(preparedStatementWrapper.incrementParameterIndex(), "%" + searchedWords[i] + "%");
            }
        }
        return preparedStatementWrapper;
    }

    public static int searchResultRowsCount(String query) {
        int searchResultRowsCount = 0;
        String sql = "SELECT COUNT(id) AS search_result_rows_count"
                + " FROM recipients" + formulateSearchFilters(query);

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p;
            p = con.prepareStatement(sql);
            formulateSearchPreparedStatement(query, new PreparedStatementWrapper(p));

            try (ResultSet result = p.executeQuery()) {
                while (result.next()) {
                    searchResultRowsCount = result.getInt("search_result_rows_count");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return searchResultRowsCount;
    }

    public static List<Recipient> search(String query, int LIMIT, int OFFSET) {
        List<Recipient> recipients = new ArrayList<>();
        String sql = "SELECT * FROM recipients" + formulateSearchFilters(query)
                + " ORDER BY name ASC"
                + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql);
            PreparedStatementWrapper preparedStatementWrapper
                    = formulateSearchPreparedStatement(query, new PreparedStatementWrapper(p));
            int parameterIndex = preparedStatementWrapper.getParameterIndex();
            p.setInt(++parameterIndex, OFFSET);
            p.setInt(++parameterIndex, LIMIT);

            try (ResultSet result = p.executeQuery()) {
                while (result.next()) {
                    Recipient recipient = new Recipient();
                    recipient.setId(result.getInt("id"));
                    recipient.setName(result.getString("name"));
                    recipients.add(recipient);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return recipients;
    }

    public static boolean update(Recipient recipient) {
        int update = 0;

        String sql = "UPDATE recipients SET name = ? WHERE id =?";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, recipient.getName());
            p.setInt(2, recipient.getId());
            update = p.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDRecipients.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (update > 0);
    }

    public static boolean delete(Recipient recipient) {
        int delete = 0;
        String sql = "DELETE FROM recipients WHERE id = ?";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql);
            p.setInt(1, recipient.getId());
            delete = p.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDRecipients.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (delete > 0);
    }
}
