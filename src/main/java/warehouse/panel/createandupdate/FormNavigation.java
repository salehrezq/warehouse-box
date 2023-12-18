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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author Saleh
 */
public class FormNavigation extends JPanel {

    private JButton btnNext;
    private JButton btnPrevious;
    private ArrayList<Navigatable> navigatables;
    private NavigateButtonsListener navigateButtonsListener;

    public FormNavigation() {

        navigatables = new ArrayList<>();
        btnNext = new JButton("Next>>");
        btnPrevious = new JButton("<<Previous");
        navigateButtonsListener = new NavigateButtonsListener();
        btnNext.addActionListener(navigateButtonsListener);
        btnPrevious.addActionListener(navigateButtonsListener);

        add(btnPrevious);
        add(btnNext);
    }

    public void addNavigationListner(Navigatable navigatable) {
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

    private class NavigateButtonsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton btnNavigate = (JButton) e.getSource();
            if (btnNavigate == btnNext) {
                notifyNext();
            } else if (btnNavigate == btnPrevious) {
                notifyPrevious();
            }
        }
    }

}
