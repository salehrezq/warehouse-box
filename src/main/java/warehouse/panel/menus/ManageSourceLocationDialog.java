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
package warehouse.panel.menus;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import warehouse.db.CRUDSourceLocation;
import warehouse.db.model.SourceLocation;

/**
 *
 * @author Saleh
 */
public class ManageSourceLocationDialog extends Dialog {

    private JPanel panel;
    private MigLayout mig;
    private JLabel lbSourceLocation;
    private JTextField tfSourceLocation;
    private JButton btnSubmit;
    private List list;
    private SourceLocation sourceLocation;

    public ManageSourceLocationDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        mig = new MigLayout("center center");
        panel = new JPanel(mig);

        lbSourceLocation = new JLabel("Source location:");
        tfSourceLocation = new JTextField(15);
        btnSubmit = new JButton("Submit");
        btnSubmit.addActionListener(new SubmitListener());
        list = new List();

        panel.add(lbSourceLocation);
        panel.add(tfSourceLocation);
        panel.add(btnSubmit, "wrap");
        panel.add(list.getList(), "span");
        add(panel);
    }

    private class SubmitListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == btnSubmit) {
                sourceLocation = new SourceLocation();
                sourceLocation.setSourceLocation(tfSourceLocation.getText());
                CRUDSourceLocation.create(sourceLocation);
                System.out.println("submit dialoge");
                ManageSourceLocationDialog.this.dispose();
            }
        }
    }
}
