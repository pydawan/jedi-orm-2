/***********************************************************************************************
 * @(#)Function.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/07/21
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
import java.util.List;

public class Function<T> implements Runnable {
    public int index;
    public T value;
    public List<T> values = new ArrayList<T>();
    public Object[] objects = null;
    public Object object = null;

    public Function() {}

    @SuppressWarnings("unchecked")
    public <E> Function(E... objects) {
        this.objects = objects;
    }

    public <E> Function(E object) {
        this.object = object;
    }

    public void run() {}
}