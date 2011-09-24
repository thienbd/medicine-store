package com.lkc.report;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import com.lkc.entities.ExaminationDetail;
import com.lkc.entities.Patient;
import com.lkc.utils.Util;

public class BillReporter implements Serializable, JRDataSource {

	private static final long serialVersionUID = 3382701170574083996L;

	private Patient patient;
	private Exception exception;
	private List<ExaminationDetail> details;
	private Map<String, Object> params;

	private int currentIndex = 0;

	public BillReporter(Patient patient, Exception exception, List<ExaminationDetail> details, Map<String, Object> params) {
		this.patient = patient;
		this.exception = exception;
		this.details = details;
		this.params = params;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
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
		return null;
	}

	@Override
	public boolean next() throws JRException {
		return details.size() > currentIndex + 1;
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
