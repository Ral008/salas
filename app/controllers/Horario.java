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

public class Horario extends Controller {

    public static void cargarHorarioDeLaSala(String cadena) throws SQLException{ 
		Long coSala = Long.parseLong(cadena);
		Connection con = DB.getConnection();
		StringBuilder query = new StringBuilder();
		query.append("select dr.hora_desde as DESDE, dr.hora_hasta as HASTA, r.de_evento as EVENTO, ");
		query.append("(select p.de_nombre from stdb_persona p where p.co_persona = r.co_persona) as NOMBRE ");
		query.append("from stdb_reserva r, stdd_detalle_reserva dr ");
		query.append("where r.co_reserva = dr.co_reserva and r.st_reserva = '1' and dr.st_detalle_reserva = '1' and ");
		query.append("co_sala = ? and dr.de_fecha = to_char(sysdate,'dd/mm/yyyy') and ");
		query.append("to_number(SUBSTR(dr.hora_hasta,0,INSTR(dr.hora_hasta,':')-1))>to_number(to_char(sysdate,'hh24')) ");
		PreparedStatement pr = con.prepareStatement(query.toString());
		pr.setLong(1,coSala);		
		ResultSet rs = pr.executeQuery();
		List<Map> result = new ArrayList<Map>();
		Map map = null;
		while(rs.next()){
			map = new HashMap();
			map.put("hora_desde", rs.getString("DESDE"));
			map.put("hora_hasta", rs.getString("HASTA"));	
			map.put("de_evento", rs.getString("EVENTO"));	
			map.put("de_nombre", rs.getString("NOMBRE"));	
			result.add(map);
		}	
		rs.close();
		pr.close();
		con.close();	
		JSONSerializer mapeo = new JSONSerializer();				
    	renderJSON(mapeo.serialize(result));
	}
    
    
}
