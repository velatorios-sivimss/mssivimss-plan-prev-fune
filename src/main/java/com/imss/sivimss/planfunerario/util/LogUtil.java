package com.imss.sivimss.planfunerario.util;

import com.google.gson.Gson;
import com.imss.sivimss.planfunerario.model.request.UsuarioDto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class LogUtil {
    @Value("${ruta-log}")
    private String rutaLog;

    private String formatoFechaLog = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date());

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LogUtil.class);


    public void crearArchivoLog(String tipoLog, String origen, String clasePath, String mensaje, String tiempoEjecucion, Authentication authentication) throws IOException {
        Gson json = new Gson();
        UsuarioDto usuarioDto = json.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
        File archivo = new File(rutaLog + new SimpleDateFormat("ddMMyyyy").format(new Date()) + ".log");
        FileWriter escribirArchivo = new FileWriter(archivo, true);
        try {
            escribirArchivo.write("" + formatoFechaLog + " --- [" + tipoLog + "] " + origen + " " + clasePath + " : " + mensaje + " , Usuario: " + usuarioDto.getCveUsuario() + " - " + tiempoEjecucion);
            escribirArchivo.write("\r\n");
            escribirArchivo.close();
            escribirArchivo.close();
        } catch (Exception e) {
            log.error("No se puede escribir el log.");
            log.error(e.getMessage());
        } finally {
            escribirArchivo.close();
        }

    }

    public void crearArchivoLogDTO(String tipoLog, String origen, String clasePath, String mensaje, String tiempoEjecucion, UsuarioDto usuarioDto) throws IOException {
        File archivo = new File(rutaLog + new SimpleDateFormat("ddMMyyyy").format(new Date()) + ".log");
        FileWriter escribirArchivo = new FileWriter(archivo, true);
        try {
            escribirArchivo.write("" + formatoFechaLog + " --- [" + tipoLog + "] " + origen + " " + clasePath + " : " + mensaje + " , Usuario: " + usuarioDto.getCveUsuario() + " - " + tiempoEjecucion);
            escribirArchivo.write("\r\n");
            escribirArchivo.close();
            escribirArchivo.close();
        } catch (Exception e) {
            log.error("No se puede escribir el log.");
            log.error(e.getMessage());
        } finally {
            escribirArchivo.close();
        }

    }

}
