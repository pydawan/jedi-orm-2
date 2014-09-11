/***********************************************************************
 * @(#)JediORMEngine.java
 *
 * Version: 1.0
 *
 * Date: 2014/09/04
 *
 * Copyright (c) 2014 Thiago Alexandre Martins Monteiro.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *    Thiago Alexandre Martins Monteiro - initial API and implementation
*************************************************************************/

package jedi.db.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import jedi.app.JediApp;
import jedi.app.JediAppLoader;

import jedi.db.connection.ConnectionFactory;

import jedi.db.enums.CascadeType;
import jedi.db.enums.FetchType;
import jedi.db.enums.Models;

import jedi.db.models.BooleanField;
import jedi.db.models.CharField;
import jedi.db.models.DateField;
import jedi.db.models.DateTimeField;
import jedi.db.models.DecimalField;
import jedi.db.models.EmailField;
import jedi.db.models.FloatField;
import jedi.db.models.ForeignKeyField;
import jedi.db.models.IPAddressField;
import jedi.db.models.IntegerField;
import jedi.db.models.Manager;
import jedi.db.models.ManyToManyField;
import jedi.db.models.Model;
import jedi.db.models.OneToOneField;
import jedi.db.models.Table;
import jedi.db.models.TextField;
import jedi.db.models.TimeField;
import jedi.db.models.URLField;
import jedi.db.util.ColumnUtil;
import jedi.db.util.TableUtil;

import jedi.generator.JediCodeGenerator;

/**
 * <p>
 * <strong>Jedi's Object-Relational Mapping Engine.</strong>
 * </p>
 *
 * @author Thiago Alexandre Martins Monteiro
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class JediORMEngine {
    // Application's root directory.
    public static String APP_ROOT_DIR = System.getProperty("user.dir");
    // Application's source code directory.
    public static String APP_SRC_DIR = String.format(
        "%s%s%s", APP_ROOT_DIR, File.separator, "src"
    );
    public static String APP_LIBS_DIR = String.format(
        "%s%s%s", APP_ROOT_DIR, File.separator, "lib"
    );
    public static String JEDI_PROPERTIES_PATH = String.format(
        "%s%s%s", JediORMEngine.APP_ROOT_DIR, File.separator, "jedi.properties"
    );
    public static String TABLE = "";
    public static boolean DATABASE_ENVIROMENTS = false;
    public static String DATABASE_ENGINE = "mysql";
    public static String DATABASE_HOST = "localhost";
    public static String DATABASE_PORT = "3306";
    public static String DATABASE_NAME = "";
    public static String DATABASE_USER = "root";
    public static String DATABASE_PASSWORD = "";
    public static String DATABASE_CHARSET = "utf8";
    public static String SQL_COLUMN_IDENTATION = "    ";
    public static boolean DATABASE_AUTO_COMMIT = true;
    public static boolean DATABASE_AUTO_CLOSE_CONNECTION = false;
    public static boolean DATABASE_AUTO_INCREMENT = true;
    public static boolean FOREIGN_KEY_CHECKS = true;
    public static boolean WEB_APP = false;
    public static boolean DEBUG = true;
    public static boolean JEDI_PROPERTIES_LOADED = false;
    public static int DATABASE_ACQUIRE_INCREMENT = 3;
    public static int DATABASE_INITIAL_POOL_SIZE = 3;
    public static int DATABASE_MAX_POOL_SIZE = 15;
    public static int DATABASE_MIN_POOL_SIZE = 3;
    public static int DATABASE_MAX_STATEMENTS = 30;
    public static FetchType FETCH_TYPE = FetchType.EAGER;
    public static CascadeType CASCADE_TYPE = CascadeType.ALL;
    public static List<String> INSTALLED_APPS = new ArrayList();
    // List of maps with table names and models.
    public static List<String> SQL_ASSOCIATION_TABLES = new ArrayList();
    public static List<String> SQL_CREATE_TABLES;
    public static Map<String, List<String>> SQL_FOREIGN_KEYS = new HashMap();
    public static Map<String, List<String>> SQL_INDEXES = new HashMap();
    public static Map<String, List<String>> MYSQL_AUTO_NOW = new HashMap();
    public static Map<String, List<String>> MYSQL_AUTO_NOW_ADD = new HashMap();
    public static Map<String, List<String>> SQL_COMMENTS = new HashMap();
    public static FileInputStream JEDI_PROPERTIES_FILE;
    public static Properties JEDI_PROPERTIES;
    public static final Class[] JEDI_FIELD_ANNOTATION_CLASSES = {
        CharField.class,
        EmailField.class,
        URLField.class,
        IPAddressField.class,
        TextField.class,
        IntegerField.class,
        DecimalField.class,
        FloatField.class,
        BooleanField.class,
        DateField.class,
        TimeField.class,
        DateTimeField.class,
        OneToOneField.class,
        ForeignKeyField.class,
        ManyToManyField.class
    };
    private static Manager SQLManager = new Manager(Model.class, false);
    private static Integer MYSQL_VERSION = null;

    static {
        JediORMEngine.loadJediProperties();
        if (JediORMEngine.DATABASE_USER.equals("root")
                && JediORMEngine.DATABASE_ENVIROMENTS) {
            JediORMEngine.createDBEnviroments();
        }
        MYSQL_VERSION = getMySQLVersion();
    }

    // Framework's model directory.
    public static final String JEDI_DB_MODELS = String.format(
        "jedi%sdb%smodels",
        File.separator,
        File.separator,
        File.separator
    );
    // Application's models that were read and that will be mapped in tables.
    public static List<String> READED_APP_MODELS = new ArrayList();
    // Generated tables.
    public static List<String> GENERATED_TABLES = new ArrayList();

    /**
     * Método que converte um PATH do sistema de arquivos em
     * CLASSPATH do Java.
     * @param path caminho do sistema de arquivos.
     * @return CLASSPATH Java.
     */
    public static String convertFilePathToClassPath(String path) {
        path = path == null ? "" : path.trim();
        if (!path.isEmpty()) {
            path = path.replace(
                String.format(
                    "%s%s", JediORMEngine.APP_SRC_DIR, File.separator
                ), ""
            );
            path = path.replace(File.separator, ".").replace(".java", "");
        }
        return path;
    }

    /**
     * @param path path
     * @return string
     */
    public static String convertClassPathToFilePath(String path) {
        path = path == null ? "" : path.trim();
        if (!path.isEmpty()) {
            path = path.replace(".", File.separator);
            path = String.format(
                "%s%s%s.java",
                JediORMEngine.APP_SRC_DIR,
                File.separator, path
            );
        }
        return path;
    }

    /**
     * Converts model objects of a application in database tables.
     *
     * @author Thiago Alexandre Martins Monteiro
     * @param path path.
     */
    public static void syncdb(String path) {
        SQL_CREATE_TABLES = new ArrayList();
        getSQLOfInstalledApps();
        // Get SQL from Jedi Models
        getSQL(path);
        /* If the create table statements exists then probably exists
         * foreign keys, indexes, etc.
         * All the other things are related to it.
         */
        if (!SQL_CREATE_TABLES.isEmpty()) {
            for (String sql : SQL_CREATE_TABLES) {
                JediORMEngine.execute(sql);
            }
            for (String sql : SQL_ASSOCIATION_TABLES) {
                JediORMEngine.execute(sql);
            }
            for (Entry<String, List<String>> sql : SQL_FOREIGN_KEYS.entrySet()) {
                for (String fk : sql.getValue()) {
                    JediORMEngine.execute(fk);
                }
            }
            for (Entry<String, List<String>> sql : SQL_INDEXES.entrySet()) {
                for (String ix : sql.getValue()) {
                    JediORMEngine.execute(ix);
                }
            }
            JediCodeGenerator.generateCode(path);
        }
    }

    /**
     * syncdb.
     */
    public static void syncdb() {
        syncdb(JediORMEngine.APP_SRC_DIR);
    }

    /**
     * @return model files
     */
    public static List<File> getModelFiles() {
        return getModelFiles(JediORMEngine.APP_SRC_DIR);
    }

    /**
     * @param path path
     * @return path
     */
    public static List<File> getModelFiles(String path) {
        List<File> modelFiles = new ArrayList<>();
        // Reference to the application's directory.
        File appDir = new File(path);
        // Checks if the appDir exists.
        if (appDir != null && appDir.exists()) {
            // Checks if appDir is a directory and not a
            // file (both are referenced as a File object).
            if (appDir.isDirectory()) {
                // Gets the appDir content.
                File[] appDirContents = appDir.listFiles();
                // Search by app/models subdirectory in the appDir content.
                for (File appDirContent : appDirContents) {
                    // Gets all directories named as "models"
                    // (except the models directory of the Framework).
                    if (!appDirContent.getAbsolutePath().contains(JediORMEngine.JEDI_DB_MODELS)
                        && appDirContent.getAbsolutePath().endsWith("models")) {
                        // Gets all files in app/models.
                        File[] appModelsFiles = appDirContent.listFiles();
                        for (File appModelFile : appModelsFiles) {
                            if (isJediModelFile(appModelFile)) {
                                modelFiles.add(appModelFile);
                            }
                        }
                    } else {
                        modelFiles.addAll(getModelFiles(appDirContent.getAbsolutePath()));
                    }
                }
            }
        }
        return modelFiles;
    }

    /**
     * @param clazz clazz
     * @return clazz
     */
    public static File getModelFile(Class clazz) {
        File file = null;
        if (clazz != null) {
            String path = JediORMEngine.convertClassPathToFilePath(clazz.getName());
            file = new File(path);
        }
        return file;
    }

    /**
     * @param file file
     * @return file
     */
    public static Class<? extends jedi.db.models.Model> getModel(File file) {
        Class clazz = null;
        if (JediORMEngine.isJediModelFile(file)) {
            String className = JediORMEngine.convertFilePathToClassPath(file.getAbsolutePath());
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
            }
        }
        return clazz;
    }

    /**
     * @return models
     */
    public static List<Class<? extends jedi.db.models.Model>> getModels() {
        return getModels(JediORMEngine.APP_SRC_DIR);
    }

    /**
     * @param path path
     * @return models
     */
    public static List<Class<? extends jedi.db.models.Model>> getModels(String path) {
        List<Class<? extends jedi.db.models.Model>> models = new ArrayList<>();
        path = path == null ? "" : path.trim();
        if (!path.isEmpty() && new File(path).exists()) {
            Class modelClass = null;
            for (File file : JediORMEngine.getModelFiles(path)) {
                modelClass = JediORMEngine.getModel(file);
                if (!isJediModel(modelClass)) {
                    continue;
                }
                models.add(modelClass);
             }
        }
        return models;
    }

    /**
     * @param path path
     */
    private static void getSQL(String path) {
        for (Class clazz : JediORMEngine.getModels()) {
            if (SQL_CREATE_TABLES != null) {
                SQL_CREATE_TABLES.add(getSQL(clazz));
                String tableName = TableUtil.getTableName(clazz);
                if (!GENERATED_TABLES.contains(tableName)) {
                    GENERATED_TABLES.add(tableName);
                }
            }
         }
    }

    /**
     * @param tables tables
     */
     public static void droptables(String...tables) {
        setForeignKeyChecks();
        if (tables != null && tables.length > 0) {
            if (JediORMEngine.DEBUG) {
                System.out.println("");
            }
            for (String table : tables) {
                JediORMEngine.execute(String.format("DROP TABLE %s", table));
            }
        }
    }

    /**
     * @param tables tables
     */
    public static void droptables(List<String> tables) {
        setForeignKeyChecks();
        if (tables != null && tables.size() > 0) {
            if (JediORMEngine.DEBUG) {
                System.out.println("");
            }
            for (String table : tables) {
                JediORMEngine.execute(String.format("DROP TABLE %s", table));
            }
        }
    }

    /**
     * drop tables.
     */
    public static void droptables() {
        droptables(JediORMEngine.GENERATED_TABLES);
    }

    /**
     * flush.
     */
    public static void flush() {
        droptables();
        System.out.println("");
        syncdb(JediORMEngine.APP_SRC_DIR);
    }

    /**
     * SQL clear.
     */
    public static void sqlclear() {
        List<String> tables = JediORMEngine.GENERATED_TABLES;
        if (tables != null && tables.size() > 0) {
            System.out.println();
            for (String table : tables) {
                String statement = String.format("DROP TABLE %s;", table);
                System.out.println(statement);
            }
        }
    }

    /**
     * @param sql SQL
     */
    public static void execute(String sql) {
        if (sql != null && !sql.trim().isEmpty()) {
            SQLManager.raw(sql);
        }
    }

    /**
     * 
     */
    private static void setForeignKeyChecks() {
    	if (JediORMEngine.DATABASE_ENGINE == null) {
            FileInputStream databaseSettingsFile;
			try {
				databaseSettingsFile = new FileInputStream(JEDI_PROPERTIES_PATH);
	            JEDI_PROPERTIES.load(databaseSettingsFile);
	            JediORMEngine.DATABASE_ENGINE = JEDI_PROPERTIES.getProperty("database.engine");
	            JediORMEngine.DATABASE_ENGINE = JediORMEngine.DATABASE_ENGINE.trim();
	            databaseSettingsFile.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}    	
    	if (JediORMEngine.DATABASE_ENGINE.equalsIgnoreCase("mysql")) {
    		if (JEDI_PROPERTIES.containsKey("database.foreign_key.checks")) {
            	String foreignkeyChecks = JEDI_PROPERTIES.getProperty("database.foreign_key.checks");
                FOREIGN_KEY_CHECKS = new Boolean(foreignkeyChecks.toLowerCase()); 
            }    		
    		if (JediORMEngine.FOREIGN_KEY_CHECKS) {
        		// Enable    		
    			JediORMEngine.execute("SET FOREIGN_KEY_CHECKS = 1");
        	} else {
        		// Disable
        		JediORMEngine.execute("SET FOREIGN_KEY_CHECKS = 0");
        	}
        }
    }    
    
    /**
     * Cria as tabelas no banco de dados correspondentes
     * aos modelos das aplicações instaladas.
     */
    private static void getSQLOfInstalledApps() {
    	JediAppLoader.setDir(APP_LIBS_DIR);
    	for (String installedApp : INSTALLED_APPS) {
    		JediApp app = JediAppLoader.get(installedApp);
    		List<Class<?>> classes = app.getClasses().get("models");
    		for (Class<?> clazz : classes) {
    			Class<? extends Model> modelClass = (Class<? extends Model>) clazz;				
				if (SQL_CREATE_TABLES != null) {
					String tableName = TableUtil.getTableName(modelClass);
					tableName = String.format("%s_%s", app.getDBTable(), tableName);
					String sql = JediORMEngine.getSQL(app, modelClass);					
					SQL_CREATE_TABLES.add(sql);
					if (!GENERATED_TABLES.contains(tableName)) {
						GENERATED_TABLES.add(tableName);
					}
				}
    		}
		}
    }
    
    private static int getMySQLVersion() {
    	if (DATABASE_ENGINE.equals("mysql")) {
    		if (MYSQL_VERSION == null) {
	            Manager manager = new Manager(Model.class);
	            String[] array = manager.raw("SELECT VERSION()")
	                .get(0)
	                .get(0)
	                .get("VERSION()")
	                .toString()
	                .split("\\.");
	            MYSQL_VERSION = Integer.parseInt(String.format("%s%s", array[0], array[1]));
    		}
        } else {
        	MYSQL_VERSION = null;
        }
    	return MYSQL_VERSION;
    }
    
    private static String getComment(Annotation annotation) {
    	String co = null;
    	Class ac = annotation == null ? null : annotation.annotationType();
    	if (isJediFieldAnnotation(ac)) {
    		if (ac == CharField.class) {
    			co = ((CharField) annotation).comment();
    		} else if (ac == EmailField.class) {
    			co = ((EmailField) annotation).comment();
    		} else if (ac == URLField.class) {
    			co = ((URLField) annotation).comment();
    		} else if (ac == IPAddressField.class) {
    			co = ((IPAddressField) annotation).comment();
    		} else if (ac == TextField.class) {
    			co = ((TextField) annotation).comment();
    		} else if (ac == IntegerField.class) {
    			co = ((IntegerField) annotation).comment();
    		} else if (ac == DecimalField.class) {
    			co = ((DecimalField) annotation).comment();
    		} else if (ac == FloatField.class) {
    			co = ((FloatField) annotation).comment();
    		} else if (ac == BooleanField.class) {
    			co = ((BooleanField) annotation).comment();
    		} else if (ac == DateField.class) {
    			co = ((DateField) annotation).comment();
    		} else if (ac == TimeField.class) {
    			co = ((TimeField) annotation).comment();
    		} else if (ac == DateTimeField.class) {
    			co = ((DateTimeField) annotation).comment();
    		} else if (ac == OneToOneField.class) {
    			co = ((OneToOneField) annotation).comment();
    		} else if (ac == ForeignKeyField.class) {
    			co = ((ForeignKeyField) annotation).comment();
    		} else if (ac == ManyToManyField.class) {
    			co = ((ManyToManyField) annotation).comment();
    		} else {
    			
    		}
    	}
    	return co == null ? "" : co.trim();
    }
    
    public static String getDefaultValue(Annotation annotation) {
    	String dv = null;
    	Class ac = annotation == null ? null : annotation.annotationType();
    	if (isJediFieldAnnotation(ac)) {
    		if (ac == CharField.class) {
    			dv = ((CharField) annotation).default_value();
    			dv = dv == null || dv.equals("\\0") ? null : dv;
    		} else if (ac == EmailField.class) {
    			dv = ((EmailField) annotation).default_value();
    			dv = dv == null || dv.equals("\\0") ? null : dv;
    		} else if (ac == URLField.class) {
    			dv = ((URLField) annotation).default_value();
    			dv = dv == null || dv.equals("\\0") ? null : dv;
    		} else if (ac == IPAddressField.class) {
    			dv = ((IPAddressField) annotation).default_value();
    			dv = dv == null || dv.equals("\\0") ? null : dv;
    		} else if (ac == TextField.class) {
    			dv = ((TextField) annotation).default_value();
    			dv = dv == null || dv.equals("\\0") ? null : dv;
    		} else if (ac == IntegerField.class) {
    			dv = ((IntegerField) annotation).default_value();
    			dv = dv == null || dv.trim().isEmpty() ? "" : dv.trim();
    		} else if (ac == DecimalField.class) {
    			dv = ((DecimalField) annotation).default_value();
    			dv = dv == null || dv.isEmpty() ? "" : dv.trim();
    		} else if (ac == FloatField.class) {
    			dv = ((FloatField) annotation).default_value();
    			dv = dv == null || dv.isEmpty() ? "" : dv.trim();
    		} else if (ac == BooleanField.class) {
    			dv = ((BooleanField) annotation).default_value() + "";
    			dv = dv.equals("false") ? "0" : "1";
    		} else if (ac == DateField.class) {
    			dv = ((DateField) annotation).default_value();
	            dv = dv == null ? "" : dv.trim();
	            dv = dv.toUpperCase();
	            dv = dv.replace("'NULL'", "NULL");
    		} else if (ac == TimeField.class) {
    			dv = ((TimeField) annotation).default_value();
	            dv = dv == null ? "" : dv.trim();
	            dv = dv.toUpperCase();
	            dv = dv.replace("'NULL'", "NULL");
    		} else if (ac == DateTimeField.class) {
    			dv = ((DateTimeField) annotation).default_value();
	            dv = dv == null ? "" : dv.trim();
	            dv = dv.toUpperCase();
	            dv = dv.replace("'NULL'", "NULL");
    		} else {
    			
    		}
    	}
		return dv;
    }
    
    private static String getSQLFormatter(Annotation annotation, String databaseEngine) {
    	String formatter = null;
    	Class ac = annotation == null ? null : annotation.annotationType();
    	if (databaseEngine != null && !databaseEngine.isEmpty()) {
    		if (databaseEngine.equalsIgnoreCase("mysql")) {
    			if (ac == CharField.class || 
    					ac == EmailField.class || 
    					ac == URLField.class || 
    					ac == IPAddressField.class) {
        			formatter = "%s VARCHAR(%d)%s%s%s%s";
        		} else if (ac == TextField.class) { 
        			formatter = "%s TEXT%s%s%s%s";
        		} else if (ac == IntegerField.class) {
        			formatter = "%s INT(%d)%s%s%s%s";
        		} else if (ac == DecimalField.class) {
        			formatter = "%s DECIMAL(%d,%d)%s%s%s%s";
        		} else if (ac == FloatField.class) {
        			formatter = "%s FLOAT(%d,%d)%s%s%s%s";
        		} else if (ac == BooleanField.class) {
        			formatter = "%s TINYINT(1)%s%s%s%s";
        		} else if (ac == DateField.class) {
        			formatter = "%s DATE%s%s%s%s";
        		} else if (ac == TimeField.class) {
        			formatter = "%s TIME%s%s%s%s";
        		} else if (ac == DateTimeField.class) {
        			formatter = "%s DATETIME%s%s%s%s%s";
        		} else {
        			
        		}
    		}
    	}
    	return formatter == null ? "" : formatter.trim();
    }
    
    public static void loadJediProperties() {
    	try {
    		File file = new File(JEDI_PROPERTIES_PATH);
    		if (!file.exists()) {
    			JEDI_PROPERTIES_PATH = String.format(
					"%s%s%s%s%s",
					APP_ROOT_DIR,
					File.separator,
					"config",
					File.separator,
					"jedi.properties"
				);
    			file = new File(JEDI_PROPERTIES_PATH);
    			if (!file.exists()) {
    				try {
    					throw new Exception("O arquivo jedi.properties não foi encontrado!");
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    			}
    		}
    		JEDI_PROPERTIES_FILE = new FileInputStream(file);
    		JEDI_PROPERTIES = new Properties();
			JEDI_PROPERTIES.load(JEDI_PROPERTIES_FILE);
			for (Object o : JEDI_PROPERTIES.keySet()) {				
				String key = o.toString().toLowerCase().trim();
				String value = JEDI_PROPERTIES.getProperty(key);
				value = value == null ? "" : value.trim().toLowerCase();
				if (key.equals("database.enviroments") || key.equals("db.enviroments")) {
					DATABASE_ENVIROMENTS = new Boolean(value.toLowerCase());
				} else if (key.equals("database.engine") || key.equals("db.engine")) {
					DATABASE_ENGINE = value;
				} else if (key.equals("database.host") || key.equals("db.host")) {
					DATABASE_HOST = value;
				} else if (key.equals("database.port") || key.equals("db.port")) {
					DATABASE_PORT = value;
				} else if (key.equals("database.name") || key.equals("db.name")) {
					DATABASE_NAME = value;
				} else if (key.equals("database.user") || key.equals("db.user")) {
					DATABASE_USER = value;
				} else if (key.equals("database.password") || key.equals("db.password")) {
					DATABASE_PASSWORD = value;
				} else if (key.equals("database.auto_commit") || key.equals("db.commit.auto")) {
	                DATABASE_AUTO_COMMIT = new Boolean(value.toLowerCase()); 
	            } else if (key.equals("database.auto_close_connection") || key.equals("db.close.auto")) { 
	            	DATABASE_AUTO_CLOSE_CONNECTION = new Boolean(value.toLowerCase());
	            } else if (key.equals("database.acquire_increment") || key.equals("db.pool.inc")) { 
	            	DATABASE_ACQUIRE_INCREMENT = Integer.parseInt(value);
	            } else if (key.equals("database.initial_pool_size") || key.equals("db.pool")) { 
	            	DATABASE_INITIAL_POOL_SIZE = Integer.parseInt(value);
	            } else if (key.equals("database.max_pool_size") || key.equals("db.pool.max")) {
	            	DATABASE_MAX_POOL_SIZE = Integer.parseInt(value);
	            } else if (key.equals("database.min_pool_size") || key.equals("db.pool.min")) {
	            	DATABASE_MIN_POOL_SIZE = Integer.parseInt(value);
	            } else if (key.equals("database.max_statements") || key.equals("db.max_statements")) { 
	            	DATABASE_MAX_STATEMENTS = Integer.parseInt(value);
	            } else if (key.equals("database.foreign_key.checks") || 
	            		key.equals("db.foreign_key.checks") || key.equals("db.fk.checks")) {
	                FOREIGN_KEY_CHECKS = new Boolean(value.toLowerCase()); 
	            } else if (key.equals("database.debug")) {
					DEBUG = new Boolean(value.toLowerCase());
				} else if (key.equals("database.fetch_type") || key.equals("db.fetch_type")) {
					if (value.equals("none")) {
						FETCH_TYPE = FetchType.NONE;
					} else if (value.equals("eager")) {
						FETCH_TYPE = FetchType.EAGER;
					} else if (value.equals("lazy")) {
						FETCH_TYPE = FetchType.LAZY;
					}
				} else if (key.equals("database.cascade_type") || key.equals("db.cascade_type")) { 
					if (value.equals("none")) {
						CASCADE_TYPE = CascadeType.NONE;
					} else if (value.equals("insert")) {
						CASCADE_TYPE = CascadeType.INSERT;
					} else if (value.equals("update")) {
						CASCADE_TYPE = CascadeType.UPDATE;
					} else if (value.equals("save")) {
						CASCADE_TYPE = CascadeType.SAVE;
					} else if (value.equals("delete")) {
						CASCADE_TYPE = CascadeType.DELETE;
					} else if (value.equals("all")) {
						CASCADE_TYPE = CascadeType.ALL;
					}
				} else if (key.equals("database.charset")) {
					DATABASE_CHARSET = value;
				} else if (key.startsWith("installed.app")) {
					if (!INSTALLED_APPS.contains(value)) {
						INSTALLED_APPS.add(value);
					}
				}
			}
			JEDI_PROPERTIES_LOADED = true;
			JEDI_PROPERTIES_FILE.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}    	
    }
    
    public static List<Field> getFields(Class<? extends jedi.db.models.Model> c, Class a) {
    	List<Field> fields = new ArrayList();
    	if (c != null) {
    		try {
    			if (!isJediFieldAnnotation(a)) {
    				throw new Exception("Não foi informada uma classe de anotação válida para o tipo de field.");
    			} else {
    				for (Field field : getAllFields(c)) {
		    			field.setAccessible(true);		    			
		    			Annotation annotation = field.getAnnotation(a);
		    			if (annotation != null) {
		    				fields.add(field);
		    			}
		    		}
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	return fields;
    }
    
    public static List<Field> getFields(Class<? extends jedi.db.models.Model> c) {
    	List<Field> fields = new ArrayList();
    	if (isJediModel(c)) {
    		for (Field field : getAllFields(c)) {
    			if (isJediField(field)) {
    				fields.add(field);
    			}
    		}
    	}
    	return fields;
    }
    
    public static Field getField(String name, Class<? extends jedi.db.models.Model> c) {
    	if (name != null && !name.trim().isEmpty() && c != null) {
	    	for (Field f : getAllFields(c)) {
	    		if (f.getName().equals(name)) {
	    			return f;
	    		}
	    	}
    	}
    	return null;
    }
    
    public static List<Field> getCharFields(Class<? extends jedi.db.models.Model> c) {    	    	
    	return getFields(c, CharField.class);
    }
    
    public static List<Field> getEmailFields(Class<? extends jedi.db.models.Model> c) {
    	return getFields(c, EmailField.class);
    }
    
    public static List<Field> getURLFields(Class<? extends jedi.db.models.Model> c) {
    	return getFields(c, URLField.class);
    }
    
    public static List<Field> getIPAddressFields(Class<? extends jedi.db.models.Model> c) {
    	return getFields(c, IPAddressField.class);
    }
    
    public static List<Field> getTextFields(Class<? extends jedi.db.models.Model> c) {
    	return getFields(c, TextField.class);
    }
    
    public static List<Field> getIntegerFields(Class<? extends jedi.db.models.Model> c) {
    	return getFields(c, IntegerField.class);
    }
    
    public static List<Field> getDecimalFields(Class<? extends jedi.db.models.Model> c) {
    	return getFields(c, DecimalField.class);
    }
    
    public static List<Field> getFloatFields(Class<? extends jedi.db.models.Model> c) {
    	return getFields(c, FloatField.class);
    }
    
    public static List<Field> getBooleanFields(Class<? extends jedi.db.models.Model> c) {
    	return getFields(c, BooleanField.class);
    }
    
    public static List<Field> getDateFields(Class<? extends jedi.db.models.Model> c) {
    	return getFields(c, DateField.class);
    }
    
    public static List<Field> getTimeFields(Class<? extends jedi.db.models.Model> c) {
    	return getFields(c, TimeField.class);
    }
    
    public static List<Field> getDateTimeFields(Class<? extends jedi.db.models.Model> c) {
    	return getFields(c, DateTimeField.class);
    }
    
    public static List<Field> getOneToOneFields(Class<? extends jedi.db.models.Model> c) {
    	return getFields(c, OneToOneField.class);
    }
    
    public static List<Field> getForeignKeyFields(Class<? extends jedi.db.models.Model> c) {
    	return getFields(c, ForeignKeyField.class);
    }
    
    public static List<Field> getManyToManyFields(Class<? extends jedi.db.models.Model> c) {
    	return getFields(c, ManyToManyField.class);
    }
    
    private static String getPrimaryKeySQL(Field field) {
    	String sql = "";
    	if (field != null && Model.class.isAssignableFrom(field.getDeclaringClass())) {
    		String format = "";
    		String columnName = "";
	    	if (DATABASE_ENGINE.equals("mysql")) {
	    		format = "%s INT NOT NULL PRIMARY KEY AUTO_INCREMENT"; 
	    		columnName = ColumnUtil.getColumnName(field);
	    	}
	    	sql = String.format(format, columnName);
    	}    	
    	return sql;
    }
    
    public static String getPrimaryKeySQL() {
    	String sql = "";
    	try {
			sql = getPrimaryKeySQL(jedi.db.models.Model.class.getDeclaredField("id"));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}    	
    	return sql;
    }
    
    public static String getCharFieldSQL(Field field) {
    	String sql = "";
    	if (field != null && Model.class.isAssignableFrom(field.getDeclaringClass())) {
	    	CharField charFieldAnnotation = field.getAnnotation(CharField.class);
	    	if (charFieldAnnotation != null) {
	    		String comment = getComment(charFieldAnnotation);
	    		String defaultValue = getDefaultValue(charFieldAnnotation);
        		sql = String.format(
    				getSQLFormatter(charFieldAnnotation, DATABASE_ENGINE),
    				ColumnUtil.getColumnName(field),
    				charFieldAnnotation.max_length(),
    				charFieldAnnotation.required() ? " NOT NULL" : "",
					defaultValue == null ? "" : String.format(" DEFAULT '%s'", defaultValue),
					charFieldAnnotation.unique() ? " UNIQUE" : "",
					DATABASE_ENGINE.equals("mysql") && !comment.isEmpty() ? 
							String.format(" COMMENT '%s'", comment) : ""
				);
        		if (DATABASE_ENGINE.equals("postgresql") || DATABASE_ENGINE.equals("oracle")) {
        			String tableName = TableUtil.getTableName(field.getDeclaringClass());
        			List<String> comments = new ArrayList();
        			comment = String.format(
        					"COMMENT ON COLUMN %s.%s IS '%s';\n\n",
        					tableName, 
        					TableUtil.getColumnName(field), 
        					comment
					);
        			comments.add(comment);
    				SQL_COMMENTS.put(tableName, comments);
        		}
	    	}
    	}
    	return sql;
    }
    
    public static String getEmailFieldSQL(Field field) {
    	String sql = "";
    	if (field != null && Model.class.isAssignableFrom(field.getDeclaringClass())) {
    		EmailField emailFieldAnnotation = field.getAnnotation(EmailField.class);
    		if (emailFieldAnnotation != null) {
    			String comment = getComment(emailFieldAnnotation);
    			String defaultValue = getDefaultValue(emailFieldAnnotation);
    			sql = String.format(
					getSQLFormatter(emailFieldAnnotation, DATABASE_ENGINE),
					ColumnUtil.getColumnName(field),
					emailFieldAnnotation.max_length(),
	                emailFieldAnnotation.required() ? " NOT NULL" : "",
	                defaultValue == null ? "" : String.format(" DEFAULT '%s'", defaultValue),
	                emailFieldAnnotation.unique() ? " UNIQUE" : "",
	                DATABASE_ENGINE.equals("mysql") && !comment.isEmpty() ? 
	                		String.format(" COMMENT '%s'", comment) : ""
	            );
    		}
    	}
    	return sql;
    }
    
    public static String getURLFieldSQL(Field field) {
    	String sql = "";
    	if (field != null && Model.class.isAssignableFrom(field.getDeclaringClass())) {
    		URLField urlFieldAnnotation = field.getAnnotation(URLField.class);
    		if (urlFieldAnnotation != null) {
            	String comment = getComment(urlFieldAnnotation);
            	String defaultValue = getDefaultValue(urlFieldAnnotation);
            	sql = String.format(
            		getSQLFormatter(urlFieldAnnotation, DATABASE_ENGINE),
                    ColumnUtil.getColumnName(field),
                    urlFieldAnnotation.max_length(),
                    urlFieldAnnotation.required() ? " NOT NULL" : "",
                    defaultValue == null ? "" : String.format(" DEFAULT '%s'", defaultValue),
                    urlFieldAnnotation.unique() ? " UNIQUE" : "",
            		DATABASE_ENGINE.equals("mysql") && !comment.isEmpty() ? 
            				String.format(" COMMENT '%s'", comment) : ""
                );
    		}
    	}
    	return sql;
    }
    
    public static String getIPAddressFieldSQL(Field field) {
    	String sql = "";
    	if (field != null && Model.class.isAssignableFrom(field.getDeclaringClass())) {
    		IPAddressField ipAddressFieldAnnotation = field.getAnnotation(IPAddressField.class);
    		if (ipAddressFieldAnnotation != null) {
            	String comment = getComment(ipAddressFieldAnnotation);
            	String defaultValue = getDefaultValue(ipAddressFieldAnnotation);
            	sql = String.format(
                    getSQLFormatter(ipAddressFieldAnnotation, DATABASE_ENGINE),
                    ColumnUtil.getColumnName(field),
                    ipAddressFieldAnnotation.max_length(),
                    ipAddressFieldAnnotation.required() ? " NOT NULL" : "",
                    defaultValue == null ? "" : String.format(" DEFAULT '%s'", defaultValue),
                    ipAddressFieldAnnotation.unique() ? " UNIQUE" : "",
            		DATABASE_ENGINE.equals("mysql") && !comment.isEmpty() ? 
            				String.format(" COMMENT '%s'", comment) : ""
                );
    		}
		}
    	return sql;
    }
    
    public static String getTextFieldSQL(Field field) {
    	String sql = "";
    	if (field != null && Model.class.isAssignableFrom(field.getDeclaringClass())) {
    		TextField textFieldAnnotation = field.getAnnotation(TextField.class);
    		if (textFieldAnnotation != null) {
                String comment = getComment(textFieldAnnotation);
                String defaultValue = getDefaultValue(textFieldAnnotation);
            	sql = String.format(
                    getSQLFormatter(textFieldAnnotation, DATABASE_ENGINE),
                    ColumnUtil.getColumnName(field),
                    textFieldAnnotation.required() ? " NOT NULL" : "",
                    defaultValue == null ? "" : String.format(" DEFAULT '%s'", defaultValue),
                    textFieldAnnotation.unique() ? " UNIQUE" : "",
            		DATABASE_ENGINE.equals("mysql") && !comment.isEmpty() ? 
            				String.format(" COMMENT '%s'", comment) : ""
                );
    		}
    	}
    	return sql;
    }
    
    public static String getIntegerFieldSQL(Field field) {
    	String sql = "";
    	if (isJediField(field)) {
    		IntegerField integerFieldAnnotation = field.getAnnotation(IntegerField.class);
    		if (integerFieldAnnotation != null) {
            	String comment = getComment(integerFieldAnnotation);
            	String defaultValue = getDefaultValue(integerFieldAnnotation);
            	sql = String.format(
                    getSQLFormatter(integerFieldAnnotation, DATABASE_ENGINE),
                    ColumnUtil.getColumnName(field),
                    integerFieldAnnotation.size(),
                    integerFieldAnnotation.required() ? " NOT NULL" : "",
                    defaultValue.isEmpty() ? defaultValue : String.format(" DEFAULT %s", defaultValue),
                    integerFieldAnnotation.unique() ? " UNIQUE" : "",
            		DATABASE_ENGINE.equals("mysql") && !comment.isEmpty() ? 
            				String.format(" COMMENT '%s'", comment) : ""
                );
    		}
    	}
    	return sql;
    }
    
    public static String getDecimalFieldSQL(Field field) {
    	String sql = "";
    	if (field != null && Model.class.isAssignableFrom(field.getDeclaringClass())) {
    		DecimalField decimalFieldAnnotation = field.getAnnotation(DecimalField.class);
    		if (decimalFieldAnnotation != null) {
            	String comment = getComment(decimalFieldAnnotation);
            	String defaultValue = getDefaultValue(decimalFieldAnnotation);
            	sql = String.format(
                    getSQLFormatter(decimalFieldAnnotation, DATABASE_ENGINE),
                    ColumnUtil.getColumnName(field),
                    decimalFieldAnnotation.scale(),
                    decimalFieldAnnotation.precision(),
                    decimalFieldAnnotation.required() ? " NOT NULL" : "",
                    defaultValue.isEmpty() ? defaultValue : String.format(" DEFAULT %s", defaultValue),
                    decimalFieldAnnotation.unique() ? " UNIQUE" : "",
            		DATABASE_ENGINE.equals("mysql") && !comment.isEmpty() ? 
            				String.format(" COMMENT '%s'", comment) : ""
                );
    		}
    	}
    	return sql;
    }
    
    public static String getFloatFieldSQL(Field field) {
    	String sql = "";
    	if (field != null && Model.class.isAssignableFrom(field.getDeclaringClass())) {
    		FloatField floatFieldAnnotation = field.getAnnotation(FloatField.class);
    		if (floatFieldAnnotation != null) {
            	String comment = getComment(floatFieldAnnotation);
            	String defaultValue = getDefaultValue(floatFieldAnnotation);
            	sql = String.format(
                    getSQLFormatter(floatFieldAnnotation, DATABASE_ENGINE),
                    ColumnUtil.getColumnName(field),
                    floatFieldAnnotation.scale(),
                    floatFieldAnnotation.precision(),
                    floatFieldAnnotation.required() ? " NOT NULL" : "",
                    defaultValue.isEmpty() ? defaultValue : String.format(" DEFAULT %s", defaultValue),
                    floatFieldAnnotation.unique() ? " UNIQUE" : "", 
            		DATABASE_ENGINE.equals("mysql") && !comment.isEmpty() ? 
            				String.format(" COMMENT '%s'", comment) : ""
                );
    		}
    	}
    	return sql;
    }
    
    public static String getBooleanFieldSQL(Field field) {
    	String sql = "";
    	if (field != null && Model.class.isAssignableFrom(field.getDeclaringClass())) {    		
    		BooleanField booleanFieldAnnotation = field.getAnnotation(BooleanField.class);
    		if (booleanFieldAnnotation != null) {
    			String comment = getComment(booleanFieldAnnotation);
    			String defaultValue = getDefaultValue(booleanFieldAnnotation);
    			sql = String.format(
	    			getSQLFormatter(booleanFieldAnnotation, DATABASE_ENGINE),
	    			ColumnUtil.getColumnName(field),
	    			booleanFieldAnnotation.required() == true ? " NOT NULL" : "",
	    			booleanFieldAnnotation.unique() == true ? " UNIQUE" : "",
					defaultValue.isEmpty() ? defaultValue : (defaultValue == "true" ? 
							" DEFAULT 1" : " DEFAULT 0"),
					DATABASE_ENGINE.equals("mysql") && !comment.isEmpty() ? 
							String.format(" COMMENT '%s'", comment) : ""
				);
    		}
    	}
    	return sql;
    }
    
    public static String getDateFieldSQL(Field field) {
    	String sql = "";
    	if (field != null && Model.class.isAssignableFrom(field.getDeclaringClass())) {
    		DateField dateFieldAnnotation = field.getAnnotation(DateField.class);
    		if (dateFieldAnnotation != null) {
    			Class c = field.getDeclaringClass();
                String fieldName = ColumnUtil.getColumnName(field);
                String tableName = TableUtil.getTableName(c);
                String comment = getComment(dateFieldAnnotation);
	            String defaultValue = getDefaultValue(dateFieldAnnotation);
	            sql = String.format(
                    getSQLFormatter(dateFieldAnnotation, DATABASE_ENGINE),
                    fieldName,
                    dateFieldAnnotation.required() ? " NOT NULL" : "",
                    defaultValue.isEmpty() ? defaultValue : String.format(" DEFAULT '%s'", defaultValue), 
                    dateFieldAnnotation.unique() ? " UNIQUE" : "",
                    DATABASE_ENGINE.equals("mysql") && !comment.isEmpty() ? 
                    		String.format(" COMMENT '%s'", comment) : ""
                );
	            if (MYSQL_VERSION != null) {	            	
                    if (dateFieldAnnotation.auto_now_add()) {
                        if (MYSQL_AUTO_NOW_ADD.get(tableName) == null) {
                            MYSQL_AUTO_NOW_ADD.put(tableName, new ArrayList<String>());
                            MYSQL_AUTO_NOW_ADD.get(tableName).add(fieldName);
                        } else {
                            MYSQL_AUTO_NOW_ADD.get(tableName).add(fieldName);
                        }
                    }
                    if (dateFieldAnnotation.auto_now()) {
                        if (MYSQL_AUTO_NOW.get(tableName) == null) {
                        	MYSQL_AUTO_NOW.put(tableName, new ArrayList<String>());
                        	MYSQL_AUTO_NOW.get(tableName).add(fieldName);
                        } else {
                        	MYSQL_AUTO_NOW.get(tableName).add(fieldName);
                        }
                    }
	            }
    		}
    	}
    	return sql;
    }
    
    public static String getTimeFieldSQL(Field field) {
    	String sql = "";
    	if (field != null && Model.class.isAssignableFrom(field.getDeclaringClass())) {
    		TimeField timeFieldAnnotation = field.getAnnotation(TimeField.class);
    		if (timeFieldAnnotation != null) {
    			Class c = field.getDeclaringClass();
            	String fieldName = ColumnUtil.getColumnName(field);
            	String tableName = TableUtil.getTableName(c);
	            String defaultValue = getDefaultValue(timeFieldAnnotation);
	            String comment = getComment(timeFieldAnnotation);
	            sql = String.format(
            		getSQLFormatter(timeFieldAnnotation, DATABASE_ENGINE),
                    fieldName,
                    timeFieldAnnotation.required() ? " NOT NULL" : "",
                    defaultValue.isEmpty() ? defaultValue : String.format(" DEFAULT '%s'", defaultValue), 
                    timeFieldAnnotation.unique() ? " UNIQUE" : "",
                    DATABASE_ENGINE.equals("mysql") && !comment.isEmpty() ? 
                    		String.format(" COMMENT '%s'", comment) : ""
                );
	            if (MYSQL_VERSION != null) {
	                if (timeFieldAnnotation.auto_now_add()) {
                        if (MYSQL_AUTO_NOW_ADD.get(tableName) == null) {
                        	MYSQL_AUTO_NOW_ADD.put(tableName, new ArrayList<String>());
                        	MYSQL_AUTO_NOW_ADD.get(tableName).add(fieldName);
                        } else {
                        	MYSQL_AUTO_NOW_ADD.get(tableName).add(fieldName);
                        }
                    }
                    if (timeFieldAnnotation.auto_now()) {
                        if (MYSQL_AUTO_NOW.get(tableName) == null) {
                        	MYSQL_AUTO_NOW.put(tableName, new ArrayList<String>());
                        	MYSQL_AUTO_NOW.get(tableName).add(fieldName);
                        } else {
                        	MYSQL_AUTO_NOW.get(tableName).add(fieldName);
                        }
                    }
	            }
    		}
    	}
    	return sql;
    }
    
    public static String getDateTimeFieldSQL(Field field) {
    	String sql = "";
		if (field != null && Model.class.isAssignableFrom(field.getDeclaringClass())) {
			DateTimeField dateTimeFieldAnnotation = field.getAnnotation(DateTimeField.class);
			if (dateTimeFieldAnnotation != null) {
				Class c = field.getDeclaringClass();
	        	String fieldName = ColumnUtil.getColumnName(field);
	        	String tableName = TableUtil.getColumnName(c);
	            String defaultValue = getDefaultValue(dateTimeFieldAnnotation);
	            String comment = getComment(dateTimeFieldAnnotation);
	            sql = String.format(
            		getSQLFormatter(dateTimeFieldAnnotation, DATABASE_ENGINE),
                    fieldName,
                    dateTimeFieldAnnotation.required() ? " NOT NULL" : "",
            		defaultValue.isEmpty() ? defaultValue : String.format(" DEFAULT '%s'", defaultValue),
                    dateTimeFieldAnnotation.auto_now() && MYSQL_VERSION >= 56 ? 
                    		String.format(" ON UPDATE CURRENT_TIMESTAMP") : "", 
                    dateTimeFieldAnnotation.unique() ? " UNIQUE" : "",
                    DATABASE_ENGINE.equals("mysql") && !comment.isEmpty() ? 
                    		String.format(" COMMENT '%s'", comment) : ""
                );
	            if (dateTimeFieldAnnotation.auto_now_add() || dateTimeFieldAnnotation.auto_now()) { 
	                if (MYSQL_VERSION != null) {
	                    if (MYSQL_VERSION >= 56 && dateTimeFieldAnnotation.auto_now_add()) {
	                    	defaultValue = " DEFAULT CURRENT_TIMESTAMP";
	                    } else {
	                    	if (dateTimeFieldAnnotation.auto_now_add()) {
	                            if (MYSQL_AUTO_NOW_ADD.get(tableName) == null) {
	                            	MYSQL_AUTO_NOW_ADD.put(tableName, new ArrayList<String>());
	                            	MYSQL_AUTO_NOW_ADD.get(tableName).add(fieldName);
	                            } else {
	                            	MYSQL_AUTO_NOW_ADD.get(tableName).add(fieldName);
	                            }
	                        }
	                        if (dateTimeFieldAnnotation.auto_now()) {
	                            if (MYSQL_AUTO_NOW.get(tableName) == null) {
	                            	MYSQL_AUTO_NOW.put(tableName, new ArrayList<String>());
	                            	MYSQL_AUTO_NOW.get(tableName).add(fieldName);
	                            } else {
	                            	MYSQL_AUTO_NOW.get(tableName).add(fieldName);
	                            }
	                        }
	                    }
	                }           
	            }
			}
		}
    	return sql;
    }
    
    public static String getOneToOneFieldSQL(Field field) {
    	String sql = "";
    	if (field != null && Model.class.isAssignableFrom(field.getDeclaringClass())) {
    		OneToOneField oneToOneFieldAnnotation = field.getAnnotation(OneToOneField.class);
    		if (oneToOneFieldAnnotation != null) {
	    		Class modelClass = field.getDeclaringClass();	    		
	    		String fieldName = ColumnUtil.getColumnName(field);
	    		String columnName = oneToOneFieldAnnotation.column_name();
	    		String tableName = TableUtil.getTableName(modelClass);
	    		String referencedColumn = oneToOneFieldAnnotation.referenced_column();
	    		String fk = "";
	    		String comment = getComment(oneToOneFieldAnnotation);
	    		columnName = columnName == null ? "" : columnName;
	    		referencedColumn = referencedColumn == null ? "" : referencedColumn;
	            if (columnName.isEmpty()) {
	                fieldName = String.format("%s_id", fieldName);
	            } else {
	                fieldName = ColumnUtil.getColumnName(columnName);
	            }
	            if (referencedColumn.isEmpty()) {
	                referencedColumn = "id";
	            } else {
	                referencedColumn = ColumnUtil.getColumnName(referencedColumn);
	            }
	            sql = String.format(
	                "%s INT NOT NULL UNIQUE%s",
	                fieldName,
	                DATABASE_ENGINE.equals("mysql") && !comment.isEmpty() ? 
	                		String.format(" COMMENT '%s'", comment) : ""
	            );
	            String onDeleteString = "";
	            if (oneToOneFieldAnnotation.on_delete().equals(Models.PROTECT)) {
	                onDeleteString = " ON DELETE RESTRICT";
	            } else if (oneToOneFieldAnnotation.on_delete().equals(Models.SET_NULL)) {
	                onDeleteString = " ON DELETE SET NULL";
	            } else if (oneToOneFieldAnnotation.on_delete().equals(Models.CASCADE)) {
	                onDeleteString = " ON DELETE CASCADE";
	            } else if (oneToOneFieldAnnotation.on_delete().equals(Models.SET_DEFAULT)) {
	                onDeleteString = " ON DELETE SET DEFAULT";
	            }
	            String onUpdateString = " ON UPDATE";
	            if (oneToOneFieldAnnotation.on_update().equals(Models.PROTECT)) {
	                onUpdateString = " ON UPDATE RESTRICT";
	            } else if (oneToOneFieldAnnotation.on_update().equals(Models.SET_NULL)) {
	                onUpdateString = " ON UPDATE SET NULL";
	            } else if (oneToOneFieldAnnotation.on_update().equals(Models.CASCADE)) {
	                onUpdateString = " ON UPDATE CASCADE";                                                
	                if (DATABASE_ENGINE != null && DATABASE_ENGINE.equalsIgnoreCase("oracle")) {
	                    onUpdateString = "";
	                }
	            } else if (oneToOneFieldAnnotation.on_update().equals(Models.SET_DEFAULT)) {
	                onUpdateString = " ON UPDATE SET DEFAULT";
	            }
	            String model = oneToOneFieldAnnotation.model().getSimpleName();	            
	            model = Model.class.getSimpleName().equals(model) ? "" : model;
	            if (model.isEmpty()) {
	            	String packageName = field.getType().getPackage().getName();
	            	
	            	model = field.getType().getName();
	            	model = model.replace(packageName + ".", "");
	            }
	            String constraintName = oneToOneFieldAnnotation.constraint_name();
	            constraintName = constraintName == null ? "" : constraintName.trim();
	            if (constraintName.isEmpty()) {
	            	constraintName = String.format("fk_%s_%s", tableName, 
	            			TableUtil.getTableName(field.getType()));
	            }
	            String references = oneToOneFieldAnnotation.references();
	            references = references == null ? "" : references.trim();
	            if (references.isEmpty()) {
	            	references = TableUtil.getTableName(field.getType());
	            } else {
	            	references = TableUtil.getColumnName(references);
	            }
	            if (SQL_FOREIGN_KEYS.get(modelClass.toString()) == null) {
	            	SQL_FOREIGN_KEYS.put(modelClass.toString(), new ArrayList<String>());	            	
	            }
	            if (MYSQL_VERSION != null) {
	            	StringBuilder formatter = new StringBuilder();	            	
	            	formatter.append("ALTER TABLE %s ADD CONSTRAINT %s ");
	            	formatter.append("FOREIGN KEY(%s) REFERENCES %s(%s)%s%s");
	                fk = String.format(
	                    formatter.toString(),
	                    tableName,
	                    constraintName,
	                    fieldName,
	                    references,
	                    referencedColumn,
	                    onDeleteString,
	                    onUpdateString
	                );
	            }
	            List<String> fks = SQL_FOREIGN_KEYS.get(modelClass.toString());
	            if (!fks.contains(fk)) {
	            	fks.add(fk);
	            }
    		}
    	}    	
    	return sql;
    }
    
    public static String getForeignKeyFieldSQL(Field field) {
    	String sql = "";
    	if (field != null && Model.class.isAssignableFrom(field.getDeclaringClass())) {
    		ForeignKeyField foreignKeyFieldAnnotation = field.getAnnotation(ForeignKeyField.class);
    		if (foreignKeyFieldAnnotation != null) {
	    		Class modelClass = field.getDeclaringClass();
	    		String fieldName = ColumnUtil.getColumnName(field);
	    		String columnName = foreignKeyFieldAnnotation.column_name();
	            String referencedColumn = foreignKeyFieldAnnotation.referenced_column();
	            String comment = getComment(foreignKeyFieldAnnotation);
	    		String tableName = TableUtil.getTableName(modelClass);
	            String fk = "";
	            columnName = columnName == null ? "" : columnName;
	            referencedColumn = referencedColumn == null ? "" : referencedColumn;
	            if (columnName.isEmpty()) {
	                fieldName = String.format("%s_id", fieldName);
	            } else {
	                fieldName = ColumnUtil.getColumnName(columnName);
	            }
	            if (referencedColumn.isEmpty()) {
	                referencedColumn = "id";
	            } else {
	                referencedColumn = ColumnUtil.getColumnName(referencedColumn);
	            }
	            sql = String.format("%s INT NOT NULL%s", fieldName, 
	            		DATABASE_ENGINE.equals("mysql") && !comment.isEmpty() ? 
	            				String.format(" COMMENT '%s'", comment) : "");
	            String onDeleteString = "";
	            if (foreignKeyFieldAnnotation.on_delete().equals(Models.PROTECT)) {
	                onDeleteString = " ON DELETE RESTRICT";
	            } else if (foreignKeyFieldAnnotation.on_delete().equals(Models.SET_NULL)) {
	                onDeleteString = " ON DELETE SET NULL";
	            } else if (foreignKeyFieldAnnotation.on_delete().equals(Models.CASCADE)) {
	                onDeleteString = " ON DELETE CASCADE";
	            } else if (foreignKeyFieldAnnotation.on_delete().equals(Models.SET_DEFAULT)) {
	                onDeleteString = " ON DELETE SET DEFAULT";
	            }
	            String onUpdateString = " ON UPDATE";
	            if (foreignKeyFieldAnnotation.on_update().equals(Models.PROTECT)) {
	                onUpdateString = " ON UPDATE RESTRICT";
	            } else if (foreignKeyFieldAnnotation.on_update().equals(Models.SET_NULL)) {
	                onUpdateString = " ON UPDATE SET NULL";
	            } else if (foreignKeyFieldAnnotation.on_update().equals(Models.CASCADE)) {
	                onUpdateString = " ON UPDATE CASCADE";                                                
	                if (DATABASE_ENGINE != null && DATABASE_ENGINE.equalsIgnoreCase("oracle")) {
	                    onUpdateString = "";
	                }
	            } else if (foreignKeyFieldAnnotation.on_update().equals(Models.SET_DEFAULT) ) {
	                onUpdateString = " ON UPDATE SET DEFAULT";
	            }
	            String model = foreignKeyFieldAnnotation.model().getSimpleName();
	            model = Model.class.getSimpleName().equals(model) ? "" : model;
	            if (model.isEmpty()) {
	            	String packageName = field.getType().getPackage().getName();
	            	String typeName = field.getType().getName();
	            	
	            	model = typeName.replace(packageName + ".", "");
	            }
	            String constraintName = foreignKeyFieldAnnotation.constraint_name();
	            constraintName = constraintName == null ? "" : constraintName;
	            if (constraintName.isEmpty()) {
	            	constraintName = String.format("fk_%s_%s", tableName, 
	            			TableUtil.getTableName(field.getType()));
	            }
	            String references = foreignKeyFieldAnnotation.references();
	            references = references == null ? "" : references;
	            if (references.isEmpty()) {
	            	references = TableUtil.getTableName(field.getType());
	            } else {
	            	references = TableUtil.getColumnName(references);
	            }
	            if (SQL_FOREIGN_KEYS.get(modelClass.toString()) == null) {
	            	SQL_FOREIGN_KEYS.put(modelClass.toString(), new ArrayList<String>());
	            }
	            if (DATABASE_ENGINE.trim().equalsIgnoreCase("mysql")) {
	            	StringBuilder formatter = new StringBuilder();
	            	formatter.append("ALTER TABLE %s ADD CONSTRAINT %s ");
	            	formatter.append("FOREIGN KEY(%s) REFERENCES %s(%s)%s%s");
	                fk = String.format(
	                	formatter.toString(),
	                    tableName,
	                    constraintName,
	                    fieldName,
	                    references,
	                    referencedColumn,
	                    onDeleteString,
	                    onUpdateString
	                );
	            }	            
	            List<String> fks = SQL_FOREIGN_KEYS.get(modelClass.toString());
	            if (!fks.contains(fk)) {
	            	fks.add(fk);
	            }
	    	}
    	}
    	return sql;
    }
    
    public static Map<String, Object> getManyToManyFieldSQL(Field field) {
    	Map<String, Object> sqls = new HashMap();
    	if (field != null && Model.class.isAssignableFrom(field.getDeclaringClass())) {    		
    		ManyToManyField manyToManyFieldAnnotation = field.getAnnotation(ManyToManyField.class);
    		if (manyToManyFieldAnnotation != null) {
                Class clazz = null;
                Class superClazz = null;
                Class modelClass = field.getDeclaringClass();
                ParameterizedType genericType = null;
                StringBuilder format = new StringBuilder();
                String through = manyToManyFieldAnnotation.through().getSimpleName();
                through = Model.class.getSimpleName().equals(through) ? "" : through;
                String model = manyToManyFieldAnnotation.model().getSimpleName();
                String references = manyToManyFieldAnnotation.references();
                String sqlManyToManyAssociation = "";
                String modelName = modelClass.getSimpleName();
                String tableName = TableUtil.getTableName(modelClass);
                String fk = "";
                String ix = "";
                through = through != null ? through.trim() : "";
                model = Model.class.getSimpleName().equals(model) ? "" : model;
                references = references != null ? references.trim() : "";
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
                if (references.isEmpty()) {
                	if (clazz != null) {
                		references = TableUtil.getTableName(clazz);
                	} else { 
                		references = TableUtil.getTableName(model);                                            	
                	}
                }
                if (through.isEmpty()) {                                           	                                            	
                	if (DATABASE_ENGINE.equalsIgnoreCase("mysql")) {
                        format.append("CREATE TABLE IF NOT EXISTS %s_%s (\n");
                        format.append("    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,\n");
                        format.append("    %s_id INT NOT NULL,\n");
                        format.append("    %s_id INT NOT NULL,\n");
                        format.append("    CONSTRAINT unq_%s_%s UNIQUE (%s_id, %s_id)\n");
                        format.append( ")");
                    } else {

                    }
                    sqlManyToManyAssociation = String.format(
                		format.toString(),
                		tableName, 
                		TableUtil.getColumnName(references),
                		TableUtil.getColumnName(modelName),
                		TableUtil.getColumnName(model),	
                		tableName,
                		TableUtil.getColumnName(references),
                		TableUtil.getColumnName(modelName),
                		TableUtil.getColumnName(model)
            		);
                    List<String> associationTables = new ArrayList<String>();
                    if (!SQL_ASSOCIATION_TABLES.contains(sqlManyToManyAssociation)) {                    	
                    	SQL_ASSOCIATION_TABLES.add(sqlManyToManyAssociation);
                    	associationTables.add(sqlManyToManyAssociation);
                    }
                    sqls.put("association_tables", associationTables);
                    String tbName = String.format("%s_%s", tableName, TableUtil.getColumnName(references));
                    if (!JediORMEngine.GENERATED_TABLES.contains(tbName)) {
                    	JediORMEngine.GENERATED_TABLES.add(tbName);
                    }                    
                    String onDeleteString = " ON DELETE";
                    if (manyToManyFieldAnnotation.on_delete().equals(Models.PROTECT)) {
                        onDeleteString = " ON DELETE RESTRICT";
                    } else if (manyToManyFieldAnnotation.on_delete().equals(Models.SET_NULL)) {
                        onDeleteString = " ON DELETE SET NULL";
                    } else if (manyToManyFieldAnnotation.on_delete().equals(Models.CASCADE)) {
                        onDeleteString = " ON DELETE CASCADE";
                    } else if (manyToManyFieldAnnotation.on_delete().equals(Models.SET_DEFAULT)) {
                        onDeleteString = " ON DELETE SET DEFAULT";
                    }	
                    String onUpdateString = " ON UPDATE";
                    if (manyToManyFieldAnnotation.on_update().equals(Models.PROTECT)) {
                        onUpdateString = " ON UPDATE RESTRICT";
                    } else if (manyToManyFieldAnnotation.on_update().equals(Models.SET_NULL)) {
                        onUpdateString = " ON UPDATE SET NULL";
                    } else if (manyToManyFieldAnnotation.on_update().equals(Models.CASCADE)) {
                        onUpdateString = " ON UPDATE CASCADE";                                                
                        if (DATABASE_ENGINE != null && DATABASE_ENGINE.equalsIgnoreCase("oracle")) {
                            onUpdateString = "";
                        }
                    } else if (manyToManyFieldAnnotation.on_update().equals(Models.SET_DEFAULT)) {
                        onUpdateString = " ON UPDATE SET DEFAULT";
                    }
                    if (SQL_FOREIGN_KEYS.get(modelClass.toString()) == null) {
    	            	SQL_FOREIGN_KEYS.put(modelClass.toString(), new ArrayList<String>());
    	            }                                        
                    if (SQL_INDEXES.get(modelClass.toString()) == null) {
                    	SQL_INDEXES.put(modelClass.toString(), new ArrayList<String>());
                    }
                    fk = String.format(
                        "ALTER TABLE %s_%s ADD CONSTRAINT fk_%s_%s_%s FOREIGN KEY (%s_id) REFERENCES %s (id)%s%s",
                        tableName,
                        TableUtil.getColumnName(references),
                        tableName,
                        TableUtil.getColumnName(references),
                        tableName,
                        TableUtil.getColumnName(modelName),
                        tableName,
                        onDeleteString,
                        onUpdateString
                    );
                    List<String> fks = SQL_FOREIGN_KEYS.get(modelClass.toString());
                    List<String> foreignKeys = new ArrayList();
    	            if (!fks.contains(fk)) {
    	            	fks.add(fk);
    	            	foreignKeys.add(fk);    	            	
    	            }
    	            sqls.put("foreign_keys", foreignKeys);
                    ix = String.format(
                        "CREATE INDEX idx_%s_%s_%s_id ON %s_%s (%s_id)",
                        tableName,
                        TableUtil.getColumnName(references),
                        TableUtil.getColumnName(modelName),
                        tableName,
                        TableUtil.getColumnName(references),
                        TableUtil.getColumnName(modelName)
                	);
    	            List<String> ixs = SQL_INDEXES.get(modelClass.toString());
    	            List<String> indexes = new ArrayList();
    	            if (!ixs.contains(ix)) {
    	            	ixs.add(ix);
    	            	indexes.add(ix);
    	            }
    	            sqls.put("indexes", indexes);
                    fk = String.format(
                        "ALTER TABLE %s_%s ADD CONSTRAINT fk_%s_%s_%s FOREIGN KEY (%s_id) REFERENCES %s (id)%s%s",
                        tableName,
                        TableUtil.getColumnName(references),
                        tableName,
                        TableUtil.getColumnName(references),
                        TableUtil.getColumnName(references),	                                                	
                        TableUtil.getColumnName(model),
                        TableUtil.getColumnName(references),
                        onDeleteString,
                        onUpdateString
                    );
                    if (!fks.contains(fk)) {
    	            	fks.add(fk);
    	            	foreignKeys.add(fk);    	            	
    	            }
                    ix = String.format(
                        "CREATE INDEX idx_%s_%s_%s_id ON %s_%s (%s_id)",
                        tableName,
                        TableUtil.getColumnName(references),
                        TableUtil.getColumnName(model),
                        tableName,
                        TableUtil.getColumnName(references),
                        TableUtil.getColumnName(model)
                    );
                    if (!ixs.contains(ix)) {
    	            	ixs.add(ix);
    	            	indexes.add(ix);
    	            }
                }
    		}
    	}
    	return sqls;
    }
    
    public static boolean isJediModel(Class c) {
    	return c != null && jedi.db.models.Model.class.isAssignableFrom(c);
    }
    
    public static boolean isJediModelFile(File file) {
    	boolean is = false;
    	if (file != null && file.exists()) {
    		String modelClassName = file.getAbsolutePath();
        	if (modelClassName.endsWith("java")) {
	        	modelClassName = JediORMEngine.convertFilePathToClassPath(modelClassName);	                                                        
	            Class modelClass = null;
	            try {
					modelClass = Class.forName(modelClassName);
				} catch (ClassNotFoundException classNotFoundException) {
					classNotFoundException.printStackTrace();
				}
	            if (isJediModel(modelClass)) {
	                is = true;
	            }
        	}
    	}
    	return is;
    }
    
    public static boolean isJediField(Field field) {    	
    	if (field != null && isJediModel(field.getDeclaringClass())) {
    		field.setAccessible(true);
    		List<Class> list = Arrays.asList(JEDI_FIELD_ANNOTATION_CLASSES);
    		for (Annotation annotation : field.getAnnotations()) {
    			if (list.contains(annotation.annotationType())) {
    				return true;
    			}
    		}    		
    	}
    	return false;
    }
    
    public static boolean isJediFieldAnnotation(Class fieldAnnotationClass) {
    	return (
			fieldAnnotationClass != null && 
			fieldAnnotationClass.isAnnotation() && 
			fieldAnnotationClass
				.getPackage()
				.getName()
				.equals("jedi.db.models")
		);
    }
    
    public static boolean isOneToOneField(Field field) {
    	boolean response = false;
    	if (JediORMEngine.isJediField(field) && field.getAnnotation(OneToOneField.class) != null) {
    		response = true;
    	}
    	return response;
    }
    
    public static boolean isForeignKeyField(Field field) {
    	boolean response = false;
    	if (JediORMEngine.isJediField(field) && field.getAnnotation(ForeignKeyField.class) != null) {
    		response = true;
    	}
    	return response;
    }
    
    public static boolean isManyToManyField(Field field) {
    	boolean response = false;
    	if (JediORMEngine.isJediField(field) && field.getAnnotation(ManyToManyField.class) != null) {
    		response = true;
    	}
    	return response;
    }
    
    public static Class getFieldAnnotationClass(Field field) {    	
    	if (isJediField(field)) {    		
    		field.setAccessible(true);    		
    		for (Class clazz : JEDI_FIELD_ANNOTATION_CLASSES) {
    			Annotation annotation = field.getAnnotation(clazz);
    			if (annotation != null) {
    				return annotation.annotationType();
    			}
    		}
    	}    	
    	return null;
    }
    
    public static Object getSQL(Field field) {
    	Object sql = null;
    	if (isJediField(field)) {
    		Class clazz = getFieldAnnotationClass(field);    		
	    	if (clazz == CharField.class) {
	    		sql = getCharFieldSQL(field);
	    	} else if (clazz == EmailField.class) {
	    		sql = getEmailFieldSQL(field);
	    	} else if (clazz == URLField.class) {
	    		sql = getURLFieldSQL(field);
	    	} else if (clazz == IPAddressField.class) {
	    		sql = getIPAddressFieldSQL(field);
	    	} else if (clazz == TextField.class) {
	    		sql = getTextFieldSQL(field);
	    	} else if (clazz == IntegerField.class) {
	    		sql = getIntegerFieldSQL(field);
	    	} else if (clazz == DecimalField.class) {
	    		sql = getDecimalFieldSQL(field);
	    	} else if (clazz == FloatField.class) {
	    		sql = getFloatFieldSQL(field);
	    	} else if (clazz == BooleanField.class) {
	    		sql = getBooleanFieldSQL(field);
	    	} else if (clazz == DateField.class) {
	    		sql = getDateFieldSQL(field);
	    	} else if (clazz == TimeField.class) {
	    		sql = getTimeFieldSQL(field);
	    	} else if (clazz == DateTimeField.class) {
	    		sql = getDateTimeFieldSQL(field);
	    	} else if (clazz == OneToOneField.class) {
	    		sql = getOneToOneFieldSQL(field);
	    	} else if (clazz == ForeignKeyField.class) {
	    		sql = getForeignKeyFieldSQL(field);
	    	} else if (clazz == ManyToManyField.class) {
	    		sql = getManyToManyFieldSQL(field);
	    	} else {
	    		
	    	}
    	}
    	return sql.toString().trim();
    }
    
    public static String getCreateTableSQL(JediApp app, Class<? extends jedi.db.models.Model> c) {
    	StringBuilder statement = new StringBuilder();
    	if (c != null) {
	    	String tableName = TableUtil.getTableName(c);
	    	if (app != null) {
	    		tableName = String.format("%s_%s", app.getDBTable(), tableName);
	    	}
            if (!GENERATED_TABLES.contains(tableName)) {
            	GENERATED_TABLES.add(tableName);
            }            
	        if (DATABASE_ENGINE.equals("mysql")) {	        	
        		statement.append(String.format("CREATE TABLE IF NOT EXISTS %s (\n", tableName));	        	
	        } else {
	        	statement.append(String.format("CREATE TABLE %s (\n", tableName));
	        }
            List<Field> fields = getFields(c);
            if (fields.isEmpty()) {
            	statement.append(String.format("%s%s\n", SQL_COLUMN_IDENTATION, getPrimaryKeySQL()));
            } else {
            	statement.append(String.format("%s%s,\n", SQL_COLUMN_IDENTATION, getPrimaryKeySQL()));
            }
            List<Field> manyToManyFields = new ArrayList<Field>();
            Annotation a = null;
            for (Field field : fields) {
            	a = field.getAnnotation(ManyToManyField.class);
            	if (a != null) {
            		getSQL(field);
            		manyToManyFields.add(field);
            	}
            }
            fields.removeAll(manyToManyFields);
            Iterator i = fields.iterator();
            Field f = null;
            while (i.hasNext()) {
            	f = (Field) i.next();
            	f.setAccessible(true);
            	String sql = getSQL(f).toString();
        		statement.append(String.format("%s%s", SQL_COLUMN_IDENTATION, sql));
        		
            	if (i.hasNext()) {
            		statement.append(",\n");
            	} else {
            		statement.append("\n");
            	}
            }
            Table tableAnnotation = c.getAnnotation(Table.class);
            if (tableAnnotation != null) {
            	String engine = tableAnnotation.engine();            	
            	String charset = tableAnnotation.charset();
            	String comment = tableAnnotation.comment();
            	String formatter = "";
            	formatter = engine == null || engine.isEmpty() ? 
            			"" : " ENGINE=%s";
            	if (!formatter.isEmpty()) {
            		engine = String.format(formatter, engine);
            	}
            	formatter = charset == null || charset.isEmpty() ? 
            			"" : " DEFAULT CHARSET=%s";
            	if (!formatter.isEmpty()) {
            		charset = String.format(formatter, charset);
            	}
            	formatter = comment == null || comment.isEmpty() ? 
            			"" : " COMMENT '%s'";
            	if (!formatter.isEmpty()) {
            		comment = String.format(formatter, comment);
            	}
            	statement.append(String.format(")%s%s%s", engine, charset, comment));
            } else {
            	statement.append(")");
            }
    	}
    	return statement.toString();
    }
    
    public static String getSQL(JediApp app, Class<? extends jedi.db.models.Model> c) {
    	return getCreateTableSQL(app, c);
    }
    
    public static String getSQL(Class<? extends jedi.db.models.Model> c) {
    	return getCreateTableSQL(null, c);
    }
    
    /**
     * Retorna a lista de instruções SQL para um determinado tipo de field em um modelo.
     * @param c
     * @param a
     * @return
     */
    public static List<String> listSQL(Class<? extends jedi.db.models.Model> c, Class a) {
    	List<String> l = new ArrayList();
    	if (isJediModel(c) && isJediFieldAnnotation(a)) {
    		for (Field f : c.getDeclaredFields()) {
    			if (isJediField(f)) {
    				l.add(getSQL(f).toString());
    			}
    		}
    	}
    	return l;
    }
    
    public static List<String> listSQL(Class<? extends jedi.db.models.Model> c) {
    	List<String> l = new ArrayList();
    	if (isJediModel(c)) {
    		for (Field f : c.getDeclaredFields()) {
    			if (isJediField(f)) {
    				l.add(getSQL(f).toString());
    			}
    		}
    	}
    	return l;
    }
    
    public static Map<Field, String> mapSQL(Class<? extends jedi.db.models.Model> c, Class a) {
    	Map<Field, String> m = new HashMap();
    	if (isJediModel(c) && isJediFieldAnnotation(a)) {
    		for (Field f : c.getDeclaredFields()) {
    			if (f.getAnnotation(a) != null) {
    				m.put(f, getSQL(f).toString());
    			}
    		}
    	}
    	return m;
    }
    
    public static Map<Field, String> mapSQL(Class<? extends jedi.db.models.Model> c) {
    	Map<Field, String> m = new HashMap();
    	if (isJediModel(c)) {
    		for (Field f : c.getDeclaredFields()) {
    			if (isJediField(f)) {
    				m.put(f, getSQL(f).toString());
    			}
    		}
    	}
    	return m;
    }
    
    public static List<Field> getAllFields(List<Field> fields, Class c) {
    	if (c != null) {
    		Class sc = c.getSuperclass();
    		/* Verifies if the superclass exists and is a subclass 
    		 * of jedi.db.models.Model class.
    		 */
    		if (sc != null && 
				jedi.db.models.Model.class.isAssignableFrom(sc) && 
				jedi.db.models.Model.class != sc) {
    			fields = getAllFields(fields, c.getSuperclass());
    		}
    		for (Field field : c.getDeclaredFields()) {
    			if (isJediField(field)) {
    				fields.add(field);
    			}
    		}
    	}
        return fields;
    }
    
    public static List<Field> getAllFields(Class<? extends jedi.db.models.Model> c) {
    	return getAllFields(new ArrayList<Field>(), c);
    }
    
    private static void createDBEnviroments() {
		String stmt = "";    		
		if (JediORMEngine.DATABASE_ENGINE.equals("mysql")) {
			String engine = String.format("engine=%s", JediORMEngine.DATABASE_ENGINE);
			String password = String.format("password=%s", JediORMEngine.DATABASE_PASSWORD);
			SQLManager.setConnection(ConnectionFactory.getConnection(engine, password));
			stmt = "CREATE DATABASE %s CHARACTER SET %s";
			String db = JediORMEngine.DATABASE_NAME;
			String charset = JediORMEngine.DATABASE_CHARSET;			
			if (db.endsWith("_development")) {
				SQLManager.raw(String.format(stmt, db, charset));
				db = db.replace("development", "test");
				SQLManager.raw(String.format(stmt, db, charset));
				db = db.replace("test", "production");
				SQLManager.raw(String.format(stmt, db, charset));
				try {
					SQLManager.getConnection().close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
    	}
    }
}
