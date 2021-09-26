/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smspreparationmanager.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

/**
 *
 * @author Enrique
 */
public class FileUtils {
    
    /**
     * Carga archivo de Properties.
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public final static Properties loadProperty(String fileName) throws IOException {
        Properties p = new Properties();
        p.load(getInputStream(fileName));
        return p;
    }

    /**
     * Busca en Directorio y dentro del .jar
     *
     * @param fileName
     * @return
     * @throws IOException
     * @since v1.0
     */
    public final static InputStream getInputStream(String fileName) throws IOException {
        try {
            return getInputStreamFromPath(fileName);
        } catch (IOException ioe) {
            return getInputStreamFromResource(fileName);
        }
    }

    /**
     * Busca el archivo en directorio.
     *
     * @param fileName
     * @return
     * @throws IOException
     * @since v1.0
     */
    public final static InputStream getInputStreamFromPath(String fileName) throws IOException {
        InputStream is;
        is = new FileInputStream(fileName);
        return is;
    }

    /**
     * Busca el archivo dentro del jar.
     *
     * @param fileName
     * @return
     * @throws IOException
     * @since v1.0
     */
    public final static InputStream getInputStreamFromResource(String fileName) throws IOException {
        InputStream is;
        is = FileUtils.class.getResourceAsStream(fileName);
        return is;
    }

    public final static URI getURI(String fileName) throws IOException, URISyntaxException {
        URI uri;
        File f = new File(fileName);
        if (f.exists()) {
            uri = f.toURI();
        } else {
            URL fileURL = FileUtils.class.getResource(fileName);
            uri = fileURL.toURI();
        }
        return uri;
    }
}
