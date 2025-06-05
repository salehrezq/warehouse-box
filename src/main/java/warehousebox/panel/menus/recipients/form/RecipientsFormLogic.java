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
package warehousebox.panel.menus.recipients.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import warehousebox.db.CRUDRecipients;
import warehousebox.db.model.Recipient;
import warehousebox.panel.menus.recipients.form.imagefilechooser.IMGFileChooser;

/**
 *
 * @author Saleh
 */
public class RecipientsFormLogic {

    private JTextField tfName;
    private JButton btnBrowse, btnSubmit;
    private ImageIcon imageIconRemoveNormal, imageIconRemoveHover, imageIconRemovePress;
    private JLabel btnRemove;
    private IMGFileChooser iMGFileChooser;
    private RecipientsImagePanel recipientsImagePanel;
    private List<RecipientCRUDListener> recipientCRUDListeners;

    public RecipientsFormLogic(RecipientsFormControls rc) {
        btnBrowse = rc.getBtnBrowse();
        imageIconRemoveNormal = rc.getImageIconRemoveNormal();
        imageIconRemoveHover = rc.getImageIconRemoveHover();
        imageIconRemovePress = rc.getImageIconRemovePress();
        btnRemove = rc.getBtnRemove();
        recipientsImagePanel = rc.getRecipientsImagePanel();
        btnSubmit = rc.getBtnSubmit();
        tfName = rc.getTfName();

        btnRemove.addMouseListener(new BtnRemoveHandler());
        recipientCRUDListeners = new ArrayList<>();
        iMGFileChooser = new IMGFileChooser();
        iMGFileChooser.addImageSelectedListener(recipientsImagePanel);
        btnBrowse.addActionListener(iMGFileChooser);
        btnSubmit.addActionListener(new SubmitHandler());
    }

    public void addRecipientCRUDListener(RecipientCRUDListener recipientCRUDListener) {
        this.recipientCRUDListeners.add(recipientCRUDListener);
    }

    public void notifyRecipientCreated(Recipient recipient) {
        this.recipientCRUDListeners.forEach((recipientCRUDListener) -> {
            recipientCRUDListener.created(recipient);
        });
    }

    private class SubmitHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (tfName.getText().isBlank()) {
                JOptionPane.showMessageDialog(null,
                        "Recipient name is missing!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Recipient recipient = new Recipient();
            recipient.setName(tfName.getText());
            if (CRUDRecipients.isExist(recipient)) {
                JOptionPane.showMessageDialog(null,
                        "The recipient is already exist!",
                        "Duplicate entry", JOptionPane.ERROR_MESSAGE);
            } else {
                recipient = CRUDRecipients.create(recipient);
                if (recipient != null) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Recipient created successfully. You can find it on a next search",
                            "Created",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                notifyRecipientCreated(recipient);
            }
        }
    }

    private class BtnRemoveHandler extends MouseAdapter {

        private boolean hovered = false;

        @Override
        public void mouseClicked(MouseEvent e) {
            System.out.println("Remove image placholder");
        }

        @Override
        public void mousePressed(MouseEvent e) {
            btnRemove.setIcon(imageIconRemovePress);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            btnRemove.setIcon((hovered) ? imageIconRemoveHover : imageIconRemoveNormal);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            hovered = true;
            btnRemove.setIcon(imageIconRemoveHover);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            hovered = false;
            btnRemove.setIcon(imageIconRemoveNormal);
        }

    }

}
