package com.imss.sivimss.planfunerario.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.imss.sivimss.planfunerario.util.AppConstantes;
import com.imss.sivimss.planfunerario.beans.RenovarBean;
import com.imss.sivimss.planfunerario.service.PagosService;
import com.imss.sivimss.planfunerario.util.LogUtil;
import com.imss.sivimss.planfunerario.util.ProviderServiceRestTemplate;
import com.imss.sivimss.planfunerario.util.QueryHelper;
import com.imss.sivimss.planfunerario.util.Response;

@Service
public class PagosServiceImpl implements PagosService {

	@Value("${endpoints.rutas.dominio-crear}")
	private String urlCrear;
	
	@Value("${endpoints.rutas.dominio-consulta}")
	private String urlConsulta;
	
	@Autowired
	private LogUtil logUtil;
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@SuppressWarnings("unchecked")
	@Override
	public void insertar(RenovarBean renovarBean, Response<?> response, Authentication authentication) throws IOException {
		
		Integer idRegistro = (Integer) response.getDatos();
		Response<?> respuesta;
		Map<String, Object> datos = new HashMap<>();
		String query;
		String nomContratante;
		Double valor;
		Integer idVelatorio;
		List<Map<String, Object>> listadatos;
		
		query = info( renovarBean.getIdConvenioPf() );
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "","CONSULTA " + query, authentication);
		
		datos.put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8)));
		
		respuesta = providerRestTemplate.consumirServicio(datos, urlConsulta,
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(respuesta.getDatos(), Map[].class));
		nomContratante = (String) listadatos.get(0).get("nomContratante");
		valor = (Double) listadatos.get(0).get("valor");
		idVelatorio = (Integer) listadatos.get(0).get("idVelatorio");
		
		query = armarQuery(idRegistro , renovarBean, nomContratante, valor, idVelatorio);
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "","CONSULTA " + query, authentication);
		
		datos.put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8)));
		
		respuesta = providerRestTemplate.consumirServicio(datos, urlCrear,
				authentication);
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "","RESPUESTA " + respuesta, authentication);
	}

	private String armarQuery(Integer idRegistro, RenovarBean renovarBean, String nomContratante, 
			Double valor, Integer idVelatorio) {
		String query;
		QueryHelper q = new QueryHelper("INSERT INTO SVT_PAGO_BITACORA");
		q.agregarParametroValues("ID_REGISTRO", idRegistro.toString());
		q.agregarParametroValues("ID_FLUJO_PAGOS", "3");
		q.agregarParametroValues("ID_VELATORIO", idVelatorio.toString());
		q.agregarParametroValues("FEC_ODS", setValor(renovarBean.getVigencia()));
		q.agregarParametroValues("NOM_CONTRATANTE", setValor(nomContratante));
		q.agregarParametroValues("CVE_FOLIO", setValor(renovarBean.getFolioAdenda()));
		q.agregarParametroValues("IMP_VALOR", setValor(valor.toString()));
		q.agregarParametroValues("CVE_ESTATUS_PAGO", "2");
		q.agregarParametroValues("ID_USUARIO_ALTA", renovarBean.getUsuarioAlta().toString());
		
		query = q.obtenerQueryInsertar();
		
		return query;
	}
	
	private String setValor(String valor) {
		
		if( valor==null || valor.equals("")) {
			return "NULL";
		} else {
			return "'" + valor + "'";
		}
	}
	
	public String info(Integer idConvenioPf){
		
		StringBuilder query = new StringBuilder("SELECT\r\n"
				+ "CONCAT(PER.NOM_PERSONA, ' ',PER.NOM_PRIMER_APELLIDO, ' ', PER.NOM_SEGUNDO_APELLIDO) AS nomContratante,\r\n"
				+ "PAQ.MON_PRECIO AS valor,\r\n"
				+ "CPF.ID_VELATORIO AS idVelatorio\r\n"
				+ "FROM SVT_CONVENIO_PF CPF\r\n"
				+ "INNER JOIN SVT_CONTRA_PAQ_CONVENIO_PF CPAQ ON CPAQ.ID_CONVENIO_PF = CPF.ID_CONVENIO_PF\r\n"
				+ "INNER JOIN SVC_CONTRATANTE CON ON CON.ID_CONTRATANTE = CPAQ.ID_CONTRATANTE\r\n"
				+ "INNER JOIN SVC_PERSONA PER ON PER.ID_PERSONA = CON.ID_PERSONA\r\n"
				+ "INNER JOIN SVT_PAQUETE PAQ ON PAQ.ID_PAQUETE = CPAQ.ID_PAQUETE\r\n"
				+ "WHERE CPF.ID_CONVENIO_PF = ");
		query.append(idConvenioPf);
		
		return query.toString();
	}
}
