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

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import warehousebox.db.model.QuantityUnit;
import warehousebox.utility.singularlisting.ListableItemFormForFilters;

/**
 *
 * @author Saleh
 */
public class OutboundsTap extends JPanel {

    private JSplitPane splitRightAndLeftPanes;
    private JSplitPane splitSearchAndItemsListPane;
    private JPanel panelGatherLeft,
            panelGatherRight,
            panelOutboundsList;
    private ItemsSearchPane outboundsSearchPane;
    private ItemsSearchLogic outboundsSearchLogic;
    private OutboundsList outboundsList;
    private ItemImage itemsImages;
    private RowAttributesDisplay rowAttributesDisplay;

    public OutboundsTap() {

        setLayout(new BorderLayout());
        panelGatherLeft = new JPanel(new BorderLayout());
        panelGatherRight = new JPanel(new BorderLayout());
        panelOutboundsList = new JPanel(new BorderLayout());
        outboundsSearchPane = new ItemsSearchPane();
        outboundsList = new OutboundsList();
        outboundsList.setListableImpl(new QuantityUnit());
        rowAttributesDisplay = new RowAttributesDisplay();
        outboundsList.setRowAttributesDisplay(rowAttributesDisplay);
        panelOutboundsList.add(rowAttributesDisplay.getContainer(), BorderLayout.PAGE_START);
        panelOutboundsList.add(outboundsList, BorderLayout.CENTER);
        outboundsSearchLogic = new ItemsSearchLogic();
        outboundsSearchLogic.addItemSearchListener(outboundsList);
        outboundsSearchLogic.setTfSearchQuery(outboundsSearchPane.getTfSearchQuery());
        outboundsSearchLogic.setBtnSearch(outboundsSearchPane.getBtnSearchQuery());
        outboundsSearchLogic.setCheckFilters(
                outboundsSearchPane.getCheckIdFilter(),
                outboundsSearchPane.getCheckNameFilter(),
                outboundsSearchPane.getCheckSpecificationFilter(),
                outboundsSearchPane.getCheckNoteFilter());
        outboundsSearchLogic.setBtnRecipientFilter(outboundsSearchPane.getBtnRecipientFilter());
        outboundsSearchLogic.setTfRecipientFilter(outboundsSearchPane.getTfRecipientFilter());
        outboundsSearchLogic.setBtnRemoveRecipient(outboundsSearchPane.getBtnRemoveRecipient());
        outboundsSearchLogic.setImageIconRemoveNormal(outboundsSearchPane.getImageIconRemoveNormal());
        outboundsSearchLogic.setImageIconRemoveHover(outboundsSearchPane.getImageIconRemoveHover());
        outboundsSearchLogic.setImageIconRemovePress(outboundsSearchPane.getImageIconRemovePress());
        outboundsSearchLogic.setBtnLoadMore(outboundsList.getBtnLoadMore());
        outboundsSearchLogic.setDateRangeFilter(outboundsSearchPane.getDateRange());
        outboundsSearchLogic.initializeFiltersReactToRetrievedPreferences();
        ListableItemFormForFilters listableItemFormForFilters = outboundsSearchLogic.getListableItemFormForFilters();
        listableItemFormForFilters.addListableItemFormForFiltersListener(outboundsSearchLogic);

        itemsImages = new ItemImage();
        outboundsList.addRowIdSelectionListener(itemsImages);
        // Add the scroll panes to a split pane.
        splitSearchAndItemsListPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitSearchAndItemsListPane.setDividerSize(5);
        splitSearchAndItemsListPane.setTopComponent(outboundsSearchPane.getContainer());
        splitSearchAndItemsListPane.setBottomComponent(panelOutboundsList);
        panelGatherLeft.add(splitSearchAndItemsListPane, BorderLayout.CENTER);
        panelGatherRight.add(itemsImages.getFormContainer(), BorderLayout.CENTER);

        splitRightAndLeftPanes = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitRightAndLeftPanes.setDividerSize(5);
        splitRightAndLeftPanes.setTopComponent(panelGatherLeft);
        splitRightAndLeftPanes.setBottomComponent(panelGatherRight);
        splitRightAndLeftPanes.setDividerLocation(700);
        add(splitRightAndLeftPanes);
    }

    public OutboundsList getOutboundsList() {
        return outboundsList;
    }

}
