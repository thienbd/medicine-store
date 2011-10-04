package com.lkc.controllers;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

import com.lkc.dao.UserDAO;
import com.lkc.entities.MyUser;
import com.lkc.utils.MessageUtil;
import com.lkc.utils.Util;

public class LoginComposer extends GenericAutowireComposer {
	private static final long serialVersionUID = 9159742662791633735L;

	private Textbox hiddenPasswordTexbox;
	private Textbox usernameTextbox;
	private Button loginButton;
	private Button closeButton;

	private DelegatingVariableResolver resolver;
	private UserDAO userDAO;
	private Component component;
	private MessageUtil messageUtil;

	public LoginComposer() {
		resolver = Util.getSpringDelegatingVariableResolver();
		userDAO = (UserDAO) resolver.resolveVariable("userDAO");
		messageUtil = (MessageUtil) resolver.resolveVariable("messageUtil");
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		this.component = comp;
		// Listener
		addListener();
	}

	private void addListener() {
		closeButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = -9048116919399534939L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				component.detach();
			}
		});
		loginButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = -5629633396436706968L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				String userName = usernameTextbox.getValue();
				String password = hiddenPasswordTexbox.getValue();
				MyUser user = userDAO.login(userName, password);
				session.setAttribute("loginedUser", user);
				if (user == null) {
					messageUtil.showError(Labels.getLabel("fail"), Labels.getLabel("invalid-login"));
				} else {
					messageUtil.showMessage(Labels.getLabel("message"), Labels.getLabel("login") + " " + Labels.getLabel("success-lower"));
					execution.sendRedirect(null);
				}
			}
		});
	}
}
