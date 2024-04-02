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
package warehouse.items.update;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;
import warehouse.db.CRUDItems;
import warehouse.db.CreateListener;
import warehouse.db.model.Image;
import warehouse.db.model.Item;
import warehouse.db.model.QuantityUnit;

/**
 *
 * @author Saleh
 */
public class FormManagement extends JPanel {

    private JButton btnNext, btnPrevious, btnSubmit;
    private ArrayList<Navigatable> navigatables;
    private NavigateButtonsListener navigateButtonsListener;
    private int formLastStep;
    private int navigateTracker;
    private ArrayList<Collectable> collectables;
    private ArrayList<CreateListener> createListeners;

    public FormManagement(ArrayList<Collectable> collectables) {

        this.collectables = collectables;
        navigatables = new ArrayList<>();
        createListeners = new ArrayList<>();
        btnNext = new JButton("Next>>");
        btnPrevious = new JButton("<<Previous");
        btnSubmit = new JButton("Submit");
        navigateButtonsListener = new NavigateButtonsListener();
        btnNext.addActionListener(navigateButtonsListener);
        btnPrevious.addActionListener(navigateButtonsListener);
        btnSubmit.addActionListener(navigateButtonsListener);
        btnSubmit.setEnabled(false);
        btnPrevious.setEnabled(false);

        add(btnPrevious);
        add(btnNext);
        add(btnSubmit);
    }

    public void setFormLastStep(int formLastStep) {
        this.formLastStep = formLastStep - 1;
    }

    public void addNavigationListener(Navigatable navigatable) {
        this.navigatables.add(navigatable);
    }

    public void notifyNext() {
        this.navigatables.forEach((form) -> {
            form.next();
        });
    }

    public void notifyPrevious() {
        this.navigatables.forEach((form) -> {
            form.previous();
        });
    }

    public void addCreateListener(CreateListener createListener) {
        this.createListeners.add(createListener);
    }

    public void notifyCreated() {
        this.createListeners.forEach((createListener) -> {
            createListener.created();
        });
    }

    private class NavigateButtonsListener implements ActionListener {

        Item item;
        ArrayList<Image> images;

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton btnNavigate = (JButton) e.getSource();
            if (btnNavigate == btnNext) {
                navigateTracker++;
                notifyNext();
            } else if (btnNavigate == btnPrevious) {
                navigateTracker--;
                notifyPrevious();
            } else if (btnNavigate == btnSubmit) {
                item = new Item();
                images = new ArrayList<>();
                collectables.forEach((c) -> {
                    if (c instanceof ItemFormTextFields) {
                        Map mapData = c.collect();
                        // If item id is avalible then it is for an update operation. 
                        Object itemId = mapData.get("id");
                        System.out.println("---");
                        System.out.println("following is itemid");
                        System.out.println(itemId);
                        System.out.println("---");
                        if (itemId != null) {
                            item.setId((int) itemId);
                        }
                        item.setName((String) c.collect().get("name"));
                        item.setSpecification((String) c.collect().get("specs"));
                        QuantityUnit qty = (QuantityUnit) c.collect().get("unit");
                        item.setUnitId(qty.getId());
                    } else if (c instanceof ItemFormImage) {
                        images = (ArrayList<Image>) c.collect().get("images");
                    }
                });

                System.out.println(item.getId());
                System.out.println(item.getName());
                System.out.println(item.getSpecification());
                System.out.println(item.getUnitId());

                boolean update = CRUDItems.update(item);
                System.out.println(update ? "Updated" : "Not updated");
//                System.out.println("Newly created Item id " + item.getId());
//                int idOfCreatedItem = item.getId();
//                if (idOfCreatedItem > 0) {
//                    notifyCreated();
//                    // Create images
//                    CRUDImages.create(images, idOfCreatedItem);
//                }
            }
            btnPrevious.setEnabled(navigateTracker > 0);
            btnNext.setEnabled(navigateTracker < formLastStep);
            btnSubmit.setEnabled(navigateTracker == formLastStep);
        }
    }
}
