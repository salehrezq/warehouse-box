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
package warehousebox.root;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.UIManager;
import warehousebox.db.Connect;
import warehousebox.db.model.QuantityUnit;
import warehousebox.db.model.Recipient;
import warehousebox.db.model.Source;
import warehousebox.panel.menus.Menu;
import warehousebox.panel.tabs.Tabs;
import warehousebox.panel.createandupdate.FormManagement;
import warehousebox.panel.items.ItemImage;
import warehousebox.panel.menus.ListableItemEditDialog;
import warehousebox.panel.menus.ListableItemManage;

/**
 *
 * @author Saleh
 */
public class MainRun {

    private static JFrame frame;
    private Tabs tabs;
    private Menu menu;

    public static JFrame getFrame() {
        return MainRun.frame;
    }

    private void createAndShowGUI() {
        menu = new Menu();
        tabs = new Tabs();
        frame = new JFrame("Warehouse Box 1.0.0");
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/app-icon/app-icon.png")));
        menu.setTarget(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(tabs);
        frame.setJMenuBar(menu.getMenuBar());
        frame.pack();
        frame.setVisible(true);

        /**
         * Shutdown the database on app termination.
         */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Connect.shutdown();
            }
        });
    }

    private void linkActionToListeners() {
        FormManagement createFormManagment = menu.getFormManagement();
        createFormManagment.addItemCRUDListener(tabs.getItemsList());

        ItemImage itemImage = tabs.getItemImage();
        FormManagement updateFormManagment = tabs.getItemsList().getUpdateItemDialog().getFormManagement();
        updateFormManagment.addItemCRUDListener(itemImage);
        updateFormManagment.addItemCRUDListener(tabs.getItemsList());

        ListableItemManage listableItemManageDialogeQuantityUnit = menu.getListableItemManage(new QuantityUnit());
        ListableItemEditDialog editDialogeForQuantityUnit = listableItemManageDialogeQuantityUnit.getListableItemEditDialog();
        editDialogeForQuantityUnit.addListableUpdateListener(tabs.getItemsList());
        editDialogeForQuantityUnit.addListableUpdateListener(tabs.getInboundsList());
        editDialogeForQuantityUnit.addListableUpdateListener(tabs.getOutboundsList());

        ListableItemManage listableItemManageDialogeSource = menu.getListableItemManage(new Source());
        ListableItemEditDialog editDialogeForSource = listableItemManageDialogeSource.getListableItemEditDialog();
        editDialogeForSource.addListableUpdateListener(tabs.getInboundsList());

        ListableItemManage listableItemManageDialogeRecipient = menu.getListableItemManage(new Recipient());
        ListableItemEditDialog editDialogeForRecipient = listableItemManageDialogeRecipient.getListableItemEditDialog();
        editDialogeForRecipient.addListableUpdateListener(tabs.getOutboundsList());
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(() -> {
            Connect.buildDatabaseIfNotExist();
            FlatLightLaf.setup();
            UIManager.put("Table.showVerticalLines", true);
            UIManager.put("Table.showHorizontalLines", true);
            MainRun mainRun = new MainRun();
            mainRun.createAndShowGUI();
            mainRun.linkActionToListeners();
        });
    }
}
