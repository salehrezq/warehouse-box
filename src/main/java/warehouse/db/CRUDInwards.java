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
import warehouse.db.model.Inward;
import warehouse.db.model.InwardMeta;

/**
 *
 * @author Saleh
 */
public class CRUDInwards {

    private static Connection con;

    public static Inward create(Inward inward) {
        String sql = "INSERT INTO inwards (`item_id`, `quantity`, `date`, `source_id`) VALUES (?, ?, ?, ?)";
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setInt(1, inward.getItemId());
            p.setBigDecimal(2, inward.getQuantity());
            p.setObject(3, inward.getDate());
            p.setInt(4, inward.getSourceId());
            p.executeUpdate();

            try (ResultSet generatedKeys = p.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    inward.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            con.commit();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDInwards.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return inward;
    }

    public static ArrayList<InwardMeta> getAll() {

        ArrayList<InwardMeta> inwardsMedta = new ArrayList<>();

        try {
            String sql = "SELECT inwards.item_id AS item_id, inwards.id AS addition_id,"
                    + " inwards.quantity, u.name AS unit_name, s.information AS source,"
                    + " inwards.date, i.name AS item_name, i.specification AS item_specs"
                    + " FROM inwards JOIN items AS i JOIN quantity_unit AS u JOIN source AS s"
                    + " ON (inwards.item_id = i.id) AND (i.unit_id = u.id) AND (s.id = inwards.source_id)"
                    + " ORDER BY inwards.date ASC, inwards.id ASC;";

            con = Connect.getConnection();
            PreparedStatement p;
            p = con.prepareStatement(sql);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                InwardMeta inwardMeta = new InwardMeta();
                inwardMeta.setItemIdd(result.getInt("item_id"));
                inwardMeta.setAdditionId(result.getInt("addition_id"));
                inwardMeta.setQuantity(result.getBigDecimal("quantity"));
                inwardMeta.setUnitName(result.getString("unit_name"));
                inwardMeta.setSource(result.getString("source"));
                inwardMeta.setDate(result.getDate("date").toLocalDate());
                inwardMeta.setItemName(result.getString("item_name"));
                inwardMeta.setItemSpecs(result.getString("item_specs"));
                inwardsMedta.add(inwardMeta);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDInwards.class.getName()).log(Level.SEVERE, null, ex);
        }
        return inwardsMedta;
    }

}
