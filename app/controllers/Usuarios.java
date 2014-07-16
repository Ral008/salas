/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

/**
 *
 * @author raul
 */
import play.mvc.*;

import java.util.*;

import flexjson.JSONSerializer;

import models.*;
import play.cache.Cache;
import play.data.validation.Valid;
import service.*;

@With({Security.class,Secure.class})
public class Usuarios extends Controller {
	
    public static void index() {
        List<VmdbUsuario> usu = ServiceUsuarios.listUsuario();
        List<VmdbRol> roles = ServiceRoles.listRol();
        render(usu,roles);
    }

    public static void guardar(@Valid VmdbUsuario usuarios , Long[] idRol ,String flag) {
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            flash.error("Ocurrio un error");
            index();
        }        
        ServiceUsuarios.saveUser(usuarios,idRol,flag);
        flash.success("Datos guardados correctamente");
        index();
    }
    
    public static void buscarPersonasSinUsuario(String nombre){
    	String personas = ServiceUsuarios.findPersonaWithOutUser(nombre);
    	renderJSON(personas);
    }

    public static void buscar(String nombre) {
    	String usu = ServiceUsuarios.findUsuariosByName(nombre);    	
    	renderJSON(usu);
    }

    public static void editar(Long id) {
    	VmdbUsuario editUser = VmdbUsuario.findById(id);
    	List<VmdbRol> roles = VmdbRol.listRol("");
        render(editUser,roles);
    }

    public static void eliminar(Long id) {
    	ServiceUsuarios.deleteUserById(id);        
    }
    
    public static void activarAlUsuario(Long id) {
    	ServiceUsuarios.updateUserById(id);    
    	Map result = new HashMap();
    	result.put("status", 1);
    	result.put("message", "El usuario fue activado");
    	JSONSerializer mapeo = new JSONSerializer();
		renderJSON(mapeo.serialize(result));
    }
    
    public static void viewCambiarContrasenia() {
    	Long coPersona = Long.parseLong(session.get("idPersona"));
    	VmdbUsuario usuario = VmdbUsuario.find("vmdbPersona.coPersona = ? and stUsuario = '1'", coPersona).first();
    	render("Usuarios/editPassword.html",usuario);
    }
    
    public static void cambiarContrasenia() {
    	String clave = params.get("clave");
    	Long coUsuario = Long.parseLong(params.get("coUsuario"));
    	Map result = new HashMap();
        String deUsuario = session.get("usuario");
        VmdbUsuario usuario = VmdbUsuario.find("coUsuario = ? and stUsuario = '1'", coUsuario).first();
        usuario.setDeClave(clave);
        usuario.setDaFechaModificacion(new Date());
        usuario.setCoUsuarioModificacion(deUsuario);
        usuario.save();
        /** Actualizar clave en Persona **/
        VmdbPersona objPersona = VmdbPersona.findById(usuario.getVmdbPersona().getCoPersona());
        objPersona.setDeClave(clave);
        objPersona.setCoUsuarioModificacion(deUsuario);
        objPersona.setDaFechaModificacion(new Date());
        objPersona.save();
        /**-----------------------------**/
        result.put("status", 1);
    	result.put("message", "Su clave fue actualizado correctamente");
    	JSONSerializer mapeo = new JSONSerializer();
		renderJSON(mapeo.serialize(result));
    }
}
