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
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import warehousebox.db.CRUDRecipients;
import warehousebox.db.CRUDRecipientsImages;
import warehousebox.db.model.Recipient;
import warehousebox.db.model.RecipientImage;
import warehousebox.panel.menus.recipients.form.RecipientsCreateUpdateDialog;
import warehousebox.panel.menus.recipients.form.RecipientsFormControls;
import warehousebox.utility.filemanage.ImageFileManager;

/**
 *
 * @author Saleh
 */
public class PopupMenuHandler implements ActionListener {

    private RecipientsCreateUpdateDialog recipientsCreateUpdateDialog;
    private final JPopupMenu popupMenu;
    private final JMenuItem menuRecipientRemove;
    private final JMenuItem menuRecipientEdit;
    private RecipientsList recipientsList;
    private JList listing;
    private RecipientsFormControls recipientsFormControls;
    private Recipient recipient;

    public PopupMenuHandler(RecipientsControls rc) {
        recipientsCreateUpdateDialog = rc.getRecipientsCreateUpdateDialog();
        recipientsList = rc.getRecipientsList();
        listing = recipientsList.getJList();
        listing.addMouseListener(new RightClickJListPopupHandler());
        recipientsFormControls = recipientsCreateUpdateDialog.getRecipientsFormControls();

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
        recipient = (Recipient) listing.getSelectedValue();
        if (source == menuRecipientEdit) {
            recipientsFormControls.setRecipient(recipient);
            recipientsFormControls.getTfName().setText(recipient.getName());
            RecipientImage recipientImage = CRUDRecipientsImages.getImageByRecipientId(recipient.getId());
            recipientsFormControls.getRecipientsBrowsedImagePanel().imageLoaded(recipientImage);
            recipientsCreateUpdateDialog.getRecipientsFormLogic().setRecipientNameOld(recipient.getName());
            recipientsCreateUpdateDialog.setVisible(true);
        } else if (source == menuRecipientRemove) {
            if (CRUDRecipients.isInUse(recipient)) {
                JOptionPane.showMessageDialog(null,
                        "Recipient cannot be deleted because it is in use.",
                        "In use!",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                int reply = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure to delete this Recipient",
                        "DELETE!",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (reply == JOptionPane.YES_OPTION) {
                    // `isImageFileDeleted` flags two cases as `true`:
                    // - The case that an image was deleted successfully.
                    // - The case that that there was no image in the first place.
                    // - The `false` case is set only when there was a file
                    //   but was not deleted due to some issue.
                    boolean isImageFileDeleted;
                    RecipientImage recipientImage = CRUDRecipientsImages.getImageByRecipientId(recipient.getId());
                    if (recipientImage != null) {
                        isImageFileDeleted = ImageFileManager
                                .delete(recipientImage.getImageName(),
                                        CRUDRecipientsImages.DIRECTORYNAME);

                        if (!isImageFileDeleted) {
                            JOptionPane.showMessageDialog(null,
                                    "The image file cannot be deleted due to some unknown issue!",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } else {
                        /**
                         * Set the boolean to true since there is no file to
                         * delete; this boolean is used in a subsequent code to
                         * proceed with the follow-up deletion process.
                         */
                        isImageFileDeleted = true;
                    }
                    if (isImageFileDeleted) {
                        CRUDRecipientsImages.deleteByRecipient(recipient);
                        if (CRUDRecipients.delete(recipient)) {
                            recipientsList.removeElement(listing.getSelectedIndex());
                        }
                    }
                }
            }
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
