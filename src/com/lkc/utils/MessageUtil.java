package com.lkc.utils;

import java.io.Serializable;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Messagebox;

import com.lkc.controllers.ActionTrigger;

public class MessageUtil implements Serializable {

	private static final long serialVersionUID = -8511139869948622906L;

	public MessageUtil() {
	}

	public void showMessage(String title, String message) throws InterruptedException {
		Messagebox.show(message, title, Messagebox.OK, Messagebox.INFORMATION);
	}

	public void showError(String title, String message) throws InterruptedException {
		Messagebox.show(message, title, Messagebox.OK, Messagebox.ERROR);
	}

	public void showConfirm(String title, String message, final ActionTrigger actionTrigger) throws InterruptedException {
		Messagebox.show(message, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new SerializableEventListener() {
			private static final long serialVersionUID = -3847250488312108478L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				int dialogResult = (Integer) arg0.getData();
				if (dialogResult == Messagebox.YES) {
					try {
						actionTrigger.doAction();
					} catch (Throwable e) {
						if (e instanceof Exception) {
							throw (Exception) e;
						} else {
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	public void showConfirm(String title, String message, final ActionTrigger yesActionTrigger, final ActionTrigger noActionTrigger)
			throws InterruptedException {
		Messagebox.show(message, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new SerializableEventListener() {
			private static final long serialVersionUID = -3847250488312108478L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				int dialogResult = (Integer) arg0.getData();
				try {
					if (dialogResult == Messagebox.YES) {
						yesActionTrigger.doAction();
					} else {
						noActionTrigger.doAction();
					}
				} catch (Throwable e) {
					if (e instanceof Exception) {
						throw (Exception) e;
					} else {
						e.printStackTrace();
					}
				}
			}
		});
	}
}
