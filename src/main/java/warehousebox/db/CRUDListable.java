/*
 * The MIT License
 *
 * Copyright 2023 Saleh.
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import warehousebox.db.model.Recipient;
import warehousebox.utility.singularlisting.Listable;

/**
 *
 * @author Saleh
 */
public class CRUDListable {

    public static boolean create(Listable listable) {
        int insert = 0;
        String sql = "INSERT INTO "
                + listable.getDBEntityName()
                + " (" + listable.getDBAttributeName() + ") VALUES (?)";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, listable.getName());
            insert = p.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return insert > 0;
    }

    public static ArrayList<Listable> getAll(Listable listableImplementation) {
        ArrayList<Listable> listables = new ArrayList<>();
        Listable listableInstance = null;
        String sqlSelectStatement = "SELECT * FROM "
                + listableImplementation.getDBEntityName()
                + " ORDER BY " + listableImplementation.getDBAttributeName() + " ASC";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sqlSelectStatement);

            try (ResultSet result = p.executeQuery()) {
                while (result.next()) {
                    listableInstance = listableImplementation.getNewInstance();
                    listableInstance.setId(result.getInt("id"));
                    listableInstance.setName(result.getString(listableImplementation.getDBAttributeName()));
                    listables.add(listableInstance);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listables;
    }

    public static Listable getById(Listable listableImplementation, int id) {
        Listable listable = null;
        String sql = "SELECT * FROM "
                + listableImplementation.getDBEntityName()
                + " WHERE id = " + id;

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql);

            try (ResultSet result = p.executeQuery()) {
                while (result.next()) {
                    listable = listableImplementation.getNewInstance();
                    listable.setId(result.getInt("id"));
                    listable.setName(result.getString(listable.getDBAttributeName()));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listable;
    }

    public static boolean isExist(Listable listable) {
        boolean exist = false;
        String sql = "SELECT COUNT(" + listable.getDBAttributeName() + ") AS count"
                + " FROM " + listable.getDBEntityName()
                + " WHERE " + listable.getDBAttributeName() + " = ?";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement isExistStatement = con.prepareStatement(sql);
            isExistStatement.setString(1, listable.getName());

            try (ResultSet result = isExistStatement.executeQuery()) {
                if (result.next()) {
                    exist = result.getInt("count") == 1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exist;
    }

    private static String formulateSearchFilters(Listable listableImplementation, String[] searchedWords) {
        String sqlFilter = " WHERE ";
        int wordsLength = searchedWords.length;

        if (wordsLength < 1 || wordsLength == 1 && searchedWords[0].isEmpty()) {
            sqlFilter = "";
            return sqlFilter;
        }

        String dbQuerySQL = listableImplementation.getDBAttributeName() + " LIKE ?";
        sqlFilter += wordsLength > 1 ? "(" : "";
        for (var i = 0; i < wordsLength; i++) {
            sqlFilter += dbQuerySQL;
            sqlFilter += (wordsLength > 1 && i == 0) ? ")" : "";
            sqlFilter += (i > 0) ? ")" : "";
            sqlFilter += (i < (wordsLength - 1)) ? " AND (" : "";
        }
        return sqlFilter;
    }

    private static PreparedStatementWrapper formulateSearchPreparedStatement(String[] searchedWords, PreparedStatementWrapper preparedStatementWrapper) throws SQLException {
        PreparedStatement p = preparedStatementWrapper.getPreparedStatement();
        if (searchedWords.length < 1 || searchedWords.length == 1 && searchedWords[0].isEmpty()) {
            return preparedStatementWrapper;
        } else {
            for (int i = 0; i < searchedWords.length; i++) {
                p.setString(preparedStatementWrapper.incrementParameterIndex(), "%" + searchedWords[i] + "%");
            }
        }
        return preparedStatementWrapper;
    }

    public static int searchResultRowsCount(Listable listableImplementation, String[] searchedWords) {
        int searchResultRowsCount = 0;
        String sql = "SELECT COUNT(id) AS search_result_rows_count"
                + " FROM " + "" + listableImplementation.getDBEntityName() + ""
                + formulateSearchFilters(listableImplementation, searchedWords);

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p;
            p = con.prepareStatement(sql);
            formulateSearchPreparedStatement(searchedWords, new PreparedStatementWrapper(p));

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

    public static List<Listable> search(Listable listableImplementation, String[] query, int LIMIT, int OFFSET) {
        List<Listable> listables = new ArrayList<>();
        String sql = "SELECT *"
                + " FROM " + "" + listableImplementation.getDBEntityName() + ""
                + formulateSearchFilters(listableImplementation, query)
                + " ORDER BY " + "" + listableImplementation.getDBAttributeName() + "" + " ASC"
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
                    Listable listableInstance = listableImplementation.getNewInstance();

                    /**
                     * Exclude the entry "Scrapper" of the Recipient model type.
                     * Because it is reserved for the scrap category of
                     * outbounds.
                     */
                    if ((listableImplementation instanceof Recipient)
                            && ((result.getString(listableImplementation.getDBAttributeName()).equals("Scrapper")))) {
                        continue;
                    }
                    listableInstance.setId(result.getInt("id"));
                    listableInstance.setName(result.getString(listableImplementation.getDBAttributeName()));
                    listables.add(listableInstance);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listables;
    }

    public static ArrayList<Listable> getSearch(Listable listableImplementation, String str) {
        ArrayList<Listable> listables = new ArrayList<>();
        String sqlSelectStatement = "SELECT * FROM " + listableImplementation.getDBEntityName() + " WHERE "
                + listableImplementation.getDBAttributeName()
                + " LIKE '%" + str + "%'";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sqlSelectStatement);

            try (ResultSet result = p.executeQuery()) {
                while (result.next()) {
                    Listable listableInstance = listableImplementation.getNewInstance();
                    listableInstance.setId(result.getInt("id"));
                    listableInstance.setName(result.getString(listableImplementation.getDBAttributeName()));
                    listables.add(listableInstance);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listables;
    }

    public static boolean update(Listable listable) {
        int update = 0;
        String sql = "UPDATE " + "" + listable.getDBEntityName() + ""
                + " SET " + "" + listable.getDBAttributeName() + "" + " = ?"
                + " WHERE id = ?";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, listable.getName());
            p.setInt(2, listable.getId());
            update = p.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (update > 0);
    }

    public static boolean isListableInUse(Listable listableImplementation) {
        boolean isUsed = false;
        String sql = "SELECT id FROM " + "" + listableImplementation.getConsumer().get("table") + ""
                + " WHERE " + "" + listableImplementation.getConsumer().get("column") + " = ?"
                + " FETCH FIRST 1 ROW ONLY";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql);
            p.setInt(1, listableImplementation.getId());

            try (ResultSet result = p.executeQuery()) {
                while (result.next()) {
                    isUsed = true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isUsed;
    }

    public static boolean delete(Listable listableImplementation) {
        int delete = 0;
        String sql = "DELETE FROM " + "" + listableImplementation.getDBEntityName() + ""
                + " WHERE id = ?";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql);
            p.setInt(1, listableImplementation.getId());
            delete = p.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (delete > 0);
    }

}
