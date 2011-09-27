package com.lkc.controllers;

import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Textbox;

import com.lkc.constraints.NoEmptyTextConstraint;
import com.lkc.dao.UserDAO;
import com.lkc.entities.User;
import com.lkc.utils.ComposerUtil;
import com.lkc.utils.MessageUtil;
import com.lkc.utils.Util;

public class AddUser extends GenericAutowireComposer {

	private static final long serialVersionUID = 3264933347192826541L;

	private Textbox usernameTextbox;
	private Textbox hiddenPasswordTexbox;
	private Textbox realNameTextbox;
	private Checkbox admincheckbox;
	private Textbox passwordTextbox;

	private Button createButton;
	private Button closeButton;

	private Component component;
	private DelegatingVariableResolver resolver;
	private UserDAO userDAO;
	private User user;
	private ActionTrigger actionTrigger;
	private MessageUtil messageUtil;
	private User currentUser;

	public AddUser() {
		resolver = Util.getSpringDelegatingVariableResolver();
		userDAO = (UserDAO) resolver.resolveVariable("userDAO");
		messageUtil = (MessageUtil) resolver.resolveVariable("messageUtil");
		currentUser = Util.getCurrentUser();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		this.component = comp;
		if (currentUser.isAdmin()) {
			// Args
			Map<String, Object> args = execution.getArg();
			user = (User) args.get(ComposerUtil.USER_KEY);
			actionTrigger = (ActionTrigger) args.get(ComposerUtil.ACTION_KEY);
			if (user != null) {
				usernameTextbox.setValue(user.getUserName());
				passwordTextbox.setValue(user.getPassword());
				hiddenPasswordTexbox.setValue(user.getPassword());
				admincheckbox.setChecked(user.isAdmin());
				realNameTextbox.setValue(user.getRealName());
			}
			// Listener
			addListener();
			// Constraint
			addConstraint();
		} else {
			messageUtil.showError(Labels.getLabel("error"), Labels.getLabel("no-permission"));
			comp.detach();
		}
	}

	private void addListener() {
		closeButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = -455210161476604842L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				component.detach();
			}
		});
		createButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = 8743996996301181293L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				String username = usernameTextbox.getValue().trim();
				String password = hiddenPasswordTexbox.getValue();
				String realName = realNameTextbox.getValue();
				boolean isAdmin = admincheckbox.isChecked();
				if (user == null) {
					user = new User(System.currentTimeMillis());
				}
				try {
					user.setUserName(username);
					user.setRealName(realName);
					user.setPassword(password);
					user.setAdmin(isAdmin);
					userDAO.saveOrUpdate(user);
					messageUtil.showMessage(Labels.getLabel("message"), Labels.getLabel("save") + " " + Labels.getLabel("success-lower"));
					component.detach();
					if (actionTrigger != null) {
						actionTrigger.doAction(user);
					}
				} catch (Exception e) {
					e.printStackTrace();
					messageUtil.showError(Labels.getLabel("error"), Labels.getLabel("save") + " " + Labels.getLabel("fail-lower"));
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void addConstraint() {
		NoEmptyTextConstraint usernameConstraint = new NoEmptyTextConstraint(Labels.getLabel("username"));
		usernameConstraint.setConstraint(new Constraint() {

			@Override
			public void validate(Component arg0, Object arg1) throws WrongValueException {
				String value = arg1.toString().trim();
				if (userDAO.checkExistByField("userName", value)) {
					throw new WrongValueException(arg0, Labels.getLabel("username-exist", new Object[] { value }));
				}
			}
		});
		usernameTextbox.setConstraint(usernameConstraint);
		NoEmptyTextConstraint realNameConstraint = new NoEmptyTextConstraint(Labels.getLabel("realName"));
		realNameTextbox.setConstraint(realNameConstraint);
	}
}
