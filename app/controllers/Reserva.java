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

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import models.VmdbCalendario;
import models.VmdbContacto;
import models.VmdbDetalleReserva;
import models.VmdbGerencia;
import models.VmdbPersona;
import models.VmdbReserva;
import models.VmdbSala;
import models.VmdbTipoEvento;
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
		List<VmdbTipoEvento> listTipoEvento = VmdbTipoEvento.listTipoEvento(cadena);
		List<VmdbReserva> listReserva = VmdbReserva.listReserva(cadena);
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
		List<VmdbReserva> listReserva = VmdbReserva.listReserva(txtNombreBuscar);		
		render("Mantenimientos/reservaPaginacion.html",listReserva);
    }
	
	public static void guardarReserva(VmdbReserva reserva, String cadFechas) throws ParseException, EmailException{
		Map result = new HashMap();
		result.put("status", 0);
		result.put("message", "Error en el Servidor");
		String usuario = session.get("usuario");
		String idGerencia = session.get("idGerencia");
		String emailUser = session.get("email");
		if(reserva.getCoReserva()==null){
			VmdbGerencia objGerencia = VmdbGerencia.findById(Long.parseLong(idGerencia));
			reserva.setVmdbGerencia(objGerencia);
			reserva.setCoUsuarioCreacion(usuario);    		
			reserva.setDaFechaCreacion(new Date());  
    		result.put("status", 1);
			result.put("message", "Se guardo correctamente");
			/**Enviar mail invitacion----------------------------**/
			HtmlEmail email = new HtmlEmail();			
			email.setFrom(Play.configuration.getProperty("mail.smtp.user"),"Notificación de Invitación");
			String [] arrayMail = reserva.getDeInvitados().split(",");
			for(int i = 0 ; i < arrayMail.length ; i++){
				email.addTo(arrayMail[i]);
			}
			email.addTo(emailUser);
			email.setSubject("Invitación");
			email.setHtmlMsg("Mensaje");
			email.addHeader("X-Priority","1");
			Mail.send(email);			
			/**--------------------------------------------------**/
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
            	lista.add(detalle); 
            	int band = reservarSala(reserva.getVmdbSala().getCoSala(), reserva.getVmdbGerencia().getCoGerencia(), fechaD, desdeD, hastaD);
            	System.out.println(band);
        	}	
        	reserva.setVmdbDetalleReservas(lista);        	
    	}    	
		reserva.save(); 
    	renderJSON(result);
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
	
	public static void buscarReservaById(Long id) throws SQLException{
		/*List<Object> json = new ArrayList<Object>();
		JSONSerializer mapeo  = new JSONSerializer();		
		List<Map> pantilla = VmdbPlantilla.buscarPlantillaById(id);		
		if(pantilla.size() > 0){
			json.add(pantilla);			
			List<Map> detallePlantilla = VmdbPlantilla.listarDetalleDePlantilla(id);
			if(detallePlantilla.size() > 0){
				json.add(detallePlantilla);
			}
		}		
		renderJSON(mapeo.serialize(json));*/
	}
	
	public static void eliminar(Long id){
		/*String usuario = session.get("usuario");
    	renderJSON(VmdbPlantilla.eliminar(id, usuario));*/
    }
	
	public static void eliminarProductoDeLaPlantillaById(Long id){//eliminarProductoDelCompuestoById
		/*String usuario = session.get("usuario");
    	renderJSON(VmdbDetallePlantilla.eliminarProductoDeLaPlantillaById(id, usuario));*/
    }
	
	public static void cargarInformacionSala(String coSala) {
		Map result = new HashMap();		
    	List<VmdbSala> infoSala = VmdbSala.find("coSala = ? and stSala = ?", Long.parseLong(coSala), '1').fetch(); 
    	result.put("aforo", infoSala.get(0).getAforo());
    	result.put("deUbicacion", infoSala.get(0).getDeUbicacion());
    	result.put("deDimension", infoSala.get(0).getDeDimension());
    	renderJSON(result);
    }
	
	public static void cargarUnavailableDates(Long coSala, int desdej, int hastaj, int diferenciaj) throws SQLException {
		List<Map> result = new ArrayList<Map>();
		List<Map> listaFechas = fechasDisponibles(coSala);
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
	
	public static List<Map> fechasDisponibles(Long coSala) throws SQLException{ 
		Connection con = DB.getConnection();
		StringBuilder query = new StringBuilder();
		query.append("select DISTINCT(de_fecha) as FECHA ");
		query.append("from stdb_calendario ");
		query.append("where st_calendario = '1' and co_sala = ? ");
		PreparedStatement pr = con.prepareStatement(query.toString());
		pr.setLong(1,coSala);		
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

}
