package com.lkc.dao;

import java.util.List;

import org.springframework.orm.hibernate3.HibernateTemplate;

import com.lkc.entities.Examination;
import com.lkc.entities.Patient;

@SuppressWarnings("unchecked")
public class ExaminationDAO extends GenericDAO<Examination> {
	public ExaminationDAO() {
		super(Examination.class);
	}

	public List<Examination> loadByPatient(final Patient patient) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		List<Examination> result = hibernateTemplate.findByNamedQuery("from " + entityBeanType.getSimpleName()
				+ " ex where ex.patient=:patient order by ex.examDate asc", "patient", patient);
		return result;
	}
}
