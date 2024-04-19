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
package warehouse.panel.inwards;

import warehouse.panel.items.*;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import warehouse.db.model.QuantityUnit;

/**
 *
 * @author Saleh
 */
public class InwardsTap extends JPanel {

    private JSplitPane splitRightAndLeftPanes;
    private JSplitPane splitSearchAndItemsListPane;
    private JPanel panelGatherLeft;
    private JPanel panelGatherRight;
    private ItemsSearch itemsSearch;
    private InwardsList inwardsList;
    private ItemImage itemsImages;

    public InwardsTap() {

        setLayout(new BorderLayout());
        panelGatherLeft = new JPanel(new BorderLayout());
        panelGatherRight = new JPanel(new BorderLayout());

        itemsSearch = new ItemsSearch();
        inwardsList = new InwardsList();
        inwardsList.setListableImpl(new QuantityUnit());
        inwardsList.loadDBInwards();
        itemsImages = new ItemImage();

        inwardsList.addRowIdSelectionListener(itemsImages);

        // Add the scroll panes to a split pane.
        splitSearchAndItemsListPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitSearchAndItemsListPane.setDividerSize(5);
        splitSearchAndItemsListPane.setTopComponent(itemsSearch);
        splitSearchAndItemsListPane.setBottomComponent(inwardsList);
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
