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
import java.util.logging.Level;
import java.util.logging.Logger;
import warehouse.db.model.AddedItems;

/**
 *
 * @author Saleh
 */
public class CRUDAddedItems {

    private static Connection con;

    public static AddedItems create(AddedItems addedItems) {
        String sql = "INSERT INTO added_items (`item_id`, `quantity`, `date`, `source_id`) VALUES (?, ?, ?, ?)";
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setInt(1, addedItems.getItemId());
            p.setBigDecimal(2, addedItems.getQuantity());
            p.setObject(3, addedItems.getDate());
            p.setInt(4, addedItems.getSourceId());
            p.executeUpdate();

            try (ResultSet generatedKeys = p.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    addedItems.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            con.commit();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDAddedItems.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return addedItems;
    }

    public static ArrayList<AddedItems> getAll() {

        ArrayList<AddedItems> addedItemses = new ArrayList<>();

        try {
            String sql = "SELECT * FROM `added_items` ORDER BY `date` ASC";
            con = Connect.getConnection();
            PreparedStatement p;
            p = con.prepareStatement(sql);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                AddedItems addedItems = new AddedItems();
                addedItems.setId(result.getInt("id"));
                addedItems.setItemId(result.getInt("item_id"));
                addedItems.setQuantity(result.getBigDecimal("quantity"));
                addedItems.setDate(result.getDate("date").toLocalDate());
                addedItems.setSourceId(result.getInt("source_id"));
                addedItemses.add(addedItems);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDAddedItems.class.getName()).log(Level.SEVERE, null, ex);
        }
        return addedItemses;
    }

}
