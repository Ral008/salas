package models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.db.jpa.GenericModel;
import flexjson.JSONSerializer;

/**
 * VmdbPersona generated by hbm2java
 */
@Entity
@Table(name = "STDB_SALA")
public class VmdbSala extends GenericModel {
	
	@Id
	@GeneratedValue(generator="SEQ_SALA", strategy=GenerationType.SEQUENCE)
	@SequenceGenerator(name="SEQ_SALA", sequenceName="SEQ_SALA", allocationSize=1)
	@Column(name = "CO_SALA")
	private Long coSala;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CO_LOCAL", nullable = false)
	private VmdbLocal vmdbLocal;
		
	@Column(name = "DE_NOMBRE", nullable = false, length = 100)
	private String deNombre;
	
	@Column(name = "AFORO")
	private Long aforo;
	
	@Column(name = "DE_DIMENSION", nullable = false, length = 50)
	private String deDimension;
	
	@Column(name = "DE_UBICACION", nullable = false, length = 50)
	private String deUbicacion;
	
	@Column(name = "DE_DESCRIPCION", nullable = false, length = 800)
	private String deDescripcion;
	
	@Column(name = "DE_AVISO", nullable = false, length = 800)
	private String deAviso;
	
	@Column(name = "ST_SALA", nullable = false, length = 1)
	private Character stSala;
	
	@Column(name = "CO_USUARIO_CREACION", nullable = false, length = 50)
	private String coUsuarioCreacion;
	
	@Column(name = "CO_USUARIO_MODIFICACION", length = 50)
	private String coUsuarioModificacion;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DA_FECHA_CREACION", nullable = false, length = 23)
	private Date daFechaCreacion;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DA_FECHA_MODIFICACION", length = 23)
	private Date daFechaModificacion;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "vmdbSala", cascade= CascadeType.ALL)
	private Set<VmdbDetalleSala> vmdbDetalleSalas = new HashSet<VmdbDetalleSala>(0);
	
	public VmdbSala() {
	}		
	
	public Long getCoSala() {
		return coSala;
	}

	public void setCoSala(Long coSala) {
		this.coSala = coSala;
	}

	public VmdbLocal getVmdbLocal() {
		return vmdbLocal;
	}

	public void setVmdbLocal(VmdbLocal vmdbLocal) {
		this.vmdbLocal = vmdbLocal;
	}

	public String getDeNombre() {
		return deNombre;
	}

	public void setDeNombre(String deNombre) {
		this.deNombre = deNombre;
	}

	public Long getAforo() {
		return aforo;
	}

	public void setAforo(Long aforo) {
		this.aforo = aforo;
	}

	public String getDeDimension() {
		return deDimension;
	}

	public void setDeDimension(String deDimension) {
		this.deDimension = deDimension;
	}

	public String getDeUbicacion() {
		return deUbicacion;
	}

	public void setDeUbicacion(String deUbicacion) {
		this.deUbicacion = deUbicacion;
	}
	
	public String getDeDescripcion() {
		return deDescripcion;
	}

	public void setDeDescripcion(String deDescripcion) {
		this.deDescripcion = deDescripcion;
	}

	public String getDeAviso() {
		return deAviso;
	}

	public void setDeAviso(String deAviso) {
		this.deAviso = deAviso;
	}

	public Character getStSala() {
		return stSala;
	}

	public void setStSala(Character stSala) {
		this.stSala = stSala;
	}

	public String getCoUsuarioCreacion() {
		return coUsuarioCreacion;
	}

	public void setCoUsuarioCreacion(String coUsuarioCreacion) {
		this.coUsuarioCreacion = coUsuarioCreacion;
	}

	public String getCoUsuarioModificacion() {
		return coUsuarioModificacion;
	}

	public void setCoUsuarioModificacion(String coUsuarioModificacion) {
		this.coUsuarioModificacion = coUsuarioModificacion;
	}

	public Date getDaFechaCreacion() {
		return daFechaCreacion;
	}

	public void setDaFechaCreacion(Date daFechaCreacion) {
		this.daFechaCreacion = daFechaCreacion;
	}

	public Date getDaFechaModificacion() {
		return daFechaModificacion;
	}

	public void setDaFechaModificacion(Date daFechaModificacion) {
		this.daFechaModificacion = daFechaModificacion;
	}

	public Set<VmdbDetalleSala> getVmdbDetalleSalas() {
		return vmdbDetalleSalas;
	}

	public void setVmdbDetalleSalas(Set<VmdbDetalleSala> vmdbDetalleSalas) {
		this.vmdbDetalleSalas = vmdbDetalleSalas;
	}

	public static List<VmdbSala> listSala(String name) {
    	List<VmdbSala> list = VmdbSala.find("UPPER(deNombre) like ? and stSala in (?,?) order by deNombre asc", "%"+name.toUpperCase()+"%", '1','2').fetch();
        return list;
    }
	
	public static String eliminar(Long id, String usuario) {
		Map result = new HashMap();
		VmdbSala sala = VmdbSala.findById(id);
		if(sala != null){
			sala.setStSala('0');
			sala.setCoUsuarioModificacion(usuario);
			sala.setDaFechaModificacion(new Date());
			sala.save();
			result.put("status",1);
			result.put("message","La sala fue eliminado correctamente!");
		}else{
			result.put("status",0);
			result.put("message","No puede ser eliminado");
		}
		JSONSerializer mapeo = new JSONSerializer();		
		return mapeo.serialize(result);
	}
	
	public static String bloquear(Long id, String usuario) {
		Map result = new HashMap();
		VmdbSala sala = VmdbSala.findById(id);
		if(sala != null){
			sala.setStSala('2');
			sala.setCoUsuarioModificacion(usuario);
			sala.setDaFechaModificacion(new Date());
			sala.save();
			result.put("status",1);
			result.put("message","La sala fue bloqueado correctamente!");
		}else{
			result.put("status",0);
			result.put("message","No puede ser bloqueado");
		}
		JSONSerializer mapeo = new JSONSerializer();		
		return mapeo.serialize(result);
	}
	
	public static String desBloquear(Long id, String usuario) {
		Map result = new HashMap();
		VmdbSala sala = VmdbSala.findById(id);
		if(sala != null){
			sala.setStSala('1');
			sala.setCoUsuarioModificacion(usuario);
			sala.setDaFechaModificacion(new Date());
			sala.save();
			result.put("status",1);
			result.put("message","La sala fue Desbloqueado correctamente!");
		}else{
			result.put("status",0);
			result.put("message","No puede ser Desbloqueado");
		}
		JSONSerializer mapeo = new JSONSerializer();		
		return mapeo.serialize(result);
	}
	
	public static List<Map> buscarSalaById(Long id) throws SQLException{
		List<Map> result = new ArrayList<Map>();
		VmdbSala sala = VmdbSala.findById(id);
		Map map = new HashMap();		
		map.put("coSala", sala.getCoSala());
		map.put("coLocal", sala.getVmdbLocal().getCoLocal());	
		map.put("deNombre", sala.getDeNombre());
		map.put("aforo", sala.getAforo());
		map.put("deDimension", sala.getDeDimension());
		map.put("deUbicacion", sala.getDeUbicacion());
		map.put("deDescripcion", sala.getDeDescripcion());
		map.put("deAviso", sala.getDeAviso());
		result.add(map);
		return result;
	}
	
	public static List<Map> listarDetalleDeLaSala(Long id) throws SQLException {
		List<Map> result = new ArrayList<Map>();
		VmdbSala sala = VmdbSala.findById(id);
		for (VmdbDetalleSala obj : sala.getVmdbDetalleSalas()) {
	    	Map map = new HashMap();
	    	if(obj.getStDetalleSala().equals('1')){
	    		map.put("id", obj.getCoDetalleSala());
		    	map.put("coMaterial", obj.getVmdbMaterial().getCoMaterial());
		    	map.put("materialName", obj.getVmdbMaterial().getDeNombre());		    	
		    	result.add(map);
	    	}	    	
		}
		return result;		
	}

}
