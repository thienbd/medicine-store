package com.lkc.controllers;

import org.zkoss.util.resource.Labels;
import org.zkoss.zul.A;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Vlayout;

import com.lkc.entities.Patient;

public class PatientPopup extends Popup {

	private static final long serialVersionUID = -7300217910766449516L;

	private A addPatientLink;
	private A editPatientLink;
	private A exportPatientLink;
	private A deletePatientLink;

	private Patient patient;

	private Vlayout vlayout;

	public PatientPopup() {
		vlayout = new Vlayout();
		appendChild(vlayout);
		addPatientLink = new A(Labels.getLabel("input-patient"));
		vlayout.appendChild(addPatientLink);
		editPatientLink = new A(Labels.getLabel("edit") + " " + Labels.getLabel("patient-lower"));
		vlayout.appendChild(editPatientLink);
		deletePatientLink = new A(Labels.getLabel("delete") + " " + Labels.getLabel("patient-lower"));
		vlayout.appendChild(deletePatientLink);
		exportPatientLink = new A(Labels.getLabel("export") + " " + Labels.getLabel("patient-lower"));
		vlayout.appendChild(exportPatientLink);
	}

	public A getAddPatientLink() {
		return addPatientLink;
	}

	public void setAddPatientLink(A addPatientLink) {
		this.addPatientLink = addPatientLink;
	}

	public A getEditPatientLink() {
		return editPatientLink;
	}

	public void setEditPatientLink(A editPatientLink) {
		this.editPatientLink = editPatientLink;
	}

	public A getExportPatientLink() {
		return exportPatientLink;
	}

	public void setExportPatientLink(A exportPatientLink) {
		this.exportPatientLink = exportPatientLink;
	}

	public A getDeletePatientLink() {
		return deletePatientLink;
	}

	public void setDeletePatientLink(A deletePatientLink) {
		this.deletePatientLink = deletePatientLink;
	}

	public Vlayout getVlayout() {
		return vlayout;
	}

	public void setVlayout(Vlayout vlayout) {
		this.vlayout = vlayout;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
		exportPatientLink.setVisible(patient != null);
	}

}
