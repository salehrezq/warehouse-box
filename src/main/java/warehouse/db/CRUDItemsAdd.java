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
import warehouse.db.model.ItemsAdd;

/**
 *
 * @author Saleh
 */
public class CRUDItemsAdd {

    private static Connection con;

    public static ItemsAdd create(ItemsAdd itemAdd) {
        String sql = "INSERT INTO items_add (`item_id`, `quantity`, `date`, `source_id`) VALUES (?, ?, ?, ?)";
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setInt(1, itemAdd.getItemId());
            p.setBigDecimal(2, itemAdd.getQuantity());
            p.setObject(3, itemAdd.getDate());
            p.setInt(4, itemAdd.getSourceId());
            p.executeUpdate();

            try (ResultSet generatedKeys = p.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    itemAdd.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            con.commit();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItemsAdd.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return itemAdd;
    }

    public static ArrayList<ItemsAdd> getAll() {

        ArrayList<ItemsAdd> itemsAdds = new ArrayList<>();

        try {
            String sql = "SELECT * FROM `items_add` ORDER BY `date` ASC";
            con = Connect.getConnection();
            PreparedStatement p;
            p = con.prepareStatement(sql);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                ItemsAdd itemAdd = new ItemsAdd();
                itemAdd.setId(result.getInt("id"));
                itemAdd.setItemId(result.getInt("item_id"));
                itemAdd.setQuantity(result.getBigDecimal("quantity"));
                itemAdd.setDate(result.getDate("date").toLocalDate());
                itemAdd.setSourceId(result.getInt("source_id"));
                itemsAdds.add(itemAdd);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItemsAdd.class.getName()).log(Level.SEVERE, null, ex);
        }
        return itemsAdds;
    }

}
