package controllers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.VmdbLocal;
import models.VmdbMaterial;
import models.VmdbTipoEvento;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;
@With({Security.class,Secure.class})
public class Material extends Controller {

	public static void index() throws SQLException {
		String cadena = "";
		List<VmdbMaterial> listMaterial = VmdbMaterial.listMaterial(cadena);
		Map scache = new HashMap();
		scache.put("listMaterial", listMaterial);
		Cache.set("session_listMaterial" + session.getId(), scache, "60mn");
		render("Mantenimientos/material.html",listMaterial);
    }
	
	public static void paginacion(int pag) {
		Map cacheMaterial = Cache.get("session_listMaterial"+session.getId(),Map.class);
		List<VmdbMaterial> listMaterial = (List<VmdbMaterial>)cacheMaterial.get("listMaterial");
        render("Mantenimientos/materialPaginacion.html",listMaterial,pag);
    }
	
	public static void buscar(String txtNombreBuscar){
		List<VmdbMaterial> listMaterial = VmdbMaterial.listMaterial(txtNombreBuscar);	
		render("Mantenimientos/materialPaginacion.html",listMaterial);
    }
	
	public static void guardarMaterial(VmdbMaterial material){
		String usuario = session.get("usuario");
    	renderJSON(VmdbMaterial.guardarMaterial(material,usuario));
    }
	
	public static void eliminar(Long id){
		String usuario = session.get("usuario");
    	renderJSON(VmdbMaterial.eliminar(id, usuario));
    }

}
