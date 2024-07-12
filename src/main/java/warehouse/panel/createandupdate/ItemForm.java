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
package warehouse.panel.createandupdate;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import utility.imagefilechooser.IMGFileChooser;

/**
 *
 * @author Saleh
 */
public class ItemForm extends JPanel implements Navigatable {

    private JPanel panelCards;
    private CardLayout cardLayout;
    private final static String FORMTEXTFIELDS = "Form text fields";
    private final static String IMAGEPREVIEW = "Card image";
    private ItemFormTextFields itemFormTextFields;
    private ItemFormImage itemFormImage;
    private FormManagement formManagement;
    private ArrayList<Collectable> collectables;
    private BoxLayout boxLayout;
    private IMGFileChooser iMGFileChooser;

    public ItemForm() {
        collectables = new ArrayList<>();
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        panelCards = new JPanel(cardLayout);
        panelCards.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        itemFormTextFields = new ItemFormTextFields();
        itemFormImage = new ItemFormImage();
        linkListenersToChangeProviders(itemFormImage);
        panelCards.add(itemFormTextFields, FORMTEXTFIELDS);
        panelCards.add(itemFormImage.getFormContainer(), IMAGEPREVIEW);
        collectables.add(itemFormTextFields);
        collectables.add(itemFormImage);
        cardLayout.show(panelCards, FORMTEXTFIELDS);
        formManagement = new FormManagement(collectables);
        formManagement.setFormLastStep(panelCards.getComponentCount());
        formManagement.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        add(panelCards, BorderLayout.CENTER);
        add(formManagement, BorderLayout.PAGE_END);
    }

    private void linkListenersToChangeProviders(ItemFormImage itemFormImage) {
        iMGFileChooser = itemFormImage.getIMGFileChooser();
        iMGFileChooser.addImageSelectedListener(itemFormImage);
        iMGFileChooser.addFilesSelectionLimitListener(itemFormImage);
        itemFormImage.addImageRemovedListener(iMGFileChooser);
    }

    public FormManagement getFormManagement() {
        return this.formManagement;
    }

    public ItemFormTextFields getItemFormTextFields() {
        return this.itemFormTextFields;
    }

    protected ItemFormImage getItemFormImage() {
        return itemFormImage;
    }

    @Override
    public void first() {
        cardLayout.first(panelCards);
    }

    @Override
    public void next() {
        cardLayout.next(panelCards);
    }

    @Override
    public void previous() {
        cardLayout.previous(panelCards);
    }

    public void resetFields() {
        itemFormTextFields.resetFields();
        itemFormImage.resetFields();
        iMGFileChooser.resetFields();
    }

}
