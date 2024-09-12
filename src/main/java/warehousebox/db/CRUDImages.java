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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import warehousebox.utility.filemanage.ImageFileManager;
import warehousebox.db.model.Image;
import warehousebox.db.model.Item;

/**
 *
 * @author Saleh
 */
public class CRUDImages {

    public static int create(List<Image> images, int itemId) {
        int[] patchArray = null;
        String sql = "INSERT INTO images (item_id, position, name, default_image, scale) VALUES (?, ?, ?, ?, ?)";
        String newImageName = "";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(sql);
            for (int i = 0; i < images.size(); i++) {
                Image image = images.get(i);
                newImageName = ImageFileManager.generateImageName(image.getImageFile());
                pstmt.setInt(1, itemId);
                pstmt.setInt(2, image.getPosition());
                pstmt.setString(3, newImageName);
                pstmt.setBoolean(4, image.isDefaultImage());
                pstmt.setBigDecimal(5, image.getScale());
                pstmt.addBatch();
                // Copy image to app directory
                ImageFileManager.copyFileUsingJava7Files(image.getImageFile(), newImageName);
            }
            patchArray = pstmt.executeBatch();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDImages.class.getName()).log(Level.SEVERE, null, ex);
        }
        return patchArray.length;
    }

    public static List<Image> getImagesByItemId(int itemId) {
        List<Image> images = new ArrayList<>();
        String sql = "SELECT * FROM images WHERE item_id =" + itemId + " ORDER BY position ASC";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p;
            p = con.prepareStatement(sql);

            try (ResultSet result = p.executeQuery()) {
                while (result.next()) {
                    Image image = new Image();
                    image.setId(result.getInt("id"));
                    image.setItemId(result.getInt("item_id"));
                    image.setImageName(result.getString("name"));
                    image.setPosition(result.getInt("position"));
                    image.setDefaultImage(result.getBoolean("default_image"));
                    image.setScale(result.getObject("scale", BigDecimal.class));
                    images.add(image);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDImages.class.getName()).log(Level.SEVERE, null, ex);
        }
        return images;
    }

    public static int update(List<Image> images) {
        int[] patchArray = null;
        String sql = "UPDATE images"
                + " SET"
                + " position = ?,"
                + " default_image = ?"
                + " WHERE id = ?";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(sql);
            for (int i = 0; i < images.size(); i++) {
                Image image = images.get(i);
                pstmt.setInt(1, image.getPosition());
                pstmt.setBoolean(2, image.isDefaultImage());
                pstmt.setInt(3, image.getId());
                pstmt.addBatch();
            }
            patchArray = pstmt.executeBatch();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDImages.class.getName()).log(Level.SEVERE, null, ex);
        }
        return patchArray.length;
    }

    public static boolean updateScale(Image image) {
        int update = 0;
        String sql = "UPDATE images"
                + " SET scale = ?"
                + " WHERE id = ?";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql);
            p.setBigDecimal(1, image.getScale());
            p.setInt(2, image.getId());
            update = p.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDImages.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (update > 0);
    }

    public static int delete(List<Image> images) {
        int[] patchArray = null;
        String sql = "DELETE FROM images WHERE id = ?";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(sql);
            for (int i = 0; i < images.size(); i++) {
                Image image = images.get(i);
                pstmt.setInt(1, image.getId());
                pstmt.addBatch();
            }
            patchArray = pstmt.executeBatch();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDImages.class.getName()).log(Level.SEVERE, null, ex);
        }
        return patchArray.length;
    }

    /**
     * Delete images related to the specified item.
     *
     * @param item
     * @return
     */
    public static int deleteByItem(Item item) {
        int delete = 0;
        String sql = "DELETE FROM images WHERE item_id = ?";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p = con.prepareStatement(sql);
            p.setInt(1, item.getId());
            delete = p.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return delete;
    }

}
