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
package warehousebox.panel.menus;

import warehousebox.panel.createandupdate.ItemCreateUpdateDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import warehousebox.db.model.QuantityUnit;
import warehousebox.db.model.Recipient;
import warehousebox.db.model.Source;
import warehousebox.panel.createandupdate.FormManagement;
import warehousebox.utility.singularlisting.Listable;

/**
 *
 * @author Saleh
 */
public class Menu {

    private JMenuBar menubar;
    private JMenu menuFile, menuSettings, menuAbout;
    private JMenuItem menuCreateItem, menuItemMangeQuantityUnit,
            menuItemMangeSources,
            menuItemManageRecipients,
            menuItemSearchResultSize;
    private JFrame target;
    private ListableItemManage manageQuantityUnitDialog,
            manageSourceDialog,
            manageRecipientDialog;
    private ItemCreateUpdateDialog createItemDialog;
    private ResultLimitSizePreference resultSizeDialog;
    private MenuItemsListener menuItemsListener;
    private About about;
    private ImageIcon imageIconAboutNormal, imageIconAboutHover, imageIconAboutPress;

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

        menuSettings = new JMenu("Settings");
        menuItemSearchResultSize = new JMenuItem("Result size...");
        menuSettings.add(menuItemSearchResultSize);
        menubar.add(menuSettings);
        resultSizeDialog = new ResultLimitSizePreference(target, "Set search result rows size on \"load more\"", true);

        imageIconAboutNormal = new ImageIcon(getClass().getResource("/images/app-about/about-normal.png"));
        imageIconAboutHover = new ImageIcon(getClass().getResource("/images/app-about/about-hovered.png"));
        imageIconAboutPress = new ImageIcon(getClass().getResource("/images/app-about/about-pressed.png"));

        menuAbout = new JMenu("About");
        menuAbout.setIcon(imageIconAboutNormal);
        menuAbout.addMouseListener(new AboutButtonMouseEventHandler());

        menubar.add(menuAbout);

        menuItemsListener = new MenuItemsListener();
        createItemDialog = new ItemCreateUpdateDialog(target, "Create Item", true);
        manageQuantityUnitDialog = new ListableItemManage(target, "Manage units", true);
        manageQuantityUnitDialog.setListableImpl(new QuantityUnit());
        //manageQuantityUnitDialog.get
        manageSourceDialog = new ListableItemManage(target, "Manage Sources", true);
        manageSourceDialog.setListableImpl(new Source());
        manageRecipientDialog = new ListableItemManage(target, "Manage Recipients", true);
        manageRecipientDialog.setListableImpl(new Recipient());
        menuItemMangeQuantityUnit.addActionListener(menuItemsListener);
        menuItemMangeSources.addActionListener(menuItemsListener);
        menuCreateItem.addActionListener(menuItemsListener);
        menuItemManageRecipients.addActionListener(menuItemsListener);
        menuItemSearchResultSize.addActionListener(menuItemsListener);
    }

    private ImageIcon getTransformedImageIcon(String resource) {
        ImageIcon imageIconOrignal = new ImageIcon(getClass().getResource(resource));
        Image image = imageIconOrignal.getImage();
        Image transformedImageIcon = image.getScaledInstance(18, 18, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(transformedImageIcon);
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

    public ListableItemManage getListableItemManage(Listable listable) {
        ListableItemManage listableItemManage = null;
        if (listable instanceof QuantityUnit) {
            listableItemManage = manageQuantityUnitDialog;
        } else if (listable instanceof Source) {
            listableItemManage = manageSourceDialog;
        } else if (listable instanceof Recipient) {
            listableItemManage = manageRecipientDialog;
        }
        return listableItemManage;
    }

    private class MenuItemsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == menuItemMangeQuantityUnit) {
                manageQuantityUnitDialog.setVisible(true);
            } else if (source == menuCreateItem) {
                createItemDialog.setVisible(true);
            } else if (source == menuItemMangeSources) {
                manageSourceDialog.setVisible(true);
            } else if (source == menuItemManageRecipients) {
                manageRecipientDialog.setVisible(true);
            } else if (source == menuItemSearchResultSize) {
                resultSizeDialog.initializeWithSavedLimitSize();
                resultSizeDialog.setVisible(true);
            }
        }
    }

    private class AboutButtonMouseEventHandler extends MouseAdapter {

        private boolean hovered = false;

        @Override
        public void mouseClicked(MouseEvent e) {
            if (about == null) {
                about = new About(target, "About", true);
            }
            about.setVisible(true);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            menuAbout.setIcon(imageIconAboutPress);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            menuAbout.setIcon((hovered) ? imageIconAboutHover : imageIconAboutNormal);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            hovered = true;
            menuAbout.setIcon(imageIconAboutHover);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            hovered = false;
            menuAbout.setIcon(imageIconAboutNormal);
        }

    }
}
