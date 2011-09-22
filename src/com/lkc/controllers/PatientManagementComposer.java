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
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.lkc.dao.PatientDAO;
import com.lkc.entities.Patient;
import com.lkc.utils.ComposerUtil;
import com.lkc.utils.MessageUtil;
import com.lkc.utils.Util;

public class PatientManagementComposer extends GenericAutowireComposer {

	private static final long serialVersionUID = -6676245595960171112L;
	private Component component;

	private Textbox searchPatientTextbox;
	private Button searchPatientButton;
	private boolean searched = false;
	private String searchData = "";

	private Button addPatientButton;
	private Rows patientRows;
	private Paging patientPaging;
	private int currentPage = 1;
	private int numOfPage = 1;
	private int patientPerPage = 10;

	private DelegatingVariableResolver resolver;
	private PatientDAO patientDAO;
	private ComposerUtil composerUtil;
	private MessageUtil messageUtil;

	public PatientManagementComposer() {
		resolver = Util.getSpringDelegatingVariableResolver();
		patientDAO = (PatientDAO) resolver.resolveVariable("patientDAO");
		composerUtil = (ComposerUtil) resolver.resolveVariable("composerUtil");
		messageUtil = (MessageUtil) resolver.resolveVariable("messageUtil");
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		this.component = comp;
		// Listener
		addListener();
		// Refresh
		refreshPatients();
	}

	private void addListener() {
		addPatientButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
			private static final long serialVersionUID = -1969797128034183689L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put(ComposerUtil.ACTION_KEY, refreshActionTrigger);
				Window window = (Window) execution.createComponents("/addPatient.zul", component, params);
				try {
					window.setClosable(true);
					window.doModal();
				} catch (Exception e) {
					e.printStackTrace();
					try {
						window.detach();
					} catch (Exception ex) {
					}
				}
			}
		});
		SerializableEventListener searchEvent = new SerializableEventListener() {
			private static final long serialVersionUID = -4585648910962389805L;

			@Override
			public void onEvent(Event arg0) throws Exception {
				searchData = searchPatientTextbox.getValue().trim();
				searched = (!searchData.isEmpty());
				refreshPatients();
			}

		};
		searchPatientTextbox.addEventListener(Events.ON_OK, searchEvent);
		searchPatientButton.addEventListener(Events.ON_CLICK, searchEvent);
	}

	private void refreshPatients() {
		composerUtil.removeAllChilds(patientRows);
		List<Patient> listPatients = null;
		if (searched) {
			Map<String, String> searchMapData = new HashMap<String, String>();
			searchMapData.put("name", searchData);
			loadPatientPage(searchMapData);
			listPatients = patientDAO.loadSearch((currentPage - 1) * patientPerPage, patientPerPage, searchMapData);
		} else {
			loadPatientPage(null);
			listPatients = patientDAO.load((currentPage - 1) * patientPerPage, patientPerPage);
		}
		int i = 0;
		for (final Patient patient : listPatients) {
			Row row = new Row();
			patientRows.appendChild(row);
			Label nobLabel = new Label(String.valueOf(++i));
			row.appendChild(nobLabel);
			Label fullnameLabel = new Label(patient.getFullName());
			row.appendChild(fullnameLabel);
			Label dateOfBirthLabel = new Label(Util.toString(patient.getDateOfBirth(), false));
			row.appendChild(dateOfBirthLabel);
			Label phoneLabel = new Label(patient.getPhone());
			row.appendChild(phoneLabel);
			Label addressLabel = new Label(patient.getAddress());
			row.appendChild(addressLabel);
			Hbox actionHbox = new Hbox();
			row.appendChild(actionHbox);
			Button editButton = new Button(Labels.getLabel("edit"));
			actionHbox.appendChild(editButton);
			editButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
				private static final long serialVersionUID = 3795760906191189265L;

				@Override
				public void onEvent(Event arg0) throws Exception {
					Map<String, Object> params = new HashMap<String, Object>();
					params.put(ComposerUtil.PATIENT_KEY, patient);
					params.put(ComposerUtil.ACTION_KEY, refreshActionTrigger);
					Window window = (Window) execution.createComponents("/addPatient.zul", component, params);
					try {
						window.setClosable(true);
						window.doModal();
					} catch (Exception e) {
						e.printStackTrace();
						try {
							window.detach();
						} catch (Exception ex) {
						}
					}
				}
			});
			Button deleteButton = new Button(Labels.getLabel("delete"));
			actionHbox.appendChild(deleteButton);
			deleteButton.addEventListener(Events.ON_CLICK, new SerializableEventListener() {
				private static final long serialVersionUID = -3908666559530360443L;

				@Override
				public void onEvent(Event arg0) throws Exception {
					messageUtil.showConfirm(Labels.getLabel("confirm"),
							Labels.getLabel("confirm-delete-patient", new Object[] { patient.getFullName() }), new ActionTrigger() {
								private static final long serialVersionUID = 3481165408946064159L;

								@Override
								public void doAction(Object data) throws Throwable {
									throw new UnsupportedOperationException();
								}

								@Override
								public void doAction() throws Throwable {
									patientDAO.delete(patient);
									refreshPatients();
								}
							});
				}
			});
		}
	}

	private void loadPatientPage(Map<String, String> searchMapData) {
		long count = 0;
		if (searchMapData != null) {
			count = patientDAO.countSearch(searchMapData);
		} else {
			count = patientDAO.count();
		}
		numOfPage = (int) (count / patientPerPage + (count % patientPerPage > 0 ? 1 : 0));
		if (currentPage > numOfPage) {
			currentPage = numOfPage;
		} else {
			if (currentPage < 1) {
				currentPage = 1;
			}
		}
		patientPaging.setTotalSize(numOfPage);
		patientPaging.setPageSize(1);
		patientPaging.setActivePage(currentPage - 1);
		patientPaging.setVisible(numOfPage > 1);
	}

	private ActionTrigger refreshActionTrigger = new ActionTrigger() {
		private static final long serialVersionUID = 3361695060556685050L;

		@Override
		public void doAction(Object data) throws Throwable {
			throw new UnsupportedOperationException();
		}

		@Override
		public void doAction() throws Throwable {
			refreshPatients();
		}
	};
}
