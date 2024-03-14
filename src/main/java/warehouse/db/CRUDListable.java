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
import warehouse.panel.menus.Listable;

/**
 *
 * @author Saleh
 */
public class CRUDListable {

    private static Connection con;

    public static int create(Listable listable) {
        int insert = 0;
        String sql = "INSERT INTO "
                + listable.getDBEntityName()
                + " (`" + listable.getDBAttributeName() + "`) VALUES (?)";
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, listable.getName());
            insert = p.executeUpdate();
            con.commit();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return insert;
    }

    public static ArrayList<Listable> getAll(Listable listableImplementation) {
        ArrayList<Listable> listables = new ArrayList<>();
        Listable listableInstance = null;
        String sqlSelectStatement = "SELECT * FROM "
                + listableImplementation.getDBEntityName()
                + " ORDER BY `" + listableImplementation.getDBAttributeName() + "` ASC";
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sqlSelectStatement);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                listableInstance = listableImplementation.getNewInstance();
                listableInstance.setId(result.getInt("id"));
                listableInstance.setName(result.getString(listableImplementation.getDBAttributeName()));
                listables.add(listableInstance);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listables;
    }

    public static Listable getById(Listable listableImplementation, int id) {
        Listable listable = null;
        String sql = "SELECT * FROM "
                + listableImplementation.getDBEntityName()
                + " WHERE id = " + id;
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                listable = listableImplementation.getNewInstance();
                listable.setId(result.getInt("id"));
                listable.setName(result.getString(listable.getDBAttributeName()));
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listable;
    }

    public static boolean isExist(Listable listable) {
        boolean exist = false;
        String tempAttibuteAlais = "is_" + listable.getDBEntityName() + "_exist";
        String sql = "SELECT EXISTS(SELECT * FROM " + listable.getDBEntityName()
                + " WHERE `" + listable.getDBAttributeName()
                + "` = ?) AS " + tempAttibuteAlais;
        con = Connect.getConnection();
        try {
            PreparedStatement isExistStatement = con.prepareStatement(sql);
            isExistStatement.setString(1, listable.getName());
            ResultSet result = isExistStatement.executeQuery();
            if (result.next()) {
                exist = result.getInt(tempAttibuteAlais) == 1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exist;
    }

    public static ArrayList<Listable> getSearch(Listable listableImplementation, String str) {
        ArrayList<Listable> listables = new ArrayList<>();
        String sqlSelectStatement = "SELECT * FROM " + listableImplementation.getDBEntityName() + " WHERE "
                + listableImplementation.getDBAttributeName()
                + " LIKE '%" + str + "%'";
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sqlSelectStatement);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                Listable listableInstance = listableImplementation.getNewInstance();
                listableInstance.setId(result.getInt("id"));
                listableInstance.setName(result.getString(listableImplementation.getDBAttributeName()));
                listables.add(listableInstance);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDListable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listables;
    }

}
