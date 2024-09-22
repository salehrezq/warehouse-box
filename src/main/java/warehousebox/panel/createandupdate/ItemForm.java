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
package warehousebox.panel.createandupdate;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import warehousebox.utility.imagefilechooser.IMGFileChooser;
import warehousebox.utility.singularlisting.LoadMoreEnabledListener;

/**
 *
 * @author Saleh
 */
public class ItemForm extends JPanel
        implements Navigatable,
        LoadMoreEnabledListener {

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
    private List<Component> order;
    private FocusTraversalPolicyCreateItemDialoge focusTraversalPolicyForCreateItemDialoge;
    private boolean loadmorebuttonadded;

    public ItemForm() {
        collectables = new ArrayList<>();
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        panelCards = new JPanel(cardLayout);
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
        setupFocusTraversPolicy();
        add(panelCards, BorderLayout.CENTER);
        add(formManagement, BorderLayout.PAGE_END);
    }

    private void linkListenersToChangeProviders(ItemFormImage itemFormImage) {
        iMGFileChooser = itemFormImage.getIMGFileChooser();
        iMGFileChooser.addImageSelectedListener(itemFormImage);
        iMGFileChooser.addFilesSelectionLimitListener(itemFormImage);
        itemFormImage.addImageRemovedListener(iMGFileChooser);
    }

    private void setupFocusTraversPolicy() {
        itemFormTextFields.getListableItemForm().addLoadMoreEnabledListener(this);
        order = new ArrayList<>();
        order.add(itemFormTextFields.getTfName());
        order.add(itemFormTextFields.getTfSpecs());
        order.add(itemFormTextFields.getListableItemForm().getTfSearch());
        order.add(itemFormTextFields.getListableItemForm().getBtnSearch());
        order.add(itemFormTextFields.getListableItemForm().getlist());
        order.add(formManagement.getBtnNext());
        focusTraversalPolicyForCreateItemDialoge = new FocusTraversalPolicyCreateItemDialoge(order);
        this.setFocusCycleRoot(true);
        this.setFocusTraversalPolicy(focusTraversalPolicyForCreateItemDialoge);
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

    @Override
    public void loadMoreEnabled(boolean enabled) {
        if (enabled && !loadmorebuttonadded) {
            // Button "load more " added to focus cycle list
            order.add(5, itemFormTextFields.getListableItemForm().getBtnLoadMore());
            loadmorebuttonadded = true;
        } else {
            if (loadmorebuttonadded) {
                // Button "load more " removed from focus cycle list
                order.remove(5);
                loadmorebuttonadded = false;
            }
        }
    }

    public static class FocusTraversalPolicyCreateItemDialoge
            extends FocusTraversalPolicy {

        private List<Component> order;

        public FocusTraversalPolicyCreateItemDialoge(List<Component> order) {
            this.order = order;
        }

        @Override
        public Component getComponentAfter(Container focusCycleRoot,
                Component aComponent) {
            int idx = (order.indexOf(aComponent) + 1) % order.size();
            return order.get(idx);
        }

        @Override
        public Component getComponentBefore(Container focusCycleRoot,
                Component aComponent) {
            int idx = order.indexOf(aComponent) - 1;
            if (idx < 0) {
                idx = order.size() - 1;
            }
            return order.get(idx);
        }

        @Override
        public Component getDefaultComponent(Container focusCycleRoot) {
            return order.get(0);
        }

        @Override
        public Component getLastComponent(Container focusCycleRoot) {
            return order.getLast();
        }

        @Override
        public Component getFirstComponent(Container focusCycleRoot) {
            return order.get(0);
        }
    }

}
