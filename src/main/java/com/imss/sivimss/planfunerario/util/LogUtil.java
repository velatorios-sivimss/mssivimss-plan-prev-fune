package com.imss.sivimss.planfunerario.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.imss.sivimss.planfunerario.model.request.UsuarioDto;

@Component
public class LogUtil {
    @Value("${ruta-log}")
    private String rutaLog;
    @Value("${spring.application.name}")
    private String nombreAplicacion;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LogUtil.class);


    public void crearArchivoLog(String tipoLog, String origen, String clasePath, String mensaje, String tiempoEjecucion, Authentication authentication) throws IOException {
        Gson json = new Gson();
        UsuarioDto usuarioDto = json.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
        DateFormat formatoFechaLog = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        DateFormat formatoFecha = new SimpleDateFormat("ddMMyyyy");
        File archivo = new File(rutaLog + nombreAplicacion + formatoFecha.format(new Date()) + ".log");
    	FileWriter escribirArchivo = new FileWriter(archivo,true);
        try {
            escribirArchivo.write("" + formatoFechaLog.format(new Date()) + " --- [" + tipoLog +"] " +  origen + " " +clasePath + " : " + mensaje + " , Usuario: " + usuarioDto.getCveUsuario() + " - " + tiempoEjecucion );
            escribirArchivo.write("\r\n");
            escribirArchivo.close();
        }catch(Exception e) {
        	log.error("No se puede escribir el log.");
            log.error(e.getMessage());
        }finally {
       	 escribirArchivo.close();
       }
    }

}
