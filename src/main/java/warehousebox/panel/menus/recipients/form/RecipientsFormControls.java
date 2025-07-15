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

import java.awt.Adjustable;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import warehousebox.db.model.Recipient;
import warehousebox.utility.scrollbarthin.ScrollBarThin;

/**
 *
 * @author Saleh
 */
public class RecipientsFormControls {

    private JPanel container;
    private JLabel lbName;
    private JTextField tfName;
    private RecipientsBrowsedImagePanel recipientsBrowsedImagePanel;
    private JButton btnBrowse, btnSubmit;
    private JLabel btnRemove;
    private ImageIcon imageIconRemoveNormal, imageIconRemoveHover, imageIconRemovePress;
    private ScrollBarThin scrollBarThinTfName;
    private Recipient recipient;

    public RecipientsFormControls() {
        container = new JPanel();
        container.setLayout(new MigLayout("center center"));
        lbName = new JLabel("Name");
        tfName = new JTextField(30);
        recipientsBrowsedImagePanel = new RecipientsBrowsedImagePanel();
        btnBrowse = new JButton("Browse...");
        imageIconRemoveNormal = new ImageIcon(getClass().getResource("/images/remove-icon/remove-normal.png"));
        imageIconRemoveHover = new ImageIcon(getClass().getResource("/images/remove-icon/remove-hovered.png"));
        imageIconRemovePress = new ImageIcon(getClass().getResource("/images/remove-icon/remove-pressed.png"));
        btnRemove = new JLabel();
        btnRemove.setOpaque(false);
        btnRemove.setIcon(imageIconRemoveNormal);
        btnSubmit = new JButton("Submit");
        scrollBarThinTfName = new ScrollBarThin(Adjustable.HORIZONTAL);
        scrollBarThinTfName.setModel(tfName.getHorizontalVisibility());
        Box boxNameField = Box.createVerticalBox();
        boxNameField.add(tfName);
        boxNameField.add(scrollBarThinTfName);
        container.add(lbName);
        container.add(boxNameField, "wrap 15");
        container.add(recipientsBrowsedImagePanel.getContainer(), "span 2, center, wrap 5");
        container.add(btnBrowse, "span 2, center, split 2");
        container.add(btnRemove, "center, wrap 10");
        container.add(btnSubmit, "span 2, center");

    }

    protected JPanel getContainer() {
        return container;
    }

    public JTextField getTfName() {
        return tfName;
    }

    protected JButton getBtnBrowse() {
        return btnBrowse;
    }

    public RecipientsBrowsedImagePanel getRecipientsBrowsedImagePanel() {
        return recipientsBrowsedImagePanel;
    }

    protected JLabel getBtnRemove() {
        return btnRemove;
    }

    protected ImageIcon getImageIconRemoveNormal() {
        return imageIconRemoveNormal;
    }

    protected ImageIcon getImageIconRemoveHover() {
        return imageIconRemoveHover;
    }

    protected ImageIcon getImageIconRemovePress() {
        return imageIconRemovePress;
    }

    protected JButton getBtnSubmit() {
        return btnSubmit;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public Recipient getRecipient() {
        return recipient;
    }

}
