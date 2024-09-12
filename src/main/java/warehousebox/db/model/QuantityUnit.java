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
package warehousebox.db.model;

import java.util.HashMap;
import java.util.Map;
import warehousebox.utility.singularlisting.Listable;

/**
 *
 * @author Saleh
 */
public class QuantityUnit implements Listable {

    private int id;
    private String name;
    private final String strLabel = "QUantity_unit";
    private final String dbEntityName = "quantity_unit";
    private final String dbAttributeName = "name";
    private Map consumer;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLabel() {
        return this.strLabel;
    }

    @Override
    public String getDBEntityName() {
        return dbEntityName;
    }

    @Override
    public String getDBAttributeName() {
        return dbAttributeName;
    }

    @Override
    public Listable getNewInstance() {
        return new QuantityUnit();
    }

    public class Consumer {

        public static final String table = "items";
        public static final String column = "unit_id";

        public static String getTable() {
            return table;
        }

        public static String getColumn() {
            return column;
        }
    }

    /**
     * Map that has information about the database relation/table that make use
     * of this relation model.That table has this model's id as foreign key.
     *
     * @return Map
     */
    @Override
    public Map getConsumer() {
        if (consumer != null) {
            return consumer;
        } else {
            consumer = new HashMap<String, String>();
            consumer.put("table", "items");
            consumer.put("column", "unit_id");
            return consumer;
        }
    }

}
