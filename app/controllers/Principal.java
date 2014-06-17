package controllers;

import java.sql.SQLException;

import play.mvc.Controller;
import play.mvc.With;


@With({Security.class,Secure.class})
public class Principal extends Controller {
		
    public static void index() throws SQLException {
        //render();
    	Reserva.index();
    }

}
