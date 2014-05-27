package tags;

import groovy.lang.Closure;
import play.templates.*;
import play.templates.GroovyTemplate.ExecutableTemplate;
import java.util.*;
import java.io.PrintWriter;
import java.lang.reflect.Method;

@FastTags.Namespace("pagination")
public class PaginationTag extends FastTags{

	public static void _pag (Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) throws Exception {
		//Refactorizar
		String titulo=(String) args.get("titulo");
		String cab=(String) args.get("cab");
		List lista=(List) args.get("lista");
		int itemPag=(Integer) args.get("itemPag");
		int cantidadPestana=(Integer) args.get("cantidadPestana");
		int cantidadLista=lista.size();
		int cantidadPag=cantidadLista/itemPag;
		int pag=1;
		if(args.get("pag")!=null){
			pag=(Integer) args.get("pag");
		}
		if(cantidadLista%itemPag!=0){
			cantidadPag++;
		}
		String html="";
		html+="<div id='pagination"+titulo+"'>";
		if(pag==1){
			if(cantidadPag==1 || cantidadPag==0){
				html+="<div class='pagination'><ul>";
				html+="<li class='active'><a href='#' class='pagination"+titulo+"'>1</a></li>";
				html+="</ul></div>";
			}else{
				int inicioPag=0;
				int finPag=0;
				int inicioRango=(cantidadPestana/2)+1;
				int finRango=(cantidadPestana/2)-1;
				if(cantidadPag<cantidadPestana){
					inicioPag=0;
					finPag=cantidadPag;
				}else{
					if(pag>inicioRango && (pag+finRango)<cantidadPag){
						inicioPag=pag-inicioRango;
						finPag=pag+finRango;
					}else if((pag+finRango)>=cantidadPag){
						inicioPag=cantidadPag-cantidadPestana;
						finPag=cantidadPag;
					}else{
						inicioPag=0;
						finPag=cantidadPestana;
					}
				}
				html+="<div class='pagination'><ul>";
				html+="<li class='active'><a href='#' class='pagination"+titulo+"' key='1'>Pri.</a></li>";
				html+="<li class='active'><a href='#' class='pagination"+titulo+"' key='1'>Ant.</a></li>";
				for (int i = inicioPag; i < finPag; i++) {
					if(pag==(i+1)){
						html+="<li class='active'><a href='#' class='pagination"+titulo+"' key='"+(i+1)+"'>"+(i+1)+"</a></li>";
					}else{
						html+="<li><a href='#' class='pagination"+titulo+"' key='"+(i+1)+"'>"+(i+1)+"</a></li>";
					}
				}
				html+="<li><a href='#' class='pagination"+titulo+"' key='2'>Sig.</a></li>";
				html+="<li><a href='#' class='pagination"+titulo+"' key='"+cantidadPag+"'>Ult.</a></li>";
				html+="</ul></div>";
			}
		}else if(cantidadPag==pag){
			int inicioPag=0;
			int finPag=0;
			int inicioRango=(cantidadPestana/2)+1;
			int finRango=(cantidadPestana/2)-1;
			if(cantidadPag<cantidadPestana){
				inicioPag=0;
				finPag=cantidadPag;
			}else{
				if(pag>inicioRango && (pag+finRango)<cantidadPag){
					inicioPag=pag-inicioRango;
					finPag=pag+finRango;
				}else if((pag+finRango)>=cantidadPag){
					inicioPag=cantidadPag-cantidadPestana;
					finPag=cantidadPag;
				}else{
					inicioPag=0;
					finPag=cantidadPestana;
				}
			}
			html+="<div class='pagination'><ul>";
			html+="<li><a href='#' class='pagination"+titulo+"' key='1'>Pri.</a></li>";
			html+="<li><a href='#' class='pagination"+titulo+"' key='"+(pag-1)+"'>Ant.</a></li>";
			for (int i = inicioPag; i < finPag; i++) {
				if(pag==(i+1)){
					html+="<li class='active'><a href='#' class='pagination"+titulo+"' key='"+(i+1)+"'>"+(i+1)+"</a></li>";
				}else{
					html+="<li><a href='#' class='pagination"+titulo+"' key='"+(i+1)+"'>"+(i+1)+"</a></li>";
				}
			}
			html+="<li class='active'><a href='#' class='pagination"+titulo+"' key='"+cantidadPag+"'>Sig.</a></li>";
			html+="<li class='active'><a href='#' class='pagination"+titulo+"' key='"+cantidadPag+"'>Ult.</a></li>";
			html+="</ul></div>";
		}else{
			int inicioPag=0;
			int finPag=0;
			int inicioRango=(cantidadPestana/2)+1;
			int finRango=(cantidadPestana/2)-1;
			if(cantidadPag<cantidadPestana){
				inicioPag=0;
				finPag=cantidadPag;
			}else{
				if(pag>inicioRango && (pag+finRango)<cantidadPag){
					inicioPag=pag-inicioRango;
					finPag=pag+finRango;
				}else if((pag+finRango)>=cantidadPag){
					inicioPag=cantidadPag-cantidadPestana;
					finPag=cantidadPag;
				}else{
					inicioPag=0;
					finPag=cantidadPestana;
				}
			}
			html+="<div class='pagination'><ul>";
			html+="<li><a href='#' class='pagination"+titulo+"' key='1'>Pri.</a></li>";
			html+="<li><a href='#' class='pagination"+titulo+"' key='"+(pag-1)+"'>Ant.</a></li>";
			for (int i = inicioPag; i < finPag; i++) {
				if(pag==(i+1)){
					html+="<li class='active'><a href='#' class='pagination"+titulo+"' key='"+(i+1)+"'>"+(i+1)+"</a></li>";
				}else{
					html+="<li><a href='#' class='pagination"+titulo+"' key='"+(i+1)+"'>"+(i+1)+"</a></li>";
				}
			}
			html+="<li><a href='#' class='pagination"+titulo+"' key='"+(pag+1)+"'>Sig.</a></li>";
			html+="<li><a href='#' class='pagination"+titulo+"' key='"+cantidadPag+"'>Ult.</a></li>";
			html+="</ul></div>";
		}
		html+="<table class='table table-striped table-bordered table-condensed'><thead><tr>";
		String[] listCab=cab.split(",");
		for (String c : listCab) {
			html+="<th>"+c+"</th>";
		}
		html+="</tr></thead>";
		html+="<tbody>";
		int inicioLista=((pag-1)*itemPag);
		int finLista=(pag*itemPag);
		if(finLista>cantidadLista){
			finLista=cantidadLista;
		}
		for (int i = inicioLista; i < finLista; i++) {
			Object object=lista.get(i);
			body.setProperty("item", object);
			html+=JavaExtensions.toString(body);
		}
		html+="</tbody></table>";
		out.println(html);
		out.println("<script type='text/javascript'>");
		out.println("var Paginacion"+titulo+" = {");
		out.println("init : function(){");
		out.println("$('.pagination"+titulo+"').click(Paginacion"+titulo+".paginacion);");
		out.println("},");
		out.println("paginacion : function(e){");
		out.println("e.preventDefault();");
		out.println("if(!$(this).parents('li').hasClass('active')){");
		out.println("$('#pagination"+titulo+"').load(rutaPaginacion"+titulo+"+'?azar='+Math.random(),");
		out.println("{pag : $(this).attr('key')});");
		out.println("}}}");
		out.println("$(function(){");
		out.println("Paginacion"+titulo+".init();");
		out.println("});");
		out.println("</script>");
		out.println("</div>");
	}

}
