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
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
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
import com.lkc.utils.MessageUtil;
import com.lkc.utils.Util;

public class AddPatientComposer extends GenericAutowireComposer {

	private static final long serialVersionUID = 3236491490523512944L;
	private Component component;

	private Combobox fullNameCombobox;
	private Datebox dateOfBirthDatebox;
	private Textbox addressTextbox;
	private Button saveButton;
	private Button clearButton;

	private Listbox examList;
	private Label listMedicineLabel;
	private Rows medicineListRows;
	private Panel medicinesPanel;
	private Button addExaminationDetailButton;

	private DelegatingVariableResolver resolver;
	private ExaminationDAO examinationDAO;
	private PatientDAO patientDAO;
	private Patient patient;
	private Map<Examination, List<ExaminationDetail>> mapExamination = new HashMap<Examination, List<ExaminationDetail>>();
	private Map<Examination, List<ExaminationDetail>> mapRemoveWhenSave = new HashMap<Examination, List<ExaminationDetail>>();
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
		// Refresh
		refreshExam();
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
		clearButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = 1015476361207982066L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				clear();
			}
		});
		saveButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = 6550487720302760839L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				MessageUtil messageUtil = (MessageUtil) resolver.resolveVariable("messageUtil");
				try {
					patientDAO.saveOrUpdate(patient);
					Set<Examination> examinations = mapExamination.keySet();
					for (Examination examination : examinations) {
						examinationDAO.saveOrUpdate(examination);
						List<ExaminationDetail> examinationDetails = mapExamination.get(examination);
						examinationDetailDAO.saveOrUpdateAll(examinationDetails);
					}
					Set<Examination> setExaminations = mapRemoveWhenSave.keySet();
					for (Examination examination : setExaminations) {
						List<ExaminationDetail> examinationDetails = mapRemoveWhenSave.get(examination);
						for (ExaminationDetail examinationDetail : examinationDetails) {
							examinationDetailDAO.delete(examinationDetail);
						}
						if (!examinations.contains(examination)) {
							examinationDAO.delete(examination);
						}
					}
					clear();
					messageUtil.showMessage(Labels.getLabel("message"), Labels.getLabel("save") + " " + Labels.getLabel("success-lower"));
				} catch (Exception e) {
					e.printStackTrace();
					messageUtil.showError(Labels.getLabel("error"), Labels.getLabel("save") + " " + Labels.getLabel("fail-lower"));
				}
			}
		});
	}

	public void clear() {
		addressTextbox.setValue("");
		fullNameCombobox.setValue("");
		dateOfBirthDatebox.setValue(null);
		mapExamination.clear();
		mapRemoveWhenSave.clear();
		refreshExam();
	}

	private ActionTrigger putnewExaminationAction = new ActionTrigger() {
		private static final long serialVersionUID = 7734638389931349151L;

		@Override
		public void doAction() throws Throwable {
			throw new UnsupportedOperationException();
		}

		@Override
		public void doAction(Object data) throws Throwable {
			Window window = (Window) data;
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

	};

	private void refreshExam() {
		composerUtil.removeAllChilds(examList);
		// Header
		Listhead listhead = new Listhead();
		examList.appendChild(listhead);
		Listheader nobListheader = new Listheader(Labels.getLabel("nob"));
		nobListheader.setWidth("50px");
		listhead.appendChild(nobListheader);
		Listheader dianogsisListheader = new Listheader(Labels.getLabel("dianogsis"));
		listhead.appendChild(dianogsisListheader);
		Listheader examDateListheader = new Listheader(Labels.getLabel("exam-date"));
		listhead.appendChild(examDateListheader);
		Listheader doctorListheader = new Listheader(Labels.getLabel("doctor"));
		listhead.appendChild(doctorListheader);
		Listheader examCostListheader = new Listheader(Labels.getLabel("exam-cost"));
		listhead.appendChild(examCostListheader);
		Listheader actionListheader = new Listheader();
		Button addExaminationButton = new Button(Labels.getLabel("add-examination"));
		actionListheader.appendChild(addExaminationButton);
		listhead.appendChild(actionListheader);
		addExaminationButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = 4401545987919876356L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put(ComposerUtil.PATIENT_KEY, patient);
				params.put(ComposerUtil.ACTION_KEY, putnewExaminationAction);
				Window window = (Window) execution.createComponents("/addExamination.zul", component, params);
				try {
					window.doModal();
				} catch (UiException e) {
					e.printStackTrace();
					try {
						window.detach();
					} catch (Exception ex) {
					}
				}
			}
		});
		examList.setEmptyMessage(Labels.getLabel("list-empty"));
		// End header
		selectedExamination = null;
		Set<Examination> setExaminations = mapExamination.keySet();
		int i = 0;
		for (final Examination examination : setExaminations) {
			Listitem listitem = new Listitem();
			examList.appendChild(listitem);
			Listcell nobListcell = new Listcell("" + (++i));
			listitem.appendChild(nobListcell);
			Listcell dianogsisListcell = new Listcell(examination.getDianogsis());
			listitem.appendChild(dianogsisListcell);
			Listcell examDateListcell = new Listcell(Util.toString(examination.getExamDate(), false));
			listitem.appendChild(examDateListcell);
			Listcell doctorListcell = new Listcell(examination.getDoctor().getRealName());
			listitem.appendChild(doctorListcell);
			Listcell examCostListcell = new Listcell(examination.getExamCost() + "");
			listitem.appendChild(examCostListcell);
			Listcell actionCell = new Listcell();
			Button editButton = new Button(Labels.getLabel("edit"));
			Button deleteButton = new Button(Labels.getLabel("delete"));
			deleteButton.setStyle("margin-left:10px");
			actionCell.appendChild(editButton);
			actionCell.appendChild(deleteButton);
			listitem.appendChild(actionCell);
			listitem.setZclass("handCursor");
			listitem.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
				private static final long serialVersionUID = 1015476361207982066L;

				@Override
				public void onEvent(Event arg0) throws Exception {
					listMedicineLabel.setValue(Labels.getLabel("medicine-for-exam",
							new Object[] { Util.toString(examination.getExamDate(), false) }));
					selectedExamination = examination;
					refreshListMedicine();
				}
			});
			editButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
				private static final long serialVersionUID = 4401545987919876356L;

				@Override
				public void onEvent(Event arg0) throws Exception {
					Map<String, Object> params = new HashMap<String, Object>();
					params.put(ComposerUtil.PATIENT_KEY, patient);
					params.put(ComposerUtil.ACTION_KEY, putnewExaminationAction);
					params.put(AddExaminationComposer.EXAM_KEY, examination);
					Window window = (Window) execution.createComponents("/addExamination.zul", component, params);
					try {
						window.doModal();
					} catch (UiException e) {
						e.printStackTrace();
						try {
							window.detach();
						} catch (Exception ex) {
						}
					}
				}
			});
			deleteButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
				private static final long serialVersionUID = 1462016137628719546L;

				@Override
				public void onEvent(Event arg0) throws Exception {
					List<ExaminationDetail> examinationDetails = mapExamination.get(examination);
					mapExamination.remove(examination);
					List<ExaminationDetail> examinationDetails2 = mapRemoveWhenSave.get(examination);
					if (examinationDetails2 == null) {
						examinationDetails2 = new ArrayList<ExaminationDetail>();
					}
					for (ExaminationDetail examinationDetail : examinationDetails) {
						if (examinationDetailDAO.checkExistByField("id", examinationDetail.getId())
								&& (!examinationDetails2.contains(examinationDetail))) {
							examinationDetails2.add(examinationDetail);
						}
					}
					mapRemoveWhenSave.put(examination, examinationDetails2);
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
			final Examination temp = selectedExamination;
			List<ExaminationDetail> details = mapExamination.get(selectedExamination);
			if (details != null) {
				int i = 0;
				for (final ExaminationDetail examinationDetail : details) {
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
					editButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {

						private static final long serialVersionUID = 1015476361207982066L;

						@Override
						public void onEvent(Event arg0) throws Exception {
							Map<String, Object> params = new HashMap<String, Object>();
							params.put(ComposerUtil.EXAMINATION_KEY, temp);
							params.put(AddExaminationDetailComposer.DETAIL_KEY, examinationDetail);
							params.put(ComposerUtil.ACTION_KEY, new ActionTrigger() {
								private static final long serialVersionUID = -2132971754026415251L;

								@Override
								public void doAction(Object data) throws Throwable {
									Window window = (Window) data;
									saveExaminationDetail(temp, window);
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
					deleteButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
						private static final long serialVersionUID = 8742730365136032211L;

						@Override
						public void onEvent(Event arg0) throws Exception {
							List<ExaminationDetail> examinationDetails = mapExamination.get(temp);
							examinationDetails.remove(examinationDetail);
							List<ExaminationDetail> examinationDetails2 = mapRemoveWhenSave.get(temp);
							if (examinationDetails2 == null) {
								examinationDetails2 = new ArrayList<ExaminationDetail>();
							}
							if (examinationDetailDAO.checkExistByField("id", examinationDetail.getId())) {
								examinationDetails2.add(examinationDetail);
							}
							mapRemoveWhenSave.put(temp, examinationDetails2);
						}
					});
				}
			}
		}
		final Examination temp = selectedExamination;
		composerUtil.removeAllEvent(addExaminationDetailButton, Events.ON_CLICK);
		addExaminationDetailButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {

			private static final long serialVersionUID = 1015476361207982066L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put(ComposerUtil.EXAMINATION_KEY, temp);
				params.put(ComposerUtil.ACTION_KEY, new ActionTrigger() {
					private static final long serialVersionUID = -2132971754026415251L;

					@Override
					public void doAction(Object data) throws Throwable {
						Window window = (Window) data;
						saveExaminationDetail(temp, window);
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

	private void saveExaminationDetail(Examination examination, Window window) {
		ExaminationDetail examinationDetail = (ExaminationDetail) window.getAttribute(AddExaminationDetailComposer.DETAIL_KEY);
		if (examinationDetail != null) {
			List<ExaminationDetail> examinationDetails = mapExamination.get(examination);
			boolean contain = false;
			for (ExaminationDetail examinationDetail2 : examinationDetails) {
				if (examinationDetail.getId() == examinationDetail2.getId()) {
					contain = true;
					examinationDetail2.setMedicine(examinationDetail.getMedicine());
					examinationDetail2.setQuantity(examinationDetail.getQuantity());
					examinationDetail2.setUsingGuide(examinationDetail.getUsingGuide());
					break;
				}
			}
			if (!contain) {
				examinationDetails.add(examinationDetail);
			}
			refreshListMedicine();
		}
	}
}
