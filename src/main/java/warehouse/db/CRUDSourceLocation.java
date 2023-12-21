/*
 * The MIT License
 *
 * Copyright 2023 Saleh.
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
import java.util.logging.Level;
import java.util.logging.Logger;
import warehouse.db.model.SourceLocation;

/**
 *
 * @author Saleh
 */
public class CRUDSourceLocation {

    private static Connection con;

    public static int create(SourceLocation sourceLocation) {
        int insert = 0;
        String sqlCreateSourceLocation = "INSERT INTO source_location (`location`) VALUES (?)";
        con = Connect.getConnection();
        try {
            PreparedStatement createLocationStatement = con.prepareStatement(sqlCreateSourceLocation);
            createLocationStatement.setString(1, sourceLocation.getSourceLocation());
            insert = createLocationStatement.executeUpdate();
            con.commit();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDSourceLocation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return insert;
    }

    public static boolean isExist(SourceLocation sourceLocation) {
        boolean exist = false;
        String sqlIsLocationExist = "SELECT EXISTS(SELECT * FROM source_location WHERE `location` = ?) AS is_location_exist";
        con = Connect.getConnection();
        try {
            PreparedStatement isExistStatement = con.prepareStatement(sqlIsLocationExist);
            isExistStatement.setString(1, sourceLocation.getSourceLocation());
            ResultSet result = isExistStatement.executeQuery();
            if (result.next()) {
                exist = result.getInt("is_location_exist") == 1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDSourceLocation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exist;
    }
}
