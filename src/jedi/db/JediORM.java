/***********************************************************************************************
 * @(#)JediORM.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/09/09
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

package jedi.db;

import java.io.File;

import jedi.db.engine.JediORMEngine;

/**
 * Classe principal ou de execução do Jedi ORM Framework.
 *
 * @author thiago.monteiro
 *
 */
public abstract class JediORM {
    /**
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        try {
            if (JediORMEngine.WEB_APP) {
                JediORMEngine.APP_SRC_DIR = String.format(
                    "%s%sweb%sWEB-INF%ssrc",
                    JediORMEngine.APP_ROOT_DIR,
                    File.separator,
                    File.separator,
                    File.separator
                );
                JediORMEngine.JEDI_PROPERTIES_PATH = String.format(
                    "%s%sweb%sWEB-INF%sconfig%sjedi.properties",
                    JediORMEngine.APP_ROOT_DIR,
                    File.separator,
                    File.separator,
                    File.separator,
                    File.separator
                );
                File jediPropertiesFile = new File(
                    JediORMEngine.JEDI_PROPERTIES_PATH
                );
                if (!jediPropertiesFile.exists()) {
                    JediORMEngine.JEDI_PROPERTIES_PATH = String.format(
                        "%s%sweb%sWEB-INF%sjedi.properties",
                        JediORMEngine.APP_ROOT_DIR,
                        File.separator,
                        File.separator,
                        File.separator
                     );
                }
            } else {
                JediORMEngine.APP_SRC_DIR = String.format(
                    "%s%ssrc",
                    JediORMEngine.APP_ROOT_DIR,
                    File.separator
                );
                JediORMEngine.JEDI_PROPERTIES_PATH = String.format(
                    "%s%sjedi.properties",
                    JediORMEngine.APP_ROOT_DIR,
                    File.separator
                );
            }
            System.out.println("\n");
            JediORMEngine.syncdb(JediORMEngine.APP_SRC_DIR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
