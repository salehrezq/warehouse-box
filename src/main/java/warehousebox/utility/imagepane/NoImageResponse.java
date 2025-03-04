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
package warehousebox.utility.imagepane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 *
 * @author Saleh
 */
public class NoImageResponse {

    private JLabel container;
    private ImageIcon[] frames;
    private Timer timer;

    public NoImageResponse(JLabel container) {
        frames = new ImageIcon[2];
        frames[0] = new ImageIcon(getClass().getResource("/images/no-image-placeholder/no-image-black.png"));
        frames[1] = new ImageIcon(getClass().getResource("/images/no-image-placeholder/no-image-orange.png"));
        this.container = container;
        Animate animate = new Animate();
        timer = new Timer(180, animate);
        animate.seTTimer(timer);
    }

    private class Animate implements ActionListener {

        private int index = 0;
        private int loop = 2;
        private Timer timer;

        private void seTTimer(Timer timer) {
            this.timer = timer;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (index < frames.length - 1) {
                index++;
            } else {
                index = 0;
            }
            container.setIcon(frames[index]);
            loop++;
            if (loop == 6) {
                loop = 0;
                timer.stop();
            }
        }
    }

    protected void noImageAnimate() {
        setInitialFrame();
        start();
    }

    protected void noImageStillFrame() {
        setInitialFrame();
    }

    protected void stopAnimation() {
        timer.stop();
    }

    private void setInitialFrame() {
        container.setIcon(frames[0]);
    }

    private void start() {
        timer.start();
    }

}
