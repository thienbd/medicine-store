package com.lkc.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.lkc.dao.ExaminationDAO;
import com.lkc.dao.ExaminationDetailDAO;
import com.lkc.dao.PatientDAO;
import com.lkc.entities.Examination;
import com.lkc.entities.ExaminationDetail;
import com.lkc.entities.Medicine;
import com.lkc.entities.Patient;
import com.lkc.utils.ComposerUtil;
import com.lkc.utils.Util;

public class AddPatientComposer extends GenericAutowireComposer {

	private static final long serialVersionUID = 3236491490523512944L;
	private Component component;

	private Combobox fullNameCombobox;
	private Datebox dateOfBirthDatebox;
	private Textbox addressTextbox;

	private Rows examRows;
	private Button addExamination;
	private Label listMedicineLabel;
	private Rows medicineListRows;
	private Panel medicinesPanel;
	private Button addExaminationDetailButton;

	private DelegatingVariableResolver resolver;
	private ExaminationDAO examinationDAO;
	private PatientDAO patientDAO;
	private Patient patient;
	private Map<Examination, List<ExaminationDetail>> mapExamination = new HashMap<Examination, List<ExaminationDetail>>();
	private ComposerUtil composerUtil;
	private Examination selectedExamination;
	private ExaminationDetailDAO examinationDetailDAO;

	public AddPatientComposer() {
		resolver = Util.getSpringDelegatingVariableResolver();
		examinationDAO = (ExaminationDAO) resolver.resolveVariable("examinationDAO");
		patientDAO = (PatientDAO) resolver.resolveVariable("patientDAO");
		patient = new Patient(System.currentTimeMillis());
		composerUtil = (ComposerUtil) resolver.resolveVariable("composerUtil");
		examinationDetailDAO = (ExaminationDetailDAO) resolver.resolveVariable("examinationDetailDAO");
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		this.component = comp;
		// Listener
		addListener();
	}

	private void addListener() {
		List<String> listPatients = patientDAO.loadFullName();
		fullNameCombobox.setModel(new SimpleListModel(listPatients));
		SerializableEventListener fullnameListner = new SerializableEventListener() {

			private static final long serialVersionUID = 4133739790890580192L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				Combobox combobox = (Combobox) arg0.getTarget();
				String value = combobox.getValue();
				List<Patient> patients = patientDAO.loadByFullName(value);
				if (patients != null && patients.size() == 1) {
					patient = patients.get(0);
					addressTextbox.setValue(patient.getAddress());
					dateOfBirthDatebox.setValue(patient.getDateOfBirth().getTime());
					mapExamination.clear();
					List<Examination> listExaminations = examinationDAO.loadByPatient(patient);
					for (Examination examination : listExaminations) {
						List<ExaminationDetail> examinationDetails = examinationDetailDAO.loadByExamination(examination);
						mapExamination.put(examination, examinationDetails);
					}
					refreshExam();
				} else {
					patient = new Patient(System.currentTimeMillis());
				}
			}
		};
		fullNameCombobox.addEventListener(Events.ON_CHANGE, fullnameListner);
		medicinesPanel.setVisible(false);
		addExamination.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = 4401545987919876356L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				try {
					Map<String, Object> params = new HashMap<String, Object>();
					params.put(ComposerUtil.PATIENT_KEY, patient);
					params.put(ComposerUtil.ACTION_KEY, new ActionTrigger() {
						private static final long serialVersionUID = 7734638389931349151L;

						@Override
						public void doAction() throws Throwable {
							throw new UnsupportedOperationException();
						}

						@Override
						public void doAction(Object data) throws Throwable {
							Window window = (Window) data;
							Boolean saved = (Boolean) window.getAttribute(AddExaminationComposer.SAVE_KEY);
							if (saved != null && saved == true) {
								Examination examination = (Examination) window.getAttribute(AddExaminationComposer.EXAM_KEY);
								if (examination != null) {
									List<ExaminationDetail> examinationDetails = mapExamination.get(examination);
									if (examinationDetails == null) {
										examinationDetails = new ArrayList<ExaminationDetail>();
									}
									mapExamination.remove(examination);
									mapExamination.put(examination, examinationDetails);
									refreshExam();
								}
							}
						}

					});
					Window window = (Window) execution.createComponents("/addExamination.zul", component, params);
					window.doModal();
				} catch (UiException e) {
					component.detach();
					e.printStackTrace();
				}
			}
		});
	}

	private void refreshExam() {
		composerUtil.removeAllChilds(examRows);
		selectedExamination = null;
		Set<Examination> setExaminations = mapExamination.keySet();
		int i = 0;
		for (final Examination examination : setExaminations) {
			Row row = new Row();
			examRows.appendChild(row);
			Label nobLabel = new Label("" + (i++));
			row.appendChild(nobLabel);
			Label dianogsisLabel = new Label(examination.getDianogsis());
			row.appendChild(dianogsisLabel);
			Label examDate = new Label(Util.toString(examination.getExamDate(), false));
			row.appendChild(examDate);
			Label doctorLabel = new Label(examination.getDoctor().getRealName());
			row.appendChild(doctorLabel);
			Label examCost = new Label(examination.getExamCost() + "");
			row.appendChild(examCost);
			Hbox hbox = new Hbox();
			Button editButton = new Button(Labels.getLabel("edit"));
			Button deleteButton = new Button(Labels.getLabel("delete"));
			deleteButton.setStyle("margin-left:10px");
			hbox.appendChild(editButton);
			hbox.appendChild(deleteButton);
			row.appendChild(hbox);
			row.setZclass("handCursor");
			row.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
				private static final long serialVersionUID = 1015476361207982066L;

				@Override
				public void onEvent(Event arg0) throws Exception {
					listMedicineLabel.setValue(Labels.getLabel("medicine-for-exam",
							new Object[] { Util.toString(examination.getExamDate(), false) }));
					selectedExamination = examination;
					refreshListMedicine();
				}
			});
		}
		refreshListMedicine();
	}

	private void refreshListMedicine() {
		composerUtil.removeAllChilds(medicineListRows);
		if (selectedExamination == null) {
			medicinesPanel.setVisible(false);
		} else {
			medicinesPanel.setVisible(true);
			List<ExaminationDetail> details = mapExamination.get(selectedExamination);
			if (details != null) {
				int i = 0;
				for (ExaminationDetail examinationDetail : details) {
					Medicine medicine = examinationDetail.getMedicine();
					Row row = new Row();
					medicineListRows.appendChild(row);
					Label nobLabel = new Label("" + (++i));
					row.appendChild(nobLabel);
					Label nameLabel = new Label(medicine.getName());
					row.appendChild(nameLabel);
					Label quantityLabel = new Label("" + examinationDetail.getQuantity());
					row.appendChild(quantityLabel);
					Label usingLabel = new Label(examinationDetail.getUsingGuide());
					row.appendChild(usingLabel);
					Hbox hbox = new Hbox();
					Button editButton = new Button(Labels.getLabel("edit"));
					Button deleteButton = new Button(Labels.getLabel("delete"));
					deleteButton.setStyle("margin-left:10px");
					hbox.appendChild(editButton);
					hbox.appendChild(deleteButton);
					row.appendChild(hbox);
				}
			}
		}
		composerUtil.removeAllEvent(addExaminationDetailButton, Events.ON_CLICK);
		addExaminationDetailButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {

			private static final long serialVersionUID = 1015476361207982066L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put(ComposerUtil.EXAMINATION_KEY, selectedExamination);
				params.put(ComposerUtil.ACTION_KEY, new ActionTrigger() {
					private static final long serialVersionUID = -2132971754026415251L;

					@Override
					public void doAction(Object data) throws Throwable {
						Window window = (Window) data;
						Boolean saved = (Boolean) window.getAttribute(AddExaminationComposer.SAVE_KEY);
						if (saved != null && saved == true) {
							ExaminationDetail examinationDetail = (ExaminationDetail) window
									.getAttribute(AddExaminationDetailComposer.DETAIL_KEY);
							if (examinationDetail != null) {
								List<ExaminationDetail> examinationDetails = mapExamination.get(selectedExamination);
								examinationDetails.add(examinationDetail);
								refreshListMedicine();
							}
						}
					}

					@Override
					public void doAction() throws Throwable {
						throw new UnsupportedOperationException();
					}
				});
				Window window = (Window) execution.createComponents("/addExaminationDetail.zul", component, params);
				window.doModal();
			}
		});
	}
}
