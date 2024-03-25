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
package warehouse.panel.menus;

import warehouse.panel.createandupdate.CreateItemDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import warehouse.db.model.QuantityUnit;
import warehouse.db.model.Recipient;
import warehouse.db.model.Source;
import warehouse.panel.createandupdate.FormManagement;

/**
 *
 * @author Saleh
 */
public class Menu {

    private JMenuBar menubar;
    private JMenu menuFile;
    private JMenuItem menuCreateItem, menuItemMangeQuantityUnit,
            menuItemMangeSources,
            menuItemManageRecipients;
    private JFrame target;
    private ListableItemManage manageQuantityUnitDialog,
            manageSourceDialog,
            manageRecipientDialog;
    private CreateItemDialog createItemDialog;
    private MenuItemsListener menuItemsListener;

    public Menu() {
        menubar = new JMenuBar();
        menuFile = new JMenu("Manage");
        menuCreateItem = new JMenuItem("Create new item...");
        menuItemMangeQuantityUnit = new JMenuItem("Manage units...");
        menuItemMangeSources = new JMenuItem("Manage sources...");
        menuItemManageRecipients = new JMenuItem("Manage recipients...");
        menuFile.add(menuCreateItem);
        menuFile.add(menuItemMangeQuantityUnit);
        menuFile.add(menuItemMangeSources);
        menuFile.add(menuItemManageRecipients);
        menubar.add(menuFile);

        menuItemsListener = new MenuItemsListener();
        createItemDialog = new CreateItemDialog(target, "Create Item", true);
        manageQuantityUnitDialog = new ListableItemManage(target, "Manage units", true);
        manageQuantityUnitDialog.setListableImpl(new QuantityUnit());
        manageSourceDialog = new ListableItemManage(target, "Manage Sources", true);
        manageSourceDialog.setListableImpl(new Source());
        manageRecipientDialog = new ListableItemManage(target, "Manage Recipients", true);
        manageRecipientDialog.setListableImpl(new Recipient());
        menuItemMangeQuantityUnit.addActionListener(menuItemsListener);
        menuItemMangeSources.addActionListener(menuItemsListener);
        menuCreateItem.addActionListener(menuItemsListener);
        menuItemManageRecipients.addActionListener(menuItemsListener);
    }

    public JMenuBar getMenuBar() {
        return this.menubar;
    }

    public void setTarget(JFrame target) {
        this.target = target;
    }

    public FormManagement getFormManagement() {
        return createItemDialog.getFormManagement();
    }

    private class MenuItemsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == menuItemMangeQuantityUnit) {
                manageQuantityUnitDialog.rePopulateUnitsList();
                manageQuantityUnitDialog.setVisible(true);
            } else if (source == menuCreateItem) {
                createItemDialog.setVisible(true);
            } else if (source == menuItemMangeSources) {
                manageSourceDialog.rePopulateUnitsList();
                manageSourceDialog.setVisible(true);
            } else if (source == menuItemManageRecipients) {
                manageRecipientDialog.rePopulateUnitsList();
                manageRecipientDialog.setVisible(true);
            }
        }
    }
}
