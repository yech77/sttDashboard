package com.stt.dash.backend.thread;

import com.google.gson.Gson;
import com.googlecode.gentyref.TypeToken;
import com.stt.dash.app.OProperties;
import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.service.AgendaService;
import com.stt.dash.backend.util.AgendaFileUtils;
import com.stt.dash.backend.util.ws.OWebClient;
import com.stt.dash.utils.ws.UtilDto;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.lang.reflect.Type;

/**
 * Esta es la que se ejecuta para subir agendas y validar.
 */
public class AgendaParserRunnable {

    /**/
    private final static Logger log = LoggerFactory.getLogger(AgendaParserRunnable.class);
    /* TODO: Descablear */
    private final static Pattern regexPattern = Pattern.compile("^(58)(412|414|416|424|426)([0-9]{7})$");
    private final Gson gson = new Gson();
    private final WebClient webClient;
    private final OProperties properties;
    /**/
    Map<Integer, Integer> lineSizeMap = new HashMap<>();
    Type gsonType = new TypeToken<HashMap<Integer, Integer>>() {
    }.getType();
    /**/
    private String userEmail;
    // Agenda para procesar
    private Agenda agenda;
    // Servicios
    private AgendaService agenda_service;
    // Datos del archivo
    private InputStream stream;
    // Lista de errores de validacion
    private final List<String> agendaLog = new ArrayList<>();

    public AgendaParserRunnable(Agenda agenda, AgendaService agenda_service, String userEmail,
                                WebClient webClient, OProperties properties) {
        this.properties = properties;
        this.webClient = webClient;
        Optional<Agenda> optional = agenda_service.findById(agenda.getId());
        if (!optional.isPresent()) {
            return;
        }
        this.agenda = optional.get();
        log.info("Agenda encontrada!!!!!!!!!!!! *********************** ");
        this.agenda_service = agenda_service;
        this.userEmail = userEmail;
    }

    //    @Override
    public void run() {
        log.info("{} [INIT]", getStringLog());
        if (agenda == null || agenda_service == null) {
            log.error("{} Arguments passed to agenda validation are null. Exiting process.", getStringLog());
            return;
        }

        // Iniciar agenda y proceso de validacion
        updateAgendaStatus(Agenda.Status.VALIDATING);
        stream = AgendaFileUtils.getFileAsStream(agenda.getFileName());

        // Si el archivo no existe, lo marca como invalido
        if (stream == null) {
            updateAgendaStatus(Agenda.Status.CORRUPT_OR_LOST);
            log.info("{}  FILE NOT EXIST. Exiting Process", getStringLog());
            logLine(0, "Archivo no existe");
            AgendaFileUtils.createFileValidationLog(agenda.getFileName(), agendaLog);
            return;
        }

        // Validacion de cada contacto se realiza comparando con el primero
        int itemCounter = 0;
        int invalidItemCounter = 0;

        try {
            Character separatorChar = '0';
            InputStreamReader isr = new InputStreamReader(stream,
                    StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            /* Marcar el principio del archivo para luego regresarme. */
            br.mark(1);
            /* El primer campo siempre es el numero de celular.*/
            separatorChar = findSeparatorIfThereIsAny(separatorChar, br);
            /* Asignar un separador por defecto */
            if (separatorChar != ';' && separatorChar != ',' && separatorChar != '|') {
                log.warn("Separador no encontrado");
                logLine(0, "Separador no encontrado");
                throw new IOException("No se pudo determinar el separador del archivo");
            }
//            log.info("{} SEPARATOR FOUND ['{}']", getStringLog(), separatorChar);
            /* Ir al inicio marcado */
            br.reset();
            /* READER */
            Iterable<CSVRecord> records = CSVFormat.newFormat(separatorChar)
                    .withQuote('"')
                    .withIgnoreEmptyLines(true)
                    .parse(br);

            int sizeOfFirsLine = 0;
            for (CSVRecord record : records) {
                itemCounter++;
                /* Obtener size y regex de la primera linea */
                if (record.getRecordNumber() == 1) {
                    sizeOfFirsLine = saveFirsLine(record);
                }
                /* keep substring from word value to the end of the string of record.toString() */
                String substring = record.toString().substring(record.toString().indexOf("values="));
                substring = substring.replaceAll(";", " ");
                UtilDto filterWords = findFilterWords(substring);
                if (filterWords.isExistAlertsOrNoPass()) {
                    if (filterWords.isExistAlerts()) {
                        log.info("{} LINE ({}) - INVALID WORDS ({}) ", getStringLog(), record.getRecordNumber(),
                                filterWords.getAlerts());
                        logLine(record.getRecordNumber(), "Palabras: \"" + filterWords.getAlerts() + "\" no estan permitidas");
                        invalidItemCounter++;
                    }
                    if (filterWords.isExistNoPass()) {
                        log.info("{} LINE ({}) - INVALID WORDS ({}) ", getStringLog(), record.getRecordNumber(),
                                filterWords.getNoPass());
                        logLine(record.getRecordNumber(), "Palabras: \"" + filterWords.getNoPass() + "\" no estan permitidas");
                        invalidItemCounter++;
                    }
                    continue;
                }

                /* Validar cantidad de parametros */
                if (record.size() != sizeOfFirsLine) {
                    invalidItemCounter++;
                    log.warn("{} LINE ({}) - INCORRECT SIZE ({}) MUST BE ({})", getStringLog(), record.getRecordNumber(),
                            record.size(), sizeOfFirsLine);
                    logLine(record.getRecordNumber(), "Parametros Invalidos; Hay " + record.size() + " campos, se necesitan " + sizeOfFirsLine);
                    continue;
                }

                /* Valida numero de telefono */
                if (!validatePhoneNumber(record.get(0))) {
                    invalidItemCounter++;
                    log.warn("{} LINE ({}) - INVALID NUMBER ({}) ", getStringLog(), record.getRecordNumber(),
                            record.get(0));
                    logLine(record.getRecordNumber(), "Numero Telefonico Invalido; \"" + record.get(0) + "\" no existe o tiene un formato erroneo/desconocido");
                }

                /* Valida columnas vacias */
                int errorOnColum = 0;
                /* Recorrer las columnas  */
                int totalSize = 0;
                for (String string : record) {
                    if (string == null || "".equals(string.trim())) {
                        log.warn("{} LINE ({}) - HAS EMPTY COLUMN", getStringLog(), record.getRecordNumber());
                        logLine(record.getRecordNumber(), "Parametro Invalido; Tiene columna vacia");
                        errorOnColum++;
                    } else {
                        totalSize += string.length();
                    }
                }
                /* sustraer el size de la columna 0 (los numeros de telefono) */
                totalSize -= record.get(0).length();
                /**/
                Integer lineCounter = lineSizeMap.get(totalSize);
                if (lineCounter == null) {
                    lineCounter = 1;
                } else {
                    lineCounter++;
                }
                lineSizeMap.put(totalSize, lineCounter);
                /**/
                if (errorOnColum > 0) {
                    invalidItemCounter++;
                }
            }
            if (itemCounter == 0) {
                log.info("{} FILE IS EMPTY. Exiting Process", getStringLog());
                logLine(0, "Archivo esta vacio");
                invalidItemCounter = 1;
            }
            // Guarda los datos de la agenda
            agenda.setInvalidItemCount(invalidItemCounter);
            agenda.setItemCount(itemCounter);
            agenda.setStatus(invalidItemCounter > 0 ? Agenda.Status.HAS_WARNINGS : Agenda.Status.READY_TO_USE);
            agenda.setSizeOfLines(gson.toJson(lineSizeMap, gsonType));
            if (agendaLog.isEmpty()) {
                agendaLog.add("No hay problemas.");
            }
            // Genera un log de errores y actualiza la agenda en la tabla
            AgendaFileUtils.createFileValidationLog(agenda.getFileName(), agendaLog, userEmail);
            agenda_service.updateState(null, agenda);
        } catch (IOException ex) {
            // Marca la agenda invalida si se encuentran problemas procesando los archivos
            updateAgendaStatus(Agenda.Status.CORRUPT_OR_LOST);
            System.out.println("Validation for file " + agenda.getFileName() + " failed. Exiting process.");
        }
    }

    private int saveFirsLine(CSVRecord record) {
        int sizeOfFirsLine;
        sizeOfFirsLine = record.size();
        StringBuilder sb = new StringBuilder();
        /* recorriendo las columnas de la primera fila */
        for (String string : record) {
            if (string.indexOf(',') > 0) {
                sb.append('"').append(string).append('"').append(',');
            } else {
                sb.append(string).append(',');
            }
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        log.info("{} NUM OF COLUMS {}", getStringLog(), record.size());
        log.info("{} FIRST LINE {}", getStringLog(), sb);
        agenda.setFirstLine(sb.toString());
        return sizeOfFirsLine;
    }

    private static char findSeparatorIfThereIsAny(char separatorChar, BufferedReader br) throws IOException {
        while (Character.isDigit(separatorChar)) {
            separatorChar = (char) br.read();
        }
        return separatorChar;
    }

    public boolean validatePhoneNumber(String number) {
        if (number == null) {
            return false;
        }
        Matcher m = regexPattern.matcher(number);
        return m.matches();
    }

    @Deprecated
    public void logLine(int line, String msg) {
        if (line < 1) {
            agendaLog.add("[Linea: ---] --- " + msg);
        } else {
            agendaLog.add("[Linea: " + line + "] --- " + msg);
        }

    }

    private void updateAgendaStatus(Agenda.Status status) {
        agenda.setStatus(status);
        agenda = agenda_service.updateState(null, agenda);
    }

    // Añade una linea de informacion al log de errores
    public void logLine(long line, String msg) {
        if (line < 1) {
            agendaLog.add("[Linea: ---] --- " + msg);
        } else {
            agendaLog.add("[Linea: " + line + "] --- " + msg);
        }

    }

    public UtilDto findFilterWords(String text) throws IOException {
        OWebClient<UtilDto> we = new OWebClient<>(webClient, UtilDto.class);
        Mono<UtilDto> monoOResponse;
        try {
            log.info("Llamando: [{}]", String.format("%sorinoco-admin/ws/data/filterword/validate/%s", properties.getOrinocoHost(), text));
            monoOResponse = we.getMonoOResponse(properties.getOrinocoHost() + "orinoco-admin/ws/data/filterword/validate/" + text);
            return monoOResponse.block();
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
    }

    private String getStringLog() {
        return "[ODASH] [" + userEmail + "] [AGENDA] [" + agenda.getName() + "]";
    }
}
