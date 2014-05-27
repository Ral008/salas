package controllers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.MailcapCommandMap;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import play.Play;
import play.libs.Mail;
import play.mvc.Controller;

public class Main extends Controller {

    public static void index() throws Exception {
    	session.put("usuario", "Test");
    	String nombre = session.get("usuario");    	    
    	
//    	MimetypesFileTypeMap mimetypes = (MimetypesFileTypeMap)MimetypesFileTypeMap.getDefaultFileTypeMap();
//        mimetypes.addMimeTypes("text/calendar ics ICS");        
//        MailcapCommandMap mailcap = (MailcapCommandMap) MailcapCommandMap.getDefaultCommandMap();
//        mailcap.addMailcap("text/calendar;; x-java-content-handler=com.sun.mail.handlers.text_plain");
    	
        //para enviar mail new
    	String CRLF = "\n";
    	SimpleDateFormat iCalendarDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmm'00'");
    	Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date start = cal.getTime();
        cal.add(Calendar.HOUR_OF_DAY, 3);
        Date end = cal.getTime();
        
        Properties props = System.getProperties();
		props.setProperty("mail.smtp.host", Play.configuration.getProperty("mail.smtp.host"));		
		Session session = Session.getInstance(props,new javax.mail.Authenticator(){
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(Play.configuration.getProperty("mail.smtp.user"), Play.configuration.getProperty("mail.smtp.pass"));
	        }
	    });
		session.setDebug(true);
		MimeMessage message = new MimeMessage(session);
		message.addHeaderLine("method=REQUEST");
		message.addHeaderLine("charset=\"UTF-8\"");
		message.addHeaderLine("component=VEVENT");
		message.setFrom(new InternetAddress(Play.configuration.getProperty("mail.smtp.user"),"Notificación de Invitación"));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress("raul_lucero07@hotmail.com"));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress("rlucero.as@gmail.com"));
		message.setSubject("Meeting Request Using JavaMail");
		Multipart multipart = new MimeMultipart();
		MimeBodyPart htmlBodyPart = new MimeBodyPart();
		htmlBodyPart.setContent(
				"<html><body>HTML : You are requested to participlate in the review meeting.</body></html>",
				"text/html");
		multipart.addBodyPart(htmlBodyPart);
		
		String rand = UUID.randomUUID().toString();
		StringBuffer buffer = new StringBuffer();
		// VCALENDAR begin
		buffer.append("BEGIN:VCALENDAR" + CRLF);
		buffer.append("PRODID:BCP - Meeting" + CRLF);
		buffer.append("VERSION:2.0" + CRLF);
		buffer.append("CALSCALE:GREGORIAN" + CRLF);
		buffer.append("METHOD:REQUEST" + CRLF);
		// VCALENDAR END:VEVENT
		buffer.append("BEGIN:VEVENT" + CRLF);
		buffer.append("ORGANIZER:mailto:" + "rlucero.as@gmail.com" + CRLF);
		buffer.append("ATTENDEE;ROLE=CHAIR;RSVP=TRUE:mailto:raul_lucero07@hotmail.com" + CRLF);
		buffer.append("DTSTAMP:"+iCalendarDateFormat.format(start) + CRLF);
		buffer.append("DTSTART:"+iCalendarDateFormat.format(start) + CRLF);
		buffer.append("DTEND:"+iCalendarDateFormat.format(end) + CRLF);
		buffer.append("LOCATION:Conference room 11" + CRLF);
		buffer.append("DESCRIPTION:This the description of the meeting." + CRLF + CRLF);
		buffer.append("SUMMARY:The summary" + CRLF);
		buffer.append("UID:" + rand + CRLF);		
		buffer.append("CATEGORIES:Meeting" + CRLF);
		buffer.append("PRIORITY:5" + CRLF);
		buffer.append("CLASS:PUBLIC" + CRLF);
		buffer.append("TRANSP:OPAQUE" + CRLF);
		buffer.append("SEQUENCE:0" + CRLF);
		// VALARM begin
		buffer.append("BEGIN:VALARM" + CRLF);
		buffer.append("TRIGGER:PT1440M" + CRLF);
		buffer.append("ACTION:DISPLAY" + CRLF);
		buffer.append("DESCRIPTION:Reminder" + CRLF);
		buffer.append("END:VALARM" + CRLF);
		// VEVENT end
		buffer.append("END:VEVENT" + CRLF);
		// VCALENDAR end
		buffer.append("END:VCALENDAR");
		
		// Create second body part
		MimeBodyPart attachPart = new MimeBodyPart();
		String filename = "invitation.ics";
		attachPart.setFileName(filename);
		attachPart.setHeader("Content-Class", "urn:content-classes:calendarmessage");
		attachPart.setHeader("Content-ID", "calendar_message");
		attachPart.setDataHandler(new DataHandler(new ByteArrayDataSource(buffer.toString(), "text/calendar")));
		// Add part two
		multipart.addBodyPart(attachPart);
		// Put parts in message
		message.setContent(multipart);
		// send message
		Transport transport = session.getTransport("smtp");
	    transport.connect(Play.configuration.getProperty("mail.smtp.user"), Play.configuration.getProperty("mail.smtp.pass"));
	    transport.sendMessage(message, message.getAllRecipients());
	    transport.close();
	    System.out.println("send mail over");
	    
        render(nombre);
    }
    
}
