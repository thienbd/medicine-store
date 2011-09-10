package com.lkc.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.zkoss.spring.DelegatingVariableResolver;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;

import com.lkc.dao.UserDAO;
import com.lkc.entities.User;
import com.lkc.enums.CookieName;
import com.lkc.enums.UserType;

@SuppressWarnings("unchecked")
public class Util {

	private static DelegatingVariableResolver delegatingVariableResolver;
	public static String WELCOME_PAGE = "";
	private static Map<String, String> properties = new HashMap<String, String>();
	public static final String propertiesFilePath = "/WEB-INF/applicationConfig.properties";
	public static final String propertiesCharSet = "UTF-8";
	static {
		try {
			properties.clear();
			InputStream ins = Util.class.getClassLoader().getResourceAsStream("../.." + propertiesFilePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(ins, propertiesCharSet));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("=")) {
					String key = line.substring(0, line.indexOf("="));
					String value = line.substring(line.indexOf("=") + 1);
					properties.put(key, value);
				}
			}
			br.close();
			WELCOME_PAGE = Util.getProperties("welcomePage", "index.lkc");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized static DelegatingVariableResolver getSpringDelegatingVariableResolver() {
		if (delegatingVariableResolver == null) {
			delegatingVariableResolver = new DelegatingVariableResolver();
		}
		return delegatingVariableResolver;
	}

	public static void readConfig() throws IOException {
		properties.clear();
		WebApp webApp = Executions.getCurrent().getSession().getWebApp();
		FileInputStream fis = new FileInputStream(webApp.getRealPath(Util.propertiesFilePath));
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis, Util.propertiesCharSet));
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			if (line.contains("=")) {
				String key = line.substring(0, line.indexOf("="));
				String value = line.substring(line.indexOf("=") + 1);
				properties.put(key, value);
			}
		}
		bufferedReader.close();
	}

	public static Map<String, String> getProperties() {
		return properties;
	}

	public static int getProperties(String key, int defaultValue) {
		String valueString = properties.get(key);
		int result = defaultValue;
		try {
			result = Integer.valueOf(valueString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getProperties(String key, String defaultValue) {
		String valueString = properties.get(key);
		String result = valueString == null ? defaultValue : valueString;
		return result;
	}

	public static double getProperties(String key, double defaultValue) {
		String valueString = properties.get(key);
		double result = defaultValue;
		try {
			result = Double.valueOf(valueString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Locale getSessionLocale() {
		Session session = Executions.getCurrent().getDesktop().getSession();
		Locale locale = (Locale) session.getAttribute(Attributes.PREFERRED_LOCALE);
		if (locale == null) {
			locale = new Locale(getProperties("defaultLang", "en"));
		}
		return locale;
	}

	public static User getCurrentUser() {
		Session session = Executions.getCurrent().getSession();
		User loginedUser = (User) session.getAttribute("loginedUser");
		if (loginedUser != null) {
			UserDAO userDAO = (UserDAO) getSpringDelegatingVariableResolver().resolveVariable("userDAO");
			userDAO.refresh(loginedUser);
		}
		if (loginedUser == null && session.getAttribute("readCookie") == null) {
			readUserCookied();
			loginedUser = (User) session.getAttribute("loginedUser");
			if (loginedUser != null) {
				UserDAO userDAO = (UserDAO) getSpringDelegatingVariableResolver().resolveVariable("userDAO");
				userDAO.refresh(loginedUser);
			}
		}
		if (loginedUser == null) {
			loginedUser = new User(UserType.GUEST.getId());
			loginedUser.setRealName("Guest");
			loginedUser.setUserName("Guest");
			loginedUser.setLanguage(getProperties("defaultLang", "en"));
		}
		session.setAttribute(Attributes.PREFERRED_LOCALE, new Locale(loginedUser.getLanguage()));
		return loginedUser;
	}

	public static String toString(Calendar calendar, boolean showTime) {
		if (calendar == null) {
			return "";
		}
		String result = "";
		String year = getCalendarField(calendar, Calendar.YEAR, 4);
		String month = getCalendarField(calendar, Calendar.MONTH, 2);
		String day = getCalendarField(calendar, Calendar.DATE, 2);
		result += year + "/" + month + "/" + day;
		if (showTime) {
			String hour = getCalendarField(calendar, Calendar.HOUR, 2);
			String minute = getCalendarField(calendar, Calendar.MINUTE, 2);
			String second = getCalendarField(calendar, Calendar.SECOND, 2);
			String milisecond = getCalendarField(calendar, Calendar.MILLISECOND, 2);
			result += " " + hour + ":" + minute + ":" + second + "." + milisecond;
		}
		return result;
	}

	public static String toString(Calendar calendar, boolean showTime, String dateSeperator, String timeSeperator) {
		if (calendar == null) {
			return "";
		}
		String result = "";
		String year = getCalendarField(calendar, Calendar.YEAR, 4);
		String month = getCalendarField(calendar, Calendar.MONTH, 2);
		String day = getCalendarField(calendar, Calendar.DATE, 2);
		result += year + dateSeperator + month + dateSeperator + day;
		if (showTime) {
			String hour = getCalendarField(calendar, Calendar.HOUR, 2);
			String minute = getCalendarField(calendar, Calendar.MINUTE, 2);
			String second = getCalendarField(calendar, Calendar.SECOND, 2);
			String milisecond = getCalendarField(calendar, Calendar.MILLISECOND, 2);
			result += " " + hour + timeSeperator + minute + timeSeperator + second + "." + milisecond;
		}
		return result;
	}

	private static String getCalendarField(Calendar calendar, int field, int length) {
		String value = "" + calendar.get(field);
		while (value.length() < length) {
			value = "0" + value;
		}
		return value;
	}

	public static byte[] readImage(String imagePath) {
		try {
			InputStream ins = Util.class.getClassLoader().getResourceAsStream("../../" + imagePath);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read;
			byte[] data = new byte[102400];
			while ((read = ins.read(data)) != -1) {
				baos.write(data, 0, read);
			}
			ins.close();
			baos.close();
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new byte[] {};
	}

	public static Map<Long, Boolean> getOrganizationClicked() {
		Session session = Executions.getCurrent().getSession();
		Map<Long, Boolean> result = (Map<Long, Boolean>) session.getAttribute("orgClicked");
		if (result == null) {
			result = new HashMap<Long, Boolean>();
			session.setAttribute("orgClicked", result);
		}
		return result;
	}

	public static Map<Long, Boolean> getDataClicked() {
		Session session = Executions.getCurrent().getSession();
		Map<Long, Boolean> result = (Map<Long, Boolean>) session.getAttribute("orgClicked");
		if (result == null) {
			result = new HashMap<Long, Boolean>();
			session.setAttribute("dataClicked", result);
		}
		return result;
	}

	public static Map<Long, Boolean> getImageClicked() {
		Session session = Executions.getCurrent().getSession();
		Map<Long, Boolean> result = (Map<Long, Boolean>) session.getAttribute("orgClicked");
		if (result == null) {
			result = new HashMap<Long, Boolean>();
			session.setAttribute("imageClicked", result);
		}
		return result;
	}

	public static String generateActiveCode() {
		String code = "";
		String characterList = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
		Random rd = new Random();
		while (code.length() < 8) {
			code += characterList.charAt(rd.nextInt(characterList.length()));
		}
		return code;
	}

	public static String generateNumber(int length) {
		String result = "";
		String numberList = "0123456789";
		Random random = new Random();
		while (result.length() < length) {
			result += numberList.charAt(random.nextInt(numberList.length()));
		}
		return result;
	}

	public static Cookie getCookie(String name) {
		Execution execution = Executions.getCurrent();
		Cookie result = null;
		HttpServletRequest request = (HttpServletRequest) execution.getNativeRequest();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					return cookie;
				}
			}
		}
		return result;
	}

	public static String getCookieValue(String name) {
		Execution execution = Executions.getCurrent();
		String result = "";
		HttpServletRequest request = (HttpServletRequest) execution.getNativeRequest();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					return cookie.getValue();
				}
			}
		}
		return result;
	}

	public static String getCookieValue(CookieName cookieName) {
		return getCookieValue(cookieName.getName());
	}

	public static void readUserCookied() {
		String startLengthString = getCookieValue(CookieName.START_LENGTH);
		String engLengthString = getCookieValue(CookieName.END_LENGTH);
		String idString = getCookieValue(CookieName.UID);
		String password = getCookieValue(CookieName.PASSWORD);
		int startLength = 0;
		try {
			startLength = Integer.parseInt(startLengthString);
		} catch (Exception e) {
		}
		int endLength = 0;
		try {
			endLength = Integer.parseInt(engLengthString);
		} catch (Exception e) {
		}
		if (startLength > 0 && endLength > 0 && idString.length() > (startLength + endLength)) {
			try {
				long uid = Long.parseLong(idString.substring(startLength, idString.length() - endLength));
				UserDAO userDAO = (UserDAO) getSpringDelegatingVariableResolver().resolveVariable("userDAO");
				User user = userDAO.login(uid, password);
				Session session = Executions.getCurrent().getSession();
				session.setAttribute("readCookie", true);
				session.setAttribute("loginedUser", user);
			} catch (Exception e) {
			}
		}
	}

	public static List<Cookie> createCookie(Map<CookieName, String> cookieMap, int version, String path, int maxAge) {
		List<Cookie> result = new ArrayList<Cookie>();
		Set<CookieName> cookieNamesSet = cookieMap.keySet();
		for (CookieName cookieName : cookieNamesSet) {
			Cookie cookie = new Cookie(cookieName.getName(), cookieMap.get(cookieName));
			cookie.setVersion(version);
			cookie.setPath(path);
			cookie.setMaxAge(maxAge);
			result.add(cookie);
		}
		return result;
	}

	public static void writeError(Exception exception) {
		try {
			StackTraceElement[] elements = exception.getStackTrace();
			WebApp webApp = Executions.getCurrent().getSession().getWebApp();
			File file = new File(webApp.getRealPath("/error/error_" + Util.toString(new GregorianCalendar(), false, "_", "_") + ".log"));
			FileOutputStream fos = new FileOutputStream(file, true);
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"));
			pw.println("----------------------------------------------------");
			pw.println(exception);
			for (StackTraceElement stackTraceElement : elements) {
				pw.println(stackTraceElement);
			}
			pw.close();
		} catch (Exception e) {
		}
	}

}
