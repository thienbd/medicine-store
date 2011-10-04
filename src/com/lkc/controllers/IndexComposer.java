package com.lkc.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zul.A;

public class IndexComposer extends GenericAutowireComposer {
	private static final long serialVersionUID = -218612071855457831L;

	private A bannerLink;

	public IndexComposer() {

	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		// Listener
		addListener();
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
}
