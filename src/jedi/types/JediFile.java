/***********************************************************************************************
 * @(#)PyFile.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/01/27
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

package jedi.types;

import java.io.FileNotFoundException;
import java.io.IOException;

public class JediFile extends java.io.RandomAccessFile {

    private java.lang.String mode;

    public JediFile(java.lang.String name, java.lang.String mode) 
        throws FileNotFoundException {
        super(name, mode.replaceAll("a|w", "rw"));
        this.mode(mode);

        try {
            if (mode.equals("a")) {
                // Salta os bytes já existentes no arquivo 
                // para que o novo conteúdo seja incluido no fim.
                this.skipBytes((int) this.length());
            } else if (mode.equals("w")) {
                // Limpa o arquivo.
                this.setLength(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }

    public JediFile(java.lang.String name) throws FileNotFoundException {
        super(name, "r");
    }

    public void write(java.lang.String text) {
        if (text != null) {
            try {
                this.write(text.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(Object object) {
        if (object != null) {
            try {
                this.write(object.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(java.lang.String format, Object... objects) {
        // Verificando se a lista de objetos foi passada.
        if (objects != null) {
            // Verificando se existe um formato e se o mesmo não é uma string
            // vazia.
            if (format != null && !format.isEmpty()) {
                java.lang.String[] formats = null;
                // Criando um array de formatos.
                if (format != null && !format.isEmpty()) {
                    formats = format.split(" ");
                }

                // Iterando através da lista de formatos e de objetos.
                if (formats.length == objects.length) {
                    for (int i = 0; i < objects.length; i++) {
                        // Escrevendo o texto formatado no arquivo.
                        this.write(java.lang.String.format(formats[i], objects[i]));
                    }
                }
            }
        }
    }

    public java.lang.String getMode() {
        return mode;
    }

    public java.lang.String mode() {
        return mode;
    }

    public void setMode(java.lang.String mode) {
        this.mode = mode;
    }

    public void mode(java.lang.String mode) {
        this.mode = mode;
    }
}