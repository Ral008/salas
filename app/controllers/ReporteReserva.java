package controllers;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import models.VmdbDetalleReserva;
import models.VmdbReserva;
import play.mvc.Controller;
import play.mvc.With;
import flexjson.JSONSerializer;

@With({Security.class,Secure.class})
public class ReporteReserva extends Controller {

    public static void index() {    	
        render();
    }
    
    public static void listaDeReservasByPersona(String evento) throws SQLException {
    	JSONSerializer mapeo  = new JSONSerializer();
    	List<Map> listado = Reserva.listaDeReservasByPersona(evento); 
    	renderJSON(mapeo.serialize(listado));
    }
    
    public static void anularReserva(Long id) throws SQLException{
		String usuario = session.get("usuario");
    	renderJSON(VmdbReserva.eliminar(id, usuario));
    }
    
    public static void verDetalle(Long id) {
    	VmdbReserva reserva = VmdbReserva.findById(id);
		List<VmdbDetalleReserva> detalleReserva = VmdbDetalleReserva.find("vmdbReserva.coReserva = ? and stDetalleReserva = ? ", id, '1').fetch();				   	
    	render("ReporteReserva/verDetalle.html",reserva,detalleReserva,id);    	    	
    }                 

}
