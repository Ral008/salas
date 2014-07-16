package controllers;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.VmdbCalendario;
import models.VmdbLocal;
import models.VmdbSala;
import play.cache.Cache;
import play.db.DB;
import play.mvc.Controller;
import play.mvc.With;
@With({Security.class,Secure.class})
public class Calendario extends Controller {

	public static void index() throws SQLException {
		String cadena = "";
		List<VmdbCalendario> listCalendario = VmdbCalendario.listCalendario(cadena);
		List<VmdbSala> listSala = VmdbSala.find("UPPER(deNombre) like ? and stSala = ? order by deNombre asc", "%"+cadena+"%", '1').fetch();
		Map scache = new HashMap();
		scache.put("listCalendario", listCalendario);
		Cache.set("session_listCalendario" + session.getId(), scache, "60mn");
		render("Mantenimientos/calendario.html",listCalendario,listSala);
    }
	
	public static void paginacion(int pag) {
		Map cacheCalendario = Cache.get("session_listCalendario"+session.getId(),Map.class);
		List<VmdbCalendario> listCalendario = (List<VmdbCalendario>)cacheCalendario.get("listCalendario");
        render("Mantenimientos/calendarioPaginacion.html",listCalendario,pag);
    }
	
	public static void buscar(String txtNombreBuscar){
		List<VmdbCalendario> listCalendario = VmdbCalendario.listCalendario(txtNombreBuscar);	
		render("Mantenimientos/calendarioPaginacion.html",listCalendario);
    }
	
	public static void guardarCalendario(VmdbCalendario calendario){
		String usuario = session.get("usuario");
    	renderJSON(VmdbCalendario.guardarCalendario(calendario,usuario));
    }
	
	public static void eliminar(Long id){
		String usuario = session.get("usuario");
    	renderJSON(VmdbCalendario.eliminar(id, usuario));
    }
	
	public static void viewCalendarioFechas() {
		String cadena = "";		
		List<VmdbSala> listSala = VmdbSala.find("UPPER(deNombre) like ? and stSala = ? order by deNombre asc", "%"+cadena+"%", '1').fetch();
		render("Mantenimientos/calendarioFechasBloque.html",listSala);
    }
	
	public static void generarFechasDelCalendario(String coSala, String deFechaInicial, String deFechaFinal) throws SQLException {
		Connection con = DB.getConnection();
		CallableStatement cs = null;
		cs = con.prepareCall("{call INSERTAR_DIAS(?,?,?,?)}");
		cs.setString(1, deFechaInicial);
		cs.setString(2, deFechaFinal);
		cs.setLong(3, Long.parseLong(coSala));
		cs.registerOutParameter(4, java.sql.Types.VARCHAR);
		cs.execute();
		String salaId = cs.getString(4);
		System.out.println("Sala ID : "+salaId);
		cs.close();
		con.close();				
		Map result = new HashMap();		    	
    	result.put("message", "La fecha fueron generadas correctamente desde "+deFechaInicial+" al "+deFechaFinal);
    	renderJSON(result);
    }

}
