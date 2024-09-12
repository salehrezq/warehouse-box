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
package warehousebox.panel.inwards;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.prefs.Preferences;
import javax.swing.JDialog;
import warehousebox.db.model.Source;
import warehousebox.utility.singularlisting.ListableItemFormForFilters;

/**
 *
 * @author Saleh
 */
public class SourceFilterDialog extends JDialog {

    private ListableItemFormForFilters listableItemFormForFilters;

    public SourceFilterDialog() {
        setLayout(new BorderLayout());
        listableItemFormForFilters = new ListableItemFormForFilters();
        listableItemFormForFilters.setListableImpl(new Source());
        add(listableItemFormForFilters, BorderLayout.CENTER);
        this.setMinimumSize(new Dimension(400, 400));
    }

    protected void setDialogeToListableItemFormForFilters() {
        listableItemFormForFilters.setDialoge(this);
    }

    protected void setPreferencesKey(String prefsKey) {
        listableItemFormForFilters.setPreferencesKey(prefsKey);
    }

    protected void setPreferences(Preferences preferences) {
        listableItemFormForFilters.setPreferences(preferences);
    }

    protected ListableItemFormForFilters getListableItemFormForFilters() {
        return listableItemFormForFilters;
    }

}
