package jedi.db;


import java.io.File;

import jedi.db.engine.JediORMEngine;

/**
 * Main class of the framework.
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
