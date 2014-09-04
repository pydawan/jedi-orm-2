/***********************************************************************************************
 * @(#)TableUtil.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/02/26
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

package jedi.db.util;

import java.lang.reflect.Field;

import jedi.db.models.Model;
import jedi.db.models.Table;

public abstract class TableUtil {
	
	public static final String getTableName(String className) {
		className = className == null ? "" : className.trim();
		String tableName = "";
		if (!className.isEmpty()) {
			if (className.matches("([A-Z](\\w+))+")) {
				String[] words = className.split("(?=[A-Z])");
				if (words != null) {
					if (words.length == 1) {
						className = String.format("%ss", className);
					} else if (words.length >= 2) {
						className = "";
						for (String word : words) {
							if (!word.isEmpty()) {
								className += String.format("%ss", word);
							}
						}
					} else {

					}
				}
			}
			className = className.trim();
			className = className.replaceAll("([a-z0-9]+)([A-Z])", "$1_$2");
			className = className.toLowerCase();
			tableName = className;
		}
		return tableName;
	}
	
	public static final String getTableName(Class<?> c) {
		String tableName = "";
		if (c != null && jedi.db.models.Model.class.isAssignableFrom(c)) {
			String className = c.getSimpleName();
			Table tableAnnotation = c.getAnnotation(Table.class);
			if (tableAnnotation != null && !tableAnnotation.name().trim().isEmpty()) {
				className = tableAnnotation.name().toLowerCase();
			}			
			if (className.matches("([A-Z](\\w+))+")) {				
				String[] words = className.split("(?=[A-Z])");
				if (words != null) {
					if (words.length == 1) {
						className = String.format("%ss", className);
					} else if (words.length >= 2) {
						className = "";
						for (String word : words) {
							if (!word.isEmpty()) {
								className += String.format("%ss", word);
							}
						}
					} else {

					}
				}
			}
			className = className.trim();
			className = className.replaceAll("([a-z0-9]+)([A-Z])", "$1_$2");
			className = className.toLowerCase();
			tableName = className;
		}
		return tableName;
	}
	
	public static final String getColumnName(String s) {
		String columnName = "";
		if (s != null && !s.isEmpty()) {
			columnName = s;
			columnName = columnName.trim();
			columnName = columnName.replaceAll("([a-z0-9]+)([A-Z])", "$1_$2");
			columnName = columnName.toLowerCase();
		}
		return columnName;
	}
	
	public static final String getColumnName(Field f) {
		String columnName = "";
		if (f != null) {
			columnName = getColumnName(f.getName());
		}
		return columnName;
	}
	
	public static final String getColumnName(Class<?> c) {
		String columnName = "";
		if (c != null && Model.class.isAssignableFrom(c)) {
			columnName = getColumnName(c.getSimpleName());
		}
		return columnName;
	}
	
	public static final String getFieldName(String columnName) {
		String fieldName = "";
		if (columnName != null && !columnName.trim().isEmpty()) {
			fieldName = columnName.trim();
			fieldName = columnName.replace("_id", "");
			if (fieldName.matches("(\\w+_\\w+)+")) {
				String[] words = fieldName.split("_");
				if (words != null) {
					if (words.length >= 2) {
						fieldName = "";
						for (String word : words) {
							if (!word.isEmpty()) {
								fieldName += String.format("%s%s", word
										.substring(0, 1).toUpperCase(), word
										.substring(1, word.length()));
							}
						}
					}
				}
			}
		}
		return fieldName;
	} 
}