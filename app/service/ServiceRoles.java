/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import flexjson.JSONSerializer;

import play.db.jpa.JPA;

import models.*;

public class ServiceRoles {
    
    public static List<VmdbRol> listRol() {
        return VmdbRol.listRol("");
    }
    
    public static String save(VmdbRol rol, int flag) {
    	String msj="";
    	if(flag==0){
    		rol.setStRol('1');
    		rol.setCoUsuarioCreacion("1");
    		rol.setDaFechaCreacion(new Date());
	    	msj="El rol ha sido registrado correctamente";
    	}else if(flag==1){
    		rol.setDaFechaModificacion(new Date());
	    	msj="El rol ha sido actualizado correctamente";
    	}
    	VmdbRol.save(rol);
    	return msj;
    }
    
    public static String findRol(String buscar){
    	List<VmdbRol> listRol = VmdbRol.listRol(buscar);
    	JSONSerializer mapeo = new JSONSerializer().include("coRol","deNombre").exclude("*");
    	String list= mapeo.serialize(listRol);
    	return list;
    }
    
    public static VmdbRol getRol(Long id){
    	return VmdbRol.get(id);
    }
    
    public static ArrayList deleteRol(Long id){
    	VmdbRol rol = VmdbRol.get(id);
    	ArrayList resultado=new ArrayList();
    	if (rol!= null) {
            rol.setStRol('0');
            VmdbRol.save(rol);
            resultado.add(1);
            resultado.add("El rol ha sido eliminado correctamente");
        }
    	return resultado;
    }
    
    public static List<VmdbMenu> listMenu(){
    	return VmdbMenu.listMenu();
    }
    
    public static String saveMenu(Long id,Long[] idmenu){
    	VmdbRol rol=VmdbRol.get(id);
    	VmdrRolMenu.deletexRol(id);
    	Set<VmdrRolMenu> vmdrRolMenus= new HashSet<VmdrRolMenu>(0);
    	VmdbMenu menu = null;
    	VmdrRolMenu rolmenu=null;
    	for (Long idme : idmenu) {
    		menu=new VmdbMenu();
    		menu.setCoMenu(idme);
    		rolmenu = new VmdrRolMenu(null, menu, rol, "Administrador", new Date(), '1');
	    	vmdrRolMenus.add(rolmenu);
		}
    	rol.setVmdrRolMenus(vmdrRolMenus);
    	VmdbRol.save(rol);
    	String msj="El menu ha sido guardado correctamente";
    	return msj;
    }
    
}
