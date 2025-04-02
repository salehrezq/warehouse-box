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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import warehousebox.db.model.Item;
import warehousebox.db.model.ItemMeta;
import warehousebox.db.model.QuantityUnit;
import warehousebox.panel.items.SearchFilters;

/**
 *
 * @author Saleh
 */
public class CRUDItems {

    private static String[] searchedWords;
    private static int wordsLength;

    public static Item create(Item item) {
        String sqlCreateItem = "INSERT INTO items (name, specification, unit_id) VALUES (?, ?, ?)";

        try (Connection con = Connect.getConnection()) {
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
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
        }
        return item;
    }

    public static ArrayList<Item> getAll() {
        ArrayList<Item> items = new ArrayList<>();

        String sql = "SELECT * FROM items ORDER BY name ASC";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p;
            p = con.prepareStatement(sql);

            try (ResultSet result = p.executeQuery()) {
                while (result.next()) {
                    Item item = new Item();
                    item.setId(result.getInt("id"));
                    item.setName(result.getString("name"));
                    item.setSpecification(result.getString("specification"));
                    item.setQuantityUnit((QuantityUnit) CRUDListable.getById(new QuantityUnit(), result.getInt("unit_id")));
                    items.add(item);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
        }
        return items;
    }

    public static int getBalance(int itemId) {
        int itemBalance = 0;

        String sql = "SELECT "
                + " (COALESCE("
                + " (SELECT sum(i.quantity)"
                + " FROM inbounds i"
                + " WHERE i.item_id = ?),0)"
                + " -"
                + " COALESCE("
                + " (SELECT sum(o.quantity)"
                + " FROM outbounds o"
                + " WHERE o.item_id = ?),"
                + " 0)) balance";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p;
            p = con.prepareStatement(sql);
            p.setInt(1, itemId);
            p.setInt(2, itemId);

            try (ResultSet result = p.executeQuery()) {
                while (result.next()) {
                    itemBalance = result.getInt("balance");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
        }
        return itemBalance;
    }

    private static String formulateSearchFilters(SearchFilters searchFilters) {
        String sqlFilter = " WHERE ";
        boolean isSearchisQueryBlank = searchFilters.getSearchQuery().length < 1;
        boolean isIdFilter = searchFilters.isIdFilter();
        boolean isNameFilter = searchFilters.isNameFilter();
        boolean isSpecificationFilter = searchFilters.isSpecificationFilter();

        boolean isAnyFilterOn = isIdFilter || isNameFilter || isSpecificationFilter;

        if (!isAnyFilterOn && isSearchisQueryBlank) {
            sqlFilter = "";
            return sqlFilter;
        }
        if (isIdFilter) {
            sqlFilter += "it.id = ?";
            return sqlFilter;
        } else {
            searchedWords = searchFilters.getSearchQuery();
            wordsLength = searchedWords.length;
            String query;
            if (isNameFilter && !isSpecificationFilter) {
                query = "it.name LIKE ?";
            } else if (isSpecificationFilter && !isNameFilter) {
                query = "it.specification LIKE ?";
            } else {
                query = "(it.name || ' ' || it.specification) LIKE ?";
            }

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
        String[] searchQuery = searchFilters.getSearchQuery();
        boolean isIdFilter = searchFilters.isIdFilter();
        boolean isNameFilter = searchFilters.isNameFilter();
        boolean isSpecificationFilter = searchFilters.isSpecificationFilter();
        PreparedStatement p = preparedStatementWrapper.getPreparedStatement();

        boolean isAnyFilterOn = isIdFilter || isNameFilter || isSpecificationFilter;

        if (!isAnyFilterOn && searchQuery.length < 1) {
            return preparedStatementWrapper;
        }
        if (isIdFilter) {
            p.setInt(preparedStatementWrapper.incrementParameterIndex(), Integer.parseInt(searchQuery[0]));
        } else {
            for (int i = 0; i < wordsLength; i++) {
                p.setString(preparedStatementWrapper.incrementParameterIndex(), "%" + searchedWords[i] + "%");
            }
        }
        return preparedStatementWrapper;
    }

    public static List<ItemMeta> search(SearchFilters searchFilters, int LIMIT, int OFFSET) {
        List<ItemMeta> itemsMeta = new ArrayList<>();

        String sql = "SELECT it.id, it.name, it.specification,"
                + " ("
                + "COALESCE((SELECT SUM(i.quantity)"
                + " FROM inbounds AS i"
                + " WHERE i.item_id = it.id),0)"
                + "-"
                + "COALESCE((SELECT SUM(o.quantity)"
                + " FROM outbounds AS o"
                + " WHERE o.item_id = it.id),0)"
                + ") AS balance, u.id AS unit_id, u.name AS unit"
                + " FROM items AS it JOIN quantity_unit AS u"
                + " ON it.unit_id = u.id"
                + formulateSearchFilters(searchFilters)
                + " ORDER BY it.id ASC"
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
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
        }
        return itemsMeta;
    }

    public static int searchResultRowsCount(SearchFilters searchFilters) {
        int searchResultRowsCount = 0;

        String sql = "SELECT COUNT(id) AS search_result_rows_count"
                + " FROM items AS it"
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

    public static boolean update(Item item) {
        int update = 0;

        String sql = "UPDATE items"
                + " SET name = ?, specification = ?, unit_id = ?"
                + " WHERE id = ?";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, item.getName());
            p.setString(2, item.getSpecification());
            p.setInt(3, item.getQuantityUnit().getId());
            p.setInt(4, item.getId());
            update = p.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
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
                + " FROM items it LEFT JOIN inbounds i ON it.id = i.item_id"
                + " LEFT JOIN outbounds o ON it.id = o.item_id"
                + " WHERE (it.id = ?) AND ((it.id = i.item_id) OR (it.id = o.item_id))"
                + " FETCH FIRST 1 ROW ONLY";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql);
            p.setInt(1, itemMeta.getId());

            try (ResultSet result = p.executeQuery()) {
                while (result.next()) {
                    isUsed = true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isUsed;
    }

    public static boolean delete(ItemMeta itemMeta) {
        int delete = 0;

        String sql = "DELETE FROM items WHERE id = ?";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql);
            p.setInt(1, itemMeta.getId());
            delete = p.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (delete > 0);
    }
}
