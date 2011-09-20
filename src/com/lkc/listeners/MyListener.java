package com.lkc.listeners;

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.EventInterceptor;
import org.zkoss.zk.ui.util.RequestInterceptor;

import com.lkc.enums.CookieName;
import com.lkc.utils.Util;

public class MyListener implements RequestInterceptor, EventInterceptor {

	@Override
	public void afterProcessEvent(Event arg0) {
	}

	@Override
	public Event beforePostEvent(Event arg0) {
		return arg0;
	}

	@Override
	public Event beforeProcessEvent(Event arg0) {
		return arg0;
	}

	@Override
	public Event beforeSendEvent(Event arg0) {
		return arg0;
	}

	@Override
	public void request(Session session, Object request, Object response) {
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		final Cookie[] cookies = servletRequest.getCookies();
		if (cookies != null && session.getAttribute(Attributes.PREFERRED_LOCALE) == null) {
			for (int j = cookies.length; --j >= 0;) {
				String cookieName = cookies[j].getName();
				String cookieValue = cookies[j].getValue();
				if (cookieName.equals(CookieName.LANGUAGE.getName())) {
					// Check if session locale is null
					if (session.getAttribute(Attributes.PREFERRED_LOCALE) == null) {
						// determine the locale
						if (cookieValue.equalsIgnoreCase("vi") || cookieValue.equalsIgnoreCase("en")) {
							Locale locale = org.zkoss.util.Locales.getLocale(cookieValue);
							session.setAttribute(Attributes.PREFERRED_LOCALE, locale);
						}
					}
				}
			}
		}
		if (session.getAttribute(Attributes.PREFERRED_LOCALE) == null) {
			session.setAttribute(Attributes.PREFERRED_LOCALE, new Locale(Util.getProperties("defaultLang", "vi")));
		}
	}
}
