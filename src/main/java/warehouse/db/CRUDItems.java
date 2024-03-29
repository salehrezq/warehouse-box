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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import warehouse.db.model.Item;

/**
 *
 * @author Saleh
 */
public class CRUDItems {

    private static Connection con;

    public static Item create(Item item) {
        String sqlCreateItem = "INSERT INTO items (`name`, `specification`, `unit_id`) VALUES (?, ?, ?)";
        con = Connect.getConnection();
        try {
            PreparedStatement createItemsStatement = con.prepareStatement(sqlCreateItem, Statement.RETURN_GENERATED_KEYS);
            createItemsStatement.setString(1, item.getName());
            createItemsStatement.setString(2, item.getSpecification());
            createItemsStatement.setInt(3, item.getUnitId());
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
                item.setUnitId(result.getInt("unit_id"));
                items.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDItems.class.getName()).log(Level.SEVERE, null, ex);
        }
        return items;
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

}
