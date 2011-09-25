package com.lkc.report;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;

import com.lkc.entities.Examination;
import com.lkc.entities.ExaminationDetail;
import com.lkc.entities.Medicine;
import com.lkc.entities.Patient;
import com.lkc.utils.Util;

public class BillReporter implements Serializable, JRDataSource {

	private static final long serialVersionUID = 3382701170574083996L;

	public static String store_name = Labels.getLabel("store-name");
	public static String address = Labels.getLabel("store-address");
	public static String phone = Labels.getLabel("phone") + ": " + Labels.getLabel("store-phone");

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
		if (name.equalsIgnoreCase("examPrice")) {
			return examination.getExamCost();
		}
		Medicine medicine = examinationDetail.getMedicine();
		if (name.equalsIgnoreCase("medicineName")) {
			return medicine.getName();
		}
		return medicine.getPrice() * examinationDetail.getQuantity();
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

	public JasperPrint process() throws JRException {
		Execution execution = Executions.getCurrent();
		Session session = execution.getSession();
		WebApp webApp = session.getWebApp();
		params.put("net.sf.jasperreports.extension.registry.factory.fonts",
				"net.sf.jasperreports.engine.fonts.SimpleFontExtensionsRegistryFactory");
		params.put("net.sf.jasperreports.extension.simple.font.families.ireport", "/WEB-INF/CLASSES/fonts.xml");
		JasperPrint jasperPrint = JasperFillManager.fillReport(webApp.getResourceAsStream("/report/bill.jasper"), params, this);
		return jasperPrint;
	}
}
