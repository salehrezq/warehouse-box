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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.miginfocom.swing.MigLayout;
import warehouse.db.CRUDSourceLocation;
import warehouse.db.model.SourceLocation;

/**
 *
 * @author Saleh
 */
public class ItemFormSourceLocation extends JPanel {

    private JTextField tfSourceLocationSearch;
    private JLabel lbSourceLocation;
    private List list;
    private DeferredDocumentListener docListener;
    private TfSourceLocationSearchListener tFListener;

    public ItemFormSourceLocation() {
        setLayout(new MigLayout("center center"));

        list = new List();
        list.setSize(300, 300);

        lbSourceLocation = new JLabel("Search & select");
        tfSourceLocationSearch = new JTextField(17);
        tFListener = new TfSourceLocationSearchListener();
        docListener = new DeferredDocumentListener(700, tFListener, false);
        tfSourceLocationSearch.getDocument().addDocumentListener(docListener);
        tfSourceLocationSearch.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // no writting
                docListener.start();
            }

            @Override
            public void focusLost(FocusEvent e) {
                // writting
                docListener.stop();
            }
        });

        add(lbSourceLocation);
        add(tfSourceLocationSearch, "wrap");
        add(list.getList(), "span 2");
    }

    public void rePopulateLocationsListSearch() {
        list.removeAllElements();
        ArrayList<SourceLocation> sourceLocations = CRUDSourceLocation.getSearch(tfSourceLocationSearch.getText());
        sourceLocations.forEach(location -> {
            list.addElement(location);
        });
    }

    private class TfSourceLocationSearchListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!tfSourceLocationSearch.getText().isBlank()) {
                rePopulateLocationsListSearch();
            }
        }
    }

    private class DeferredDocumentListener implements DocumentListener {

        private final Timer timer;

        public DeferredDocumentListener(int timeOut, ActionListener listener, boolean repeats) {
            timer = new Timer(timeOut, listener);
            timer.setRepeats(repeats);
        }

        public void start() {
            timer.start();
        }

        public void stop() {
            timer.stop();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            timer.restart();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            timer.restart();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            timer.restart();
        }

    }
}
