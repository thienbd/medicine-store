package com.lkc.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

import com.lkc.entities.Examination;
import com.lkc.entities.Patient;
import com.lkc.utils.Util;

@SuppressWarnings("unchecked")
public class PatientDAO extends GenericDAO<Patient> {
	public PatientDAO() {
		super(Patient.class);
	}

	public List<Patient> loadByFullName(final String value) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		List<Patient> result = hibernateTemplate.execute(new HibernateCallback<List<Patient>>() {
			@Override
			public List<Patient> doInHibernate(Session session) throws HibernateException, SQLException {
				String field = "fullName";
				Query query = session.createQuery("from " + entityBeanType.getSimpleName() + " b where b." + field
						+ " like :value order by " + field + " asc");
				query.setString("value", value);
				return query.list();
			}
		});
		return result;
	}

	public long countByFullName(final String value) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		Long result = hibernateTemplate.execute(new HibernateCallback<Long>() {
			@Override
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				String field = "fullName";
				Query query = session.createQuery("select count(" + field + ") from " + entityBeanType.getSimpleName() + " b where b."
						+ field + " like :value order by " + field + " asc");
				query.setString("value", value + "%");
				return (Long) query.list().get(0);
			}
		});
		if (result == null) {
			return 0;
		}
		return result;
	}

	public List<String> loadFullName() {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		List<String> result = hibernateTemplate.execute(new HibernateCallback<List<String>>() {
			@Override
			public List<String> doInHibernate(Session session) throws HibernateException, SQLException {
				String field = "fullName";
				Query query = session.createQuery("select " + field + " from " + entityBeanType.getSimpleName() + " b order by " + field
						+ " asc");
				return query.list();
			}
		});
		return result;
	}

	@Override
	public void delete(Patient entity) {
		DelegatingVariableResolver resolver = Util.getSpringDelegatingVariableResolver();
		ExaminationDAO examinationDAO = (ExaminationDAO) resolver.resolveVariable("examinationDAO");
		ExaminationDetailDAO examinationDetailDAO = (ExaminationDetailDAO) resolver.resolveVariable("examinationDetailDAO");
		List<Examination> examinations = examinationDAO.loadByPatient(entity);
		for (Examination examination : examinations) {
			examinationDetailDAO.deleteAllByExamination(examination);
		}
		examinationDAO.deleteAllByPatient(entity);
		super.delete(entity);
	}

}
