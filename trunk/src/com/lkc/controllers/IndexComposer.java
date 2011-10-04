package com.lkc.controllers;

import javax.servlet.http.HttpServletRequest;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zul.A;
import org.zkoss.zul.Include;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;

import com.lkc.entities.MyUser;
import com.lkc.enums.UserType;
import com.lkc.utils.Util;

public class IndexComposer extends GenericAutowireComposer {
	private static final long serialVersionUID = -218612071855457831L;

	private A bannerLink;
	private Menubar menubar;

	private Menuitem patientManagementMenuitem;
	private Menuitem medicineManagementMenuitem;
	private Menuitem statisticsMenuitem;
	private Menu welcomeMenuitem;
	private Menuitem loginMenuitem;
	private Menuitem logoutMenuitem;
	private Menuitem usermanagementMenuitem;

	private Include pageInclude;
	private MyUser currentUser;

	public IndexComposer() {

	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentUser = Util.getCurrentUser();
		// Display
		HttpServletRequest request = (HttpServletRequest) execution.getNativeRequest();
		String display = request.getParameter(ParamConst.DISPLAY);
		display = (display == null ? ParamConst.PATIEN_MANAGEMENT : display.trim());
		initDisplay(display);
		// Listener
		addListener();
		// Init Menu
		initMenu();
	}

	private void addListener() {
		bannerLink.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = -291074374979910767L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				execution.sendRedirect(PageConst.INDEX);
			}
		});
	}

	private void initMenu() {
		patientManagementMenuitem.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = 7019445664475386786L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				execution.sendRedirect(PageConst.INDEX + "?" + ParamConst.DISPLAY + "=" + ParamConst.PATIEN_MANAGEMENT);
			}
		});
		medicineManagementMenuitem.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = 4682490764546073242L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				execution.sendRedirect(PageConst.INDEX + "?" + ParamConst.DISPLAY + "=" + ParamConst.MEDICINE_MANAGEMENT);
			}
		});
		statisticsMenuitem.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = 4536058257786105801L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				execution.sendRedirect(PageConst.INDEX + "?" + ParamConst.DISPLAY + "=" + ParamConst.STATISTIC);
			}
		});
		usermanagementMenuitem.setVisible(currentUser.isAdmin());
		if (usermanagementMenuitem.isVisible()) {
			usermanagementMenuitem.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
				private static final long serialVersionUID = 5011980838907585837L;

				@Override
				public void onEvent(Event arg0) throws Exception {
					execution.sendRedirect(PageConst.INDEX + "?" + ParamConst.DISPLAY + "=" + ParamConst.USER_MANAGEMENT);
				}
			});
		}
		loginMenuitem.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = -5338343675315546595L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				execution.sendRedirect(PageConst.INDEX + "?" + ParamConst.DISPLAY + "=" + ParamConst.LOGIN);
			}
		});
		logoutMenuitem.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = -571107319023560214L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				Session session = execution.getSession();
				session.setAttribute("loginedUser", null);
				execution.sendRedirect(null);
			}
		});
	}

	private void initDisplay(String display) {
		welcomeMenuitem.setLabel(Labels.getLabel("welcome") + " " + currentUser.getRealName());
		if (currentUser.getId() != UserType.GUEST.getId()) {
			menubar.setVisible(true);
			if (display.equalsIgnoreCase(ParamConst.PATIEN_MANAGEMENT)) {
				pageInclude.setSrc(PageConst.PATIENT_MANAGEMENT);
				return;
			}
			if (display.equalsIgnoreCase(ParamConst.MEDICINE_MANAGEMENT)) {
				pageInclude.setSrc(PageConst.MEDICINE_MANAGEMENT);
				return;
			}
			if (display.equalsIgnoreCase(ParamConst.STATISTIC)) {
				pageInclude.setSrc(PageConst.STATISTIC);
				return;
			}
			if (display.equalsIgnoreCase(ParamConst.USER_MANAGEMENT)) {
				pageInclude.setSrc(PageConst.USER_MANAGEMENT);
				return;
			}
			if (display.equalsIgnoreCase(ParamConst.LOGIN)) {
				pageInclude.setSrc(PageConst.LOGIN);
				return;
			}
		} else {
			menubar.setVisible(false);
			pageInclude.setSrc(PageConst.LOGIN);
			return;
		}
	}
}
