package job;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.VmdbDetalleReserva;
import models.VmdbReserva;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import play.Play;
import play.jobs.Job;
import play.jobs.On;
import play.libs.Mail;
import controllers.Reserva;


@On("0 0/60 * 1/1 * ? *")
public class EnviarMailHora extends Job {
	
	public void doJob() throws SQLException, ParseException, EmailException { //0 0/60 * 1/1 * ? *	 every day cada 60 minutos
		List<Map> result = VmdbReserva.listaParaRecordarioDeEvento();
		for (Map map : result) {									
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			int n1 = map.get("hora").toString().indexOf(":");//8:00
			int h = Integer.parseInt(map.get("hora").toString().substring(0, n1));
			String hora = "";
			if(h<10){
				hora = "0"+h+":00";
			}else{
				hora = map.get("hora").toString();
			}			
			Date d = df.parse(hora); 
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			cal.add(Calendar.MINUTE, -60);
			String newTime = df.format(cal.getTime());
			
			/**date now**/
			Calendar calNow = Calendar.getInstance();
			calNow.setTime(new Date());
			String nowTime = df.format(calNow.getTime());
			//System.out.println(nowTime);
			if(newTime.equals(nowTime)){
				//System.out.println("Envia mail : " +newTime);
				VmdbReserva objReserva = VmdbReserva.findById(Long.parseLong(map.get("coReserva").toString()));
				/**mail de recordatorio**/
				int band = enviarMailRecordatorio(objReserva, objReserva.getVmdbPersona().getDeCorreo());
				if(band == 1){
					System.out.println("send mail reminder before 60 minutes");
				}
				/**Actualizar a mail enviado de recordatorio**/
				VmdbDetalleReserva dr = VmdbDetalleReserva.findById(Long.parseLong(map.get("coDetalleReserva").toString()));
				dr.setStEnviado('0');
				dr.save();
			}																															 
		}		
    }	
	
	public static int enviarMailRecordatorio(VmdbReserva reserva, String emailUser) throws EmailException{
		int band = 0;
		StringBuilder htmlEmailTemplate = new StringBuilder();
		htmlEmailTemplate.append("<html>");
		htmlEmailTemplate.append("<head>");
		htmlEmailTemplate.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />");
		htmlEmailTemplate.append("</head>");
		htmlEmailTemplate.append("<body>");
		htmlEmailTemplate.append("<font><i><h4>Su reunión se llevará a cabo dentro de 1 hora.</h4></i></font>");
		htmlEmailTemplate.append("<div>");
		htmlEmailTemplate.append("<table>");
		htmlEmailTemplate.append("<tr>");
		htmlEmailTemplate.append("<td style='font-weight:bold'><h2>"+reserva.getDeEvento()+"</h2></td>");
		htmlEmailTemplate.append("</tr>");
		htmlEmailTemplate.append("</table>");
		htmlEmailTemplate.append("<table>");
		htmlEmailTemplate.append("<tr>");
		htmlEmailTemplate.append("<td style='font-weight:bold'>Sala</td>");
		htmlEmailTemplate.append("<td width='200'>:  "+reserva.getVmdbSala().getDeNombre()+"</td>");
		htmlEmailTemplate.append("<td></td>");
		htmlEmailTemplate.append("<td></td>");
		htmlEmailTemplate.append("</tr>");
		htmlEmailTemplate.append("<tr>");
		htmlEmailTemplate.append("<td style='font-weight:bold'>Gerencia</td>");
		htmlEmailTemplate.append("<td width='200'>:  "+reserva.getVmdbGerencia().getDeNombre()+"</td>");
		htmlEmailTemplate.append("<td style='font-weight:bold'>Organizador</td>");
		htmlEmailTemplate.append("<td>:  "+reserva.getVmdbPersona().getDeNombre()+"</td>");
		htmlEmailTemplate.append("</tr>");
		htmlEmailTemplate.append("<tr>");
		htmlEmailTemplate.append("<td style='font-weight:bold'>Hora desde</td>");
		htmlEmailTemplate.append("<td width='200'>:  "+reserva.getHoraDesde()+"</td>");
		htmlEmailTemplate.append("<td style='font-weight:bold'>Hora hasta</td>");
		htmlEmailTemplate.append("<td>:  "+reserva.getHoraHasta()+"</td>");
		htmlEmailTemplate.append("</tr>");
		htmlEmailTemplate.append("</table>");
		htmlEmailTemplate.append("<p></p>");
		htmlEmailTemplate.append("<font style='font-weight:bold'><i>Días del evento</i></font>");
		htmlEmailTemplate.append("<table border='1' cellpadding='0' cellspacing='0'>");
		htmlEmailTemplate.append("<tr>");
		htmlEmailTemplate.append("<td width='200' height='30' bgcolor='#0088CC' style='font-weight:bold;text-align:center'><font color='FFFFFF'>FECHA</font></td>");
		htmlEmailTemplate.append("<td width='200' height='30' bgcolor='#0088CC' style='font-weight:bold;text-align:center'><font color='FFFFFF'>HORA DESDE</font></td>");
		htmlEmailTemplate.append("<td width='200' height='30' bgcolor='#0088CC' style='font-weight:bold;text-align:center'><font color='FFFFFF'>HORA HASTA</font></td>");
		htmlEmailTemplate.append("</tr>");
		for (VmdbDetalleReserva detalleReserva : reserva.getVmdbDetalleReservas()) {
			if(detalleReserva.getStDetalleReserva().equals('1')){
				htmlEmailTemplate.append("<tr>");
				htmlEmailTemplate.append("<td height='30' style='text-align:center'>"+detalleReserva.getDeFecha()+"</td>");
				htmlEmailTemplate.append("<td height='30' style='text-align:center'>"+detalleReserva.getHoraDesde()+"</td>");
				htmlEmailTemplate.append("<td height='30' style='text-align:center'>"+detalleReserva.getHoraHasta()+"</td>");
				htmlEmailTemplate.append("</tr>");
			}
		}			
		htmlEmailTemplate.append("</table>");
		htmlEmailTemplate.append("</div>");
		htmlEmailTemplate.append("</body>");
		htmlEmailTemplate.append("<p></p>");
		htmlEmailTemplate.append("<p></p>");
		htmlEmailTemplate.append("<font><i>You are requested to participate in the meeting.</i></font>");
		htmlEmailTemplate.append("</html>");
		/**Enviar mail invitacion----------------------------**/
		HtmlEmail email = new HtmlEmail();			
		email.setFrom(Play.configuration.getProperty("mail.smtp.user"),"Calendar");
		String [] arrayMail = reserva.getDeInvitados().split(",");
		for(int i = 0 ; i < arrayMail.length ; i++){
			email.addTo(arrayMail[i]);
		}
		email.addTo(emailUser);
		email.setSubject("Recordatorio: "+reserva.getDeEvento());
		email.setHtmlMsg(htmlEmailTemplate.toString());
		//set the alternative message
	    email.setTextMsg("Your email client does not support HTML messages");
		Mail.send(email);	
		band = 1;
		return band;		
		/**--------------------------------------------------**/		
	}
	
	
//	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");
//	Date date = new Date();
//	System.out.println(dateFormat.format(date)); 
	
//	SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy HH:mm");
//	int n1 = map.get("hora").toString().indexOf(":");//8:00
//	int h = Integer.parseInt(map.get("hora").toString().substring(0, n1));
//	String hora = "";
//	if(h<10){
//		hora = "0"+h+":00";
//	}else{
//		hora = map.get("hora").toString();
//	}
//	String dateInString = map.get("fecha").toString() +" "+hora;
//	Date date = sdf.parse(dateInString);
//	System.out.println(date); 
	
	
  
}
