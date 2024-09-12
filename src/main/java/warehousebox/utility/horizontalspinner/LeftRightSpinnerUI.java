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
package warehousebox.utility.horizontalspinner;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicSpinnerUI;

/**
 *
 * @author Saleh
 */
public class LeftRightSpinnerUI extends BasicSpinnerUI {

    public static ComponentUI createUI(JComponent c) {
        return new LeftRightSpinnerUI();
    }

    @Override
    protected Component createNextButton() {
        Component c = createArrowButton(SwingConstants.EAST);
        c.setName("Spinner.nextButton");
        installNextButtonListeners(c);
        return c;
    }

    @Override
    protected Component createPreviousButton() {
        Component c = createArrowButton(SwingConstants.WEST);
        c.setName("Spinner.previousButton");
        installPreviousButtonListeners(c);
        return c;
    }

    // copied from BasicSpinnerUI
    private Component createArrowButton(int direction) {
        JButton button = new BasicArrowButton(direction);
        Border buttonBorder = UIManager.getBorder("Spinner.arrowButtonBorder");
        if (buttonBorder instanceof UIResource) {
            button.setBorder(new CompoundBorder(buttonBorder, null));
        } else {
            button.setBorder(buttonBorder);
        }
        button.setInheritsPopupMenu(true);
        return button;
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        c.removeAll();
        c.setLayout(new BorderLayout() {
            @Override
            public void addLayoutComponent(Component comp, Object constraints) {
                if (constraints.equals("Editor")) {
                    constraints = CENTER;
                }
                super.addLayoutComponent(comp, constraints);
            }
        });
        c.add(createNextButton(), BorderLayout.EAST);
        c.add(createPreviousButton(), BorderLayout.WEST);
        c.add(createEditor(), BorderLayout.CENTER);
    }
}
