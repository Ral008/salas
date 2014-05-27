package service;

import java.util.List;

import flexjson.JSONSerializer;
import models.VmdbUsuario;

public class ServiceVmdbUsuarioMobile {
	public static String login(String usuario, String clave) {
		List<Object> user = VmdbUsuario.validarLogin(usuario, clave);
		JSONSerializer mapeo = new JSONSerializer();
        String list = mapeo.serialize(user);
		return list;
	}
	
}
