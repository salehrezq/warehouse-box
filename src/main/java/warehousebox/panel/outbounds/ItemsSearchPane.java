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
package warehousebox.panel.outbounds;

import java.awt.Adjustable;
import java.awt.Color;
import java.awt.Image;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import warehousebox.panel.menus.recipients.RecipientsImagePanel;
import warehousebox.utility.scrollbarthin.ScrollBarThin;

/**
 *
 * @author Saleh
 */
public class ItemsSearchPane {

    private JPanel container;
    private JTextField tfSearchQuery, tfRecipientFilter;
    private ImageIcon imageIconRemoveNormal, imageIconRemoveHover, imageIconRemovePress;
    private ScrollBarThin scrollBarThinTfSearchQuery, scrollBarThinTfRecipientFilter;
    private JButton btnSearchQuery, btnRecipientFilter;
    private JLabel btnRemoveRecipient;
    private JCheckBox checkOutboundIdFilter,
            checkItemIdFilter,
            checkNameFilter,
            checkSpecificationFilter,
            checkNoteFilter,
            checkConsumableFilter,
            checkReturnableFilter,
            checkScrapFilter;
    private DateRange dateRange;
    private RecipientsImagePanel recipientsImagePanel;
    private final Color colorTextField = new Color(84, 84, 84);

    public ItemsSearchPane() {
        container = new JPanel(new MigLayout("fillx"));

        // Setup Text field search query
        tfSearchQuery = new JTextField(35);
        scrollBarThinTfSearchQuery = new ScrollBarThin(Adjustable.HORIZONTAL);
        scrollBarThinTfSearchQuery.setModel(tfSearchQuery.getHorizontalVisibility());
        Box boxSearchQueryField = Box.createVerticalBox();
        boxSearchQueryField.add(tfSearchQuery);
        boxSearchQueryField.add(scrollBarThinTfSearchQuery);

        btnSearchQuery = new JButton("Search");
        checkOutboundIdFilter = new JCheckBox("Outbound Id");
        checkItemIdFilter = new JCheckBox("Item Id");
        checkNameFilter = new JCheckBox("Name");
        checkSpecificationFilter = new JCheckBox("Specification");
        checkNoteFilter = new JCheckBox("Note");
        checkConsumableFilter = new JCheckBox("Consumables");
        checkReturnableFilter = new JCheckBox("Returnables");
        checkScrapFilter = new JCheckBox("Scrap");

        btnRecipientFilter = new JButton("Recipient...");
        tfRecipientFilter = new JTextField(15);
        tfRecipientFilter.setEditable(false);
        tfRecipientFilter.setDisabledTextColor(colorTextField);
        scrollBarThinTfRecipientFilter = new ScrollBarThin(Adjustable.HORIZONTAL);
        scrollBarThinTfRecipientFilter.setModel(tfRecipientFilter.getHorizontalVisibility());
        imageIconRemoveNormal = getTransformedImageIcon("/images/remove-icon-circular/remove-normal.png");
        imageIconRemoveHover = getTransformedImageIcon("/images/remove-icon-circular/remove-hovered.png");
        imageIconRemovePress = getTransformedImageIcon("/images/remove-icon-circular/remove-pressed.png");
        btnRemoveRecipient = new JLabel();
        btnRemoveRecipient.setOpaque(false);
        btnRemoveRecipient.setIcon(imageIconRemoveNormal);
        Box boxRecipientFilterField = Box.createVerticalBox();
        boxRecipientFilterField.add(tfRecipientFilter);
        boxRecipientFilterField.add(scrollBarThinTfRecipientFilter);

        dateRange = new DateRange();
        recipientsImagePanel = new RecipientsImagePanel(64, 64);
        recipientsImagePanel.setImagePlaceholder();

        Box boxFilters = Box.createHorizontalBox();
        boxFilters.add(checkOutboundIdFilter);
        boxFilters.add(Box.createHorizontalStrut(5));
        boxFilters.add(checkItemIdFilter);
        boxFilters.add(Box.createHorizontalStrut(5));
        boxFilters.add(checkNameFilter);
        boxFilters.add(Box.createHorizontalStrut(5));
        boxFilters.add(checkSpecificationFilter);
        boxFilters.add(Box.createHorizontalStrut(5));
        boxFilters.add(checkNoteFilter);
        boxFilters.add(Box.createHorizontalStrut(2));
        boxFilters.add(btnRecipientFilter);
        boxFilters.add(Box.createHorizontalStrut(2));
        boxFilters.add(boxRecipientFilterField);
        boxFilters.add(Box.createHorizontalStrut(2));
        boxFilters.add(btnRemoveRecipient);

        Box boxIssuanceTypesFilters = Box.createHorizontalBox();
        boxIssuanceTypesFilters.add(checkConsumableFilter);
        boxIssuanceTypesFilters.add(Box.createHorizontalStrut(2));
        boxIssuanceTypesFilters.add(checkReturnableFilter);
        boxIssuanceTypesFilters.add(Box.createHorizontalStrut(2));
        boxIssuanceTypesFilters.add(checkScrapFilter);
        boxIssuanceTypesFilters.add(Box.createHorizontalStrut(155));

        /**
         * Group boxIssuanceTypesFilters with date range with the recipient
         * image.
         */
        Box leftGroup = Box.createVerticalBox();
        leftGroup.add(boxIssuanceTypesFilters);
        leftGroup.add(dateRange.getContainer());
        Box group = Box.createHorizontalBox();
        group.add(leftGroup);
        group.add(recipientsImagePanel.getContainer());

        container.add(boxSearchQueryField, "center, span 2, split 2");
        container.add(btnSearchQuery, "wrap");
        container.add(boxFilters, "center, span 2, wrap");
        container.add(group, "center");
    }

    private ImageIcon getTransformedImageIcon(String resource) {
        ImageIcon imageIconOrignal = new ImageIcon(getClass().getResource(resource));
        Image image = imageIconOrignal.getImage();
        Image transformedImageIcon = image.getScaledInstance(18, 18, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(transformedImageIcon);
    }

    protected JPanel getContainer() {
        return this.container;
    }

    protected JTextField getTfSearchQuery() {
        return tfSearchQuery;
    }

    protected JButton getBtnSearchQuery() {
        return btnSearchQuery;
    }

    protected JLabel getBtnRemoveRecipient() {
        return btnRemoveRecipient;
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

    protected JCheckBox getCheckOutboundIdFilter() {
        return checkOutboundIdFilter;
    }

    protected JCheckBox getCheckItemIdFilter() {
        return checkItemIdFilter;
    }

    protected JCheckBox getCheckNameFilter() {
        return checkNameFilter;
    }

    protected JCheckBox getCheckSpecificationFilter() {
        return checkSpecificationFilter;
    }

    protected JCheckBox getCheckNoteFilter() {
        return checkNoteFilter;
    }

    protected JCheckBox getCheckConsumableFilter() {
        return checkConsumableFilter;
    }

    protected JCheckBox getCheckReturnableFilter() {
        return checkReturnableFilter;
    }

    protected JCheckBox getCheckScrapFilter() {
        return checkScrapFilter;
    }

    protected JButton getBtnRecipientFilter() {
        return btnRecipientFilter;
    }

    protected JTextField getTfRecipientFilter() {
        return tfRecipientFilter;
    }

    protected DateRange getDateRange() {
        return this.dateRange;
    }

    protected RecipientsImagePanel getRecipientsImagePanel() {
        return recipientsImagePanel;
    }
}
