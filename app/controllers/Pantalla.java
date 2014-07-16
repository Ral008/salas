package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import flexjson.JSONSerializer;
import play.db.DB;
import play.mvc.Controller;

public class Pantalla extends Controller {

    public static void pantallaQenqo() throws Exception {   	        	
        render("pantallaQenqo.html");
    }
    
    public static void pantallaPisac() throws Exception {   	        	
        render("pantallaPisac.html");
    }
    
    public static void pantallaMoray() throws Exception {   	        	
        render("pantallaMoray.html");
    }
    
}
