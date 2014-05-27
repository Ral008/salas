package models;

public class VmdbCalendar {

	private String coYear;
	private String deYear;
	
	private String coDay;
	private String deDay;
	
	private String coWeek;
	private String deWeek;
	private String coWeekCompplete;
	
	public VmdbCalendar() {
	}
	
	public VmdbCalendar(String coYear, String deYear,String coDay,String deDay){
		this.coYear = coYear;
		this.deYear = deYear;
		this.coDay  = coDay;
		this.deDay  = deDay;
	}
	
	public String getCoYear() {
		return coYear;
	}
	public void setCoYear(String coYear) {
		this.coYear = coYear;
	}
	public String getDeYear() {
		return deYear;
	}
	public void setDeYear(String deYear) {
		this.deYear = deYear;
	}

	public String getCoDay() {
		return coDay;
	}

	public void setCoDay(String coDay) {
		this.coDay = coDay;
	}

	public String getDeDay() {
		return deDay;
	}

	public void setDeDay(String deDay) {
		this.deDay = deDay;
	}

	public String getCoWeek() {
		return coWeek;
	}

	public void setCoWeek(String coWeek) {
		this.coWeek = coWeek;
	}

	public String getDeWeek() {
		return deWeek;
	}

	public void setDeWeek(String deWeek) {
		this.deWeek = deWeek;
	}

	public String getCoWeekCompplete() {
		return coWeekCompplete;
	}

	public void setCoWeekCompplete(String coWeekCompplete) {
		this.coWeekCompplete = coWeekCompplete;
	}
	
	
}
