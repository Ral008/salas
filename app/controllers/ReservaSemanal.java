package controllers;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import models.VmdbReserva;
import models.VmdbSala;
import play.mvc.Controller;
import play.mvc.With;
import flexjson.JSONSerializer;

@With({Security.class,Secure.class})
public class ReservaSemanal extends Controller {

    public static void index() {   
    	String name ="";
    	List<VmdbSala> listSala = VmdbSala.find("UPPER(deNombre) like ? and stSala = ? order by deNombre asc", "%"+name.toUpperCase()+"%", '1').fetch();
        render(listSala);
    }
    
    public static void listaDeReservaSemanales(Long coSala, String fechaInicial, String fechaFinal) throws SQLException {
    	JSONSerializer mapeo  = new JSONSerializer();
    	List<Map> listado = VmdbReserva.listaDeReservaSemanales(coSala,fechaInicial,fechaFinal); 
    	renderJSON(mapeo.serialize(listado));
    }                       

}
