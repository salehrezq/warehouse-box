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
package warehouse.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import warehouse.singularlisting.Listable;

/**
 *
 * @author Saleh
 */
public class CRUDListable {

    private static Connection con;

    public static boolean create(Listable listable) {
        int insert = 0;
        String sql = "INSERT INTO "
                + listable.getDBEntityName()
                + " (`" + listable.getDBAttributeName() + "`) VALUES (?)";
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, listable.getName());
            insert = p.executeUpdate();
            con.commit();
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
                + " ORDER BY `" + listableImplementation.getDBAttributeName() + "` ASC";
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sqlSelectStatement);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                listableInstance = listableImplementation.getNewInstance();
                listableInstance.setId(result.getInt("id"));
                listableInstance.setName(result.getString(listableImplementation.getDBAttributeName()));
                listables.add(listableInstance);
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
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                listable = listableImplementation.getNewInstance();
                listable.setId(result.getInt("id"));
                listable.setName(result.getString(listable.getDBAttributeName()));
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listable;
    }

    public static boolean isExist(Listable listable) {
        System.out.println("listable name " + listable.getName());
        boolean exist = false;
        String tempAttibuteAlais = "is_" + listable.getDBEntityName() + "_exist";
        System.out.println("tempAttibuteAlais " + tempAttibuteAlais);
        String sql = "SELECT EXISTS(SELECT " + "`" + listable.getDBAttributeName() + "`"
                + " FROM " + "`" + listable.getDBEntityName() + "`"
                + " WHERE (" + "`" + listable.getDBAttributeName() + "` = ?))"
                + " AS " + tempAttibuteAlais;
        con = Connect.getConnection();
        try {
            PreparedStatement isExistStatement = con.prepareStatement(sql);
            System.out.println(isExistStatement);
            isExistStatement.setString(1, listable.getName());
            ResultSet result = isExistStatement.executeQuery();
            if (result.next()) {
                exist = result.getInt(tempAttibuteAlais) == 1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exist;
    }

    public static boolean isExistExceptThis(Listable listable) {
        boolean exist = false;
        String tempAttibuteAlais = "is_" + listable.getDBEntityName() + "_exist";
        String sql = "SELECT EXISTS(SELECT " + "`" + listable.getDBAttributeName() + "`"
                + " FROM " + "`" + listable.getDBEntityName() + "`"
                + " WHERE (" + "`" + listable.getDBAttributeName() + "` = ? AND `id` NOT IN (?)))"
                + " AS " + tempAttibuteAlais;

        con = Connect.getConnection();
        System.out.println(sql);
        try {
            PreparedStatement isExistStatement = con.prepareStatement(sql);
            isExistStatement.setString(1, listable.getName());
            isExistStatement.setInt(2, listable.getId());
            ResultSet result = isExistStatement.executeQuery();
            if (result.next()) {
                exist = result.getInt(tempAttibuteAlais) == 1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exist;
    }

    private static String formulateSearchFilters(Listable listableImplementation, String query) {
        String sqlFilter = "";
        if (query.isBlank()) {
            return sqlFilter;
        }
        sqlFilter = " WHERE";
        sqlFilter += " `" + listableImplementation.getDBAttributeName() + "`";
        sqlFilter += " LIKE ?";
        return sqlFilter;
    }

    private static PreparedStatementWrapper formulateSearchPreparedStatement(String query, PreparedStatementWrapper preparedStatementWrapper) throws SQLException {
        PreparedStatement p = preparedStatementWrapper.getPreparedStatement();
        if (query.isBlank()) {
            return preparedStatementWrapper;
        }
        p.setString(preparedStatementWrapper.incrementParameterIndex(), "%" + query + "%");
        return preparedStatementWrapper;
    }

    public static int searchResultRowsCount(Listable listableImplementation, String query) {
        int searchResultRowsCount = 0;
        try {
            String sql = "SELECT COUNT(id) AS search_result_rows_count"
                    + " FROM " + "`" + listableImplementation.getDBEntityName() + "`"
                    + formulateSearchFilters(listableImplementation, query);
            con = Connect.getConnection();
            PreparedStatement p;
            p = con.prepareStatement(sql);
            formulateSearchPreparedStatement(query, new PreparedStatementWrapper(p));
            ResultSet result = p.executeQuery();
            while (result.next()) {
                searchResultRowsCount = result.getInt("search_result_rows_count");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return searchResultRowsCount;
    }

    public static List<Listable> search(Listable listableImplementation, String query, int LIMIT, int OFFSET) {
        List<Listable> listables = new ArrayList<>();
        try {
            String sql = "SELECT *"
                    + " FROM " + "`" + listableImplementation.getDBEntityName() + "`"
                    + formulateSearchFilters(listableImplementation, query)
                    + " ORDER BY " + "`" + listableImplementation.getDBAttributeName() + "`" + " ASC"
                    + " LIMIT ? OFFSET ?";

            con = Connect.getConnection();
            PreparedStatement p;
            p = con.prepareStatement(sql);
            PreparedStatementWrapper preparedStatementWrapper
                    = formulateSearchPreparedStatement(query, new PreparedStatementWrapper(p));
            int parameterIndex = preparedStatementWrapper.getParameterIndex();
            p.setInt(++parameterIndex, LIMIT);
            p.setInt(++parameterIndex, OFFSET);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                Listable listableInstance = listableImplementation.getNewInstance();
                listableInstance.setId(result.getInt("id"));
                listableInstance.setName(result.getString(listableImplementation.getDBAttributeName()));
                listables.add(listableInstance);
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
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sqlSelectStatement);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                Listable listableInstance = listableImplementation.getNewInstance();
                listableInstance.setId(result.getInt("id"));
                listableInstance.setName(result.getString(listableImplementation.getDBAttributeName()));
                listables.add(listableInstance);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listables;
    }

    public static boolean update(Listable listable) {
        int update = 0;
        String sql = "UPDATE " + "`" + listable.getDBEntityName() + "`"
                + " SET " + "`" + listable.getDBAttributeName() + "`" + " = ?"
                + " WHERE `id` = ?";
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, listable.getName());
            p.setInt(2, listable.getId());
            update = p.executeUpdate();
            con.commit();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return (update > 0);
    }

    public static boolean isListableInUse(Listable listableImplementation) {
        boolean isUsed = false;
        String sql = "SELECT id FROM " + "`" + listableImplementation.getConsumer().get("table") + "`"
                + " WHERE " + "`" + listableImplementation.getConsumer().get("column") + "` = ?"
                + " LIMIT 1";
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql);
            p.setInt(1, listableImplementation.getId());
            ResultSet result = p.executeQuery();
            while (result.next()) {
                isUsed = true;
            }
            con.commit();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return isUsed;
    }

    public static boolean delete(Listable listableImplementation) {
        int delete = 0;
        String sql = "DELETE FROM " + "`" + listableImplementation.getDBEntityName() + "`"
                + " WHERE `id` = ?";
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql);
            p.setInt(1, listableImplementation.getId());
            delete = p.executeUpdate();
            con.commit();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return (delete > 0);
    }

}
