package com.stt.dash.backend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

public class AgendaFileUtils {

    private static final Logger log = LoggerFactory.getLogger(AgendaFileUtils.class);

    private String baseDirect;

    //@Value("${agenda.basedir}")
    //private static String baseDir;
//    private final static String baseDir = "src/main/resources/agendafiles/";
    private static String baseDir = "";

    public final static void setBaseDir(String base) {
        baseDir = base;
        System.out.println("***** Asignando Upload ****** " + base);
    }

    /**
     * Genera un nombre de archivo válido y único basado en fileName
     *
     * @param fileName
     * @return
     */
    public final static String createUniqueFileName(String fileName) {
        int len = fileName.length();
        if (len < 5) {
            System.out.println("File name has strange format; might be invalid or be missing fileType.");
            fileName = "auto-generated-name.csv";
        }
        if (!fileName.substring(len - 4).equals(".csv") && !fileName.substring(len - 4).equals(".txt")) {
            System.out.println("File is not a .csv or .txt file. ");
        }

        File foundFile = new File(baseDir + fileName);

        while (foundFile.exists()) {
            fileName = (fileName.substring(0, len - 4) + "(1)" + fileName.substring(len - 4));
            foundFile = new File(baseDir + fileName);
        }

        return fileName;
    }

    /**
     * Crea un archivo que representa los datos de una agenda o log de agenda
     *
     * @param fileName
     * @param fileStream
     * @return
     */
    public final static boolean createAgendaFile(String fileName, InputStream fileStream) {
        try {
            byte[] buffer = new byte[fileStream.available()];
            fileStream.read(buffer);

            File targetFile = new File(baseDir + fileName);
            if (targetFile.exists()) {
                targetFile.delete();
            }
            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(buffer);

        } catch (IOException ex) {
            log.error("", ex);
            return false;
        }
        return true;
    }

    /**
     * Ecuentra un archivo
     *
     * @param fileName
     * @return
     */
    public final static File getFile(String fileName) {
        File foundFile = new File(baseDir + fileName);
        if (!foundFile.exists()) {
            System.out.println("Error finding file!!!");
            return null;
        }
        return foundFile;
    }

    /**
     * Encuentra un archivo y lo devuelve como un InputStream
     *
     * @param fileName
     * @return
     */
    public final static InputStream getFileAsStream(String fileName) {
        File foundFile = new File(baseDir + fileName);
        if (!foundFile.exists()) {
            log.info("Error finding file: {}", fileName);
            return null;
        }
        try {
            InputStream stream = new FileInputStream(foundFile);
            return stream;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.out.println("COULD NOT CREATE STREAM FROM FILE");
            return null;
        }
    }

    /**
     * Encuentra el archivo con información sobre los errores de la agenda a la
     * cual le corresponde (en forma de InputStream)
     *
     * @param fileName
     * @return
     */
    public final static InputStream getStreamValidationLog(String fileName) {
        return getStreamValidationLog("", fileName);
    }

    /**
     * Encuentra el archivo con información sobre los errores de la agenda a la
     * cual le corresponde (en forma de InputStream)
     *
     * @param userEmail
     * @param fileName
     * @return
     */
    public final static InputStream getStreamValidationLog(String userEmail, String fileName) {
        int len = fileName.length();
        if (len < 5) {
            log.warn("[ODASH] [{}] [{}] Bad name or fileType when fetching file.", userEmail, fileName);
            return null;
        }
        String logFileName = fileName.substring(0, len - 4) + "-log.txt";
        return getFileAsStream(logFileName);
    }

    /**
     * Encuentra el archivo con información sobre los errores de la agenda a la
     * cual le corresponde (en forma de archivo)
     *
     * @param fileName
     * @return
     */
    public final static File getFileValidationLog(String fileName) {
        int len = fileName.length();
        if (len < 5 || !fileName.substring(len - 4).equals(".csv")) {
            log.warn("Bad name or fileType when fetching file.");
            return null;
        }
        String logFileName = fileName.substring(0, len - 4) + "-log.txt";
        return getFile(logFileName);
    }

    public final static boolean createFileValidationLog(String fileName, List<String> errorList) {
        return createFileValidationLog(fileName, errorList, "");
    }

    /**
     * Crea un archivo con información sobre los errores de la agenda a la cual
     * le corresponde
     *
     * @param fileName
     * @param errorList
     * @return
     */
    public final static boolean createFileValidationLog(String fileName, List<String> errorList, String userEmail) {
        log.info("[ODASH] [{}] [{}]({}) CREATING VALIDATION FILE", userEmail, fileName, errorList.size());
        int len = fileName.length();
        if (len < 5) {
            log.warn("[ODASH] [{}] [{}] Bad name or fileType when fetching file.", userEmail, fileName);
            return false;
        }
        String logFileName = fileName.substring(0, len - 4) + "-log.txt";

        File targetFile = new File(baseDir + logFileName);
        try {
            if (!targetFile.createNewFile()) {
                System.out.println("Over-writing new Log onto old Log");
                targetFile.delete();
            }
            int listLen = errorList.size();
            if (listLen < 1) {
                System.out.println("The errorList is empty.");
            }
            FileWriter logWriter = new FileWriter(targetFile);

            for (String line : errorList) {
                logWriter.write(line + "\n");
            }

            logWriter.close();
            log.info("[ODASH] [{}] [{}] CREATED", userEmail, logFileName);

        } catch (IOException ex) {
            String sCarpAct = System.getProperty("user.dir");
            File carpeta = new File(sCarpAct);
            String[] listado = carpeta.list();
            if (listado == null || listado.length == 0) {
                System.out.println("No hay elementos dentro de la carpeta actual");

            } else {
                for (int i = 0; i < listado.length; i++) {
                    System.out.println(listado[i]);
                }
            }
            //System.out.println("COULD NOT CREATE FILE OR WRITER");
            log.error("COULD NOT CREATE FILE OR WRITER");
            log.error("", ex);
        }

        return true;
    }

    /**
     * Borra el archivo de la carpeta de agendas
     *
     * @param fileName
     * @return
     */
    public final static boolean deleteFile(String fileName) {
        String logFileName = fileName.substring(0, fileName.length() - 4) + "-log.txt";
        File delFile = new File(baseDir + fileName);
        File delFile2 = new File(baseDir + logFileName);
        if (delFile.delete() && delFile2.delete()) {
            System.out.println("Se ha borrado el archivo: " + delFile.getName());
            return true;
        }
        System.out.println("No se ha borrado el archivo.");
        return false;
    }
}
