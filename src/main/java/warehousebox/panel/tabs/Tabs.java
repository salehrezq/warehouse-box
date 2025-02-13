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
package warehousebox.panel.tabs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import warehousebox.panel.inbounds.InboundDialog;
import warehousebox.panel.inbounds.InboundsList;
import warehousebox.panel.inbounds.InboundsTap;
import warehousebox.panel.items.ItemsList;
import warehousebox.panel.items.ItemsTab;
import warehousebox.panel.outbounds.OutboundsTap;
import warehousebox.panel.items.ItemImage;
import warehousebox.panel.outbounds.OutboundDialog;
import warehousebox.panel.outbounds.OutboundScrapDialog;
import warehousebox.panel.outbounds.OutboundsList;

/**
 *
 * @author Saleh
 */
public class Tabs extends JPanel {

    private JTabbedPane tabs;
    private ItemsTab itemsTab;
    private InboundsTap inboundsTap;
    private OutboundsTap outboundsTap;

    public Tabs() {

        this.setLayout(new BorderLayout());
        tabs = new JTabbedPane();
        itemsTab = new ItemsTab();
        inboundsTap = new InboundsTap();
        outboundsTap = new OutboundsTap();

        InboundDialog inboundDialog = itemsTab.getItemsList().getInboundCreateDialog();
        inboundDialog.addInboundCRUDListener(inboundsTap.getInboundsList());
        inboundDialog.addInboundCRUDListener(itemsTab.getItemsList());

        OutboundDialog outInboundDialog = itemsTab.getItemsList().getOutboundCreateDialog();
        outInboundDialog.addOutboundCRUDListener(outboundsTap.getOutboundsList());
        outInboundDialog.addOutboundCRUDListener(itemsTab.getItemsList());

        OutboundScrapDialog outboundScrapDialog = itemsTab.getItemsList().getOutboundScrapDialog();
        outboundScrapDialog.addOutboundScrapCRUDListener(itemsTab.getItemsList());
        outboundScrapDialog.addOutboundScrapCRUDListener(outboundsTap.getOutboundsList());

        inboundsTap.getInboundsList().addInboundDeleteListener(itemsTab.getItemsList());
        outboundsTap.getOutboundsList().addOutboundDeleteListener(itemsTab.getItemsList());

        tabs.add(itemsTab, "Items");
        tabs.add(inboundsTap, "Inbounds");
        tabs.add(outboundsTap, "Outbounds");
        this.add(tabs, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(1000, 500));
    }

    public ItemsList getItemsList() {
        return itemsTab.getItemsList();
    }

    public ItemImage getItemImage() {
        return itemsTab.getItemImage();
    }

    public InboundsList getInboundsList() {
        return inboundsTap.getInboundsList();
    }

    public OutboundsList getOutboundsList() {
        return outboundsTap.getOutboundsList();
    }
}
