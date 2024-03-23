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
import warehouse.db.model.Outward;
import warehouse.db.model.OutwardMeta;

/**
 *
 * @author Saleh
 */
public class CRUDOutwards {

    private static Connection con;

    public static Outward create(Outward outward) {
        String sql = "INSERT INTO outwards (`item_id`, `quantity`, `recipient_id`, `for`, `date`) VALUES (?, ?, ?, ?, ?)";
        con = Connect.getConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            p.setInt(1, outward.getItemId());
            p.setBigDecimal(2, outward.getQuantity());
            p.setInt(3, outward.getRecipientId());
            p.setString(4, outward.getUsedFor());
            p.setObject(5, outward.getDate());
            p.executeUpdate();
            con.commit();
        } catch (SQLException ex) {
            Logger.getLogger(CRUDOutwards.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return outward;
    }

    public static ArrayList<OutwardMeta> getAll() {

        ArrayList<OutwardMeta> outwardMetas = new ArrayList<>();

        try {
            String sql = "SELECT outwards.id AS outward_id, outwards.item_id AS item_id,"
                    + " outwards.quantity, u.name AS unit_name, r.name AS recipient,"
                    + " outwards.date, i.name AS item_name, i.specification AS item_specs"
                    + " FROM outwards JOIN items AS i JOIN quantity_unit AS u JOIN recipients AS r"
                    + " ON (outwards.item_id = i.id) AND (i.unit_id = u.id) AND (r.id = outwards.recipient_id)"
                    + " ORDER BY outwards.date ASC, outwards.id ASC;";

            con = Connect.getConnection();
            PreparedStatement p;
            p = con.prepareStatement(sql);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                OutwardMeta outwardMeta = new OutwardMeta();
                outwardMeta.setId(result.getInt("outward_id"));
                outwardMeta.setId(result.getInt("item_id"));
                outwardMeta.setQuantity(result.getBigDecimal("quantity"));
                outwardMeta.setUnitName(result.getString("unit_name"));
                outwardMeta.setRecipient(result.getString("recipient"));
                outwardMeta.setUsedFor(result.getString("for"));
                outwardMeta.setDate(result.getDate("date").toLocalDate());
                outwardMetas.add(outwardMeta);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDOutwards.class.getName()).log(Level.SEVERE, null, ex);
        }
        return outwardMetas;
    }

}
