package com.lkc.controllers;

import java.util.Date;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Panel;

public class StatisticsComposer extends GenericAutowireComposer {

	private static final long serialVersionUID = -1559038765094156428L;

	private Datebox fromDatebox;
	private Datebox toDatebox;
	private Button process;

	private Panel resultPanel;
	private Label traceLabel;
	private Label tradeValue;
	private Label patientNumberLabel;
	private Label patientNumberValue;

	public StatisticsComposer() {

	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		// init
		init();
		//Listener
		addListener();
	}

	private void init() {
		fromDatebox.setValue(new Date());
		toDatebox.setValue(new Date());
		Constraint nonEmptyConstraint = new Constraint() {

			@Override
			public void validate(Component arg0, Object arg1) throws WrongValueException {
				Datebox datebox = (Datebox) arg0;
				if (arg1 == null) {
					String field = (datebox.equals(fromDatebox)) ? Labels.getLabel("from-date") : Labels.getLabel("to-date");
					throw new WrongValueException(arg0, field + " " + Labels.getLabel("noEmpty"));
				}
			}
		};
		fromDatebox.setConstraint(nonEmptyConstraint);
		toDatebox.setConstraint(nonEmptyConstraint);
		resultPanel.setVisible(false);
	}
	
	private void addListener() {
		process.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = 1206098005476756416L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
