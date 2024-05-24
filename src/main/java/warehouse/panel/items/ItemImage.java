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

import utility.imagepane.ScrollableScalableImageContainer;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import utility.horizontalspinner.Renderer;
import utility.horizontalspinner.SpinnerH;
import warehouse.db.CRUDImages;
import warehouse.db.model.Image;
import warehouse.db.model.Item;

/**
 *
 * @author Saleh
 */
public class ItemImage
        implements
        RowIdSelectionListener,
        ItemCRUDListener {

    private JPanel panelContainer, panelContols;
    private ScrollableScalableImageContainer scalableImageContainer;
    private BufferedImage imageSelected;
    private Map data;
    private SpinnerH spinnerH;
    private HashMap<Integer, Image> imagesMap;
    private ArrayList<Image> imagesSelected;
    private int spinnerValueOnSpinning;

    public ItemImage() {
        data = new HashMap<String, BufferedImage>();
        imagesMap = new HashMap<>();
        panelContainer = new JPanel(new BorderLayout());
        scalableImageContainer = new ScrollableScalableImageContainer();
        panelContols = new JPanel();
        spinnerH = new SpinnerH();
        spinnerH.setModel(0, 0, 0, 1);
        spinnerH.getSpinner().addChangeListener(new JSpinnerHandler());
        panelContols.add(spinnerH.getSpinner());
        panelContols.add(Box.createHorizontalStrut(4));
        panelContainer.add(scalableImageContainer.getContainer(), BorderLayout.CENTER);
        panelContainer.add(panelContols, BorderLayout.PAGE_END);
    }

    public JScrollPane getScrollableScalableImageContainer() {
        return scalableImageContainer.getContainer();
    }

    public JPanel getFormContainer() {
        return panelContainer;
    }

    public void clearFields() {
        spinnerValueOnSpinning = 0;
        imagesMap.clear();
        scalableImageContainer.setBufferedImage(null);
        spinnerH.setModel(0, 0, 0, 1);
    }

    private void setImagesOfSelectedItem(int itemId) {
        List<Image> images = CRUDImages.getImagesByItemId(itemId);
        int imagesCount = images.size();
        int spinnerValue = 0;
        if (imagesCount > 0) {
            for (Image image : images) {
                imagesMap.put(image.getOrder(), image);
                if (image.isDefaultImage()) {
                    scalableImageContainer.setBufferedImage(image.getBufferedImage());
                    spinnerValue = image.getOrder();
                    spinnerValueOnSpinning = spinnerValue;
                }
            }
        } else {
            spinnerValueOnSpinning = 0;
            imagesMap.clear();
            scalableImageContainer.setBufferedImage(null);
        }
        spinnerH.setModel(spinnerValue, (imagesCount > 0) ? 1 : 0, imagesCount, 1);
    }

    @Override
    public void selectedRowId(int rowId) {
        setImagesOfSelectedItem(rowId);
    }

    @Override
    public void created(Item createdItem) {
        // Not required.
    }

    @Override
    public void updated(Item updatedItem) {
        setImagesOfSelectedItem(updatedItem.getId());
    }

    private class JSpinnerHandler implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            if (!imagesMap.isEmpty()) {
                JSpinner spinner = (JSpinner) e.getSource();
                Renderer renderer = (Renderer) spinner.getValue();
                spinnerValueOnSpinning = renderer.getValue();
                Image image = imagesMap.get(spinnerValueOnSpinning);
                scalableImageContainer.setBufferedImage(image.getBufferedImage());
            }
        }
    }

}
