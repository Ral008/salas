package controllers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import play.Play;
import play.libs.Mail;
import play.mvc.Controller;
import service.ServiceMobile;
import flexjson.JSONSerializer;

public class Mobile extends Controller{
	
	/*public static void traerLosProductos(Long coPersona, String callback){		 
		 renderJSON(callback + "("+ServiceMobile.getAllProductos(coPersona)+")");
	 }
	
	public static void traerLasFerulas(Long coPersona, String callback){		 
		 renderJSON(callback + "("+ServiceMobile.getAllFerulas(coPersona)+")");
	 }*/
	
	public static void enviarPedidoSmartCar(String callback,String deUsuario ,Long coPersona,String mail,String nombre,String namePaciente,String lugarEntrega,String productos) throws EmailException {
		/*SimpleEmail email = new SimpleEmail();
		email.setFrom(mail,nombre);
		email.addTo("rlucero.as@gmail.com");
		email.setSubject("Pedido para "+namePaciente);
		email.setMsg(productos);
		Mail.send(email); */
		String [] arrayProductos = productos.split("@");
		//SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");        
		StringBuilder htmlEmailTemplate = new StringBuilder();
		htmlEmailTemplate.append("<html>");
		htmlEmailTemplate.append("<head>");
		htmlEmailTemplate.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />");
		htmlEmailTemplate.append("</head>");
		htmlEmailTemplate.append("<body>");
		htmlEmailTemplate.append("<div>");
		htmlEmailTemplate.append("<table>");
		htmlEmailTemplate.append("<tr>");
		htmlEmailTemplate.append("<td style='font-weight:bold;'>Paciente</td>");
		htmlEmailTemplate.append("<td>:  "+namePaciente+"</td>");
		htmlEmailTemplate.append("</tr>");		
		/*htmlEmailTemplate.append("<tr>");
		htmlEmailTemplate.append("<td style='font-weight:bold;'>Fecha de cirugia</td>");
		htmlEmailTemplate.append("<td>:  "+formatter.format(dateCirugia)+"</td>");
		htmlEmailTemplate.append("</tr>");*/
		htmlEmailTemplate.append("<tr>");
		htmlEmailTemplate.append("<td style='font-weight:bold;'>Lugar de entrega</td>");
		htmlEmailTemplate.append("<td>:  "+lugarEntrega+"</td>");
		htmlEmailTemplate.append("</tr>");		
		htmlEmailTemplate.append("</table>");
		htmlEmailTemplate.append("<p></p>");		
		htmlEmailTemplate.append("<table border='1' cellpadding='0' cellspacing='0'>");
		htmlEmailTemplate.append("<tr>");
		htmlEmailTemplate.append("<td width='610' height='30' bgcolor='#0088CC' style='font-weight:bold'><font color='FFFFFF'>PRODUCTO</font></td>");
		htmlEmailTemplate.append("<td width='90' height='30' bgcolor='#0088CC' style='font-weight:bold;text-align:center'><font color='FFFFFF'>CANTIDAD</font></td>");
		htmlEmailTemplate.append("</tr>");		
		for(int i = 0 ; i < arrayProductos.length ; i++){
			String [] arrayLinea = arrayProductos[i].split(",");
			String name = arrayLinea[0];
			String cantidad = arrayLinea[1];
			htmlEmailTemplate.append("<tr>");
			htmlEmailTemplate.append("<td height='30'>"+name+"</td>");
			htmlEmailTemplate.append("<td height='30' style='text-align:center'>"+cantidad+"</td>");
			htmlEmailTemplate.append("</tr>");								
		}					
		htmlEmailTemplate.append("</table>");				
		htmlEmailTemplate.append("</div>");
		htmlEmailTemplate.append("</body>");
		htmlEmailTemplate.append("</html>");
		
		HtmlEmail email = new HtmlEmail();
		email.addTo("info@globalmax.med.ec");//raul_lucero07@hotmail.com
		email.addBcc("rlucero.as@gmail.com");
		email.addCc("ventas@globalmax.med.ec");//luisandvasquez@hotmail.com
		email.setFrom(Play.configuration.getProperty("mail.smtp.user"),nombre);
		email.setSubject("Pedido para "+namePaciente);			
		email.setHtmlMsg(htmlEmailTemplate.toString());
		//set the alternative message
		email.setTextMsg("Your email client does not support HTML messages");		
		Mail.send(email);
		Map result = new HashMap();
		result.put("status", 1);
		result.put("message", "Su pedido fue enviado correctamente");	
		JSONSerializer mapeo = new JSONSerializer();				
		renderJSON(callback + "(" +mapeo.serialize(result)+")");
	}
	
	 /*public static void productosDeVisita(String callback , Long coVisita) throws SQLException{
		 renderJSON(callback + "(" + ServiceMobile.getProductosXVisita(coVisita) + ")");
	 }
	 
	 //new
	 public static void traerLosRepresentantes(Long coPersona, String callback) throws SQLException{		 
		 renderJSON(callback + "("+ServiceMobile.getAllRepresentantes(coPersona)+")");
	 }
	 
	 public static void traerLosProductosxCicloxGrupoxEspecialidadxCategoria(Long grupo, Long especialidad , Long ciclo,Long categoria,Long persona ,String callback,Long institucion){
		 renderJSON(callback + "("+ServiceMobile.getProductosxCicloxGrupoxEspecialidadxCategoria(grupo,especialidad,ciclo,categoria,persona,institucion)+")");
	 }	 
	
	 public static void traerLosProductosxStockPersona(String callback,Long persona){
		 renderJSON(callback+"("+ServiceMobile.getProductosxStockPersona(persona)+")");
	 }

	public static void traerMedicoCortesia(String callback,String medico,Long grupo,Long zona, Long persona) {
		renderJSON(callback + "(" + ServiceMobile.getMedicosParaCortesia(grupo, zona,medico, persona)+")");
	}
	
	public static void guardarVisitaProgramada(String callback,String deObservacion,String ptoEncuentro,String productos,Long coVisita, Long coPersona,String dePersona,String descargaProd) throws SQLException{
		 renderJSON(callback+"("+ServiceMobile.guardarVisitaProgramada(deObservacion,ptoEncuentro,productos,coVisita,coPersona,dePersona, descargaProd)+")");
	 }	 	
	
	public static void guardarVisitaDeCortesia(String dePersona,Long coPersona,Long coGrupo,Long coCiclo,Long coMedicoColegio,String productos,String ptoEncuentro,Long coInstitucion ,String callback) throws SQLException{
		renderJSON(callback + "(" + ServiceMobile.guardarVisitaDeCortesia(dePersona, coPersona, coCiclo,coGrupo, coMedicoColegio, productos, ptoEncuentro, coInstitucion) +")");
	}
	
	public static void guardarDatosDelMedico(String callback,Long medico,String email,String fecha,String telefono, String dni, String direccionad, String equipo, String lanpass) throws SQLException{
		renderJSON(callback + "(" + ServiceMobile.guardarDatosDelMedico(medico,email,fecha,telefono, dni, direccionad, equipo, lanpass)+")");
	}
	
	public static void listarSemanasDelMes(String callback) throws ParseException{
		renderJSON(callback+"("+ServiceMobile.listarSemanasDelMes()+")");
	}
	
	public static void listarMedicosEnLaSemana(String callback,Long persona,String ini , String fin,Long ciclo,Long grupo,Long zona) throws SQLException{
		renderJSON(callback+"("+ServiceMobile.listarMedicosEnLaSemana(persona,ciclo,grupo,zona,ini,fin)+")"); 
	}
	
	public static void listarVisitasSemanaActual(String callback,Long persona,Long ciclo,Long grupo,Long zona) throws SQLException{
		renderJSON(callback+ "(" + ServiceMobile.listarVisitasSemanaActual(persona,ciclo,grupo,zona)+")");
	}
	
	public static void listarVisitasDiarioActual(String callback,Long persona,Long ciclo,Long grupo,Long zona) throws SQLException{
		renderJSON(callback + "(" +ServiceMobile.listarVisitasDiarioActual(persona,ciclo,grupo,zona)+")");
	}
	
	public static void traerDatosDelRepresentante(String callback,Long persona) throws SQLException{
		renderJSON(callback + "(" + ServiceMobile.getDatosRepresentante(persona) + ")");
	}
	
	public static void buscarInstituciones(String callback, String criteriodebusqueda){
		String pornombre = "0";
		if(params.get("pornombre") != null){pornombre = "1";}
		renderJSON(callback + "(" +ServiceMobile.buscarInstitucion(criteriodebusqueda, pornombre)+ ")");
	}
	
	public static void getInstitucion(String callback, Long coInstitucion){
		renderJSON(callback + "(" +ServiceMobile.getInstitucion(coInstitucion)+ ")");
	}
	
	//new
	public static void getMedico(String callback, Long coMedico){
		renderJSON(callback + "(" +ServiceMobile.getMedico(coMedico)+ ")");
	}
	
	//new
	public static void getMercado(String callback) throws SQLException{
		renderJSON(callback + "(" +ServiceMobile.getMercado()+ ")");
	}
	
	//new    
	public static void getPeriodo(String callback) throws SQLException{
		renderJSON(callback + "(" +ServiceMobile.getPeriodo()+ ")");
	}
	
	//new    
	public static void listarProductosDeFichaCloseUp(String callback, Long coMedico, String coMercado, String coPeriodo) throws SQLException{
		renderJSON(callback + "(" +ServiceMobile.listarProductosDeFichaCloseUp(coMedico, coMercado, coPeriodo)+ ")");
	}
	
	public static void actualizarNombreInstitucion(String callback,Long coInstitucion, String deNombre){
		renderJSON(callback + "(" +ServiceMobile.guardarDatosInstitucion(coInstitucion, deNombre)+ ")");
	}

	public static void getEspecialidadesXmedico(String callback, Long coMedico){
		renderJSON(callback + "(" +ServiceMobile.getEspecialidadesXmedico(coMedico)+ ")");
	}
	
	public static void getNotificaciones(String callback, Long coPersona){
		renderJSON(callback + "(" + ServiceMobile.getNotificaciones(coPersona) + ")");
	}
	
	//new this
	public static void getProductosMobile(String callback, Long coPersona) throws SQLException{
		renderJSON(callback + "(" +ServiceMobile.getProductosMobile(coPersona)+ ")");
	}
	
	//new
	public static void getValidarStockMobile(String callback, Long coPersona, Long coProducto) throws SQLException{
		renderJSON(callback + "(" +ServiceMobile.getValidarStockMobile(coPersona, coProducto)+ ")");
	}
	
	//new 
	public static void guardarTransferencia(String callback,Long coPersona, Long coPersonaAlQTransfiero,int cantidad,Long coProducto) throws SQLException{
		 renderJSON(callback+"("+ServiceMobile.guardarTransferencia(coPersona,coPersonaAlQTransfiero,cantidad,coProducto)+")");
	 }*/
}
