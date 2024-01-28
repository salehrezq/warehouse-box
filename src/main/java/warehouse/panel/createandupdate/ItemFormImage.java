/*
 * The MIT License
 *
 * Copyright 2024 Saleh.
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
package warehouse.panel.createandupdate;

import utility.imagefilechooser.IMGFileChooser;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import utility.horizontalspinner.SpinnerH;
import utility.imagepane.ScrollableScalableImageContainer;
import utility.imagefilechooser.ImagesSelectedListener;
import warehouse.db.model.Image;

/**
 *
 * @author Saleh
 */
@SuppressWarnings("LeakingThisInConstructor")
public class ItemFormImage implements ImagesSelectedListener, Collectable {

    private JPanel panelContainer, panelContols;
    private JButton btnBrowse;
    private ScrollableScalableImageContainer scalableImageContainer;
    private IMGFileChooser iMGFileChooser;
    private BufferedImage imageSelected;
    private Map data;
    private SpinnerH spinnerH;

    public ItemFormImage() {
        data = new HashMap<String, BufferedImage>();
        panelContainer = new JPanel(new BorderLayout());
        scalableImageContainer = new ScrollableScalableImageContainer();
        panelContols = new JPanel();
        btnBrowse = new JButton("Browse...");
        spinnerH = new SpinnerH();
        spinnerH.setModel(0, 0, 0, 1);
        panelContols.add(btnBrowse);
        panelContols.add(spinnerH.getSpinner());
        panelContainer.add(scalableImageContainer.getContainer(), BorderLayout.CENTER);
        panelContainer.add(panelContols, BorderLayout.PAGE_END);
        iMGFileChooser = new IMGFileChooser();
        btnBrowse.addActionListener(iMGFileChooser);
        iMGFileChooser.addImageSelectedListener(this);
    }

    public JScrollPane getScrollableScalableImageContainer() {
        return scalableImageContainer.getContainer();
    }

    public JPanel getFormContainer() {
        return panelContainer;
    }

    @Override
    public void imagesSelected(ArrayList<Image> images) {
        int spinnerSize = images.size();
        int spinnerValue = 0;
        for (Image image : images) {
            if (image.isDefaultImage()) {
                scalableImageContainer.setBufferedImage(image.getBufferedImage());
                spinnerValue = image.getOrder();
            }
        }
        spinnerH.setModel(spinnerValue, 1, spinnerSize, 1);
    }

    @Override
    public Map collect() {
        data.put("image", imageSelected);
        return data;
    }

}
