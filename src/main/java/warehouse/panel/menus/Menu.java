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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import warehouse.panel.createandupdate.FormManagement;

/**
 *
 * @author Saleh
 */
public class Menu {

    private JMenuBar menubar;
    private JMenu menuFile;
    private JMenuItem menuCreateItem, menuItemMangeQuantityUnit,
            menuItemMangeStores,
            menuItemMangeUnits;
    private JFrame target;
    private ManageQuantityUnitDialog manageQuantityUnitDialog;
    private CreateItemDialog createItemDialog;
    private MenuItemsListener menuItemsListener;

    public Menu() {
        menubar = new JMenuBar();
        menuFile = new JMenu("Manage");
        menuCreateItem = new JMenuItem("Create new item...");
        menuItemMangeQuantityUnit = new JMenuItem("Manage units...");
        menuItemMangeStores = new JMenuItem("Manage stores...");
        menuItemMangeUnits = new JMenuItem("Manage units...");
        menuFile.add(menuCreateItem);
        menuFile.add(menuItemMangeQuantityUnit);
        menuFile.add(menuItemMangeStores);
        menuFile.add(menuItemMangeUnits);
        menubar.add(menuFile);

        menuItemsListener = new MenuItemsListener();
        createItemDialog = new CreateItemDialog(target, "Create Item", true);
        manageQuantityUnitDialog = new ManageQuantityUnitDialog(target, "Manage units", true);
        menuItemMangeQuantityUnit.addActionListener(menuItemsListener);
        menuCreateItem.addActionListener(menuItemsListener);
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
            }
        }
    }
}
