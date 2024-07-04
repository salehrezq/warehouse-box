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
package warehouse.panel.inwards;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JDialog;
import warehouse.db.model.Source;
import warehouse.singularlisting.ListableItemFormForFilters;

/**
 *
 * @author Saleh
 */
public class SourceFilterDialog extends JDialog {

    private ListableItemFormForFilters formListableItemFormClearSelection;

    public SourceFilterDialog() {
        setLayout(new BorderLayout());
        formListableItemFormClearSelection = new ListableItemFormForFilters();
        formListableItemFormClearSelection.setListableImpl(new Source());
        add(formListableItemFormClearSelection, BorderLayout.CENTER);
        this.setMinimumSize(new Dimension(400, 400));
    }

    public void setDialogeToListableItemFormForFilters() {
        formListableItemFormClearSelection.setDialoge(this);
    }
}
