package com.lkc.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericAutowireComposer;

import com.lkc.utils.Util;

public class ErrorLogger extends GenericAutowireComposer {

	private static final long serialVersionUID = -2453017924266916578L;

	public ErrorLogger() {

	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		try {
			Exception exception = (Exception) requestScope.get("javax.servlet.error.exception");
			Util.writeError(exception);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
