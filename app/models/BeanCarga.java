package models;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import play.db.DB;

public class BeanCarga implements Serializable {
	private int nro;
	private String nombreContacto;
	private String errorNombreContacto;
	private String correoContacto;
	private String errorCorreoContacto;	
	private String errorBD;
	private String ok;
	
	public BeanCarga(){		
	}

	public int getNro() {
		return nro;
	}

	public void setNro(int nro) {
		this.nro = nro;
	}

	public String getNombreContacto() {
		return nombreContacto;
	}

	public void setNombreContacto(String nombreContacto) {
		this.nombreContacto = nombreContacto;
	}		
	
	public String getErrorNombreContacto() {
		return errorNombreContacto;
	}

	public void setErrorNombreContacto(String errorNombreContacto) {
		this.errorNombreContacto = errorNombreContacto;
	}

	public String getCorreoContacto() {
		return correoContacto;
	}

	public void setCorreoContacto(String correoContacto) {
		this.correoContacto = correoContacto;
	}

	public String getErrorCorreoContacto() {
		return errorCorreoContacto;
	}

	public void setErrorCorreoContacto(String errorCorreoContacto) {
		this.errorCorreoContacto = errorCorreoContacto;
	}

	public String getErrorBD() {
		return errorBD;
	}

	public void setErrorBD(String errorBD) {
		this.errorBD = errorBD;
	}

	public String getOk() {
		return ok;
	}

	public void setOk(String ok) {
		this.ok = ok;
	}  
	
	public static List<BeanCarga> listSiExisteMailDeContacto(String adjunto) throws SQLException {
		List<BeanCarga> lista = new ArrayList<BeanCarga>();
		Connection cn = DB.getConnection();
		StringBuilder query = new StringBuilder();
		query.append("SELECT NRO FROM (");
		query.append(adjunto);
		query.append(") T ");
		query.append("WHERE ( ");
		query.append("SELECT COUNT(*) FROM STDB_CONTACTO ");
		query.append("WHERE UPPER(DE_CORREO) = UPPER(T.MAIL_CONTACTO) ");
		query.append(") >= 1 ");
		PreparedStatement pr = cn.prepareStatement(query.toString());
		ResultSet rs = pr.executeQuery();
		while(rs.next()){
			BeanCarga v=new BeanCarga();
			v.setNro(rs.getInt(1));
			lista.add(v);
		}
		return lista;
	}
	
}
