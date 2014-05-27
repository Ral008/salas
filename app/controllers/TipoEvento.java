package controllers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.VmdbLocal;
import models.VmdbTipoEvento;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;
@With({Security.class,Secure.class})
public class TipoEvento extends Controller {

	public static void index() throws SQLException {
		String cadena = "";
		List<VmdbTipoEvento> listTipoEvento = VmdbTipoEvento.listTipoEvento(cadena);
		Map scache = new HashMap();
		scache.put("listTipoEvento", listTipoEvento);
		Cache.set("session_listTipoEvento" + session.getId(), scache, "60mn");
		render("Mantenimientos/tipoEvento.html",listTipoEvento);
    }
	
	public static void paginacion(int pag) {
		Map cacheTipoEvento = Cache.get("session_listTipoEvento"+session.getId(),Map.class);
		List<VmdbTipoEvento> listTipoEvento = (List<VmdbTipoEvento>)cacheTipoEvento.get("listTipoEvento");
        render("Mantenimientos/tipoEventoPaginacion.html",listTipoEvento,pag);
    }
	
	public static void buscar(String txtNombreBuscar){
		List<VmdbTipoEvento> listTipoEvento = VmdbTipoEvento.listTipoEvento(txtNombreBuscar);	
		render("Mantenimientos/tipoEventoPaginacion.html",listTipoEvento);
    }
	
	public static void guardarTipoEvento(VmdbTipoEvento tipoEvento){
		String usuario = session.get("usuario");
    	renderJSON(VmdbTipoEvento.guardarTipoEvento(tipoEvento,usuario));
    }
	
	public static void eliminar(Long id){
		String usuario = session.get("usuario");
    	renderJSON(VmdbTipoEvento.eliminar(id, usuario));
    }

}
