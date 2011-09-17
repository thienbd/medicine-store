package com.lkc.controllers;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Rows;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;

import com.lkc.dao.ExaminationDAO;
import com.lkc.dao.PatientDAO;
import com.lkc.entities.Patient;
import com.lkc.utils.Util;

public class InputPatientComposer extends GenericAutowireComposer {

	private static final long serialVersionUID = 3236491490523512944L;
	private Combobox fullNameCombobox;
	private Datebox dateOfBirthDatebox;
	private Textbox addressTextbox;

	private Rows examRows;
	private Label listMedicineLabel;
	private Rows medicineListRows;

	private DelegatingVariableResolver resolver;
	private ExaminationDAO examinationDAO;
	private PatientDAO patientDAO;
	private Patient tempPatient;

	public InputPatientComposer() {
		resolver = Util.getSpringDelegatingVariableResolver();
		examinationDAO = (ExaminationDAO) resolver.resolveVariable("examinationDAO");
		patientDAO = (PatientDAO) resolver.resolveVariable("patientDAO");
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		// Listener
		addListener();
	}

	private void addListener() {
		List<Patient> listPatients = patientDAO.load();
		fullNameCombobox.setModel(new SimpleListModel(listPatients));
	}
}
