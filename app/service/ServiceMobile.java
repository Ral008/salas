package service;


public class ServiceMobile {

	/*public static String getAllProductos(Long coPersona) {		
		List<Object> productos  = VmdbProducto.getAllProductos(coPersona);
		JSONSerializer mapeo = new JSONSerializer();
		return mapeo.serialize(productos);		
	}
	
	public static String getAllFerulas(Long coPersona) {		
		List<Object> plantillas  = VmdbPlantilla.getAllFerulas(coPersona);
		JSONSerializer mapeo = new JSONSerializer();
		return mapeo.serialize(plantillas);		
	}
	
	public static String guardarVisitaNoProgramada(String dePersona,Long coPersona,Long coCategoria, Long coGrupo, Long coCiclo, Long coConInstEsp,String productos, String ptoEncuentro, Long coInstitucion, Long noDisponible, String observacion) throws SQLException,Exception {
		Map result = VmdbVisita.guardarVisitaSinProgramar(productos, dePersona, ptoEncuentro, coCiclo, coPersona, coConInstEsp, coCategoria, coGrupo, coInstitucion, noDisponible, observacion);
		JSONSerializer mapeo = new JSONSerializer();		
		return mapeo.serialize(result);
	}*/
	
	//new
	/*public static String getAllRepresentantes(Long coPersona) throws SQLException {		
		List<Map> representantes  = VmdbPersona.listarRepresentantesforMobile(coPersona);
		JSONSerializer mapeo = new JSONSerializer();
		return mapeo.serialize(representantes);		
	}

	public static String getProductosxCicloxGrupoxEspecialidadxCategoria(Long grupo,Long especialidad, Long ciclo,Long categoria,Long persona,Long institucion) {
		List<Object> productos = VmdrMalla.listProductosxCicloxGrupoxEspecialidadxCategoria(grupo,especialidad,ciclo,categoria,persona,institucion);
		JSONSerializer mapeo = new JSONSerializer();
		return mapeo.serialize(productos);		
	}

	public static String getMedicosParaCortesia(Long grupo, Long zona , String medico, Long persona) {
		List<Object> medicos = VmdrContactoGrupoZonaCategoria.listarMedicosCortesia(grupo,zona,medico, persona);
		JSONSerializer mapeo = new JSONSerializer();
		return mapeo.serialize(medicos);		
	}

	public static String getProductosxStockPersona(Long coPersona) {
		List<Object> productos = VmdrPersonaProducto.listProductosxPersona(coPersona);
		JSONSerializer mapeo = new JSONSerializer();
		return mapeo.serialize(productos);		
	}
	
	public static String guardarVisitaProgramada(String deObservacion,String ptoEncuentro,String productos,Long coVisita, Long coPersona,String dePersona, String descargaProd) throws SQLException{
		Map result = VmdbVisita.guardarVisitaProgramada(deObservacion,ptoEncuentro,productos,coVisita,coPersona,dePersona, descargaProd);
		JSONSerializer mapeo = new JSONSerializer();		
		return mapeo.serialize(result);
	}
	
	public static String guardarVisitaDeCortesia(String dePersona,Long coPersona,Long coCiclo,Long coGrupo,Long coConInstEsp,String productos,String ptoEncuentro, Long coInstitucion) throws SQLException{
		Map result = VmdbVisita.guardarVisitaDeCortesia(dePersona,productos,coPersona,coCiclo,coGrupo,coConInstEsp,ptoEncuentro,coInstitucion);
		JSONSerializer mapeo = new JSONSerializer();
		return mapeo.serialize(result);
	}

	public static String getProductosXVisita(Long coVisita) throws SQLException {
		List<Map> result = VmdbVisita.listProductosByVisita(coVisita);
		JSONSerializer mapeo = new JSONSerializer();		
		return mapeo.serialize(result);
	}

	public static String guardarDatosDelMedico(Long medico, String email,String fecha, String telefono, String dni, String direccionad, String equipo, String lanpass) throws SQLException {				
		JSONSerializer json = new JSONSerializer();
		Map mapeo = new HashMap();
		int count = VmdbMedico.guardarDatosDelMedico(medico,email,fecha,telefono, dni, direccionad, equipo, lanpass);
		if(count > 0){
			mapeo.put("status", 1);
		}else{
			mapeo.put("status", 0);
		}
		return json.serialize(mapeo);
	}

	public static String listarSemanasDelMes() throws ParseException {
		Calendar calendario = Calendar.getInstance();
		Date fecha=new Date();
		calendario.setTime(fecha);
		String mes=String.valueOf(calendario.get(Calendar.MONTH)+1);
		String ano=String.valueOf(calendario.get(Calendar.YEAR));
		List<VmdbCalendar> semanas = util.DateUtil.getWeekList(ano,mes);
		JSONSerializer json = new JSONSerializer().include("coWeekCompplete").exclude("*");
		return json.serialize(semanas);
	}

	public static String listarMedicosEnLaSemana(Long persona, Long ciclo,Long grupo, Long zona,String ini, String fin) throws SQLException {
		List<Map> result = VmdbVisita.listarMedicosEnLaSemana(persona,ciclo,grupo,zona,ini,fin);
		JSONSerializer mapeo = new JSONSerializer();		
		return mapeo.serialize(result);
	}

	public static String listarVisitasSemanaActual(Long persona, Long ciclo,Long grupo, Long zona)throws SQLException {
		List<Map> result = VmdbVisita.listarVisitasSemanaActual(persona,ciclo,grupo,zona);
		JSONSerializer mapeo = new JSONSerializer();		
		return mapeo.serialize(result);
	}

	public static String listarVisitasDiarioActual(Long persona, Long ciclo,Long grupo, Long zona) throws SQLException {
		List<Map> result = VmdbVisita.listarVisitasDiarioActual(persona,ciclo,grupo,zona);
		JSONSerializer mapeo = new JSONSerializer();		
		return mapeo.serialize(result);
	}

	public static String getDatosRepresentante(Long persona) throws SQLException {
		Map datos = VmdbPersona.getDatos(persona);
		JSONSerializer json = new JSONSerializer(); 
		return json.serialize(datos);
	}
	
	public static String buscarInstitucion(String criteriobusqueda, String pornombre){
		List<VmdbInstitucion> datos;
		if(pornombre.equals("1")){
			datos = VmdbInstitucion.find("DE_NOMBRE LIKE ? ORDER BY DE_NOMBRE ASC", "%"+criteriobusqueda+"%").fetch(70);
		}else{
			datos = VmdbInstitucion.find("DE_NOMBRE LIKE ? ORDER BY DE_NOMBRE ASC", "%"+criteriobusqueda+"%").fetch(70);
		}
		
		JSONSerializer json = new JSONSerializer(); 
		return json.serialize(datos);
	}
	
	public static String getInstitucion(Long coInstitucion){
		VmdbInstitucion institucion = VmdbInstitucion.findById(coInstitucion);
		JSONSerializer json = new JSONSerializer(); 
		return json.serialize(institucion);
	}
	
	//new
	public static String getMedico(Long coMedico){
		VmdbMedico medico = VmdbMedico.findById(coMedico);
		JSONSerializer json = new JSONSerializer();
		return json.serialize(medico);
	}
	
	//new
	public static String getMercado() throws SQLException{		
		List<Map> mercados  = VmdbEstadistica.getMercados();
		JSONSerializer mapeo = new JSONSerializer();
		return mapeo.serialize(mercados);		
	}
	
	//new
	public static String getPeriodo() throws SQLException{		
		List<Map> periodos  = VmdbEstadistica.getPeriodos();
		JSONSerializer mapeo = new JSONSerializer();
		return mapeo.serialize(periodos);		
	}
	
	//new
	public static String listarProductosDeFichaCloseUp(Long coMedico, String coMercado, String coPeriodo) throws SQLException{		
		List<Map> periodos  = VmdbEstadistica.listarProductosDeFichaCloseUp(coMedico, coMercado, coPeriodo);
		JSONSerializer mapeo = new JSONSerializer();
		return mapeo.serialize(periodos);		
	}
	
	public static String guardarDatosInstitucion(Long coInstitucion, String deNombre){
		VmdbInstitucion institucion = VmdbInstitucion.findById(coInstitucion);
		institucion.setDeNombre(deNombre);
		institucion.setStInstitucion('1');
		institucion.save();
		JSONSerializer json = new JSONSerializer(); 
		return "{\"result\" : \"true\"}";//return json.serialize("{\"result\" : \"true\"}");
	}
	
	public static String getEspecialidadesXmedico(Long coMedico){
		List<VmdrMedicoColegio> medicoColegio = VmdrMedicoColegio.find("CO_MEDICO", coMedico).fetch();
		JSONSerializer mapeo = new JSONSerializer();		
		return mapeo.serialize(medicoColegio);
	}
	
	public static String getNotificaciones(Long coPersona){
		List<VmdbNotificacion> notificaciones = VmdbNotificacion.find("ST_NOTIFICACION = 1 AND CO_PERSONA = ? ORDER BY DE_FECHA DESC", coPersona).fetch();
		JSONSerializer mapeo = new JSONSerializer();		
		return mapeo.serialize(notificaciones);
	}
	
	//new
	public static String getProductosMobile(Long coPersona) throws SQLException{
		List<Map> productos  = VmdbPersona.getProductosMobile(coPersona);
		JSONSerializer mapeo = new JSONSerializer();
		return mapeo.serialize(productos);		
	}
	
	//new
	public static String getValidarStockMobile(Long coPersona, Long coProducto) throws SQLException{
		List<Map> producto  = VmdbPersona.getValidarStockMobile(coPersona, coProducto);
		JSONSerializer mapeo = new JSONSerializer();
		return mapeo.serialize(producto);		
	}
	
	//new
	public static String guardarTransferencia(Long coPersona, Long coPersonaAlQTransfiero,int cantidad,Long coProducto) throws SQLException{
		Map result = VmdrPersonaProducto.guardarTransferencia(coPersona,coPersonaAlQTransfiero,cantidad,coProducto);
		JSONSerializer mapeo = new JSONSerializer();		
		return mapeo.serialize(result);
	}*/
}
