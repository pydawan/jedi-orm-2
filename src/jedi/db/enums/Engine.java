/***********************************************************************************************
 * @(#)Adapters.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/05/16
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

package jedi.db.enums;

/**
 * @author Thiago Alexandre Martins Monteiro
 * @version 1.0
 * 
 */
public enum Engine {
    MYSQL("MYSQL"),
    POSTGRESQL("POSTGRESQL"),
    ORACLE("ORACLE"),
    SQL_SERVER("SQL_SERVER"),
    H2("H2"),
    SQLITE("SQLITE");

    private final String value;

    private Engine(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}