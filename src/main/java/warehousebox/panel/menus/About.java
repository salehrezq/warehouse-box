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
package warehousebox.panel.menus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 *
 * @author Saleh
 */
public class About extends JDialog implements HyperlinkListener {

    private JPanel panel;
    private final int width;
    private final int height;
    private final Color bgColor;
    private final Font font;
    private ImageIcon image;
    private StringBuilder stringBuilder;
    private JEditorPane aboutPane;
    private JButton btnClose;
    private About thisInstance = About.this;

    public About(JFrame owner, String title, boolean modal) {
        super(owner, title, modal);
        // System.out.println(" dialog constructor");
        this.width = 420;
        this.height = 400;
        // this.thisParentFrame = parentFrame;
        //  this.iz = thisParentFrame.imageZoom;
        this.setSize(new Dimension(width, height));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                setSize(width, height);
            }
        });

        aboutPane = new JEditorPane();
        aboutPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        aboutPane.setContentType("text/html");
        font = new Font("Arial", Font.PLAIN, 12);
        aboutPane.setFont(font);
        // this is the trick!
        stringBuilder = new StringBuilder(1000);
        image = new ImageIcon(getClass().getResource("/images/app-icon/app-icon.png"));
        stringBuilder.append("<html><p style='text-align:center;'><img src='" + image + "' width=50 height=50></img></p>");
        stringBuilder.append("<h1 style='text-align:center;'>Warehouse Box</h1>");
        stringBuilder.append("<p style='text-align:center;'>Version: 1.0.0</p>");
        stringBuilder.append("<p>Warehouse Box is maintained at <a href='https://github.com/salehrezq/warehouse-box'>Github</a>.</p>");
        stringBuilder.append("<p>Feedback on errors or suggestions can go to <a href='mailto:salehrezq@gmail.com'>salehrezq@gmail.com</a>,<br>");
        stringBuilder.append("or Whatsapp/Telegram on:+967 780 431 625.</p>");
        stringBuilder.append("<p>Credit goes to Ala'a M AlBadany for the business logic.</p>");
        stringBuilder.append("</html>");
        aboutPane.setText(stringBuilder.toString());
        aboutPane.setEditable(false);
        aboutPane.setOpaque(false);
        aboutPane.setCaret(new InvisibleCaret());
        aboutPane.addHyperlinkListener(this);

        btnClose = new JButton("close");
        btnClose.addActionListener(new BtnCloseHandler());

        bgColor = new Color(254, 254, 245);

        panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(102, 102, 102)));
        panel.setBackground(bgColor);
        panel.add(aboutPane, BorderLayout.CENTER);

        JPanel panelBtn = new JPanel();
        panelBtn.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(102, 102, 102)));
        panelBtn.setBackground(bgColor);
        panelBtn.add(btnClose);
        panel.add(panelBtn, BorderLayout.PAGE_END);

        this.getContentPane().add(panel);
        this.setUndecorated(true);
        this.setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent hle) {
        if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(hle.getURL().toURI());
            } catch (URISyntaxException | IOException ex) {
                Logger.getLogger(About.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private class BtnCloseHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            thisInstance.dispose();
        }
    }

}
