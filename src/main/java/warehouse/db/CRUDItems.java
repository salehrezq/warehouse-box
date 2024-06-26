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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import warehouse.db.model.Item;
import warehouse.db.model.ItemMeta;
import warehouse.db.model.QuantityUnit;
import warehouse.panel.items.SearchFilters;

/**
 *
 * @author Saleh
 */
public class CRUDItems {

    private static Connection con;
    private static int OFFSET = 0;
    private static final int LIMIT = 20;
    private static Map<String, Boolean> searchFilters;

    public static Item create(Item item) {
        String sqlCreateItem = "INSERT INTO items (`name`, `specification`, `unit_id`) VALUES (?, ?, ?)";
        con = Connect.getConnection();
        try {
            PreparedStatement createItemsStatement = con.prepareStatement(sqlCreateItem, Statement.RETURN_GENERATED_KEYS);
            createItemsStatement.setString(1, item.getName());
            createItemsStatement.setString(2, item.getSpecification());
            createItemsStatement.setInt(3, item.getQuantityUnit().getId());
            createItemsStatement.executeUpdate();

            try (ResultSet generatedKeys = createItemsStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            con.commit();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return item;
    }

    public static ArrayList<Item> getAll() {

        ArrayList<Item> items = new ArrayList<>();

        try {
            String sql = "SELECT * FROM `items` ORDER BY `name` ASC";
            con = Connect.getConnection();
            PreparedStatement p;
            p = con.prepareStatement(sql);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                Item item = new Item();
                item.setId(result.getInt("id"));
                item.setName(result.getString("name"));
                item.setSpecification(result.getString("specification"));
                item.setQuantityUnit((QuantityUnit) CRUDListable.getById(new QuantityUnit(), result.getInt("unit_id")));
                items.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
        }
        return items;
    }

    public static int getBalance(int itemId) {
        int itemBalance = 0;
        try {
            String sql = "SELECT "
                    + " (COALESCE("
                    + " (SELECT sum(i.quantity)"
                    + " FROM inwards i"
                    + " WHERE i.item_id = ?),0)"
                    + " -"
                    + " COALESCE("
                    + " (SELECT sum(o.quantity)"
                    + " FROM outwards o"
                    + " WHERE o.item_id = ?),"
                    + " 0)) balance";

            con = Connect.getConnection();
            PreparedStatement p;
            p = con.prepareStatement(sql);
            p.setInt(1, itemId);
            p.setInt(2, itemId);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                itemBalance = result.getInt("balance");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
        }
        return itemBalance;
    }

    private static String formulateFilters(Map<String, Boolean> searchFilters) {
        String sqlFilter = " WHERE";
        if (searchFilters != null) {
            boolean boolCodeFilter = searchFilters.get("code");
            if (boolCodeFilter) {
                sqlFilter += " it.`id` = ?";
                return sqlFilter;
            }
            boolean boolNameFilter = searchFilters.get("name");
            boolean boolSpecificationFilter = searchFilters.get("specification");
            if (boolNameFilter) {
                sqlFilter += " it.`name` LIKE ?";
                if (boolSpecificationFilter) {
                    sqlFilter += " OR";
                }
            }
            if (boolSpecificationFilter) {
                sqlFilter += " it.`specification` LIKE ?";
            }
        }
        return sqlFilter;
    }

    private static String formulateSearchFilters(SearchFilters searchFilters) {
        String sqlFilter = " WHERE";
        boolean isSearchisQueryBlank = searchFilters.getSearchQuery().isBlank();
        boolean isCodeFilter = searchFilters.isCodeFilter();
        boolean isNameFilter = searchFilters.isNameFilter();
        boolean isSpecificationFilter = searchFilters.isSpecificationFilter();

        boolean isAnyFilterOn = isCodeFilter || isNameFilter || isSpecificationFilter;

        if (!isAnyFilterOn || isSearchisQueryBlank) {
            sqlFilter = "";
            return sqlFilter;
        }
        if (isCodeFilter) {
            sqlFilter += " it.`id` = ?";
            return sqlFilter;
        }
        if (isNameFilter) {
            sqlFilter += " (it.`name` LIKE ?";
            sqlFilter += (isSpecificationFilter) ? " OR" : ")";
        }
        if (isSpecificationFilter) {
            sqlFilter += isNameFilter ? "" : "(";
            sqlFilter += " it.`specification` LIKE ?)";
        }
        return sqlFilter;
    }

    private static PreparedStatementWrapper formulateSearchPreparedStatement(SearchFilters searchFilters, PreparedStatementWrapper preparedStatementWrapper) throws SQLException {
        String searchQuery = searchFilters.getSearchQuery();
        boolean isCodeFilter = searchFilters.isCodeFilter();
        boolean isNameFilter = searchFilters.isNameFilter();
        boolean isSpecificationFilter = searchFilters.isSpecificationFilter();
        PreparedStatement p = preparedStatementWrapper.getPreparedStatement();

        boolean isAnyFilterOn = isCodeFilter || isNameFilter || isSpecificationFilter;

        if (!isAnyFilterOn || searchQuery.isBlank()) {
            return preparedStatementWrapper;
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

    public static List<ItemMeta> search(SearchFilters searchFilters, int LIMIT, int OFFSET) {
        List<ItemMeta> itemsMeta = new ArrayList<>();
        try {
            String sql = " SELECT it.id , it.`name`, it.specification,"
                    + " ("
                    + " COALESCE((SELECT SUM(i.quantity)"
                    + " FROM inwards AS i"
                    + " WHERE i.item_id = it.id),0)"
                    + " -"
                    + " COALESCE((SELECT SUM(o.quantity)"
                    + " FROM outwards AS o"
                    + " WHERE o.item_id = it.id),0)"
                    + " ) AS balance, u.id AS unit_id, u.`name` AS unit"
                    + " "
                    + " FROM `items` AS it JOIN `quantity_unit` AS u"
                    + " ON it.unit_id = u.id"
                    + formulateSearchFilters(searchFilters)
                    + " ORDER BY `id` ASC"
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
                ItemMeta itemMeta = new ItemMeta();
                itemMeta.setId(result.getInt("id"));
                itemMeta.setName(result.getString("name"));
                itemMeta.setSpecification(result.getString("specification"));
                itemMeta.setBalance(result.getBigDecimal("balance"));
                QuantityUnit quantityUnit = new QuantityUnit();
                quantityUnit.setId(result.getInt("unit_id"));
                quantityUnit.setName(result.getString("unit"));
                itemMeta.setQuantityUnit(quantityUnit);
                itemsMeta.add(itemMeta);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
        }
        return itemsMeta;
    }

    public static int searchResultRowsCount(SearchFilters searchFilters) {
        int searchResultRowsCount = 0;
        try {
            String sql = "SELECT COUNT(id) AS search_result_rows_count"
                    + " FROM `items` AS `it`"
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
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
        }
        return searchResultRowsCount;
    }

    public static int getRecordsCount() {
        int numberOfRows = 0;
        String sql = "SELECT COUNT(*) AS `items_rows_count` FROM items";
        con = Connect.getConnection();
        try {
            PreparedStatement p;
            p = con.prepareStatement(sql);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                numberOfRows = result.getInt("items_rows_count");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numberOfRows;
    }

    public static boolean update(Item item) {
        int update = 0;
        String sql = "UPDATE items"
                + " SET name = ?, specification = ?, unit_id = ?"
                + " WHERE id = ?";
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, item.getName());
            p.setString(2, item.getSpecification());
            p.setInt(3, item.getQuantityUnit().getId());
            p.setInt(4, item.getId());
            update = p.executeUpdate();
            con.commit();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return (update > 0);
    }

    public static BufferedImage toBufferedImage(byte[] photo) {
        BufferedImage img = null;
        if (photo != null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(photo);
            try {
                img = ImageIO.read(bis);
            } catch (IOException ex) {
                Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return img;
    }

    public static boolean isInUse(ItemMeta itemMeta) {
        boolean isUsed = false;

        String sql = "SELECT it.id"
                + " FROM items it JOIN inwards i JOIN outwards o"
                + " WHERE (it.id = ?) AND ((it.id = i.item_id) OR (it.id = o.item_id))"
                + " LIMIT 1";

        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql);
            p.setInt(1, itemMeta.getId());
            ResultSet result = p.executeQuery();
            while (result.next()) {
                isUsed = true;
            }
            con.commit();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return isUsed;
    }

    public static boolean delete(ItemMeta itemMeta) {
        int delete = 0;
        String sql = "DELETE FROM items WHERE `id` = ?";
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql);
            p.setInt(1, itemMeta.getId());
            delete = p.executeUpdate();
            con.commit();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return (delete > 0);
    }
}
