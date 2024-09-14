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
package warehousebox.panel.inwards;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import warehousebox.db.model.QuantityUnit;
import warehousebox.utility.singularlisting.ListableItemFormForFilters;

/**
 *
 * @author Saleh
 */
public class InwardsTap extends JPanel {

    private JSplitPane splitRightAndLeftPanes;
    private JSplitPane splitSearchAndItemsListPane;
    private JPanel panelGatherLeft,
            panelGatherRight,
            panelInwardsList;
    private ItemsSearchPane inwardsSearchPane;
    private ItemsSearchLogic inwardsSearchLogic;
    private InwardsList inwardsList;
    private ItemImage itemsImages;
    private RowAttributesDisplay rowAttributesDisplay;

    public InwardsTap() {

        setLayout(new BorderLayout());
        panelGatherLeft = new JPanel(new BorderLayout());
        panelGatherRight = new JPanel(new BorderLayout());

        inwardsSearchPane = new ItemsSearchPane();
        panelInwardsList = new JPanel(new BorderLayout());
        inwardsList = new InwardsList();
        inwardsList.setListableImpl(new QuantityUnit());
        rowAttributesDisplay = new RowAttributesDisplay();
        inwardsList.setnameAndSpecDisplayFields(rowAttributesDisplay);
        panelInwardsList.add(rowAttributesDisplay.getContainer(), BorderLayout.PAGE_START);
        panelInwardsList.add(inwardsList, BorderLayout.CENTER);
        inwardsSearchLogic = new ItemsSearchLogic();
        inwardsSearchLogic.addItemSearchListener(inwardsList);
        inwardsSearchLogic.setTfSearchQuery(inwardsSearchPane.getTfSearchQuery());
        inwardsSearchLogic.setBtnSearch(inwardsSearchPane.getBtnSearchQuery());
        inwardsSearchLogic.setCheckFilters(
                inwardsSearchPane.getCheckCodeFilter(),
                inwardsSearchPane.getCheckNameFilter(),
                inwardsSearchPane.getCheckSpecificationFilter());
        inwardsSearchLogic.setBtnSourceFilter(inwardsSearchPane.getBtnSourceFilter());
        inwardsSearchLogic.setTfSourceFilter(inwardsSearchPane.getTfSourceFilter());
        inwardsSearchLogic.setBtnRemoveSource(inwardsSearchPane.getBtnRemoveSource());
        inwardsSearchLogic.setImageIconRemoveNormal(inwardsSearchPane.getImageIconRemoveNormal());
        inwardsSearchLogic.setImageIconRemoveHover(inwardsSearchPane.getImageIconRemoveHover());
        inwardsSearchLogic.setImageIconRemovePress(inwardsSearchPane.getImageIconRemovePress());
        inwardsSearchLogic.setBtnLoadMore(inwardsList.getBtnLoadMore());
        inwardsSearchLogic.setDateRangeFilter(inwardsSearchPane.getDateRange());
        inwardsSearchLogic.initializeFiltersReactToRetrievedPreferences();
        ListableItemFormForFilters listableItemFormForFilters = inwardsSearchLogic.getListableItemFormForFilters();
        listableItemFormForFilters.addListableItemFormForFiltersListener(inwardsSearchLogic);

        itemsImages = new ItemImage();

        inwardsList.addRowIdSelectionListener(itemsImages);

        // Add the scroll panes to a split pane.
        splitSearchAndItemsListPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitSearchAndItemsListPane.setDividerSize(5);
        splitSearchAndItemsListPane.setTopComponent(inwardsSearchPane.getContainer());
        splitSearchAndItemsListPane.setBottomComponent(panelInwardsList);
        panelGatherLeft.add(splitSearchAndItemsListPane, BorderLayout.CENTER);
        panelGatherRight.add(itemsImages.getFormContainer(), BorderLayout.CENTER);

        splitRightAndLeftPanes = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitRightAndLeftPanes.setDividerSize(5);
        splitRightAndLeftPanes.setTopComponent(panelGatherLeft);
        splitRightAndLeftPanes.setBottomComponent(panelGatherRight);
        splitRightAndLeftPanes.setDividerLocation(700);
        add(splitRightAndLeftPanes);
    }

    public InwardsList getInwardsList() {
        return inwardsList;
    }
}