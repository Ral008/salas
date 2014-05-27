package controllers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.VmdbContacto;
import models.VmdbGerencia;
import models.VmdbLocal;
import models.VmdbMaterial;
import models.VmdbTipoEvento;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;
@With({Security.class,Secure.class})
public class Contacto extends Controller {

	public static void index() throws SQLException {
		String cadena = "";
		String idPersona = session.get("idPersona");
		List<VmdbContacto> listContacto = VmdbContacto.listContacto(cadena,idPersona);
		Map scache = new HashMap();
		scache.put("listContacto", listContacto);
		Cache.set("session_listContacto" + session.getId(), scache, "60mn");
		render("Mantenimientos/contacto.html",listContacto);
    }
	
	public static void paginacion(int pag) {
		Map cacheContacto = Cache.get("session_listContacto"+session.getId(),Map.class);
		List<VmdbContacto> listContacto = (List<VmdbContacto>)cacheContacto.get("listContacto");
        render("Mantenimientos/contactoPaginacion.html",listContacto,pag);
    }
	
	public static void buscar(String txtNombreBuscar){
		String idPersona = session.get("idPersona");
		List<VmdbContacto> listContacto = VmdbContacto.listContacto(txtNombreBuscar,idPersona);	
		render("Mantenimientos/contactoPaginacion.html",listContacto);
    }
	
	public static void guardarContacto(VmdbContacto contacto){
		String usuario = session.get("usuario");
		String idPersona = session.get("idPersona");
    	renderJSON(VmdbContacto.guardarContacto(contacto,usuario,idPersona));
    }
	
	public static void eliminar(Long id){
		String usuario = session.get("usuario");
    	renderJSON(VmdbContacto.eliminar(id, usuario));
    }

}
