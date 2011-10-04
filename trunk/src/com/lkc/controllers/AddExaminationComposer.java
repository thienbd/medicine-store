package com.lkc.controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;

import com.lkc.constraints.NoEmptyTextConstraint;
import com.lkc.constraints.PatternConstraint;
import com.lkc.entities.Examination;
import com.lkc.entities.Patient;
import com.lkc.entities.MyUser;
import com.lkc.utils.ComposerUtil;
import com.lkc.utils.MessageUtil;
import com.lkc.utils.Util;

@SuppressWarnings("unchecked")
public class AddExaminationComposer extends GenericAutowireComposer {

	private static final long serialVersionUID = -4315562193832308696L;
	public static String EXAM_KEY = "examination";

	private Textbox dianogsisTextbox;
	private Datebox examDateDatebox;
	private Textbox examCostTextbox;
	private Datebox nextAppointDatebox;
	private Button closeButton;
	private Button saveButton;

	private MessageUtil messageUtil;
	private DelegatingVariableResolver resolver;
	private Component component;
	private ActionTrigger actionTrigger;
	private Patient patient;
	private Examination examination;

	public AddExaminationComposer() {
		resolver = Util.getSpringDelegatingVariableResolver();
		messageUtil = (MessageUtil) resolver.resolveVariable("messageUtil");
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		this.component = comp;
		Map<String, Object> args = execution.getArg();
		patient = (Patient) args.get(ComposerUtil.PATIENT_KEY);
		actionTrigger = (ActionTrigger) args.get(ComposerUtil.ACTION_KEY);
		if (patient == null || actionTrigger == null) {
			messageUtil.showError(Labels.getLabel("error"),
					Labels.getLabel("pleaseForwardParam", new Object[] { ComposerUtil.PATIENT_KEY }) + " " + Labels.getLabel("and") + " \""
							+ ComposerUtil.ACTION_KEY + "\"");
			comp.detach();
		} else {
			examination = (Examination) args.get(EXAM_KEY);
			if (examination == null) {
				examDateDatebox.setValue(new Date());
			} else {
				dianogsisTextbox.setValue(examination.getDianogsis());
				examDateDatebox.setValue(examination.getExamDate().getTime());
				examCostTextbox.setValue(examination.getExamCost() + "");
			}
			// Constraint
			addConstraint();
			// Listener
			addListener();
		}
	}

	private void addConstraint() {
		PatternConstraint examCostConstraint = new PatternConstraint(Labels.getLabel("exam-cost"),
				PatternConstraint.doubleLargerThanOrEqual0Pattern, Labels.getLabel("mustBeNumberLargerOrEqual0"));
		examCostTextbox.setConstraint(examCostConstraint);
		NoEmptyTextConstraint dianogsisConstraint = new NoEmptyTextConstraint(Labels.getLabel("dianogsisTextbox"));
		dianogsisTextbox.setConstraint(dianogsisConstraint);
		NoEmptyTextConstraint examDateConstraint = new NoEmptyTextConstraint(Labels.getLabel("exam-date"));
		examDateDatebox.setConstraint(examDateConstraint);
	}

	private void addListener() {
		closeButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = 1043487679590117236L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				component.detach();
			}
		});
		saveButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = -4759132460940120709L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				try {
					MyUser currentUser = Util.getCurrentUser();
					Calendar examDate = new GregorianCalendar();
					examDate.setTime(examDateDatebox.getValue());
					Calendar nextAppoint = new GregorianCalendar();
					nextAppoint.setTime(nextAppointDatebox.getValue());
					if (examination == null) {
						examination = new Examination(System.currentTimeMillis(), dianogsisTextbox.getValue(), examDate, currentUser,
								Double.parseDouble(examCostTextbox.getValue()), patient, nextAppoint);
					} else {
						examination.setDianogsis(dianogsisTextbox.getValue());
						examination.setExamDate(examDate);
						examination.setExamCost(Double.valueOf(examCostTextbox.getValue()));
						examination.setNextAppointment(nextAppoint);
					}
					component.setAttribute(EXAM_KEY, examination);
					component.detach();
					if (actionTrigger != null) {
						actionTrigger.doAction(component);
					}
					messageUtil.showMessage(Labels.getLabel("message"), Labels.getLabel("save") + " " + Labels.getLabel("success-lower"));
				} catch (Throwable e) {
					e.printStackTrace();
					messageUtil.showError(Labels.getLabel("error"), Labels.getLabel("save") + " " + Labels.getLabel("fail-lower"));
				}
			}
		});
	}
}
