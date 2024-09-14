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
package warehousebox.panel.inbounds;

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
import warehousebox.utility.scrollbarthin.ScrollBarThin;

/**
 *
 * @author Saleh
 */
public class ItemsSearchPane {

    private JPanel container;
    private JTextField tfSearchQuery, tfSourceFilter;
    private ImageIcon imageIconRemoveNormal, imageIconRemoveHover, imageIconRemovePress;
    private ScrollBarThin scrollBarThinTfSearchQuery, scrollBarThinTfSourceFilter;
    private JButton btnSearchQuery, btnSourceFilter;
    private JLabel btnRemoveSource;
    private JCheckBox checkCodeFilter,
            checkNameFilter,
            checkSpecificationFilter;
    private DateRange dateRange;
    private final Color colorTextField = new Color(84, 84, 84);

    public ItemsSearchPane() {
        container = new JPanel();

        // Setup Text field search query:
        tfSearchQuery = new JTextField(35);
        scrollBarThinTfSearchQuery = new ScrollBarThin(Adjustable.HORIZONTAL);
        scrollBarThinTfSearchQuery.setModel(tfSearchQuery.getHorizontalVisibility());
        Box boxSearchQueryField = Box.createVerticalBox();
        boxSearchQueryField.add(tfSearchQuery);
        boxSearchQueryField.add(scrollBarThinTfSearchQuery);
        // --
        btnSearchQuery = new JButton("Search");
        checkCodeFilter = new JCheckBox("Code");
        checkNameFilter = new JCheckBox("Name");
        checkSpecificationFilter = new JCheckBox("Specification");
        btnSourceFilter = new JButton("Source...");
        tfSourceFilter = new JTextField(15);
        tfSourceFilter.setEditable(false);
        tfSourceFilter.setDisabledTextColor(colorTextField);
        scrollBarThinTfSourceFilter = new ScrollBarThin(Adjustable.HORIZONTAL);
        scrollBarThinTfSourceFilter.setModel(tfSourceFilter.getHorizontalVisibility());
        imageIconRemoveNormal = getTransformedImageIcon("/images/remove-icon-circular/remove-normal.png");
        imageIconRemoveHover = getTransformedImageIcon("/images/remove-icon-circular/remove-hovered.png");
        imageIconRemovePress = getTransformedImageIcon("/images/remove-icon-circular/remove-pressed.png");
        btnRemoveSource = new JLabel();
        btnRemoveSource.setOpaque(false);
        btnRemoveSource.setIcon(imageIconRemoveNormal);
        Box boxSourceFilterField = Box.createVerticalBox();
        boxSourceFilterField.add(tfSourceFilter);
        boxSourceFilterField.add(scrollBarThinTfSourceFilter);

        dateRange = new DateRange();

        Box boxSearch = Box.createHorizontalBox();
        boxSearch.add(boxSearchQueryField);
        boxSearch.add(btnSearchQuery);
        Box boxFilters = Box.createHorizontalBox();
        boxFilters.add(checkCodeFilter);
        boxFilters.add(Box.createHorizontalStrut(5));
        boxFilters.add(checkNameFilter);
        boxFilters.add(Box.createHorizontalStrut(5));
        boxFilters.add(checkSpecificationFilter);
        boxFilters.add(Box.createHorizontalStrut(5));
        boxFilters.add(btnSourceFilter);
        boxFilters.add(Box.createHorizontalStrut(2));
        boxFilters.add(boxSourceFilterField);
        boxFilters.add(Box.createHorizontalStrut(2));
        boxFilters.add(btnRemoveSource);
        Box boxContainer = Box.createVerticalBox();
        boxContainer.add(boxSearch);
        boxContainer.add(boxFilters);
        boxContainer.add(dateRange.getContainer());
        container.add(boxContainer);
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

    protected JLabel getBtnRemoveSource() {
        return btnRemoveSource;
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

    protected JButton getBtnSearchQuery() {
        return btnSearchQuery;
    }

    protected JCheckBox getCheckCodeFilter() {
        return checkCodeFilter;
    }

    protected JCheckBox getCheckNameFilter() {
        return checkNameFilter;
    }

    protected JCheckBox getCheckSpecificationFilter() {
        return checkSpecificationFilter;
    }

    protected JButton getBtnSourceFilter() {
        return btnSourceFilter;
    }

    protected JTextField getTfSourceFilter() {
        return tfSourceFilter;
    }

    protected DateRange getDateRange() {
        return this.dateRange;
    }

}
