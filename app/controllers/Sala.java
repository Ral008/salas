package controllers;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import models.VmdbDetalleSala;
import models.VmdbLocal;
import models.VmdbMaterial;
import models.VmdbSala;
import play.Play;
import play.cache.Cache;
import play.db.jpa.Blob;
import play.mvc.Controller;
import play.mvc.With;
import flexjson.JSONSerializer;
@With({Security.class,Secure.class})
public class Sala extends Controller {

	public static void index() throws SQLException {
		Cache.delete("session_foto_"+session.getId());
		String cadena = "";
		List<VmdbSala> listSala = VmdbSala.listSala(cadena);
		List<VmdbLocal> listLocal = VmdbLocal.listLocal(cadena);
		List<VmdbMaterial> listMaterial = VmdbMaterial.listMaterial(cadena);
		Map scache = new HashMap();
		scache.put("listSala", listSala);
		Cache.set("session_listSala" + session.getId(), scache, "60mn");
		render("Mantenimientos/sala.html",listSala,listLocal,listMaterial);
    }
	
	public static void paginacion(int pag) {
		Map cacheSala = Cache.get("session_listSala"+session.getId(),Map.class);
		List<VmdbSala> listSala = (List<VmdbSala>)cacheSala.get("listSala");
        render("Mantenimientos/salaPaginacion.html",listSala,pag);
    }		
	
	public static void buscar(String txtNombreBuscar){
		List<VmdbSala> listSala = VmdbSala.listSala(txtNombreBuscar);		
		render("Mantenimientos/salaPaginacion.html",listSala);
    }
	
	public static void guardarSala(VmdbSala sala, String codMaterial) throws ParseException, IOException{
		Map result = new HashMap();
		result.put("status", 0);
		result.put("message", "Error en el Servidor");
		String usuario = session.get("usuario");
		int n = 0;
		if(sala.getCoSala()==null){
			List<VmdbSala> list = VmdbSala.find("UPPER(deNombre) = ? and stSala in(?,?)", sala.getDeNombre().trim().toUpperCase(),'1','2').fetch();
			if(list.size()>0){
				n = list.size();
				result.put("status", 3);
				result.put("message", "El nombre de la sala ya esta registrado");
			}else{
				sala.setCoUsuarioCreacion(usuario);    		
				sala.setDaFechaCreacion(new Date());  
	    		result.put("status", 1);
				result.put("message", "Se guardo correctamente");
			}			
    	}else{
    		sala.setCoUsuarioModificacion(usuario);
    		sala.setDaFechaModificacion(new Date());
    		result.put("status", 2);
			result.put("message", "Se actualizo correctamente");
    	}
		if(n==0){
		 if(!codMaterial.equalsIgnoreCase("")){
    		HashSet<VmdbDetalleSala> lista = new HashSet<VmdbDetalleSala>(0);
    		String [] arrayMaterial = codMaterial.split("@"); //[0]    		
        	for(int i = 0 ; i < arrayMaterial.length ; i++){
        		VmdbDetalleSala detalle = new VmdbDetalleSala();              		
            	String [] materialObj = arrayMaterial[i].split(",");   
            	String coMaterial = materialObj[0];//material                	            	             	
            	VmdbMaterial objetoMaterial = VmdbMaterial.findById(Long.parseLong(coMaterial));            	
            	detalle.setVmdbSala(sala);           	
            	detalle.setVmdbMaterial(objetoMaterial);
            	detalle.setCoUsuarioCreacion(usuario);    		
            	detalle.setDaFechaCreacion(new Date());
            	detalle.setStDetalleSala('1');  
            	lista.add(detalle);
        	}	
        	sala.setVmdbDetalleSalas(lista);      	
    	 }    	
		 VmdbSala s = sala.save(); 
		 if(Cache.get("session_foto_"+session.getId(),Map.class) != null){
			 Map cacheUser = Cache.get("session_foto_"+session.getId(),Map.class);
			 Blob foto = (Blob)cacheUser.get("foto");
			 File outputfile = new File(Play.applicationPath.getPath()+"/public/sala/"+s.getCoSala()+".jpg");
			 BufferedImage fotoBI = ImageIO.read(foto.get());
			 ImageIO.write(fotoBI, "jpg", outputfile);
		 }
		}
    	renderJSON(result);
    }
	
	public static void buscarSalaById(Long id) throws SQLException{
		List<Object> json = new ArrayList<Object>();
		JSONSerializer mapeo  = new JSONSerializer();		
		List<Map> sala = VmdbSala.buscarSalaById(id);		
		if(sala.size() > 0){
			json.add(sala);			
			List<Map> detalleSala = VmdbSala.listarDetalleDeLaSala(id);
			if(detalleSala.size() > 0){
				json.add(detalleSala);
			}
		}		
		renderJSON(mapeo.serialize(json));
	}
	
	public static void eliminar(Long id){
		String usuario = session.get("usuario");
    	renderJSON(VmdbSala.eliminar(id, usuario));
    }
	
	public static void bloquear(Long id){
		String usuario = session.get("usuario");
    	renderJSON(VmdbSala.bloquear(id, usuario));
    }
	
	public static void desBloquear(Long id){
		String usuario = session.get("usuario");
    	renderJSON(VmdbSala.desBloquear(id, usuario));
    }
	
	public static void eliminarMaterialDeLaSalaById(Long id){
		String usuario = session.get("usuario");
    	renderJSON(VmdbDetalleSala.eliminarMaterialDeLaSalaById(id, usuario));
    }
	
	public static void guardarFoto(Blob foto) throws IOException{
		String msj="";
    	BufferedImage fotoBI = ImageIO.read(foto.get());
		int alto=0;
		int ancho=0;
		if(fotoBI.getHeight()>=300){
			alto=300;
			ancho=300*fotoBI.getWidth()/fotoBI.getHeight();
			Image scaledImage = fotoBI.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH); 
			BufferedImage fotoBIModificada = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB); 
			fotoBIModificada.getGraphics().drawImage(scaledImage, 0, 0,  new Color(255,255,255), null);
			ImageIO.write(fotoBIModificada, "JPG", foto.getFile());
		}else{
			ImageIO.write(fotoBI, "JPG", foto.getFile());
		}
		Map cacheUser = new HashMap();
		cacheUser.put("foto", foto);
		Cache.set("session_foto_"+session.getId(), cacheUser, "60mn");
		msj="[\"Carga Exitosa\"]";
    	renderJSON(msj);
    }
	
	public static void verFoto(String deFoto) throws IOException{    	
    	if(Cache.get("session_foto_"+session.getId(),Map.class)!=null){
    		Map cacheUser = Cache.get("session_foto_"+session.getId(),Map.class);
    		Blob foto = (Blob)cacheUser.get("foto");
    		response.setContentTypeIfNotSet(foto.type());
    		renderBinary(foto.get());
    	}else{
    		File outputfile = new File(Play.applicationPath.getPath()+"/public/sala/"+deFoto);
    		JFileChooser view = new JFileChooser();
    		String ext = view.getTypeDescription(outputfile);
    		response.setContentTypeIfNotSet(ext);
    		renderBinary(outputfile);
    	}
    }

}
