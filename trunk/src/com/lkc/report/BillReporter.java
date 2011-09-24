package com.lkc.report;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.zkoss.util.resource.Labels;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import com.lkc.entities.Examination;
import com.lkc.entities.ExaminationDetail;
import com.lkc.entities.Medicine;
import com.lkc.entities.Patient;
import com.lkc.utils.Util;

public class BillReporter implements Serializable, JRDataSource {

	private static final long serialVersionUID = 3382701170574083996L;

	private Patient patient;
	private Examination examination;
	private List<ExaminationDetail> details;
	private Map<String, Object> params;

	private int currentIndex = -1;

	public BillReporter(Patient patient, Examination examination, List<ExaminationDetail> details, Map<String, Object> params) {
		this.patient = patient;
		this.examination = examination;
		this.details = details;
		this.params = params;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Examination getExamination() {
		return examination;
	}

	public void setExamination(Examination examination) {
		this.examination = examination;
	}

	public List<ExaminationDetail> getDetails() {
		return details;
	}

	public void setDetails(List<ExaminationDetail> details) {
		this.details = details;
	}

	@Override
	public Object getFieldValue(JRField jrfield) throws JRException {
		String name = jrfield.getName();
		if (name.equalsIgnoreCase("patientId")) {
			return patient.getId();
		}
		if (name.equalsIgnoreCase("patientName")) {
			return patient.getFullName();
		}
		if (name.equalsIgnoreCase("dateOfBirth")) {
			return Util.toString(patient.getDateOfBirth(), false);
		}
		if (name.equalsIgnoreCase("phone")) {
			return patient.getFullName();
		}
		if (name.equalsIgnoreCase("address")) {
			return patient.getAddress();
		}
		if (name.equalsIgnoreCase("doctor")) {
			return examination.getDoctor().getRealName();
		}
		if (name.equalsIgnoreCase("nextAppoint")) {
			return Util.toString(examination.getNextAppointment(), false);
		}
		if (name.equalsIgnoreCase("dianogsis")) {
			return examination.getDianogsis();
		}
		if (name.equalsIgnoreCase("examDate")) {
			return Util.toString(examination.getExamDate(), false);
		}
		if (name.equalsIgnoreCase("nob")) {
			return currentIndex + 1;
		}
		if (name.equalsIgnoreCase("total")) {
			double total = examination.getExamCost();
			for (ExaminationDetail examinationDetail : details) {
				total += examinationDetail.getQuantity() * examinationDetail.getMedicine().getPrice();
			}
			return Labels.getLabel("total-cost", new Object[] { total });
		}
		ExaminationDetail examinationDetail = details.get(currentIndex);
		if (name.equalsIgnoreCase("medicineQuantity")) {
			return examinationDetail.getQuantity();
		}
		Medicine medicine = examinationDetail.getMedicine();
		return medicine.getName();
	}

	@Override
	public boolean next() throws JRException {
		boolean next = details.size() > ++currentIndex;
		return next;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

}
