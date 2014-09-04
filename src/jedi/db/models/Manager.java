/***********************************************************************************************
 * @(#)Manager.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/08/07
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

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import jedi.db.FetchType;
import jedi.db.connection.ConnectionFactory;
import jedi.db.engine.JediORMEngine;
import jedi.db.util.TableUtil;
import jedi.types.DateTime;

/**
 * <p>
 * Classe que gerencia consultas relacionadas aos dados do modelo
 * em um banco de dados.
 * </p>
 * @author Thiago Alexandre Martins Monteiro
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class Manager {
    private Connection connection;
    private String tableName;
    private boolean autoCloseConnection = JediORMEngine.DATABASE_AUTO_CLOSE_CONNECTION;
    
    public Class entity;

    public Manager() {
    	this(null, null);
    }

    public Manager(Connection connection) {
        this(null, connection);
    }

    public Manager(Class entity) {
    	this(entity, null);
    }
    
    public Manager(Class entity, Connection connection) {
    	this(entity, connection, true);
    }
    
    public Manager(Class entity, Connection connection, boolean autoCloseConnection) {
    	this.autoCloseConnection = autoCloseConnection;
    	if (entity != null && Model.class.isAssignableFrom(entity)) {
    		this.entity = entity;
    		tableName = TableUtil.getTableName(this.entity);
    	}
    	if (connection != null) {
    		this.connection = connection;
    	} else {
    		if (!JediORMEngine.JEDI_PROPERTIES_LOADED) {
    			JediORMEngine.loadJediProperties();
    		}
    		this.connection = ConnectionFactory.getConnection();
    	}
    }
    
    public Manager(Class entity, boolean autoCloseConnection) {
    	this.autoCloseConnection = autoCloseConnection;
    	if (entity != null && Model.class.isAssignableFrom(entity)) {
    		this.entity = entity;
    		tableName = TableUtil.getTableName(this.entity);
    	}		
    }

    public Connection getConnection() {
        return connection;
    }
    
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
        
    public String getTableName() {
    	return tableName;
    }
    
    public void setTableName(String tableName) {
    	this.tableName = TableUtil.getTableName(tableName);
    }
    
    public boolean getAutoCloseConnection() {
    	return autoCloseConnection;
    }
    
    public void setAutoCloseConnection(boolean autoCloseConnection) {
    	this.autoCloseConnection = autoCloseConnection;
    }

    /**
     * Returns all the rows in a table.
     * 
     * @return QuerySet
     */
    private <T extends Model> QuerySet<T> all(Class<T> modelClass) {
        QuerySet<T> querySet = new QuerySet<T>();
        querySet.setEntity(this.entity);
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        OneToOneField oneToOneFieldAnnotation = null;
        ForeignKeyField foreignKeyFieldAnnotation = null;
        ManyToManyField manyToManyFieldAnnotation = null;
        try {
        	if (this.connection == null || !this.connection.isValid(10)) {
				this.connection = ConnectionFactory.getConnection();
			}
            String sql = "SELECT * FROM";            
            tableName = TableUtil.getTableName(modelClass);
            sql = String.format("%s %s", sql, tableName);
            if (JediORMEngine.DEBUG) {
            	System.out.println(sql + ";\n");
            }
            statement = this.connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return querySet;
            }
            resultSet.beforeFirst();
            while (resultSet.next()) {
                Object obj = entity.newInstance();
                Field id = jedi.db.models.Model.class.getDeclaredField("id");
                id.setAccessible(true);
                // Oracle returns BigDecimal object.
                if (this.connection.toString().startsWith("oracle")) {
                    id.set(obj, ((java.math.BigDecimal) resultSet.getObject(id.toString()
                        .substring(id.toString().lastIndexOf('.') + 1))).intValue());
                } else {
                    // MySQL and PostgreSQL returns a Integer object.
                    id.set(obj, resultSet.getObject(id.toString()
                		.substring(id.toString().lastIndexOf('.') + 1)));
                }
                for (Field field : JediORMEngine.getAllFields(this.entity)) {
                    field.setAccessible(true);
                    if (!JediORMEngine.isJediField(field)) {
                    	continue;
                    }
                    if (field.toString().substring(field.toString()
                		.lastIndexOf('.') + 1).equals("serialVersionUID")) {
                        continue;
                    }
                    if (field.getName().equalsIgnoreCase("objects")) {
                        continue;
                    }
                    oneToOneFieldAnnotation = field.getAnnotation(OneToOneField.class);
                    foreignKeyFieldAnnotation = field.getAnnotation(ForeignKeyField.class);
                    manyToManyFieldAnnotation = field.getAnnotation(ManyToManyField.class);
                    FetchType fetchType = JediORMEngine.FETCH_TYPE;
                    Manager manager = null;
                    if (manyToManyFieldAnnotation != null) {
                    	fetchType = fetchType.equals(FetchType.NONE) ? 
                				manyToManyFieldAnnotation.fetch_type() : fetchType;
                		if (fetchType.equals(FetchType.EAGER)) {
                			String packageName = this.entity.getPackage().getName();
                        	String model = manyToManyFieldAnnotation.model().getSimpleName();
                        	model = Model.class.getSimpleName().equals(model) ? "" : model;
                        	Class superClazz = null;
                        	Class clazz = null;
                            if (model.isEmpty()) {
                            	ParameterizedType genericType = null;
                            	if (ParameterizedType.class.isAssignableFrom(field.getGenericType().getClass())) {
                                    genericType = (ParameterizedType) field.getGenericType();
                                    superClazz = ((Class) (genericType.getActualTypeArguments()[0])).getSuperclass();
                                    if (superClazz == Model.class) {
                                        clazz = (Class) genericType.getActualTypeArguments()[0];
                                        model = clazz.getSimpleName();
                                    }
                                }                                            	
                            }
                            String references = manyToManyFieldAnnotation.references();
                            if (references == null || references.trim().isEmpty()) {
                            	if (clazz != null) {
                            		references = TableUtil.getTableName(clazz);                        		
                            	} else {
                            		references = TableUtil.getTableName(model);
                            	}
                            }
                            String intermediateModelclassName = String.format("%s.%s", packageName, model);
                            Class associatedModelClass = Class.forName(intermediateModelclassName);
                            manager = new Manager(associatedModelClass);
                            QuerySet querySetAssociatedModels = null;
                            String intermediateModelName = manyToManyFieldAnnotation.through().getSimpleName();
                            intermediateModelName = Model.class.getSimpleName().equals(intermediateModelName) ? "" : intermediateModelName;
                            if (intermediateModelName != null && !intermediateModelName.trim().isEmpty()) {
                            	intermediateModelclassName = String.format("%s.%s", packageName, intermediateModelName);
                            	Class intermediateModelClass = Class.forName(intermediateModelclassName);
                            	String intermediateTableName = ((Model) intermediateModelClass.newInstance()).getTableName();
                            	querySetAssociatedModels = manager.raw(
                                    String.format(
                                        "SELECT * FROM %s WHERE id IN (SELECT %s_id FROM %s WHERE %s_id = %d)",
                                        TableUtil.getTableName(references),
                                        TableUtil.getColumnName(model),                                         
                                        intermediateTableName,
                                        TableUtil.getColumnName(obj.getClass()),
                                        ((Model) obj).getId()
                                    ), 
                                    associatedModelClass
                                );
                            } else {
                                querySetAssociatedModels = manager.raw(
                                    String.format(
                                        "SELECT * FROM %s WHERE id IN (SELECT %s_id FROM %s_%s WHERE %s_id = %d)",
                                        TableUtil.getTableName(references),                                         
                                        TableUtil.getColumnName(model), 
                                        tableName, 
                                        TableUtil.getTableName(references), 
                                        TableUtil.getColumnName(obj.getClass()),
                                        ((Model) obj).getId()
                                    ), 
                                    associatedModelClass
                                );
                            }
                            field.set(obj, querySetAssociatedModels);
                		} else {
                			field.set(obj, null);
                		}
                    } else if (oneToOneFieldAnnotation != null || foreignKeyFieldAnnotation != null) {
                    	if (oneToOneFieldAnnotation != null) {
                			fetchType = fetchType.equals(FetchType.NONE) ? 
                    				oneToOneFieldAnnotation.fetch_type() : fetchType;
                		} else {
                			fetchType = fetchType.equals(FetchType.NONE) ? 
                					foreignKeyFieldAnnotation.fetch_type() : fetchType;
                		}
                		if (fetchType.equals(FetchType.EAGER)) {
                			// Recovers the attribute class.
                            Class associatedModelClass = Class.forName(field.getType().getName());
                            manager = new Manager(associatedModelClass);
                            String s = String.format("%s_id", TableUtil.getColumnName(field));
                            Object o = resultSet.getObject(s);
                            Model associatedModel = manager.get("id", o);
                            // References a model associated by a foreign key.
                            field.set(obj, associatedModel);
                		} else {
                			field.set(obj, null);
                		}
                    } else {
                        // Sets the fields that aren't Model instances.
                        if ((field.getType().getSimpleName().equals("int") || 
                    		 field.getType().getSimpleName().equals("Integer")) && 
                    		 this.connection.toString().startsWith("oracle")) {
                            if (resultSet.getObject(TableUtil.getColumnName(field)) == null) {
                                field.set(obj, 0);
                            } else {
                            	String columnName = TableUtil.getColumnName(field);
                            	BigDecimal bigDecimal = (BigDecimal) resultSet.getObject(columnName);
                                field.set(obj, bigDecimal.intValue());
                            }
                        } else {
                        	Object columnValue = resultSet.getObject(TableUtil.getColumnName(field));
                        	columnValue = convertZeroDateToNull(columnValue);
                        	if (columnValue instanceof Timestamp) {
                        		Timestamp timestamp = (Timestamp) columnValue;
                        		columnValue = new DateTime(timestamp.getTime());
                        	}                        	
                    		field.set(obj, columnValue);                        	
                        }
                    }
                    manager = null;
                }
                T model = (T) obj;
                if (model != null) {
                    model.isPersisted(true);
                }
                querySet.add(model);
            }            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try {
        		if (resultSet != null) {
        			resultSet.close();
        		}        		
        		if (statement != null) {
        			statement.close();
        		}        		
        		if (this.connection != null && this.autoCloseConnection) {
        			this.connection.close();
        		}
        	} catch (SQLException e) {
        		e.printStackTrace();
        	}
        }
        return querySet;
    }
    
    public <T extends Model> QuerySet<T> all() {
        return (QuerySet<T>) this.all(this.entity);
    }

    /**
     * @param modelClass
     * @param fields
     * @return
     */
    private <T extends Model> QuerySet<T> filter(Class<T> modelClass, String... fields) {
        QuerySet<T> querySet = new QuerySet<T>();
        querySet.setEntity(this.entity);
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        OneToOneField oneToOneFieldAnnotation = null;
        ForeignKeyField foreignKeyFieldAnnotation = null;
        ManyToManyField manyToManyFieldAnnotation = null;
        if (fields != null) {
            try {
            	if (this.connection == null || !this.connection.isValid(10)) {
    				this.connection = ConnectionFactory.getConnection();
    			}
                String sql = String.format("SELECT * FROM %s WHERE", tableName);
                String where = "";
                String fieldName = "";
                String fieldValue = "";
                // Iterates through the pairs field=value passed.
                for (int i = 0; i < fields.length; i++) {
                	fields[i] = fields[i] == null ? "" : fields[i].trim();
                	if (fields[i].isEmpty()) {
                		continue;
                	}
                	if (fields[i].equalsIgnoreCase("AND")) {
                		fields[i] = "AND";
                	}
                	if (fields[i].equalsIgnoreCase("OR")) {
                		fields[i] = "OR";
                	}
                    // Changes the name of the field to the corresponding pattern name on the database.
                    if (fields[i].contains("=")) {
                    	fieldName = fields[i].substring(0, fields[i].lastIndexOf("="));
                    	fieldName = TableUtil.getColumnName(fieldName);
                    	fieldValue = fields[i].substring(fields[i].lastIndexOf("="));
                        fields[i] = String.format("%s%s", fieldName, fieldValue);
                    }
                    // Adds a blank space between the field name and value.
                    fields[i] = fields[i].replace("=", " = ");
                    // Replaces % by \%
                    fields[i] = fields[i].replace("%", "\\%");
                    // Adds a blank space between the values separated by commas.
                    fields[i] = fields[i].replace(",", ", ");
                    // Checks if the current pair contains __startswith, __contains or __endswith.
                    if (fields[i].indexOf("__startswith") > -1 || fields[i].indexOf("__contains") > -1 
                        || fields[i].indexOf("__endswith") > -1) {
                        // Creates a LIKE statement in SQL.
                        if (fields[i].indexOf("__startswith") > -1) {
                            fields[i] = fields[i].replace("__startswith = ", " LIKE ");
                            // Replaces 'value' by 'value%'.
                            fields[i] = fields[i].substring(0, fields[i].lastIndexOf("\'"));
                            fields[i] = fields[i] + "%\'";
                        } else if (fields[i].indexOf("__contains") > -1) {
                            fields[i] = fields[i].replace("__contains = ", " LIKE ");
                            // Replaces 'value' by '%value%'.
                            fields[i] = fields[i].replaceFirst("\'", "\'%");
                            fields[i] = fields[i].substring(0, fields[i].lastIndexOf("\'"));
                            fields[i] = fields[i] + "%\'";
                        } else if (fields[i].indexOf("__endswith") > -1) {
                            fields[i] = fields[i].replace("__endswith = ", " LIKE ");
                            // Replaces 'value' by '%value'.
                            fields[i] = fields[i].replaceFirst("\'", "\'%");
                        }
                    }
                    if (fields[i].indexOf("__in") > -1) {
                        // Creates a IN statement in SQL.
                        fields[i] = fields[i].replace("__in = ", " IN ");
                        // Replaces [] by ()
                        fields[i] = fields[i].replace("[", "(");
                        fields[i] = fields[i].replace("]", ")");
                    }
                    if (fields[i].indexOf("__range") > -1) {
                        // Creates a BETWEEN statement in SQL.
                        fields[i] = fields[i].replace("__range = ", " BETWEEN ");
                        // Removes [ or ] characters.
                        fields[i] = fields[i].replace("[", "");
                        fields[i] = fields[i].replace("]", "");
                        // Replaces , (comma character) by AND.
                        fields[i] = fields[i].replace(", ", " AND ");
                    }
                    if (fields[i].indexOf("__lt") > -1) {
                        fields[i] = fields[i].replace("__lt = ", " < ");
                    }
                    if (fields[i].indexOf("__lte") > -1) {
                        fields[i] = fields[i].replace("__lte = ", " <= ");
                    }
                    if (fields[i].indexOf("__gt") > -1) {
                        fields[i] = fields[i].replace("__gt = ", " > ");
                    }
                    if (fields[i].indexOf("__gte") > -1) {
                        fields[i] = fields[i].replace("__gte = ", " >= ");
                    }
                    if (fields[i].indexOf("__exact") > -1) {
                        fields[i] = fields[i].replace("__exact = ", " = ");
                    }
                    if (fields[i].indexOf("__isnull") > -1) {
                        String bool = fields[i].substring(fields[i].indexOf("=") + 1, fields[i].length()).trim();
                        if (bool.equalsIgnoreCase("true")) {
                            fields[i] = fields[i].replace("__isnull = ", " IS NULL ");
                        }
                        if (bool.equalsIgnoreCase("false")) {
                            fields[i] = fields[i].replace("__isnull = ", " IS NOT NULL ");
                        }
                        fields[i] = fields[i].replace(bool, "");
                    }
                    where += fields[i] + " AND ";
                    where = where.replace(" AND OR AND", " OR");
                    where = where.replace(" AND AND AND", " AND");
                }
                where = where.substring(0, where.lastIndexOf("AND"));
                sql = String.format("%s %s", sql, where);
                if (JediORMEngine.DEBUG) {
                	System.out.println(sql + ";\n");
                }
                statement = this.connection.prepareStatement(sql);
                resultSet = statement.executeQuery();
                if (!resultSet.next()) {
                    return querySet;
                }
                resultSet.beforeFirst();
                while (resultSet.next()) {
                    Object obj = entity.newInstance();
                    if (resultSet.getObject("id") != null) {
                        Field id = jedi.db.models.Model.class.getDeclaredField("id");
                        id.setAccessible(true);
                        if (this.connection.toString().startsWith("oracle")) {
                            id.set(obj, ((java.math.BigDecimal) resultSet.getObject(id.getName())).intValue());
                        } else {
                            id.set(obj, resultSet.getObject(id.getName()));
                        }
                    }
                    // Iterates through the fields of the model.
                    for (Field field : JediORMEngine.getAllFields(this.entity)) {
                        // Sets private or protected fields as accessible.
                        field.setAccessible(true);
                        // Discards non annotated fields
                        if (!JediORMEngine.isJediField(field)) {
                        	continue;
                        }
                        // Discards the serialVersionUID field.
                        if (field.getName().equals("serialVersionUID"))
                            continue;
                        // Discards the objects field.
                        if (field.getName().equalsIgnoreCase("objects"))
                            continue;
                        // Checks if the field are annotated as OneToOneField, ForeignKeyField or ManyToManyField.
                        oneToOneFieldAnnotation = field.getAnnotation(OneToOneField.class);
                        foreignKeyFieldAnnotation = field.getAnnotation(ForeignKeyField.class);
                        manyToManyFieldAnnotation = field.getAnnotation(ManyToManyField.class);
                        FetchType fetchType = JediORMEngine.FETCH_TYPE;
                        Manager manager = null;
                        if (manyToManyFieldAnnotation != null) {
                        	fetchType = fetchType.equals(FetchType.NONE) ? 
                        			manyToManyFieldAnnotation.fetch_type() : fetchType;
                    		if (fetchType.equals(FetchType.EAGER)) {
                    			Class superClazz = null;
                            	Class clazz = null;
                            	String packageName = this.entity.getPackage().getName();
                            	String model = manyToManyFieldAnnotation.model().getSimpleName();
                            	model = Model.class.getSimpleName().equals(model) ? "" : model;
                                if (model.isEmpty()) {
                                	ParameterizedType genericType = null;
                                	if (ParameterizedType.class.isAssignableFrom(field.getGenericType().getClass())) {
                                        genericType = (ParameterizedType) field.getGenericType();
                                        superClazz = ((Class) (genericType.getActualTypeArguments()[0])).getSuperclass();
                                        if (superClazz == Model.class) {
                                            clazz = (Class) genericType.getActualTypeArguments()[0];
                                            model = clazz.getSimpleName();
                                        }
                                    }                                            	
                                }
                                String references = manyToManyFieldAnnotation.references();
                                if (references == null || references.trim().isEmpty()) {
                                	if (clazz != null) {
                                		references = TableUtil.getTableName(clazz);
                                	} else {
                                		references = TableUtil.getTableName(model);
                                	}
                                }
                                Class associatedModelClass = Class.forName(String.format("%s.%s", packageName, model));
                                manager = new Manager(associatedModelClass);
                                List<List<HashMap<String, Object>>> recordSet = null;
                                // Performs a SQL query.
                                recordSet = manager.raw(
                                    String.format(
                                        "SELECT %s_id FROM %s_%s WHERE %s_id = %d",                                    
                                        TableUtil.getColumnName(model),
                                        tableName,
                                        TableUtil.getTableName(references),
                                        TableUtil.getColumnName(this.entity),
                                        ((Model) obj).id()
                                    )
                                );
                                String args = recordSet.toString();
                                args = args.replace("[", "");
                                args = args.replace("{", "");
                                args = args.replace("]", "");
                                args = args.replace("}", "");
                                args = args.replace("=", "");
                                args = args.replace(", ", ",");
                                args = args.replace(String.format("%s_id", TableUtil.getColumnName(model)), "");
                                args = String.format("id__in=[%s]", args);
                                QuerySet querySetAssociatedModels = manager.filter(args);
                                field.set(obj, querySetAssociatedModels);
                    		} else {
                    			field.set(obj, null);
                    		}
                        } else if (oneToOneFieldAnnotation != null || foreignKeyFieldAnnotation != null) {
                        	if (oneToOneFieldAnnotation != null) {
                    			fetchType = fetchType.equals(FetchType.NONE) ?
                        				oneToOneFieldAnnotation.fetch_type() : fetchType;
                    		} else {
                    			fetchType = fetchType.equals(FetchType.NONE) ?
                        				foreignKeyFieldAnnotation.fetch_type() : fetchType;
                    		}
                    		if (fetchType.equals(FetchType.EAGER)) {
                    			// If it's recovers the field's class.
                                Class associatedModelClass = Class.forName(field.getType().getName());
                                // Instanciates a Model Manager.
                                manager = new Manager(associatedModelClass);                            
                                String columnName = TableUtil.getColumnName(field);
                                Object id = resultSet.getObject(String.format("%s_id", columnName));
                                Model associatedModel = manager.get("id", id);
                                // Calls the get method recursivelly.                            
                                // References the model associated by foreign key annotation.
                                field.set(obj, associatedModel);
                    		} else {
                    			field.set(obj, null);
                    		}
                        } else {
                            // Sets fields the aren't Model's instances.
                            if ((field.getType().getSimpleName().equals("int") || 
                        		 field.getType().getSimpleName().equals("Integer")) && 
                        		 this.connection.toString().startsWith("oracle")) {
                                if (resultSet.getObject(TableUtil.getColumnName(field.getName())) == null) {
                                    field.set(obj, 0);
                                } else {
                                	String columnName = TableUtil.getColumnName(field.getName());
                                	BigDecimal columnValue = (BigDecimal) resultSet.getObject(columnName);
                                    field.set(obj, columnValue.intValue());
                                }
                            } else {
                            	String columnName = TableUtil.getColumnName(field.getName());
                            	Object columnValue = resultSet.getObject(columnName);                        	
                            	if (columnValue instanceof Timestamp) {
                            		Timestamp timestamp = (Timestamp) columnValue;
                            		columnValue = new DateTime(timestamp.getTime());
                            	}                        	
                        		field.set(obj, columnValue);
                            }
                        }
                        manager = null;
                    }
                    T model = (T) obj;
                    if (model != null) {
                        model.isPersisted(true);
                    }
                    querySet.add(model);
                }
                if (querySet != null) {
                    querySet.isPersisted(true);
                }                
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            	try {
            		if (resultSet != null) {
            			resultSet.close();
            		}            		
            		if (statement != null) {
            			statement.close();
            		}            		
            		if (this.connection != null && this.autoCloseConnection) {
            			this.connection.close();
            		}
            	} catch (SQLException e) {
            		e.printStackTrace();
            	}
            }
        }
        return querySet;
    }
    
    public <T extends Model> QuerySet<T> filter(String... fields) {
        return (QuerySet<T>) this.filter(this.entity, fields);
    }

    /**
     * @param modelClass
     * @param list
     * @return
     */
    private <T extends Model> T create(Class<T> modelClass, String... list) {
        Object obj = null;
        PreparedStatement statement = null;
        // TODO - Verificar como funciona o create com OnetoOneField, ForeignKeyField e ManyToManyField.
        OneToOneField oneToOneFieldAnnotation = null;
        ForeignKeyField foreignKeyFieldAnnotation = null;
        // ManyToManyField manyToManyFieldAnnotation = null;
        if (list != null) {
            try {
            	if (this.connection == null || !this.connection.isValid(10)) {
    				this.connection = ConnectionFactory.getConnection();
    			}
                String sql = String.format("INSERT INTO %s", tableName);
                String fields = "";
                String field = "";
                String values = "";
                String value = "";
                // Instanciates a model object managed by this Manager.
                obj = this.entity.newInstance();
                for (int i = 0; i < list.length; i++) {
                	list[i] = list[i] == null ? "" : list[i].trim();
                	if (list[i].isEmpty()) {
                		continue;
                	}
                    field = list[i].split("=")[0];
                    value = list[i].split("=")[1];
                    Field f = null;
                    if (field.endsWith("_id")) {
                    	f = JediORMEngine.getField(field.replace("_id", ""), this.entity);
                    } else {
                        f = JediORMEngine.getField(field, this.entity);
                    }
                    // Changes the field name to reflect the pattern to the table column names.
                    field = String.format("%s", TableUtil.getColumnName(field));                    
                    // Handles the insertion of the OneToOneField, ForeignKeyField or ManyToManyField.
                    oneToOneFieldAnnotation = f.getAnnotation(OneToOneField.class);
                    foreignKeyFieldAnnotation = f.getAnnotation(ForeignKeyField.class);
                    // manyToManyFieldAnnotation = f.getAnnotation(ManyToManyField.class);
                    // Allows access to the private and protected fields (attributes).
                    f.setAccessible(true);
                    if (!JediORMEngine.isJediField(f)) {
                    	continue;
                    }
                    // Discards serialVersionUID field.
                    if (f.getName().equals("serialVersionUID"))
                        continue;
                    // Discards objects field.
                    if (f.getName().equalsIgnoreCase("objects"))
                        continue;
                    // Converts the data to the appropriate type.
                    if (value.matches("\\d+")) {
                        if (oneToOneFieldAnnotation != null || foreignKeyFieldAnnotation != null) {
                            Manager manager = new Manager(f.getType());
                            f.set(obj, manager.get("id", value));
                        } else {
                            f.set(obj, Integer.parseInt(value)); // Integer
                        }
                    } else if (value.matches("\\d+f")) { // Float
                        f.set(obj, Float.parseFloat(value));
                    } else if (value.matches("\\d+.d+")) { // Double
                        f.set(obj, Double.parseDouble(value));
                    } else if (f.getType().getName().equals("java.util.Date") ||
                    		f.getType().getName().equals("jedi.types.DateTime")) {
                    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    	Date date = formatter.parse(list[i].split("=")[1].replace("'", ""));
                    	f.set(obj, date);
                    } else { // String
                        f.set(obj, list[i].split("=")[1]);
                    }
                    fields += field + ", ";
                    values += value + ", ";
                }
                fields = fields.substring(0, fields.lastIndexOf(","));
                values = values.substring(0, values.lastIndexOf(","));
                sql = String.format("%s (%s) VALUES (%s)", sql, fields, values);
                if (JediORMEngine.DEBUG) {
                	System.out.println(sql + ";\n");
                }
                statement = this.connection.prepareStatement(sql);
                statement.execute();                
                Field f = jedi.db.models.Model.class.getDeclaredField("id");
                f.setAccessible(true);
                /* Gets the primary key (pk) of the last row inserted and 
                 * assigns it to the model.
                 */
                f.set(obj, this.getLastInsertedID());
                T model = (T) obj;
                if (model != null) {
                    model.isPersisted(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            	try {                       		
            		if (statement != null) {
            			statement.close();
            		}            		
            		if (this.connection != null && this.autoCloseConnection) {
            			this.connection.close();
            		}
            	} catch (SQLException e) {
            		e.printStackTrace();
            	}
            }
        }
        return (T) obj;
    }
    
    public <T extends Model> T create(String... list) {
        return (T) this.create(entity, list);
    }

    /**
     * Returns the id of the last inserted row.
     * @return int
     */
    public int getLastInsertedID() {
        int id = 0;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
        	if (this.connection == null || !this.connection.isValid(10)) {
				this.connection = ConnectionFactory.getConnection();
			}
            String sql = "";
            Properties databaseSettings = new Properties();
            FileInputStream fileInputStream = new FileInputStream(JediORMEngine.JEDI_PROPERTIES_PATH);
            databaseSettings.load(fileInputStream);
            String databaseEngine = databaseSettings.getProperty("database.engine");
            if (databaseEngine != null) {
                if (databaseEngine.trim().equalsIgnoreCase("mysql") || 
            		databaseEngine.trim().equalsIgnoreCase("postgresql") || 
            		databaseEngine.trim().equalsIgnoreCase("h2")) {
                    sql = String.format("SELECT id FROM %s ORDER BY id DESC LIMIT 1", tableName);
                } else if (databaseEngine.trim().equalsIgnoreCase("oracle")) {
                    sql = String.format("SELECT MAX(id) AS id FROM %s", tableName);
                } else {
                	
                }
            } else {
                return id;
            }
            statement = this.connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt("id");
            }            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try {
        		if (resultSet != null) {
        			resultSet.close();
        		}        		
        		if (statement != null) {
        			statement.close();
        		}        		
        		if (this.connection != null && this.autoCloseConnection) {
        			this.connection.close();
        		}
        	} catch (SQLException e) {
        		e.printStackTrace();
        	}
        }   
        return id;
    }

    /**
     * @param conditions
     * @return int
     */
    public int count(String... conditions) {
        int rows = 0;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
        	if (this.connection == null || !this.connection.isValid(10)) {
				this.connection = ConnectionFactory.getConnection();
			}
            String sql = String.format("SELECT COUNT(id) AS \"rows\" FROM %s", tableName);
            if (conditions != null) {
                String where = "WHERE";
                for (int i = 0; i < conditions.length; i++) {
                	conditions[i] = conditions[i] == null ? "" : conditions[i].trim();
                    if (!conditions[i].isEmpty()) {
                    	if (conditions[i].equalsIgnoreCase("AND")) {
                    		conditions[i] = "AND";
                    	}
                    	if (conditions[i].equalsIgnoreCase("OR")) {
                    		conditions[i] = "OR";
                    	}
                        /* Changes the name of the field to reflect the name 
                         * pattern of the table columns.
                         */
                    	String fieldName = conditions[i].substring(0, conditions[i].lastIndexOf("="));
                    	String fieldValue = conditions[i].substring(conditions[i].lastIndexOf("="));
                        if (conditions[i].contains("=")) {
                            conditions[i] = String.format("%s%s", TableUtil.getColumnName(fieldName), fieldValue);
                        }
                        // Adds a blank space between the field's name and value.
                        conditions[i] = conditions[i].replace("=", " = ");
                        // Replaces % by \%
                        conditions[i] = conditions[i].replace("%", "\\%");
                        // Adds a blank space between the values separated by comma character.
                        conditions[i] = conditions[i].replace(",", ", ");
                        // Checks if the current pair contains __startswith, __contains or __endswith.
                        if (conditions[i].indexOf("__startswith") > -1 || conditions[i].indexOf("__contains") > -1
                                || conditions[i].indexOf("__endswith") > -1) {
                            // Creates the LIKE SQL statement.
                            if (conditions[i].indexOf("__startswith") > -1) {
                                conditions[i] = conditions[i].replace("__startswith = ", " LIKE ");
                                // Replaces 'value' by 'value%'.
                                conditions[i] = conditions[i].substring(0, conditions[i].lastIndexOf("\'"));
                                conditions[i] = conditions[i] + "%\'";
                            } else if (conditions[i].indexOf("__contains") > -1) {
                                conditions[i] = conditions[i].replace("__contains = ", " LIKE ");
                                // Replaces 'value' by '%value%'.
                                conditions[i] = conditions[i].replaceFirst("\'", "\'%");
                                conditions[i] = conditions[i].substring(0, conditions[i].lastIndexOf("\'"));
                                conditions[i] = conditions[i] + "%\'";
                            } else if (conditions[i].indexOf("__endswith") > -1) {
                                conditions[i] = conditions[i].replace("__endswith = ", " LIKE ");
                                // Replaces 'value' by '%value'.
                                conditions[i] = conditions[i].replaceFirst("\'", "\'%");
                            }
                        }
                        if (conditions[i].indexOf("__in") > -1) {
                            // Creates the IN SQL statement.
                            conditions[i] = conditions[i].replace("__in = ", " IN ");
                            // Replaces the [] characters by ().
                            conditions[i] = conditions[i].replace("[", "(");
                            conditions[i] = conditions[i].replace("]", ")");
                        } else
                        if (conditions[i].indexOf("__range") > -1) {
                            // Creates the BETWEEN SQL statement.
                            conditions[i] = conditions[i].replace("__range = ", " BETWEEN ");
                            // Removes the [ or ] characters.
                            conditions[i] = conditions[i].replace("[", "");
                            conditions[i] = conditions[i].replace("]", "");
                            // Replaces the comma character by AND.
                            conditions[i] = conditions[i].replace(", ", " AND ");
                        }
                        if (conditions[i].indexOf("__lt") > -1) {
                            conditions[i] = conditions[i].replace("__lt = ", " < ");
                        }
                        if (conditions[i].indexOf("__lte") > -1) {
                            conditions[i] = conditions[i].replace("__lte = ", " <= ");
                        }
                        if (conditions[i].indexOf("__gt") > -1) {
                            conditions[i] = conditions[i].replace("__gt = ", " > ");
                        }
                        if (conditions[i].indexOf("__gte") > -1) {
                            conditions[i] = conditions[i].replace("__gte = ", " >= ");
                        }
                        if (conditions[i].indexOf("__exact") > -1) {
                            conditions[i] = conditions[i].replace("__exact = ", " = ");
                        }
                        where += " " + conditions[i] + " AND";
                        where = where.replace(" AND OR AND", " OR");
                        where = where.replace(" AND AND AND", " AND");
                    }
                }
                if (where.indexOf(" AND") > -1) {
                    where = where.substring(0, where.lastIndexOf("AND"));
                    sql = String.format("%s %s", sql, where);
                }
            }
            if (JediORMEngine.DEBUG) {
            	System.out.println(sql + ";\n");
            }
            statement = this.connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                rows = resultSet.getInt("rows");
            }            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try {
        		if (resultSet != null) {
        			resultSet.close();
        		}        		
        		if (statement != null) {
        			statement.close();
        		}        		
        		if (this.connection != null && this.autoCloseConnection) {
        			this.connection.close();
        		}
        	} catch (SQLException e) {
        		e.printStackTrace();
        	}
        }
        return rows;
    }

    /**
     * @return int
     */
    public int count() {
        return count("");
    }

    /**
     * @param fields
     * @return
     */
    public <T extends Model> QuerySet<T> exclude(String... fields) {
        QuerySet<T> querySet = new QuerySet<T>();
        querySet.setEntity(this.entity);
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        OneToOneField oneToOneFieldAnnotation = null;
        ForeignKeyField foreignKeyFieldAnnotation = null;
        ManyToManyField manyToManyFieldAnnotation = null;
        if (fields != null) {
            try {
            	if (this.connection == null || !this.connection.isValid(10)) {
    				this.connection = ConnectionFactory.getConnection();
    			}
                String sql = String.format("SELECT * FROM %s WHERE", tableName);
                String where = "";
                // Iterates through the pairs field=value.
                for (int i = 0; i < fields.length; i++) {
                	fields[i] = fields[i] == null ? "" : fields[i].trim();
                	if (fields[i].isEmpty()) {
                		continue;
                	}
                	if (fields[i].equalsIgnoreCase("AND")) {
                		fields[i] = "AND";
                	}
                	if (fields[i].equalsIgnoreCase("OR")) {
                		fields[i] = "OR";
                	}
                    // Creates the column name.
                    if (fields[i].contains("=")) {
                        fields[i] = String.format(
                            "%s%s",
                            TableUtil.getColumnName(fields[i].substring(0, fields[i].lastIndexOf("="))),
                            fields[i].substring(fields[i].lastIndexOf("="))
                        );
                    }
                    // Adds a blank space between the field name and value.
                    fields[i] = fields[i].replace("=", " = ");
                    // Replaces % by \%
                    fields[i] = fields[i].replace("%", "\\%");
                    // Adds a blank space between the values separated by comma character.
                    fields[i] = fields[i].replace(",", ", ");
                    // Checks if the current pair contains __startswith, __contains ou __endswith.
                    if (fields[i].indexOf("__startswith") > -1 || 
                		fields[i].indexOf("__contains") > -1 || 
                		fields[i].indexOf("__endswith") > -1) {
                        // Creates a LIKE SQL statement.
                        if (fields[i].indexOf("__startswith") > -1) {
                            fields[i] = fields[i].replace("__startswith = ", " LIKE ");
                            // Replaces 'value' by 'value%'.
                            fields[i] = fields[i].substring(0, fields[i].lastIndexOf("\'"));
                            fields[i] = fields[i] + "%\'";
                        } else if (fields[i].indexOf("__contains") > -1) {
                            fields[i] = fields[i].replace("__contains = ", " LIKE ");
                            // Replaces 'value' by '%value%'.
                            fields[i] = fields[i].replaceFirst("\'", "\'%");
                            fields[i] = fields[i].substring(0, fields[i].lastIndexOf("\'"));
                            fields[i] = fields[i] + "%\'";
                        } else if (fields[i].indexOf("__endswith") > -1) {
                            fields[i] = fields[i].replace("__endswith = ", " LIKE ");
                            // Replaces 'value' by '%value'.
                            fields[i] = fields[i].replaceFirst("\'", "\'%");
                        }
                    }
                    if (fields[i].indexOf("__in") > -1) {
                        // Creates a IN SQL statement.
                        fields[i] = fields[i].replace("__in = ", " IN ");
                        // Replaces [] characters by () characters.
                        fields[i] = fields[i].replace("[", "(");
                        fields[i] = fields[i].replace("]", ")");
                    }
                    if (fields[i].indexOf("__range") > -1) {
                        // Creates a BETWEEN SQL statement.
                        fields[i] = fields[i].replace("__range = ", " BETWEEN ");
                        // Removes the [ character.
                        fields[i] = fields[i].replace("[", "");
                        // Removes the ] character.
                        fields[i] = fields[i].replace("]", "");
                        // Substituindo o caracter , por AND.
                        fields[i] = fields[i].replace(", ", " AND ");
                    }
                    if (fields[i].indexOf("__lt") > -1) {
                        fields[i] = fields[i].replace("__lt = ", " < ");
                    }
                    if (fields[i].indexOf("__lte") > -1) {
                        fields[i] = fields[i].replace("__lte = ", " <= ");
                    }
                    if (fields[i].indexOf("__gt") > -1) {
                        fields[i] = fields[i].replace("__gt = ", " > ");
                    }
                    if (fields[i].indexOf("__gte") > -1) {
                        fields[i] = fields[i].replace("__gte = ", " >= ");
                    }
                    if (fields[i].indexOf("__exact") > -1) {
                        fields[i] = fields[i].replace("__exact = ", " = ");
                    }
                    where += fields[i] + " AND ";                    
                    where = where.replace(" AND OR AND", " OR");
                    where = where.replace(" AND AND AND", " AND");
                }
                where = where.substring(0, where.lastIndexOf("AND"));
                sql = String.format("%s NOT (%s)", sql, where);
                if (JediORMEngine.DEBUG) {
                	System.out.println(sql + ";\n");
                }
                statement = this.connection.prepareStatement(sql);
                resultSet = statement.executeQuery();
                if (!resultSet.next()) {
                    return querySet;
                }
                resultSet.beforeFirst();
                while (resultSet.next()) {
                    Object obj = entity.newInstance();
                    if (resultSet.getObject("id") != null) {
                        Field id = jedi.db.models.Model.class.getDeclaredField("id");
                        id.setAccessible(true);
                        if (this.connection.toString().startsWith("oracle")) {
                            id.set(obj, ((java.math.BigDecimal) resultSet.getObject(id.toString()
                                .substring(id.toString().lastIndexOf('.') + 1))).intValue());
                        } else {
                            id.set(obj, resultSet.getObject(id.toString()
                                .substring(id.toString().lastIndexOf('.') + 1)));
                        }
                    }
                    for (Field field : JediORMEngine.getAllFields(this.entity)) {
                        field.setAccessible(true);
                        if (!JediORMEngine.isJediField(field)) {
                        	continue;
                        }
                        if (field.getName().equals("serialVersionUID")) {
                            continue;
                        }
                        if (field.getName().equals("objects")) {
                            continue;
                        }
                        oneToOneFieldAnnotation = field.getAnnotation(OneToOneField.class);
                        foreignKeyFieldAnnotation = field.getAnnotation(ForeignKeyField.class);
                        manyToManyFieldAnnotation = field.getAnnotation(ManyToManyField.class);
                        FetchType fetchType = JediORMEngine.FETCH_TYPE;
                        Manager manager = null;
                        if (manyToManyFieldAnnotation != null) {
                        	fetchType = fetchType.equals(FetchType.NONE) ?
                        			manyToManyFieldAnnotation.fetch_type() : fetchType;
                        	if (fetchType.equals(FetchType.EAGER)) {
                        		Class superClazz = null;
                            	Class clazz = null;
                            	String model = manyToManyFieldAnnotation.model().getSimpleName();
                            	model = Model.class.getSimpleName().equals(model) ? "" : model;
                                if (model.isEmpty()) {
                                	ParameterizedType genericType = null;
                                	if (ParameterizedType.class.isAssignableFrom(field.getGenericType().getClass())) {
                                        genericType = (ParameterizedType) field.getGenericType();
                                        superClazz = ((Class) (genericType.getActualTypeArguments()[0])).getSuperclass();
                                        if (superClazz == Model.class) {
                                            clazz = (Class) genericType.getActualTypeArguments()[0];
                                            model = clazz.getSimpleName();
                                        }
                                    }                                            	
                                }
                                String references = manyToManyFieldAnnotation.references();
                                if (references == null || references.trim().isEmpty()) {
                                	if (clazz != null) {
                                		references = TableUtil.getTableName(clazz);
                                	} else {
                                		references = TableUtil.getTableName(model);
                                	}
                                }
                                String packageName = this.entity.getPackage().getName();
                                Class associatedModelClass = Class.forName(String.format("%s.%s", packageName, model));
                                manager = new Manager(associatedModelClass);
                                QuerySet querySetAssociatedModels = manager.raw(
                                    String.format(
                                        "SELECT * FROM %s WHERE id IN (SELECT %s_id FROM %s_%s WHERE %s_id = %d)", 
                                        references,
                                        model,
                                        tableName,
                                        references,
                                        TableUtil.getColumnName(obj.getClass()),
                                    ((Model) obj).getId()),
                                    associatedModelClass
                                );
                                field.set(obj, querySetAssociatedModels);
                        	} else {
                        		field.set(obj, null);
                        	}
                        } else if (oneToOneFieldAnnotation != null || foreignKeyFieldAnnotation != null) {
                        	if (oneToOneFieldAnnotation != null) {
                        		fetchType = fetchType.equals(FetchType.NONE) ?
                            			oneToOneFieldAnnotation.fetch_type() : fetchType;
                        	} else {
                        		fetchType = fetchType.equals(FetchType.NONE) ? 
                        				foreignKeyFieldAnnotation.fetch_type() : fetchType;
                        	}
                        	if (fetchType.equals(FetchType.EAGER)) {
                        		Class associatedModelClass = Class.forName(field.getType().getName());
                                manager = new Manager(associatedModelClass);
                                Model associatedModel = manager.get(
                            		String.format("id"), 
                            		resultSet.getObject(String.format("%s_id", TableUtil.getColumnName(field)))
                        		);
                                field.set(obj, associatedModel);
                        	} else {
                        		field.set(obj, null);
                        	}
                        } else {
                            if ((field.getType().getSimpleName().equals("int") || 
                        		field.getType().getSimpleName().equals("Integer")) && 
                        		this.connection.toString().startsWith("oracle")) {
                                if (resultSet.getObject(TableUtil.getColumnName(field)) == null) {
                                    field.set(obj, 0);
                                } else {
                                	String columnName = TableUtil.getColumnName(field);
                                	BigDecimal bigDecimal = (BigDecimal) resultSet.getObject(columnName); 
                                    field.set(obj, bigDecimal.intValue());
                                }
                            } else {
                            	String columnName = TableUtil.getColumnName(field);
                            	Object columnValue = resultSet.getObject(columnName);                        	
                            	if (columnValue instanceof Timestamp) {
                            		Timestamp timestamp = (Timestamp) columnValue;
                            		columnValue = new DateTime(timestamp.getTime());
                            	}                        	
                        		field.set(obj, columnValue);                                
                            }
                        }
                    }
                    T model = (T) obj;
                    if (model != null) {
                        model.isPersisted(true);
                    }
                    querySet.add(model);
                }                
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            	try {
            		if (resultSet != null) {
            			resultSet.close();
            		}        		
            		if (statement != null) {
            			statement.close();
            		}        		
            		if (this.connection != null && this.autoCloseConnection) {
            			this.connection.close();
            		}
            	} catch (SQLException e) {
            		e.printStackTrace();
            	}
            }
        }
        return querySet;
    }

    /**
     * @param sql
     * @return
     */
    public List<List<HashMap<String, Object>>> raw(String sql) {
        // Creates a list of list of maps.
        // The first list represents a set of rows.
        // The second list represents a row (set of columns).
        // The map represents a pair of key value (the column and its value).
        List<List<HashMap<String, Object>>> recordSet = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        sql = sql == null ? "" : sql.trim();
        if (!sql.isEmpty()) {
            try {            	
            	if (this.connection == null || !this.connection.isValid(10)) {
    				this.connection = ConnectionFactory.getConnection();
    			} 
            	if (JediORMEngine.DEBUG) {
            		if (!sql.equals("SELECT VERSION()")) {
            			System.out.println(sql + ";\n");
            		}
            	}
                // DQL - Data Query Language (SELECT).
                if (sql.startsWith("select") || sql.startsWith("SELECT")) {
                    // Returns a navigable ResultSet.
                	statement = this.connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    resultSet = statement.executeQuery();
                    ResultSetMetaData tableMetadata = null;
                    if (resultSet != null) {
                        tableMetadata = resultSet.getMetaData();
                        if (tableMetadata != null) {
                            // Deslocando o cursor até o último registro.
                            recordSet = new ArrayList<List<HashMap<String, Object>>>();
                            while (resultSet.next()) {
                                List<HashMap<String, Object>> tableRow = new ArrayList<HashMap<String, Object>>();
                                HashMap<String, Object> tableColumn = new HashMap<String, Object>();
                                for (int i = 1; i <= tableMetadata.getColumnCount(); i++) {
                                    tableColumn.put(
                                        tableMetadata.getColumnLabel(i), 
                                        resultSet.getObject(tableMetadata.getColumnLabel(i))
                                    );
                                }
                                tableRow.add(tableColumn);
                                recordSet.add(tableRow);
                            }                            
                        }
                    }
                } else {
                    // DML - Data Manipulation Language (INSERT, UPDATE or DELETE).
                    statement = this.connection.prepareStatement(sql);
                    statement.executeUpdate();
                    if (!this.connection.getAutoCommit()) {
                        this.connection.commit();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (!this.connection.getAutoCommit()) {
                        this.connection.rollback();
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            } finally {
            	try {
            		if (resultSet != null) {
            			resultSet.close();
            		}        		
            		if (statement != null) {
            			statement.close();
            		}        		
            		if (this.connection != null && this.autoCloseConnection) {
            			this.connection.close();
            		}
            	} catch (SQLException e) {
            		e.printStackTrace();
            	}
            }
        }
        return recordSet;
    }

    /**
     * @param sql
     * @param modelClass
     * @return
     */
    private <T extends Model> QuerySet<T> raw(String sql, Class<T> modelClass) {
        QuerySet<T> querySet = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        OneToOneField oneToOneFieldAnnotation = null;
        ForeignKeyField foreignKeyFieldAnnotation = null;
        ManyToManyField manyToManyFieldAnnotation = null;
        sql = sql == null ? "" : sql.trim();
        if (!sql.isEmpty()) {
            try {
            	if (this.connection == null || !this.connection.isValid(10)) {
    				this.connection = ConnectionFactory.getConnection();
    			}
            	if (JediORMEngine.DEBUG) {
            		System.out.println(sql + ";\n");
            	}
            	statement = this.connection.prepareStatement(sql);
                resultSet = statement.executeQuery();
                querySet = new QuerySet();
                querySet.setEntity(modelClass);
                while (resultSet.next()) {
                    T obj = modelClass.newInstance();
                    if (resultSet.getObject("id") != null) {
                        Field id = modelClass.getSuperclass().getDeclaredField("id");
                        id.setAccessible(true);
                        if (this.connection.toString().startsWith("oracle")) {
                            id.set(obj, ((java.math.BigDecimal) resultSet.getObject(id.getName())).intValue());
                        } else {
                            id.set(obj, resultSet.getObject(id.getName()));
                        }
                    }
                    if (obj != null) {
                        obj.isPersisted(true);
                    }
                    for (Field field : modelClass.getDeclaredFields()) {
                        field.setAccessible(true);
                        if (!JediORMEngine.isJediField(field)) {
                        	continue;
                        }
                        if (field.getName().equals("serialVersionUID")) {
                            continue;
                        }
                        if (field.getName().equalsIgnoreCase("objects")) {
                            continue;
                        }
                        oneToOneFieldAnnotation = field.getAnnotation(OneToOneField.class);
                        foreignKeyFieldAnnotation = field.getAnnotation(ForeignKeyField.class);
                        manyToManyFieldAnnotation = field.getAnnotation(ManyToManyField.class);
                        FetchType fetchType = JediORMEngine.FETCH_TYPE;
                        String tableName = TableUtil.getTableName(modelClass);
                        String model = null;
                        String references = null;
                        Manager manager = null;
                        if (oneToOneFieldAnnotation != null || foreignKeyFieldAnnotation != null) {
                        	if (oneToOneFieldAnnotation != null) {
                        		fetchType = fetchType.equals(FetchType.NONE) ?
                        				oneToOneFieldAnnotation.fetch_type() : fetchType;
                        	} else {
                        		fetchType = fetchType.equals(FetchType.NONE) ?
                        				foreignKeyFieldAnnotation.fetch_type() : fetchType;
                        	}
                        	if (fetchType.equals(FetchType.EAGER)) {
                        		Class associatedModelClass = Class.forName(field.getType().getName());
                                manager = new Manager(associatedModelClass);
                                Model associatedModel = manager.get(
                            		String.format("id"), 
                            		resultSet.getObject(String.format("%s_id", TableUtil.getColumnName(field)))
                        		);
                                field.set(obj, associatedModel);
                        	} else {
                        		field.set(obj, null);
                        	}
                        } else if (manyToManyFieldAnnotation != null) {
                        	fetchType = fetchType.equals(FetchType.NONE) ?
                    				manyToManyFieldAnnotation.fetch_type() : fetchType;
                    		if (fetchType.equals(FetchType.EAGER)) {
                    			Class superClazz = null;
                            	Class clazz = null;
                            	model = manyToManyFieldAnnotation.model().getSimpleName();
                            	model = Model.class.getSimpleName().equals(model) ? "" : model;
                                if (model.isEmpty()) {
                                	ParameterizedType genericType = null;
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
                                Class associatedModelClass = Class.forName(String.format("%s.%s", this.entity.getPackage().getName(), model));
                                manager = new Manager(associatedModelClass);
                                List<List<HashMap<String, Object>>> associatedModelsRecordSet = null;
                                associatedModelsRecordSet = manager.raw(
                                    String.format(
                                        "SELECT %s_id FROM %s_%s WHERE %s_id = %d", 
                                        TableUtil.getColumnName(model),
                                        tableName,
                                        TableUtil.getTableName(references),
                                        TableUtil.getColumnName(modelClass),
                                        obj.id()
                                    )
                                );
                                if (associatedModelsRecordSet != null) {
                                    String args = associatedModelsRecordSet.toString().toLowerCase();
                                    args = args.replace("[", "");
                                    args = args.replace("{", "");
                                    args = args.replace("]", "");
                                    args = args.replace("}", "");
                                    args = args.replace("=", "");
                                    args = args.replace(", ", ",");
                                    args = args.replace(String.format("%s_id", TableUtil.getColumnName(model)), "");
                                    args = String.format("id__in=[%s]", args);
                                    QuerySet qs = manager.filter(args);
                                    field.set(obj, qs);
                                } else {
                                	// TODO - Devolver lista vazia no lugar de null.
                                    field.set(obj, null);
                                }
                    		} else {
                            	// TODO - Devolver lista vazia no lugar de null.
                    			field.set(obj, null);
                    		}
                        } else {
                            // Configurando campos que não são instancias de model.
                            if ((field.getType().getSimpleName().equals("int") || 
                        		field.getType().getSimpleName().equals("Integer")) && 
                        		this.connection.toString().startsWith("oracle")) {
                                if (resultSet.getObject(TableUtil.getColumnName(field)) == null) {
                                    field.set(obj, 0);
                                } else {
                                	String columnName = TableUtil.getColumnName(field);
                                	BigDecimal bigDecimal = (BigDecimal) resultSet.getObject(columnName);
                                    field.set(obj,  bigDecimal.intValue());
                                }
                            } else {
                            	String columnName = TableUtil.getColumnName(field);
                            	Object columnValue = resultSet.getObject(columnName);                        	
                            	if (columnValue instanceof Timestamp) {
                            		Timestamp timestamp = (Timestamp) columnValue;
                            		columnValue = new DateTime(timestamp.getTime());
                            	}                        	
                        		field.set(obj, columnValue);                                
                            }
                        }
                        manager = null;
                    }
                    T model = (T) obj;
                    if (model != null) {
                        model.isPersisted(true);
                    }
                    querySet.add(model);
                }                
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            	try {
            		if (resultSet != null) {
            			resultSet.close();
            		}        		
            		if (statement != null) {
            			statement.close();
            		}        		
            		if (this.connection != null && this.autoCloseConnection) {
            			this.connection.close();
            		}
            	} catch (SQLException e) {
            		e.printStackTrace();
            	}
            }
        }
        return querySet;
    }
    
    // DEVE SER ALTERADO POIS PODE RETORNAR MAIS DE UM OBJETO.
    /**
     * @param field
     * @param value
     * @param modelClass
     * @return
     */
    private <T extends Model> T get(String field, Object value, Class<T> modelClass) {
        T model = null;
        String columnName = "";
        Object o = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        OneToOneField oneToOneFieldAnnotation = null;
        ForeignKeyField foreignKeyFieldAnnotation = null;
        ManyToManyField manyToManyFieldAnnotation = null;
        field = field == null ? "" : field.trim();
        if (!field.isEmpty()) {
            try {
            	if (this.connection == null || !this.connection.isValid(10)) {
    				this.connection = ConnectionFactory.getConnection();
    			}
                field = TableUtil.getColumnName(field);
                String sql = "SELECT * FROM";
                if (value != null) {
                    sql = String.format("%s %s WHERE %s = '%s'", sql, tableName, field, value.toString());
                } else {
                    sql = String.format("%s %s WHERE %s IS NULL", sql, tableName, field);
                }

                /* Se o tipo de dado do valor passado é numérico
                 * o apóstrofe é retirado.
                 */
                if (Integer.class.isInstance(value) || 
            		Float.class.isInstance(value) || 
            		Double.class.isInstance(value)) {
                    sql = sql.replaceAll("\'", "");
                }
                statement = this.connection.prepareStatement(sql);
                if (JediORMEngine.DEBUG) {
                	System.out.println(sql + ";\n");
                }
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    model = (T) entity.newInstance();
                    if (resultSet.getObject("id") != null) {
                    	Field id = jedi.db.models.Model.class.getDeclaredField("id");
                        id.setAccessible(true);
                        o = resultSet.getObject(id.getName());
                        /*
                         * Trata o tipo de dado BigDecimal retornado pelo Oracle.
                         * No MySQL e no PostgreSQL o tipo do dado é Integer.
                         */
                        if (this.connection.toString().startsWith("oracle")) {
                            id.set(model, ((java.math.BigDecimal) o).intValue());
                        } else {
                            id.set(model, o);
                        }
                    }
                    for (Field f : JediORMEngine.getAllFields(this.entity)) {
                        f.setAccessible(true);
                        if (!JediORMEngine.isJediField(f)) {
                        	continue;
                        }
                        if (f.getName().equals("serialVersionUID"))
                            continue;
                        if (f.getName().equalsIgnoreCase("objects"))
                            continue;
                        oneToOneFieldAnnotation = f.getAnnotation(OneToOneField.class);
                        foreignKeyFieldAnnotation = f.getAnnotation(ForeignKeyField.class);
                        manyToManyFieldAnnotation = f.getAnnotation(ManyToManyField.class);
                        FetchType fetchType = JediORMEngine.FETCH_TYPE;
                        Manager manager = null;
                        String referencedModel = null;
                        String referencedTable = null;
                        if (manyToManyFieldAnnotation != null) {
                        	fetchType = fetchType.equals(FetchType.NONE) ?
                        			manyToManyFieldAnnotation.fetch_type() : fetchType;
                        	if (fetchType.equals(FetchType.EAGER)) {
                        		if (!manyToManyFieldAnnotation.through()
                        				.getSimpleName().equals(Model.class.getSimpleName())) {
                        			continue;
                        		}
                            	Class superClazz = null;
                            	Class clazz = null;
                            	referencedModel = manyToManyFieldAnnotation.model().getSimpleName();
                            	referencedModel = Model.class.getSimpleName().equals(referencedModel) ? "" : referencedModel;
    	                        if (referencedModel.isEmpty()) {
    	                        	ParameterizedType genericType = null;
    	                        	if (ParameterizedType.class.isAssignableFrom(f.getGenericType().getClass())) {
    	                                genericType = (ParameterizedType) f.getGenericType();
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
    	                        		referencedTable = TableUtil.getTableName(referencedModel);
    	                        	}
    	                        }
                        		String packageName = this.entity.getPackage().getName();
                                Class associatedModelClass = Class.forName(String.format("%s.%s", packageName, referencedModel));
                                manager = new Manager(associatedModelClass);
                                QuerySet associatedModelsQuerySet = manager.raw(
                                    String.format(
                                        "SELECT * FROM %s WHERE id IN (SELECT %s_id FROM %s_%s WHERE %s_id = %d)",
                                        referencedTable,
                                        referencedModel.toLowerCase(),
                                        tableName,
                                        referencedTable,
                                        TableUtil.getColumnName(model.getClass()),
                                        model.getId()
                                    ),
                                    associatedModelClass
                                );
                                // Configurando o campo (atributo) com a referência
                                // para o queryset criado anteriormente.
                                f.set(model, associatedModelsQuerySet);
                        	} else {
                                f.set(model, null);
                        	}
                        } else if (foreignKeyFieldAnnotation != null) {
                    		fetchType = fetchType.equals(FetchType.NONE) ?
                    				foreignKeyFieldAnnotation.fetch_type() : fetchType;
                        	if (fetchType.equals(FetchType.EAGER)) {
                        		 // Caso seja recupera a classe do atributo.
                                Class associatedModelClass = Class.forName(f.getType().getName());
                                // Instanciando um model manager.
                                manager = new Manager(associatedModelClass);
                                // Chamando o método esse método (get)
                                // recursivamente.
                                Model associatedModel = manager.get(
                            		"id", 
                            		resultSet.getObject(String.format("%s_id", TableUtil.getColumnName(f)))
                        		);
                                // Atributo (campo) referenciando o modelo anotado
                                // como ForeignKeyField.
                                f.set(model, associatedModel);
                        	} else {
                        		f.set(model, null);
                        	}
                        } else if (oneToOneFieldAnnotation != null) {
                    		fetchType = fetchType.equals(FetchType.NONE) ?
                    				oneToOneFieldAnnotation.fetch_type() : fetchType;
                    		if (fetchType.equals(FetchType.EAGER)) {
	                            Class associatedModelClass = Class.forName(f.getType().getName());
	                            manager = new Manager(associatedModelClass);
	                            columnName = TableUtil.getColumnName(f.getType().getSimpleName());
	                            o = resultSet.getObject(String.format("%s_id", columnName));
	                            Model associatedModel = manager.get("id", o);  
	                            f.set(model, associatedModel);
                    		} else {
                    			f.set(model, null);
                    		}
                        } else {
                            // Configurando campos que não são instancias de
                            // Model.
                            if ((f.getType().getSimpleName().equals("int") || 
                        		f.getType().getSimpleName().equals("Integer")) && 
                        		this.connection.toString().startsWith("oracle")) {
                            	columnName = TableUtil.getColumnName(f.getName());
                            	o = resultSet.getObject(columnName);
                                if (o == null) {
                                    f.set(model, 0);
                                } else {
                                	columnName = TableUtil.getColumnName(f.getName());
                                	o = resultSet.getObject(columnName);
                                    f.set(model, ((BigDecimal) o).intValue());
                                }
                            } else {                            	
                            	columnName = TableUtil.getColumnName(f.getName());
                            	// Column´s value.
                            	o = resultSet.getObject(columnName);
                            	if (o instanceof Timestamp) { 
                            		o = new DateTime(((Timestamp) o).getTime());
                            	}
                                f.set(model, o);
                            }
                        }
                        manager = null;
                    }
                }
                if (model != null) {
                    model.isPersisted(true);
                }               
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            	try {
            		if (resultSet != null) {
            			resultSet.close();
            		}        		
            		if (statement != null) {
            			statement.close();
            		}        		
            		if (this.connection != null && this.autoCloseConnection) {
            			this.connection.close();
            		}
            	} catch (SQLException e) {
            		e.printStackTrace();
            	}
            }
        }
        return model;
    }
    
    /**
     * @param field
     * @param value
     * @return
     */
    public <T extends Model> T get(String field, Object value) {
        return (T) this.get(field, value, this.entity);
    }

    /**
     * @param field
     * @param modelClass
     * @return
     */
    private <T extends Model> T latest(String field, Class<T> modelClass) {
        T model = null;
        field = field == null ? "" : field.trim();
        if (!field.isEmpty()) {
        	try {
	        	if (this.connection == null || !this.connection.isValid(10)) {
					this.connection = ConnectionFactory.getConnection();
				}
	            // Renomeando o atributo para ficar no mesmo padrão do nome da
	            // coluna na tabela associada ao modelo.
	            field = TableUtil.getColumnName(field);
	            String sql = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT 1", tableName, field);
	            if (this.connection.toString().startsWith("oracle")) {
	                sql = String.format("SELECT * FROM %s WHERE ROWNUM < 2 ORDER BY %s DESC", tableName, field);
	            }
	            QuerySet querySet = this.raw(sql, entity);
	            if (querySet != null && !querySet.isEmpty()) {
	                model = (T) querySet.get(0);
	                if (model != null) {
	                    model.isPersisted(true);
	                }
	            }
        	} catch (Exception e) {
        		e.printStackTrace();
        	} finally {
            	try {        		
            		if (this.connection != null && this.autoCloseConnection) {
            			this.connection.close();
            		}
            	} catch (SQLException e) {
            		e.printStackTrace();
            	}
        	}
        }
        return model;
    }

    /**
     * @param field
     * @return
     */
    public <T extends Model> T latest(String field) {
        return (T) latest(field, entity);
    }

    /**
     * @return
     */
    public <T extends Model> T latest() {
        return (T) latest("id", entity);
    }

    /**
     * @param field
     * @param modelClass
     * @return
     */
    private <T extends Model> T earliest(String field, Class<T> modelClass) {
        T model = null;
        field = field == null ? "" : field.trim();
        if (!field.isEmpty()) {
        	try {
	        	if (this.connection == null || !this.connection.isValid(10)) {
					this.connection = ConnectionFactory.getConnection();
				}
	            String sql = String.format("SELECT * FROM %s ORDER BY %s ASC LIMIT 1", tableName, field);
	            if (this.connection.toString().startsWith("oracle")) {
	                sql = String.format("SELECT * FROM %s WHERE ROWNUM < 2 ORDER BY %s ASC", tableName, field);
	            }
	            QuerySet querySet = this.raw(sql, entity);
	            if (querySet != null && !querySet.isEmpty()) {
	                model = (T) querySet.get(0);
	                if (model != null) {
	                    model.isPersisted(true);
	                }
	            }
        	} catch (Exception e) {
        		e.printStackTrace();
        	} finally {
            	try {        		
            		if (this.connection != null && this.autoCloseConnection) {
            			this.connection.close();
            		}
            	} catch (SQLException e) {
            		e.printStackTrace();
            	}
        	}
        }
        return (T) model;
    }

    /**
     * @param field
     * @return
     */
    public <T extends Model> T earliest(String field) {
        return (T) earliest(field, entity);
    }

    /**
     * @return
     */
    public <T extends Model> T earliest() {
        return (T) earliest("id", entity);
    }

    /**
     * @param associatedModelClass
     * @param id
     * @return
     * 
     * Example: SELECT livros.* FROM livros, livros_autores WHERE livros.id =
     * livros_autores.livro_id AND livros_autores.autor_id = 1;
     */
    public <S extends Model, T extends Model> QuerySet<S> getSet(Class<T> associatedModelClass, int id) {
        QuerySet<S> querySet = null;
        if (associatedModelClass != null && associatedModelClass.getSuperclass().getName().equals("jedi.db.models.Model")) {
            String sql = "";        
            String tableNameAssociatedModel = TableUtil.getTableName(associatedModelClass);
            ForeignKeyField foreignKeyFieldAnnotation = null;
            for (Field field : JediORMEngine.getAllFields(this.entity)) {
                foreignKeyFieldAnnotation = field.getAnnotation(ForeignKeyField.class);
                if (foreignKeyFieldAnnotation != null) {
                	String model = foreignKeyFieldAnnotation.model().getSimpleName();
                	model = Model.class.getSimpleName().equals(model) ? "" : model;
                    if (model.isEmpty()) {
                    	model = field.getType().getName().replace(field.getType().getPackage().getName() + ".", "");
                    }
                	if (model.equals(associatedModelClass.getSimpleName())) {
                		querySet = this.filter(String.format("%s_id=%d", field.getName(), id));
                	}
                }
            }
            if (querySet == null) {
                sql = String.format(
                    "SELECT %s.* FROM %s, %s_%s WHERE %s.id = %s_%s.%s_id AND %s_%s.%s_id = %d",
                    tableName,
                    tableName,
                    tableName,
                    tableNameAssociatedModel,
                    tableName,
                    tableName,
                    tableNameAssociatedModel,
                    TableUtil.getColumnName(this.entity),
                    tableName,
                    tableNameAssociatedModel,
                    TableUtil.getColumnName(associatedModelClass),
                    id
                );
                querySet = this.raw(sql, this.entity);
            }
        }
        return (QuerySet<S>) querySet;
    }
    
    /**
     * Método que insere no banco de dados a lista de objetos fornecida de uma
     * maneira eficiente.
     * 
     * @param models
     */
    public void bulkCreate(List<Model> objects) {
    	
    }
    
    public <T extends Model> QuerySet<T> getOrCreate() {
    	return null;
    }
    
    private Object convertZeroDateToNull(Object date) {
    	if (date instanceof java.sql.Time) {
    		date = date.toString().equals("00:00:00") ? null : date;
    	} else if (date instanceof java.sql.Date) {
    		date = date.toString().equals("0000-00-00 00:00:00") ? null : date;
    	} else if (date instanceof java.sql.Timestamp) {
    		date = date.toString().equals("00000000000000") ? null : date;
    	}
    	return date;
    }
}