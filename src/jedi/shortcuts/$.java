/***********************************************************************************************
 * @(#)$.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/03/14
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

package jedi.shortcuts;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import jedi.types.Block;
import jedi.types.Function;
import jedi.types.Range;
import jedi.types.JediString;

/**
 * Classe de atalhos que aplica idéias do jQuery.
 * 
 * @author thiago.monteiro
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class $ {

	public Object object;
	public Collection collection;

	public $(Object object, Collection collection) {
		this.object = object;
		this.collection = collection;
	}

	public static void each(Collection collection, Block block) {
		int i = 0;
		for (Object obj : collection) {
			block.index = i++;
			block.value = obj;
			block.run();
		}
	}

	public static void each(float[] array, Function function) {
		for (int i = 0; i < array.length; i++) {
			function.index = i;
			function.value = array[i];
			function.run();
		}
	}

	public static void each(int[] array, Function function) {
		for (int i = 0; i < array.length; i++) {
			function.index = i;
			function.value = array[i];
			function.run();
		}
	}

	public static void each(double[] array, Function function) {
		for (int i = 0; i < array.length; i++) {
			function.index = i;
			function.value = array[i];
			function.run();
		}
	}

	public static void each(Object[] array, Function function) {
		int i = 0;
		for (Object obj : array) {
			function.index = i++;
			function.value = obj;
			function.run();
		}
	}

	public static void each(Collection collection, Function function) {
		int i = 0;
		if (collection != null) {
			for (Object obj : collection) {
				function.index = i++;
				function.value = obj;
				function.run();
			}
		}
	}

	public static Range range(int start, int end) {
		Range range = new Range(start, end);
		return range;
	}

	public static Range range(int start, int end, int increment) {
		Range range = new Range(start, end, increment);
		return range;
	}

	public static void each(Range range, Function function) {
		for (int i = 0; i < range.size(); i++) {
			function.index = i;
			function.value = range.get(i);
			function.run();
		}
	}

	public static List list(Object[] array) {
		List l = null;
		if (array != null) {
			l = new ArrayList();
			for (int i = 0; i < array.length; i++) {
				l.add(array[i]);
			}
		}
		return l;

	}

	public static List list(String items) {
		List l = null;
		if (items != null) {
			l = new ArrayList();
			for (int i = 0; i < items.length(); i++) {
				l.add(items.charAt(i));
			}
		}
		return l;
	}

	public static void print(Object object) {
		if (object != null) {
			System.out.print(object);
		}
	}

	public static void print(String format, Object... objects) {
		System.out.printf(format, objects);
	}

	public static void print(String format, int amount, boolean vertical, Object... objects) {
		if (objects != null) {
			java.lang.String[] formats = null;
			if (format != null && !format.isEmpty()) {
				formats = format.split(" ");
			}
			if (formats.length == objects.length) {
				for (int i = 0; i < objects.length; i++) {
					$.print(formats[i], objects[i], 1, vertical);
				}
			}
		}
	}

	public static void println(Object object) {
		if (object != null) {
			System.out.println(object);
		}
	}

	public static void println(Object... objects) {
		if (objects != null) {
			for (Object object : objects) {
				System.out.println(object);
			}
		}
	}

	public static void print(Object object, int amount) {
		if (object != null) {
			$.print(object, amount, false);
		}
	}

	public static void println(Object object, int amount) {
		if (object != null) {
			$.println(object, amount, false);
		}
	}

	public static void print(String format, Object object, int amount) {
		if (object != null) {
			$.print(format, object, amount, false);
		}
	}

	public static void println(String format, Object object, int amount) {
		if (object != null) {
			$.println(format, object, amount, false);
		}
	}

	public static void print(Object object, int amount, boolean vertical) {
		if (object != null) {
			java.lang.String out = JediString.repeat(object, amount, vertical);
			print(out);
		}
	}

	public static void println(Object object, int amount, boolean vertical) {
		if (object != null) {
			java.lang.String out = JediString.repeat(object, amount, vertical);
			println(out);
		}
	}

	public static void print(String format, Object object,
			int amount, boolean vertical) {
		if (object != null) {
			java.lang.String out = JediString.repeat(format, object, amount, vertical);
			print(out);
		}
	}

	public static void println(String format, Object object, int amount, boolean vertical) {
		if (object != null) {
			java.lang.String out = JediString.repeat(format, object, amount, vertical);
			println(out);
		}
	}

	public static void puts(Object object) {
		print(object);
	}

	public static void puts(String string, int amount) {
		if (string != null) {
			$.puts(string, amount, false);
		}
	}

	public static void puts(String string, int amount, boolean vertical) {
		if (string != null) {
			java.lang.String str_out = JediString.repeat(string, amount, vertical);
			System.out.println(str_out);
		}
	}

	public static void printf(String format, Object... text) {
		if (text != null) {
			if (format != null && !format.trim().isEmpty()) {
				System.out.printf(format, text);
			} else {
				System.out.println(text);
			}
		}
	}

	public static Date date() {
		return new Date();
	}

	public static Date date(String date) {
		return date(date, "yyyy-MM-dd");
	}

	public static Date date(String date, String format) {
		Date _date = new Date();
		if (date != null && !date.isEmpty() && format != null && !format.isEmpty()) {
			SimpleDateFormat date_formatter = new SimpleDateFormat(format);
			try {
				_date = date_formatter.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return _date;
	}

	public static String toString(Date date, String format) {
		return JediString.toString(date, format);
	}

	/**
	 * Método que avalia e interpreta a expressão e retorna o tipo de dados
	 * adequado.
	 * 
	 * @param expression
	 */
	public static void eval(String expression) {

	}
}