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
package warehousebox.panel.items;

import java.awt.Adjustable;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import warehousebox.utility.scrollbarthin.ScrollBarThin;

/**
 *
 * @author Saleh
 */
public class ItemsSearchPane extends JPanel {

    private JPanel container;
    private JTextField tfSearchQuery;
    private ScrollBarThin scrollBarThinTfSearchQuery;
    private JButton btnSearchQuery;
    private JCheckBox checkIdFilter,
            checkNameFilter,
            checkSpecificationFilter;

    public ItemsSearchPane() {
        container = new JPanel();

        // Setup Text field search query
        tfSearchQuery = new JTextField(35);
        scrollBarThinTfSearchQuery = new ScrollBarThin(Adjustable.HORIZONTAL);
        scrollBarThinTfSearchQuery.setModel(tfSearchQuery.getHorizontalVisibility());
        Box boxSearchQueryField = Box.createVerticalBox();
        boxSearchQueryField.add(tfSearchQuery);
        boxSearchQueryField.add(scrollBarThinTfSearchQuery);

        btnSearchQuery = new JButton("Search");

        checkIdFilter = new JCheckBox("Id");
        checkNameFilter = new JCheckBox("Name");
        checkSpecificationFilter = new JCheckBox("Specification");

        Box boxSearch = Box.createHorizontalBox();
        boxSearch.add(boxSearchQueryField);
        boxSearch.add(btnSearchQuery);
        Box boxFilters = Box.createHorizontalBox();
        boxFilters.add(checkIdFilter);
        boxFilters.add(Box.createHorizontalStrut(5));
        boxFilters.add(checkNameFilter);
        boxFilters.add(Box.createHorizontalStrut(5));
        boxFilters.add(checkSpecificationFilter);
        Box boxContainer = Box.createVerticalBox();
        boxContainer.add(boxSearch);
        boxContainer.add(boxFilters);
        container.add(boxContainer);
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

    protected JCheckBox getCheckIdFilter() {
        return checkIdFilter;
    }

    protected JCheckBox getCheckNameFilter() {
        return checkNameFilter;
    }

    protected JCheckBox getCheckSpecificationFilter() {
        return checkSpecificationFilter;
    }

}
