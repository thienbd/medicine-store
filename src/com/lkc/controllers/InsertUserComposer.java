package com.lkc.controllers;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Label;

import com.lkc.dao.UserDAO;
import com.lkc.entities.Doctor;
import com.lkc.enums.UserType;
import com.lkc.utils.Util;

public class InsertUserComposer extends GenericAutowireComposer {
	private static final long serialVersionUID = -9107363626097941029L;

	private Label messageLabel;

	public InsertUserComposer() {
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		try {
			Doctor user = new Doctor(UserType.GUEST.getId(), "guest", Labels.getLabel("guest"),
					"a870e1ac0a21ce41d566183173572d309236910db44adf202d6ae1559ffb7c7ad8a919d079c9c1db275bcb5d879657b0");
			DelegatingVariableResolver resolver = Util.getSpringDelegatingVariableResolver();
			UserDAO userDAO = (UserDAO) resolver.resolveVariable("userDAO");
			userDAO.save(user);
			user = new Doctor(1, "admin", Labels.getLabel("admin"),
					"960a606a6a5410050de497a677aa62a2c3f93ac109cc2b66d3ff9a5c66dfa9445cb328aa4ed04fe600acda51e18d7286");
			user.setAdmin(true);
			userDAO.save(user);
			messageLabel.setValue("Insert successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			messageLabel.setValue("Insert fail!");
		}
	}
}
