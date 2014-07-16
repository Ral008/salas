package controllers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.BeanCarga;
import models.VmdbContacto;
import models.VmdbPersona;

import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;

@With({Security.class,Secure.class})
public class UploadContacto extends Controller {
	
	public static void index() {
		render("Mantenimientos/uploadContacto.html");
    }

	public static void leerExcel(File carga) throws Exception{
		Map scache = new HashMap();
		WorkbookSettings ws = new WorkbookSettings();
		ws.setEncoding("ISO-8859-1");			
		Workbook workbook = Workbook.getWorkbook(carga,ws);		
		Sheet sheet = workbook.getSheet(0);
		List<BeanCarga> list = new ArrayList<BeanCarga>();
		boolean cargarExcel = true;
		String formatoError = null;
		List<String> listDistinto = new ArrayList<String>();
		List<Integer> listErrorRepetido = new ArrayList<Integer>();
		BeanCarga cv = null;
		StringBuilder query = new StringBuilder();
		String adjunto="";
		int n1 = 0, n2 = 0;
		if(sheet.getColumns()>=1){
			for (int i=1; i<sheet.getRows(); i++) {
				cv = new BeanCarga();				
				if(sheet.getCell(0, i).getContents().toString().trim()!=""){
					cv.setNombreContacto(sheet.getCell(0, i).getContents().toString().trim());	
					n1 = 1;
				}else{
					cv.setErrorNombreContacto("1");		
					cargarExcel=false;
					n1 = 0;
				}				
				if(sheet.getCell(1, i).getContents().toString().trim()!=""){
					cv.setCorreoContacto(sheet.getCell(1, i).getContents().toString().trim());					
					n2 = 1;					
				}else{
					cv.setErrorCorreoContacto("1");					
					cargarExcel=false;
					n2 = 0;
				}
				if(n1==0 || n2==0){
					cv.setOk("");
					cv.setErrorBD("Tiene celdas vacias");
				}else{					
					cv.setOk("1");
					cv.setErrorBD("Correcto");
				}
				
				if(cv.getCorreoContacto()!=null){
					if(listDistinto.contains(cv.getCorreoContacto())){
						listErrorRepetido.add(i);
						cv.setOk("");
					}else{
						listDistinto.add(cv.getCorreoContacto());
					}
				}else{
					cargarExcel=false;
					formatoError="Existen filas vacias, revise el documento";
					break;
				}
				if(query.toString().equals("")){
					query.append("SELECT ");
					query.append(i).append(" as NRO, '");
					query.append(cv.getCorreoContacto()).append("' as MAIL_CONTACTO FROM DUAL ");
					//System.out.println("01 -> " +query.toString());
				}else{	
					query.append("UNION ALL ");
					query.append("SELECT ");
					query.append(i).append(" as NRO, '");
					query.append(cv.getCorreoContacto()).append("' as MAIL_CONTACTO FROM DUAL ");
					//System.out.println("02 -> " +query.toString());
				}
				list.add(cv);
			}
			//System.out.println("Query -> " +query.toString());
			adjunto = query.toString();
			if(cargarExcel){
				List<BeanCarga> listSiExisteContacto = BeanCarga.listSiExisteMailDeContacto(adjunto);
				if(listErrorRepetido.size()!=0){
					for (int ncv : listErrorRepetido) {
						list.get(ncv-1).setErrorBD("El correo del contacto esta repetido en el excel");
					}
					formatoError="El archivo contiene errores";
					cargarExcel=false;
				}					
				if(listSiExisteContacto.size()!=0){
					for (BeanCarga ncv : listSiExisteContacto) {
						list.get(ncv.getNro()-1).setErrorBD("El correo del contacto ya esta registrado en el sistema");
						list.get(ncv.getNro()-1).setOk("");
					}
					formatoError="El archivo contiene errores";
					cargarExcel=false;
				}
			}else{
				if(listErrorRepetido.size()!=0){
					for (int ncv : listErrorRepetido) {
						list.get(ncv-1).setErrorBD("El correo del contacto esta repetido");
					}
					formatoError="El archivo contiene errores";
					cargarExcel=false;
				}
			}
		}else{
			formatoError="Format error";
		}
		scache.put("listCargaContactos", list);
		scache.put("cadenaCargaContactos", adjunto);
		Cache.set("session_listCargaContactos" + session.getId(), scache, "60mn");
    	render("Mantenimientos/leerExcelContacto.html",list,cargarExcel,formatoError);
    }
	
	public static void cargarExcel() throws Exception{
		Map cacheCargaContactos = Cache.get("session_listCargaContactos"+session.getId(),Map.class);
		List<BeanCarga> list = (List<BeanCarga>)cacheCargaContactos.get("listCargaContactos");
		boolean cargarExcel=true;
		String adjunto=(String)cacheCargaContactos.get("cadenaCargaContactos");
		List<BeanCarga> listSiExisteContacto = BeanCarga.listSiExisteMailDeContacto(adjunto);
		if(listSiExisteContacto.size()!=0){
			for (BeanCarga ncv : listSiExisteContacto) {
				list.get(ncv.getNro()-1).setErrorBD("El correo del contacto ya esta registrado en el sistema");
			}
			cargarExcel=false;
		}
		int resultado=0;
		String msj=null;
		String usuario = session.get("usuario");	
		String idPersona = session.get("idPersona");		
		if(cargarExcel){
			VmdbPersona objPersona = VmdbPersona.findById(Long.parseLong(idPersona));
			VmdbContacto contacto = null;
			for (BeanCarga obj : list) {
				contacto = new VmdbContacto();
				contacto.setVmdbPersona(objPersona);
				contacto.setDeNombre(obj.getNombreContacto());
				contacto.setDeCorreo(obj.getCorreoContacto());				
				contacto.setStContacto('1');
				contacto.setCoUsuarioCreacion(usuario);
				contacto.setDaFechaCreacion(new Date());
				contacto.save();
			}						
			resultado = 1;
		}
		if(resultado==1){
			msj="Los contactos fueron ingresados correctamente";
		}
		render("Mantenimientos/leerExcelContacto.html",list,msj);
	}
	
}
