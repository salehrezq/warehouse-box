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
package warehouse.panel.items;

import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Saleh
 */
public class AddItemsDialog extends JDialog {

    private JPanel container;
    private JTextField tfQuantity;
    private JLabel lbQuantity, lbSource, lbDate;
    private DateField dateField;

    public AddItemsDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        container = new JPanel();
        container.setLayout(new MigLayout("center center"));

        lbQuantity = new JLabel("Quantity");
        tfQuantity = new JTextField(10);

        lbDate = new JLabel("Date");
        dateField = new DateField();

        container.add(lbQuantity);
        container.add(tfQuantity, "wrap");
        container.add(lbDate);
        container.add(dateField.getDatePicker(), "wrap");
        add(container);
        pack();
    }

}
