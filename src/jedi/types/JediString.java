/***********************************************************************************************
 * @(#)PyString.java
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

import java.text.SimpleDateFormat;

public class JediString {

    public static String capitalize(String text) {
        String s = "";

        if (text != null && !text.isEmpty()) {
            s = String.valueOf(text.charAt(0)).toUpperCase() + 
                text.substring(1).toLowerCase();
        }
        return s;
    }

    public static String upper(String text) {
        String s = "";

        if (text != null && !text.isEmpty()) {
            s = text.toUpperCase();
        }
        return s;
    }

    public static String lower(String text) {
        String s = "";

        if (text != null && !text.isEmpty()) {
            s = text.toLowerCase();
        }
        return s;
    }

    public static String repeat(Object object, int amount) {
        String s = "";
        s = repeat(object, amount, false);
        return s;
    }

    public static String repeat(Object object, int amount, boolean vertical) {
        String s = "";
        s = JediString.repeat("", object, amount, vertical);
        return s;
    }

    public static String repeat(String format, Object object, int amount, boolean vertical) {
        String s = "";

        if (object != null && !object.toString().isEmpty() && amount > 0) {
            for (int i = 0; i < amount; i++) {
                if (vertical) {
                    if (format == null || format.isEmpty()) {
                        s += object + "\n";
                    } else {
                        s += String.format(format, object) + "\n";
                    }
                } else {
                    if (format == null || format.isEmpty()) {
                        s += object;
                    } else {
                        s += String.format(format, object);
                    }
                }
            }

            if (vertical) {
                s = s.substring(0, s.lastIndexOf("\n"));
            }
        }
        return s;
    }

    public static String toString(java.util.Date date, String format) {
        String _date = "";

        if (date != null) {
            if (format != null && !format.trim().isEmpty()) {
                SimpleDateFormat date_formatter = new SimpleDateFormat(format);
                _date = date_formatter.format(date);
            }
        }
        return _date;
    }
}