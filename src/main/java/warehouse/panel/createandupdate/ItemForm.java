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

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 *
 * @author Saleh
 */
public class ItemForm extends JPanel implements Navigatable {

    private JPanel cards;
    private CardLayout cardLayout;
    private final static String FORMCODENAMESPECS = "Card code name specs";
    private final static String LOCATION = "Card locatiob";
    private ItemFormCodeNameSpecs itemFormCodeNameSpecs;
    private ItemFormLocation itemFormLocation;
    private FormNavigation formNavigation;
    private BoxLayout boxLayout;

    public ItemForm() {
        boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxLayout);
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        cards.setMaximumSize(new Dimension(250, 400));
        cards.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        itemFormCodeNameSpecs = new ItemFormCodeNameSpecs();
        itemFormLocation = new ItemFormLocation();
        cards.add(itemFormCodeNameSpecs, FORMCODENAMESPECS);
        cards.add(itemFormLocation, LOCATION);
        cardLayout.show(cards, FORMCODENAMESPECS);
        formNavigation = new FormNavigation();
        formNavigation.setMaximumSize(new Dimension(200, 50));
        formNavigation.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        add(cards);
        add(formNavigation);
    }

    protected FormNavigation getFormNavigation() {
        return this.formNavigation;
    }

    @Override
    public void next() {
        System.out.println("Next clicked");
    }

    @Override
    public void previous() {
        System.out.println("Previous clicked");
    }

}