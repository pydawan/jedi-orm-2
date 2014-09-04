/***********************************************************************************************
 * @(#)Model.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/08/23
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

package jedi.db.models;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import jedi.db.CascadeType;
import jedi.db.connection.ConnectionFactory;
import jedi.db.engine.JediORMEngine;
import jedi.db.util.TableUtil;

/**
 * Classe que representa um modelo.
 * @author Thiago Alexandre Martins Monteiro
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings({ "rawtypes", "unused", "unchecked", "deprecation" })
public class Model implements Comparable<Model>, Serializable {
	// Attributes
	private static final long serialVersionUID = 3655866130678459258L;
	private static transient Connection connection;
	private boolean autoCloseConnection = JediORMEngine.DATABASE_AUTO_CLOSE_CONNECTION;
	
	static {
		if (!JediORMEngine.JEDI_PROPERTIES_LOADED) {
			JediORMEngine.loadJediProperties();			
		}
		connection = ConnectionFactory.getConnection();
	}
	
	protected int id;
	protected transient boolean isPersisted;
	protected String tableName;
	
	// Constructors
	public Model() {
		this.setTableName(TableUtil.getTableName(this.getClass()));
	}

	public boolean getAutoCloseConnection() {
		return autoCloseConnection;
	}

	public boolean autoCloseConnection() {
		return autoCloseConnection;
	}

	public int getId() {
		return id;
	}

	public int id() {
		return id;
	}

	public boolean isPersisted() {
		return isPersisted;
	}

	public String getTableName() {
		return tableName;
	}

	public String tableName() {
		return tableName;
	}

	public Object get(String field) {
		Object object = null;
		if (field != null && !field.trim().isEmpty()) {
			try {
				Field f = null;
				if (field.trim().equalsIgnoreCase("id")) {
					f = this.getClass().getSuperclass().getDeclaredField(field);
				} else {
					f = this.getClass().getDeclaredField(field);
				}
				f.setAccessible(true);
				object = f.get(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return object;
	}

	// Setters
	public void setConnection(Connection connection) {
		Model.connection = connection;
	}

	public Model connection(Connection connection) {
		Model.connection = connection;
		return this;
	}

	public void setTableName(String tableName) {
		this.tableName = TableUtil.getTableName(tableName);
	}

	public Model tableName(String tableName) {
		setTableName(tableName);
		return this;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Model id(int id) {
		this.id = id;
		return this;
	}

	public void setAutoCloseConnection(boolean autoCloseConnection) {
		this.autoCloseConnection = autoCloseConnection;
	}

	public Model autoCloseConnection(boolean autoCloseConnection) {
		this.autoCloseConnection = autoCloseConnection;
		return this;
	}

	public void isPersisted(boolean isPersisted) {
		this.isPersisted = isPersisted;
	}

	public Model set(String field, Object value) {
		if (field != null && !field.trim().isEmpty()) {
			try {
				Field f = this.getClass().getDeclaredField(field);
				f.setAccessible(true);
				f.set(this, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this;
	}

	/**
	 * Método que insere o modelo invocador na tabela apropriada no banco de
	 * dados.
	 * 
	 * @author - Thiago Alexandre Martins Monteiro
	 * @param - nenhum
	 * @return - nenhum
	 * @throws - java.lang.Exception
	 */
	public void insert() {
		Statement statement = null;
		try {
			if (Model.connection == null || !Model.connection.isValid(10)) {
				Model.connection = ConnectionFactory.getConnection();
			}
			String sql = "INSERT INTO";
			String columns = "";
			String values = "";
			String defaultValue = "";
			String references = null;
			String manyToManySQLFormatter = "INSERT INTO %s_%s (%s_id, %s_id) VALUES (%d,";
			List<String> manyToManySQLs = new ArrayList<String>();
			Manager associatedModelManager = null;
			OneToOneField oneToOneFieldAnnotation = null;
			ForeignKeyField foreignKeyFieldAnnotation = null;
			ManyToManyField manyToManyFieldAnnotation = null;
			DateField dateFieldAnnotation = null;
			TimeField timeFieldAnnotation = null;
			DateTimeField dateTimeFieldAnnotation = null;
			CharField charFieldAnnotation = null;
			BooleanField booleanFieldAnnotation = null;
			IPAddressField ipAddresFieldAnnotation = null;
			for (Field field : JediORMEngine.getAllFields(this.getClass())) {	
				field.setAccessible(true);
				if (!JediORMEngine.isJediField(field)) {
					continue;
				}
				if (field.getName().equals("serialVersionUID"))
					continue;
				if (field.getName().equals("objects"))
					continue;
				oneToOneFieldAnnotation = field.getAnnotation(OneToOneField.class);
				foreignKeyFieldAnnotation = field.getAnnotation(ForeignKeyField.class);				
				manyToManyFieldAnnotation = field.getAnnotation(ManyToManyField.class);
				dateFieldAnnotation = field.getAnnotation(DateField.class);
				timeFieldAnnotation = field.getAnnotation(TimeField.class);
				dateTimeFieldAnnotation = field.getAnnotation(DateTimeField.class);
				charFieldAnnotation = field.getAnnotation(CharField.class);
				booleanFieldAnnotation = field.getAnnotation(BooleanField.class);
				ipAddresFieldAnnotation = field.getAnnotation(IPAddressField.class);
				CascadeType cascadeType = JediORMEngine.CASCADE_TYPE;
				// Treats the columns.
				if (field.getType().getSuperclass() != null && 
					field.getType().getSuperclass().getSimpleName().equals("Model")) {
					if (foreignKeyFieldAnnotation != null) {						
						columns += String.format("%s_id, ", TableUtil.getColumnName(field));
					} else if (oneToOneFieldAnnotation != null) {
						columns += String.format("%s_id, ", TableUtil.getColumnName(field));
					} else {
						
					}
				} else if (field.getType().getName().equals("java.util.List") || 
						   field.getType().getName().equals("jedi.db.models.QuerySet")) {
					// Doesn't creates the field here.
				} else {
					columns += String.format("%s, ", TableUtil.getColumnName(field.getName()));
				}
				// Treats the values.
				if (field.getType().getSimpleName().equalsIgnoreCase("boolean")) {
					values += String.format("%s, ", field.get(this).equals(Boolean.FALSE) ? 0 : 1);
				} else if (field.getType().toString().endsWith("String")) {
					if (field.get(this) != null) {
						// Substituindo ' por \' para evitar erro de sintaxe no SQL.
						values += String.format("'%s', ", ((String) field.get(this)).replaceAll("'", "\\\\'"));
					} else {
						if (charFieldAnnotation != null) {
							defaultValue = JediORMEngine.getDefaultValue(charFieldAnnotation);
							if (defaultValue == null) {
								// Removes the column.
								columns = columns.replace(String.format("%s, ", TableUtil.getColumnName(field.getName())), "");
							} else {
								values += String.format("'%s', ", charFieldAnnotation.default_value().replaceAll("'", "\\\\'"));
							}
						} else {
							values += String.format("'', ", field.get(this));
						}
					}
				} else if (field.getType().toString().endsWith("Date") || 
						   field.getType().toString().endsWith("PyDate") || 
						   field.getType().toString().endsWith("DateTime")) {
					Date date = (Date) field.get(this);
					if (date != null) {
						if (dateFieldAnnotation != null) {
							values += String.format(
								"'%d-%02d-%02d', ",
								date.getYear() + 1900, 
								date.getMonth() + 1,
								date.getDate() 
							);
						} else if (timeFieldAnnotation != null) { 
							values += String.format(
								"'%02d:%02d:%02d', ",
								date.getHours(),
								date.getMinutes(), 
								date.getSeconds()
							);
						} else if (dateTimeFieldAnnotation != null) {
							values += String.format(
								"'%d-%02d-%02d %02d:%02d:%02d', ",
								date.getYear() + 1900, 
								date.getMonth() + 1,
								date.getDate(), 
								date.getHours(),
								date.getMinutes(), 
								date.getSeconds()
							);
						} else {
							
						}						
					} else {
						if (dateFieldAnnotation != null) {
							defaultValue = JediORMEngine.getDefaultValue(dateFieldAnnotation);
							if (defaultValue.isEmpty()) {
								// Atribui a data atual ao inserir ou atualizar.
								if (dateTimeFieldAnnotation.auto_now_add()) {
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
									values += String.format("'%s', ", sdf.format(new Date()));
								} else {
									// Remove coluna da instrução SQL.
									columns = columns.replace(String.format("%s, ", TableUtil.getColumnName(field)), "");
								}
							} else {
								values += String.format(
									defaultValue.equalsIgnoreCase("NULL") ? 
											"%s, " : "'%s', ", 
									defaultValue
								);
							}
						} else if (timeFieldAnnotation != null) {
							defaultValue = JediORMEngine.getDefaultValue(timeFieldAnnotation);
							if (defaultValue.isEmpty()) {
								if (timeFieldAnnotation.auto_now_add()) {
									SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
									values += String.format("'%s', ", sdf.format(new Date()));
								} else {
									columns = columns.replace(String.format("%s, ", TableUtil.getColumnName(field)), "");
								}
							} else {
								values += String.format(
									defaultValue.equalsIgnoreCase("NULL") ? 
											"%s, " : "'%s', ", 
									defaultValue
								);
							}
						} else if (dateTimeFieldAnnotation != null) {
							defaultValue = JediORMEngine.getDefaultValue(dateTimeFieldAnnotation);
							if (defaultValue.isEmpty()) {
								if (dateTimeFieldAnnotation.auto_now_add()) {
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									values += String.format("'%s', ", sdf.format(new Date()));
								} else {
									columns = columns.replace(String.format("%s, ", TableUtil.getColumnName(field)), "");
								}
							} else {
								values += String.format(
									defaultValue.equalsIgnoreCase("NULL") ? 
											"%s, " : "'%s', ", 
									defaultValue
								);
							}
						} else {
							values += String.format("'', ", field.get(this));
						}
					}
				} else {
					if (oneToOneFieldAnnotation != null || foreignKeyFieldAnnotation != null) {
						// BUG - Deve verificar se o modelo é nulo
						Object id = ((Model) field.get(this)).id;
						if (oneToOneFieldAnnotation != null) {
							cascadeType = cascadeType.equals(CascadeType.NONE) ?
									oneToOneFieldAnnotation.cascade_type() : cascadeType;
						} else {
							cascadeType = cascadeType.equals(CascadeType.NONE) ?
									foreignKeyFieldAnnotation.cascade_type() : cascadeType;
						}
						
						if (cascadeType.equals(CascadeType.INSERT) || cascadeType.equals(CascadeType.SAVE) || 
							cascadeType.equals(CascadeType.ALL)) {
							if (Integer.parseInt(id.toString()) == 0) {
								((Model) field.get(this)).save();
								id = ((Model) field.get(this)).id;
							}
						}
						values += String.format("%s, ", id);
					} else if ((field.getType().getName().equals("java.util.List") || 
							    field.getType().getName().equals("jedi.db.models.QuerySet")) &&
							    manyToManyFieldAnnotation != null) {
                        String model = manyToManyFieldAnnotation.model().getSimpleName();
                        model = Model.class.getSimpleName().equals(model) ? "" : model;
                        ParameterizedType genericType = null;
                        Class superClazz = null;
                        Class clazz = null;
                        if (model.isEmpty()) {
                        	if (ParameterizedType.class.isAssignableFrom(field.getGenericType().getClass())) {
                                genericType = (ParameterizedType) field.getGenericType();
                                superClazz = ((Class) (genericType.getActualTypeArguments()[0])).getSuperclass();
                                if (superClazz == Model.class) {
                                    clazz = (Class) genericType.getActualTypeArguments()[0];
                                    model = clazz.getSimpleName();
                                }
                            }                                            	
                        }
                        references = manyToManyFieldAnnotation.references();
                        if (references == null || references.trim().isEmpty()) {
                        	if (clazz != null) {
                        		references = TableUtil.getTableName(clazz);
                        	} else {
                        		references = TableUtil.getTableName(model);
                        	}
                        }
                        String packageName = this.getClass().getPackage().getName();
						associatedModelManager = new Manager(Class.forName(String.format("%s.%s", packageName, model)));
						cascadeType = cascadeType.equals(CascadeType.NONE) ?
								manyToManyFieldAnnotation.cascade_type() : cascadeType;
							if (cascadeType.equals(CascadeType.INSERT) || cascadeType.equals(CascadeType.SAVE) || 
									cascadeType.equals(CascadeType.ALL)) {
								if (field.getType().getName().equals("java.util.List")) {
									for (Object obj : (List) field.get(this)) {
										((Model) obj).insert();
										manyToManySQLs.add(
											String.format(
												manyToManySQLFormatter,
												tableName,
												references,
												TableUtil.getColumnName(model),
												TableUtil.getColumnName(this.getClass()),
												((Model) obj).id()
											)
										);
									}
								}
								if (field.getType().getName().equals("jedi.db.models.QuerySet")) {
									for (Object obj : (QuerySet) field.get(this)) {
										((Model) obj).insert();
										manyToManySQLs.add(
											String.format(
												manyToManySQLFormatter,
												tableName,
												references,										
												TableUtil.getColumnName(model),												
												TableUtil.getColumnName(this.getClass()),												
												((Model) obj).id()
											)
										);
									}
								}
							}
					} else {
						values += String.format("%s, ", field.get(this));						
					}
				}
			}
			columns = columns.substring(0, columns.lastIndexOf(','));
			values = values.substring(0, values.lastIndexOf(','));
			sql = String.format("%s %s (%s) VALUES (%s);", sql, tableName, columns, values);
			if (JediORMEngine.DEBUG) {
				System.out.println(sql + "\n");
			}
			statement = Model.connection.createStatement();
			statement.executeUpdate(sql);
			if (!Model.connection.getAutoCommit()) {
				Model.connection.commit();
			}
			Manager manager = new Manager(this.getClass());
			this.id = manager.getLastInsertedID();
			for (String associatedModelSQL : manyToManySQLs) {
				associatedModelSQL = String.format("%s %d);", associatedModelSQL, this.id());
				if (JediORMEngine.DEBUG) {
					System.out.println(associatedModelSQL + "\n");
				}
				associatedModelManager.raw(associatedModelSQL);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (!Model.connection.getAutoCommit()) {
					Model.connection.rollback();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (Model.connection != null && this.autoCloseConnection) {
					Model.connection.close();
					Model.connection = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Método que atualiza o modelo invocador na tabela apropriada no banco de
	 * dados.
	 * 
	 * @author - Thiago Alexandre Martins Monteiro
	 * @param - nenhum
	 * @return - nenhum
	 * @throws - java.lang.Exception
	 */
	public void update(String... args) {
		PreparedStatement statement = null;
		try {
			if (args == null) {
				return;
			}
			if (Model.connection == null || !Model.connection.isValid(10)) {
				Model.connection = ConnectionFactory.getConnection();
			}
			String sql = "UPDATE";
			String fieldsAndValues = "";
			String defaultValue = "";
			String referencedModel = null;
			String referencedTable = null;
			List<String> manyToManySQLs = new ArrayList<String>();
			sql = String.format("%s %s SET", sql, this.getTableName());
			OneToOneField oneToOneFieldAnnotation = null;
			ForeignKeyField foreignKeyFieldAnnotation = null;
			ManyToManyField manyToManyFieldAnnotation = null;
			DateField dateFieldAnnotation = null;
			TimeField timeFieldAnnotation = null;
			DateTimeField dateTimeFieldAnnotation = null;
			if (args.length == 0) {
				for (Field field : this.getClass().getDeclaredFields()) {
					field.setAccessible(true);
					if (!JediORMEngine.isJediField(field)) {
						continue;
					}
					if (field.getName().equals("serialVersionUID"))
						continue;
					if (field.getName().equals("objects"))
						continue;
					oneToOneFieldAnnotation = field.getAnnotation(OneToOneField.class);
					foreignKeyFieldAnnotation = field.getAnnotation(ForeignKeyField.class);
					manyToManyFieldAnnotation = field.getAnnotation(ManyToManyField.class);
					dateFieldAnnotation = field.getAnnotation(DateField.class);
					timeFieldAnnotation = field.getAnnotation(TimeField.class);
					dateTimeFieldAnnotation = field.getAnnotation(DateTimeField.class);
					CascadeType cascadeType = JediORMEngine.CASCADE_TYPE;
					String fieldName = field.getName();
					String columnName = TableUtil.getColumnName(fieldName);
					if (field.getType().getName().equals("jedi.db.models.QuerySet") || 
						field.getType().getName().equals("java.util.List")) {
						if (manyToManyFieldAnnotation != null) {
							Class superClazz = null;
                        	Class clazz = null;
							referencedModel = manyToManyFieldAnnotation.model().getSimpleName();
							referencedModel = Model.class.getSimpleName().equals(referencedModel) ? "" : referencedModel;
	                        if (referencedModel.isEmpty()) {
	                        	ParameterizedType genericType = null;
	                        	if (ParameterizedType.class.isAssignableFrom(field.getGenericType().getClass())) {
	                                genericType = (ParameterizedType) field.getGenericType();
	                                superClazz = ((Class) (genericType.getActualTypeArguments()[0])).getSuperclass();
	                                if (superClazz == Model.class) {
	                                    clazz = (Class) genericType.getActualTypeArguments()[0];
	                                    referencedModel = clazz.getSimpleName();
	                                }
	                            }                                            	
	                        }
	                        referencedTable = manyToManyFieldAnnotation.references();
	                        if (referencedTable == null || referencedTable.trim().isEmpty()) {
	                        	if (clazz != null) {
	                        		referencedTable = TableUtil.getTableName(clazz);	                        		
	                        	} else {
	                        		referencedTable = TableUtil.getTableName(referencedTable);
	                        	}
	                        }
							boolean persistedModel = false;
							cascadeType = cascadeType.equals(CascadeType.NONE) ?
									manyToManyFieldAnnotation.cascade_type() : cascadeType;
							if (cascadeType.equals(CascadeType.UPDATE) || cascadeType.equals(CascadeType.SAVE) || 
									cascadeType.equals(CascadeType.ALL)) {
								for (Model model : (List<Model>) field.get(this)) {
									persistedModel = model.isPersisted();
									model.save();
									Object id = model.id;
									String intermediateModel = manyToManyFieldAnnotation.through().getSimpleName();
									intermediateModel = Model.class.getSimpleName().equals(intermediateModel) ? 
											"" : intermediateModel;
									if (intermediateModel.isEmpty()) {
										// Checks if the model was persisted.
										if (!persistedModel) {
											manyToManySQLs.add(
												String.format(
													"INSERT INTO %s_%s (%s_id, %s_id) VALUES (%s, %s)",
													tableName,
													TableUtil.getColumnName(referencedTable),
													TableUtil.getColumnName(this.getClass()),
													TableUtil.getColumnName(referencedModel),
													this.id, 
													id
												)
											);
										} else {
											manyToManySQLs.add(
												String.format(
													"UPDATE %s_%s SET %s_id = %s WHERE %s_id = %s AND %s_id = %s", 
													tableName, 
													TableUtil.getColumnName(referencedTable), 
													TableUtil.getColumnName(referencedModel),
													id, 
													TableUtil.getColumnName(this.getClass()),
													this.id,
													TableUtil.getColumnName(referencedModel), 
													id
												)
											);
										}
									}
								}
							}
						}
					} else if (field.getType().getSuperclass() != null && 
							   field.getType().getSuperclass().getSimpleName().equals("Model")) {
						Model model = (Model) field.get(this);
						if (oneToOneFieldAnnotation != null) {
							cascadeType = cascadeType.equals(CascadeType.NONE) ?
									oneToOneFieldAnnotation.cascade_type() : cascadeType;
						} else if (foreignKeyFieldAnnotation != null) {
							cascadeType = cascadeType.equals(CascadeType.NONE) ?
									foreignKeyFieldAnnotation.cascade_type() : cascadeType;
						} else {
							
						}
						if (cascadeType.equals(CascadeType.UPDATE) || cascadeType.equals(CascadeType.SAVE) || 
							cascadeType.equals(CascadeType.ALL)) {
							model.save(); // Saves the model if it not saved yet.
						}
						if (foreignKeyFieldAnnotation != null || oneToOneFieldAnnotation != null) {
							fieldsAndValues += String.format("%s_id = ", columnName);
						}
					} else {
						fieldsAndValues += String.format("%s = ", columnName);
					}
					if (field.getType().toString().endsWith("String")) {
						if (field.get(this) != null) {
							fieldsAndValues += String.format("'%s', ", ((String) field.get(this)).replaceAll("'", "\\\\'"));
						} else {
							fieldsAndValues += "'', ";
						}
					} else if (field.getType().toString().endsWith("Date") || 
							   field.getType().toString().endsWith("PyDate") || 
							   field.getType().toString().endsWith("DateTime")) {
						Date date = (Date) field.get(this);
						if (date != null) {
							if (dateFieldAnnotation != null) {
								fieldsAndValues += String.format(
									"'%d-%02d-%02d', ",
									date.getYear() + 1900, 
									date.getMonth() + 1,
									date.getDate() 
								);
							} else if (timeFieldAnnotation != null) {
								fieldsAndValues += String.format(
									"'%02d:%02d:%02d', ",
									date.getHours(),
									date.getMinutes(), 
									date.getSeconds()
								);
							} else if (dateTimeFieldAnnotation != null) {
								fieldsAndValues += String.format(
									"'%d-%02d-%02d %02d:%02d:%02d', ",
									date.getYear() + 1900, 
									date.getMonth() + 1,
									date.getDate(), 
									date.getHours(),
									date.getMinutes(), 
									date.getSeconds()
								);
							} else {
								
							}
						} else {						
							if (dateFieldAnnotation != null) {
								defaultValue = JediORMEngine.getDefaultValue(dateFieldAnnotation);
								if (defaultValue.isEmpty()) {
									if (dateFieldAnnotation.auto_now()) {
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
										fieldsAndValues += String.format("'%s', ", sdf.format(new Date()));
									} else {
										fieldsAndValues = fieldsAndValues.replace(String.format("%s, ", TableUtil.getColumnName(field)), "");
									}
								} else {
									fieldsAndValues += String.format(
										defaultValue.equalsIgnoreCase("NULL") ? 
												"%s, " : "'%s', ", 
										defaultValue
									);
								}
							} else if (timeFieldAnnotation != null) {
								defaultValue = JediORMEngine.getDefaultValue(timeFieldAnnotation);
								if (defaultValue.isEmpty()) {
									if (timeFieldAnnotation.auto_now()) {
										SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
										fieldsAndValues += String.format("'%s', ", sdf.format(new Date()));
									} else {
										fieldsAndValues = fieldsAndValues.replace(String.format("%s, ", TableUtil.getColumnName(field)), "");
									}
								} else {
									fieldsAndValues += String.format(
										defaultValue.equalsIgnoreCase("NULL") ? 
												"%s, " : "'%s', ", 
										defaultValue
									);
								}
							} else if (dateTimeFieldAnnotation != null) {
								defaultValue = JediORMEngine.getDefaultValue(dateTimeFieldAnnotation);
								if (defaultValue.isEmpty()) {
									if (dateTimeFieldAnnotation.auto_now()) {
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
										fieldsAndValues += String.format("'%s', ", sdf.format(new Date()));
									} else {
										fieldsAndValues = fieldsAndValues.replace(String.format("%s, ", TableUtil.getColumnName(field)), "");
									}
								} else {
									fieldsAndValues += String.format(
											defaultValue.equalsIgnoreCase("NULL") ? 
													"%s, " : "'%s', ", 
											defaultValue
									);
								}
							} else {
								fieldsAndValues += String.format("'', ", field.get(this));
							}
						}
					} else {
						if (oneToOneFieldAnnotation != null || foreignKeyFieldAnnotation != null) {
							Object id = ((Model) field.get(this)).id;
							fieldsAndValues += String.format("%s, ", id);
						} else if (manyToManyFieldAnnotation != null) {

						} else {
							fieldsAndValues += String.format("%s, ", field.get(this));
						}
					}
				}
				fieldsAndValues = fieldsAndValues.substring(0, fieldsAndValues.lastIndexOf(','));
			} else {
				if (args.length > 0) {
					Field field = null;
					String fieldName = "";
					String fieldValue = "";
					String columnName = "";
					String columnValue = "";
					for (int i = 0; i < args.length; i++) {
						args[i] = args[i] == null ? "" : args[i].trim();
						if (args[i].isEmpty()) {
							continue;
						}
						fieldName = args[i].split("=")[0];
						columnName = TableUtil.getColumnName(fieldName);
						if (fieldName.endsWith("_id")) {
							fieldName = fieldName.replace("_id", "");
						}
						fieldValue = args[i].split("=")[1];
						columnValue = fieldValue;
						if (fieldValue.startsWith("'") && fieldValue.endsWith("'")) {
							fieldValue = fieldValue.substring(1, fieldValue.length() - 1);
						}
						field = this.getClass().getDeclaredField(fieldName);
						field.setAccessible(true);
						if (field.getType() == String.class) {
							field.set(this, fieldValue);
						} else if (field.getType() == Integer.class) {
							field.set(this, Integer.parseInt(fieldValue));
						} else if (field.getType() == Float.class) {
							field.set(this, Float.parseFloat(fieldValue));
						} else if (field.getType() == Double.class) {
							field.set(this, Double.parseDouble(fieldValue));
						} else if (field.getType() == Date.class) {
							field.set(this, Date.parse(fieldValue));
						} else if (field.getType() == Boolean.class) {
							field.set(this, Boolean.parseBoolean(fieldValue));
						} else if (field.getAnnotation(ForeignKeyField.class) != null) {
							if (field.get(this) != null) {
								((Model) field.get(this)).setId(Integer.parseInt(fieldValue));
							}
						} else {
						}
						fieldsAndValues += String.format("%s = %s, ", columnName, columnValue);
					}
					fieldsAndValues = fieldsAndValues.substring(0, fieldsAndValues.lastIndexOf(","));
				}
			}
			sql = String.format(
				"%s %s WHERE id = %s", 
				sql, 
				fieldsAndValues,
				jedi.db.models.Model.class.getDeclaredField("id").get(this)
			);
			if (JediORMEngine.DEBUG) {
				System.out.println(sql + ";\n");
			}
			statement = Model.connection.prepareStatement(sql);
			statement.execute();
			for (String manyToManySQL : manyToManySQLs) {
				if (JediORMEngine.DEBUG) {
					System.out.println(manyToManySQL + "\n");
				}
				statement = Model.connection.prepareStatement(manyToManySQL);
				statement.execute();
			}
			if (!Model.connection.getAutoCommit()) {
				Model.connection.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (!Model.connection.getAutoCommit()) {
					Model.connection.rollback();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (Model.connection != null && this.autoCloseConnection) {
					Model.connection.close();
					Model.connection = null;
				}				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void save() {
		try {
			if (!this.isPersisted) {
				this.insert();
			} else {
				this.update();
			}
			this.isPersisted(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public <T extends Model> T save(Class<T> modelClass) {
		this.save();
		return this.as(modelClass);
	}

	public void delete() {
		// TODO - Verificar a viabilidade de CascadeType.DELETE.
		PreparedStatement statement = null;
		try {
			if (Model.connection == null || !Model.connection.isValid(10)) {
				Model.connection = ConnectionFactory.getConnection();
			}
			String sql = "DELETE FROM";
			sql = String.format("%s %s WHERE", sql, tableName);
			sql = String.format(
				"%s id = %s", 
				sql, jedi.db.models.Model.class.getDeclaredField("id").get(this)
			);
			if (JediORMEngine.DEBUG) {
				System.out.println(sql + ";\n");
			}
			statement = Model.connection.prepareStatement(sql);
			statement.execute();			
			if (!Model.connection.getAutoCommit()) {
				Model.connection.commit();
			}
			this.isPersisted(false);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (!Model.connection.getAutoCommit()) {
					Model.connection.rollback();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (Model.connection != null && this.autoCloseConnection) {
					Model.connection.close();
					Model.connection = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public int compareTo(Model model) {
		if (this.id < model.id) {
			return -1;
		}
		if (this.id > model.id) {
			return 1;
		}
		return 0;
	}

	public String toString(int i) {
		// i - identation level
		String s = "";
		String identationToClass = "";
		String identationToFields = "    ";
		String identationToListItems = "        ";
		for (int j = 0; j < i; j++) {
			identationToClass += "    ";
			identationToFields += "    ";
			identationToListItems += "    ";
		}
		try {
			s = String.format(
				"%s%s {\n%sid: %s,", 
				identationToClass, 
				this
					.getClass()
					.getSimpleName(), 
				identationToFields, 
				jedi.db.models.Model.class
					.getDeclaredField("id")
					.get(this)
			);
			List<Field> fields = JediORMEngine.getAllFields(this.getClass());
			for (Field f : fields) {
				f.setAccessible(true);
				if (f.getName().equals("serialVersionUID"))
					continue;
				if (f.getName().equalsIgnoreCase("objects"))
					continue;
				if (f.getType().getSuperclass() != null && 
					f.getType().getSuperclass().getName().equals("jedi.db.models.Model")) {
					if (f.get(this) != null) {
						s += String.format(
							"\n%s%s: %s,", 
							identationToFields, 
							f.getName(), 
							((Model) f.get(this)).toString(i + 1).trim()
						);
					}
				} else if (f.getType().getName().equals("java.util.List")
						|| f.getType().getName().equals("jedi.db.models.QuerySet")) {
					String strItems = "";
					for (Object item : (List) f.get(this)) {
						strItems += String.format("\n%s,", ((Model) item).toString((i + 2)));
					}
					if (strItems.lastIndexOf(",") >= 0) {
						strItems = strItems.substring(0, strItems.lastIndexOf(","));
					}
					s += String.format(
						"\n%s%s: [%s\n%s],", 
						identationToFields, 
						f.getName(),						
						strItems, 
						identationToFields
					);
				} else {
					if (f.get(this) instanceof String) {
						s += String.format(
							"\n%s%s: \"%s\",", 
							identationToFields, 
							f.getName(), 
							f.get(this)
						);
					} else {
						s += String.format(
							"\n%s%s: %s,", 
							identationToFields, 
							f.getName(), 
							f.get(this)
						);
					}
				}
			}
			if (s.lastIndexOf(",") >= 0) {
				s = s.substring(0, s.lastIndexOf(","));
			}
			s += String.format("\n%s}", identationToClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public String toString() {
		return toString(0);
	}
	
	@Override
	public boolean equals(Object o) {
		boolean r = false;
		if (this.id == ((Model) o).id) {
			r = true;
		}
		return r;
	}

	/**
	 * @param i int
	 * @return String
	 */
	public String toJSON(int i) {
		// i - identation level
		String json = "";
		String identationToClass = "";
		String identationToFields = "    ";
		String identationToListItems = "        ";
		for (int j = 0; j < i; j++) {
			identationToClass += "    ";
			identationToFields += "    ";
			identationToListItems += "    ";
		}
		try {			
			json = String.format(
				"%s{\n%s\"id\": %s,",
				identationToClass,
				identationToFields,
				jedi.db.models.Model.class.getDeclaredField("id").get(this)
			);
			List<Field> fields = JediORMEngine.getAllFields(this.getClass());
			for (Field f : fields) {
				f.setAccessible(true);
				if (f.getName().equals("serialVersionUID"))
					continue;
				if (f.getName().equalsIgnoreCase("objects"))
					continue;
				if (f.getType().getSuperclass() != null && 
					f.getType().getSuperclass().getName().equals("jedi.db.models.Model")) {
					if (f.get(this) != null) {
						json += String.format(
							"\n%s\"%s\": %s,", 
							identationToFields, 
							f.getName(), 
							((Model) f.get(this)).toJSON(i + 1).trim()
						);
					}
				} else if (f.getType().getName().equals("java.util.List")
						|| f.getType().getName().equals("jedi.db.models.QuerySet")) {
					String strItems = "";
					for (Object item : (List) f.get(this)) {
						strItems += String.format("\n%s,", ((Model) item).toJSON((i + 2)));
					}
					if (strItems.lastIndexOf(",") >= 0) {
						strItems = strItems.substring(0, strItems.lastIndexOf(","));
					}
					json += String.format(
						"\n%s\"%s\": [%s\n%s],", 
						identationToFields, 
						f.getName(), 
						strItems, 
						identationToFields
					);
				} else {
					if (f.get(this) instanceof String) {
						json += String.format(
							"\n%s\"%s\": \"%s\",", 
							identationToFields, 
							f.getName(), 
							f.get(this)
						);
					} else {
						json += String.format(
							"\n%s\"%s\": %s,", 
							identationToFields, 
							f.getName(), 
							f.get(this)
						);
					}
				}
			}
			if (json.lastIndexOf(",") >= 0) {
				json = json.substring(0, json.lastIndexOf(","));
			}
			json += String.format("\n%s}", identationToClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * @return String
	 */
	public String toJSON() {
		return toJSON(0);
	}

	public String toXML(int i) {
		// i - identation level
		String xmlElement = this.getClass().getSimpleName().toLowerCase();
		StringBuilder xml = new StringBuilder();
		StringBuilder xmlElementAttributes = new StringBuilder();
		StringBuilder xmlChildElements = new StringBuilder();
		xmlElementAttributes.append("");
		String xmlElementString = "";
		String identationToElement = "";
		String identationToChildElements = "    ";
		for (int j = 0; j < i; j++) {
			identationToElement += "    ";
			identationToChildElements += "    ";
		}
		try {
			xmlElementAttributes.append(
				String.format(
					"id=\"%d\"", 
					jedi.db.models.Model.class
						.getDeclaredField("id")
						.getInt(this)
				)
			);
			List<Field> fields = JediORMEngine.getAllFields(this.getClass());
			for (Field f : fields) {
				f.setAccessible(true);
				if (f.getName().equals("serialVersionUID"))
					continue;
				if (f.getName().equalsIgnoreCase("objects"))
					continue;
				if (f.getType().getSuperclass() != null && 
					f.getType().getSuperclass().getName().equals("jedi.db.models.Model")) {
					xmlChildElements.append(String.format("\n%s\n", ((Model) f.get(this)).toXML(i + 1)));
				} else if (f.getType().getName().equals("java.util.List")
						|| f.getType().getName().equals("jedi.db.models.QuerySet")) {
					String xmlChildOpenTag = "";
					String xmlChildCloseTag = "";
					Table tableAnnotation = null;
					if (!((List) f.get(this)).isEmpty()) {
						tableAnnotation = ((List) f.get(this)).get(0).getClass().getAnnotation(Table.class);
						if (tableAnnotation != null && !tableAnnotation.name().trim().isEmpty()) {
							xmlChildOpenTag = String.format(
								"\n%s<%s>",
								identationToChildElements, 
								tableAnnotation
									.name()
									.trim()
									.toLowerCase()
							);
							xmlChildCloseTag = String.format(
								"\n%s</%s>\n",
								identationToChildElements, 
								tableAnnotation
									.name()
									.trim()
									.toLowerCase()
							);
						} else {
							xmlChildOpenTag = String.format(
								"\n%s<%ss>", 
								identationToChildElements, 
								((List) f
									.get(this))
									.get(0)
									.getClass()
									.getSimpleName()
									.toLowerCase()
							);
							xmlChildCloseTag = String.format(
								"\n%s</%ss>",
								identationToChildElements, 
								((List) f
									.get(this))
									.get(0)
									.getClass()
									.getSimpleName()
									.toLowerCase()
							);
						}
						xmlChildElements.append(xmlChildOpenTag);
						for (Object item : (List) f.get(this)) {
							xmlChildElements.append(String.format("\n%s", ((Model) item).toXML(i + 2)));
						}
						xmlChildElements.append(xmlChildCloseTag);
					}
				} else {
					xmlElementAttributes.append(String.format(" %s=\"%s\"", f.getName(), f.get(this)));
				}
			}
			if (xmlChildElements.toString().isEmpty()) {
				xml.append(String.format(
					"%s<%s %s />", 
					identationToElement, 
					xmlElement, 
					xmlElementAttributes.toString()
				)
			);
			} else {
				xml.append(
					String.format(
						"%s<%s %s>%s%s</%s>", 
						identationToElement, 
						xmlElement, 
						xmlElementAttributes.toString(), 
						xmlChildElements, 
						identationToElement, 
						xmlElement
					)
				);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xml.toString();
	}

	public String toXML() {
		return toXML(0);
	}

	public String toExtenseXML(int i) {
		// i - identation level
		String xmlElement = this.getClass().getSimpleName().toLowerCase();
		StringBuilder xml = new StringBuilder();
		StringBuilder xmlElementAttributes = new StringBuilder();
		StringBuilder xmlChildElements = new StringBuilder();
		xmlElementAttributes.append("");
		String xmlElementString = "";
		String identationToElement = "";
		String identationToAttributes = "    ";
		String identationToChildElements = "    ";
		for (int j = 0; j < i; j++) {
			identationToElement += "    ";
			identationToAttributes += "    ";
			identationToChildElements += "    ";
		}
		try {
			xmlElementAttributes.append(
				String.format(
					"\n%s<id>%d</id>\n", 
					identationToAttributes, 
					jedi.db.models.Model.class
						.getDeclaredField("id")
						.get(this)
				)
			);
			List<Field> fields = JediORMEngine.getAllFields(this.getClass());
			for (Field f : fields) {
				f.setAccessible(true);
				if (f.getName().equals("serialVersionUID"))
					continue;
				if (f.getName().equalsIgnoreCase("objects"))
					continue;
				if (f.getType().getSuperclass() != null && 
					f.getType().getSuperclass().getName().equals("jedi.db.models.Model")) {
					xmlChildElements.append(String.format("%s\n", ((Model) f.get(this)).toExtenseXML(i + 1)));
				} else if (f.getType().getName().equals("java.util.List")
						|| f.getType().getName().equals("jedi.db.models.QuerySet")) {
					String xmlChildOpenTag = "";
					String xmlChildCloseTag = "";
					Table tableAnnotation = null;
					if (!((List) f.get(this)).isEmpty()) {
						tableAnnotation = ((List) f
							.get(this))
							.get(0)
							.getClass()
							.getAnnotation(Table.class);
						if (tableAnnotation != null && 
							!tableAnnotation.name().trim().isEmpty()) {
							xmlChildOpenTag = String.format(
								"%s<%s>",
								identationToChildElements, 
								tableAnnotation
									.name()
									.trim()
									.toLowerCase()
							);
							xmlChildCloseTag = String.format(
								"\n%s</%s>\n", 
								identationToChildElements, 
								tableAnnotation
									.name()
									.trim()
									.toLowerCase()
							);
						} else {
							xmlChildOpenTag = String.format(
								"%s<%ss>", 
								identationToChildElements, 
								((List) f
									.get(this))
									.get(0)
									.getClass()
									.getSimpleName()
									.toLowerCase()
							);
							xmlChildCloseTag = String.format(
								"\n%s</%ss>\n", 
								identationToChildElements, 
								((List) f
									.get(this))
									.get(0)
									.getClass()
									.getSimpleName()
									.toLowerCase()
							);
						}
						xmlChildElements.append(xmlChildOpenTag);
						for (Object item : (List) f.get(this)) {
							xmlChildElements.append(
								String.format(
									"\n%s",
									((Model) item).toExtenseXML(i + 2)
								)
							);
						}
						xmlChildElements.append(xmlChildCloseTag);
					}
				} else {
					xmlElementAttributes.append(
						String.format(
							"%s<%s>%s</%s>\n", 
							identationToAttributes, 
							f.getName(), 
							f.get(this), 
							f.getName()
						)
					);
				}
			}
			if (xmlChildElements.toString().isEmpty()) {
				xml.append(
					String.format(
						"%s<%s>%s%s</%s>", 
						identationToElement, 
						xmlElement,
						xmlElementAttributes
							.toString(), 
						identationToElement,
						xmlElement
					)
				);
			} else {
				xml.append(
					String.format(
						"%s<%s>%s%s%s</%s>",
						identationToElement, 
						xmlElement,
						xmlElementAttributes
							.toString(), 
						xmlChildElements,
						identationToElement, 
						xmlElement
					)
				);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xml.toString();
	}

	public String toExtenseXML() {
		StringBuilder xml = new StringBuilder();
		xml.append(toExtenseXML(0));
		return xml.toString();
	}
	
	/**
	 * Method that returns the model data representation 
	 * in CSV (Comma Separated Value) format. 
	 * @return String
	 */
	public String toCSV() {
		String csv = this.getId() > 0 ? String.format("%d,", this.getId()) : "";
		List<Field> fields = JediORMEngine.getAllFields(this.getClass());
		for (Field field : fields) {
			field.setAccessible(true);
			if (field.getName().equals("serialVersionUID")) {
				continue;
			}
			if (field.getName().equals("objects")) {
				continue;
			}
			try {
				if (field.get(this) != null) {
					if (Collection.class.isAssignableFrom(field.getType())) {
						List<Model> models = (List<Model>) field.get(this); 
						for (Model model : models) {
							csv += String.format("\"%s\",", model.toCSV());
						}
					} else if (Model.class.isAssignableFrom(field.getType())) {
						csv += String.format("\"%s\",", ((Model) field.get(this)).toCSV());
					} else {
						String s = field.get(this).toString();
						if (s.startsWith("\"") && s.endsWith("\"")) {
							if (s.contains(",")) {
								csv += String.format("\"%s\",", field.get(this));
							} else {
								csv += String.format("\"\"%s\"\",", field.get(this));
							}
						} else {
							csv += String.format("%s,", field.get(this));
						}
					}
				}				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}		
		csv = csv.endsWith(",") ? csv.substring(0, csv.length() - 1) : csv; 
		return csv;
	}

	public <T extends Model> T as(Class<T> c) {
		return (T) this;
	}
}