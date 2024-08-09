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
package warehouse.panel.menus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Saleh
 */
public class ResultLimitSizePreference extends JDialog {

    private final JTextField tfResultLimitSizeLimit;
    private final JButton btnSave;
    private final JButton btnCancel;
    private final BtnHandler btnHandler;
    private static Preferences prefs;
    private static final String PREFS_DB_RESULT_LIMIT_SIZE = "DB_RESULT_LIMIT_SIZE";
    private static final int DEFAULT_LIMIT = 20;
    // Regular expression to validate numbers from 1 to 100
    private final Pattern pattern = Pattern.compile("^[1-9][0-9]?$|^100$");
    private final Color colorError = new Color(255, 255, 0);

    public ResultLimitSizePreference(JFrame owner, String title, boolean modal) {
        super(owner, title, modal);
        prefs = Preferences.userRoot().node(getClass().getName());

        tfResultLimitSizeLimit = new JTextField(20);
        tfResultLimitSizeLimit.setText(String.valueOf(DEFAULT_LIMIT));
        tfResultLimitSizeLimit.getDocument().addDocumentListener(new ValidateNumberHandler());

        btnSave = new JButton("Save");
        btnCancel = new JButton("Cancel");

        btnHandler = new BtnHandler();
        btnSave.addActionListener(btnHandler);
        btnCancel.addActionListener(btnHandler);

        setLayout(new BorderLayout());
        add(tfResultLimitSizeLimit, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);
        setMinimumSize(new Dimension(300, 95));
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public static int getResultLimitSize() {
        return prefs.getInt(PREFS_DB_RESULT_LIMIT_SIZE, DEFAULT_LIMIT);
    }

    private class BtnHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton) e.getSource();
            if (source == btnSave) {
                String limitTxt = tfResultLimitSizeLimit.getText();

                if (isInt(limitTxt)) {
                    int limitInt = Integer.parseInt(limitTxt);
                    prefs.putInt(PREFS_DB_RESULT_LIMIT_SIZE, limitInt);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            "Value must be a number",
                            "Error",
                            JOptionPane.WARNING_MESSAGE);
                }
            } else if (source == btnCancel) {
                dispose();
            }
        }

        public boolean isInt(String s) {
            try {
                Integer.valueOf(s);
                return true;
            } catch (NumberFormatException ex) {
                return false;
            }
        }
    }

    protected void initializeWithSavedLimitSize() {
        tfResultLimitSizeLimit.setText(String.valueOf(prefs.getInt(PREFS_DB_RESULT_LIMIT_SIZE, DEFAULT_LIMIT)));
    }

    private void tfValidateNumber() {
        if (pattern.matcher(tfResultLimitSizeLimit.getText()).matches()) {
            tfResultLimitSizeLimit.setBackground(Color.WHITE);
            btnSave.setEnabled(true);
        } else {
            tfResultLimitSizeLimit.setBackground(colorError);
            btnSave.setEnabled(false);
        }
    }

    private class ValidateNumberHandler implements DocumentListener {

        private void check(DocumentEvent e) {
            if (e.getDocument() == tfResultLimitSizeLimit.getDocument()) {
                tfValidateNumber();
            }
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            check(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            check(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            check(e);
        }

    }

}
