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
package warehouse.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import warehouse.panel.inwards.InwardDialog;
import warehouse.panel.inwards.InwardsTap;
import warehouse.panel.items.ItemsList;
import warehouse.panel.items.ItemsTab;
import warehouse.panel.outwards.OutwardsTap;
import warehouse.panel.items.ItemImage;
import warehouse.panel.outwards.OutwardDialog;

/**
 *
 * @author Saleh
 */
public class Tabs extends JPanel {

    private JTabbedPane tabs;
    private ItemsTab itemsTab;
    private InwardsTap inwardsTap;
    private OutwardsTap outwardsTap;

    public Tabs() {

        this.setLayout(new BorderLayout());
        tabs = new JTabbedPane();
        itemsTab = new ItemsTab();
        inwardsTap = new InwardsTap();
        outwardsTap = new OutwardsTap();

        InwardDialog inwardDialog = itemsTab.getItemsList().getInwardCreateDialog();
        inwardDialog.addInwardCRUDListener(inwardsTap.getInwardsList());
        inwardDialog.addInwardCRUDListener(itemsTab.getItemsList());

        OutwardDialog outInwardDialog = itemsTab.getItemsList().getOutwardCreateDialog();
        outInwardDialog.addOutwardCRUDListener(outwardsTap.getOutwardsList());
        outInwardDialog.addOutwardCRUDListener(itemsTab.getItemsList());

        tabs.add(itemsTab, "Items");
        tabs.add(inwardsTap, "Inwards");
        tabs.add(outwardsTap, "Outwards");
        this.add(tabs, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(1000, 500));
    }

    public ItemsList getItemsList() {
        return itemsTab.getItemsList();
    }

    public ItemImage getItemImage() {
        return itemsTab.getItemImage();
    }
}
