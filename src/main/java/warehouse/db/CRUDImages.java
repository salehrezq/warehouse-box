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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import warehouse.db.model.Image;

/**
 *
 * @author Saleh
 */
public class CRUDImages {

    private static Connection con;

    public static int create(ArrayList<Image> images, int itemId) {
        int insert = 0;
        String sql = "INSERT INTO images (`item_id`, `image`, `order`, `default_image`, `scale`) VALUES (?, ?, ?, ?, ?)";
        con = Connect.getConnection();
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            for (int i = 0; i < images.size(); i++) {
                Image image = images.get(i);
                pstmt.setInt(1, image.getItemId());
                pstmt.setBytes(2, image.getImageBytes());
                pstmt.setInt(3, image.getOrder());
                pstmt.setBoolean(4, image.isDefaultImage());
                pstmt.setBigDecimal(5, image.getScale());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            con.commit();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDImages.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return insert;
    }
}
