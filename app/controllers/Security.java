package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.oval.constraint.InstanceOf;

import models.VmdbPersona;
import models.VmdrUsuarioRol;
import models.VmdbMenu;
import models.VmdbRol;
import models.VmdbUsuario;
import models.VmdrRolMenu;
import play.Logger;
import play.cache.Cache;
import play.mvc.*;
import service.ServiceUsuarios;

public class Security extends Secure.Security {
	private static Map scache;
	static boolean authenticate(String username, String password) {
		VmdbUsuario user = ServiceUsuarios.findUsuarioByUserName(username);
		if (user != null && user.getDeClave().equals(password)) {
			scache = new HashMap();
			Map lmenu = new HashMap();
			List<String> roles = new ArrayList<String>();			
			for (VmdrUsuarioRol userRol : user.getVmdrUsuarioRols()) {
				roles.add(userRol.getVmdbRol().getDeNombre());
				for (VmdrRolMenu menuRol : userRol.getVmdbRol().getVmdrRolMenus()) {
					if (lmenu.get(menuRol.getVmdbMenu().getVmdbMenu().getDeMenu()) == null) {
						lmenu.put(menuRol.getVmdbMenu().getVmdbMenu().getDeMenu(), new ArrayList<VmdbMenu>());
					}
					List<VmdbMenu> menu = (List<VmdbMenu>) lmenu.get(menuRol.getVmdbMenu().getVmdbMenu().getDeMenu());
					menu.add(menuRol.getVmdbMenu());
					lmenu.put(menuRol.getVmdbMenu().getVmdbMenu().getDeMenu(),menu);					
				}
			}		
			
			scache.put("coUsuario", user);
			scache.put("deUsuario", username);
			scache.put("deRoles", roles);
			scache.put("coPersona", user.getVmdbPersona().getCoPersona());
//			scache.put("coTipoPersona", user.getVmdbPersona().getVmdbTipoPersona().getCoTipoPersona());
			
//			if(user.getVmdbPersona().getVmdbGrupo() != null){
//				scache.put("coGrupo", user.getVmdbPersona().getVmdbGrupo().getCoGrupo());
//			}
			
			session.put("usuario", user.getDeUsuario());
			session.put("nombre",user.getVmdbPersona().getDeNombre());
			session.put("email",user.getVmdbPersona().getDeCorreo());
			session.put("idPersona", user.getVmdbPersona().getCoPersona());
			session.put("fotoPersona", user.getVmdbPersona().getDeFoto());			
			session.put("rol", roles.get(0));
//			session.put("idTipoPersona",user.getVmdbPersona().getVmdbTipoPersona().getCoTipoPersona());
			session.put("idGerencia",user.getVmdbPersona().getVmdbGerencia().getCoGerencia());
//			if(user.getVmdbPersona().getVmdbGrupo() != null){
//				session.put("grupo", user.getVmdbPersona().getVmdbGrupo().getDeNombre());
//				session.put("idGrupo", user.getVmdbPersona().getVmdbGrupo().getCoGrupo());
//			}else{
//				session.put("grupo", "ninguno");
//				session.put("idGrupo", 0);
//			}
			
//			if(user.getVmdbPersona().getVmdbZona() != null){
//				session.put("zona", user.getVmdbPersona().getVmdbZona().getDeNombre());
//				session.put("idZona", user.getVmdbPersona().getVmdbZona().getCoZona());;
//			}else{
//				session.put("zona", "ninguno");
//				session.put("idZona", 0);
//			}
			
			scache.put("sessionID", session.getId());
			scache.put("menu", lmenu);
			Cache.set("session_" + session.getId(), scache, "60mn");
			Logger.debug("user " + user + " logged in as session " + session.getId());
			return true;
		}
		flash("msj","Usuario/Clave Combinacion incorrecta");
		return false;
	}
	
	static boolean validarRol(String[] noTieneAcceso){
		Map cacheUser = Cache.get("session_"+session.getId() , Map.class);
		List<String> roles = (List) cacheUser.get("deRoles");
		for(String rol : roles){
			for(String sinPermiso : noTieneAcceso){
				if(sinPermiso.equalsIgnoreCase(rol)){
					return false;
				}
			}			
		}
		return true;
	}
	
	@Before()
	static void getMenu() {
		if (Cache.get("session_"+session.getId(),Map.class) != null) {
			Map cacheUser = Cache.get("session_"+session.getId(),Map.class);
			renderArgs.put("menuUser", cacheUser.get("menu"));
			renderArgs.put("idpersona", session.get("idPersona"));
			renderArgs.put("fotoPersona", session.get("fotoPersona"));
			renderArgs.put("deUsuario", session.get("usuario"));
			renderArgs.put("nombrePersona", session.get("nombre"));
			renderArgs.put("rol", session.get("rol"));
		}
	}
	@After
	static void reGenerarMenu() throws Throwable{
		if(Cache.get("session_"+session.getId(),Map.class) != null){
			Map cacheUser = Cache.get("session_"+session.getId(),Map.class);
			Cache.set("session_" + session.getId(), cacheUser, "60mn");			
		}else{
			Secure.logout();
		}
	}
}
