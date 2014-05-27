package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

import models.VmdbCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

public class DateUtil {
	
	private static String defaultDatePattern = null;

	public static List getWeekList(String coYear, String nuMes)
			throws java.text.ParseException {
		Calendar cal = Calendar.getInstance();
		int start = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
		String fechaFiltro = start + "/" + nuMes + "/" + coYear;
		fechaFiltro = ActionMethodUtil.generateDateCorrect(fechaFiltro);
		// ///////////////////////////
		Calendar firstDay = Calendar.getInstance();
		try {
			firstDay.setTime(DateUtil.convertStringToDate(fechaFiltro));
		} catch (ParseException e) {
		}
		Calendar lastDay = getUltimoDiaDelMes(fechaFiltro,
				Constante.DATE_PATTERN);// ultimo dia
		List listAll = new ArrayList();
		List weekList = new ArrayList();
		int iniDay = firstDay.get(Calendar.DAY_OF_MONTH);
		int finDay = lastDay.get(Calendar.DAY_OF_MONTH);
		boolean isBackMonth = false;
		boolean isNextMonth = false;
		boolean isNextMonthOther = false;
		int daysBackMonth = 0;
		int daysBeforeMonth = 0;
		int daysBeforeMonthOther = 0;
		Calendar calOther = null;
		for (int i = iniDay; i <= finDay; i++) {
			String currentDay = i + "/" + nuMes + "/" + coYear;
			currentDay = ActionMethodUtil.generateDateCorrect(currentDay);
			int indexDay = getDayOfWeek(currentDay, Constante.DATE_PATTERN);
			if (i == iniDay) {
				if (indexDay != Constante.LUNES) {
					isBackMonth = true; // el ultimo lunes del mes anterior
					if (indexDay == Constante.DOMINGO) {
						daysBackMonth = 6;
					}
					// else{
					// daysBackMonth = indexDay - Constante.LUNES;
					// }
				}
			} else if (i == finDay) {
				if (indexDay != Constante.DOMINGO) {
					isNextMonth = true; // el ultimo lunes del presente mes
					if (indexDay != Constante.LUNES) {
						daysBeforeMonth = indexDay - Constante.LUNES;
					}
				}
			} else if ((i + 6) > finDay) {
				// en el caso que este dia tome parte del siguiente
				// mes pero no sea el dia fin del mes
				isNextMonthOther = true;
				if (indexDay != Constante.LUNES) {
					daysBeforeMonthOther = indexDay - Constante.LUNES;
				}
				try {
					calOther = stringToCalendar(Constante.DATE_PATTERN,
							currentDay);
				} catch (ParseException e) {
				}
			}
			if (indexDay == Constante.LUNES) {
				int rest = 0;
				if ((i + 6) <= finDay) {
					if (indexDay <= 7) {
						rest = 7 - indexDay;
					}
					String sunday = (i + rest) + "/" + nuMes + "/" + coYear;
					sunday = ActionMethodUtil.generateDateCorrect(sunday);
					VmdbCalendar obj = new VmdbCalendar();
					obj.setCoWeek(sunday);
					obj.setDeWeek(currentDay + " - " + sunday);
					obj.setCoWeekCompplete(currentDay + "-" + sunday);
					weekList.add(obj);
				}
				i = i + 6;
			}
		}
		if (isBackMonth) {
			// firstDay.add(Calendar.DAY_OF_MONTH, daysBackMonth);
			String monday = calendarToString(Constante.DATE_PATTERN, firstDay);
			Calendar lastMonday = Calendar.getInstance();
			lastMonday.setTime(DateUtil.convertStringToDate(monday));
			if (firstDay.get(Calendar.DAY_OF_WEEK) != Constante.DOMINGO) {
				firstDay.add(Calendar.DAY_OF_MONTH,
						7 - firstDay.get(Calendar.DAY_OF_WEEK));
			} else {
				firstDay.add(
						Calendar.DAY_OF_MONTH,
						firstDay.get(Calendar.DAY_OF_WEEK)
								- firstDay.get(Calendar.DAY_OF_WEEK));
			}
			String sunday = calendarToString(Constante.DATE_PATTERN, firstDay);
			VmdbCalendar obj = new VmdbCalendar();
			obj.setCoWeek(sunday);
			obj.setDeWeek(monday + " - " + sunday);
			obj.setCoWeekCompplete(monday + "-" + sunday);
			listAll.add(obj);
		}
		if (isNextMonth) {
			if (daysBeforeMonth > 0) {
				lastDay.add(Calendar.DAY_OF_MONTH, -daysBeforeMonth);
				String monday = calendarToString(Constante.DATE_PATTERN,
						lastDay);
				Calendar lastMonday = Calendar.getInstance();
				lastMonday.setTime(DateUtil.convertStringToDate(monday));
				lastDay.add(
						Calendar.DAY_OF_MONTH,
						lastDay.get(Calendar.DAY_OF_WEEK)
								- lastMonday.get(Calendar.DAY_OF_WEEK));
				String sunday = calendarToString(Constante.DATE_PATTERN,
						lastDay);
				VmdbCalendar obj = new VmdbCalendar();
				obj.setCoWeek(sunday);
				obj.setDeWeek(monday + " - " + sunday);
				obj.setCoWeekCompplete(monday + "-" + sunday);
				weekList.add(obj);
			} else {
				// lunes
				String monday = calendarToString(Constante.DATE_PATTERN,
						lastDay);
				Calendar lastMonday = Calendar.getInstance();
				lastMonday.setTime(DateUtil.convertStringToDate(monday));
				lastDay.add(
						Calendar.DAY_OF_MONTH,
						lastDay.get(Calendar.DAY_OF_WEEK)
								- lastMonday.get(Calendar.DAY_OF_WEEK));
				String sunday = calendarToString(Constante.DATE_PATTERN,
						lastDay);
				VmdbCalendar obj = new VmdbCalendar();
				obj.setCoWeek(sunday);
				obj.setDeWeek(monday + " - " + sunday);
				obj.setCoWeekCompplete(monday + "-" + sunday);
				weekList.add(obj);
			}
		}
		if (isNextMonthOther) {
			if (daysBeforeMonthOther > 0) {
				calOther.add(Calendar.DAY_OF_MONTH, -daysBeforeMonthOther);
				String monday = calendarToString(Constante.DATE_PATTERN,
						calOther);
				Calendar lastMonday = Calendar.getInstance();
				lastMonday.setTime(DateUtil.convertStringToDate(monday));
				calOther.add(
						Calendar.DAY_OF_MONTH,
						lastDay.get(Calendar.DAY_OF_WEEK)
								- lastMonday.get(Calendar.DAY_OF_WEEK));
				String sunday = calendarToString(Constante.DATE_PATTERN,
						calOther);
				VmdbCalendar obj = new VmdbCalendar();
				obj.setCoWeek(sunday);
				obj.setDeWeek(monday + " - " + sunday);
				obj.setCoWeekCompplete(monday + "-" + sunday);
				weekList.add(obj);
			} else {
				// lunes
				String monday = calendarToString(Constante.DATE_PATTERN,
						calOther);
				Calendar lastMonday = Calendar.getInstance();
				lastMonday.setTime(DateUtil.convertStringToDate(monday));
				calOther.add(
						Calendar.DAY_OF_MONTH,
						lastDay.get(Calendar.DAY_OF_WEEK)
								- lastMonday.get(Calendar.DAY_OF_WEEK));
				String sunday = calendarToString(Constante.DATE_PATTERN,
						calOther);
				VmdbCalendar obj = new VmdbCalendar();
				obj.setCoWeek(sunday);
				obj.setDeWeek(monday + " - " + sunday);
				obj.setCoWeekCompplete(monday + "-" + sunday);
				weekList.add(obj);
			}
		}
		listAll.addAll(weekList);
		return listAll;
	}

	public static Calendar getUltimoDiaDelMes(String fechaFiltro, String formato)
			throws java.text.ParseException {

		Calendar cal = null;
		try {
			cal = stringToCalendar(formato, fechaFiltro);
		} catch (ParseException e) {
			cal = Calendar.getInstance();
		}
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.getActualMaximum(Calendar.DAY_OF_MONTH),
				cal.getMaximum(Calendar.HOUR_OF_DAY),
				cal.getMaximum(Calendar.MINUTE),
				cal.getMaximum(Calendar.SECOND));
		return cal;
	}

	public static Calendar stringToCalendar(String formatoFecha, String fecha)
			throws java.text.ParseException {

		DateFormat dateFormat = new SimpleDateFormat(formatoFecha);
		Date date = (Date) dateFormat.parse(fecha);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		return calendar;
	}

	public static String calendarToString(String formatoFecha, Calendar calendar) {
		Date date = new Date(calendar.getTimeInMillis());
		DateFormat dateFormat = new SimpleDateFormat(formatoFecha);
		String strToday = dateFormat.format(date);
		return strToday;
	}

	public static int getDayOfWeek(String fechaFiltro, String formato)
			throws java.text.ParseException {
		Calendar cal = null;
		try {
			cal = stringToCalendar(formato, fechaFiltro);
		} catch (ParseException e) {
			cal = Calendar.getInstance();
		}
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	public static Date convertStringToDate(String strDate)
			throws ParseException, java.text.ParseException {
		Date aDate = null;
		aDate = convertStringToDate(getDatePattern(), strDate);
		return aDate;
	}

	public static String getDatePattern() {
		defaultDatePattern = Constante.DATE_PATTERN;
		return defaultDatePattern;
	}

	public static final Date convertStringToDate(String aMask, String strDate)
			throws ParseException, java.text.ParseException {
		SimpleDateFormat df = null;
		Date date = null;
		df = new SimpleDateFormat(aMask);
		date = df.parse(strDate);
		return (date);
	}

	public static String getPrimerDiaDelMes() {
		Calendar cal = Calendar.getInstance();
		int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		String cadena = firstDay + "/" + month + "/" + year;
		return cadena;
	}

	public static String getUltimoDiaDelMes() {
		Calendar cal = Calendar.getInstance();
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		int month = cal.MONTH + 1;
		int year = cal.get(Calendar.YEAR);
		String cadena = lastDay + "/" + month + "/" + year;
		return cadena;
	}

	public static Date getActualDate() throws ParseException,
			java.text.ParseException {
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		String before = day + "/" + month + "/" + year;
		Date chain = convertStringToDate(Constante.DATE_PATTERN, before);

		return chain;
	}

	public static String getTimeActual() {
		SimpleDateFormat simple = new SimpleDateFormat("HH:mm-a");
		Date d = new Date();
		d = new Date(d.getTime());
		String time = simple.format(d);
		return time;
	}
	public static String getDateActual() {
		SimpleDateFormat simple = new SimpleDateFormat("yyyy/MM/dd");
		Date d = new Date();
		d = new Date(d.getTime());
		String time = simple.format(d);
		return time;
	}
	
	public static String getDateForHersil(){
		SimpleDateFormat simple = new SimpleDateFormat("ddMMyyyy");
		Date d = new Date();
		d = new Date(d.getTime());
		String time = simple.format(d);
		return time;
	}
	public static String getHourForHersil() {
		SimpleDateFormat simple = new SimpleDateFormat("HHmmss");
		Date d = new Date();
		d = new Date(d.getTime());
		String time = simple.format(d);
		return time;
	}
	public static String obtenerValorMes(String mes) {

		int resultado = 0;

		if (mes.equals("NU_ENE")) {
			resultado = Integer.parseInt(Constante.NU_ENE);
		}

		if (mes.equals("NU_FEB")) {
			resultado = Integer.parseInt(Constante.NU_FEB);
		}
		if (mes.equals("NU_MAR")) {
			resultado = Integer.parseInt(Constante.NU_MAR);
		}
		if (mes.equals("NU_ABR")) {
			resultado = Integer.parseInt(Constante.NU_ABR);
		}
		if (mes.equals("NU_MAY")) {
			resultado = Integer.parseInt(Constante.NU_MAY);
		}
		if (mes.equals("NU_JUN")) {
			resultado = Integer.parseInt(Constante.NU_JUN);
		}
		if (mes.equals("NU_JUL")) {
			resultado = Integer.parseInt(Constante.NU_JUL);
		}
		if (mes.equals("NU_AGO")) {
			resultado = Integer.parseInt(Constante.NU_AGO);
		}
		if (mes.equals("NU_SEP")) {
			resultado = Integer.parseInt(Constante.NU_SEP);
		}
		if (mes.equals("NU_OCT")) {
			resultado = Integer.parseInt(Constante.NU_OCT);
		}
		if (mes.equals("NU_NOV")) {
			resultado = Integer.parseInt(Constante.NU_NOV);
		}
		if (mes.equals("NU_DIC")) {
			resultado = Integer.parseInt(Constante.NU_DIC);
		}

		return resultado + "";
	}

	

}
