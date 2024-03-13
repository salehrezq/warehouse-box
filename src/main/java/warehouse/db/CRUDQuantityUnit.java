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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import warehouse.db.model.QuantityUnit;

/**
 *
 * @author Saleh
 */
public class CRUDQuantityUnit {

    private static Connection con;

    public static int create(QuantityUnit unit) {
        int insert = 0;
        String sqlCreateQuantityUnit = "INSERT INTO quantity_unit (`name`) VALUES (?)";
        con = Connect.getConnection();
        try {
            PreparedStatement createQuantityUnitStatement = con.prepareStatement(sqlCreateQuantityUnit);
            createQuantityUnitStatement.setString(1, unit.getName());
            insert = createQuantityUnitStatement.executeUpdate();
            con.commit();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDQuantityUnit.class.getName()).log(Level.SEVERE, null, ex);
        }
        return insert;
    }

    public static ArrayList<QuantityUnit> getAll() {
        ArrayList<QuantityUnit> quantityUnits = new ArrayList<>();
        String sqlSelectStatement = "SELECT * FROM quantity_unit ORDER BY `name` ASC";
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sqlSelectStatement);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                QuantityUnit quantityUnit = new QuantityUnit();
                quantityUnit.setId(result.getInt("id"));
                quantityUnit.setName(result.getString("unit"));
                quantityUnits.add(quantityUnit);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDQuantityUnit.class.getName()).log(Level.SEVERE, null, ex);
        }
        return quantityUnits;
    }

    public static QuantityUnit getById(int id) {
        QuantityUnit quantityUnit = null;
        String sql = "SELECT * FROM quantity_unit WHERE id = " + id;
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                quantityUnit = new QuantityUnit();
                quantityUnit.setId(result.getInt("id"));
                quantityUnit.setName(result.getString("name"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDQuantityUnit.class.getName()).log(Level.SEVERE, null, ex);
        }
        return quantityUnit;
    }

    public static boolean isExist(QuantityUnit unit) {
        boolean exist = false;
        String sqlIsUnitExist = "SELECT EXISTS(SELECT * FROM quantity_unit WHERE `name` = ?) AS is_unitname_exist";
        con = Connect.getConnection();
        try {
            PreparedStatement isExistStatement = con.prepareStatement(sqlIsUnitExist);
            isExistStatement.setString(1, unit.getName());
            ResultSet result = isExistStatement.executeQuery();
            if (result.next()) {
                exist = result.getInt("is_unitname_exist") == 1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDQuantityUnit.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exist;
    }

    public static ArrayList<QuantityUnit> getSearch(String str) {
        ArrayList<QuantityUnit> quantityUnits = new ArrayList<>();
        String sqlSelectStatement = "SELECT * FROM quantity_unit WHERE name LIKE '%" + str + "%'";
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sqlSelectStatement);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                QuantityUnit quantityUnit = new QuantityUnit();
                quantityUnit.setId(result.getInt("id"));
                quantityUnit.setName(result.getString("unit"));
                quantityUnits.add(quantityUnit);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDQuantityUnit.class.getName()).log(Level.SEVERE, null, ex);
        }
        return quantityUnits;
    }

}
