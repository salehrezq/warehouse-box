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
package warehouse.panel.items.nameandspecsdisplay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.BoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 *
 * @author Saleh
 */
public class ArrowButtonlessScrollBarUI extends BasicScrollBarUI {

    private static final Color DEFAULT_COLOR = new Color(220, 100, 100);
    private static final Color DRAGGING_COLOR = new Color(200, 100, 100);
    private static final Color ROLLOVER_COLOR = new Color(255, 120, 100);

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return new ZeroSizeButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return new ZeroSizeButton();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        //Graphics2D g2 = (Graphics2D) g.create();
        //g2.setPaint(new Color(100, 100, 100));
        //g2.fillRect(r.x, r.y, r.width - 1, r.height - 1);
        //g2.dispose();
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        JScrollBar sb = (JScrollBar) c;
        if (!sb.isEnabled()) {
            return;
        }
        BoundedRangeModel m = sb.getModel();
        int iv = m.getMaximum() - m.getMinimum() - m.getExtent() - 1; // -1: bug?
        if (iv > 0) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color color;
            if (isDragging) {
                color = DRAGGING_COLOR;
            } else if (isThumbRollover()) {
                color = ROLLOVER_COLOR;
            } else {
                color = DEFAULT_COLOR;
            }
            g2.setPaint(color);
            g2.fillRect(r.x, r.y, r.width - 1, r.height - 1);
            g2.dispose();
        }
    }
}
