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

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractSpinnerModel;

/**
 *
 * @author Saleh
 */
public class CustomSpinnerModel extends AbstractSpinnerModel {

    private int value;
    private int minimum;
    private int maximum;
    private int stepSize;

    private int listIndex;

    private List<Renderer> spinnerList;

    public CustomSpinnerModel(int value, int minimum, int maximum,
            int stepSize) throws IllegalArgumentException {
        if (!((minimum <= value) && (value <= maximum))) {
            throw new IllegalArgumentException(
                    "(minimum <= value <= maximum) is false");
        }

        this.value = value;
        this.minimum = minimum;
        this.maximum = maximum;
        this.stepSize = stepSize;

        this.spinnerList = new ArrayList<Renderer>();
        setSpinnerList();
    }

    private void setSpinnerList() {
        int index = 0;
        for (int i = minimum; i <= maximum; i += stepSize) {
            Renderer renderer = new Renderer(i, maximum);
            if (i == value) {
                listIndex = index;
            }
            spinnerList.add(renderer);
            index++;
        }
    }

    @Override
    public Object getNextValue() {
        listIndex = Math.min(++listIndex, (spinnerList.size() - 1));
        fireStateChanged();
        return spinnerList.get(listIndex);
    }

    @Override
    public Object getPreviousValue() {
        listIndex = Math.max(--listIndex, 0);
        fireStateChanged();
        return spinnerList.get(listIndex);
    }

    @Override
    public Object getValue() {
        return spinnerList.get(listIndex);
    }

    @Override
    public void setValue(Object object) {

    }

}
