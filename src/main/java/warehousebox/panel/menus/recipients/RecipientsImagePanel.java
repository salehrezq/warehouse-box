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
package warehousebox.panel.menus.recipients;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import warehousebox.db.CRUDRecipientsImages;
import warehousebox.db.model.RecipientImage;
import warehousebox.panel.menus.recipients.form.RecipientImageCRUDListener;
import warehousebox.panel.menus.recipients.utility.imagepane.ImagePane;

/**
 *
 * @author Saleh
 */
public class RecipientsImagePanel implements RecipientImageCRUDListener {

    private JPanel panelContainer, panelContols;
    private ImagePane imagePane;
    private BufferedImage imageSelected;
    private RecipientImage imagesSelected;
    private SwingWorker swingWorker;
    private ScheduledThreadPoolExecutor delayedSynchronousExecution;

    public RecipientsImagePanel() {
        panelContainer = new JPanel(new BorderLayout());
        panelContainer.setPreferredSize(new Dimension(130, 0));
        imagePane = new ImagePane();
        panelContainer.add(imagePane.getContainer(), BorderLayout.CENTER);
    }

    public JPanel getContainer() {
        return panelContainer;
    }

    public void clearFields() {
        imagePane.setImage(null);
    }

    protected void setImageOfSelectedItem(int recipientId) {
        imagePane.setImage(null);
        /**
         * Cancel swingWorker. This is the case that a new item row is selected,
         * cancel the ongoing doInBackground() to start a new one.
         */
        if (swingWorker != null) {
            swingWorker.cancel(true);
        }

        swingWorker = new SwingWorker<RecipientImage, Void>() {

            @Override
            public RecipientImage doInBackground() {
                return CRUDRecipientsImages.getImageByRecipientId(recipientId);
            }

            @Override
            public void done() {
                if (!isCancelled()) {
                    try {
                        RecipientImage image = get();

                        if (image != null) {

                            /**
                             * Stopping the animation is necessary to maintain
                             * consistency. If the animation takes longer time
                             * than the concurrent retrieval of images; the "no
                             * image" icon will win the display. This scenario
                             * occurs when a user double-clicks on a row with no
                             * associated images and then quickly double-clicks
                             * on a row with available images. To prevent the
                             * "no images" display, we need to stop the
                             * animation.
                             */
                            imagePane.stopAnimation();
                            imagePane.setImage(image);

                        } else {

                            imagePane.setImage(null);
                            imagePane.noImageResponseAnimated();
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        Logger.getLogger(RecipientsImagePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        swingWorker.execute();

        /**
         * ScheduledThreadPoolExecutor to invoke task between doInBackground()
         * and done(). After the method doInBackground() is starting, if done()
         * does not finish after specified time, then synchronously invoke
         * loading animation. Later, the loading animation will be removed in
         * the done() method.
         */
        delayedSynchronousExecution = new ScheduledThreadPoolExecutor(1);
        delayedSynchronousExecution.schedule(() -> {
            if (swingWorker != null && !swingWorker.isDone()) {
                imagePane.loading();
            }
        }, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void updated(RecipientImage recipientImage) {
        setImageOfSelectedItem(recipientImage.getRecipientId());
    }

    @Override
    public void deleted() {
        System.out.println("deleted response in RecipientsImagePanel");
    }

//    @Override
//    public void selectedRowId(int rowId) {
//        setImageOfSelectedItem(rowId);
//    }
//
//    @Override
//    public void selectedRowHasBeenDeleted() {
//        scalableImageContainer.setImage(null);
//    }
//
//    @Override
//    public void created(Item createdItem) {
//        // Not required.
//    }
//
//    @Override
//    public void updated(Item updatedItem) {
//        setImageOfSelectedItem(updatedItem.getId());
//    }
}
