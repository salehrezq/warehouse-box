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

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import warehousebox.db.model.QuantityUnit;
import warehousebox.utility.singularlisting.ListableItemFormForFilters;

/**
 *
 * @author Saleh
 */
public class InboundsTap extends JPanel {

    private JSplitPane splitRightAndLeftPanes;
    private JSplitPane splitSearchAndItemsListPane;
    private JPanel panelGatherLeft,
            panelGatherRight,
            panelInboundsList;
    private ItemsSearchPane inboundsSearchPane;
    private ItemsSearchLogic inboundsSearchLogic;
    private InboundsList inboundsList;
    private ItemImage itemImage;
    private RowAttributesDisplay rowAttributesDisplay;

    public InboundsTap() {

        setLayout(new BorderLayout());
        panelGatherLeft = new JPanel(new BorderLayout());
        panelGatherRight = new JPanel(new BorderLayout());

        inboundsSearchPane = new ItemsSearchPane();
        panelInboundsList = new JPanel(new BorderLayout());
        inboundsList = new InboundsList();
        inboundsList.setListableImpl(new QuantityUnit());
        rowAttributesDisplay = new RowAttributesDisplay();
        inboundsList.setnameAndSpecDisplayFields(rowAttributesDisplay);
        panelInboundsList.add(rowAttributesDisplay.getContainer(), BorderLayout.PAGE_START);
        panelInboundsList.add(inboundsList, BorderLayout.CENTER);
        inboundsSearchLogic = new ItemsSearchLogic();
        inboundsSearchLogic.addItemSearchListener(inboundsList);
        inboundsSearchLogic.setTfSearchQuery(inboundsSearchPane.getTfSearchQuery());
        inboundsSearchLogic.setBtnSearch(inboundsSearchPane.getBtnSearchQuery());
        inboundsSearchLogic.setCheckFilters(
                inboundsSearchPane.getCheckIdFilter(),
                inboundsSearchPane.getCheckNameFilter(),
                inboundsSearchPane.getCheckSpecificationFilter());
        inboundsSearchLogic.setBtnSourceFilter(inboundsSearchPane.getBtnSourceFilter());
        inboundsSearchLogic.setTfSourceFilter(inboundsSearchPane.getTfSourceFilter());
        inboundsSearchLogic.setBtnRemoveSource(inboundsSearchPane.getBtnRemoveSource());
        inboundsSearchLogic.setImageIconRemoveNormal(inboundsSearchPane.getImageIconRemoveNormal());
        inboundsSearchLogic.setImageIconRemoveHover(inboundsSearchPane.getImageIconRemoveHover());
        inboundsSearchLogic.setImageIconRemovePress(inboundsSearchPane.getImageIconRemovePress());
        inboundsSearchLogic.setBtnLoadMore(inboundsList.getBtnLoadMore());
        inboundsSearchLogic.setDateRangeFilter(inboundsSearchPane.getDateRange());
        inboundsSearchLogic.initializeFiltersReactToRetrievedPreferences();
        ListableItemFormForFilters listableItemFormForFilters = inboundsSearchLogic.getListableItemFormForFilters();
        listableItemFormForFilters.addListableItemFormForFiltersListener(inboundsSearchLogic);

        itemImage = new ItemImage();

        inboundsList.addRowIdSelectionListener(itemImage);

        // Add the scroll panes to a split pane.
        splitSearchAndItemsListPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitSearchAndItemsListPane.setDividerSize(5);
        splitSearchAndItemsListPane.setTopComponent(inboundsSearchPane.getContainer());
        splitSearchAndItemsListPane.setBottomComponent(panelInboundsList);
        panelGatherLeft.add(splitSearchAndItemsListPane, BorderLayout.CENTER);
        panelGatherRight.add(itemImage.getFormContainer(), BorderLayout.CENTER);

        splitRightAndLeftPanes = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitRightAndLeftPanes.setDividerSize(5);
        splitRightAndLeftPanes.setTopComponent(panelGatherLeft);
        splitRightAndLeftPanes.setBottomComponent(panelGatherRight);
        splitRightAndLeftPanes.setDividerLocation(700);
        add(splitRightAndLeftPanes);
    }

    public InboundsList getInboundsList() {
        return inboundsList;
    }
}
