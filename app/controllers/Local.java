package controllers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.VmdbLocal;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;
@With({Security.class,Secure.class})
public class Local extends Controller {

	public static void index() throws SQLException {
		String cadena = "";
		List<VmdbLocal> listLocal = VmdbLocal.listLocal(cadena);
		Map scache = new HashMap();
		scache.put("listLocal", listLocal);
		Cache.set("session_listLocal" + session.getId(), scache, "60mn");
		render("Mantenimientos/local.html",listLocal);
    }
	
	public static void paginacion(int pag) {
		Map cacheLocal = Cache.get("session_listLocal"+session.getId(),Map.class);
		List<VmdbLocal> listLocal = (List<VmdbLocal>)cacheLocal.get("listLocal");
        render("Mantenimientos/localPaginacion.html",listLocal,pag);
    }
	
	public static void buscar(String txtNombreBuscar){
		List<VmdbLocal> listLocal = VmdbLocal.listLocal(txtNombreBuscar);	
		render("Mantenimientos/localPaginacion.html",listLocal);
    }
	
	public static void guardarLocal(VmdbLocal local){
		String usuario = session.get("usuario");
    	renderJSON(VmdbLocal.guardarLocal(local,usuario));
    }
	
	public static void eliminar(Long id){
		String usuario = session.get("usuario");
    	renderJSON(VmdbLocal.eliminar(id, usuario));
    }

}
