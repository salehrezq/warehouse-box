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
package warehouse.panel.items;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 *
 * @author Saleh
 */
public class ItemsTab extends JPanel {

    private JSplitPane splitRightAndLeftPanes;
    private JSplitPane splitSearchAndItemsListPane;
    private JPanel panelGatherLeft;
    private JPanel panelGatherRight;
    private ItemsSearchPane itemsSearchPane;
    private ItemsSearchLogic itemsSearchLogic;
    private JPanel panelItemsList;
    private NameAndSpecDisplayFields nameAndSpecDisplayFields;
    private ItemsList itemsList;
    private ItemImage itemsImages;

    public ItemsTab() {

        setLayout(new BorderLayout());
        panelGatherLeft = new JPanel(new BorderLayout());
        panelGatherRight = new JPanel(new BorderLayout());

        itemsSearchPane = new ItemsSearchPane();
        panelItemsList = new JPanel(new BorderLayout());
        nameAndSpecDisplayFields = new NameAndSpecDisplayFields();
        itemsList = new ItemsList();
        itemsList.setnameAndSpecDisplayFields(nameAndSpecDisplayFields);
        panelItemsList.add(nameAndSpecDisplayFields.getContainer(), BorderLayout.PAGE_START);
        panelItemsList.add(itemsList, BorderLayout.CENTER);
        itemsSearchLogic = new ItemsSearchLogic();
        itemsSearchLogic.addItemSearchListener(itemsList);
        itemsSearchLogic.setTfSearchQuery(itemsSearchPane.getTfSearchQuery());
        itemsSearchLogic.setBtnSearch(itemsSearchPane.getBtnSearchQuery());
        itemsSearchLogic.setCheckCodeFilter(itemsSearchPane.getCheckCodeFilter());
        itemsSearchLogic.setCheckNameFilter(itemsSearchPane.getCheckNameFilter());
        itemsSearchLogic.setCheckSpecificationFilter(itemsSearchPane.getCheckSpecificationFilter());
        itemsSearchLogic.setBtnLoadMore(itemsList.getBtnLoadMore());
        ItemsSearchLogic.setResultsPageLimit(3);
        itemsImages = new ItemImage();

        itemsList.addRowIdSelectionListener(itemsImages);

        // Add the scroll panes to a split pane.
        splitSearchAndItemsListPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitSearchAndItemsListPane.setDividerSize(5);
        splitSearchAndItemsListPane.setTopComponent(itemsSearchPane.getContainer());
        splitSearchAndItemsListPane.setBottomComponent(panelItemsList);

        panelGatherLeft.add(splitSearchAndItemsListPane, BorderLayout.CENTER);
        panelGatherRight.add(itemsImages.getFormContainer(), BorderLayout.CENTER);

        splitRightAndLeftPanes = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitRightAndLeftPanes.setDividerSize(5);
        splitRightAndLeftPanes.setTopComponent(panelGatherLeft);
        splitRightAndLeftPanes.setBottomComponent(panelGatherRight);
        splitRightAndLeftPanes.setDividerLocation(700);
        add(splitRightAndLeftPanes);
    }

    public ItemsList getItemsList() {
        return itemsList;
    }

    public ItemImage getItemImage() {
        return itemsImages;
    }

}
