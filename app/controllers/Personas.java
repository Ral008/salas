package controllers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

import models.VmdbGerencia;
import models.VmdbPersona;
import models.VmdbTipoPersona;
import play.Play;
import play.cache.Cache;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.jpa.Blob;
import play.mvc.Controller;
import play.mvc.With;
import service.ServicePersonas;

@With({Security.class,Secure.class})
public class Personas extends Controller {	

	public static void index() {
		Cache.delete("session_foto_"+session.getId());
		List<VmdbTipoPersona> listTipoPersona = ServicePersonas.listTipoPersona();
		List<VmdbPersona> listPersona = ServicePersonas.listPersona();
		List<VmdbGerencia> listGerencia = VmdbGerencia.listGerencia("");
    	Map scache = new HashMap();
		scache.put("listPersona", listPersona);
		Cache.set("session_listPersona" + session.getId(), scache, "60mn");
		render("Mantenimientos/personas.html",listTipoPersona,listPersona,listGerencia);
    }
	
	public static void paginacion(int pag) {
		Map cachePersona = Cache.get("session_listPersona"+session.getId(),Map.class);
		List<VmdbPersona> listPersona=(List<VmdbPersona>)cachePersona.get("listPersona");
        render("Mantenimientos/personasPaginacion.html",listPersona,pag);
    }

	public static void buscarPersona(String buscar){
		Cache.delete("session_foto_"+session.getId());
		List<VmdbPersona> listPersona=ServicePersonas.findPersona(buscar);
    	Map scache = new HashMap();
		scache.put("listPersona", listPersona);
		Cache.set("session_listPersona" + session.getId(), scache, "60mn");
		render("Mantenimientos/personasPaginacion.html",listPersona);
    }
    
    public static void guardarFoto(Blob foto) throws IOException{
    	renderJSON(ServicePersonas.saveFoto(foto,session.getId()));
    }

    public static void verFoto(String deFoto) throws IOException{
    	if(deFoto==null){
    		Map cacheUser = Cache.get("session_foto_"+session.getId(),Map.class);
    		Blob foto = (Blob)cacheUser.get("foto");
    		response.setContentTypeIfNotSet(foto.type());
    		renderBinary(foto.get());
    	}else{
    		File outputfile = new File(Play.applicationPath.getPath()+"/public/photos/"+deFoto);
    		JFileChooser view = new JFileChooser();
    		String ext = view.getTypeDescription(outputfile);
    		response.setContentTypeIfNotSet(ext);
    		renderBinary(outputfile);
    	}
    }
    
    public static void guardar(VmdbPersona persona) throws IOException {
    	String usuario = session.get("usuario");
    	renderJSON(ServicePersonas.save(persona, session.getId(), usuario));
    }
    
    public static void eliminar(Long id){
    	String usuario = session.get("usuario");
    	renderJSON(ServicePersonas.eliminarPersona(id,usuario));
    } 
    
}
