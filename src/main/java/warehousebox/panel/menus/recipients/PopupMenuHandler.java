/*
 * The MIT License
 *
 * Copyright 2025 Saleh.
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
package warehousebox.panel.menus.recipients;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import warehousebox.panel.menus.recipients.form.RecipientsCreateUpdateDialog;

/**
 *
 * @author Saleh
 */
public class PopupMenuHandler implements ActionListener {

    private RecipientsCreateUpdateDialog recipientsCreateUpdateDialog;
    private final JPopupMenu popupMenu;
    private final JMenuItem menuRecipientRemove;
    private final JMenuItem menuRecipientEdit;
    private JList listing;

    public PopupMenuHandler(RecipientsControls rc) {
        recipientsCreateUpdateDialog = rc.getRecipientsCreateUpdateDialog();
        listing = rc.getRecipientsList().getJList();
        listing.addMouseListener(new RightClickJListPopupHandler());

        menuRecipientEdit = new JMenuItem("Edit");
        menuRecipientRemove = new JMenuItem("Remove");

        popupMenu = new JPopupMenu();
        popupMenu.add(menuRecipientEdit);
        popupMenu.addSeparator();
        popupMenu.add(menuRecipientRemove);
    }

    public void setUp() {
        menuRecipientEdit.addActionListener(this);
        menuRecipientRemove.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem) e.getSource();
        if (source == menuRecipientEdit) {
            System.out.println("Editing");
        } else if (source == menuRecipientRemove) {
            System.out.println("Removing");
        }
    }

    private class RightClickJListPopupHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                listing.setSelectedIndex(listing.locationToIndex(e.getPoint()));
                popupMenu.show(listing, e.getPoint().x, e.getPoint().y);
            }
        }
    }

}
