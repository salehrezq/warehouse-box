/*
 * The MIT License
 *
 * Copyright 2025 Saleh.
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
package warehousebox.panel.menus.recipients.form;

import javax.swing.JButton;
import javax.swing.JTextField;
import warehousebox.panel.menus.recipients.form.imagefilechooser.IMGFileChooser;

/**
 *
 * @author Saleh
 */
public class RecipientsFormLogic {

    private JTextField tfName;
    private JButton btnBrowse, btnSubmit;
    private IMGFileChooser iMGFileChooser;
    private RecipientsImagePanel recipientsImagePanel;

    public RecipientsFormLogic(RecipientsFormControls rc) {
        btnBrowse = rc.getBtnBrowse();
        recipientsImagePanel = rc.getRecipientsImagePanel();
        btnSubmit = rc.getBtnSubmit();
        tfName = rc.getTfName();

        iMGFileChooser = new IMGFileChooser();
        iMGFileChooser.addImageSelectedListener(recipientsImagePanel);
        btnBrowse.addActionListener(iMGFileChooser);
    }

}
