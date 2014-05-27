/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.persistence.Query;

import play.Play;
import play.cache.Cache;
import play.db.jpa.Blob;

import flexjson.JSONSerializer;
import flexjson.transformer.NullTransformer;

import models.*;

public class ServicePersonas {
    
    public static List<VmdbPersona> listPersona() {
        return VmdbPersona.listPersona("");
    }
    
    public static VmdbPersona findPersona(Long coPersona){
    	return VmdbPersona.findById(coPersona);
    }

	public static List<VmdbTipoPersona> listTipoPersona() {
		return VmdbTipoPersona.listTipoPersona("");
	}

    public static List<VmdbPersona> findPersona(String buscar){
    	List<VmdbPersona> list = VmdbPersona.listPersona(buscar);
    	return list;
    }
    
    public static String saveFoto(Blob foto,String id) throws IOException{
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
		Cache.set("session_foto_"+id, cacheUser, "60mn");
		msj="[\"Uploaded successfully\"]";
    	return msj;
    }
    
    public static Map save(VmdbPersona persona,String id, String usuario) throws IOException {
    	Map result = new HashMap();
    	result.put("status", 0);
		result.put("message", "Error on Server");
    	if(Cache.get("session_foto_"+id,Map.class) != null){
			Map cacheUser = Cache.get("session_foto_"+id,Map.class);
			Blob foto = (Blob)cacheUser.get("foto");
			persona.setDeFoto(persona.getNuDni()+".jpg");
			File outputfile = new File(Play.applicationPath.getPath()+"/public/photos/"+persona.getDeFoto());
			BufferedImage fotoBI = ImageIO.read(foto.get());
			ImageIO.write(fotoBI, "jpg", outputfile);
		}else{
			if(persona.getDeFoto()!=null){
//				File outputfile = new File(Play.applicationPath.getPath()+"/public/img_fotos/"+persona.getDeFoto());
//				outputfile.delete();
//				persona.setDeFoto(null);
			}
		}
    	if(persona.getCoPersona()==null){
    		persona.setCoUsuarioCreacion(usuario);
    		persona.setDaFechaCreacion(new Date());
    		result.put("status", 1);
			result.put("message", "The employee has been saved successfully");
    	}else{
    		persona.setCoUsuarioModificacion(usuario);
    		persona.setDaFechaModificacion(new Date());
    		result.put("status", 2);
			result.put("message", "The employee has been updated successfully");
    	}
    	VmdbPersona.save(persona);
    	return result;
    }

	public static String eliminarPersona(Long id, String usuario) {
		Map result = new HashMap();
		VmdbPersona persona = VmdbPersona.findById(id);
		if(persona!=null){
			persona.setStPersona('0');
			persona.setCoUsuarioModificacion(usuario);
			persona.setDaFechaModificacion(new Date());
			persona.save();
			result.put("status", 1);
			result.put("message", "The employee has been removed successfully");
		}else{
			result.put("status", 0);
			result.put("message", "You can not be removed");
		}
		JSONSerializer mapeo = new JSONSerializer();
		return mapeo.serialize(result);		
	}

	public static List<VmdbPersona> listAgent() {
		return VmdbPersona.find("vmdbTipoPersona.coTipoPersona = ? order by deNombre",3L).fetch();
	}
	
}