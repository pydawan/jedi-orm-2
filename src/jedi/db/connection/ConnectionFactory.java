/***********************************************************************************************
 * @(#)ConnectionFactory.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/07/30
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

package jedi.db.connection;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import jedi.db.engine.JediORMEngine;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Database Connection Factory.
 * 
 * @author Thiago Alexandre Martins Monteiro
 * @version 1.0
 * 
 */
public class ConnectionFactory {

    private static Connection connection;
    private static ComboPooledDataSource dataSource;
    
    static {
    	if (JediORMEngine.JEDI_PROPERTIES_LOADED) {
	        String engine = JediORMEngine.DATABASE_ENGINE;
	        String host = JediORMEngine.DATABASE_HOST;
	        String port = JediORMEngine.DATABASE_PORT;
	        String user = JediORMEngine.DATABASE_USER;
	        String password = JediORMEngine.DATABASE_PASSWORD;
	        String database = JediORMEngine.DATABASE_NAME;
	        String driver = "";
	        String url = "";            
	        
	        // Egine
	        if (!engine.isEmpty()) {
	        	// Creates the connection pool
	            dataSource = new ComboPooledDataSource();
	            dataSource.setInitialPoolSize(JediORMEngine.DATABASE_INITIAL_POOL_SIZE);
	            dataSource.setAcquireIncrement(JediORMEngine.DATABASE_ACQUIRE_INCREMENT);
	            dataSource.setMaxPoolSize(JediORMEngine.DATABASE_MAX_POOL_SIZE);
	            dataSource.setMinPoolSize(JediORMEngine.DATABASE_MIN_POOL_SIZE);
	            dataSource.setMaxStatements(JediORMEngine.DATABASE_MAX_STATEMENTS);
	        	// Host
	            if (host.isEmpty()) {
	                if (!engine.equals("h2") && !engine.equals("sqlite")) {
	                    host = "localhost";
	                }
	            }                    
	            // Port
	            if (port.isEmpty()) {
	                if (engine.equals("mysql")) {
	                    port = "3306";
	                } else if (engine.equals("postgresql")) {
	                    port = "5432";
	                } else if (engine.equals("oracle")) {
	                    port = "1521";
	                }
	            }
	            // User
	            if (user.isEmpty()) {
	                if (engine.equals("mysql")) {
	                    user = "root";
	                } else if (engine.equals("postgresql")) {
	                    user = "postgres";
	                } else if (engine.equals("oracle")) {
	                    user = "hr";
	                } else if (engine.equals("h2")) {
	                    user = "sa";
	                }
	                dataSource.setUser(user);
	            }                    
	            // Password
	            dataSource.setPassword(password);
	            // Database
	            if (database.isEmpty()) {
	                if (engine.equals("mysql")) {
	                    database = "mysql";
	                } else if (engine.equals("postgresql")) {
	                    database = "postgres";
	                } else if (engine.equals("oracle")) {
	                    database = "xe";
	                } else if (engine.equals("h2")) {
	                    database = "test";
	                }
	            }                
	            // JDBC Drive and URL
	            if (engine.equals("mysql")) {
	            	driver = "com.mysql.jdbc.Driver";
	            	url = String.format(
	                    "jdbc:%s://%s:%s/%s?user=%s&password=%s",
	                    engine, host, port, database, user, password
	                );
	            } else if (engine.equals("postgresql")) {
	            	driver = "org.postgresql.Driver";
	            } else if (engine.equals("oracle")) {
	            	driver = "oracle.jdbc.driver.OracleDriver";	
	            } else if (engine.equals("h2")) {
	            	driver = "org.h2.Driver";
	            }
	            dataSource.setJdbcUrl(url);
	            try {
					dataSource.setDriverClass(driver);
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				}
	        }
    	}
    }
    
    public static Connection getConnection() {
    	try {
    		if (dataSource != null) {
    			connection = dataSource.getConnection();
    		}			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return connection;
    }
    
    /**
     * @return Connection Returns a connection to a database.
     */
    public Connection connect() {
    	return getConnection();
    }

    /**
     * @param args
     * @return
     */
    public static Connection connect(String... args) {
        return getConnection(args);
    }
        
    /**
     * @param args
     * @return
     */
    public static Connection getConnection(String... args) {
        Connection connection = null;
        if (args != null && args.length > 0) {
            try {
                String databaseEngine = "";
                String databaseHost = "";
                String databasePort = "";
                String databaseUser = "";
                String databasePassword = "";
                String databaseName = "";
                String databaseOptionsAutocommit = "";
                for (int i = 0; i < args.length; i++) {
                    args[i] = args[i].toLowerCase();
                    args[i] = args[i].replace(" = ", "=");
                    // Engine
                    if (args[i].equals("engine=mysql")) {
                        Class.forName("com.mysql.jdbc.Driver");
                        databaseEngine = "mysql";
                    } else if (args[i].equals("engine=postgresql")) {
                        Class.forName("org.postgresql.Driver");
                        databaseEngine = "postgresql";
                    } else if (args[i].equals("engine=oracle")) {
                        Class.forName("oracle.jdbc.driver.OracleDriver");
                        databaseEngine = "oracle";
                    } else if (args[i].equals("engine=sqlite")) {
                        Class.forName("org.sqlite.JDBC");
                        databaseEngine = "sqlite";
                    } else if (args[i].equals("engine=h2")) {
                        databaseEngine = "h2";
                        Class.forName("org.h2.Driver");
                    }
                    // Host
                    if (args[i].startsWith("host=")) {
                        if (args[i].split("=").length > 1) {
                            databaseHost = args[i].split("=")[1];
                        }
                    }
                    if (databaseHost != null && databaseHost.isEmpty() && !databaseEngine.equals("h2") 
                        && !databaseEngine.equals("sqlite")) {
                        databaseHost = "localhost";
                    }
                    // Port
                    if (args[i].matches("port=\\d+")) {
                        databasePort = args[i].split("=")[1];
                    }
                    if (databasePort != null && databasePort.isEmpty()) {
                        if (databaseEngine.equals("mysql")) {
                            databasePort = "3306";
                        } else if (databaseEngine.equals("postgresql")) {
                            databasePort = "5432";
                        } else if (databaseEngine.equals("oracle")) {
                            databasePort = "1521";
                        }
                    }
                    // Database
                    if (args[i].startsWith("database=")) {
                        if (args[i].split("=").length > 1) {
                            databaseName = args[i].split("=")[1];
                        }
                    }
                    if (databaseName != null && databaseName.isEmpty()) {
                        if (databaseEngine.equals("mysql")) {
                            databaseName = "mysql";
                        } else if (databaseEngine.equals("postgresql")) {
                            databaseName = "postgres";
                        } else if (databaseEngine.equals("oracle")) {
                            databaseName = "xe";
                        } else if (databaseEngine.equals("h2")) {
                            databaseName = "test";
                        }
                    }
                    // User
                    if (args[i].startsWith("user=")) {
                        if (args[i].split("=").length > 1) {
                            databaseUser = args[i].split("=")[1];
                        }
                    }
                    if (databaseUser != null && databaseUser.isEmpty()) {
                        if (databaseEngine.equals("mysql")) {
                            databaseUser = "root";
                        } else if (databaseEngine.equals("postgresql")) {
                            databaseUser = "postgres";
                        } else if (databaseEngine.equals("oracle")) {
                            databaseUser = "hr";
                        } else if (databaseEngine.equals("h2")) {
                            databaseUser = "sa";
                        }
                    }
                    // Password
                    if (args[i].startsWith("password=")) {
                        if (args[i].split("=").length > 1) {
                            databasePassword = args[i].split("=")[1];
                        }
                    }
                    if (databasePassword != null && databasePassword.isEmpty()) {
                        if (databaseEngine.equals("mysql")) {
                            databasePassword = "mysql";
                        } else if (databaseEngine.equals("postgresql")) {
                            databasePassword = "postgres";
                        } else if (databaseEngine.equals("oracle")) {
                            databasePassword = "hr";
                        } else if (databaseEngine.equals("h2")) {
                            databasePassword = "1";
                        }
                    }
                    if (args[i].startsWith("autocommit=")) {
                        if (args[i].split("=").length > 1) {
                            databaseOptionsAutocommit = args[i].split("=")[1];
                        }
                    }
                    args[i] = args[i].replace("=", " = ");
                }
                if (databaseEngine.equals("mysql")) {
                    connection = DriverManager.getConnection(
                        String.format(
                            "jdbc:mysql://%s:%s/%s?user=%s&password=%s", 
                            databaseHost, databasePort, databaseName, databaseUser, databasePassword
                        )
                    );
                } else if (databaseEngine.equals("postgresql")) {
                    connection = DriverManager.getConnection(
                        String.format(
                            "jdbc:postgresql://%s:%s/%s", 
                            databaseHost, databasePort, databaseName
                        ), 
                        databaseUser, 
                        databasePassword
                    );
                } else if (databaseEngine.equals("oracle")) {
                    String sid = databaseName;
                    String url = "jdbc:oracle:thin:@" + databaseHost + ":" + databasePort + ":" + sid;
                    connection = DriverManager.getConnection(url, databaseUser, databasePassword);
                } else if (databaseEngine.equals("sqlite")) {
                    Class.forName("org.sqlite.JDBC");
                    connection = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
                } else if (databaseEngine.equals("h2")) {
                    connection = DriverManager.getConnection(
                        String.format(
                            "jdbc:%s:~/%s", 
                            databaseEngine, databaseName
                        ), 
                        databaseUser, 
                        databasePassword
                    );
                }
                if (connection != null) {
                    if (!databaseOptionsAutocommit.isEmpty() && (databaseOptionsAutocommit.equalsIgnoreCase("true") 
                        || databaseOptionsAutocommit.equalsIgnoreCase("false"))) {
                        connection.setAutoCommit(Boolean.parseBoolean(databaseOptionsAutocommit));
                    } else {
                        connection.setAutoCommit(false);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Ocorreram uma ou mais falhas ao tentar obter uma conexão com o banco de dados.");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("O driver de conexão com o banco de dados não foi encontrado.");
                e.printStackTrace();
            }
        }
        return connection;
    }
}