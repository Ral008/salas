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

    public static void pantalla01() throws Exception {   	        	
        render("pantalla01.html");
    }
    
}
