package controllers;

/**
*
* @author Raul
*/

import java.applet.Applet;
import play.*;
import play.mvc.*;

import java.util.*;
import java.util.logging.Level;

import flexjson.JSONSerializer;

import models.*;
import service.*;

public class LoginMobile extends Controller {

    public static void login(String user, String pass, String callback) {
        renderJSON(callback+"("+ServiceVmdbUsuarioMobile.login(user, pass)+")");
    }
    
}
