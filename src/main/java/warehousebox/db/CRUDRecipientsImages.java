/*
 * The MIT License
 *
 * Copyright 2025 Saleh.
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import warehousebox.db.model.Recipient;
import warehousebox.db.model.RecipientImage;
import warehousebox.utility.filemanage.ImageFileManager;

/**
 *
 * @author Saleh
 */
public class CRUDRecipientsImages {

    public static final String DIRECTORYNAME = "recipients";

    public static int create(RecipientImage image, int recipientId) {
        int insert = 0;
        String sql = "INSERT INTO recipients_images (recipient_id, name) VALUES (?, ?)";
        try (Connection con = Connect.getConnection()) {
            String newImageName = "";
            newImageName = ImageFileManager.generateImageName(image.getImageFile());

            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, recipientId);
            pstmt.setString(2, newImageName);
            insert = pstmt.executeUpdate();
            if (insert > 0) {
                // Copy image to app directory
                BufferedImage bufferedImage = image.getBufferedImageThumbnailed();
                ImageFileManager.saveBufferedImageToFileSystem(
                        bufferedImage,
                        newImageName,
                        DIRECTORYNAME);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDRecipientsImages.class.getName()).log(Level.SEVERE, null, ex);
        }
        return insert;
    }

    public static RecipientImage getImageByRecipientId(int recipientId) {
        RecipientImage recipientImage = null;
        String sql = "SELECT * FROM recipients_images WHERE recipient_id = ?";

        try (Connection con = Connect.getConnection()) {
            PreparedStatement p;
            p = con.prepareStatement(sql);
            try (ResultSet result = p.executeQuery()) {
                if (result.next()) {
                    recipientImage = new RecipientImage();
                    recipientImage.setId(result.getInt("id"));
                    recipientImage.setRecipientId(result.getInt("recipient_id"));
                    recipientImage.setImageName(result.getString("name"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDRecipientsImages.class.getName()).log(Level.SEVERE, null, ex);
        }
        return recipientImage;
    }

    public static int update(RecipientImage recipientImage) {
        return 0;
    }

    public static int delete(RecipientImage recipientImage) {
        int delete = 0;
        String sql = "DELETE FROM recipients_images WHERE id = ?";
        try (Connection con = Connect.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, recipientImage.getId());
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDRecipientsImages.class.getName()).log(Level.SEVERE, null, ex);
        }
        return delete;
    }

    /**
     * Delete image related to the specified Recipient.
     *
     * @param recipient
     * @return
     */
    public static int deleteByRecipient(Recipient recipient) {
        int delete = 0;
        String sql = "DELETE FROM recipients_images WHERE recipient_id = ?";
        try (Connection con = Connect.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, recipient.getId());
            delete = pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDRecipientsImages.class.getName()).log(Level.SEVERE, null, ex);
        }
        return delete;
    }

}
