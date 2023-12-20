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

/**
 *
 * @author Saleh
 */
public class Menu {

    private JMenuBar menubar;
    private JMenu menuFile;
    private JMenuItem menuItemMangeSourceLocations,
            menuItemMangeStores,
            menuItemMangeUnits;
    private JFrame target;
    private ManageSourceLocationDialog manageSourceLocationDialog;
    private MenuItemsListener menuItemsListener;

    public Menu() {
        menubar = new JMenuBar();
        menuFile = new JMenu("Manage");
        menuItemMangeSourceLocations = new JMenuItem("Manage locations...");
        menuItemMangeStores = new JMenuItem("Manage stores...");
        menuItemMangeUnits = new JMenuItem("Manage units...");
        menuFile.add(menuItemMangeSourceLocations);
        menuFile.add(menuItemMangeStores);
        menuFile.add(menuItemMangeUnits);
        menubar.add(menuFile);

        menuItemsListener = new MenuItemsListener();
        manageSourceLocationDialog = new ManageSourceLocationDialog(target, "Manage locations", true);
        menuItemMangeSourceLocations.addActionListener(menuItemsListener);
    }

    public JMenuBar getMenuBar() {
        return this.menubar;
    }

    public void setTarget(JFrame target) {
        this.target = target;
    }

    private class MenuItemsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == menuItemMangeSourceLocations) {
                manageSourceLocationDialog.setVisible(true);
                System.out.println("menu manageSourceLocationDialog");
            }
        }
    }
}
