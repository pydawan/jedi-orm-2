/***********************************************************************************************
 * @(#)PyRange.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/01/27
 * 
 * Copyright (c) 2014 Thiago Alexandre Martins Monteiro.
 * 
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the GNU Public License v2.0 which accompanies 
 * this distribution, and is available at http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *    Thiago Alexandre Martins Monteiro - initial API and implementation
 ************************************************************************************************/

package jedi.types;

import java.util.ArrayList;

public class Range extends ArrayList<Integer> {

    private static final long serialVersionUID = 1L;

    public int start;
    public int end;
    public int increment;
    public int[] values;

    public Range() {}

    public Range(int start, int end) {
        this(start, end, 1);
    }

    public Range(int start, int end, int increment) {
        this.start = start;
        this.end = end;
        this.increment = increment;

        for (int i = start; i < end; i += increment) {
            this.add(i);
        }
    }

    public Range(String range) {
        this(range, 0);
    }

    public Range(String range, int increment) {
        System.out.println(range);
    }

    public Range reverse() {
        Range r = new Range();

        for (int i = this.size() - 1; i >= 0; i--) {
            r.add(this.get(i));
        }
        return r;
    }

    public void each(Function<Integer> function) {
        int index = 0;

        if (function != null) {
            for (Integer object : this) {
                function.index = index++;
                function.value = object;
                function.run();
            }
        }
    }
}