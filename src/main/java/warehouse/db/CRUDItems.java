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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import warehouse.db.model.Item;

/**
 *
 * @author Saleh
 */
public class CRUDItems {

    private static Connection con;

    public static int create(Item item) {
        int insert = 0;
        String sqlCreateItem = "INSERT INTO items (`name`, `specification`, `unit`, `image`) VALUES (?, ?, ?, ?)";
        con = Connect.getConnection();
        try {
            PreparedStatement createItemsStatement = con.prepareStatement(sqlCreateItem);
            createItemsStatement.setString(1, item.getName());
            createItemsStatement.setString(2, item.getSpecification());
            createItemsStatement.setInt(3, item.getUnit());
            createItemsStatement.setBytes(4, item.getImage());
            insert = createItemsStatement.executeUpdate();
            con.commit();
        } catch (SQLException ex) {
            Logger.getLogger(Item.class.getName()).log(Level.SEVERE, null, ex);
        }
        return insert;
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
                item.setUnit(result.getInt("unit"));
                item.setImage(result.getBytes("image"));
                items.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
        }
        return items;
    }

}
