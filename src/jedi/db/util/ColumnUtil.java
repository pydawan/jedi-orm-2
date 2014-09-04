package jedi.db.util;

import java.lang.reflect.Field;

import jedi.db.models.Model;

public abstract class ColumnUtil {
	
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
}