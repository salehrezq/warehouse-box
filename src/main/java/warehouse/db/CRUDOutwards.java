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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import warehouse.db.model.Outward;
import warehouse.db.model.OutwardMeta;

/**
 *
 * @author Saleh
 */
public class CRUDOutwards {

    private static Connection con;

    public static Outward create(Outward outward) {
        String sql = "INSERT INTO outwards (`item_id`, `quantity`, `recipient_id`, `for`, `date`) VALUES (?, ?, ?, ?, ?)";
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setInt(1, outward.getItemId());
            p.setBigDecimal(2, outward.getQuantity());
            p.setInt(3, outward.getRecipientId());
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

    public static ArrayList<OutwardMeta> getAll() {

        ArrayList<OutwardMeta> outwardMetas = new ArrayList<>();

        try {
            String sql = "SELECT o.id AS outward_id, o.item_id AS item_id,"
                    + " o.quantity, u.name AS unit_name, r.name AS recipient, o.`for`,"
                    + " o.date, i.name AS item_name, i.specification AS item_specs"
                    + " FROM outwards AS o JOIN items AS i JOIN quantity_unit AS u JOIN recipients AS r"
                    + " ON (o.item_id = i.id) AND (i.unit_id = u.id) AND (r.id = o.recipient_id)"
                    + " ORDER BY o.date ASC, o.id ASC;";

            con = Connect.getConnection();
            PreparedStatement p;
            p = con.prepareStatement(sql);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                OutwardMeta outwardMeta = new OutwardMeta();
                outwardMeta.setId(result.getInt("outward_id"));
                outwardMeta.setItemId(result.getInt("item_id"));
                outwardMeta.setQuantity(result.getBigDecimal("quantity"));
                outwardMeta.setUnitName(result.getString("unit_name"));
                outwardMeta.setRecipient(result.getString("recipient"));
                outwardMeta.setUsedFor(result.getString("for"));
                outwardMeta.setDate(result.getDate("date").toLocalDate());
                outwardMeta.setItemName(result.getString("item_name"));
                outwardMeta.setItemSpecification(result.getString("item_specs"));
                outwardMetas.add(outwardMeta);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDOutwards.class.getName()).log(Level.SEVERE, null, ex);
        }
        return outwardMetas;
    }

    private static String formulateFilters(Map<String, Boolean> searchFilters) {
        String sqlFilter = " WHERE";
        if (searchFilters != null) {
            boolean boolCodeFilter = searchFilters.get("code");
            if (boolCodeFilter) {
                sqlFilter += " i.`id` = ?";
                return sqlFilter;
            }
            boolean boolNameFilter = searchFilters.get("name");
            boolean boolSpecificationFilter = searchFilters.get("specification");
            if (boolNameFilter) {
                sqlFilter += " i.`name` LIKE ?";
                if (boolSpecificationFilter) {
                    sqlFilter += " OR";
                }
            }
            if (boolSpecificationFilter) {
                sqlFilter += " i.`specification` LIKE ?";
            }
        }
        return sqlFilter;
    }

    public static List<OutwardMeta> search(String query, Map<String, Boolean> searchFilters, int LIMIT, int OFFSET) {
        List<OutwardMeta> outwardsMeta = new ArrayList<>();
        boolean isFiltersAvailable = (searchFilters != null);
        boolean isAnyFilterOn = isFiltersAvailable
                ? searchFilters.values().stream().anyMatch(filterValue -> filterValue == true) : false;
        boolean isQueryBlank = query.isBlank();
        try {
            String sql = "SELECT o.id AS outward_id, o.item_id AS item_id,"
                    + " o.quantity, u.name AS unit_name, r.name AS recipient, o.`for`,"
                    + " o.date, i.name AS item_name, i.specification AS item_specs"
                    + " FROM outwards AS o JOIN items AS i JOIN quantity_unit AS u JOIN recipients AS r"
                    + " ON (o.item_id = i.id) AND (i.unit_id = u.id) AND (r.id = o.recipient_id)"
                    + ((isQueryBlank || !isAnyFilterOn) ? "" : formulateFilters(searchFilters))
                    + " ORDER BY o.date ASC, o.id ASC"
                    + " LIMIT ? OFFSET ?";

            con = Connect.getConnection();
            PreparedStatement p;
            p = con.prepareStatement(sql);
            int parameterIndex = 0;
            if (!isQueryBlank) {
                for (var filter : searchFilters.entrySet()) {
                    if (filter.getValue() == true) {
                        if (filter.getKey().equals("code")) {
                            p.setInt(++parameterIndex, Integer.parseInt(query));
                        } else {
                            p.setString(++parameterIndex, "%" + query + "%");
                        }
                    }
                }
            }
            p.setInt(++parameterIndex, LIMIT);
            p.setInt(++parameterIndex, OFFSET);
            System.out.println(p);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                OutwardMeta outwardMeta = new OutwardMeta();
                outwardMeta.setId(result.getInt("outward_id"));
                outwardMeta.setItemId(result.getInt("item_id"));
                outwardMeta.setQuantity(result.getBigDecimal("quantity"));
                outwardMeta.setUnitName(result.getString("unit_name"));
                outwardMeta.setRecipient(result.getString("recipient"));
                outwardMeta.setDate(result.getDate("date").toLocalDate());
                outwardMeta.setUsedFor(result.getString("for"));
                outwardMeta.setItemName(result.getString("item_name"));
                outwardMeta.setItemSpecification(result.getString("item_specs"));
                outwardsMeta.add(outwardMeta);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDOutwards.class.getName()).log(Level.SEVERE, null, ex);
        }
        return outwardsMeta;
    }

    public static int searchResultRowsCount(String query, Map<String, Boolean> searchFilters) {
        int searchResultRowsCount = 0;
        boolean isFiltersAvailable = (searchFilters != null);
        boolean isAnyFilterOn = isFiltersAvailable
                ? searchFilters.values().stream().anyMatch(filterValue -> filterValue == true) : false;
        boolean isQueryBlank = query.isBlank();
        try {
            String sql = "SELECT COUNT(outwards.id) AS search_result_rows_count"
                    + " FROM outwards JOIN items AS i"
                    + " ON outwards.item_id = i.id"
                    + ((isQueryBlank || !isAnyFilterOn) ? "" : formulateFilters(searchFilters));

            con = Connect.getConnection();
            PreparedStatement p;
            p = con.prepareStatement(sql);
            if (!isQueryBlank) {
                int parameterIndex = 0;
                for (var filter : searchFilters.entrySet()) {
                    if (filter.getValue() == true) {
                        if (filter.getKey().equals("code")) {
                            p.setInt(++parameterIndex, Integer.parseInt(query));
                        } else {
                            p.setString(++parameterIndex, "%" + query + "%");
                        }
                    }
                }
            }
            System.out.println(p);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                searchResultRowsCount = result.getInt("search_result_rows_count");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDOutwards.class.getName()).log(Level.SEVERE, null, ex);
        }
        return searchResultRowsCount;
    }

}
