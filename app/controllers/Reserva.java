package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import models.VmdbCalendario;
import models.VmdbContacto;
import models.VmdbDetalleReserva;
import models.VmdbGerencia;
import models.VmdbPersona;
import models.VmdbReserva;
import models.VmdbSala;
import models.VmdbTipoEvento;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import play.Play;
import play.cache.Cache;
import play.db.DB;
import play.libs.Mail;
import play.mvc.Controller;
import play.mvc.With;
import flexjson.JSONSerializer;
@With({Security.class,Secure.class})
public class Reserva extends Controller {

	public static void index() throws SQLException {
		String cadena = "";
		String idPersona = session.get("idPersona");
		List<VmdbTipoEvento> listTipoEvento = VmdbTipoEvento.listTipoEvento(cadena);
		List<VmdbReserva> listReserva = VmdbReserva.listReserva(cadena,idPersona);
		Map scache = new HashMap();
		scache.put("listReserva", listReserva);
		Cache.set("session_listReserva" + session.getId(), scache, "60mn");
		render("Mantenimientos/reserva.html",listTipoEvento,listReserva);
    }
	
	public static void paginacion(int pag) {
		Map cacheReserva = Cache.get("session_listReserva"+session.getId(),Map.class);
		List<VmdbReserva> listReserva = (List<VmdbReserva>)cacheReserva.get("listReserva");
        render("Mantenimientos/reservaPaginacion.html",listReserva,pag);
    }	
	
	public static void buscar(String txtNombreBuscar){
		String idPersona = session.get("idPersona");
		List<VmdbReserva> listReserva = VmdbReserva.listReserva(txtNombreBuscar,idPersona);		
		render("Mantenimientos/reservaPaginacion.html",listReserva);
    }
	
	public static void guardarReserva(VmdbReserva reserva, String cadFechas) throws ParseException, EmailException{
		Map result = new HashMap();
		result.put("status", 0);
		result.put("message", "Error en el Servidor");
		String usuario = session.get("usuario");
		String idGerencia = session.get("idGerencia");
		String emailUser = session.get("email");
		String idPersona = session.get("idPersona");
		if(reserva.getCoReserva()==null){
			VmdbGerencia objGerencia = VmdbGerencia.findById(Long.parseLong(idGerencia));
			reserva.setVmdbGerencia(objGerencia);
			VmdbPersona objPersona = VmdbPersona.findById(Long.parseLong(idPersona));
			reserva.setVmdbPersona(objPersona);
			reserva.setCoUsuarioCreacion(usuario);    		
			reserva.setDaFechaCreacion(new Date());  
    		result.put("status", 1);
			result.put("message", "Se guardo correctamente");
			/**mail invitation before**/
    	}else{
    		reserva.setCoUsuarioModificacion(usuario);
    		reserva.setDaFechaModificacion(new Date());
    		result.put("status", 2);
			result.put("message", "Se actualizo correctamente");
    	} 
		if(!cadFechas.equalsIgnoreCase("")){
    		HashSet<VmdbDetalleReserva> lista = new HashSet<VmdbDetalleReserva>(0);
    		String [] arrayFecha = cadFechas.split("@"); //[0]    		
        	for(int i = 0 ; i < arrayFecha.length ; i++){
        		VmdbDetalleReserva detalle = new VmdbDetalleReserva();              		
            	String [] fechaObj = arrayFecha[i].split(",");   
            	String fechaD = fechaObj[0];//fecha
            	String desdeD = fechaObj[1];//desde
            	String hastaD = fechaObj[2];//hasta            	
            	detalle.setVmdbReserva(reserva);            	
            	detalle.setDeFecha(fechaD);
            	detalle.setHoraDesde(desdeD);
            	detalle.setHoraHasta(hastaD);
            	detalle.setCoUsuarioCreacion(usuario);    		
            	detalle.setDaFechaCreacion(new Date());
            	detalle.setStDetalleReserva('1');  
            	detalle.setStEnviado('0');
            	lista.add(detalle); 
            	int band = reservarSala(reserva.getVmdbSala().getCoSala(), reserva.getVmdbGerencia().getCoGerencia(), fechaD, desdeD, hastaD);
            	System.out.println(band);
        	}	
        	reserva.setVmdbDetalleReservas(lista);        	
    	}    	
		reserva.save(); 
		/**mail invitation now**/
		int band = enviarMailInvitacion(reserva, emailUser);
		if(band == 1){
			System.out.println("send mail over");
		}
		/**--------------------------------------**/
    	renderJSON(result);
    }
	
	public static int enviarMailInvitacion(VmdbReserva reserva, String emailUser) throws EmailException{
		int band = 0;
		StringBuilder htmlEmailTemplate = new StringBuilder();
		htmlEmailTemplate.append("<html>");
		htmlEmailTemplate.append("<head>");
		htmlEmailTemplate.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />");
		htmlEmailTemplate.append("</head>");
		htmlEmailTemplate.append("<body>");
		htmlEmailTemplate.append("<div>");
		htmlEmailTemplate.append("<table>");
		htmlEmailTemplate.append("<tr>");
		htmlEmailTemplate.append("<td style='font-weight:bold'><h2>"+reserva.getDeEvento()+"</h2></td>");
		htmlEmailTemplate.append("</tr>");
		htmlEmailTemplate.append("</table>");
		htmlEmailTemplate.append("<table>");
		htmlEmailTemplate.append("<tr>");
		htmlEmailTemplate.append("<td style='font-weight:bold'>Sala</td>");
		htmlEmailTemplate.append("<td width='200'>:  "+reserva.getVmdbSala().getDeNombre()+"</td>");
		htmlEmailTemplate.append("<td></td>");
		htmlEmailTemplate.append("<td></td>");
		htmlEmailTemplate.append("</tr>");
		htmlEmailTemplate.append("<tr>");
		htmlEmailTemplate.append("<td style='font-weight:bold'>Gerencia</td>");
		htmlEmailTemplate.append("<td width='200'>:  "+reserva.getVmdbGerencia().getDeNombre()+"</td>");
		htmlEmailTemplate.append("<td style='font-weight:bold'>Organizador</td>");
		htmlEmailTemplate.append("<td>:  "+reserva.getVmdbPersona().getDeNombre()+"</td>");
		htmlEmailTemplate.append("</tr>");
		htmlEmailTemplate.append("<tr>");
		htmlEmailTemplate.append("<td style='font-weight:bold'>Hora desde</td>");
		htmlEmailTemplate.append("<td width='200'>:  "+reserva.getHoraDesde()+"</td>");
		htmlEmailTemplate.append("<td style='font-weight:bold'>Hora hasta</td>");
		htmlEmailTemplate.append("<td>:  "+reserva.getHoraHasta()+"</td>");
		htmlEmailTemplate.append("</tr>");
		htmlEmailTemplate.append("</table>");
		htmlEmailTemplate.append("<p></p>");
		htmlEmailTemplate.append("<font color='5bb75b' style='font-weight:bold'><i>Días del evento</i></font>");
		htmlEmailTemplate.append("<table border='1' cellpadding='0' cellspacing='0'>");
		htmlEmailTemplate.append("<tr>");
		htmlEmailTemplate.append("<td width='200' height='30' bgcolor='#0088CC' style='font-weight:bold;text-align:center'><font color='FFFFFF'>FECHA</font></td>");
		htmlEmailTemplate.append("<td width='200' height='30' bgcolor='#0088CC' style='font-weight:bold;text-align:center'><font color='FFFFFF'>HORA DESDE</font></td>");
		htmlEmailTemplate.append("<td width='200' height='30' bgcolor='#0088CC' style='font-weight:bold;text-align:center'><font color='FFFFFF'>HORA HASTA</font></td>");
		htmlEmailTemplate.append("</tr>");
		for (VmdbDetalleReserva detalleReserva : reserva.getVmdbDetalleReservas()) {
			if(detalleReserva.getStDetalleReserva().equals('1')){
				htmlEmailTemplate.append("<tr>");
				htmlEmailTemplate.append("<td height='30' style='text-align:center'>"+detalleReserva.getDeFecha()+"</td>");
				htmlEmailTemplate.append("<td height='30' style='text-align:center'>"+detalleReserva.getHoraDesde()+"</td>");
				htmlEmailTemplate.append("<td height='30' style='text-align:center'>"+detalleReserva.getHoraHasta()+"</td>");
				htmlEmailTemplate.append("</tr>");
			}
		}			
		htmlEmailTemplate.append("</table>");
		htmlEmailTemplate.append("</div>");
		htmlEmailTemplate.append("</body>");
		htmlEmailTemplate.append("<p></p>");
		htmlEmailTemplate.append("<p></p>");
		htmlEmailTemplate.append("<font><i>You are requested to participate in the meeting.</i></font>");
		htmlEmailTemplate.append("</html>");
		/**Enviar mail invitacion----------------------------**/
		HtmlEmail email = new HtmlEmail();			
		email.setFrom(Play.configuration.getProperty("mail.smtp.user"),"Notificación de Invitación");
		String [] arrayMail = reserva.getDeInvitados().split(",");
		for(int i = 0 ; i < arrayMail.length ; i++){
			email.addTo(arrayMail[i]);
		}
		email.addTo(emailUser);
		email.setSubject("Convocatoria de reunión");
		email.setHtmlMsg(htmlEmailTemplate.toString());
		//set the alternative message
	    email.setTextMsg("Your email client does not support HTML messages");
		Mail.send(email);	
		band = 1;
		return band;		
		/**--------------------------------------------------**/		
	}
	
	public static int reservarSala(Long coSala, Long coGerencia, String fecha, String hDesde, String hHasta) {
		int band = 0;
		String usuario = session.get("usuario");
		int n1 = hDesde.indexOf(":");
		int desdeIn = Integer.parseInt(hDesde.substring(0, n1));
		int n2 = hHasta.indexOf(":");
		int hastaIn = Integer.parseInt(hHasta.substring(0, n2));
		String hora = "";
		for (int i = desdeIn; i < hastaIn; i++) {
			hora = i+":00";
			VmdbCalendario objCalendario = VmdbCalendario.find("vmdbSala.coSala = ? and deFecha = ? and hora = ? and stCalendario = ?", coSala,fecha,hora,'1').first();
			VmdbGerencia objGerencia = VmdbGerencia.findById(coGerencia);
			objCalendario.setVmdbGerencia(objGerencia);
        	objCalendario.setStCalendario('3');
        	objCalendario.setCoUsuarioCreacion(usuario);    		
        	objCalendario.setDaFechaCreacion(new Date());
        	objCalendario.save();
        	band = 1;
		}
		return band;
	}
	
	public static void cargarInformacionSala(String coSala) {
		Map result = new HashMap();		
    	List<VmdbSala> infoSala = VmdbSala.find("coSala = ? and stSala = ?", Long.parseLong(coSala), '1').fetch(); 
    	result.put("aforo", infoSala.get(0).getAforo());
    	result.put("deUbicacion", infoSala.get(0).getDeUbicacion());
    	result.put("deDimension", infoSala.get(0).getDeDimension());
    	renderJSON(result);
    }
	
	public static void cargarUnavailableDates(Long coSala, int desdej, int hastaj, int diferenciaj, String fechaDesdej, String fechaHastaj) throws SQLException {
		List<Map> result = new ArrayList<Map>();
		List<Map> listaFechas = fechasDisponibles(coSala,fechaDesdej,fechaHastaj);
		String fechaDis = "";
		String hora = "";
		int s = 0;
		for (Map mapFecha : listaFechas) {
			fechaDis = mapFecha.get("fecha").toString();			
			for (int i = desdej; i < hastaj; i++) {
				if(i>11){
					hora = i+":00";//PM
				}else{
					hora = i+":00";//AM
				}
				VmdbCalendario objCalendario = VmdbCalendario.find("vmdbSala.coSala = ? and deFecha = ? and hora = ? and stCalendario = ? ", coSala,fechaDis,hora,'1').first();
				if(objCalendario!=null){
					s++;
				}
			}
			if(s==diferenciaj){
				Map map = new HashMap();
				map.put("fecha", fechaDis);
				result.add(map);
			}	
			s=0;
		}
		JSONSerializer mapeo = new JSONSerializer();		
    	renderJSON(mapeo.serialize(result));	
	}
	
	/**Dias disponibles pp**/
	public static void cargarEnableDates(Long coSala, int desdej, int hastaj, int diferenciaj, String fechaDesdej, String fechaHastaj) throws SQLException {
		//System.out.println(diferenciaj);
		List<Map> result = new ArrayList<Map>();
		List<Map> listaFechas = fechasDisponibles(coSala,fechaDesdej,fechaHastaj);
		String fechaDis = "";
		String hora = "";
		int s = 0;
		for (Map mapFecha : listaFechas) {
			fechaDis = mapFecha.get("fecha").toString();			
			for (int i = desdej; i < hastaj; i++) {
				if(i>11){
					hora = i+":00";//PM
				}else{
					hora = i+":00";//AM
				}
				VmdbCalendario objCalendario = VmdbCalendario.find("vmdbSala.coSala = ? and deFecha = ? and hora = ? and stCalendario = ? ", coSala,fechaDis,hora,'1').first();
				if(objCalendario!=null){
					Map map = new HashMap();
					map.put("fecha", fechaDis);
					result.add(map);
				}
			}						
		}
		JSONSerializer mapeo = new JSONSerializer();		
    	renderJSON(mapeo.serialize(result));	
	}
	
	public static List<Map> fechasDisponibles(Long coSala, String fechaIni, String fechaFin) throws SQLException{ 
		Connection con = DB.getConnection();
		//System.out.println(fechaIni+" "+fechaFin);
		StringBuilder query = new StringBuilder();
		query.append("select DISTINCT(de_fecha) as FECHA ");
		query.append("from stdb_calendario ");
		query.append("where st_calendario = '1' and co_sala = ? and "
				+ "TO_DATE(de_fecha, 'dd/mm/yyyy') >= TO_DATE(?, 'dd/mm/yyyy') and TO_DATE(de_fecha, 'dd/mm/yyyy') <= TO_DATE(?, 'dd/mm/yyyy')");
		PreparedStatement pr = con.prepareStatement(query.toString());
		pr.setLong(1,coSala);	
		pr.setString(2,fechaIni);	
		pr.setString(3,fechaFin);	
		ResultSet rs = pr.executeQuery();
		List<Map> result = new ArrayList<Map>();
		Map map = null;
		while(rs.next()){
			map = new HashMap();
			map.put("fecha", rs.getString("FECHA"));			
			result.add(map);
		}	
		rs.close();
		pr.close();
		con.close();
		return result;		
	}
	
	public static List<Map> fechasNoDisponibles(Long coSala, String fechaIni, String fechaFin) throws SQLException{ 
		Connection con = DB.getConnection();
		System.out.println(fechaIni+" "+fechaFin);
		StringBuilder query = new StringBuilder();
		query.append("select DISTINCT(de_fecha) as FECHA ");
		query.append("from stdb_calendario ");
		query.append("where st_calendario = '3' and co_sala = ? and "
				+ "TO_DATE(de_fecha, 'dd/mm/yyyy') >= TO_DATE(?, 'dd/mm/yyyy') and TO_DATE(de_fecha, 'dd/mm/yyyy') <= TO_DATE(?, 'dd/mm/yyyy')");
		PreparedStatement pr = con.prepareStatement(query.toString());
		pr.setLong(1,coSala);	
		pr.setString(2,fechaIni);	
		pr.setString(3,fechaFin);	
		ResultSet rs = pr.executeQuery();
		List<Map> result = new ArrayList<Map>();
		Map map = null;
		while(rs.next()){
			map = new HashMap();
			map.put("fecha", rs.getString("FECHA"));			
			result.add(map);
		}	
		rs.close();
		pr.close();
		con.close();
		return result;		
	}
	
	public static void cargarSalasDisponibles(int desdej, int hastaj, int diferenciaj, String fechaDesdej, String fechaHastaj) throws SQLException {
		List<Map> result = new ArrayList<Map>();
		List<Map> listaPosiblesSalas = posiblesSalasDisponibles(fechaDesdej,fechaHastaj);
		String fechaDis = "";
		String hora = "";
		int s = 0, v = 0;
		for (Map mapps : listaPosiblesSalas) {
			List<Map> listaFechas = fechasDisponiblesByRango(Long.parseLong(mapps.get("co_sala").toString().toString()),fechaDesdej,fechaHastaj);
			for (Map mapFecha : listaFechas) {
				s=0;
				fechaDis = mapFecha.get("fecha").toString();			
				for (int i = desdej; i < hastaj; i++) {
					hora = i+":00";					
					VmdbCalendario objCalendario = VmdbCalendario.find("vmdbSala.coSala = ? and deFecha = ? and hora = ? and stCalendario = ? ", Long.parseLong(mapps.get("co_sala").toString().toString()),fechaDis,hora,'1').first();
					if(objCalendario!=null){
						s++;
					}
				}
				if(s==diferenciaj){
					v=s;
					break;
				}					
			}
			if(v==diferenciaj){
				Map map = new HashMap();
				VmdbSala sala = VmdbSala.find("coSala = ? and stSala = ? ", Long.parseLong(mapps.get("co_sala").toString().toString()), '1').first();
				map.put("coSala", mapps.get("co_sala").toString().toString());
				map.put("deNombre", sala.getDeNombre());
				map.put("aforo", sala.getAforo());
				map.put("ubicacion", sala.getDeUbicacion());
				result.add(map);	
			}
			v=0;
		}						
		JSONSerializer mapeo = new JSONSerializer();		
    	renderJSON(mapeo.serialize(result));	
	}
	
	/**METODO DE SALAS DISPONIBLES pp**/
	/*public static void cargarSalasDisponibles(int desdej, int hastaj, int diferenciaj, String fechaDesdej, String fechaHastaj) throws SQLException {
		List<Map> result = new ArrayList<Map>();
		Map mapKeys = new HashMap();
		List<Map> listaPosiblesSalas = posiblesSalasDisponibles(fechaDesdej,fechaHastaj);
		String fechaDis = "";
		String hora = "";
		for (Map mapps : listaPosiblesSalas) {
			List<Map> listaFechas = fechasDisponiblesByRango(Long.parseLong(mapps.get("co_sala").toString().toString()),fechaDesdej,fechaHastaj);
			for (Map mapFecha : listaFechas) {
				fechaDis = mapFecha.get("fecha").toString();			
				for (int i = desdej; i < hastaj; i++) {
					hora = i+":00";					
					VmdbCalendario objCalendario = VmdbCalendario.find("vmdbSala.coSala = ? and deFecha = ? and hora = ? and stCalendario = ? ", Long.parseLong(mapps.get("co_sala").toString().toString()),fechaDis,hora,'1').first();
					if(objCalendario!=null){
						Map map = new HashMap();
						if(!mapKeys.containsKey("coSala_"+mapps.get("co_sala").toString())){
							System.out.println("sala: "+"coSala_"+mapps.get("co_sala").toString());
							VmdbSala sala = VmdbSala.find("coSala = ? and stSala = ? ", Long.parseLong(mapps.get("co_sala").toString()), '1').first();
							mapKeys.put("coSala_"+mapps.get("co_sala").toString().toString(),"coSala_"+mapps.get("co_sala").toString());
							//map.put("coSala_"+mapps.get("co_sala").toString().toString(),"coSala_"+mapps.get("co_sala").toString());
							map.put("coSala", mapps.get("co_sala").toString().toString());
							map.put("deNombre", sala.getDeNombre());
							map.put("aforo", sala.getAforo());
							map.put("ubicacion", sala.getDeUbicacion());
							result.add(map);
						}						
					}
				}
				
			}
		}						
		JSONSerializer mapeo = new JSONSerializer();		
    	renderJSON(mapeo.serialize(result));	
	}*/
	
	public static List<Map> posiblesSalasDisponibles(String fechaDesde, String fechaHasta) throws SQLException{ 
		Connection con = DB.getConnection();
		StringBuilder query = new StringBuilder();
		query.append("select DISTINCT(co_sala) as SALA ");
		query.append("from stdb_calendario ");
		query.append("where st_calendario = '1' and ");
		query.append("TO_DATE(de_fecha, 'dd/mm/yyyy') >= TO_DATE(?, 'dd/mm/yyyy') and TO_DATE(de_fecha, 'dd/mm/yyyy') <= TO_DATE(?, 'dd/mm/yyyy') ");
		PreparedStatement pr = con.prepareStatement(query.toString());
		pr.setString(1,fechaDesde);
		pr.setString(2,fechaHasta);
		ResultSet rs = pr.executeQuery();
		List<Map> result = new ArrayList<Map>();
		Map map = null;
		while(rs.next()){
			map = new HashMap();
			map.put("co_sala", rs.getString("SALA"));			
			result.add(map);
		}	
		rs.close();
		pr.close();
		con.close();
		return result;		
	}
	
	public static List<Map> fechasDisponiblesByRango(Long coSala,String fechaDesde, String fechaHasta) throws SQLException{ 
		Connection con = DB.getConnection();
		StringBuilder query = new StringBuilder();
		query.append("select DISTINCT(de_fecha) as FECHA ");
		query.append("from stdb_calendario ");
		query.append("where st_calendario = '1' and ");
		query.append("TO_DATE(de_fecha, 'dd/mm/yyyy') >= TO_DATE(?, 'dd/mm/yyyy') and TO_DATE(de_fecha, 'dd/mm/yyyy') <= TO_DATE(?, 'dd/mm/yyyy') ");
		query.append("and co_sala = ? ");
		PreparedStatement pr = con.prepareStatement(query.toString());
		pr.setString(1,fechaDesde);
		pr.setString(2,fechaHasta);
		pr.setLong(3,coSala);
		ResultSet rs = pr.executeQuery();
		List<Map> result = new ArrayList<Map>();
		Map map = null;
		while(rs.next()){
			map = new HashMap();
			map.put("fecha", rs.getString("FECHA"));			
			result.add(map);
		}	
		rs.close();
		pr.close();
		con.close();
		return result;		
	}
	
	public static void listarContacto(String nombre){
		String idPersona = session.get("idPersona");
		List<VmdbContacto> contactos = VmdbContacto.listContacto(nombre,idPersona);
		JSONSerializer mapeo = new JSONSerializer().include("coContacto","deNombre","deCorreo").exclude("*");
    	renderJSON(mapeo.serialize(contactos));
    }
	
	public static void registrarContactoDesdeReserva(String nombreModel, String correoModel){		
		String usuario = session.get("usuario");
		String idPersona = session.get("idPersona");
		Map result = new HashMap();
		result.put("status", 0);
		result.put("message", "Error on Server");
		List<VmdbContacto> list = VmdbContacto.find("vmdbPersona.coPersona = ? and deCorreo = ? and stContacto = ?", Long.parseLong(idPersona),correoModel,'1').fetch();
		if(list.size()>0){
			result.put("status", 3);
			result.put("message", "El contacto ya esta registrado");
		}else{
			VmdbPersona objPersona = VmdbPersona.findById(Long.parseLong(idPersona));
			VmdbContacto objContacto = new VmdbContacto();
			objContacto.setVmdbPersona(objPersona);
			objContacto.setDeNombre(nombreModel);
			objContacto.setDeCorreo(correoModel);
			objContacto.setStContacto('1');
			objContacto.setCoUsuarioCreacion(usuario);
			objContacto.setDaFechaCreacion(new Date());
			objContacto.save();
			result.put("status", 1);
			result.put("message", "El contacto fue agregado correctamente");
		}		
		JSONSerializer mapeo = new JSONSerializer();				
    	renderJSON(mapeo.serialize(result));
    }
	
	public static List<Map> listaDeReservasByPersona(String evento) throws SQLException{ 
		String idPersona = session.get("idPersona");
		Connection con = DB.getConnection();
		StringBuilder query = new StringBuilder();
		query.append("select DISTINCT(r.co_reserva) as reserva, r.de_evento as evento, ");
		query.append("(select s.de_nombre from stdb_sala s where s.co_sala = r.co_sala) as sala, ");
		query.append("r.hora_desde as desde, r.hora_hasta as hasta, ");
		query.append("(CASE ");
		query.append("WHEN r.st_reserva = '1' THEN 'Activo' ");
		query.append("ELSE 'Anulado' ");
		query.append("END) as estado, ");
		query.append("r.info_inicial as inicial, ");
		query.append("r.info_final as final ");
		query.append("from stdb_reserva r, stdd_detalle_reserva dr ");
		query.append("where r.co_reserva = dr.co_reserva ");
		query.append("and dr.st_detalle_reserva = '1' ");
		query.append("and r.st_reserva = '1' ");
		query.append("and r.co_persona = ? ");
		query.append("and to_date(dr.de_fecha, 'dd/mm/yyyy') >= to_date(to_char(sysdate,'dd/mm/yyyy'),'dd/mm/yyyy') ");
		query.append("and upper(r.de_evento) like ? ");
		query.append("order by r.de_evento asc ");
		PreparedStatement pr = con.prepareStatement(query.toString());
		pr.setLong(1,Long.parseLong(idPersona));
		pr.setString(2,"%"+evento.toUpperCase()+"%");	
		ResultSet rs = pr.executeQuery();
		List<Map> result = new ArrayList<Map>();
		Map map = null;
		while(rs.next()){
			map = new HashMap();
			map.put("id", rs.getString("reserva"));
			map.put("evento", rs.getString("evento"));	
			map.put("sala", rs.getString("sala"));	
			map.put("desde", rs.getString("desde"));
			map.put("hasta", rs.getString("hasta"));
			map.put("estado", rs.getString("estado"));
			map.put("inicial", rs.getString("inicial"));
			map.put("final", rs.getString("final"));
			result.add(map);
		}	
		rs.close();
		pr.close();
		con.close();	
		return result;
	}
	
	public static void guardarInfoInicial(Long id, String infoInicial){
		Map result = new HashMap();
		String usuario = session.get("usuario");
		VmdbReserva objReserva = VmdbReserva.findById(id);
		if(objReserva != null){
			objReserva.setInfoInicial(infoInicial);
			objReserva.setCoUsuarioModificacion(usuario);
			objReserva.setDaFechaModificacion(new Date());
			objReserva.save();
			result.put("status",1);
			result.put("message","El informe inicial fue ingresado");
		}else{
			result.put("status",0);
			result.put("message","Ocurrio un error");
		}
		JSONSerializer mapeo = new JSONSerializer();				
    	renderJSON(mapeo.serialize(result));
    }
	
	public static void guardarInfoFinal(Long id, String infoFinal){
		Map result = new HashMap();
		String usuario = session.get("usuario");
		VmdbReserva objReserva = VmdbReserva.findById(id);
		if(objReserva != null){
			objReserva.setInfoFinal(infoFinal);
			objReserva.setCoUsuarioModificacion(usuario);
			objReserva.setDaFechaModificacion(new Date());
			objReserva.save();
			result.put("status",1);
			result.put("message","El informe final fue ingresado");
		}else{
			result.put("status",0);
			result.put("message","Ocurrio un error");
		}
		JSONSerializer mapeo = new JSONSerializer();				
    	renderJSON(mapeo.serialize(result));
    }	
	
	public static void cargarDatosDeSala(String coSala) {
		Map result = new HashMap();		
    	VmdbSala salaObj = VmdbSala.findById(Long.parseLong(coSala)); 
    	result.put("descripcion", salaObj.getDeDescripcion());
		result.put("aviso", salaObj.getDeAviso());		
    	renderJSON(result);
    }
	
	public static void viewFechas(Long coReserva){
		List<Map> result = new ArrayList<Map>();
		List<VmdbDetalleReserva> detalleReserva = VmdbDetalleReserva.find("vmdbReserva.coReserva = ? and stDetalleReserva = ? order by deFecha asc", coReserva,'1').fetch();
		for (VmdbDetalleReserva obj : detalleReserva) {
			Map map = new HashMap();
			map.put("fecha", obj.getDeFecha());
	    	map.put("horaDesde", obj.getHoraDesde());
	    	map.put("horaHasta", obj.getHoraHasta());
	    	result.add(map);
		}				
		JSONSerializer mapeo = new JSONSerializer();		
    	renderJSON(mapeo.serialize(result));		
	}

}
