package com.lkc.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;

@SuppressWarnings("unchecked")
public class ComposerUtil implements Serializable {

	private static final long serialVersionUID = -6930360482311771979L;

	public static String PATIENT_KEY = "patient";
	public static String ACTION_KEY = "action";
	public static String EXAMINATION_KEY = "examination";
	public static String MEDICINE_KEY = "medicine";
	public static String USER_KEY = "user";

	public ComposerUtil() {

	}

	public void removeAllChilds(Component component) {
		List<Component> components = component.getChildren();
		List<Component> temp = new ArrayList<Component>();
		temp.addAll(components);
		for (Component child : temp) {
			component.removeChild(child);
		}
	}

	public void removeAllEvent(Component component, String event) {
		Iterator<EventListener> listenerIterator = component.getListenerIterator(event);
		List<EventListener> listEventListeners = new ArrayList<EventListener>();
		while (listenerIterator.hasNext()) {
			listEventListeners.add(listenerIterator.next());
		}
		for (EventListener eventListener : listEventListeners) {
			component.removeEventListener(event, eventListener);
		}
	}
}
