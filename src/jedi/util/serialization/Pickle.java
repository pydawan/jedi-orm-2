/***********************************************************************************************
 * @(#)Pickle.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/01/23
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

package jedi.util.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

/**
 * Serializes and Deserializes objects.
 * This implementation was based on pickle module of the Python language.
 * 
 * @author Thiago Alexandre Martins Monteiro
 * @version 1.0
 * 
 */
public abstract class Pickle {

    /**
     * Serializes a object into a file.
     * 
     * @param o Object to be serialized.
     * @param f File where the serialization will occurs.
     * @return void
     */
    public static void dump(Object o, File f) {
        dump(o, f, false);
    }

    /**
     * Serializes a object into a file.
     * 
     * @param o Object to be serialized.
     * @param f File where the serialization will occurs.
     * @param append defines if the write will occur in append mode.
     */
    public static void dump(Object o, File f, boolean append) {

        if (o != null && f != null) {

            try {
                // File where the data will be written.
                FileOutputStream fos = new FileOutputStream(f, append);
                
                // Object that writes (writer) the data on the file.
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.flush();
                oos.writeObject(o); // write the data.
                oos.close();

                fos.close();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Serializes a object on the main memory and returns a sequence of bytes as a String.
     * 
     * @param o Object to be serialized.
     * @return String
     */

    public static String dumps(Object o) {
        String s = "";

        try {
            // Serializing.

            // Reference to a sequence of bytes on the memory.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.close();

            // Converts a array of bytes into a String.
            // The default conversion doesn't works on the other hand Base64 works fine.
            s = new String(Base64.encodeBase64(baos.toByteArray() ) );

            baos.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * Deserializes objects.
     * 
     * @param f File that holds the serialized objects.
     * @return Object
     */
    public static Object load(File f) {
        Object o = null;

        if (f != null) {
            try {
                FileInputStream fis = new FileInputStream(f);

                // The file reader.
                ObjectInputStream ois = new ObjectInputStream(fis);
                o = ois.readObject();
                ois.close();

                fis.close();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return o;
    }

    /**
     * Receives a sequence of bytes and returns a object.
     * 
     * @param s Sequence of bytes.
     * @return Object
     */
    public static Object loads(String s) {
        Object o = null;
        ByteArrayInputStream bais;

        try {
            bais = new ByteArrayInputStream(Base64.decodeBase64(s.getBytes() ) );
            ObjectInputStream ois = new ObjectInputStream(bais);
            
            o = ois.readObject();
            
            ois.close();
            bais.close();
            
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return o;
    }
}