package util;

public class ActionMethodUtil {

public static String generateDateCorrect(String fechaFiltro){
		
		if(fechaFiltro != null && !fechaFiltro.equalsIgnoreCase("")){
			
			String strDia = "";
			String strMes = "";
			String[] array = fechaFiltro.split("[/]");
			
			int dia  = Integer.parseInt(array[0]);
			int mes  = Integer.parseInt(array[1]);
			int anio = Integer.parseInt(array[2]);
			
			if(dia < 10){
				strDia = "0"+dia;
			}else{
				strDia = ""+dia;
			}
			
			if(mes < 10){
				strMes = "0"+mes;
			}else{
				strMes = ""+mes;
			}
			
			return strDia+"/"+strMes+"/"+anio;
		}
		
		return "";
	}
	
}
