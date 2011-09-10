package com.lkc.dao;

import com.lkc.entities.Patient;

public class PatientDAO extends GenericDAO<Patient> {
	public PatientDAO() {
		super(Patient.class);
	}
}
