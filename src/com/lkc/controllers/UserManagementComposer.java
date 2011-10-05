package com.lkc.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import com.lkc.dao.UserDAO;
import com.lkc.entities.Doctor;
import com.lkc.utils.ComposerUtil;
import com.lkc.utils.MessageUtil;
import com.lkc.utils.Util;

public class UserManagementComposer extends GenericAutowireComposer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7525784937123769700L;

	private Rows userRows;
	private Paging userPaging;
	private int currentUserPage = 1;
	private int numOfUserPage = 1;
	private int userPerPage = 10;

	private UserDAO userDAO;
	private DelegatingVariableResolver resolver;
	private UserPopup userPopup;
	private Component component;
	private MessageUtil messageUtil;
	private Doctor currentUser;

	public UserManagementComposer() {
		resolver = Util.getSpringDelegatingVariableResolver();
		userDAO = (UserDAO) resolver.resolveVariable("userDAO");
		userPerPage = Util.getProperties("userPerPage", 10);
		userPopup = new UserPopup();
		messageUtil = (MessageUtil) resolver.resolveVariable("messageUtil");
		currentUser = Util.getCurrentUser();
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		this.component = comp;
		if (currentUser.isAdmin()) {
			comp.appendChild(userPopup);
			// Refresh
			refresh();
			// Listener
			addListener();
		} else {
			messageUtil.showError(Labels.getLabel("error"), Labels.getLabel("no-permission"));
			comp.detach();
		}
	}

	private void refresh() {
		long count = userDAO.count();
		numOfUserPage = (int) (count / userPerPage + (count % userPerPage > 0 ? 1 : 0));
		if (numOfUserPage == 0) {
			numOfUserPage = 1;
		}
		if (currentUserPage > numOfUserPage) {
			currentUserPage = numOfUserPage;
		} else {
			if (currentUserPage < 1) {
				currentUserPage = 1;
			}
		}
		userPaging.setVisible(numOfUserPage > 1);
		List<Doctor> listUsers = userDAO.load((currentUserPage - 1) * userPerPage, userPerPage);
		for (final Doctor user : listUsers) {
			Row row = new Row();
			userRows.appendChild(row);
			Label useridLabel = new Label(String.valueOf(user.getId()));
			row.appendChild(useridLabel);
			Label usernameLabel = new Label(user.getUserName());
			row.appendChild(usernameLabel);
			Label realNameLabel = new Label(user.getRealName());
			row.appendChild(realNameLabel);
			Label adminLabel = new Label(user.isAdmin() ? Labels.getLabel("admin") : "");
			row.appendChild(adminLabel);
			Button actionButton = new Button(Labels.getLabel("action"));
			row.appendChild(actionButton);
			actionButton.setPopup(userPopup);
			actionButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
				private static final long serialVersionUID = -8800910076420118855L;

				@Override
				public void onEvent(Event arg0) throws Exception {
					userPopup.setUser(user);
				}
			});
		}
	}

	public class UserPopup extends Popup {

		private static final long serialVersionUID = 2916289757876401572L;

		private A addUserLink;
		private A editUserLink;
		private A deleteUserLink;

		private Vlayout vlayout;
		private Doctor user;

		public UserPopup() {
			vlayout = new Vlayout();
			appendChild(vlayout);
			addUserLink = new A(Labels.getLabel("add-user"));
			vlayout.appendChild(addUserLink);
			editUserLink = new A(Labels.getLabel("edit") + " " + Labels.getLabel("user-lower"));
			vlayout.appendChild(editUserLink);
			deleteUserLink = new A(Labels.getLabel("delete") + " " + Labels.getLabel("user-lower"));
			vlayout.appendChild(deleteUserLink);
		}

		public A getAddUserLink() {
			return addUserLink;
		}

		public void setAddUserLink(A addUserLink) {
			this.addUserLink = addUserLink;
		}

		public A getEditUserLink() {
			return editUserLink;
		}

		public void setEditUserLink(A editUserLink) {
			this.editUserLink = editUserLink;
		}

		public A getDeleteUserLink() {
			return deleteUserLink;
		}

		public void setDeleteUserLink(A deleteUserLink) {
			this.deleteUserLink = deleteUserLink;
		}

		public Vlayout getVlayout() {
			return vlayout;
		}

		public void setVlayout(Vlayout vlayout) {
			this.vlayout = vlayout;
		}

		public Doctor getUser() {
			return user;
		}

		public void setUser(Doctor user) {
			this.user = user;
		}

	}

	private void addListener() {
		userPopup.getAddUserLink().addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = -7027308124078679245L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put(ComposerUtil.ACTION_KEY, new ActionTrigger() {
					private static final long serialVersionUID = -4693653250967067484L;

					@Override
					public void doAction(Object data) throws Throwable {
						doAction();
					}

					@Override
					public void doAction() throws Throwable {
						refresh();
					}
				});
				Window window = (Window) execution.createComponents("/addUser.zul", component, params);
				try {
					window.doModal();
				} catch (Exception e) {
					e.printStackTrace();
					window.detach();
				}
			}
		});
		userPopup.getEditUserLink().addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = 8981772124442486577L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				Doctor user = userPopup.getUser();
				Map<String, Object> params = new HashMap<String, Object>();
				params.put(ComposerUtil.ACTION_KEY, new ActionTrigger() {
					private static final long serialVersionUID = -4693653250967067484L;

					@Override
					public void doAction(Object data) throws Throwable {
						doAction();
					}

					@Override
					public void doAction() throws Throwable {
						refresh();
					}
				});
				params.put(ComposerUtil.USER_KEY, user);
				Window window = (Window) execution.createComponents("/addUser.zul", component, params);
				try {
					window.doModal();
				} catch (Exception e) {
					e.printStackTrace();
					window.detach();
				}
			}
		});
		userPopup.getDeleteUserLink().addEventListener(Events.ON_CLICK, new SerializableEventListener() {

			private static final long serialVersionUID = -567847130252029238L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				final Doctor user = userPopup.getUser();
				messageUtil.showConfirm(Labels.getLabel("confirm"),
						Labels.getLabel("confirm-delete-user", new Object[] { user.getRealName() }), new ActionTrigger() {

							private static final long serialVersionUID = 8453783117582492722L;

							@Override
							public void doAction(Object data) throws Throwable {
								throw new UnsupportedOperationException();
							}

							@Override
							public void doAction() throws Throwable {
								try {
									userDAO.delete(user);
									messageUtil.showMessage(
											Labels.getLabel("message"),
											Labels.getLabel("delete") + " " + Labels.getLabel("user-lower") + " "
													+ Labels.getLabel("success-lower"));
								} catch (Exception e) {
									e.printStackTrace();
									messageUtil.showError(
											Labels.getLabel("error"),
											Labels.getLabel("delete") + " " + Labels.getLabel("user-lower") + " "
													+ Labels.getLabel("fail-lower"));
								}
							}
						});
			}
		});
	}

}
