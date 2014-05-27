/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import play.mvc.*;

import java.util.*;

import flexjson.JSONSerializer;

import models.*;
import play.data.validation.Valid;
import play.data.validation.Error;
import service.ServiceRoles;

@With({Security.class,Secure.class})
public class Roles extends Controller {

    public static void index() {
        List<VmdbRol> listRol = ServiceRoles.listRol();
        int flag=0;
        VmdbRol rol = null;
        render(listRol,flag,rol);
    }

    public static void guardar(@Valid VmdbRol rol, int flag) {
    	if(validation.hasErrors()) {
        	if(flag==0){
        		validation.keep();
        		index();
        	}else if(flag==1){
        		render("@editar", rol,flag);
        	}
        }else{
        	String msj=ServiceRoles.save(rol, flag);
        	flash.success(msj);
        	index();
        }
    }

    public static void buscar(String buscar) {
    	String list= ServiceRoles.findRol(buscar);
    	renderJSON(list);
    }

    public static void editar(Long id) {
    	VmdbRol rol = ServiceRoles.getRol(id);
    	int flag=1;
    	render(rol,flag);
    }

    public static void eliminar(Long id) {
    	ArrayList resultado = ServiceRoles.deleteRol(id);
    	renderJSON(resultado);
    }

    public static void menu(Long id) {
    	VmdbRol rol=ServiceRoles.getRol(id);
    	List<VmdbMenu> listMenu = ServiceRoles.listMenu();
        render(rol,listMenu);
    }
    
    public static void guardarMenu(Long id,Long[] idmenu) {
    	String msj=ServiceRoles.saveMenu(id, idmenu);
    	flash.success(msj);
    	menu(id);
    }
    
}