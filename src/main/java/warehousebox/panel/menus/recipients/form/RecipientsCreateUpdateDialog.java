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

import java.awt.Frame;
import javax.swing.JDialog;
import warehousebox.db.model.Recipient;

/**
 *
 * @author Saleh
 */
public class RecipientsCreateUpdateDialog extends JDialog implements RecipientCRUDListener {

    private RecipientsFormControls recipientsFormControls;
    private RecipientsFormLogic recipientsFormLogic;

    public RecipientsCreateUpdateDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        recipientsFormControls = new RecipientsFormControls();
        recipientsFormLogic = new RecipientsFormLogic(recipientsFormControls);
        add(recipientsFormControls.getContainer());
        setSize(400, 600);
    }

    @Override
    public void created(Recipient recipient) {
        this.dispose();
    }

    @Override
    public void updated(Recipient recipient) {
        this.dispose();
    }

    @Override
    public void noCRUD() {
        this.dispose();
    }

    public void addThisToRecipientCRUDListener() {
        recipientsFormLogic.addRecipientCRUDListener(this);
    }

    public RecipientsFormControls getRecipientsFormControls() {
        return recipientsFormControls;
    }
}
