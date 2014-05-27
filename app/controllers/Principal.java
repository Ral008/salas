package controllers;

import java.util.List;
import java.util.Map;

import models.VmdbMenu;
import models.VmdbUsuario;
import play.cache.Cache;
import play.mvc.*;


@With({Security.class,Secure.class})
public class Principal extends Controller {
		
    public static void index() {
        render();
    }

}
