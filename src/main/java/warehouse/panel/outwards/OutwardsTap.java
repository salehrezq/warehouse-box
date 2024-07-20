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
package warehouse.panel.outwards;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import warehouse.db.model.QuantityUnit;
import warehouse.singularlisting.ListableItemFormForFilters;

/**
 *
 * @author Saleh
 */
public class OutwardsTap extends JPanel {

    private JSplitPane splitRightAndLeftPanes;
    private JSplitPane splitSearchAndItemsListPane;
    private JPanel panelGatherLeft,
            panelGatherRight,
            panelOutwardsList;
    private ItemsSearchPane outwardsSearchPane;
    private ItemsSearchLogic outwardsSearchLogic;
    private OutwardsList outwardsList;
    private ItemImage itemsImages;
    private NameAndSpecDisplayFields nameAndSpecDisplayFields;

    public OutwardsTap() {

        setLayout(new BorderLayout());
        panelGatherLeft = new JPanel(new BorderLayout());
        panelGatherRight = new JPanel(new BorderLayout());
        panelOutwardsList = new JPanel(new BorderLayout());
        outwardsSearchPane = new ItemsSearchPane();
        outwardsList = new OutwardsList();
        outwardsList.setListableImpl(new QuantityUnit());
        nameAndSpecDisplayFields = new NameAndSpecDisplayFields();
        outwardsList.setnameAndSpecDisplayFields(nameAndSpecDisplayFields);
        panelOutwardsList.add(nameAndSpecDisplayFields.getContainer(), BorderLayout.PAGE_START);
        panelOutwardsList.add(outwardsList, BorderLayout.CENTER);
        outwardsSearchLogic = new ItemsSearchLogic();
        outwardsSearchLogic.addItemSearchListener(outwardsList);
        outwardsSearchLogic.setTfSearchQuery(outwardsSearchPane.getTfSearchQuery());
        outwardsSearchLogic.setBtnSearch(outwardsSearchPane.getBtnSearchQuery());
        outwardsSearchLogic.setCheckFilters(
                outwardsSearchPane.getCheckCodeFilter(),
                outwardsSearchPane.getCheckNameFilter(),
                outwardsSearchPane.getCheckSpecificationFilter());
        outwardsSearchLogic.setBtnRecipientFilter(outwardsSearchPane.getBtnRecipientFilter());
        outwardsSearchLogic.setTfRecipientFilter(outwardsSearchPane.getTfRecipientFilter());
        outwardsSearchLogic.setBtnLoadMore(outwardsList.getBtnLoadMore());
        outwardsSearchLogic.setDateRangeFilter(outwardsSearchPane.getDateRange());
        outwardsSearchLogic.initializeFiltersReactToRetrievedPreferences();
        ListableItemFormForFilters listableItemFormForFilters = outwardsSearchLogic.getListableItemFormForFilters();
        listableItemFormForFilters.addListableItemFormForFiltersListener(outwardsSearchLogic);

        ItemsSearchLogic.setResultsPageLimit(3);
        itemsImages = new ItemImage();
        outwardsList.addRowIdSelectionListener(itemsImages);
        // Add the scroll panes to a split pane.
        splitSearchAndItemsListPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitSearchAndItemsListPane.setDividerSize(5);
        splitSearchAndItemsListPane.setTopComponent(outwardsSearchPane.getContainer());
        splitSearchAndItemsListPane.setBottomComponent(panelOutwardsList);
        panelGatherLeft.add(splitSearchAndItemsListPane, BorderLayout.CENTER);
        panelGatherRight.add(itemsImages.getFormContainer(), BorderLayout.CENTER);

        splitRightAndLeftPanes = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitRightAndLeftPanes.setDividerSize(5);
        splitRightAndLeftPanes.setTopComponent(panelGatherLeft);
        splitRightAndLeftPanes.setBottomComponent(panelGatherRight);
        splitRightAndLeftPanes.setDividerLocation(700);
        add(splitRightAndLeftPanes);
    }

    public OutwardsList getOutwardsList() {
        return outwardsList;
    }

}
