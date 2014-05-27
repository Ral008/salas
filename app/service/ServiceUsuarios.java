/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import play.mvc.Scope.RenderArgs;

import flexjson.JSONSerializer;
import models.*;

public class ServiceUsuarios {
    
	public static List<VmdbUsuario> listUsuario() {
        List<VmdbUsuario> list = VmdbUsuario.findAll();
        return list;
    }

    public static VmdbUsuario saveUser(VmdbUsuario usuario,Long[] idRol,String flag) {
    	if(flag.equalsIgnoreCase("0")){
    		usuario.setDaFechaCreacion(new Date());
        	usuario.setStUsuario('1');
            VmdbUsuario user = VmdbUsuario.saveUser(usuario);
            /**Change the rol **/
            Set<VmdrUsuarioRol> vmdrUsuarioRol= new HashSet<VmdrUsuarioRol>(0);
            VmdbRol rol = null;
        	VmdrUsuarioRol usuarioRol=null;
        	for (Long idro : idRol) {
        		rol=new VmdbRol();
        		rol.setCoRol(idro);
        		usuarioRol = new VmdrUsuarioRol(null,user,rol,"ADMINISTRADOR",new Date(),'1');
        		vmdrUsuarioRol.add(usuarioRol);
    		}
        	user.setVmdrUsuarioRols(vmdrUsuarioRol);    	
        	return VmdbUsuario.saveUser(user);
    	}else{
    		VmdbUsuario user = VmdbUsuario.findById(usuario.getCoUsuario());
    		user.setCoUsuarioModificacion(usuario.getCoUsuarioCreacion());
    		user.setDaFechaModificacion(new Date());
    		VmdbUsuario userModificado = VmdbUsuario.saveUser(user);
    		/**Change the rol **/
    		VmdrUsuarioRol.deleteRols(user.getCoUsuario());
            Set<VmdrUsuarioRol> vmdrUsuarioRol= new HashSet<VmdrUsuarioRol>(0);
            VmdbRol rol = null;
        	VmdrUsuarioRol usuarioRol=null;
        	for (Long idro : idRol) {
        		rol=new VmdbRol();
        		rol.setCoRol(idro);
        		usuarioRol = new VmdrUsuarioRol(null,user,rol,"ADMINISTRADOR",new Date(),'1');
        		vmdrUsuarioRol.add(usuarioRol);
    		}
        	userModificado.setVmdrUsuarioRols(vmdrUsuarioRol);    	
        	return VmdbUsuario.saveUser(userModificado);
    	}
    	
    }

	public static String findUsuariosByName(String deUsuario) {		
		List<VmdbUsuario> usuarios =  VmdbUsuario.findUsuariosByName(deUsuario);
		JSONSerializer mapeo = new JSONSerializer().include("coUsuario","deUsuario","deClave","vmdbPersona.deNombre","stUsuario").exclude("*");
    	String list= mapeo.serialize(usuarios);
    	return list;
	}
	
	public static VmdbUsuario findUsuarioByName(String username){
		return VmdbUsuario.findUsuarioByName(username);
	} 

	public static void deleteUser(long coUsuario) {
		VmdbUsuario userDelete = VmdbUsuario.find("coUsuario = ? ", coUsuario).first();		
		userDelete.delete();
	}

	public static List<VmdbUsuario> findUsuariosByRol(String rol) {
		List<VmdbUsuario> usuarios = VmdbUsuario.findUsuariosByRol(rol);
		return usuarios;
	}

	public static VmdbUsuario findUsuarioById(Long id) {
		return VmdbUsuario.findById(id);
	}

	public static void deleteUserById(Long id) {
		VmdbUsuario user = VmdbUsuario.findById(id);		
		user.setStUsuario('0');
		user.save();
	}
	
	public static void updateUserById(Long id) {
		VmdbUsuario user = VmdbUsuario.findById(id);		
		user.setStUsuario('1');
		user.save();
	}
	
	public static String findPersonaWithOutUser(String nombre){
		List<VmdbPersona> personas = VmdbPersona.listPersonaWithOutUser(nombre);
		JSONSerializer mapeo = new JSONSerializer().include("coPersona","deNombre").exclude("*");
    	String list= mapeo.serialize(personas);
    	return list;
	}

	public static VmdbUsuario findUsuarioByUserName(String username) {
		return VmdbUsuario.find("UPPER(deUsuario) = ? and stUsuario = ? ", username.toUpperCase(),'1').first();
	}
}
