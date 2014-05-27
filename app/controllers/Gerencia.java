package controllers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.VmdbGerencia;
import models.VmdbLocal;
import models.VmdbMaterial;
import models.VmdbTipoEvento;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;
@With({Security.class,Secure.class})
public class Gerencia extends Controller {

	public static void index() throws SQLException {
		String cadena = "";
		List<VmdbGerencia> listGerencia = VmdbGerencia.listGerencia(cadena);
		Map scache = new HashMap();
		scache.put("listGerencia", listGerencia);
		Cache.set("session_listGerencia" + session.getId(), scache, "60mn");
		render("Mantenimientos/gerencia.html",listGerencia);
    }
	
	public static void paginacion(int pag) {
		Map cacheGerencia = Cache.get("session_listGerencia"+session.getId(),Map.class);
		List<VmdbGerencia> listGerencia = (List<VmdbGerencia>)cacheGerencia.get("listGerencia");
        render("Mantenimientos/gerenciaPaginacion.html",listGerencia,pag);
    }
	
	public static void buscar(String txtNombreBuscar){
		List<VmdbGerencia> listGerencia = VmdbGerencia.listGerencia(txtNombreBuscar);	
		render("Mantenimientos/gerenciaPaginacion.html",listGerencia);
    }
	
	public static void guardarGerencia(VmdbGerencia gerencia){
		String usuario = session.get("usuario");
    	renderJSON(VmdbGerencia.guardarGerencia(gerencia,usuario));
    }
	
	public static void eliminar(Long id){
		String usuario = session.get("usuario");
    	renderJSON(VmdbGerencia.eliminar(id, usuario));
    }

}
